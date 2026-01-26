package org.example.weatherscout.client;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import org.example.weatherscout.shared.WeatherData;
import org.example.weatherscout.utils.AbstractLogs;
import org.example.weatherscout.utils.Config;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class HelloController extends AbstractLogs {

    @FXML private TextField cityInput;
    @FXML private Label welcomeText;
    @FXML private Label temperature;
    @FXML private Label humidity;
    @FXML private HBox humidityBox;
    @FXML private Label clothingTip;
    @FXML private TextArea historyArea;
    @FXML private ToggleButton unitToggle;
    @FXML private Button detailsButton;
    @FXML private Button themeButton;

    private final WeatherClient clientService;
    private final HistoryService historyService;
    private final ThemeManager themeManager;

    private boolean isFahrenheit = false;
    private WeatherData lastWeatherData;

    public HelloController() {
        this.clientService = new WeatherClient();
        this.historyService = HistoryService.getInstance();
        this.themeManager = new ThemeManager();
    }

    @Override
    protected String getPrefix() { return "GUI"; }

    @FXML
    public void initialize() {
        String savedUnit = Config.getSetting("app.unit", "C");
        isFahrenheit = "F".equals(savedUnit);
        unitToggle.setSelected(isFahrenheit);
        unitToggle.setText(isFahrenheit ? "°F" : "°C");

        javafx.application.Platform.runLater(() -> {
            if (themeButton != null && themeButton.getScene() != null) {
                themeManager.applyCurrentTheme(themeButton.getScene());
                themeButton.setText(themeManager.getCurrentThemeName());
            }
        });

        sendUdpLog("Client gestartet");
    }

    private void sendUdpLog(String message) {
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                byte[] data = message.getBytes();
                InetAddress ip = InetAddress.getByName("localhost");
                int port = Config.getUdpPort();

                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                socket.send(packet);
                socket.close();
                log("UDP Log gesendet: " + message);
            } catch (Exception e) {
                log("UDP Fehler: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    protected void onHelloButtonClick() {
        String city = cityInput.getText();

        if (city == null || city.trim().isEmpty()) {
            welcomeText.setText("Bitte Stadt eingeben!");
            cityInput.setStyle("-fx-border-color: red;");
            return;
        }

        // Regex Prüfung: Nur Buchstaben und Bindestrich
        if (!city.matches("[a-zA-ZäöüßÄÖÜ\\s-]+")) {
            welcomeText.setText("Ungültige Zeichen!");
            cityInput.setStyle("-fx-border-color: red;");
            return;
        }

        cityInput.setStyle("");
        // ---------------------------------------------

        // GUI Reset
        welcomeText.setText("Lade Daten...");
        temperature.setText("");
        humidity.setText("");
        if (humidityBox != null) humidityBox.setVisible(false);
        if (clothingTip != null) clothingTip.setText("");
        if (detailsButton != null) detailsButton.setDisable(true);

        sendUdpLog("Frage Wetter ab für: " + city);

        Task<String> weatherTask = createWeatherTask(city);
        new Thread(weatherTask).start();
    }

    @FXML
    public void changeTheme() {
        if (themeButton != null && themeButton.getScene() != null) {
            themeManager.cycleTheme(themeButton.getScene());
            themeButton.setText(themeManager.getCurrentThemeName());
        }
    }

    private Task<String> createWeatherTask(String city) {
        Task<String> weatherTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return clientService.askServerForWeather(city);
            }
        };

        weatherTask.setOnSucceeded(e -> {
            String result = weatherTask.getValue();
            processServerResponse(result);
        });

        weatherTask.setOnFailed(e -> {
            welcomeText.setText("Verbindungsfehler!");
            log("Fehler: " + weatherTask.getException().getMessage());
        });
        return weatherTask;
    }

    private void processServerResponse(String response) {
        if (response == null) {
            welcomeText.setText("Keine Antwort vom Server.");
            return;
        }

        lastWeatherData = parseServerResponse(response);

        if (lastWeatherData != null) {
            welcomeText.setText("Wetter in " + lastWeatherData.city());
            updateTemperatureDisplay(lastWeatherData.temperature());
            humidity.setText(lastWeatherData.humidity() + "%");

            if (humidityBox != null) humidityBox.setVisible(true);
            if (clothingTip != null) clothingTip.setText(lastWeatherData.getClothingTip());
            if (detailsButton != null) detailsButton.setDisable(false);

            checkUserWarning(lastWeatherData.temperature());
            historyService.saveToHistory(lastWeatherData);
        } else {
            welcomeText.setText("Ungültige Daten empfangen.");
        }
    }

    private WeatherData parseServerResponse(String response) {
        String[] parts = response.split("\\|");

        if (parts.length >= 4 && parts[0].equals("OK")) {
            try {
                String city = parts[1];
                double temp = Double.parseDouble(parts[2]);
                int hum = Integer.parseInt(parts[3]);

                double apparentTemp = parts.length > 4 ? Double.parseDouble(parts[4]) : temp;
                int weatherCode = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;
                double windSpeed = parts.length > 6 ? Double.parseDouble(parts[6]) : 0;
                double windGusts = parts.length > 7 ? Double.parseDouble(parts[7]) : 0;
                int cloudCover = parts.length > 8 ? Integer.parseInt(parts[8]) : 0;
                double dewPoint = parts.length > 9 ? Double.parseDouble(parts[9]) : 0;

                return new WeatherData(city, temp, hum, windSpeed, cloudCover, dewPoint,
                        apparentTemp, weatherCode, windGusts);
            } catch (Exception e) {
                log("Parse Fehler: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    private void checkUserWarning(double temp) {
        if (temp > 30.0) showWarning("Hitzewarnung", "Über 30°C! Viel trinken.");
        else if (temp < -5.0) showWarning("Kältewarnung", "Unter -5°C! Glatteisgefahr.");
    }

    private void showWarning(String title, String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    protected void onShowHistoryClick() {
        historyArea.setText("Lade Historie...");
        Task<List<String>> historyTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return historyService.loadHistory();
            }
        };
        historyTask.setOnSucceeded(e -> historyArea.setText(String.join("\n", historyTask.getValue())));
        historyTask.setOnFailed(e -> historyArea.setText("Fehler beim Laden."));
        new Thread(historyTask).start();
    }

    @FXML
    protected void onExportHistoryCsv() {
        File output = new File("weather_history.csv");
        try {
            historyService.exportToCsv(output);
            welcomeText.setText("Exportiert: " + output.getName());
        } catch (IOException e) {
            welcomeText.setText("Fehler: " + e.getMessage());
        }
    }

    @FXML
    protected void onClearHistoryClick() {
        historyService.clearHistory();
        historyArea.setText("Historie gelöscht.");
    }

    @FXML
    protected void onUnitToggle() {
        isFahrenheit = unitToggle.isSelected();
        unitToggle.setText(isFahrenheit ? "°F" : "°C");
        if (lastWeatherData != null) updateTemperatureDisplay(lastWeatherData.temperature());
        Config.saveSetting("app.unit", isFahrenheit ? "F" : "C");
    }

    @FXML
    protected void onDetailsClick() {
        if (lastWeatherData != null) historyArea.setText(buildDetailsString());
    }

    private String buildDetailsString() {
        double temp = lastWeatherData.temperature();
        double feel = lastWeatherData.apparentTemp();
        String unit = "°C";

        if (isFahrenheit) {
            temp = temp * 9 / 5 + 32;
            feel = feel * 9 / 5 + 32;
            unit = "°F";
        }
        return String.format("Stadt: %s\nWetter: %s\nTemp: %.1f %s\nGefühlt: %.1f %s",
                lastWeatherData.city(), lastWeatherData.getWeatherDescription(), temp, unit, feel, unit);
    }

    private void updateTemperatureDisplay(double tempCelsius) {
        if (isFahrenheit) {
            temperature.setText(String.format("%.1f °F", tempCelsius * 9 / 5 + 32));
        } else {
            temperature.setText(String.format("%.1f °C", tempCelsius));
        }
    }
}
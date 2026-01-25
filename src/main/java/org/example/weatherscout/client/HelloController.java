package org.example.weatherscout.client;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.weatherscout.shared.WeatherData;
import org.example.weatherscout.utils.AbstractLogs;

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

    private final WeatherClient clientService;
    private final HistoryService historyService;
    private boolean isFahrenheit = false;
    private WeatherData lastWeatherData;

    private static final String VALIDATION_ERROR = "Fehler: Nur Buchstaben, Leerzeichen und Bindestriche erlaubt.";

    public HelloController() {
        this.clientService = new WeatherClient();
        this.historyService = HistoryService.getInstance();
    }

    @Override
    protected String getPrefix() {
        return "GUI";
    }

    @FXML
    public void initialize() {
        setupCityValidation();

        HistoryService s1 = HistoryService.getInstance();
        HistoryService s2 = HistoryService.getInstance();

        log("Test Instanz 1: " + s1.toString());
        log("Test Instanz 2: " + s2.toString());

        if (s1 == s2) {
            log("ERFOLG: Es ist dieselbe Instanz. Singleton funktioniert.");
        }
        else {
            log("FEHLER: Es sind unterschiedliche/mehrere Instanzen!");
        }
    }

    private void setupCityValidation() {
        if (cityInput == null) return;

        cityInput.setTextFormatter(new TextFormatter<String>(change -> {
            if (!isValidInput(change.getText())) {
                showValidationError();
                return null;
            }
            clearValidationError();
            return change;
        }));

        cityInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            if (!isValidInput(newVal)) {
                showValidationError();
            } else {
                clearValidationError();
            }
        });
    }

    private boolean isValidInput(String input) {
        if (input == null || input.isEmpty()) return true;
        return input.matches("[a-zA-ZäöüßÄÖÜ\\s-]*");
    }

    private void showValidationError() {
        welcomeText.setText(VALIDATION_ERROR);
        cityInput.setStyle("-fx-border-color: #b83535; -fx-border-width: 1;");
    }

    private void clearValidationError() {
        if (VALIDATION_ERROR.equals(welcomeText.getText())) {
            welcomeText.setText("");
        }
        cityInput.setStyle("");
    }

    @FXML
    protected void onHelloButtonClick() {
        String city = cityInput.getText().trim();

        if (city.isEmpty()) {
            welcomeText.setText("Bitte Stadt eingeben!");
            return;
        }

        if (!isCityValid(city)) {
            welcomeText.setText(VALIDATION_ERROR);
            cityInput.requestFocus();
            cityInput.selectAll();
            return;
        }

        welcomeText.setText("Lade Daten...");
        temperature.setText("");
        humidity.setText("");
        if (humidityBox != null) humidityBox.setVisible(false);
        if (clothingTip != null) clothingTip.setText("");
        if (detailsButton != null) detailsButton.setDisable(true);

        Task<String> weatherTask = createWeatherTask(city);
        new Thread(weatherTask).start();
    }

    private boolean isCityValid(String city) {
        if (city == null || city.isEmpty()) {
            return false;
        }
        if (!city.matches("[a-zA-ZäöüßÄÖÜ\\s-]*")) {
            return false;
        }
        return city.matches(".*[a-zA-ZäöüßÄÖÜ].*");
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
            Throwable error = weatherTask.getException();
            welcomeText.setText("Verbindungsfehler!");
            log("Fehler: " + error.getMessage());
        });
        return weatherTask;
    }

    private void processServerResponse(String response) {
        if (response == null) {
            welcomeText.setText("Keine Antwort vom Server.");
            return;
        }

        String[] parts = response.split("\\|");

        if (parts.length >= 4 && parts[0].equals("OK")) {
            String city = parts[1];
            double temp = Double.parseDouble(parts[2]);
            int hum = Integer.parseInt(parts[3]);

            double apparentTemp = parts.length > 4 ? Double.parseDouble(parts[4]) : temp;
            int weatherCode = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;
            double windSpeed = parts.length > 6 ? Double.parseDouble(parts[6]) : 0;
            double windGusts = parts.length > 7 ? Double.parseDouble(parts[7]) : 0;
            int cloudCover = parts.length > 8 ? Integer.parseInt(parts[8]) : 0;
            double dewPoint = parts.length > 9 ? Double.parseDouble(parts[9]) : 0;

            lastWeatherData = new WeatherData(city, temp, hum, windSpeed, cloudCover, dewPoint,
                    apparentTemp, weatherCode, windGusts);

            welcomeText.setText("Wetter in " + city);
            updateTemperatureDisplay(temp);
            humidity.setText(hum + "%");
            if (humidityBox != null) humidityBox.setVisible(true);

            if (clothingTip != null) clothingTip.setText(lastWeatherData.getClothingTip());

            if (detailsButton != null) {
                detailsButton.setDisable(false);
            }

            historyService.saveToHistory(lastWeatherData);

        } else if (parts[0].equals("ERROR")) {
            welcomeText.setText("Fehler: " + (parts.length > 1 ? parts[1] : "Unbekannt"));
        } else {
            welcomeText.setText("Ungültige Daten empfangen.");
        }
    }

    @FXML
    protected void onShowHistoryClick() {
        historyArea.setText(String.join("\n", historyService.loadHistory()));
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

        if (lastWeatherData != null) {
            updateTemperatureDisplay(lastWeatherData.temperature());
        }
    }

    @FXML
    protected void onDetailsClick() {
        if (lastWeatherData == null) {
            welcomeText.setText("Bitte erst eine Stadt suchen!");
            return;
        }

        String details = buildDetailsString();
        historyArea.setText(details);
    }

    private String buildDetailsString() {
        double tempDisplay = isFahrenheit
            ? lastWeatherData.temperature() * 9 / 5 + 32
            : lastWeatherData.temperature();
        String tempUnit = isFahrenheit ? "°F" : "°C";

        double apparentTempDisplay = isFahrenheit
            ? lastWeatherData.apparentTemp() * 9 / 5 + 32
            : lastWeatherData.apparentTemp();

        double dewPointDisplay = isFahrenheit
            ? lastWeatherData.dewPoint() * 9 / 5 + 32
            : lastWeatherData.dewPoint();

        return "Stadt: " + lastWeatherData.city() + "\n" +
               "Wetter: " + lastWeatherData.getWeatherDescription() + "\n" +
               "Temperatur: " + String.format("%.1f", tempDisplay) + " " + tempUnit + "\n" +
               "Gefühlt wie: " + String.format("%.1f", apparentTempDisplay) + " " + tempUnit + "\n" +
               "Luftfeuchtigkeit: " + lastWeatherData.humidity() + "%\n" +
               "Taupunkt: " + String.format("%.1f", dewPointDisplay) + " " + tempUnit + "\n" +
               "Wolkenbedeckung: " + lastWeatherData.getCloudDescription() + "\n" +
               "Windgeschwindigkeit: " + String.format("%.1f", lastWeatherData.windSpeed()) + " km/h (" + lastWeatherData.getWindDescription() + ")\n" +
               "Windböen: " + String.format("%.1f", lastWeatherData.windGusts()) + " km/h";
    }

    private void updateTemperatureDisplay(double tempCelsius) {
        if (isFahrenheit) {
            double tempF = tempCelsius * 9 / 5 + 32;
            temperature.setText(String.format("%.1f °F", tempF));
        } else {
            temperature.setText(String.format("%.1f °C", tempCelsius));
        }
    }
}


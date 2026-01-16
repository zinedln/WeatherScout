package org.example.weatherscout.client;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.weatherscout.shared.WeatherData;
import org.example.weatherscout.utils.AbstractLogs;

public class HelloController extends AbstractLogs {

    @FXML private TextField cityInput;
    @FXML private ListView<String> suggestionList;
    @FXML private Label welcomeText;
    @FXML private Label temperature;
    @FXML private Label humidity;
    @FXML private Label clothingTip;
    @FXML private TextArea historyArea;

    private final WeatherClient clientService;
    private final HistoryService historyService;

    public HelloController() {
        this.clientService = new WeatherClient();
        this.historyService = new HistoryService();
    }

    @Override
    protected String getPrefix() {
        return "GUI";
    }

    @FXML
    public void initialize() {
        if (suggestionList != null) suggestionList.setVisible(false);
    }

    @FXML
    protected void onHelloButtonClick() {
        String city = cityInput.getText().trim();
        if (city.isEmpty()) {
            welcomeText.setText("Bitte Stadt eingeben!");
            return;
        }

        welcomeText.setText("Lade Daten...");
        temperature.setText("");
        humidity.setText("");
        if (clothingTip != null) clothingTip.setText("");

        Task<String> weatherTask = createWeatherTask(city);

        new Thread(weatherTask).start();
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

            welcomeText.setText("Wetter in " + city);
            temperature.setText(String.format("🌡 %.1f °C", temp));
            humidity.setText("💧 " + hum + "%");

            WeatherData data = new WeatherData(city, temp, hum);
            if (clothingTip != null) clothingTip.setText(data.getClothingTip());
            historyService.saveToHistory(data);

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
}
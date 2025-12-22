package org.example.weatherscout;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controller für die Wetter-GUI.
 * Verbindet die UI-Elemente mit dem WeatherService.
 */
public class HelloController {

    @FXML
    private TextField cityInput;

    @FXML
    private Label welcomeText;

    @FXML
    private Label temperature;

    @FXML
    private Label humidity;

    @FXML
    private Label clothingTip;

    @FXML
    private TextArea historyArea;

    private final WeatherService weatherService;
    private final HistoryService historyService;

    public HelloController() {
        this.weatherService = new WeatherService();
        this.historyService = new HistoryService();
    }

    /**
     * Wird beim Klick auf den "Suchen"-Button ausgeführt.
     * Holt die Wetterdaten für die eingegebene Stadt.
     */
    @FXML
    protected void onHelloButtonClick() {
        String city = cityInput.getText().trim();

        if (city.isEmpty()) {
            welcomeText.setText("Bitte gib eine Stadt ein!");
            temperature.setText("");
            humidity.setText("");
            if (clothingTip != null) clothingTip.setText("");
            return;
        }

        // Zeige Ladezustand
        welcomeText.setText("Lade Wetterdaten für " + city + "...");
        temperature.setText("");
        humidity.setText("");
        if (clothingTip != null) clothingTip.setText("");

        // API-Abfrage im Hintergrund-Thread (UI nicht blockieren)
        new Thread(() -> {
            try {
                WeatherData data = weatherService.getWeather(city);

                // UI-Update muss im JavaFX-Thread erfolgen
                Platform.runLater(() -> {
                    welcomeText.setText("Wetter in " + data.city() + ":");
                    temperature.setText("🌡️ Temperatur: " + data.getTemperatureFormatted());
                    humidity.setText("💧 Luftfeuchtigkeit: " + data.getHumidityFormatted());
                    if (clothingTip != null) clothingTip.setText(data.getClothingTip());

                    // In Historie speichern
                    historyService.saveToHistory(data);
                });

            } catch (WeatherException e) {
                Platform.runLater(() -> {
                    welcomeText.setText("Fehler: " + e.getMessage());
                    temperature.setText("");
                    humidity.setText("");
                    if (clothingTip != null) clothingTip.setText("");
                });
            }
        }).start();
    }

    /**
     * Zeigt die Historie an.
     */
    @FXML
    protected void onShowHistoryClick() {
        List<String> history = historyService.loadHistory();

        if (history.isEmpty()) {
            historyArea.setText("Noch keine Abfragen gespeichert.");
        } else {
            // Neueste zuerst anzeigen
            StringBuilder sb = new StringBuilder();
            for (int i = history.size() - 1; i >= 0; i--) {
                sb.append(history.get(i)).append("\n");
            }
            historyArea.setText(sb.toString());
        }
    }

    /**
     * Löscht die Historie.
     */
    @FXML
    protected void onClearHistoryClick() {
        historyService.clearHistory();
        historyArea.setText("Historie wurde gelöscht.");
    }
}

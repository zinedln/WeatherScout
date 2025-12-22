package org.example.weatherscout;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    private ListView<String> suggestionList;

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
     * Wird automatisch nach dem Laden der FXML aufgerufen.
     * Hier richten wir das Autocomplete ein.
     */
    @FXML
    public void initialize() {
        // Listener für Texteingabe - bei Änderung Vorschläge laden
        cityInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2) {
                // Suche im Hintergrund-Thread
                new Thread(() -> {
                    List<String> cities = weatherService.searchCities(newValue);

                    // UI-Update im JavaFX-Thread
                    Platform.runLater(() -> {
                        suggestionList.getItems().clear();
                        suggestionList.getItems().addAll(cities);
                        suggestionList.setVisible(!cities.isEmpty());
                    });
                }).start();
            } else {
                suggestionList.setVisible(false);
            }
        });

        // Bei Klick auf einen Vorschlag: Stadt übernehmen und suchen
        suggestionList.setOnMouseClicked(event -> {
            String selected = suggestionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Nur den Stadtnamen nehmen (vor dem Komma)
                String city = selected.contains(",") ? selected.split(",")[0].trim() : selected;
                cityInput.setText(city);
                suggestionList.setVisible(false);
                onHelloButtonClick();  // Direkt Wetter abrufen
            }
        });
    }

    /**
     * Wird beim Klick auf den "Suchen"-Button ausgeführt.
     * Holt die Wetterdaten für die eingegebene Stadt.
     */
    @FXML
    protected void onHelloButtonClick() {
        String city = cityInput.getText().trim();
        suggestionList.setVisible(false);  // Vorschläge ausblenden

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

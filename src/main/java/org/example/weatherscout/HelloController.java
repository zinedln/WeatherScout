package org.example.weatherscout;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    private final WeatherService weatherService;

    public HelloController() {
        this.weatherService = new WeatherService();
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
}

package org.example.weatherscout;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    protected void onHelloButtonClick() {
        String city = cityInput.getText().trim();

        if (city.matches("[a-zA-Z]+")) {
            String formattedCity = city.substring(0, 1).toUpperCase() + city.substring(1).toLowerCase();

            cityInput.setStyle(null);
            welcomeText.setStyle(null);

            welcomeText.setText("Wie ist das Wetter in " + formattedCity + "?");
            temperature.setText("Die Temperatur beträgt " + "25" + "°C");
            humidity.setText("Die Luftfeuchtigkeit liegt bei " + "60" + "%.");
        }
        else {
            welcomeText.setText("Achtung! Es sind nur Buchstaben erlaubt!");
            welcomeText.setStyle("-fx-text-fill: red;");

            cityInput.setStyle("-fx-text-fill: red; -fx-border-color: red;");

            temperature.setText("");
            humidity.setText("");
        }
        // Note to myself: cases for specific degrees (don't forget your coat, etc.)
    }
}

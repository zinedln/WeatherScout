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
        String city = cityInput.getText();
        welcomeText.setText("Wie ist das Wetter in " + city + "?");

        temperature.setText("Die Temperatur beträgt " + "25" + "°C");
        humidity.setText("Die Luftfeuchtigkeit liegt bei " + "60" + "%.");

        // Note to myself: cases for specific degrees (don't forget your coat, etc.)
    }
}

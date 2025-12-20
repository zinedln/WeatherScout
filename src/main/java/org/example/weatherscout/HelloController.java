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
        welcomeText.setText("Checking the weather for: " + city);

        temperature.setText("The temperature is: " + temperature.getText() + "°C");
        humidity.setText("The humidity is: " + humidity.getText() + "%");
    }
}

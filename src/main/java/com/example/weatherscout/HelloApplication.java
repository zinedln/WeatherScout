package com.example.weatherscout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        Scene scene = new Scene(root, 800, 800);

        Label cityLabel = new Label("City: Vienna");
        root.getChildren().add(cityLabel);

        Label temperatureLabel = new Label("Temperature: Celsius");
        root.getChildren().add(temperatureLabel);

        Label humidityLabel = new Label("Humidity: %");
        root.getChildren().add(humidityLabel);

        Label windSpeedLabel = new Label("Wind speed: km'/'h");
        root.getChildren().add(windSpeedLabel);

        stage.setTitle("Weather Scout");
        stage.setScene(scene);
        stage.show();
    }
}

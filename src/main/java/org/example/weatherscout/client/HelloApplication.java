package org.example.weatherscout.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/weatherscout/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        String initialCss = HelloApplication.class.getResource("/org/example/weatherscout/normal-theme.css").toExternalForm();
        scene.getStylesheets().add(initialCss);

        stage.setTitle("WeatherScout");
        stage.setScene(scene);
        stage.show();
    }
}

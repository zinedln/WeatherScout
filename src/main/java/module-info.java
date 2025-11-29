module com.example.weatherscout {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.weatherscout to javafx.fxml;
    exports com.example.weatherscout;
}
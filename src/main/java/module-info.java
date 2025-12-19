module org.example.weatherscout {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.weatherscout to javafx.fxml;
    exports org.example.weatherscout;
}
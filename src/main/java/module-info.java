module org.example.weatherscout {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens org.example.weatherscout to javafx.fxml;
    exports org.example.weatherscout;
}

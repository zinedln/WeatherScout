package org.example.weatherscout.client;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.weatherscout.shared.WeatherData;
import org.example.weatherscout.utils.AbstractLogs;

public class HelloController extends AbstractLogs {

    @FXML private TextField cityInput;
    @FXML private ListView<String> suggestionList;
    @FXML private Label welcomeText;
    @FXML private Label temperature;
    @FXML private Label humidity;
    @FXML private Label clothingTip;
    @FXML private TextArea historyArea;
    @FXML private ToggleButton unitToggle;

    private final WeatherClient clientService;
    private final HistoryService historyService;
    private boolean isFahrenheit = false;
    private WeatherData lastWeatherData;

    public HelloController() {
        this.clientService = new WeatherClient();
        this.historyService = HistoryService.getInstance();
    }

    @Override
    protected String getPrefix() {
        return "GUI";
    }

    @FXML
    public void initialize() {
        if (suggestionList != null) suggestionList.setVisible(false);

        // TextFormatter: verhindert das Einfügen/Tippen von Ziffern
        if (cityInput != null) {
            cityInput.setTextFormatter(new TextFormatter<String>(change -> {
                String newText = change.getText();
                if (newText != null && newText.matches(".*\\d.*")) {
                    // Verwerfe Änderungen, die Ziffern enthalten und zeige sofort Feedback
                    if (welcomeText != null) welcomeText.setText("Fehler: Der Stadtname darf keine Zahlen enthalten.");
                    cityInput.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                    return null; // Änderung verwerfen
                } else {
                    // Falls vorher eine Fehlermeldung angezeigt wurde, entfernen
                    if (welcomeText != null && "Fehler: Der Stadtname darf keine Zahlen enthalten.".equals(welcomeText.getText())) {
                        welcomeText.setText("");
                    }
                    cityInput.setStyle("");
                }
                return change;
            }));

            // Live-Validierung: prüft bei jeder Texteingabe, ob Ziffern vorhanden sind
            cityInput.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) return;
                String trimmed = newVal.trim();

                if (isCityInvalid(trimmed)) {
                    // Zeige sofortige Fehlermeldung im welcomeText und markiere das Feld
                    welcomeText.setText("Fehler: Der Stadtname darf keine Zahlen enthalten.");
                    cityInput.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                } else {
                    // Entferne die Fehlermarkierung nur, wenn sie zuvor von unserer Validierung gesetzt wurde
                    if ("Fehler: Der Stadtname darf keine Zahlen enthalten.".equals(welcomeText.getText())) {
                        welcomeText.setText("");
                    }
                    cityInput.setStyle("");
                }
            });
        }

        // TEST: Funktioniert Singleton?
        HistoryService s1 = HistoryService.getInstance();
        HistoryService s2 = HistoryService.getInstance();

        log("Test Instanz 1: " + s1.toString());
        log("Test Instanz 2: " + s2.toString());

        if (s1 == s2) {
            log("ERFOLG: Es ist dieselbe Instanz. Singleton funktioniert.");
        }
        else {
            log("FEHLER: Es sind unterschiedliche/mehrere Instanzen!");
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        String city = cityInput.getText().trim();
        if (city.isEmpty()) {
            welcomeText.setText("Bitte Stadt eingeben!");
            return;
        }

        // Neue Validierung: keine Ziffern in Stadtnamen erlaubt
        if (isCityInvalid(city)) {
            welcomeText.setText("Fehler: Der Stadtname darf keine Zahlen enthalten.");
            // Fokus zurück auf das Eingabefeld und kompletten Text markieren, damit der Nutzer neu eingeben kann
            cityInput.requestFocus();
            cityInput.selectAll();
            return;
        }

        welcomeText.setText("Lade Daten...");
        temperature.setText("");
        humidity.setText("");
        if (clothingTip != null) clothingTip.setText("");

        Task<String> weatherTask = createWeatherTask(city);

        new Thread(weatherTask).start();
    }

    private Task<String> createWeatherTask(String city) {
        Task<String> weatherTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return clientService.askServerForWeather(city);
            }
        };

        weatherTask.setOnSucceeded(e -> {
            String result = weatherTask.getValue();
            processServerResponse(result);
        });

        weatherTask.setOnFailed(e -> {
        Throwable error = weatherTask.getException();
        welcomeText.setText("Verbindungsfehler!");
        log("Fehler: " + error.getMessage());
    });
        return weatherTask;
    }

    private void processServerResponse(String response) {
        if (response == null) {
            welcomeText.setText("Keine Antwort vom Server.");
            return;
        }

        String[] parts = response.split("\\|");

        if (parts.length >= 4 && parts[0].equals("OK")) {
            String city = parts[1];
            double temp = Double.parseDouble(parts[2]);
            int hum = Integer.parseInt(parts[3]);

            lastWeatherData = new WeatherData(city, temp, hum);

            welcomeText.setText("Wetter in " + city);
            updateTemperatureDisplay(temp);
            humidity.setText("💧 " + hum + "%");

            if (clothingTip != null) clothingTip.setText(lastWeatherData.getClothingTip());
            historyService.saveToHistory(lastWeatherData);

        } else if (parts[0].equals("ERROR")) {
            welcomeText.setText("Fehler: " + (parts.length > 1 ? parts[1] : "Unbekannt"));
        } else {
            welcomeText.setText("Ungültige Daten empfangen.");
        }
    }

    @FXML
    protected void onShowHistoryClick() {
        historyArea.setText(String.join("\n", historyService.loadHistory()));
    }

    @FXML
    protected void onClearHistoryClick() {
        historyService.clearHistory();
        historyArea.setText("Historie gelöscht.");
    }

    @FXML
    protected void onUnitToggle() {
        isFahrenheit = unitToggle.isSelected();
        unitToggle.setText(isFahrenheit ? "°F" : "°C");

        if (lastWeatherData != null) {
            updateTemperatureDisplay(lastWeatherData.temperature());
        }
    }

    private void updateTemperatureDisplay(double tempCelsius) {
        if (isFahrenheit) {
            double tempF = tempCelsius * 9 / 5 + 32;
            temperature.setText(String.format("🌡 %.1f °F", tempF));
        } else {
            temperature.setText(String.format("🌡 %.1f °C", tempCelsius));
        }
    }

    // Hilfsmethode: true, wenn der Stadtnamen Ziffern enthält (klarer Name, verhindert invertierte Aufrufe)
    private boolean isCityInvalid(String city) {
        // Gibt true zurück, wenn der String mindestens eine Ziffer enthält.
        return city != null && city.matches(".*\\d.*");
    }
}

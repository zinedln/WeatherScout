# WeatherScout 🌤️

Eine JavaFX-Anwendung zur Abfrage von Wetterdaten.

## Features

- 🔍 Suche nach Städten weltweit
- 🌡️ Aktuelle Temperatur anzeigen
- 💧 Luftfeuchtigkeit anzeigen
- 👕 Kleidungstipps basierend auf dem Wetter

## Projektstruktur

```
src/main/java/org/example/weatherscout/
├── HelloApplication.java  # JavaFX Application Entry Point
├── HelloController.java   # GUI Controller
├── Launcher.java          # Starter-Klasse für modulares JavaFX
├── WeatherService.java    # API-Service für Wetterdaten
├── WeatherData.java       # Datenmodell für Wetterdaten
└── WeatherException.java  # Exception-Klasse

src/main/resources/org/example/weatherscout/
└── hello-view.fxml        # GUI Layout
```

## Starten

```bash
./gradlew run
```

## Bauen

```bash
./gradlew build
```

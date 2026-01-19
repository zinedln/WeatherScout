# WeatherScout

Eine JavaFX-Anwendung zur Abfrage von Wetterdaten.

## Features

- Suche nach Städten weltweit
- Aktuelle Temperatur anzeigen
- Luftfeuchtigkeit anzeigen
- Kleidungstipps basierend auf dem Wetter
- Umwandlung von Celsius in Fahrenheit als Toggle Button in der GUI

## Projektstruktur

```
src/main/java/org/example/weatherscout/
├── client/
│   ├── HelloApplication.java
│   ├── HelloController.java
│   ├── HistoryService.java
│   ├── Launcher.java
│   └── WeatherClient.java
├── server/
│   ├── ClientHandler.java
│   ├── OpenMeteo.java
│   └── WeatherServer.java
├── shared/
│   ├── Protocol.java
│   ├── WeatherData.java
│   └── WeatherException.java
└── utils/
    ├── AbstractLogs.java
    └── Config.java

src/main/resources/org/example/weatherscout/
├── hello-view.fxml
└── styles.css
```

## Starten

```bash
./gradlew run
```

## Bauen

```bash
./gradlew build
```

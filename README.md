# WeatherScout

Eine JavaFX-Anwendung zur Abfrage von Wetterdaten mit TCP-Client/Server-Architektur.

### Mitglieder
- Donato Dolib (ic24b101@technikum-wien.at)
- Berk Yilmaz (ic24b058@technikum-wien.at)
- Zinedin Saleh (ic24b121@technikum-wien.at)

## Features & Anforderungen

| ID | Anforderung | Status |
|---|---|---|
| **MH1** | TCP-Client für API-Abfrage | Ja |
| **MH2** | Asynchrone Datenverarbeitung & GUI-Anzeige | Ja |
| **MH3** | Speichern der Abfrage-Historie | Ja |
| **SH1** | Konfigurierbarer API-Endpunkt & Ports | Ja |
| **SH2** | Detailansicht für erweiterte Wetterdaten | Ja |
| **SH3** | Anzeigen der Abfrage-Historie in der GUI | Ja |
| **NTH1** | Server-Discovery über UDP-Datagrams | Ja |
| **NTH2** | Benutzerdefinierte Wetterwarnungen | Ja |
| **NTH3** | Export der Historie als CSV | Ja |
| **OK1** | Eigener TCP-Server | Ja |
| **OK2** | Automatische Aktualisierung mit Timer | Nein |
| **OK3** | Persistente Benutzereinstellungen | Ja |

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
│   ├── WeatherProvider.java
│   └── WeatherServer.java
├── shared/
│   ├── Protocol.java
│   ├── WeatherData.java
│   └── WeatherException.java
├── utils/
│   ├── AbstractLogs.java
│   └── Config.java
└── resources/
    ├── hello-view.fxml
    ├── normal-theme.css
    ├── dark-theme.css
    ├── christmas-theme.css
    ├── halloween-theme.css
    └── lgbtq-theme.css
```

## Starten

1. **WeatherServer starten:**
   - Öffne `WeatherServer.java` und führe die Datei aus

2. **Client starten:**
   - Im Gradle Plugin: `Tasks` → `application` → `run`
   - Oder im Terminal: `./gradlew run`

## Konfiguration

Die Datei `config.properties` enthält die Konfiguration:
```properties
server.host=localhost
server.port=4711
api.endpoint=https://api.open-meteo.com/v1/forecast
```


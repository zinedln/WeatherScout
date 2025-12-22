package org.example.weatherscout;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service zum Speichern und Laden der Abfrage-Historie.
 * Speichert jede Wetter-Abfrage in einer Textdatei.
 */
public class HistoryService {

    // Dateiname für die Historie
    private static final String HISTORY_FILE = "weather_history.txt";

    // Datum-Format für die Anzeige
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Speichert eine Wetter-Abfrage in der Historie-Datei.
     *
     * @param data Die Wetterdaten die gespeichert werden sollen
     */
    public void saveToHistory(WeatherData data) {
        // Aktuelle Zeit holen
        String timestamp = LocalDateTime.now().format(formatter);

        // Zeile für die Datei erstellen
        String line = timestamp + " | " + data.city() + " | " +
                      data.getTemperatureFormatted() + " | " +
                      data.getHumidityFormatted();

        // In Datei schreiben (append = true -> anfügen, nicht überschreiben)
        try (FileWriter fw = new FileWriter(HISTORY_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(line);
            bw.newLine();  // Neue Zeile am Ende

        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Historie: " + e.getMessage());
        }
    }

    /**
     * Lädt alle Einträge aus der Historie-Datei.
     *
     * @return Liste aller Historie-Einträge (neueste zuletzt)
     */
    public List<String> loadHistory() {
        List<String> history = new ArrayList<>();

        File file = new File(HISTORY_FILE);

        // Prüfen ob Datei existiert
        if (!file.exists()) {
            return history;  // Leere Liste zurückgeben
        }

        // Datei Zeile für Zeile lesen
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            String line;
            while ((line = br.readLine()) != null) {
                history.add(line);
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Historie: " + e.getMessage());
        }

        return history;
    }

    /**
     * Löscht die komplette Historie.
     */
    public void clearHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}


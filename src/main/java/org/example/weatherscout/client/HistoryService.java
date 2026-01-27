package org.example.weatherscout.client;

import org.example.weatherscout.shared.WeatherData;
import org.example.weatherscout.utils.Config;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class HistoryService {

    private static HistoryService instance;
    private final String historyFile;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private HistoryService() {
        this.historyFile = Config.getHistoryFile();
    }

    public static synchronized HistoryService getInstance() {
        if (instance == null) {
            instance = new HistoryService();
        }
        return instance;
    }

    public void saveToHistory(WeatherData data) {
        String timestamp = LocalDateTime.now().format(formatter);

        String line = timestamp + " | " +
                      "Stadt: " + data.city() + " | " +
                      "Wetter: " + data.getWeatherDescription() + " | " +
                      "Temperatur: " + String.format("%.1f", data.temperature()) + "°C | " +
                      "Gefühlt wie: " + String.format("%.1f", data.apparentTemp()) + "°C | " +
                      "Luftfeuchtigkeit: " + data.humidity() + "% | " +
                      "Taupunkt: " + String.format("%.1f", data.dewPoint()) + "°C | " +
                      "Wolkenbedeckung: " + data.getCloudDescription() + " | " +
                      "Windgeschwindigkeit: " + String.format("%.1f", data.windSpeed()) + " km/h (" + data.getWindDescription() + ") | " +
                      "Windböen: " + String.format("%.1f", data.windGusts()) + " km/h";

        try (FileWriter fw = new FileWriter(historyFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Historie: " + e.getMessage());
        }
    }

    public List<String> loadHistory() {
        List<String> history = new ArrayList<>();
        File file = new File(historyFile);

        if (!file.exists()) {
            return history;
        }

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

    public void clearHistory() {
        File file = new File(historyFile);
        if (file.exists()) {
            file.delete();
        }
    }


            bw.flush();
        }
    }

    // Hilfsmethode: um CSV-Felder sicher zu escapen
    private String csvEscape(String s) {
        if (s == null) return "";
        String value = s.replace("\"", "\"\"");
        // Felder quote, wenn sie Semikolon, Anführungszeichen oder Zeilenumbruch enthalten
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value + "\"";
        }
        return value;
    }
}

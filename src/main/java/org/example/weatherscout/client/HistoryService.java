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

    // Export to CSV
    public void exportToCsv(File output) throws IOException {
        File input = new File(historyFile);
        File parent = input.getAbsoluteFile().getParentFile();

        if (!input.exists()) {
            throw new FileNotFoundException("Keine Historie zum Exportieren.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(input));
             FileWriter fw = new FileWriter(output);
             BufferedWriter bw = new BufferedWriter(fw)) {


            bw.write("Timestamp;Stadt;Wetter;Temperatur;Gefuehlt;Luftfeuchtigkeit;Taupunkt;Wolkenbedeckung;Windgeschwindigkeit;Windboeen");
            bw.newLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s\\|\\s");
                List<String> columns = new ArrayList<>();

                // timestamp
                String ts = "";
                if (parts.length > 0 && parts[0] != null) {
                    String raw = parts[0].trim();
                    if (raw.isEmpty() || "###".equals(raw)) {
                        ts = LocalDateTime.now().format(formatter);
                    } else {
                        ts = raw;
                    }
                } else {
                    ts = LocalDateTime.now().format(formatter);
                }
                columns.add(csvEscape(ts));

                // Für die weiteren erwarteten Felder extrahiere den Teil nach ': ' falls vorhanden
                for (int i = 1; i < Math.min(parts.length, 11); i++) {
                    String p = parts[i];
                    int idx = p.indexOf(":");
                    String value = (idx >= 0 && idx + 1 < p.length()) ? p.substring(idx + 1).trim() : p.trim();

                    // special handling fields
                    // i==3 -> Temperatur, i==4 -> Gefuehlt, i==5 -> Luftfeuchtigkeit, i==6 -> Taupunkt
                    // Komma zu Punkt
                    value = value.replaceAll("(\\d),(\\d)", "$1.$2");
                    if (i == 5) {

                        value = value.replace("°C", "").trim();
                        columns.add(csvEscape(value));
                    } else if (i == 3 || i == 4 || i == 6) {

                        value = value.replace("°C", "").replace("%", "").trim();
                        if (value.isEmpty()) {
                            columns.add(csvEscape(""));
                        } else {
                            // Führendes Apostroph zwingt Excel, das Feld als Text darzustellen
                            String forced = "'" + value;
                            columns.add(csvEscape(forced));
                        }
                    } else {
                        // Standardfall: entferne Einheiten
                        value = value.replace("°C", "").replace("%", "").trim();
                        columns.add(csvEscape(value));
                    }
                }

                // Filling empty columns
                while (columns.size() < 11) columns.add("");

                bw.write(String.join(";", columns));
                bw.newLine();
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

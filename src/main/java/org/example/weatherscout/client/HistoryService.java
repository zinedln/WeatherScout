package org.example.weatherscout.client;

import org.example.weatherscout.shared.WeatherData;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class HistoryService {

    private static final String HISTORY_FILE = "weather_history.txt";

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public void saveToHistory(WeatherData data) {
        String timestamp = LocalDateTime.now().format(formatter);

        String line = timestamp + " | " + data.city() + " | " +
                      data.getTemperatureFormatted() + " | " +
                      data.getHumidityFormatted();

        try (FileWriter fw = new FileWriter(HISTORY_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(line);
            bw.newLine();

        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Historie: " + e.getMessage());
        }
    }

    public List<String> loadHistory() {
        List<String> history = new ArrayList<>();

        File file = new File(HISTORY_FILE);

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
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}


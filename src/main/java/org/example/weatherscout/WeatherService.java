package org.example.weatherscout;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

/**
 * Service-Klasse für Wetter-API-Abfragen.
 * Verwendet die Open-Meteo API (kostenlos, kein API-Key erforderlich).
 */
public class WeatherService {

    private final HttpClient httpClient;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Holt Wetterdaten für eine Stadt.
     *
     * @param city Name der Stadt
     * @return WeatherData mit Temperatur und Luftfeuchtigkeit
     * @throws WeatherException wenn die Abfrage fehlschlägt
     */
    public WeatherData getWeather(String city) throws WeatherException {
        try {
            // Schritt 1: Koordinaten für die Stadt holen (Geocoding)
            double[] coordinates = getCoordinates(city);
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            // Schritt 2: Wetterdaten für die Koordinaten holen
            return fetchWeatherData(latitude, longitude, city);

        } catch (WeatherException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherException("Fehler beim Abrufen der Wetterdaten: " + e.getMessage(), e);
        }
    }

    /**
     * Holt die Koordinaten für eine Stadt über die Open-Meteo Geocoding API.
     */
    private double[] getCoordinates(String city) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=de";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new WeatherException("Geocoding API Fehler: HTTP " + response.statusCode());
        }

        String json = response.body();

        // Prüfe ob Ergebnisse vorhanden sind
        if (!json.contains("\"results\"") || json.contains("\"results\":[]")) {
            throw new WeatherException("Stadt nicht gefunden: " + city);
        }

        // Finde den results-Array
        int resultsStart = json.indexOf("\"results\"");
        if (resultsStart == -1) {
            throw new WeatherException("Stadt nicht gefunden: " + city);
        }

        // Extrahiere latitude und longitude aus dem ersten Ergebnis
        String resultsSection = json.substring(resultsStart);
        double latitude = extractFirstNumber(resultsSection, "latitude");
        double longitude = extractFirstNumber(resultsSection, "longitude");

        return new double[]{latitude, longitude};
    }

    /**
     * Holt die Wetterdaten von der Open-Meteo Weather API.
     */
    private WeatherData fetchWeatherData(double latitude, double longitude, String city) throws Exception {
        String url = String.format(
                java.util.Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m&timezone=auto",
                latitude, longitude
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new WeatherException("Weather API Fehler: HTTP " + response.statusCode());
        }

        String json = response.body();

        // Finde den "current" Block
        int currentStart = json.indexOf("\"current\"");
        if (currentStart == -1) {
            throw new WeatherException("Keine aktuellen Wetterdaten verfügbar");
        }

        String currentSection = json.substring(currentStart);

        // Extrahiere Werte aus der "current" Section
        double temperature = extractFirstNumber(currentSection, "temperature_2m");
        int humidity = (int) extractFirstNumber(currentSection, "relative_humidity_2m");

        return new WeatherData(city, temperature, humidity);
    }

    /**
     * Extrahiert den ersten Zahlenwert für einen Key aus einem JSON-String.
     */
    private double extractFirstNumber(String json, String key) throws WeatherException {
        String searchKey = "\"" + key + "\":";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            throw new WeatherException("Daten nicht gefunden: " + key);
        }

        int valueStart = keyIndex + searchKey.length();

        // Überspringe Leerzeichen
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        int valueEnd = valueStart;

        // Finde Ende des Zahlenwerts (kann auch negativ sein oder Dezimalpunkt haben)
        while (valueEnd < json.length()) {
            char c = json.charAt(valueEnd);
            if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                break;
            }
            valueEnd++;
        }

        String valueStr = json.substring(valueStart, valueEnd).trim();

        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            throw new WeatherException("Ungültiger Wert für " + key + ": " + valueStr);
        }
    }
}


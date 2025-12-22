package org.example.weatherscout;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class WeatherService {

    private final HttpClient httpClient;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public WeatherData getWeather(String city) throws WeatherException {
        try {
            double[] coords = getCoordinates(city);
            return fetchWeather(city, coords[0], coords[1]);
        } catch (WeatherException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherException("Fehler: " + e.getMessage(), e);
        }
    }

    private double[] getCoordinates(String city) throws Exception {
        String encoded = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + encoded + "&count=1&language=de";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        if (!json.contains("\"results\"")) {
            throw new WeatherException("Stadt nicht gefunden: " + city);
        }

        double lat = extractNumber(json, "\"latitude\":");
        double lon = extractNumber(json, "\"longitude\":");

        return new double[]{lat, lon};
    }

    private WeatherData fetchWeather(String city, double lat, double lon) throws Exception {
        String url = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m",
                lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        int currentIdx = json.indexOf("\"current\"");
        String currentSection = json.substring(currentIdx);

        double temp = extractNumber(currentSection, "\"temperature_2m\":");
        int humidity = (int) extractNumber(currentSection, "\"relative_humidity_2m\":");

        return new WeatherData(city, temp, humidity);
    }

    private double extractNumber(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) return 0;
        start += key.length();

        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == ',' || c == '}' || c == ']') break;
            end++;
        }

        return Double.parseDouble(json.substring(start, end).trim());
    }
}

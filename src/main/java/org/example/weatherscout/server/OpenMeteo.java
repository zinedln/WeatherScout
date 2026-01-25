package org.example.weatherscout.server;

import org.example.weatherscout.shared.WeatherException;
import org.example.weatherscout.utils.AbstractLogs;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class OpenMeteo extends AbstractLogs implements WeatherProvider {

    private final HttpClient httpClient;

    public OpenMeteo() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    protected String getPrefix() {
        return "API";
    }

    @Override
    public String fetchWeatherData(String city) throws WeatherException {
        try {
            log("Suche Koordinaten für: " + city);
            double[] coords = getCoordinates(city);

            return fetchFromApi(city, coords[0], coords[1]);
        } catch (WeatherException e) {
            throw e;
        } catch (Exception e) {
            log("Unerwarteter Fehler: " + e.getMessage());
            throw new WeatherException("Interner API Fehler: " + e.getMessage());
        }
    }

    private double[] getCoordinates(String city) throws Exception {
        String encoded = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + encoded + "&count=1&language=de";

        String json = sendRequest(url);

        if (!json.contains("\"results\"")) {
            throw new WeatherException("Stadt '" + city + "' nicht gefunden");
        }

        double lat = extractNumber(json, "\"latitude\":");
        double lon = extractNumber(json, "\"longitude\":");
        return new double[]{lat, lon};
    }

    private String fetchFromApi(String city, double lat, double lon) throws Exception {
        String url = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,wind_gusts_10m,cloud_cover,dew_point_2m",
                lat, lon);

        String json = sendRequest(url);

        log("API Response: " + json.substring(0, Math.min(500, json.length())));

        int currentIdx = json.indexOf("\"current\"");
        String currentSection = json.substring(currentIdx);
        double temp = extractNumber(currentSection, "\"temperature_2m\":");
        int humidity = (int) extractNumber(currentSection, "\"relative_humidity_2m\":");
        double apparentTemp = extractNumber(currentSection, "\"apparent_temperature\":");
        int weatherCode = (int) extractNumber(currentSection, "\"weather_code\":");
        double windSpeed = extractNumber(currentSection, "\"wind_speed_10m\":");
        double windGusts = extractNumber(currentSection, "\"wind_gusts_10m\":");
        int cloudCover = (int) extractNumber(currentSection, "\"cloud_cover\":");
        double dewPoint = extractNumber(currentSection, "\"dew_point_2m\":");

        log("Geparste Werte: temp=" + temp + ", humidity=" + humidity + ", apparentTemp=" + apparentTemp +
            ", weatherCode=" + weatherCode + ", windSpeed=" + windSpeed + ", windGusts=" + windGusts +
            ", cloudCover=" + cloudCover + ", dewPoint=" + dewPoint);

        return String.format(Locale.US, "OK|%s|%.1f|%d|%.1f|%d|%.1f|%.1f|%d|%.1f",
                city, temp, humidity, apparentTemp, weatherCode, windSpeed, windGusts, cloudCover, dewPoint);
    }

    private String sendRequest(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private double extractNumber(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) return 0;
        start += key.length();
        while (start < json.length() && (Character.isWhitespace(json.charAt(start)) || json.charAt(start) == ':')) {
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
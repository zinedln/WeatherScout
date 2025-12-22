package org.example.weatherscout;

/**
 * Exception für Fehler bei der Wetter-API-Abfrage.
 */
public class WeatherException extends Exception {

    public WeatherException(String message) {
        super(message);
    }

    public WeatherException(String message, Throwable cause) {
        super(message, cause);
    }
}


package org.example.weatherscout.server;
import org.example.weatherscout.shared.WeatherException;

public interface WeatherProvider {
    /**
     * Holt die Wetterdaten für eine Stadt.
     * @param city Name der Stadt
     * @return Formatierter String für das Protokoll (z.B. "OK|Berlin|20.5|60")
     * @throws WeatherException bei fachlichen Fehlern (Stadt nicht gefunden)
     */
    String fetchWeatherData(String city) throws WeatherException;
}

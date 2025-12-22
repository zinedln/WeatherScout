package org.example.weatherscout;

/**
 * Record für Wetterdaten einer Stadt.
 *
 * @param city Name der Stadt
 * @param temperature Temperatur in Grad Celsius
 * @param humidity Luftfeuchtigkeit in Prozent
 */
public record WeatherData(String city, double temperature, int humidity) {

    /**
     * Gibt die Temperatur formatiert zurück.
     */
    public String getTemperatureFormatted() {
        return String.format("%.1f°C", temperature);
    }

    /**
     * Gibt die Luftfeuchtigkeit formatiert zurück.
     */
    public String getHumidityFormatted() {
        return humidity + "%";
    }

    /**
     * Gibt einen Kleidungstipp basierend auf der Temperatur zurück.
     */
    public String getClothingTip() {
        if (temperature < 0) {
            return "Es ist sehr kalt! Zieh dich warm an mit Winterjacke und Handschuhen.";
        } else if (temperature < 10) {
            return "Es ist kühl. Vergiss deine Jacke nicht!";
        } else if (temperature < 20) {
            return "Angenehme Temperaturen. Ein leichter Pullover reicht.";
        } else if (temperature < 30) {
            return "Schönes Wetter! T-Shirt-Wetter.";
        } else {
            return "Es ist sehr heiß! Bleib im Schatten und trink viel Wasser.";
        }
    }
}


package org.example.weatherscout.shared;

public record WeatherData(String city, double temperature, int humidity,
                          double windSpeed, int cloudCover, double dewPoint,
                          double apparentTemp, int weatherCode, double windGusts) {

    public WeatherData(String city, double temperature, int humidity) {
        this(city, temperature, humidity, 0, 0, 0, 0, 0, 0);
    }

    public String getTemperatureFormatted() {
        return String.format("%.1f°C", temperature);
    }

    public String getHumidityFormatted() {
        return humidity + "%";
    }

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

    // Berechnet die gefühlte Temperatur (Wind Chill)
    public double getFeelsLike() {
        if (temperature <= 10 && windSpeed > 0) {
            return temperature - (0.2 * windSpeed);
        }
        return temperature;
    }

    public String getCloudDescription() {
        if (cloudCover <= 10) return "Klar";
        if (cloudCover <= 25) return "Überwiegend klar";
        if (cloudCover <= 50) return "Teils bewölkt";
        if (cloudCover <= 75) return "Überwiegend bewölkt";
        return "Bedeckt";
    }

    public String getWindDescription() {
        if (windSpeed <= 2) return "Windstille";
        if (windSpeed <= 5) return "Leichter Wind";
        if (windSpeed <= 11) return "Mäßiger Wind";
        if (windSpeed <= 19) return "Frischer Wind";
        if (windSpeed <= 28) return "Starker Wind";
        return "Sturm";
    }

    public String getWeatherDescription() {
        return switch (weatherCode) {
            case 0 -> "Klarer Himmel";
            case 1, 2 -> "Überwiegend klar";
            case 3 -> "Bedeckt";
            case 45, 48 -> "Nebel";
            case 51, 53, 55 -> "Nieselregen";
            case 61, 63, 65 -> "Regen";
            case 71, 73, 75 -> "Schnee";
            case 77 -> "Schneekörner";
            case 80, 81, 82 -> "Regenschauer";
            case 85, 86 -> "Schneeschauer";
            case 95, 96, 99 -> "Gewitter";
            default -> "Unbekannt";
        };
    }
}

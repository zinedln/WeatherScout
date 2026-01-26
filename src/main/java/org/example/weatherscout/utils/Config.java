package org.example.weatherscout.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Achtung! Konfiguration nicht gefunden. Nutze Standardwerte.");
        }
    }

    public static String getHost() {
        return props.getProperty("server.host");
    }

    public static int getPort() {
        return Integer.parseInt(props.getProperty("server.port"));
    }

    public static int getUdpPort() {
        return Integer.parseInt(props.getProperty("udp.port"));
    }

    public static String getHistoryFile() {
        return props.getProperty("history.file");
    }

    public static void saveSetting(String key, String value) {
        props.setProperty(key, value);
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "WeatherScout Konfiguration");
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Config: " + e.getMessage());
        }
    }

    public static String getSetting(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}

package org.example.weatherscout.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Achtung! config.properties nicht gefunden. Nutze Standardwerte.");
            props.setProperty("server.host","localhost");
            props.setProperty("server.port","4711");
            props.setProperty("udp.port", "4712");
            props.setProperty("history.file", "weather_history.txt");
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
}

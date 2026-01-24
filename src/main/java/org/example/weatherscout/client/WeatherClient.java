package org.example.weatherscout.client;

import org.example.weatherscout.utils.AbstractLogs;
import org.example.weatherscout.utils.Config;

import java.io.*;
import java.net.Socket;

public class WeatherClient extends AbstractLogs {
    private static final String SERVER_HOST = Config.getHost();
    private static final int SERVER_PORT = Config.getPort();

    @Override
    protected String getPrefix() {
        return "CLIENT-SERVICE";
    }

    public String askServerForWeather(String city) throws IOException {
        log("Verbinde zu Server " + SERVER_HOST + ":" + SERVER_PORT);

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())))
             {
                 writer.write(city);
                 writer.newLine();
                 writer.flush();

                 String response = reader.readLine();
                 log("Antwort erhalten: " + response);

                 return response;
             }
    }
}

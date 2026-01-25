package org.example.weatherscout.server;

import org.example.weatherscout.shared.WeatherException;
import org.example.weatherscout.utils.AbstractLogs;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends AbstractLogs implements Runnable {

    private final Socket clientSocket;
    private final WeatherProvider apiProvider;

    public ClientHandler(Socket socket, WeatherProvider apiProvider) {
        this.clientSocket = socket;
        this.apiProvider = apiProvider;
    }

    @Override
    protected String getPrefix() {
        return "HANDLER-" + clientSocket.getPort();
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            String city = reader.readLine();

            if (city != null) {
                log("Anfrage erhalten: " + city);

                try {
                    String response = apiProvider.fetchWeatherData(city);

                    writer.write(response);
                } catch (WeatherException e) {
                    log ("Inhaltlicher Fehler: " + e.getMessage());
                    writer.write("ERROR| " + e.getMessage());
                }
                writer.newLine();
                writer.flush();
            }

        } catch (IOException e) {
            log("Verbindungsfehler: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
            }
        }
    }
}


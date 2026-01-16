package org.example.weatherscout.server;

import org.example.weatherscout.utils.AbstractLogs;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends AbstractLogs implements Runnable {

    private final Socket clientSocket;
    private final OpenMeteo apiProvider;

    public ClientHandler(Socket socket, OpenMeteo apiProvider) {
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

                String response = apiProvider.fetchWeatherData(city);

                writer.write(response);
                writer.newLine();
                writer.flush();

                log("Antwort gesendet: " + response);
            }

        } catch (IOException e) {
            log("Verbindungsfehler: " + e.getMessage());
        } finally {
            try { clientSocket.close();
            } catch (IOException e) {
                // Auch egal. Hauptsache Socket wird geschlossen.
            }
        }
    }
}
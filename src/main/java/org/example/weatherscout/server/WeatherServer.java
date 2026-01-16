package org.example.weatherscout.server;

import org.example.weatherscout.utils.AbstractLogs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WeatherServer extends AbstractLogs {

    private static final int PORT = 4711;
    private final OpenMeteo apiProvider;

    public WeatherServer() {
        this.apiProvider = new OpenMeteo();
    }

    @Override
    protected String getPrefix() {
        return "SERVER";
    }

    public void start() {
        log("Server gestartet auf Port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                log("Neuer Client verbunden: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, apiProvider);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            log("Server abgestürzt: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new WeatherServer().start();
    }
}
package org.example.weatherscout.server;

import org.example.weatherscout.utils.AbstractLogs;
import org.example.weatherscout.utils.Config;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer extends AbstractLogs implements Runnable {
    private final int port = Config.getUdpPort();

    @Override
    protected String getPrefix() { return "UDP"; }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            log("Log-Server gestartet auf Port " + port);
            byte[] buf = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                log("Empfangen von " + packet.getAddress() + ": " + message);
            }
        } catch (Exception e) {
            log("Fehler: " + e.getMessage());
        }
    }
}
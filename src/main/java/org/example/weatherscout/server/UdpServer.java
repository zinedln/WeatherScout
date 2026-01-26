package org.example.weatherscout.server;

import org.example.weatherscout.utils.AbstractLogs;
import org.example.weatherscout.utils.Config;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer extends AbstractLogs implements Runnable {
    private final int port = Config.getUdpPort();

    @Override
    protected String getPrefix() {
        return "UDP-Server";
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            log("UDP Listener gestartet auf Port " + port);
            byte[] buf = new byte[256];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                log("Empfangen: " + received);

                if ("DISCOVER".equals(received.trim())) {
                    String response = "WEATHERSCOUT_ALIVE|" + Config.getPort();
                    byte[] sendBuf = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(sendBuf, sendBuf.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                }
            }
        } catch (Exception e) {
            log("UDP Fehler: " + e.getMessage());
        }
    }
}

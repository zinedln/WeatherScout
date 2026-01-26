package org.example.weatherscout.client;

import javafx.concurrent.Task;
import org.example.weatherscout.utils.Config;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryService {
    public Task<String> createDiscoveryTask() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(2000);
                    byte[] buf = "DISCOVER".getBytes();
                    InetAddress address = InetAddress.getByName("localhost");
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Config.getUdpPort());
                    socket.send(packet);

                    byte[] recBuf = new byte[256];
                    DatagramPacket response = new DatagramPacket(recBuf, recBuf.length);
                    socket.receive(response);
                    return new String(response.getData(), 0, response.getLength());
                }
            }
        };
    }
}
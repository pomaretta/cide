package com.github.pomaretta.cide.sockets.exercice03;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    
    public static void main(String[] args) throws Exception {
        
        DatagramSocket socket = new DatagramSocket(54605);    
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
    
        while (true) {
            socket.receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received: " + received);

            if (received.equals("Uep")) {
                String message = "Que tal?";
                byte[] buffer = message.getBytes();
                socket.send(new DatagramPacket(buffer, buffer.length, new java.net.InetSocketAddress("localhost", 54604)));
            } else if (received.equals("done")) {
                System.out.println("Server is done");
                break;
            }   
        }

        socket.close();

    }

}
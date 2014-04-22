package com.example.Carduino_CalculatedToast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.util.Log;
import java.io.IOException;

/**
 * Created by Dakota Kim on 4/15/14.
 */
public class UDPSender {

    public static void logAndSendPacket(String packetContents, String ipAddress, int port)
    {


        try
        {
        //Creates a socket for communications.
        DatagramSocket socket = new DatagramSocket(port);

        InetAddress address = InetAddress.getByName(ipAddress);

        // Package message.
        int messageLength = packetContents.length();
        byte[] byteMessage = packetContents.getBytes();

        // Create packet.
        DatagramPacket packet = new DatagramPacket(byteMessage, messageLength, port);

        // Send packet
        socket.send(packet);

        // Log packet.
        Log.d("UDP", String.format("Send packet %s on port $d to address %s", packet.toString(), port, ipAddress);
        }
        catch (IOException e)
        {
            // Log error
            Log.e("UDP", "IOException occured during packet send", e);
        }



        // Specifies whether the mail man is running.

    }
}

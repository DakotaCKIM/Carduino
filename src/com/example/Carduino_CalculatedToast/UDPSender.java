package com.example.Carduino_CalculatedToast;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.*;

/**
 * Sends UDP packets for you. Call {@link #beginUdpLoop()} to begin a loop that continuously sends out commands
 * as the state of the left, right, forward, and reverse boolean values changes in response to button events
 */
public class UDPSender {



    // Ip address and port to send to
    public static int port = 0;
    public static String ipAddress;

    // The address to send to as an InetAddress Object
    private static InetAddress local;

    // Current engaged directions

    public static boolean forward = false;
    public static boolean reverse = false;
    public static boolean left = false;
    public static boolean right = false;
    public static boolean park = false;
    public static boolean realign = false;
    public static boolean authenticate = false;

    // Specifies whether the mail man is running
    private static boolean isRunning = false;

    // The socket to use for UDP communication
    private static DatagramSocket socket;

    // The packet to send; we will reuse this object for each send to avoid unnecessary memory usage
    static DatagramPacket packet;

    private static InetAddress address;

    public static String getIpAddress() {
        return ipAddress;
    }

    public static void setIpAddress(String ipAddress) {
        UDPSender.ipAddress = ipAddress;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        UDPSender.port = port;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void setRunningState(boolean isRunning) {
        UDPSender.isRunning = isRunning;
    }

    /**
     * Initiates the main loop that continuously sends commands to the arduino
     */
    public static void beginUdpLoop() {

            //Creates a socket for communications.
            try {
                socket = new DatagramSocket(port);
                local = InetAddress.getByName(ipAddress);
            } catch (SocketException e) {
                Log.e(UDPSender.class.getName(), String.format("Could not instantiate DatagramSocket object, " +
                        "exception: %s", e.toString()));
            } catch (UnknownHostException e) {
                Log.e(UDPSender.class.getName(), "Unknown host exception", e);
            }

        // Spawn a new thread to send UDP messages
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                // Start looping the first time we're called
                isRunning = true;

                // While we're set to run, and while there is some command to send, loop and send appropriate packets
                while (isRunning) {  // isRunning is set to false by the method setRunningState(false)
                        if (forward) {
                            logAndSendPacket("N");
                        }
                        else if(reverse) {
                            logAndSendPacket("S");
                        }

                        if (left) {
                            logAndSendPacket("L");

                        }

                        else if (right) {
                            logAndSendPacket("R");
                        }

                        /*stop simply ends up killing power to the motor in a specific direction
                          This must be sent every time you release a forward or back button */
                        else if (park) {
                            logAndSendPacket("P");
                        }
                        /*
                            realign simply sends the signal "s" for straighten out the wheels by shifting the servo from
                            80/100 degrees back to 90 degrees or straight ahead.
                         */
                        else if (realign) {
                            logAndSendPacket("F");

                        }
                        /*
                            authenticate sends the "hello" authentication string to the Engineering UDP server
                        */
                        else if (authenticate) {
                            logAndSendPacket("hello");
                            UDPSender.authenticate = false;
                        }
                }
                return null;
            }
        }.execute();

    }

    /**
     * Log an outgoing packet and send it
     *
     * @param packetContents the packet to send
     */
    public static void logAndSendPacket(String packetContents)
    {


        try
        {


            // Package message.
            int messageLength = packetContents.length();
            byte[] byteMessage = packetContents.getBytes();

            // Create packet.
            DatagramPacket packet = new DatagramPacket(byteMessage, messageLength, local, port);

            // Send packet
            socket.send(packet);

            // Log packet.
            Log.d("UDP", String.format("Send packet %s on port $d to address %s", packet.toString(), port, ipAddress));
        }
        catch (IOException e)
        {
            // Log error
            Log.e("UDP", "IOException occured during packet send", e);
        }
    }


}
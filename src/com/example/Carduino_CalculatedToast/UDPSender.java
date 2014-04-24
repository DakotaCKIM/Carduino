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

    private static final String TAG = UDPSender.class.getName();

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
                    try {


                        if (forward) {
                            String data = "N";
                            int msg_length=data.length();
                            byte[] message = data.getBytes();
                            packet = new DatagramPacket(message, msg_length, local,port);
                            logAndSendPacket(packet);

                        }
                        else if(reverse) {
                            String data = "S";
                            int msg_length=data.length();
                            byte[] message = data.getBytes();
                            packet = new DatagramPacket(message, msg_length, local,port);
                            logAndSendPacket(packet);
                        }

                        if (left) {
                            String data = "";
                            int msg_length=data.length();
                            byte[] message = data.getBytes();
                            packet = new DatagramPacket(message, msg_length, local,port);
                            logAndSendPacket(packet);

                        }
                        else if (right) {
                            String data = "r";
                            int msg_length = data.length();
                            byte[] message = data.getBytes();
                            packet = new DatagramPacket(message, msg_length, local, port);
                            socket.send(packet);
                        }

                        /*stop simply ends up killing power to the motor in a specific direction
                          This must be sent every time you release a forward or back button */
                        else if (park) {
                            String data = "p";
                            int msg_length = data.length();
                            byte[] message = data.getBytes();
                            packet = new DatagramPacket(message, msg_length, local, port);
                            socket.send(packet);
                        }
                        /*
                            realign simply sends the signal "s" for straighten out the wheels by shifting the servo from
                            80/100 degrees back to 90 degrees or straight ahead.
                         */
                        else if (realign) {
                            String data = "s"; //we should
                            int msg_length=data.length();
                            byte[] message = data.getBytes();
                            packet = new DatagramPacket(message, msg_length, local,port);
                            socket.send(packet);
                        }
                        /*
                            authenticate sends the "hello" authentication string to the Engineering UDP server
                        */
                        else if (authenticate) {
                            String data = "hello"; //we should
                            int msg_length = data.length();
                            byte[] message = data.getBytes();
                            packet = new DatagramPacket(message, msg_length, local, port);
                            socket.send(packet);
                        }


                    } catch (SocketException e) {
                        Log.e(TAG, "Socket exception!", e);
                    } catch (UnknownHostException e) {
                        Log.e(TAG, "UnknownHost exception!", e);
                    } catch (IOException e) {
                        Log.e(TAG, "IOexception!", e);
                    }
                }

                return null;


            }
        }.execute();

    }

    /**
     * Log an outgoing packet and send it
     *
     * @param packet the packet to send
     */
    public static void logAndSendPacket(DatagramPacket packet) {
        try {
            socket.send(packet);
            Log.d(UDPSender.class.getName(), String.format("Sent packet %s on port %d to address %s",
                    packet.toString(), port, ipAddress));
        } catch (IOException e) {
            Log.e(UDPSender.class.getName(), String.format("Error! Exception occurred during sending of packet %s " +
                    "Exception: %s",
                    packet.toString(), e.toString()));
        }

    }


}
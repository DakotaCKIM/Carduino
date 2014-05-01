package com.example.Carduino_CalculatedToast;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Log;
import com.example.Carduino_CalculatedToast.UDPSender;

/**
 * Fragment to display car controller
 */
public class ControllerFragment extends Fragment {

    public static final String PREF_KEY_IP_ADDRESS = "pref_key_host_ip";
    public static final String PREF_KEY_HOST_PORT = "pref_key_host_port";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Ensure that the settings contain a port and ip address
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String ipAddress = prefs.getString(PREF_KEY_IP_ADDRESS, "");
        String port = prefs.getString(PREF_KEY_HOST_PORT, "");
        if (ipAddress.equals("") || port.equals("")) {
            Intent intent = new Intent(activity, PreferencesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Grab the IP and port from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        UDPSender.setPort(Integer.parseInt(prefs.getString(PREF_KEY_HOST_PORT, "2390")));
        UDPSender.setIpAddress(prefs.getString(PREF_KEY_IP_ADDRESS, "127.0.0.0"));

        View viewTreeRoot = inflater.inflate(R.layout.fragment_controller, container, false);

        // Set callbacks for all buttons

        Button powerButton = (Button) viewTreeRoot.findViewById(R.id.Start);
        powerButton.setActivated(false);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View powerButton) {
                if (UDPSender.isRunning()) {
                    UDPSender.setRunningState(false);
                    powerButton.setBackgroundResource(R.drawable.bg);

                }
                else {
                    UDPSender.setRunningState(true);
                    powerButton.setBackgroundResource(R.drawable.bg);
                    UDPSender.beginUdpLoop();
                    UDPSender.authenticate = true; //authenticate to the server
                    UDPSender.authenticate = false; // stop sending authentication messages
                }
            }
        });

        Button upButton = (Button) viewTreeRoot.findViewById(R.id.A);
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        UDPSender.park = false;  //quit stopping
                        UDPSender.forward = true; //go
                        Log.d("controller Fragment","Up button pressed");
                        break;
                    case MotionEvent.ACTION_UP:
                        UDPSender.forward = false; //stop going
                        UDPSender.park = true; // stop
                        break;
                }
                return true;
            }

        });

        // park = stop

        Button downButton = (Button) viewTreeRoot.findViewById(R.id.B);
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        UDPSender.park = false; // quit sending stop messages
                        UDPSender.reverse = true;//go
                        break;
                    case MotionEvent.ACTION_UP:
                        UDPSender.reverse = false;//stop sending signal to go
                        UDPSender.park = true;//kill power
                        break;
                }
                return true;
            }

        });

        Button leftButton = (Button) viewTreeRoot.findViewById(R.id.Left);
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        UDPSender.realign = false; //stop telling it to realign
                        UDPSender.left = true; //start turning left
                        break;
                    case MotionEvent.ACTION_UP:
                        UDPSender.left = false; //stop sending left turn signals
                        UDPSender.realign = true; //realign the wheels
                        break;
                }
                return true;
            }

        });

        Button rightButton = (Button) viewTreeRoot.findViewById(R.id.Right);
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        UDPSender.realign = false;
                        UDPSender.right = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        UDPSender.right = false; //stop sending right turn signals
                        UDPSender.realign = true; //reset wheels to straight
                        break;
                }
                return true;
            }

        });

        return viewTreeRoot;
    }
}

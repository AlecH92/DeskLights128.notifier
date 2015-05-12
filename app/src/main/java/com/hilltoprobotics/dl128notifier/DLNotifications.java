package com.hilltoprobotics.dl128notifier;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DLNotifications extends AccessibilityService {
    FileInputStream fileIn;
    HashMap<String, String> theMap;
    public static SharedPreferences sharedPrefs;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            final String packagename = String.valueOf(event.getPackageName());
            theMap = new HashMap<>();
            Log.d("DL128","Got notification change: " + packagename);
            try {
                fileIn = openFileInput(event.getPackageName().toString());
                byte[] buffer = new byte[(int) fileIn.getChannel().size()];
                fileIn.read(buffer);
                String str= "";
                for(byte b:buffer) str+=(char)b;
                fileIn.close();
                Log.d("DL128", "initial " + str);

                Properties props = new Properties();
                props.load(new StringReader(str.substring(1, str.length() - 1).replace(", ", "\n")));
                for (Map.Entry<Object, Object> e : props.entrySet()) {
                    theMap.put((String)e.getKey(), (String)e.getValue());
                }
                Log.d("Dl128", "aftermap " + theMap.toString());

                if(theMap.get("checked").equals("y")) {
                    Log.d("DL128","checkedTrue");
                    String whatToSend = "alertArea?h=" + Integer.toHexString(Integer.parseInt(theMap.get("color"))).toUpperCase().substring(2)
                            + "&x=" + theMap.get("xInitial")+ "&y=" + theMap.get("yInitial")
                            + "&c=" + theMap.get("xEnd")+ "&u=" + theMap.get("yEnd");
                    sendData(whatToSend);
                }
                else {
                    Log.d("DL128","checkedFalse");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d("DL128","Disconnected");
    }

    @Override
    protected void onServiceConnected() {
        Log.d("DL128", "Connected");
    }

    void sendData(String theData) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String url = "http://" + sharedPrefs.getString("prefIP", "127.0.0.1") + "/" + theData;
        Log.d("DL128","Sending data: " + url);
        final ThreadedRequest tReq = new ThreadedRequest(url);
        tReq.start(new Runnable() {
            public void run() {
            }
        });
    }
}

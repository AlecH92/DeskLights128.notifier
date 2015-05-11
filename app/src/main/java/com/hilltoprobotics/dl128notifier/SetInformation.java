package com.hilltoprobotics.dl128notifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class SetInformation extends ActionBarActivity {
    String theTitle = "";
    String pkgInfo = "";
    CheckBox enabledCheck;
    EditText xInitial;
    EditText xEnd;
    EditText yInitial;
    EditText yEnd;
    HashMap<String, String> theMap;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    FileOutputStream fileOut;
    FileInputStream fileIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_information);
        theMap = new HashMap<>();
        enabledCheck = (CheckBox) findViewById(R.id.enabledCheck);
        xInitial = (EditText) findViewById(R.id.xValue);
        xEnd = (EditText) findViewById(R.id.xValueE);
        yInitial = (EditText) findViewById(R.id.yValue);
        yEnd = (EditText) findViewById(R.id.yValueE);
        theTitle = getIntent().getStringExtra("title");
        pkgInfo = getIntent().getStringExtra("pkgName");
        Log.d("DL128", pkgInfo);
        try {
            fileIn = openFileInput(pkgInfo);
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
                enabledCheck.setChecked(true);
            }
            else {
                Log.d("DL128","checkedFalse");
                enabledCheck.setChecked(false);
            }
            xInitial.setText(theMap.get("xInitial"));
            xEnd.setText(theMap.get("xEnd"));
            yInitial.setText(theMap.get("yInitial"));
            yEnd.setText(theMap.get("yEnd"));
            theMap.clear();
        } catch (IOException e) {
            theMap.put("checked","n");
            theMap.put("xInitial","0");
            theMap.put("xEnd","0");
            theMap.put("yInitial","0");
            theMap.put("yEnd","0");
            enabledCheck.setChecked(false);
            xInitial.setText("0");
            xEnd.setText("0");
            yInitial.setText("0");
            yEnd.setText("0");
            e.printStackTrace();
        }
        android.support.v7.app.ActionBar theBar = getSupportActionBar();
        theBar.setTitle("Settings for " + theTitle);
        theMap.put("checked", "n");
        theMap.put("xInitial","0");
        theMap.put("xEnd","0");
        theMap.put("yInitial","0");
        theMap.put("yEnd","0");

        enabledCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
        xInitial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkData();
            }
        });
        xEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkData();
            }
        });
        yEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkData();
            }
        });
        yInitial.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkData();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        checkData();
        Log.d("DL128","save");
        if(fileOut != null) {
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
     public void onStop() {
        super.onStop();
        checkData();
        Log.d("DL128", "save");
        Log.d("DL128", theMap.toString());
        if(fileOut != null) {
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void checkData() {
        if (enabledCheck.isChecked()) {
            theMap.put("checked", "y");
        } else {
            theMap.put("checked","n");
        }
        if(xInitial.getText().toString() == "") {
            theMap.put("xInitial","0");
        }
        else {
            theMap.put("xInitial", xInitial.getText().toString());
        }
        if(xEnd.getText().toString() == "") {
            theMap.put("xEnd","0");
        }
        else {
            theMap.put("xEnd", xEnd.getText().toString());
        }
        if(xInitial.getText().toString() == "") {
            theMap.put("yInitial","0");
        }
        else {
            theMap.put("yInitial", yInitial.getText().toString());
        }
        if(xInitial.getText().toString() == "") {
            theMap.put("yEnd", "0");
        }
        else {
            theMap.put("yEnd", yEnd.getText().toString());
        }
        try {
            deleteFile(pkgInfo);
            fileOut = openFileOutput(pkgInfo, MODE_PRIVATE);
            fileOut.write(theMap.toString().getBytes());
            Log.d("DL128","edit: " + theMap.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.hilltoprobotics.dl128notifier;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import yuku.ambilwarna.AmbilWarnaDialog;


public class SetInformation extends ActionBarActivity {
    String theTitle = "";
    String pkgInfo = "";
    Button colorSelect;
    ImageView thisColorView;
    CheckBox enabledCheck;
    EditText xInitial;
    EditText xEnd;
    EditText yInitial;
    EditText yEnd;
    HashMap<String, String> theMap;
    FileOutputStream fileOut;
    FileInputStream fileIn;
    public AmbilWarnaDialog dialog;
    public int theColor = -12303292;
    int initialColor = 0;

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
        colorSelect = (Button) findViewById(R.id.colorSelect);
        thisColorView = (ImageView) findViewById(R.id.colorView);
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
            thisColorView.setImageDrawable(new ColorDrawable(Integer.parseInt(theMap.get("color"))));
            initialColor = Integer.parseInt(theMap.get("color"));
            theColor = initialColor;
            theMap.clear();
        } catch (IOException e) {
            theMap.put("checked","n");
            theMap.put("xInitial","0");
            theMap.put("xEnd","0");
            theMap.put("yInitial","0");
            theMap.put("yEnd", "0");
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

        colorSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

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

        dialog = new AmbilWarnaDialog(this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                theColor = color;
                thisColorView.setImageDrawable(new ColorDrawable(theColor));
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
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
        if(xInitial.getText().toString().equals("")) {
            theMap.put("xInitial","0");
        }
        else {
            theMap.put("xInitial", xInitial.getText().toString());
        }
        if(xEnd.getText().toString().equals("")) {
            theMap.put("xEnd","0");
        }
        else {
            theMap.put("xEnd", xEnd.getText().toString());
        }
        if(xInitial.getText().toString().equals("")) {
            theMap.put("yInitial","0");
        }
        else {
            theMap.put("yInitial", yInitial.getText().toString());
        }
        if(xInitial.getText().toString().equals("")) {
            theMap.put("yEnd", "0");
        }
        else {
            theMap.put("yEnd", yEnd.getText().toString());
        }
        theMap.put("color", String.valueOf(theColor));
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

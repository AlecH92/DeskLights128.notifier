package com.hilltoprobotics.dl128notifier;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class MainActivity extends ActionBarActivity {
    ListView thisList;
    List<ApplicationInfo> packages;
    ArrayList<String> packagesInfoName;
    ArrayList<String> packagesRealName;
    ArrayList<String> packagesNew;
    HashMap<String, String> aList;
    HashMap<String, String> theMap;
    FileInputStream fileIn;
    Integer packageCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PackageManager pm = getPackageManager();
        thisList = (ListView) findViewById(R.id.listView);
        packagesInfoName = new ArrayList<>();
        packagesRealName = new ArrayList<>();
        packagesNew = new ArrayList<>();
        aList = new HashMap<>();
        theMap = new HashMap<>();
        packageCount = 2;

        String[] pkgList = fileList();
        for(String thePackage : pkgList) {
            try {
                fileIn = openFileInput(thePackage);
                byte[] buffer = new byte[(int) fileIn.getChannel().size()];
                fileIn.read(buffer);
                String str = "";
                for (byte b : buffer) str += (char) b;
                fileIn.close();

                Properties props = new Properties();
                props.load(new StringReader(str.substring(1, str.length() - 1).replace(", ", "\n")));
                for (Map.Entry<Object, Object> e : props.entrySet()) {
                    theMap.put((String) e.getKey(), (String) e.getValue());
                }

                boolean isEnabled = Boolean.parseBoolean(theMap.get("checked"));
                if(isEnabled) {
                    Log.d("DL128", "Enabled: " + thePackage);
                    packageCount++;
                }
                else {
                    Log.d("DL128", "Disabled: " + thePackage);
                }
            }
            catch (IOException e) {
            }
        }



        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            try {
                String theData = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                packagesInfoName.add(theData + " - " + packageInfo.processName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(packagesInfoName);

        packagesNew.add("Currently Enabled:");

        for(String thePackage : pkgList) {
            try {
                fileIn = openFileInput(thePackage);
                byte[] buffer = new byte[(int) fileIn.getChannel().size()];
                fileIn.read(buffer);
                String str = "";
                for (byte b : buffer) str += (char) b;
                fileIn.close();

                Properties props = new Properties();
                props.load(new StringReader(str.substring(1, str.length() - 1).replace(", ", "\n")));
                for (Map.Entry<Object, Object> e : props.entrySet()) {
                    theMap.put((String) e.getKey(), (String) e.getValue());
                }

                boolean isEnabled = Boolean.parseBoolean(theMap.get("checked"));
                if(isEnabled) {
                    String theData = (String) pm.getApplicationLabel(pm.getApplicationInfo(thePackage, PackageManager.GET_META_DATA));
                    packagesNew.add(theData + " - " + pm.getApplicationInfo(thePackage, PackageManager.GET_META_DATA).processName);
                }
                else {
                }
            }
            catch (IOException e) {
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        packagesNew.add("All apps:");
        packagesNew.addAll(packagesInfoName);
        Log.d("DL128", "packageCount: " + packageCount);

        int i = 0;
        for (String str : packagesNew) {
            if(i==0) {
                aList.put(String.valueOf(i),"system");
                packagesRealName.add("system");
                i++;
            }
            else if(i==packageCount-1) {
                aList.put(String.valueOf(i),"system");
                packagesRealName.add("system");
                i++;
            }
            else {
                String[] parts = str.split(" - ");
                aList.put(String.valueOf(i), parts[1]);
                packagesRealName.add(parts[1]);
                i++;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,packagesNew);
        thisList.setAdapter(adapter);
        thisList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    return;
                }
                if(position==packageCount-1) {
                    return;
                }
                Log.d("DL128", "Value of pos " + String.valueOf(position));
                Intent intent = new Intent(MainActivity.this, SetInformation.class);
                intent.putExtra("pkgName", aList.get(String.valueOf(position)));
                intent.putExtra("title", "error");
                try {
                    intent.putExtra("title", (String) pm.getApplicationLabel(pm.getApplicationInfo(aList.get(String.valueOf(position)), PackageManager.GET_META_DATA)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

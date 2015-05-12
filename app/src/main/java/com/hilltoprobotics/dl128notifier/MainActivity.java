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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    ListView thisList;
    List<ApplicationInfo> packages;
    ArrayList<String> packagesInfoName;
    ArrayList<String> packagesRealName;
    HashMap<String, String> aList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PackageManager pm = getPackageManager();
        thisList = (ListView) findViewById(R.id.listView);
        packagesInfoName = new ArrayList<>();
        packagesRealName = new ArrayList<>();
        aList = new HashMap<>();

        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            try {
                String theData = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                packagesInfoName.add(theData + " - " + packageInfo.processName);
                //packagesRealName.add(packageInfo.processName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(packagesInfoName);
        int i = 0;
        for (String str : packagesInfoName) {
            String[] parts = str.split(" - ");
            aList.put(String.valueOf(i),parts[1]);
            packagesRealName.add(parts[1]);
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,packagesInfoName);
        thisList.setAdapter(adapter);
        thisList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DL128","Value of pos " + String.valueOf(position));
                Intent intent = new Intent(MainActivity.this,SetInformation.class);
                intent.putExtra("pkgName", aList.get(String.valueOf(position)));
                intent.putExtra("title","error");
                try {
                    intent.putExtra("title",(String) pm.getApplicationLabel(pm.getApplicationInfo(aList.get(String.valueOf(position)), PackageManager.GET_META_DATA)));
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

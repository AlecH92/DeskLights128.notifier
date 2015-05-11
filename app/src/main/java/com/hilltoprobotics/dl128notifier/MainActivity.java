package com.hilltoprobotics.dl128notifier;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    ListView thisList;
    List<ApplicationInfo> packages;
    ArrayList<String> packagesInfoName;
    ArrayList<String> packagesRealName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PackageManager pm = getPackageManager();
        thisList = (ListView) findViewById(R.id.listView);
        packagesInfoName = new ArrayList<String>();
        packagesRealName = new ArrayList<String>();

        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            try {
                String theData = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                packagesInfoName.add(theData + " - " + packageInfo.processName);
                packagesRealName.add(packageInfo.processName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,packagesInfoName);
        thisList.setAdapter(adapter);
        thisList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) thisList.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this,SetInformation.class);
                intent.putExtra("pkgName",packagesRealName.get(position));
                intent.putExtra("title","error");
                try {
                    intent.putExtra("title",(String) pm.getApplicationLabel(pm.getApplicationInfo(packages.get(position).packageName, PackageManager.GET_META_DATA)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

package com.example.servicecreator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class wifi extends Service {
    public wifi() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        STEP 3:
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Intent i = new Intent();
        i.putExtra("data", String.format("Your location: (%s, %s)", location.getLatitude(), location.getLongitude()));

        i.setComponent(new ComponentName("com.example.serviceinvoker", "com.example.serviceinvoker.Receiver"));
        startService(i);

//        STEP 2:
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wi = wm.getConnectionInfo();
//        String ssid = wi.getSSID();
//
//        Intent i = new Intent();
//        i.putExtra("data", ssid);
//        i.setComponent(new ComponentName("com.example.serviceinvoker", "com.example.serviceinvoker.Receiver"));
//        startService(i);


//        STEP 1:
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wi = wm.getConnectionInfo();
//        String ssid = wi.getSSID();
//        Toast.makeText(this, ssid, Toast.LENGTH_LONG).show();



        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
package com.example.serviceinvoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class Receiver extends Service {
    public Receiver() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String data = "Toast from serviceInvoker: " + intent.getStringExtra("data");

        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
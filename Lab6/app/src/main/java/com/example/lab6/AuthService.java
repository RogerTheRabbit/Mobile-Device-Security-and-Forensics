package com.example.lab6;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthService extends Service {
    public AuthService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Authenticator authenticator = new Authenticator(this);
        authenticator.getIBinder();
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
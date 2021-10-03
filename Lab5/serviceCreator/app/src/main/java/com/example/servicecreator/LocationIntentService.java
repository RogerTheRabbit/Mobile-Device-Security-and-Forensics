package com.example.servicecreator;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationIntentService extends IntentService {

    private static final String ACTION_GET_LOCATION = "com.example.servicecreator.action.ACTION_GET_LOCATION";

    public LocationIntentService() {
        super("LocationIntentService");
    }

    public static void startActionGetLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Intent i = new Intent();
        i.putExtra("data", String.format("Your location: (%s, %s)", location.getLatitude(), location.getLongitude()));

        i.setComponent(new ComponentName("com.example.serviceinvoker", "com.example.serviceinvoker.Receiver"));
        context.startService(i);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_LOCATION.equals(action)) {
                handleActionGetLocation();
            }
        }
    }

    private void handleActionGetLocation() {
        startActionGetLocation(getApplicationContext());
    }
}
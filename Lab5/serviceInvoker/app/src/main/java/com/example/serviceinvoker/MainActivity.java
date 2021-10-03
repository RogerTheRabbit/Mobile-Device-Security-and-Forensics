package com.example.serviceinvoker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

//    Part 4
    private static final String ACTION_GET_LOCATION = "com.example.servicecreator.action.ACTION_GET_LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent();
        i.putExtra("message", "Hello world!");

//        Part 1-3
//        i.setComponent(new ComponentName("com.example.servicecreator", "com.example.servicecreator.wifi"));

//        Part 4
        i.setAction(ACTION_GET_LOCATION);
        i.setComponent(new ComponentName("com.example.servicecreator", "com.example.servicecreator.LocationIntentService"));

        startService(i);
    }
}
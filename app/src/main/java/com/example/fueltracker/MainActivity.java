package com.example.fueltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = findViewById(R.id.layoutMain);

        TextView vText = new TextView(this);
        TextView aText = new TextView(this);
        TextView fuelText = new TextView(this);

        vText.setText("a");
        aText.setText("b");
        fuelText.setText("c");

        layout.addView(vText);
        layout.addView(aText);
        layout.addView(fuelText);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                vText.setText(Float.toString(CarSession._speed));
                aText.setText(Float.toString(CarSession._acceleration));
                fuelText.setText(Float.toString(CarSession._fuel));
            }
        }, 0, 500);
    }
}
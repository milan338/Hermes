package com.example.fueltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Hermes by 'Team Name'");

//        LinearLayout layout = findViewById(R.id.layoutMain);

//        TextView vText = new TextView(this);
//        TextView aText = new TextView(this);
//        TextView fuelText = new TextView(this);

        Button speedButton = findViewById(R.id.speedButton);
        Button fuelButton = findViewById(R.id.fuelButton);
        TextView carLabel = findViewById(R.id.carLabel);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                speedButton.setText(
                        String.format("Speed: %.0f kmh⁻¹    Acceleration: %.0f ms⁻²",
                                CarSession._speed * 3.6, CarSession._acceleration));
                fuelButton.setText(String.format("Fuel: %.0f%%", CarSession._fuel));
                carLabel.setText(String.format("%d %s %s", CarSession._year, CarSession._make, CarSession._model));
            }
        }, 0, 100);
    }
}
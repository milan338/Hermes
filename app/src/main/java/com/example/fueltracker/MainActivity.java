package com.example.fueltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int lastStrikes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Hermes by 'Team Name'");

        Button speedButton = findViewById(R.id.speedButton);
        Button fuelButton = findViewById(R.id.fuelButton);
        TextView carLabel = findViewById(R.id.carLabel);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Update UI text
                speedButton.setText(
                        String.format("Speed: %.0f kmh⁻¹    Acceleration: %.0f ms⁻²",
                                CarSession._speed * 3.6, CarSession._acceleration));
                fuelButton.setText(String.format("Fuel: %.0f%%", CarSession._fuel));
                if (CarSession._year != 0)
                    carLabel.setText(String.format("%d %s %s", CarSession._year, CarSession._make, CarSession._model));
                // Update scores
                handleScore();
            }
        }, 0, 100);
    }

    private void updateScoreSlider() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        float percent = 100 - (3 * preferences.getFloat("FuelTrackerScore", 0));
        ProgressBar scoreSlider = findViewById(R.id.efficiencyProgress);
        scoreSlider.setProgress(Math.max(Math.round(percent), 0));
    }

    private void handleScore() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int strikes = CarSession.strikes;
        // Strikes didn't change
        if (strikes == lastStrikes) return;
        // Reset strikes in car session
        CarSession.strikes = 0;
        long time = System.currentTimeMillis() - CarSession.strikeTime;
        // Update strike time to go from
        CarSession.strikeTime = System.currentTimeMillis();
        lastStrikes = strikes;
        int totalStrikes = preferences.getInt("FuelTrackerStrikes", 0) + strikes;
        long totalTime = preferences.getLong("FuelTrackerStrikesTime", 0) + time;
        // Given an hour (3 600 000 ms) of driving:
        // - 0 strikes is great
        // - 3 errors is ok
        // - 5 errors and more is bad
        // These values scale such that with the same numbers of errors
        // In a shorter time, the efficiency is worse, and is better given a longer time
        float score = totalStrikes * (3600000f / totalTime);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("FuelTrackerStrikes", totalStrikes);
        editor.putLong("FuelTrackerStrikesTime", totalTime);
        editor.putFloat("FuelTrackerScore", score);
        editor.apply();
        updateScoreSlider();
    }
}
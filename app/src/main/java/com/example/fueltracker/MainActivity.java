package com.example.fueltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    }
}
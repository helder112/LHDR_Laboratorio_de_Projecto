package com.droiduino.bluetoothconn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        final Button buttonBack = findViewById(R.id.buttonBack2);

        final TextView textViewTemp = findViewById(R.id.textViewTemp);
        final TextView textViewTempValue = findViewById(R.id.textViewTempValue);

        final TextView textViewHum = findViewById(R.id.textViewHum);
        final TextView textViewHumValue = findViewById(R.id.textViewHumValue);

        final TextView textViewLum = findViewById(R.id.textViewLum);
        final TextView textViewLumValue = findViewById(R.id.textViewLumValue);







        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
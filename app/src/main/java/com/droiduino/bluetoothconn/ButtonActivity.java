package com.droiduino.bluetoothconn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ButtonActivity extends AppCompatActivity {

    private TextView textViewInfo3;
    private TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        textViewInfo3 = findViewById(R.id.textViewInfo3);
        textView5 = findViewById(R.id.textView5);

        Intent intent = getIntent();

        if (intent != null) {
            String arduinoMessage = intent.getStringExtra("arduino_message");
            if (arduinoMessage != null) {
                textViewInfo3.setText(arduinoMessage);
            }
        }

        final Button buttonToggle4 = findViewById(R.id.buttonToggle4);
        final Button buttonToggle5 = findViewById(R.id.buttonToggle5);
        final Button buttonToggle7 = findViewById(R.id.buttonToggle7);
        final Button buttonBack = findViewById(R.id.buttonBack4);

        buttonToggle4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "4;";
                MainActivity.connectedThread.write(cmdText);
                textView5.setText("text");
            }
        });

        buttonToggle5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "5;";
                MainActivity.connectedThread.write(cmdText);
            }
        });

        buttonToggle7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "6;";
                MainActivity.connectedThread.write(cmdText);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

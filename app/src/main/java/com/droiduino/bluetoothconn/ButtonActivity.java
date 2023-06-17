package com.droiduino.bluetoothconn;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.droiduino.bluetoothconn.MainActivity;



public class ButtonActivity extends AppCompatActivity {

    public static MainActivity.CreateConnectThread createConnectThread;

    public static MainActivity.ConnectedThread connectedThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        final TextView textViewInfo = findViewById(R.id.textViewInfo4);
        final Button buttonToggle4 = findViewById(R.id.buttonToggle4);

        final Button buttonToggle5 = findViewById(R.id.buttonToggle5);

        final Button buttonToggle7 = findViewById(R.id.buttonToggle7);

        connectedThread = MainActivity.connectedThread ;



        buttonToggle4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "4;";
                connectedThread.write(cmdText);
            }
        });


        buttonToggle5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "5;";
                connectedThread.write(cmdText);
            }
        });

        buttonToggle7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "6;";
                connectedThread.write(cmdText);
            }
        });
    }
}
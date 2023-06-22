package com.lhdr.bluetoothconn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Initialization
        final ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        viewFlipper.setInAnimation(slideInAnimation);
        viewFlipper.setOutAnimation(slideOutAnimation);
        viewFlipper.setDisplayedChild(0);

        //First Screen Elements
        final Button buttonConnect = findViewById(R.id.buttonConnect);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        final Button lightsButton = findViewById(R.id.lightsButton);
        lightsButton.setEnabled(false);
        final Button buttonToggle2 = findViewById(R.id.buttonToggle2);
        buttonToggle2.setEnabled(false);
        final Button buttonToggle3 = findViewById(R.id.buttonToggle3);
        buttonToggle3.setEnabled(false);
        //First Screen Elements End

        //Second Screen Elements
        final Button buttonBack4 = findViewById(R.id.buttonBack4);
        final Button buttonToggle4 = findViewById(R.id.buttonToggle4);
        final Button buttonToggle5 = findViewById(R.id.buttonToggle5);
        final Button buttonToggle7 = findViewById(R.id.buttonToggle7);
        final Button buttonToggleAll = findViewById(R.id.buttonToggleA);
        final TextView textViewInfoLights = findViewById(R.id.textViewInfo3);
        final Button buttonToggleAutoLightsAll = findViewById(R.id.buttonToggleAutoLightsAll);
        //Second Screen Elements End

        //Third Screen Elements
        final Button buttonBack2 = findViewById(R.id.buttonBack2);
        final TextView textViewTempValue = findViewById(R.id.textViewTempValue);
        final TextView textViewHumValue = findViewById(R.id.textViewHumValue);
        final TextView textViewLumValue = findViewById(R.id.textViewLumValue);
        //Third Screen Elements End

        //Fourth Screen Elements
        final Button buttonBackSettings = findViewById(R.id.buttonBackSettings);;
        final Button applytemp = findViewById(R.id.applytemp);
        final Button applyhum = findViewById(R.id.applyhum);
        final Button applylum = findViewById(R.id.applylum);
        final EditText tempmin = findViewById(R.id.tempmin);
        final EditText tempmax = findViewById(R.id.tempmax);
        final EditText hummin = findViewById(R.id.hummin);
        final EditText hummax = findViewById(R.id.hummax);
        final EditText lummin = findViewById(R.id.lummin);
        final EditText lummax = findViewById(R.id.lummax);
        final ToggleButton dinTB = findViewById(R.id.dinTB);
        final ToggleButton bedTB = findViewById(R.id.bedTB);
        final ToggleButton livTB = findViewById(R.id.livTB);
        //Fourth Screen Elements End


        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progress and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            buttonConnect.setEnabled(false);

            /*
            When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }

        //GUI Handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                lightsButton.setEnabled(true);
                                buttonToggle2.setEnabled(true);
                                buttonToggle3.setEnabled(true);
                                connectedThread.write("k;");
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        String[] splitMsg = arduinoMsg.split(" ");


                        switch (splitMsg[0]) {
                            //Startup Configs
                            case "configs":
                                tempmin.setText(splitMsg[2]);
                                tempmax.setText(splitMsg[3]);
                                hummin.setText(splitMsg[6]);
                                hummax.setText(splitMsg[7]);
                                lummin.setText(splitMsg[10]);
                                lummax.setText(splitMsg[11]);
                                break;
                            case "Living":
                            case "Bedroom":
                            case "Dining":
                            case "ALL":
                            case "Auto":
                                textViewInfoLights.setText(arduinoMsg);
                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        textViewInfoLights.setText("");
                                    }
                                }, 2000);
                                break;
                            case "Temperature":
                                textViewTempValue.setText(splitMsg[1]);
                                textViewHumValue.setText(splitMsg[3]);
                                break;
                            case "Luminosity":
                                textViewLumValue.setText(splitMsg[1]);
                                break;
                        }
                        break;
                 }
            }
        };
        // Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to adapter list
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });

        // Back Buttons Action
        buttonBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });
        buttonBack4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });
        buttonBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });

        //Main Page Buttons Action
        lightsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(1);
            }
        });
        buttonToggle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(2);
            }
        });
        buttonToggle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(3);
            }
        });

        /* ============================ Light Control Screen Action =================================== */
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
        buttonToggleAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "0;";
                connectedThread.write(cmdText);
            }
        });
        buttonToggleAutoLightsAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = "1;";
                connectedThread.write(cmdText);
            }
        });

        dinTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Check if the toggle button is checked
                if (isChecked) {
                    String cmdText = "D 1;";
                    connectedThread.write(cmdText);
                } else if (!isChecked){
                    String cmdText = "D 0;";
                    connectedThread.write(cmdText);
                }
            }
        });
        bedTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Check if the toggle button is checked
                if (isChecked) {
                    String cmdText = "E 1;";
                    connectedThread.write(cmdText);
                } else if (!isChecked){
                    String cmdText = "E 0;";
                    connectedThread.write(cmdText);
                }

            }
        });
        livTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Check if the toggle button is checked
                if (isChecked) {
                    String cmdText = "F 1;";
                    connectedThread.write(cmdText);
                } else if (!isChecked){
                    String cmdText = "F 0;";
                    connectedThread.write(cmdText);
                }
            }
        });

        applytemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String min = tempmin.getText().toString();
                String max = tempmax.getText().toString();
                String toSend = "A" + "," + min + "," + max + ";";
                connectedThread.write(toSend);
            }
        });
        applyhum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String min = hummin.getText().toString();
                String max = hummax.getText().toString();
                String toSend = "B" + "," + min + "," + max + ";";
                connectedThread.write(toSend);
            }
        });
        applylum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String min = lummin.getText().toString();
                String max = lummax.getText().toString();
                String toSend = "C" + "," + min + "," + max + ";";
                connectedThread.write(toSend);
            }
        });


    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}

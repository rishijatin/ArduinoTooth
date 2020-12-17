package com.example.arduinotooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button button;
    Button button2;
    TextView textView;
    BluetoothAdapter bluetoothAdapter;
    Spinner spinner;
    ProgressBar progressBar;
    BlueToothBrodcast blueToothBrodcast;
    DeviceDiscoverBroadCast deviceDiscoverBroadCast;
    ArrayList<String> devices = new ArrayList<String>();
    ArrayList<BluetoothDevice> devicesAddress = new ArrayList<BluetoothDevice>();
    ConnectThread connectThread;
    ArrayAdapter <String> arrayAdapter;


    BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String action= intent.getAction();
                if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
                {
                    devices.clear();
                    devicesAddress.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                }
                else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
                {
                    progressBar.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    arrayAdapter.notifyDataSetChanged();
                    button2.setVisibility(View.VISIBLE);
                }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*********** Get Instance of Custom BlueToothBroadCast and Register Reciever**************/
        blueToothBrodcast = new BlueToothBrodcast();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(blueToothBrodcast,filter);

        IntentFilter filter1= new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 =  new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,filter1);
        registerReceiver(broadcastReceiver,filter2);

        deviceDiscoverBroadCast = new DeviceDiscoverBroadCast();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        button= findViewById(R.id.button);
        button2=findViewById(R.id.button2);
        spinner = findViewById(R.id.spinner);
        progressBar=findViewById(R.id.progressBar);
        textView= findViewById(R.id.textView);

        if ((bluetoothAdapter.isEnabled())) {
            button.setText("BlueTooth - ON");
        } else {
            button.setText("BluteTooth - OFF");
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,devices);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);




        /***** State Listeners *******/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForNewDevices();
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position= spinner.getSelectedItemPosition();
                if(position!=-1) {
                    bluetoothAdapter.cancelDiscovery();
                    connectThread= new ConnectThread(devicesAddress.get(position));
                    connectThread.setMyCallback(new ConnectThread.callback() {
                        @Override
                        public void sucess(String name, String address) {
                            spinner.setVisibility(View.GONE);
                            button2.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    connectThread.write("a");
                                }
                            });
                            textView.setText(String.format("Device connected\nName- %s\nM.A.C Address - %s",name,address));
                            button.setText("Send data");
                        }
                    });
                    connectThread.run();

                }
            }
        });







        blueToothBrodcast.setbrodcastListener(new BlueToothBrodcast.brodcastListener() {
            @Override
            public void getBTState(String state) {
                if(state.equals("ON"))
                {
                    button.setText("BlueTooth- ON");
                }
                else if( state.equals("OFF"))
                {
                    button.setText("BlueTooth - OFF");
                }
            }
        });

    }

    void connectToBT()
    {

        if(bluetoothAdapter==null)
        {
            Toast.makeText(this,"Bluetooth not supported",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(!bluetoothAdapter.isEnabled())
            {
                Intent enableBTIntent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent,1);
            }
        }
    }



    void searchForNewDevices()
    {
        bluetoothAdapter.startDiscovery();
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(deviceDiscoverBroadCast,filter3);
        deviceDiscoverBroadCast.setDeviceListener(new DeviceDiscoverBroadCast.discoverDevice() {
            @Override
            public void device(String deviceName, String deviceAddress,BluetoothDevice device) {
                if(deviceName!=null) {
                    devices.add(deviceName);
                    devicesAddress.add(device);
                    Log.i("Jatin", deviceName);
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blueToothBrodcast);
        unregisterReceiver(deviceDiscoverBroadCast);
        unregisterReceiver(broadcastReceiver);
    }
}
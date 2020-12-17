package com.example.arduinotooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button;
    BluetoothAdapter bluetoothAdapter;
    BlueToothBrodcast blueToothBrodcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*********** Get Instance of Custom BlueToothBroadCast and Register Reciever**************/
        blueToothBrodcast = new BlueToothBrodcast();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(blueToothBrodcast,filter);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        button= findViewById(R.id.button);

        if ((bluetoothAdapter.isEnabled())) {
            button.setText("BlueTooth - ON");
        } else {
            button.setText("BluteTooth - OFF");
        }


        /***** State Listeners *******/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToBT();
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
}
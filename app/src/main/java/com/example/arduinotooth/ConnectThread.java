package com.example.arduinotooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private OutputStream outputStream;
    private callback myCallback;

    public void setMyCallback(callback myCallback) {
        this.myCallback = myCallback;
    }

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            Log.e("Jatin", "Socket's create() method failed", e);
        }
        mmSocket = tmp;

    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.i("Jatin","Started");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Jatin", "Could not close the client socket", closeException);
            }
            return;
        }

        myCallback.success(mmDevice.getName(),mmDevice.getAddress());

    }

    public void write(String data)
    {
        try {
            outputStream= mmSocket.getOutputStream();
            outputStream.write(data.getBytes());
        }
        catch (Exception e)
        {
            Log.i("Jatin","Some thing occured");
            e.printStackTrace();
        }
    }


    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Jatin", "Could not close the client socket", e);
        }
    }

    public interface callback
    {
        void success(String name,String address);
    }
}

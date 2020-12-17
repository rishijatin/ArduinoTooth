package com.example.arduinotooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceDiscoverBroadCast extends BroadcastReceiver {

    public discoverDevice deviceListener;

    public void setDeviceListener(discoverDevice deviceListener) {
        this.deviceListener = deviceListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress();
            deviceListener.device(deviceName,deviceHardwareAddress,device);
        }
    }

    public interface discoverDevice
    {
        void device(String deviceName, String deviceAddress,BluetoothDevice device);
    }


}

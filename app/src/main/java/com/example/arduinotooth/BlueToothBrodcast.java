package com.example.arduinotooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BlueToothBrodcast extends BroadcastReceiver {

    public brodcastListener mListener;


    public void setbrodcastListener(brodcastListener mListener)
    {
        this.mListener=mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action= intent.getAction();

        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
        {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                               BluetoothAdapter.ERROR);

            switch (state)
            {
                case BluetoothAdapter.STATE_OFF:
                    mListener.getBTState("OFF");
                    break;

                case BluetoothAdapter.STATE_ON:
                    mListener.getBTState("ON");
                    break;
            }

        }
    }

    public interface brodcastListener
    {
        void getBTState(String state);
    }

}

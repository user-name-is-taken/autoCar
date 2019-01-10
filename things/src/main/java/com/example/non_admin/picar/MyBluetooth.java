package com.example.non_admin.picar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.things.bluetooth.BluetoothClassFactory;
import com.google.android.things.bluetooth.BluetoothConfigManager;

import static android.content.ContentValues.TAG;

/**
 *
 */
public class MyBluetooth extends MikeProvider{
    private static Context context;
    private static BluetoothAdapter mBluetoothAdapter;



    /**
     * Rust a default constructor that passes toyRobot to the constructor because
     * I'm currently using this as a toy robot.
     *
     * @param context
     * @param mExecutor
     * @see MyBluetooth(Context, MikeExecutor, byte[])
     */
    public MyBluetooth(Context context, MikeExecutor mExecutor){
        super(mExecutor);
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.i(TAG, "Device can't use bluetooth");
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            enableDisableBT();
        }else if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();//in video 1, 3:00, they use an intent to ask permission.
            //I can't ask for permission because I'm on things.
            Log.i(TAG, "Bluetooth switched to enabled");
            //an intent/broadcast callback still might be necessary if enable
            //is done asyncronously.
        }else{
            Log.i(TAG, "Bluetooth was already enabled");
        }

    }



    //note, you must be paired before you can establish an RFCOMM connection




    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "device can't use bluetooth");
        }else if(!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBTIntent);
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
            context.registerReceiver(btStateBroadcastReceiver, BTIntent);
        }else{
            mBluetoothAdapter.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.registerReceiver(btStateBroadcastReceiver, BTIntent);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    // todo: make this non static? remember, it's unregistered in MainActivity.onDestroy
    public final BroadcastReceiver btStateBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "STATE TURNING ON");
                        break;
                }
            }
        }
    };


}

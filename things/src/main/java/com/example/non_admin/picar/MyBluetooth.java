package com.example.non_admin.picar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.bluetooth.BluetoothClass;
import android.util.Log;

import com.google.android.things.bluetooth.BluetoothClassFactory;
import com.google.android.things.bluetooth.BluetoothConfigManager;

import static android.content.ContentValues.TAG;

/**
 *
 */
public class MyBluetooth extends MikeProvider{
    private static Context context;
    private static BluetoothConfigManager manager;
    private static BluetoothAdapter mBluetoothAdapter;


    /*
    This 3 byte toyRobot array represents the Class of Device (CoD) for a toy robot.
    CoDs tell the BluetoothManager what to advertise this device as.

    CoDs in general follow [this standard.](https://www.bluetooth.com/specifications/assigned-numbers/baseband)
    byte[3] of CoDs are specified in this android method, [BluetoothClassFactory.build(byte[]).](https://developer.android.com/reference/com/google/android/things/bluetooth/BluetoothClassFactory)
    To generate the toyRobot byte[3] object, I used [this generator.](http://bluetooth-pentest.narod.ru/software/bluetooth_class_of_device-service_generator.html)
    */
    public static byte[] toyRobot = {0x00, 0x08, 0x04};


    /**
     * Rust a default constructor that passes toyRobot to the constructor because
     * I'm currently using this as a toy robot.
     *
     * @param context
     * @param mExecutor
     * @see MyBluetooth(Context, MikeExecutor, byte[])
     */
    public MyBluetooth(Context context, MikeExecutor mExecutor){
        this(context, mExecutor, toyRobot);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.i(TAG, "Device can't use bluetooth");
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

    /**
     *
     *
     * @param context the context of the app, (might not be necesary?)
     * @param mExecutor the executor that this MikeProvider sends Bluetooth data to.
     * @param CoD see toyRobot
     * @see MikeExecutor
     * @see MikeProvider#provide(String)
     * @see <a href="https://developer.android.com/things/sdk/apis/bluetooth#device-pairing">
     *     this android things bluetooth demo</a>
     *
     * todo: possibly add a param for [manager.setIoCapability](https://developer.android.com/reference/com/google/android/things/bluetooth/BluetoothConfigManager#setIoCapability(int))
     */
    public MyBluetooth(Context context, MikeExecutor mExecutor, byte[] CoD){
        super(mExecutor);
        //********* Configureing device attributes****************
        this.context = context;
        manager = BluetoothConfigManager.getInstance();
        // Report the local Bluetooth device class as a speaker
        BluetoothClass deviceClass = BluetoothClassFactory.build(CoD);
        manager.setBluetoothClass(deviceClass);
        manager.setIoCapability(BluetoothConfigManager.IO_CAPABILITY_UNKNOWN);//the default
    }

    //note, you must be paired before you can establish an RFCOMM connection

}

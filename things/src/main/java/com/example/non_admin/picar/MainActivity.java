package com.example.non_admin.picar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;

import java.util.List;
import java.util.Locale;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    private static String TAG = "MainAct";
    private static BTRemote mBTRemote;
    public static CustomTTS ttsEngine;
    private static final int TTS_DATA_CHECKING = 0;
    public static MyBluetooth myBluetooth;
    private static CustomGPIO mGpio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity started.");
        setContentView(R.layout.activity_main);
        UsbDevice device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        //todo figure out how these devices are uniquely identified. For example, if I disconnect
        //and reconnect the same device, how do I know it's the same device.
        if(device == null){
            Log.d(TAG, "no usb connected");
        }else{
            //according to this https://stackoverflow.com/questions/14053764/how-to-identify-uniquely-a-usb-device
            //You can't uniquely identify USB devices, except by name
            new Device(device, this);// the constructor adds this to its own static HashMap
            Log.d(TAG, "usb device connected. See the constructor for more details");
        }


        if(ttsEngine == null) {
            //create an Intent
            Intent checkData = new Intent();
            //set it up to check for tts data
            checkData.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            //start it so that it returns the result
            startActivityForResult(checkData, TTS_DATA_CHECKING);
        }

        /*
        if(myBluetooth == null){
            //mBTRemote = new BTRemote(this);
            Log.d(TAG, "setting up bluetooth");
            myBluetooth = new MyBluetooth(this, mBTRemote);
        }else{
            Log.d(TAG, "Bluetooth already setup");
        }
        if(mBTRemote == null){
            Log.d(TAG, "Setting up the bluetooth remote");
            mBTRemote = new BTRemote(this);
            //todo: once bluetooth is working, tell the user to hit the GPIO button to
            //make it discoverable with ttsEngine.
        }
        */

        if(mGpio == null) {
            Log.d(TAG, "setting up GPIO");
            mGpio = new CustomGPIO();
        }else{
            Log.d(TAG, "GPIO already setup");
        }
    }
    /**
     * When CustomTTS is created, this handles the result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //do they have the data
        switch (requestCode){
            case TTS_DATA_CHECKING:
                /*
                 * This checks if text to speach is installed. It was copied from:
                 * http://androidthings.blogspot.com/2012/01/android-text-to-speech-tts-basics.html
                 */
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //installed - go ahead and instantiate
                    Log.v(TAG, "TTS is installed...");
                    ttsEngine = new CustomTTS( this);
                }
                else {
                    //no data, prompt to install it
                    Intent promptInstall = new Intent();
                    promptInstall.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivityForResult(promptInstall, TTS_DATA_CHECKING);
                    //you currently don't handle this because TextToSpeech.Engine doesn't have a
                    Log.w(TAG, "TTS wasn't installed, so I'm prompting for it.");
                }
                break;
            default:
                Log.i(TAG, "No matching activity callback for MainActivity." +
                        "onActivityResult");
                break;
        }
    }


    @Override
    public void onStop(){
        ttsEngine.stop();
        mGpio.stop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mGpio.start();
        super.onStart();
    }

    @Override
    protected void onDestroy(){
        if(ttsEngine != null) {
            ttsEngine.shutdown();
        }
        if(mGpio != null){
            Log.i(TAG, "killing gpio");
            mGpio.kill();
        }
        //todo: maybe disconnect devices? maybe disable bluetooth? unregister usb detach receivers?
        Log.i(TAG, "MainActivity destroyed.");
        super.onDestroy();
        unregisterReceiver(myBluetooth.btStateBroadcastReceiver);
        for(Device dev : Device.devName.values()){
            dev.stopUsbConnection();
            dev.unregisterDetachListener();
        }
    }
}
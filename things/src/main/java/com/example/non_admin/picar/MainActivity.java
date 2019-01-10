package com.example.non_admin.picar;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

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
public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private static String TAG = "MainAct";
    private static BTRemote mBTRemote;
    public static CustomTTS ttsEngine;
    private static final int TTS_DATA_CHECKING = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity started.");
        setContentView(R.layout.activity_main);
        UsbDevice device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        //todo figure out how these devices are uniquely identified. For example, if I disconnect
        //and reconnect the same device, how do I know it's the same device.
        if(device == null){
            Log.e(TAG, "no usb connected");
        }else{
            //according to this https://stackoverflow.com/questions/14053764/how-to-identify-uniquely-a-usb-device
            //You can't uniquely identify USB devices, except by name
            Device myDev = new Device(device, this);
            Log.i(TAG, "usb device connected. See the constructor for more details");
        }
        if(mBTRemote == null){
            //mBTRemote = new BTRemote(this);
        }

        if(ttsEngine == null) {
            //create an Intent
            Intent checkData = new Intent();
            //set it up to check for tts data
            checkData.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            //start it so that it returns the result
            startActivityForResult(checkData, TTS_DATA_CHECKING);
        }

    }

    /**
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
                    ttsEngine = new CustomTTS(this, this);
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

    /**
     * This method is Overriden from the TextToSpeech.OnInitListener interface.
     * It is called when the ttsEngine is initialized.
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Locale.ENGLISH
            Locale myLoc = new Locale("en", "US");
            //Locale myLoc = Locale.US;
            Locale.setDefault(myLoc);
            //I'm having an error where the local isn't set.
            // this sets the local: https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
            //I don't think this is why the speech isn't working.
            ttsEngine.setLanguage(myLoc);
            ttsEngine.setPitch(1f);
            ttsEngine.setSpeechRate(1f);
            Log.i(TAG, "Created text to speech engine");
            ttsEngine.speak("Hello world");
        } else {
            Log.w(TAG, "Could not open TTS Engine (onInit status=" + status + ")");
            ttsEngine = null;
        }
    }

    @Override
    public void onStop(){
        ttsEngine.stop();
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        if(ttsEngine != null) {
            ttsEngine.shutdown();
        }
        //todo: maybe disconnect devices? maybe disable bluetooth? unregister usb detach receivers?
        Log.i(TAG, "MainActivity destroyed.");
        super.onDestroy();
    }
}
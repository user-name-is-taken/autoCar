package com.example.non_admin.picar;

import android.content.Context;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CustomGPIO{
    private Gpio mGpio;
    private static final String GPIO_NAME = "BCM4";
    private GpioCallback mGpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                if (gpio.getValue()) {
                    //Pin is HIGH
                    Log.i(TAG, "pin is high");
                    if(MainActivity.ttsEngine != null && MainActivity.ttsEngine.isAvailable() ){
                        MainActivity.ttsEngine.speak("pin is high");
                    }
                } else {
                    //pin is LOW
                    Log.i(TAG, "pin is low");
                    if(MainActivity.ttsEngine != null && MainActivity.ttsEngine.isAvailable()) {
                        MainActivity.ttsEngine.speak("pin is low");
                    }
                }
                return true;
            }catch (IOException e){
                Log.e(TAG, "gpio.getValue IOexception", e);
                return false;
            }
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w(TAG, gpio + ": Error event " + error);
        }

    };

    public CustomGPIO(){
        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            mGpio = manager.openGpio(GPIO_NAME);
            mGpio.setDirection(Gpio.DIRECTION_IN);
            mGpio.setActiveType(Gpio.ACTIVE_HIGH);//between BCM4 and Gnd
            mGpio.setEdgeTriggerType(Gpio.EDGE_RISING);

        } catch (IOException e) {
            Log.w(TAG, "Unable to access GPIO", e);
        }


    }

    public void start(){
        try {
            mGpio.registerGpioCallback(mGpioCallback);
        } catch (IOException e) {
            Log.w(TAG, "Can't link GPIO", e);
        }
    }

    public void stop(){
        mGpio.unregisterGpioCallback(this.mGpioCallback);
    }
    public void kill(){
        try {
            mGpio.close();
            mGpio = null;
        } catch (IOException e) {
            Log.w(TAG, "Unable to close GPIO", e);
        }

    }

    /**
     * A method that can list GPIO ports on a device. This can be helpful for debugging,
     * if you want to use this class ofr other devices.
     */
    public static void listGPIOports() {
        PeripheralManager manager = PeripheralManager.getInstance();
        List<String> portList = manager.getGpioList();
        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.");
        } else {
            Log.i(TAG, "List of available ports: " + portList);
        }
    }
}

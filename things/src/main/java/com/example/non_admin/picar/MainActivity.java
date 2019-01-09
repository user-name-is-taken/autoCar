package com.example.non_admin.picar;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

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
            mBTRemote = new BTRemote(this);
        }
    }
}
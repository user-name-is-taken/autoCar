package com.example.non_admin.picar

import android.app.Activity
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import java.io.IOException
import android.util.Log
import com.google.android.things.contrib.driver.pwmservo.Servo
import com.google.android.things.pio.PeripheralManager
import java.util.HashSet
import java.util.logging.Logger

private val TAG = MainActivity::class.java.simpleName
private val PWM_BUS = "BUS NAME"

class MainActivity : Activity() {
    //https://github.com/androidthings/sample-usbenum/blob/master/java/app/src/main/java/com/example/androidthings/usbenum/UsbActivity.java
/*
Here's the ultimate guide on this:
http://nilhcem.com/android-things/usb-communications
 */
    //This is how you specify statics in Kotlin
    companion object {
        val devices : HashSet<Device> = HashSet<Device>()
        //This set will contain all the devices.
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        if(device === null){
            Log.e(TAG, "no usb!")//change this to logging?
        }else{
            Log.d(TAG,"device found")
            if(MainActivity.devices.contains(UsbDevice: device)) {
                //TODO: fix this cocntains
                //TODO: add a way for a programmer to access devices by name, vendor ID, Product ID...
                    // not by device object

            }else{
                MainActivity.devices.add(Device(device));
            }
        }

        //wireless debugging:
            //https://stackoverflow.com/questions/4893953/run-install-debug-android-applications-over-wi-fi
        //println(PeripheralManager.getInstance().uartDeviceList.size)
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}


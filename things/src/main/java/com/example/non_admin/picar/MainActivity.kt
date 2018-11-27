package com.example.non_admin.picar

import android.app.Activity
import android.content.Context
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Device.setUsbManager(getSystemService(Context.USB_SERVICE) as UsbManager)
        //might want to move this somewhere where it only runs once.

        var device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        if(device === null){
            Log.e(TAG, "no usb device!")//change this to logging?
        }else{
            Log.d(TAG,"device found")
            if(!Device.devSet.contains(device)) {
                Device(device)//add to the static device in the Device constructor
            }
        }

        //wireless debugging:
            //https://stackoverflow.com/questions/4893953/run-install-debug-android-applications-over-wi-fi
        //println(PeripheralManager.getInstance().uartDeviceList.size)
    }


    override fun onDestroy() {
        Device.killAllDevs()
        super.onDestroy()
    }


}


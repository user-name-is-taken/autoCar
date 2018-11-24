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

        var device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        if(device === null){
            Log.e("hi", "no usb!")//change this to logging?
        }else{
            Log.d("hi","device found")
        }

        //wireless debugging:
            //https://stackoverflow.com/questions/4893953/run-install-debug-android-applications-over-wi-fi
        //println(PeripheralManager.getInstance().uartDeviceList.size)
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}


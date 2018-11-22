package com.example.non_admin.picar

import android.app.Activity
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import java.io.IOException
import android.util.Log
import com.google.android.things.contrib.driver.pwmservo.Servo
import java.util.logging.Logger

private val TAG = MainActivity::class.java.simpleName
private val PWM_BUS = "BUS NAME"

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        if(device === null){
            println("no usb!")//change this to logging?
        }else{
            println("device found")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}


package com.example.non_admin.picar

import android.app.Activity
import android.os.Bundle
import java.io.IOException
import android.util.Log
import com.google.android.things.contrib.driver.pwmservo.Servo

private val TAG = MainActivity::class.java.simpleName
private val PWM_BUS = "BUS NAME"

class MainActivity : Activity() {
    private lateinit var mServo: Servo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupServo()
        try {
            mServo.setAngle(30.0)
        } catch (e: IOException) {
            Log.e(TAG, "Error setting the angle", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyServo()
    }

    private fun setupServo() {
        try {
            mServo = Servo(PWM_BUS)
            mServo.setAngleRange(0.0, 180.0)
            mServo.setEnabled(true)
        } catch (e: IOException) {
            Log.e(TAG, "Error creating Servo", e)
        }

    }

    private fun destroyServo() {
        try {
            mServo.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing Servo")
        }
    }

}

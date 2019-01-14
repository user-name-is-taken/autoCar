package com.example.non_admin.picar;

import android.content.Context;
import android.util.Log;

import static android.content.ContentValues.TAG;


/**
 * Receives input from the [bluetooth remote](https://github.com/user-name-is-taken/android-bluetooth-joystick)
 *
 */
public class BTRemote extends MikeExecutor {
    MyBluetooth mMyBluetooth;
    MSv2Motors m1;
    MSv2Motors m2;
    MSv2Motors m3;
    MSv2 mMSv2;

    public BTRemote(Context context){
        mMyBluetooth = new MyBluetooth(context, this);//the provider for this executor.
    }


    /**
     * Takes the input from the bluetooth remote (MyBluetooth) and runs the related functions.
     * For example, move forward, backward...
     *
     * Currently, this just takes the data input from the provider, forwards it to the onPostExecute
     * and logs it.
     *
     * @param dataFromInput
     * @return
     */
    @Override
    protected String doInBackground(String... dataFromInput) {
        if(setupMotors()){
            Log.i(TAG, "Motors exist.");
        }
        Log.i(TAG, "Data received in BTRemote from its provider: " + dataFromInput);
        return dataFromInput[0];
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "The result of the BTRemote execution was: " + result);
    }


    /**
     * Sets up the 3 DC motors defined at the top.
     *
     * @return if true, the motors were setup successfully. Else, they weren't.
     */
    private boolean setupMotors(){
        try {
            if (mMSv2 == null || m1 == null || m2 == null || m3 == null) {
                Device mMSv2Dev = Device.devName.get("motors");
                if (mMSv2Dev != null) {
                    mMSv2 = (MSv2) mMSv2Dev.getAPI("MSv2");
                    if (mMSv2 != null) {
                        mMSv2.setShield(0);
                        Shield mShield = mMSv2.getShield(0);
                        if (mShield != null) {
                            m1 = mShield.setDCMotor(1);
                            m2 = mShield.setDCMotor(2);
                            m3 = mShield.setDCMotor(3);
                        } else {
                            Log.i(TAG, "Shield 0 (96) not attached");
                        }
                    } else {
                        Log.i(TAG, "MSv2 isn't a library on the motors ArduinoAPI for some reason.");
                    }
                } else {
                    Log.i(TAG, "motors hasn't been attached yet. Maybe you need to plug it in?" +
                            " Maybe it hasn't completed its boot process yet?");
                }
            }
            if (m1 != null && m2 != null && m3 != null) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }
}

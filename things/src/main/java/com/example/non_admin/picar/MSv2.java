package com.example.non_admin.picar;

import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * This class links MSv2Motors and MSv2Steppers using an array of Shields
 * so DC motors and stepper motors co-exist peacefully.
 */

public class MSv2 extends ArduinoAPI {
    private Shield[] allShields;

    private MSv2Steppers[][] stepperGroups;//see executeGroup for why this is here.

    /**
     * Constructor for this class. used in Device's getAPIfromName(String)
     *
     * @param dev this device is forwarded to the super constructor. It is used for sending,
     *            receiving...
     * @see this.dev.getAPIfromName(String)
     * @see ArduinoAPI(Device)
     */
    public MSv2 (Device dev){
        super(dev);
        allShields = new Shield[32];
        stepperGroups = new MSv2Steppers[16][10];
        this.startupRoutine();
    }


    @Override
    boolean receive(String message) {
        //log the output.
        //Log errors in an array?
        if(message.startsWith("MSv2")){
            Log.i(TAG, message + " was send from the arduino for the pi");
            return true;
        }
        return false;
    }

    /**
     * This doesn't work. Need to make send add to a buffer, then have arduinos
     * send 'ready' when they're ready to get another message.
     */
    void startupRoutine() {
        Log.i(TAG, "Inside MSv2 startupRoutine");
        this.setShield(0);
        MSv2Motors motor1 = this.getShield(0).setDCMotor(1);
        Log.i(TAG, "Inside MSv2 startupRoutine DCMotor DONE!");
        MSv2Steppers step2 = this.getShield(0).setStepperMotor(2);
        Log.i(TAG, "Inside MSv2 startupRoutine setStepperMotor DONE!");

        motor1.setSpeed(100);
        step2.setMoveAmount(100, 0);
        motor1.setDirection(true);
        this.executeGroup(0);
        Log.i(TAG, "Inside MSv2 startupRoutine DONE!");
    }

    /**
     * This sets up a shield at the specified index
     * These indexes correspond to the I2C addresses.
     *
     * @param shieldIndex this is the shield index (0-31), not the I2C address
     */
    public void setShield(int shieldIndex){
        if(getShield(shieldIndex) == null) {
            this.allShields[shieldIndex] = new Shield(this.dev, shieldIndex);
        }else{
            //error, shield already exists
            Log.w(TAG, "MSv2.setShield(" + shieldIndex + ") warning," +
                    " shield already exists");
        }
    }

    /**
     * Gets a shield at this index.
     *
     * @param shieldIndex the index in allShields to get a Shield from
     * @return the Shield at this given index
     */
    public Shield getShield(int shieldIndex){
        return this.allShields[shieldIndex];
    }

    /**
     * While groups are only relevant to stepper motors, those stepper motors can be
     * on different shields, so I'm adding this execute group parameter here because it can
     * act across all shields on an Arduino.
     * @param group
     */
    public void executeGroup(int group){
        for(MSv2Steppers stepperMotor : stepperGroups[group]){
            stepperMotor.executeGroup(group);
        }
        this.dev.send(String.format(MSv2Steppers.groupExeStr,
                "0" + Integer.toHexString(group)));
    }
}
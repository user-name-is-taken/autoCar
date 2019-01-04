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

    public MSv2 (Device dev){
        super(dev);
        allShields = new Shield[32];
        stepperGroups = new MSv2Steppers[16][10];
    }

    @Override
    boolean receive(String message) {
        //log the output.
        //Log errors in an array?
        return false;
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
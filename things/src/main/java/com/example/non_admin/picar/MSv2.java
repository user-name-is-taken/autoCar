package com.example.non_admin.picar;

/**
 * This class links MSv2Motors and MSv2Steppers using an array of Shields
 * so DC motors and stepper motors co-exist peacefully.
 */

public class MSv2 extends ArduinoAPI {
    private Shield[] allShields;

    public MSv2 (Device dev){
        super(dev);
        allShields = new Shield[32];
    }

    @Override
    boolean receive(String message) {
        //log the output.
        //Log errors in an array?
        return false;
    }
}
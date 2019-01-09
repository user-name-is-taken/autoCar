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
        this.addStepperToGroup(step2, 0);
        //TODO: add this stepper motor to this.MSv2Steppers somehow???
        Log.i(TAG, "Inside MSv2 startupRoutine setStepperMotor DONE!");

        step2.setMoveAmount(100, 0);
        motor1.setSpeed(100);
        motor1.setDirection(true);
        this.executeGroup(0);
        Log.i(TAG, "Inside MSv2 startupRoutine DONE!");
    }

    /**
     * This sets up a shield at the specified index
     * These indexes correspond to the I2C addresses.
     *
     * todo: handle error messages from the Arduino telling you that this shield isn't connected.
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
     * @return the Shield at this given index. null if it doesn't exist yet.
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
        String executeCommand = String.format(MSv2Steppers.groupExeStr,
                Integer.toHexString(group)).replace(' ','0');//leftPad
        Log.i(TAG, "Sending " + executeCommand + " to the device");
        Log.i(TAG, "Outside Execute group is: " + group);
        for(MSv2Steppers stepperMotor : stepperGroups[group]){
            if(stepperMotor != null) {
                Log.i(TAG, "Execute group is: " + group);
                stepperMotor.executeGroup(group);
            }else{
                //break;
            }
        }
        this.dev.send(executeCommand);
    }

    /**
     * checks if an MSv2Steppers object is already in group.
     *
     * @param stepper the stepper motor to look for
     * @param group the group to check in
     * @return true if it's in the group
     * @see MSv2Steppers#equals(MSv2Steppers)
     */
    public boolean stepperInGroup(MSv2Steppers stepper, int group){
        for(int i = 0; i < 10 && stepperGroups[group][i] != null; i++){
            if(stepper.equals(stepperGroups[group][i])){
                Log.i(TAG, "Stepper motor found at position " +
                        i + " in group " + group);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a stepper motor to a group so it can execute with the other motors.
     *
     * Currently this is necessary, but I might design this out?
     * @param stepper the stepper motor to add to the group
     */
    public void addStepperToGroup(MSv2Steppers stepper, int group){
        if(!stepperInGroup(stepper, group)) {
            int i = 0;
            for (; i < 10; i++) {
                if (stepperGroups[group][i] == null) {
                    stepperGroups[group][i] = stepper;
                    i = 20;
                    Log.i(TAG, "Stepper motor added.");
                    break;
                }
            }
            if (i != 20) {
                IndexOutOfBoundsException e = new IndexOutOfBoundsException(
                        "Can't add a stepper motor to group" +
                        group + " because the group is full.");
                Log.e(TAG, e.getMessage(), e);
                throw e;
            }
        }
    }
}
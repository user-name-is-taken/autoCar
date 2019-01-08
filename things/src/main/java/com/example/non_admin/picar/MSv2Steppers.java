package com.example.non_admin.picar;

import android.util.Log;

import java.security.InvalidParameterException;

import static android.content.ContentValues.TAG;


public class MSv2Steppers{

    private Device dev;
    private int position;// the actual position
    private int[] groupToMovePositions;//not yet moved positions.
    private Shield shield;
    private String toMoveString;
    private int stepperNumb;
    public static final String groupExeStr = "MSv2Steppers_execute_group_%2s";


    /**
     * constructor for an object that controls stepper motors
     * @param dev
     * @see <a href="https://learn.adafruit.com/adafruit-motor-shield-v2-for-arduino/using-stepper-motors">
     *     the adafruit motor shield v2.3 stepper motor docs</a>
     */
    public MSv2Steppers(Device dev, Shield shield, int stepperIndex){
        this.dev = dev;
        this.shield = shield;
        this.stepperNumb = stepperIndex + 1;
        this.groupToMovePositions = new int[16];
        // the stepper motor can be in any (or all) of the 16 groups.
        Log.d(TAG, "Inside MSv2Steppers, stepperNumb: " + this.stepperNumb);
        this.toMoveString = String.format("%s_%2s_move_%s_%s%s_group_%s", getClassName(),
                shield.getI2Caddr(), this.stepperNumb, "%s", "%3s", "%2s");
        //MSv2Steppers_60_move_1_+0FF_group_0F


    }

    public Shield getShield(){
        return this.shield;
    }

    public int getStepperNumb(){
        return this.stepperNumb;
    }


    public boolean equals(MSv2Steppers other){
        return other.shield.equals(this.getShield()) &&
                other.getStepperNumb() == stepperNumb;
    }

    /**
     * Records the execution action of a group for this motor by
     * adding this.position[group] to this.position.
     *
     * @param group the group who's movement is executed.
     */
    public void executeGroup(int group){
        this.position += this.groupToMovePositions[group];
        this.groupToMovePositions[group] = 0;
    }

    /**
     * gets the name of this class
     * @return
     */
    private String getClassName(){
        return this.getClass().getSimpleName();
    }

    /**
     * Sets how much this stepper motor will move when this group is executed:
     *  1. Recording how much it will move by adding ticks to this.groupToMovePositions[group]
     *  2. Sending this information to the arduino in a string. for example:
     *      "MSv2Steppers_60_move_1_+0FF_group_0F"
     *          will tell shield 0x60's motor 1 to move 0FF (255) additional ticks when group 0F (15) is executed
     * @param ticks the amount the motor will move
     * @param group the group. Note, this has to be between 0 and 15
     */
    public void setMoveAmount(int ticks, int group){
        String tickHex;
        String sign;
        if(ticks > 4095 || ticks < -4095){
            //invalid number of ticks
            InvalidParameterException e = new InvalidParameterException("MSv2Steppers, the number of ticks "
                    + ticks + " is outside the valid range of -4095 to 4096 (-FFF to FFF)");
            Log.e(TAG, e.getStackTrace() + e.getMessage());
            throw e;
        }
        if(ticks < 0){
            ticks *= -1;
            sign = "-";
            tickHex = Integer.toHexString(ticks);
        }else{
            sign = "+";
            tickHex = Integer.toHexString(ticks);
        }
        if(group >= 16 || group < 0){
            //invalid group
            //throw error
            IndexOutOfBoundsException e = new IndexOutOfBoundsException(
                    "MSv2Steppers, " + group + "is an invalid group number");
            Log.e(TAG, e.getStackTrace() + e.getMessage());
            throw e;
        }
        String toSend = String.format(this.toMoveString, sign, tickHex, group,
                Integer.toHexString(group)).replace(" ", "0"); //.replace is leftPad

        this.dev.send(toSend);
    }


}
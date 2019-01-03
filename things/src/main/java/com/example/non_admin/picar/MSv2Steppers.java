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
    public static final String groupExeStr = "MSv2Steppers_execute_group_%s";


    /**
     * constructor
     * @param dev
     */
    public MSv2Steppers(Device dev, Shield shield, int stepperIndex){
        this.dev = dev;
        this.shield = shield;
        this.stepperNumb = stepperIndex + 1;
        this.groupToMovePositions = new int[16];
        // the stepper motor can be in any (or all) of the 16 groups.
        this.toMoveString = String.format("%s_%s_move_%s_%s_group_%s", getClassName(),
                shield.getI2Caddr(), this.stepperNumb, "%s", "%s");


    }

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
     * example: MSv2Steppers_60_move_1_+0FF_group_00
     * @param ticks
     * @param group
     */
    public void setMoveAmount(int ticks, int group){
        String tickHex;
        if(ticks > 4095 || ticks < -4095){
            //invalid number of ticks
            String msg = "MSv2Steppers, the number of ticks " + ticks +
                    " is outside the valid range of -4095 to 4096 (-FFF to FFF)";
            Log.e(TAG, msg);
            throw new InvalidParameterException(msg);
        }
        if(ticks < 0){
            ticks *= -1;
            tickHex = "-" + Integer.toHexString(ticks);
        }else{
            tickHex = "+" + Integer.toHexString(ticks);
        }
        if(group >= 16 || group < 0){
            //invalid group
            //throw error
            String msg = "MSv2Steppers, " + group + "is an invalid group number";
            Log.e(TAG, msg);
            throw new InvalidParameterException(msg);
        }
        String toSend = String.format(this.toMoveString, tickHex, Integer.toHexString(group));

        this.dev.send(toSend);
    }


}
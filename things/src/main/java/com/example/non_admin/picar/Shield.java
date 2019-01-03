package com.example.non_admin.picar;

import android.util.Log;

import java.security.InvalidParameterException;

import static android.content.ContentValues.TAG;

public class Shield {
    private MSv2Motors[] dcMotors = {null, null, null, null};
    private MSv2Steppers[] stepperMotors = {null, null};
    private Device dev;
    private final int index;

    public Shield(Device dev, int index){
        this.dev = dev;
        this.index = index;
    }

    /**
     * converts the shield's index to its I2C address by
     * converting the address adding 96 to it, then converting it to hex.
     *
     * @return a String representing the I2C address of this string.
     */
    public String getI2Caddr(){
        return Integer.toHexString(this.index + 96);
    }

    public MSv2Motors getDCMotor(int motorNumb){
        motorNumb --; // decrementing motorNumb for indexing.
        if(motorNumb < 0 || motorNumb > 3 || dcMotors[motorNumb] == null){
            Log.e(TAG, "Shield " + getI2Caddr() + " has no motor at the index "
                    + motorNumb);
            throw new InvalidParameterException("Shield " + getI2Caddr()
                    + " has no motor at the index " + motorNumb);
        }else{
            return this.dcMotors[motorNumb];
        }
    }

    /**
     *
     * @param motorNumb
     * @param force this boolean will force the motor shield to remove and overwrite a stepper motor
     *              on the same pins if it exists.
     * @return the MSv2Motors at the position in the position
     */
    public MSv2Motors setDCMotor (int motorNumb, boolean force){
        motorNumb --; // decrementing motorNumb for indexing purposes.
        if(motorNumb < 0 || motorNumb > 3){
            Log.e(TAG, "Shield " + getI2Caddr() + " has no motor at the index "
                    + motorNumb);
            throw new InvalidParameterException("Shield " + getI2Caddr()
                    + " has no motor at the index " + motorNumb);
        }else if(dcMotors[motorNumb] != null){
            Log.w(TAG, "Shield " + getI2Caddr() + " already has a motor at the index "
                    + motorNumb);
            //not allowing a force reset here, because that should be a function of the MSv2Motors itself
        }else if(!force && stepperMotors[(motorNumb)/2] != null){
            Log.e(TAG, "Shield " + getI2Caddr() + " already has a stepper motor at position" +
                    motorNumb/2 + " preventing you from having a MSv2Motors at position" + motorNumb);
            throw new InvalidParameterException("Shield " + getI2Caddr() +
                    " already has a stepper motor at position" + motorNumb/2 +
                    " preventing you from having a MSv2Motors at position" + motorNumb);
        }else if(force){
            stepperMotors[motorNumb/2] = null;
        }else{
            dcMotors[motorNumb] = new MSv2Motors(this.dev, this.index, motorNumb);
            //TODO: make these parameters right
            //MSv2Motors(Device dev, int shieldIndex, int motorIndex)
        }
        return dcMotors[motorNumb];
    }

    /**
     * TODO: write this function
     * @param stepNumb
     * @param force
     * @return
     */
    public MSv2Steppers setStepperMotor(int stepNumb, boolean force){
        stepNumb --;

    }

    /**
     * TODO: write this function
     * @param stepNumb
     * @return
     */
    public MSv2Steppers getStepperMotor(int stepNumb){
        return this.stepperMotors[stepNumb - 1];
    }

    /**
     *
     * @param motorNumb tells which motor to get.
     * @return The motor at that index
     * @see this.setDCMotor(int, boolean)
     */
    public MSv2Motors setDCMotor(int motorNumb){
        return setDCMotor(motorNumb, false);
    }


}
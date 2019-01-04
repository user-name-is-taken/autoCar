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
        if(motorNumb < 0 || motorNumb > 3 || dcMotors[motorNumb] == null) {
            InvalidParameterException e = new InvalidParameterException("Shield " + getI2Caddr()
                    + " has no motor at the index " + motorNumb);
            Log.e(TAG, e.getStackTrace() + " " + e.getMessage(), e);
            throw e;
        }else if(dcMotors[motorNumb] == null){
            Log.w(TAG, "There isn't a DC motor on shield " + getI2Caddr() +
            " at position " + (motorNumb + 1) + " in Shields.getDCMotor.");

        }
        return this.dcMotors[motorNumb];
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
            IndexOutOfBoundsException e = new IndexOutOfBoundsException("Invalid motorNumb, " +
                    (motorNumb + 1) + ", passed to shield.setDCMotor on shield " + getI2Caddr());
            Log.e(TAG, e.getStackTrace() + " " + e.getMessage(), e);
            throw e;
        }else if(dcMotors[motorNumb] != null){
            Log.w(TAG, "Shield " + getI2Caddr() + " already has a motor at the index "
                    + motorNumb);
            //not allowing a force reset here, because that should be a function of the MSv2Motors itself
        }else if(!force && stepperMotors[(motorNumb)/2] != null){
            InvalidParameterException e = new InvalidParameterException("Shield " + getI2Caddr() +
                    " already has a stepper motor at position" + motorNumb/2 +
                    " preventing you from having a MSv2Motors at position" + motorNumb);
            Log.e(TAG, e.getStackTrace() + " " + e.getMessage(), e);
            throw e;
        }else{
            if(force) {
                stepperMotors[motorNumb / 2] = null;
            }
            dcMotors[motorNumb] = new MSv2Motors(this.dev, this.index, motorNumb);
            //MSv2Motors(Device dev, int shieldIndex, int motorIndex)
        }
        return dcMotors[motorNumb];
    }

    /**
     * setDCMotor where force is false
     * @param motorNumb tells which motor to get.
     * @return The motor at that index
     * @see this.setDCMotor(int, boolean) This is just setDCMotor(int, false).
     * It's just a pass though functon
     */
    public MSv2Motors setDCMotor(int motorNumb){
        return setDCMotor(motorNumb, false);
    }

    /**
     * Creates a stepper motor and adds it to this.stepperMotors
     * @param stepNumb
     * @param force
     * @return
     */
    public MSv2Steppers setStepperMotor(int stepNumb, boolean force){
        stepNumb --;
        if(stepNumb < 0 || stepNumb > 1){
            IndexOutOfBoundsException e = new IndexOutOfBoundsException("Shield " + getI2Caddr()
                    + " has no stepper motor at the index " + stepNumb);
            Log.e(TAG, e.getStackTrace() + " " + e.getMessage(), e);
            throw e;
        }else if(this.stepperMotors[stepNumb] != null){
            Log.w(TAG, "Shield " + getI2Caddr() + " already has a stepper motor at the index "
                    + stepNumb);
            //not allowing a force reset here, because that should be a function of the MSv2Motors itself
        }else if(!force && stepperMotors[(stepNumb)*2] != null &&
                stepperMotors[((stepNumb)*2) + 1 ] != null){
            InvalidParameterException e = new InvalidParameterException("Shield " + getI2Caddr() +
                    "  has DC motor at position" + stepNumb * 2 + " or " + ((stepNumb * 2) + 1) +
                    " preventing you from having a stepper at position" + stepNumb);
            Log.e(TAG, e.getStackTrace() + " " + e.getMessage(), e);
            throw e;
        }else{
            if(force){
                dcMotors[stepNumb * 2] = null;
                dcMotors[(stepNumb * 2) + 1] = null;
            }
            stepperMotors[stepNumb] = new MSv2Steppers(this.dev, this, stepNumb);
            //MSv2Motors(Device dev, int shieldIndex, int motorIndex)
        }
        return stepperMotors[stepNumb];
    }

    /**
     * just a pass through function
     * @param stepNumb
     * @return
     * @see this.setStepperMotor(int, boolean)
     */
    public MSv2Steppers setStepperMotor(int stepNumb){
        return setStepperMotor(stepNumb, false);
    }

    /**
     * This function gets a MSv2Stepper object that can be used to manipulate stepper motors
     * on this board.
     * @param stepNumb an int (1 or 2) corresponding to the motor
     * @return an MSv2Stepper motor
     * @see MSv2Steppers(Device, Shield, int) for how stepper motors are constructed.
     */
    public MSv2Steppers getStepperMotor(int stepNumb){
        stepNumb --;
        if(stepNumb < 0 || stepNumb > 1) {
            InvalidParameterException e = new InvalidParameterException(
                    "Invalid stepNumb, " + (1 + stepNumb) +
                            ", passed to shield.getStepperMotor on shield" + getI2Caddr());
            Log.e(TAG, e.getStackTrace() + " " + e.getMessage(), e);
            throw e;
        }else if(stepperMotors[stepNumb] == null){
            Log.w(TAG, "There isn't a stepper motor at position " + (stepNumb + 1) +
            " on shield " + getI2Caddr() + " in Shield.getStepperMotor(int).");
        }
        return this.stepperMotors[stepNumb];
    }




}
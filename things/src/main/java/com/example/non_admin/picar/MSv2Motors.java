package com.example.non_admin.picar;

import android.util.Log;


import java.security.InvalidParameterException;

import static android.content.ContentValues.TAG;

/**
 * currently this class only does methods for motors,
 * but in the future it will do methods for all Motor shield hardware namely:
 *  pwm, servo, motors, and digital IO
 * @author pi
 *
 * MOTORS ARE 1-4, ARRAYS ARE 0-3 ADJUST
 *
 * This is just 1 motor, but I'd have to rename it in c++ and that's harder than keeping
 * this name
 */
public class MSv2Motors {
	Device dev;
	private int speed;
	private Boolean direction;
	private final String directionMessageBase;
	private final String speedMessageBase;
	private final int motorNumb;
	private final String shieldNumb;
	private final String name;


	public MSv2Motors(Device dev, int shieldIndex, int motorIndex) {
		this.dev = dev;
		this.speed = 0;
		this.direction = false;
		this.motorNumb = (motorIndex + 1);
		this.shieldNumb = Integer.toHexString(shieldIndex + 96);
		this.name = this.getClass().getSimpleName();
		this.directionMessageBase = String.format("%s_%s_direction_%s_",
                this.name, this.shieldNumb, this.motorNumb);
        //^MSv2Motors_[67]%x_direction_[1-4]_[0-2]$
		this.speedMessageBase = String.format("%s_%s_speed_%s_",
                this.name, this.shieldNumb, this.motorNumb);
        //format = ^MSv2Motors_[67]%x_speed_[1-4]_%x%x$

	}

    public String getName(){
        return this.name;
    }


	/**
	 * TODO: delete this method?
	 *
	 * just a standard receive. Checks if message was meant for this API,
	 * if so it does some stuff.
	 * @param message The message received by the Device.
	 * @return
	 */
	protected boolean receive(String message) {
		Log.d(TAG,message);
		if(message.startsWith("MSv2Motors")){
			/*
			if(	this.attachedShieldsReceive(message) ){
				Log.v(TAG, "shields set");
			}
			*/
			//check for error messages here.
			return true;
		}else{
			return false;
		}
		//throw RejectedExecutionException if error returned?
	}

	
	/**
	 * sets the direction of the motor (forward or backward)
	 * @param direction true is forward false is backward, null is stopped
	 */
	public void setDirection(Boolean direction){
		//validate
		//format = ^MSv2Motors_[67]%x_direction_[1-4]_[0-2]$
		this.direction = direction;
        String messageDirection = "";
        if(direction == null){
            messageDirection = "0";//RELEASE
        }else if(direction){
            messageDirection = "1";//FORWARD
        }else{
            messageDirection = "2";//BACKWARD
        }
        this.dev.send(this.directionMessageBase + messageDirection);
	}
	


	/**
	 * @return the direction this motor is moving in
	 */
	public Boolean getDirection(){
		return this.direction;
	}

	/**
	 * sets the speed of the motor
	 * @param speed
	 */
	public void setSpeed(int speed){
		//format = ^MSv2Motors_[67]%x_speed_[1-4]_%x%x$
        if(speed < 0 || speed > 255){
            Log.e(TAG, "MSv2Motors: invalid speed," + speed +
                    " passed to setSpeed");
            throw new InvalidParameterException("MSv2Motors: invalid speed," + speed +
                    " passed to setSpeed.");
        }else{
            this.speed = speed;
            this.dev.send(this.speedMessageBase +
					"%2s".format(Integer.toHexString(speed)).replace(' ', '0') );//leftPad
        }
	}

	
	/**
	 * gets the speed for this motor
	 * @return The speed of the motor
	 */
	public int getSpeed(){
		return this.speed;
	}
}

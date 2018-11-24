package com.example.non_admin.picar;

import android.util.Log;

/**
 * currently this class only does methods for motors,
 * but in the future it will do methods for all Motor shield hardware namely:
 *  pwm, servo, motors, and digital IO
 * @author pi
 *
 * MOTORS ARE 1-4, ARRAYS ARE 0-3 ADJUST
 */
public class MotorShield extends ArduinoAPI {
	
	public MotorShield(Device dev) {
		super("MSv2", dev);
		// TODO Auto-generated constructor stub
	}

	boolean[] direction = {true, true, true, true};
	int[] speed = {0,0,0,0};



	@Override
	protected boolean receive(String message) {
		// TODO Auto-generated method stub
		Log.e("MSv2:",message);
		return false;
	}
	
	/**
	 * sets the direction of the motor (forward or backward)
	 * @param direction true is forward false is backward
	 */
	public void setDirection(boolean direction, int motor){
		//validate
		//format = ^MSv2_[67]%x_direction_[1-4]_[0-2]$
		//convert those ints to bytes before sending
		//name is the board (default 60)
		this.direction[motor-1] = direction;
		this.dev.send(motor + (direction?"1":"0"));//add the motor to this
	}
	
	
	/**
	 * Returns the direction this motor is going
	 * @param motor the motor you want to know the direction of
	 * @return the direction of the this motor
	 */
	public boolean getDirection(int motor){
		return this.direction[motor-1];		
	}
	
	/**
	 * sets the speed of the motor
	 * @param speed
	 */
	public void setSpeed(int speed,int motor){
		//format = ^MSv2_[67]%x_speed_[1-4]_%x%x$
		this.speed[motor-1] = speed;
		this.dev.send(""+speed);
	}
	
	/**
	 * gets the speed for this motor from the class
	 * @param Motor Which motor you want to set the speed for
	 * @return The speed of the motor
	 */
	public int getSpeed(int Motor){
		return this.speed[Motor-1];
	}
}

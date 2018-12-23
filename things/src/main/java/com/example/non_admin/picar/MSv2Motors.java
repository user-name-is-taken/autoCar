package com.example.non_admin.picar;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * currently this class only does methods for motors,
 * but in the future it will do methods for all Motor shield hardware namely:
 *  pwm, servo, motors, and digital IO
 * @author pi
 *
 * MOTORS ARE 1-4, ARRAYS ARE 0-3 ADJUST
 */
public class MSv2Motors extends ArduinoAPI {
	static HashMap<String, Shield> allShields;

	public MSv2Motors(Device dev) {
		super(dev);
		// TODO Auto-generated constructor stub
		// TODO: validate I2C addressing (parse from handshake? request?)
		this.dev.send("MSv2_shields");
		new AsyncExample().execute(this);
	}


	/**
	 * just a standard receive. Checks if message was meant for this API,
	 * if so it does some stuff.
	 * @param message The message received by the Device.
	 * @return
	 */
	@Override
	protected boolean receive(String message) {
		// TODO Auto-generated method stub
		Log.d(TAG,message);
		if(message.startsWith("MSv2Motors")){
			if(	this.attachedShieldsReceive(message) ){
				Log.v(TAG, "shields set");
			}
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Change this? It already returns an error if the shield isn't connected.
	 * @param message
	 * @return
	 */
	private boolean attachedShieldsReceive(String message){
		Pattern pattern = Pattern.compile("^MSv2Motors_shields(_[67]%x)*$");
		Matcher m  = pattern.matcher(message);
		if (m.matches()){
			//parse out the valid shield addresses
			String[] arr = message.split("_");
			for (int i = 2; i < arr.length; i++) {
				new Shield(arr[i]);
			}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * sets the direction of the motor (forward or backward)
	 * @param direction true is forward false is backward, null is stopped
	 */
	public void setDirection(Boolean direction, int motor, String shield) throws InvalidShield{
		//validate
		//format = ^MSv2_[67]%x_direction_[1-4]_[0-2]$
		//convert those ints to bytes before sending
		//name is the board (default 60)
		if(allShields.containsKey(shield)){
			allShields.get(shield).setDirection(motor-1, direction);
			String messageDirection = "";
			if(direction == null){
				messageDirection = "0";//RELEASE
			}else if(direction){
				messageDirection = "1";//FORWARD
			}else{
				messageDirection = "2";//BACKWARD
			}
			this.dev.send(motor + "_" + messageDirection);//add the motor to this
		}else{
			//shield doesn't exist, throw an error
		}
	}

	/**
	 * Just the default for setDirection, where shield is 60
	 * @param direction
	 * @param motor
	 */
	public void setDirection(Boolean direction, int motor) throws InvalidShield{
		setDirection(direction, motor, "60");
	}
	
	
	/**
	 * Returns the direction this motor is going
	 * @param motor the motor you want to know the direction of
	 * @return the direction of the this motor
	 */
	public boolean getDirection(int motor, String shield) throws InvalidShield{
		return this.allShields.get(shield).getDirection(motor-1);
	}

	/**
	 * just the default method for getDirection (where shield's value is 60)
	 * @param motor
	 * @return
	 */
	public boolean getDirection(int motor) throws InvalidShield{
		getDirection(motor, "60");
	}

	/**
	 * sets the speed of the motor
	 * @param speed
	 */
	public void setSpeed(int speed,int motor, String shield) throws InvalidShield{
		//format = ^MSv2_[67]%x_speed_[1-4]_%x%x$
		if(this.allShields.containsKey(shield)){
			this.allShields.get(shield).setSpeed(motor-1, speed);
			String hexSpeed = Integer.toHexString(speed);
			this.dev.send(getAPIname() + "_" + shield + "speed" + "_" +
					motor + "_" + hexSpeed);

		}else{
			//throw error, shield not here
		}
	}

	/**
	 * Just set speed with the default shield ("60")
	 * @param speed
	 * @param motor
	 */
	public void setSpeed(int speed,int motor) throws InvalidShield{
		setSpeed(speed, motor, "60");
	}
	
	/**
	 * gets the speed for this motor from the class
	 * @param motor Which motor you want to set the speed for
	 * @return The speed of the motor
	 */
	public int getSpeed(int motor, String shield) throws InvalidShield{
		return this.allShields.get(shield).getSpeed(motor);
	}

	/**
	 * Just getSpeed with the default shield, "60"
	 * @param motor
	 * @return
	 */
	public int getSpeed(int motor) throws InvalidShield{
		return getSpeed(motor, "60");
	}
}

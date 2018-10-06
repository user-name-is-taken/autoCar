/**
 * Motor classes have a member array of motors
 * @author pi
 *
 */

public interface MotorInterface{
	/**
	 * sets the direction of the motor (forward or backward)
	 * @param direction true is forward false is backward
	 */
	void setDirection(boolean direction, int Motor);
	
	/**
	 * Returns the direction this motor is turning
	 * 
	 * @param Motor Which motor you want to know the direction of.
	 * @return The direction the motor is going t == forward f == backward
	 */
	boolean getDirection(int Motor);
	
	/**
	 * sets the speed of the motor
	 * @param speed
	 */
	void setSpeed(int speed, int Motor);
	
	/**
	 * gets the speed for this motor from the class
	 * @param Motor Which motor you want to set the speed for
	 * @return The speed of the motor
	 */
	int getSpeed(int Motor);
	
	// add a stop all motors?
	
}

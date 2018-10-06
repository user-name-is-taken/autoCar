
public interface ServoInterface {
	//have an array of servos
	//have an array of angles
	
	/**
	 * Sets the position of this servo
	 * @param angle the angle the servo should go to
	 * @param servo the servo you want to set
	 */
	void setServo(float angle, int servo);
	
	/**
	 * Tells the stored state of this servo
	 * 
	 * @param servo the servo you want to know about
	 * @return the angle of this servo
	 */
	float getServo(int servo);

}

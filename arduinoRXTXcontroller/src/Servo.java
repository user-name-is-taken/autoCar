
public class Servo extends ArduinoAPI {
	//have an array of servos
	//have an array of angles
	
	public Servo(String name, Device dev) {
		super(name, dev);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the position of this servo
	 * @param angle the angle the servo should go to
	 * @param servo the servo you want to set
	 */
	void setServo(float angle, int servo){
		//set the angle...
	}
	
	/**
	 * Tells the stored state of this servo
	 * 
	 * @param servo the servo you want to know about
	 * @return the angle of this servo
	 */
	double getServo(int servo){
		return 0.0;
	}
	
	boolean receive(String message){
		return false;
	}

}

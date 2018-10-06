import javax.swing.JOptionPane;
/**
 * currently this class only does methods for motors,
 * but in the future it will do methods for all Motor shield hardware namely:
 *  pwm, servo, motors, and digital IO
 * @author pi
 *
 * MOTORS ARE 1-4, ARRAYS ARE 0-3 ADJUST
 */
public class MotorShield extends Device implements MotorInterface {
	
	boolean[] direction = {true, true, true, true};
	int[] speed = {0,0,0,0};

	public MotorShield(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void receive(String message) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, message);
	}
	
	/**
	 * sets the direction of the motor (forward or backward)
	 * @param direction true is forward false is backward
	 */
	public void setDirection(boolean direction, int motor){
		//validate
		this.direction[motor-1] = direction;
		this.send(motor + (direction?"1":"0"));//add the motor to this
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
		//validate
		this.speed[motor-1] = speed;
		this.send(""+speed);
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

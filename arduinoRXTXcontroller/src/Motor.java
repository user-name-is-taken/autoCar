import javax.swing.JOptionPane;

public class Motor extends Device {

	public Motor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	void receive(String message) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, message);
	}
	
	/**
	 * sets the direction of the motor (forward or backward)
	 * @param direction true is forward false is backward
	 */
	void setDirection(boolean direction){
		//validate
		this.send(direction?"1":"0");
	}
	
	/**
	 * sets the speed of the motor
	 * @param speed
	 */
	void setSpeed(int speed){
		//validate
		this.send(""+speed);
	}

}

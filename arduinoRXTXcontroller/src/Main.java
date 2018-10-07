import javax.swing.JOptionPane;

public class Main {

	/**
	 * This is the main function, it runs the program.
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DeviceSetup.listPorts();
		String port = JOptionPane.showInputDialog("enter port name");
		Device dev = new Device(port);
		MotorShield sheild = new MotorShield("Motor 1", dev);
		sheild.setDirection(true, 1);
		sheild.setSpeed(1,1);
		dev.killConnection();

	}

}

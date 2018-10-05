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
		Motor rightMotor = new Motor(port);
		rightMotor.setDirection(true);
		rightMotor.setSpeed(1);
		rightMotor.killConnection();

	}

}

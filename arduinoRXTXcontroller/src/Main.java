import javax.swing.JOptionPane;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DeviceSetup.listPorts();
		String port = JOptionPane.showInputDialog("enter port name");
		Motor rightMotor = new Motor(port);
		rightMotor.setDirection(true);
		rightMotor.setSpeed(1);

	}

}

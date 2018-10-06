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
		MotorShield sheild = new MotorShield(port);
		sheild.setDirection(true);
		sheild.setSpeed(1,1);
		sheild.killConnection();

	}

}

import javax.swing.JOptionPane;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DeviceSetup.listPorts();
		String port = JOptionPane.showInputDialog("enter port name");
		//DeviceSetup.connect(port);

	}

}

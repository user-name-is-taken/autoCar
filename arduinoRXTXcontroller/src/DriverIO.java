
public class DriverIO extends ArduinoAPI{

	public DriverIO(String name, Device dev) {
		super(name, dev);
		// TODO Auto-generated constructor stub
	}

	/**
	 * sets the state of the pin
	 * @param state
	 * @param pin
	 */
	void setPinState(boolean state, int pin){
		
	}
	
	boolean getPinState(int pin){
		return false;
	}
	
	/**
	 * see the ArduinoAPI abstract class
	 */
	public boolean receive(String message){
		return false;
	}
	
}

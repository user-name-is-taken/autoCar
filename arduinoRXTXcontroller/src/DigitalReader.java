
public class DigitalReader extends ArduinoAPI {
	
	public DigitalReader(String name, Device dev) {
		super(name, dev);
		// TODO Auto-generated constructor stub
	}

	/**
	 * reads from a digital pin
	 * @param pin
	 * @return
	 */
	boolean getPinLevel(int pin){
		return false;
	}

	@Override
	boolean receive(String message) {
		// TODO Auto-generated method stub
		return false;
	}

}

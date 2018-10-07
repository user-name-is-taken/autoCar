
public class Relay extends ArduinoAPI{
	
	public Relay(String name, Device dev) {
		super(name, dev);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the power of the relay
	 * 
	 * @param power if the power is on or off
	 * @param relay which relay to set
	 */
	void setPower(boolean power, int relay){
		
	}
	
	boolean getPower(int relay){
		return false;
	}

	@Override
	boolean receive(String message) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}

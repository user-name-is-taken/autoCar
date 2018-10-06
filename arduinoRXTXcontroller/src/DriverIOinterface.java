
public interface DriverIOinterface {

	/**
	 * sets the state of the pin
	 * @param state
	 * @param pin
	 */
	void setPinState(boolean state, int pin);
	
	boolean getPinState(int pin);
	
}

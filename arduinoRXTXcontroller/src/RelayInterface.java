
public interface RelayInterface {
	
	/**
	 * Sets the power of the relay
	 * 
	 * @param power if the power is on or off
	 * @param relay which relay to set
	 */
	void setPower(boolean power, int relay);
	
	boolean getPower(int relay);
	
	

}


public interface PWMinterface {
	//pwms %1 to 0 
	
	void setPctOn(float onPct, int pin);
	
	/**
	 * 
	 * @param frequency
	 * @param pin
	 */
	void setPWMFrequency(int frequency, int pin);
	

}

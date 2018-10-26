
public class PWM extends ArduinoAPI {
	//pwms %1 to 0 
	
	public PWM(String name, Device dev) {
		super(name, dev);
		// TODO Auto-generated constructor stub
	}

	void setPctOn(float onPct, int pin){
		
	}
	
	/**
	 * 
	 * @param frequency
	 * @param pin
	 */
	void setPWMFrequency(int frequency, int pin){
		
	}

	@Override
	boolean receive(String message) {
		// TODO Auto-generated method stub
		return false;
	}
	

}

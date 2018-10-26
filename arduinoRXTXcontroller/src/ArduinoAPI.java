
public abstract class ArduinoAPI {
	String name;
	Device dev;
	
	/**
	 * Creates the API object and adds it to the device.
	 * 
	 * @param name The name of the API, this is used as the PK in Device.
	 * @param dev The Device that this API is connected to. This is the Device the API sends over.
	 */
	public ArduinoAPI(String name, Device dev) {
		// TODO Auto-generated constructor stub
		super();
		dev.addAPI(name, this);
		this.name = name;
		this.dev = dev;
	}
	
	/**
	 * The method the Device calls when it receives a message.
	 * 
	 * @param message The message received by the Device.
	 * @return This will return true if the message was meant for and processed by this API.
	 */
	abstract boolean receive(String message);

}

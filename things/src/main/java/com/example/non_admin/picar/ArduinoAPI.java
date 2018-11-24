package com.example.non_admin.picar;

import android.content.res.Resources;

public abstract class ArduinoAPI {
	private String name;
	Device dev;
	
	/**
	 * Creates the API object and adds it to the device.
	 * 
	 * @param APIname The name of the API, this is used as the PK in Device.
	 * @param dev The Device that this API is connected to. This is the Device the API sends over.
	 */
	public ArduinoAPI(String APIname, Device dev) {
		// TODO Auto-generated constructor stub
		super();
		setAPIname(APIname);
		dev.addAPI(APIname, this);
		this.dev = dev;
	}

	public String getAPIname(){
		return this.name;
	}

	private void setAPIname(String name){
		this.name = name;
	}

	
	/**
	 * The method the Device calls when it receives a message.
	 * 
	 * @param message The message received by the Device.
	 * @return This will return true if the message was meant for and processed by this API.
	 */
	abstract boolean receive(String message);

}

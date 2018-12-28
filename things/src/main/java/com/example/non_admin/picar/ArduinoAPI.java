package com.example.non_admin.picar;

import android.content.res.Resources;

public abstract class ArduinoAPI {
	private String name;
	Device dev;
	
	/**
	 * Creates the API object and adds it to the device.
	 *
	 * @param dev The Device that this API is connected to. This is the Device the API sends over.
	 */
	public ArduinoAPI(Device dev) {
		// TODO Auto-generated constructor stub
		super();
		setAPIname();
		dev.addAPI(this.getAPIname(), this);
		this.dev = dev;
	}

	public String getAPIname(){
		return this.name;
	}

	private void setAPIname(){
		this.name = this.getClass().getSimpleName();
	}

	
	/**
	 * The method the Device calls when it receives a message.
	 * 
	 * @param message The message received by the Device.
	 * @return This will return true if the message was meant for and processed by this API.
	 */
	abstract boolean receive(String message);

}

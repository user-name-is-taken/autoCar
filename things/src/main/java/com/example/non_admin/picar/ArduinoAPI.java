package com.example.non_admin.picar;

import android.content.res.Resources;

public abstract class ArduinoAPI {
	protected String name;
	Device dev;
	
	/**
	 * Creates the API object and adds it to the device.
	 *
	 * @param dev The Device that this API is connected to. This is the Device the API sends over.
	 */
	public ArduinoAPI(Device dev) {
		super();
		setAPIname();
		dev.addAPI(this.getAPIname(), this);
		this.dev = dev;
		//startupRoutine();
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
	//maybe when you get "ready", you should set a flag?

	/**
	 * Just a startup routine ArduinoAPIs must implement so you know they're working.
	 * You could leave this blank, but that would be dumb
	 *
	 * I took this out because I can just make a startup routine in the API's constructor
	 */
	//abstract void startupRoutine();



}

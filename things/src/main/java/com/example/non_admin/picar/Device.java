package com.example.non_admin.picar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
//https://github.com/Nilhcem/usbfun-androidthings/blob/master/mobile/src/main/java/com/nilhcem/usbfun/mobile/MainActivity.java

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static android.support.v4.content.ContextCompat.getSystemService;
//import UsbSerial;

/**
 * This is just a fancy interface to the a connected UsbDevice.
 * - It:
 *   - does handshaking so everyone knows who they are.
 *   - enables writing
 *   - has a callback on receiving that forwards messages to the appropriate interfaces.
 *   - TODO: add a way for a sensor device to forward a command to an actuator device through the pi
 *     - multicast groups?
 *
 * see the following 3 links:
 *   - https://github.com/Nilhcem/usbfun-androidthings/blob/master/mobile/src/main/java/com/nilhcem/usbfun/mobile/MainActivity.java)
 *   - http://nilhcem.com/android-things/usb-communications
 *   - https://github.com/felHR85/UsbSerial
 *
 * Devices must be serial devices (not parallel) TODO: double check this
 *
 * @author pi
 *
 */
public class Device {
	private static UsbManager usbManager;
	public static HashSet<Device> devSet = new HashSet<>();
	public static HashMap<String, Device> devName = new HashMap<>();
	private static final String TAG = MainActivity.class.getSimpleName();

	private String name;
	private HashMap<String, ArduinoAPI> APIs;
	// make a singleton device manager
	private UsbDevice mDevice;
	private UsbDeviceConnection connection;
	private UsbSerialDevice serialDevice;
	private String buffer = ""; // delete this?

	//These store the devices


	private UsbSerialInterface.UsbReadCallback callback;

	private final BroadcastReceiver usbDetachedReceiver;

	private class myUSB_BroadcastReceiver extends android.content.BroadcastReceiver {
		public int PID;
		public int VID;
		public myUSB_BroadcastReceiver(){
			this.PID = mDevice.getProductId();
			this.VID = mDevice.getVendorId();
		}
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (device != null && this.PID == device.getProductId()
						&& this.VID == device.getVendorId()) {
					Log.i(TAG, "USB device detached");
					stopUsbConnection();
				}
			}
		}
	}


	private class myCallback implements UsbSerialInterface.UsbReadCallback {
		/**
		 * This message is called when a device sends a message to the pi
		 * <p>
		 * If an API gets a message and can process it, it will return true.
		 *
		 * @param data - the message received.
		 *             TODO: get device name and APIs from the device
		 */
		@Override
		public void onReceivedData(byte[] data) {
			try {
				Log.d(TAG, "data received! " + Arrays.toString(data));
				String dataUtf8 = new String(data, "UTF-8");
				//Log.d(TAG, "data received! " + dataUtf8);
				buffer += dataUtf8;
				int index;
				while ((index = buffer.indexOf('\n')) != -1) {
					final String dataStr = buffer.substring(0, index + 1).trim();
					buffer = buffer.length() == index ? "" : buffer.substring(index + 1);
					onSerialDataReceived(dataStr);//I changed the way this is run.
					// It was called inside a runOnUiThread() anonymous class
				}
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Error receiving USB data", e);
			}
		}
	}



	/**
	 * constructor
	 *
	 * @param mDevice
	 */
	public Device(UsbDevice mDevice, Context context) {
		// add to devSet and devName in here
		this.mDevice = mDevice;
		this.APIs = new HashMap<String, ArduinoAPI>();
		this.callback = new myCallback();
		this.usbDetachedReceiver = new myUSB_BroadcastReceiver();
		IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
		context.registerReceiver(usbDetachedReceiver, filter);
		try {
			this.connect();
			devSet.add(this);
			this.serialDevice.read(callback);//adding a callback to the connection
			this.send("APIs");
			Log.i(TAG, "Device connected!");
		} catch (Exception e) {
			Log.e(TAG, e.getStackTrace() + e.getMessage());
			e.printStackTrace();
		}
	}



	/**
	 * This just passes other to its appropriate equals
	 * @param other
	 * @return true or false
	 */
	@Override
	public boolean equals(Object other){
		if(other instanceof Device)
			return this.equals((Device) other);
		else if (other instanceof UsbDevice)
			return this.equals((UsbDevice) other);
		else
			return super.equals(other);
	}
	/**
	 * Makes the static data structures able to compare to UsbDevices. Note, you can't store
	 * UsbDevices in the static data structures, but you can check if a Device with this UsbDevice
	 * already exists in the static data structures.
	 *
	 * @param other the UsbDevice to compare to.
	 * @return if this UsbDevice is the same device as other
	 */
	public boolean equals(UsbDevice other){
		return this.mDevice.equals(other);
	}
	/**
	 * Makes the static data structures able to compare to Devices.
	 * Does this by comparing the Devices' UsbDevices'
	 *
	 * @param other
	 * @return
	 */
	public boolean equals(Device other){
		return this.mDevice.equals(other.getDevice());
	}
	/**
	 * The hash code of the UsbDevice passed into this class.
	 * This is necessary so the Device class's static data structures.
	 * @return the hash code of this Device's UsbDevice
	 */
	@Override
	public int hashCode() {
		return this.mDevice.hashCode();
	}

	/**
	 * gets the actual device
	 * @return The UsbDevice
	 */
	public UsbDevice getDevice(){
		return this.mDevice;
	}


	/**
	 * This adds an API the connected arduino can use. For example the motor API can control motors
	 * @param name A unique identifier for the API name.
	 * @param api The actual api. Note,  You COULD have API objects of the same type. For example if you have two motor controllers connected,
	 * You'd need an API for each because APIs also store state information.
	 *
	 *  TODO: delete this?
	 */
	public void addAPI(String name, ArduinoAPI api){
		APIs.put(name, api);//maybe check if name is already in APIs?
	}

	public ArduinoAPI getAPI(String name){
		return APIs.get(name);
	}

	/**
	 * 	This resolves the name into a class assuming the name and the class are the same
	 * 	MAKE SURE 'name' IN THE ARDUINO API IS THE SAME AS THIS!!!
	 * 	Remember, when you create a ArduinoAPI it adds itself to Device's static data structures
	 */
	private ArduinoAPI getAPIfromName(String name){
		try{
			Package pkg = this.getClass().getPackage();
			Class cls = Class.forName(pkg.getName() + name);
			ArduinoAPI api = (ArduinoAPI) cls.getConstructor(String.class, Device.class)
					.newInstance(name, this);
			return api;
		}catch(Exception e){
			Log.e(TAG, "getAPIfromName can't resolve name");
			return null;
		}
	}

	/**
	 * Takes the _ delimited message starting with "APIs" then the API name and parses out the API
	 * names then converts the strings to ArduinoAPI objects. These objects then add themselves
	 * to the Device class's static data structures.
	 * @param message
	 */
	private void parseAPIs(String message){
		String[] apis = message.split("_");
		this.setName(apis[1]);//the name. remember, it starts with "APIs"
		for(int i=1; i < apis.length; i++){
			ArduinoAPI curAPI = getAPIfromName(apis[i]);
			if(curAPI != null)
				this.APIs.put(apis[i], curAPI);
		}
	}

	/**
	 * This sends data to the device
	 * @param message the data to be sent
	 */
	protected void send(String message){
	    this.serialDevice.write(message.getBytes());
	}
	
	/**
	 * gets the name of the device
	 * @return name the name of the port the device is connected to. (ex: /dev/ttyUSB1)
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Sets the name of the device. The name is important for connecting.
	 * @param name the name of the port the device is connected to. (ex: /dev/ttyUSB1)
	 */
	void setName(String name){
		this.name = name;
	}

	/**
	 * This sets a static UsbManager for this class because the connect methods need it
	 * @param usb The UsbManager
	 */
	public static void setUsbManager(UsbManager usb, boolean force){
		if(usbManager == null || force){
			usbManager = usb;
		}
	}

	/**
	 * connects to a physical device and sets up the SerialReader reader and SerialWriter writer 
	 * member variables. These are 
	 *
	 */
	protected void connect (){
	    //connect(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
		this.connect (115200, UsbSerialInterface.DATA_BITS_8,
				UsbSerialInterface.STOP_BITS_1, UsbSerialInterface.PARITY_NONE);
	}
	
	/** see connect(), This does the same thing as it, but it lets you set the serial communication
	 * paramaters yourself instead of assuming defaults.
	 * 
	 * see <a href = "https://stackoverflow.com/questions/391127/meaning-of-serial-port-parameters-in-java#391751">
	 * This stackoverflow post on these parameters </a>
	 * 
	 * @param speed the baud rate
	 * @param bits of data that are transferred at a time. This is typically 8 since most machines have 8-bit bytes these days.
	 * @param stop_bits defines # of trailing bits added to mark the end of the word.
	 * @param parity defines how error checking is done
	 * @throws Exception see connect()
	 * maybe add flow control to this???
	 */
   protected void connect (int speed, int bits, int stop_bits, int parity)
    {
		Log.i(TAG, "Ready to open USB device connection");
		connection = usbManager.openDevice(this.mDevice);
		serialDevice = UsbSerialDevice.createUsbSerialDevice(this.mDevice, connection);
		if (serialDevice != null) {
			if (serialDevice.open()) {
				serialDevice.setBaudRate(speed);
				serialDevice.setDataBits(bits);
				serialDevice.setStopBits(stop_bits);
				serialDevice.setParity(parity);
				serialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
				Log.i(TAG, "Serial connection opened");
			} else {
				Log.w(TAG, "Cannot open serial connection");
			}
		} else {
			Log.w(TAG, "Could not create Usb Serial Device");
		}
    }

	private void onSerialDataReceived(String data) {
		// Add whatever you want here
		Log.i(TAG, "Serial data received: " + data);
		if(data.startsWith("APIs")){
			parseAPIs(data);
		}else{
			for (ArduinoAPI api : APIs.values()) {
				if (api.receive(data))
					break;//the callback was for the api
			}
		}
	}

	private void stopUsbConnection() {
		try {
			if (serialDevice != null) {
				serialDevice.close();
			}

			if (connection != null) {
				connection.close();
			}
		} finally {
			serialDevice = null;
			connection = null;
		}
	}
}
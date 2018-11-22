package com.example.non_admin.picar;

import android.content.Context;
import android.hardware.usb.UsbManager;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Classes that inherit this can write to and read from the serial port.
 * They MUST override the void receive(String) method that's called when they get input.
 * Devices must be serial devices (not parallel)
 * 
 * Code for the connection and reading and writing over RXTX is taken from the 
 * <a href="http://rxtx.qbang.org/wiki/index.php/Examples">examples on the RXTX wiki</a>.
 * 
 * More useful links:
 * 
 * - <a href="http://users.frii.com/jarvi/rxtx/doc/index.html>gnu.io docs</a>
 * - <a href="https://docs.oracle.com/cd/E17802_01/products/products/javacomm/reference/api/index.html">
 * javax.comm docs</a>
 * - <a href-"http://rxtx.qbang.org/wiki/index.php/Download">
 * download link for the RXTXcomm.jar I'm using</a>
 * - <a href="http://rxtx.qbang.org/wiki/index.php/Using_RXTX_In_Eclipse">
 * Partially complete installation in eclipse instructions</a>
 * 
 * To fully install on eclipse in linux, run `sudo apt-get install librxtx-java`
 * then, make `/usr/share/java:/usr/lib/jni` the native library location of the RXTXcomm.jar
 * when you add it to the build path
 * 
 * 
 * 
 * @author pi
 *
 */
public class Device{
	String name;
	SerialWriter writer;
	SerialReader reader;
	SerialPort serialPort;
	HashMap<String, ArduinoAPI> APIs;
	// make a singleton device manager
	UsbManager usbManager;
	
	public Device(String name){
		setName(name);
		try{
			this.connect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * This adds an API the connected arduino can use. For example the motor API can control motors
	 * @param name A unique identifier for the API name.
	 * @param api The actual api. Note,  You COULD have API objects of the same type. For example if you have two motor controllers connected,
	 * You'd need an API for each because APIs also store state information.
	 */
	public void addAPI(String name, ArduinoAPI api){
		APIs.put(name, api);
	}
	
	
	
	/**
	 * This message is called when a device sends a message to the pi
	 * 
	 * If an API gets a message and can process it, it will return true.
	 * @param message - the message received.
	 */
	protected void receive(String message){
		for(ArduinoAPI api : APIs.values()){
			if(api.receive(message))
				break;
		}
		//error, nothing
	}
	
	/**
	 * This sends data to the device
	 * @param message the data to be sent
	 */
	protected void send(String message){
		this.writer.write(message);
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
	 * connects to a physical device and sets up the SerialReader reader and SerialWriter writer 
	 * member variables. These are 
	 * 
	 * @throws Exception because that's what the example told me to do
	 */
	protected void connect () throws Exception{
		   connect(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
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
	 */
   protected void connect (int speed, int bits, int stop_bits, int parity) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.getName());
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                this.serialPort = (SerialPort) commPort;
                this.serialPort.setSerialPortParams( speed, bits, stop_bits, parity);
                
                InputStream in = this.serialPort.getInputStream();
                OutputStream out = this.serialPort.getOutputStream();
                
                this.writer = new SerialWriter(out);
                (new Thread(this.writer)).start();
                
                this.reader = new SerialReader(in, this);
                this.serialPort.addEventListener(this.reader);
                
                this.serialPort.notifyOnDataAvailable(true);

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }

   public void killConnection(){
	   this.serialPort.removeEventListener();
	   this.serialPort.close();
   }
}

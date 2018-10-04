import java.io.IOException;
import java.io.InputStream;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;


public class SerialReader implements SerialPortEventListener {

    private InputStream in;
    private byte[] buffer = new byte[1024];
    private Device device;
    
    public SerialReader ( InputStream in, Device device )
    {
        this.in = in;
        this.device = device;
    }
    
    public void serialEvent(SerialPortEvent arg0) {
        int data;
      
        try
        {
            int len = 0;
            while ( ( data = in.read()) > -1 )
            {
                if ( data == '\n' ) {
                    break;
                }
                buffer[len++] = (byte) data;
            }
            device.receive(new String(buffer,0,len));
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is straight from the docs on RXTX
 * @author pi
 *
 */
public class SerialWriter implements Runnable {

	
    OutputStream out;
    
    public SerialWriter ( OutputStream out )
    {
        this.out = out;
    }
    
    public void write(String word){
    	try{
    	    this.out.write(word.getBytes());
    	    this.out.flush();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    public void run ()
    {
        try
        {                
            int c = 0;
            while ( ( c = System.in.read()) > -1 )
            {
                this.out.write(c);
            }                
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            System.exit(-1);
        }            
    }
}

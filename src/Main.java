import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/*
 * Use is subject to the license terms.
 */

/**
 * 
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Main {
    public static void main(String[] args) throws Exception {
        
        PrintStream out = new PrintStream( new ForkOutputStream(
             System.out, openConnection(args[0])));
        
        String recipient = args[1];
        
        out.println("HELO localhost");
        Thread.sleep(500);
        
        MBoxParser parser = new MBoxParser(System.in);
        while(parser.hasNext()) {
            Message m = parser.nextMessage().stripHeader("Received");
            
            out.println("MAIL FROM:<"+m.from+">");
            Thread.sleep(500);
            out.println("RCPT TO:<"+recipient+">");
            Thread.sleep(500);
            
            out.println("DATA");
            Thread.sleep(500);
            
            m.sendBodyTo(out);
            
            out.print("\r\n.\r\n");
            Thread.sleep(1000);
        }

        out.println("QUIT");
        

        out.close();
    }
    
    private static OutputStream openConnection( String server ) throws IOException {
        Socket s = new Socket(server,25);
        final InputStream in = s.getInputStream();
        new Thread() {
            public void run() {
                try {
                    byte[] buf = new byte[256];
                    int len;
                    while((len=in.read(buf))!=-1) {
                        System.out.write(buf,0,len);
                    }
                } catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        }.start();
        
        return s.getOutputStream();
    }
}

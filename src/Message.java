import java.io.PrintStream;

/*
 * Use is subject to the license terms.
 */

/**
 * Immutable SMTP message representation.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Message {
    /** Mail originally sent from this address */
    public final String from;
    
    /** Body of the e-mail. */
    public final String body;
    
    public Message(String _from, String _body) {
        this.from = _from;
        this.body = _body;
    }
    
    public Message stripHeader( String headerName ) {
        String[] lines = body.split("\r\n");
        StringBuffer buf = new StringBuffer();
        
        boolean stripping = false;
        int i;
        
        for( i=0; i<lines.length; i++ ) {
            if(stripping) {
                if( lines[i].length()>0
                && (lines[i].charAt(0)==' '||lines[i].charAt(0)=='\t'))
                    continue;   // skip this multi-line header
                stripping = false;
            }
            if(lines[i].length()==0)
                break;  // end of headers
            if(lines[i].startsWith(headerName+':'))
                stripping = true;
            else {
                buf.append(lines[i]);
                buf.append("\r\n");
            }
        }
        
        buf.append("\r\n");
        
        for( ; i<lines.length; i++ ) {
            buf.append(lines[i]);
            buf.append("\r\n");
        }
        
        return new Message(from,buf.toString());
    }

    /**
     * Sends the message body to the given output.
     * '.' in the beginning of a line will be doubled.
     */
    public void sendBodyTo(PrintStream out) {
        String[] lines = body.split("\r\n");
        for( int i=0; i<lines.length; i++ ) {
            if(lines[i].length()!=0 && lines[i].charAt(0)=='.')
                out.print('.');
            out.print(lines[i]);
            out.print("\r\n");
        }
    }
}

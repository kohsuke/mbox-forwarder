import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Use is subject to the license terms.
 */

/**
 * Parses the mbox format and retrieves messages one by one.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class MBoxParser {
    private final BufferedReader input;
    
    private static final Pattern fromLineParser = Pattern.compile(
        "From ([^ ]+) \\w\\w\\w \\w\\w\\w \\d\\d \\d\\d:\\d\\d:\\d\\d \\d\\d\\d\\d");

    private final StringBuffer buf = new StringBuffer();
    
    private String nextHeader;
    
    public MBoxParser( InputStream in ) throws IOException {
        input = new BufferedReader(new InputStreamReader(in,"US-ASCII"));
        nextHeader = input.readLine();
    }
    
    public Message nextMessage() throws IOException, ParseException {
        Matcher m = fromLineParser.matcher(nextHeader);
        if(!m.matches())
            throw new ParseException("unable to parse the From line",-1);
        
        String sender = m.group(1);
        
        String lastLine="";
        while(true) {
            String line=input.readLine();
            if(line==null) {
                // this is the last message
                nextHeader = null;
                break;
            }
            if(lastLine.length()==0 && fromLineParser.matcher(line).matches()) {
                // found the next header
                nextHeader = line;
                break;
            }
            
            buf.append(line);
            buf.append("\r\n");
            
            lastLine=line;
        }
        
        Message r = new Message(sender,buf.toString());
        buf.setLength(0);
        
        return r;
    }

    public boolean hasNext() throws IOException {
        return nextHeader!=null;
    }
}

import java.io.IOException;
import java.io.OutputStream;

/*
 * Use is subject to the license terms.
 */

/**
 * 
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ForkOutputStream extends OutputStream {
    
    private final OutputStream a;
    private final OutputStream b;
    
    public ForkOutputStream(OutputStream _a, OutputStream _b) {
        this.a = _a;
        this.b = _b;
    }
    
    public void write(int ch) throws IOException {
        a.write(ch);
        b.write(ch);
    }

    public void close() throws IOException {
        a.close();
        b.close();
    }

    public void flush() throws IOException {
        a.flush();
        b.flush();
    }

    public void write(byte[] buf) throws IOException {
        a.write(buf);
        a.write(buf);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        a.write(buf, off, len);
        b.write(buf, off, len);
    }

}

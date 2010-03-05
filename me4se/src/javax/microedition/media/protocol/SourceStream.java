/*
 * Created on 02.07.2005
 */
package javax.microedition.media.protocol;

import java.io.IOException;
import javax.microedition.media.Controllable;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */ 
public interface SourceStream extends Controllable {

    /**
     * @API MMAPI-1.0
     */ 
    public static final int NOT_SEEKABLE = 0;
    
    /**
     * @API MMAPI-1.0
     */ 
    public static final int SEEKABLE_TO_START = 1;
    
    /**
     * @API MMAPI-1.0
     */ 
    public static final int RANDOM_ACCESSIBLE = 2;    
    
    /**
     * @API MMAPI-1.0
     */ 
    public abstract ContentDescriptor getContentDescriptor();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract long getContentLength();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int read(byte[] b, int off, int len) throws IOException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int getTransferSize();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract long seek(long where) throws IOException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract long tell();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract int getSeekType();
}
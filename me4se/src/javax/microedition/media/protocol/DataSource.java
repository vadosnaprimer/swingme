/*
 * Created on 02.07.2005
 */
package javax.microedition.media.protocol;

import java.io.IOException;
import javax.microedition.media.Control;
import javax.microedition.media.Controllable;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */ 
public abstract class DataSource implements Controllable {

    /**
     * @API MMAPI-1.0
     */ 
    private String sourceLocator;    
    
    /**
     * @API MMAPI-1.0
     */ 
    public DataSource(String locator) {
        sourceLocator = locator;
    }

    /**
     * @API MMAPI-1.0
     */ 
    public String getLocator() {
        return sourceLocator;
    }

    /**
     * @API MMAPI-1.0
     */ 
    public abstract String getContentType();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract void connect() throws IOException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract void disconnect();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract void start() throws IOException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract void stop() throws IOException;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract SourceStream[] getStreams();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract Control[] getControls();

    /**
     * @API MMAPI-1.0
     */ 
    public abstract Control getControl(String s);
}
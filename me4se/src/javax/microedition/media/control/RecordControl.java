/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */
public interface RecordControl extends Control {
    
    /**
     * @API MMAPI-1.0
     */     
    public void commit() throws java.io.IOException;

    /**
     * @API MMAPI-1.0
     */     
    public String getContentType();

    /**
     * @API MMAPI-1.0
     */ 
    public void reset()  throws java.io.IOException;

    /**
     * @API MMAPI-1.0
     */     
    public void setRecordLocation(java.lang.String locator)throws java.io.IOException, MediaException;
    
    /**
     * @API MMAPI-1.0
     */ 
    public int setRecordSizeLimit(int size) throws MediaException;
    
    /**
     * @API MMAPI-1.0
     */ 
    public void setRecordStream(java.io.OutputStream stream);
    
    /**
     * @API MMAPI-1.0
     */ 
    public void startRecord();
    
    /**
     * @API MMAPI-1.0
     */ 
    public void stopRecord(); 
}

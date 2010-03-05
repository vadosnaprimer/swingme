/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;


/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */
public interface RateControl extends Control {
    
    /**
     * @API MMAPI-1.0
     */         
    public int getMaxRate();
    
    /**
     * @API MMAPI-1.0
     */     
    public int getMinRate();
    
    /**
     * @API MMAPI-1.0
     */     
    public int getRate();
    
    /**
     * @API MMAPI-1.0
     */     
    public int setRate(int millirate); 
}

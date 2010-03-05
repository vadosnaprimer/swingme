/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */
public interface StopTimeControl extends Control {

    /**
     * @API MMAPI-1.0
     */        
    public static final long RESET = 0x7fffffffffffffffL;
    
    /**
     * @API MMAPI-1.0
     */        
    public void setStopTime(long stopTime);

    /**
     * @API MMAPI-1.0
     */        
    public long getStopTime();
}

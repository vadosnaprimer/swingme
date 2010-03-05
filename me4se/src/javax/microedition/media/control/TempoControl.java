/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */
public interface TempoControl extends RateControl {
    
    /**
     * @API MMAPI-1.0
     */        
    public int getTempo();

    /**
     * @API MMAPI-1.0
     */        
    public int setTempo(int millitempo);
}

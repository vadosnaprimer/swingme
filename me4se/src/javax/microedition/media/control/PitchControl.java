/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;


/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */
public interface PitchControl extends Control {
    
    /**
     * @API MMAPI-1.0
     */     
    public int getMaxPitch();

    /**
     * @API MMAPI-1.0
     */ 
    public int getMinPitch();

    /**
     * @API MMAPI-1.0
     */ 
    public int getPitch();

    /**
     * @API MMAPI-1.0
     */ 
    public int setPitch(int millisemitones); 
}

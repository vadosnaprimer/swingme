/*
 * Created on 02.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;


/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */ 
public interface FramePositioningControl extends Control {

     /**
      * @API MMAPI-1.0
      */ 
    public long mapFrameToTime(int frameNumber);

    /**
     * @API MMAPI-1.0
     */ 
    public int mapTimeToFrame(long mediaTime);

    /**
     * @API MMAPI-1.0
     */ 
    public int seek(int frameNumber);

    /**
     * @API MMAPI-1.0
     */ 
    public int skip(int framesToSkip); 

}

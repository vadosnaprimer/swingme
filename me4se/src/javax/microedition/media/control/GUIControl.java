/*
 * Created on 01.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;


/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */ 
public interface GUIControl extends Control {

    /**
     * @API MMAPI-1.0
     */ 
    public static final int USE_GUI_PRIMITIVE = 0;

    /**
     * @API MMAPI-1.0
     */ 
    public abstract Object initDisplayMode(int mode, Object arg);
}

/*
 * Created on 01.07.2005
 */
package javax.microedition.media.control;

import javax.microedition.media.MediaException;

/**
 * @author Michael Kroll
 * @API MMAPI-1.0
 */ 
public interface VideoControl extends GUIControl {

    /**
     * @API MMAPI-1.0
     */     
    public static final int USE_DIRECT_VIDEO = 1;
    
    /**
     * @API MMAPI-1.0
     */     
    public Object initDisplayMode(int mode, Object arg);

    /**
     * @API MMAPI-1.0
     */     
    public  void setDisplayLocation(int x, int y);

    /**
     * @API MMAPI-1.0
     */     
    public int getDisplayX();

    /**
     * @API MMAPI-1.0
     */     
    public int getDisplayY();

    /**
     * @API MMAPI-1.0
     */     
    public  void setVisible(boolean visible);

    /**
     * @API MMAPI-1.0
     */     
    public void setDisplaySize(int width, int height) throws MediaException;

    /**
     * @API MMAPI-1.0
     */     
    public void setDisplayFullScreen(boolean fullScreenMode) throws MediaException;

    /**
     * @API MMAPI-1.0
     */     
    public int getSourceWidth();

    /**
     * @API MMAPI-1.0
     */     
    public int getSourceHeight();

    /**
     * @API MMAPI-1.0
     */     
    public int getDisplayWidth();

    /**
     * @API MMAPI-1.0
     */     
    public int getDisplayHeight();

    /**
     * @API MMAPI-1.0
     */     
    public byte[] getSnapshot(String imageType) throws MediaException;
}
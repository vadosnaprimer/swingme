package javax.microedition.amms.control.camera;

public interface CameraControl {

  public static int ROTATE_LEFT = 2; 
  public static int ROTATE_NONE = 3; 
  public static int ROTATE_RIGHT = 4; 
  public static int UNKNOWN = -1004; 

  public void enableShutterFeedback(boolean enable);
  
  public int getCameraRotation(); 

  public String getExposureMode() ;

  public int getStillResolution(); 

  public String[] getSupportedExposureModes();

  public int[]  getSupportedStillResolutions(); 

  public int[]  getSupportedVideoResolutions(); 

  public int  getVideoResolution(); 

  public boolean  isShutterFeedbackEnabled(); 

  public void setExposureMode(java.lang.String mode); 

  public void setStillResolution(int index); 

  public void setVideoResolution(int index); 
}

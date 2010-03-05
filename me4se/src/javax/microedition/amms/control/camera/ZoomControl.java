package javax.microedition.amms.control.camera;

public interface ZoomControl {
  
  public static int NEXT     = -1001;
  public static int PREVIOUS = -1002;
  public static int UNKNOWN  = -1004;

  public int getDigitalZoom();
  
  public int getDigitalZoomLevels(); 
  
  public int getMaxDigitalZoom(); 
  
  public int getMaxOpticalZoom(); 
  
  public int getMinFocalLength(); 
  
  public int getOpticalZoom(); 
  
  public int getOpticalZoomLevels(); 
  
  public int setDigitalZoom(int level); 
  
  public int setOpticalZoom(int level); 
}
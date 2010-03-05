package javax.microedition.amms.control.camera;

public interface SnapShotControl {
  
  public static int FREEZE              = -2;  
  public static int FREEZE_AND_CONFIRM  = -1;
  public static String SHOOTING_STOPPED = "SHOOTING_STOPPED";
  public static String STORAGE_ERROR    = "STORAGE_ERROR";
  public static String WAITING_UNFREEZE = "WAITING_UNFREEZE";

  public String getDirectory();
  
  public String getFilePrefix(); 
  
  public String getFileSuffix(); 
  
  public void setDirectory(String directory); 
  
  public void setFilePrefix(String prefix); 
  
  public void setFileSuffix(String suffix); 
  
  public void start(int maxShots); 
  
  public void stop(); 
  
  public void unfreeze(boolean save); 
}

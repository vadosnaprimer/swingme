package javax.microedition.amms.control.camera;

public interface FocusControl {
  
  public static int AUTO      = -1000; 
  public static int AUTO_LOCK = -1005;
  public static int NEXT      = -1001;
  public static int PREVIOUS  = -1002;
  public static int UNKNOWN   = -1004;
  
  public int getFocus(); 
  
  public int getFocusSteps(); 
  
  public boolean getMacro(); 
  
  public int getMinFocus(); 
  
  public boolean isAutoFocusSupported(); 
  
  public boolean isMacroSupported(); 
  
  public boolean isManualFocusSupported(); 
  
  public int setFocus(int distance); 
  
  public void setMacro(boolean enable); 
}

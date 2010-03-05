package javax.microedition.amms.control.camera;

public interface FlashControl {

  public static int AUTO                    = 2; 
  public static int AUTO_WITH_REDEYEREDUCE  = 3;
  public static int FILLIN                  = 6;
  public static int FORCE                   = 4;
  public static int FORCE_WITH_REDEYEREDUCE = 5; 
  public static int OFF                     = 1;

  public int getMode(); 
  public int[]  getSupportedModes(); 
  public boolean  isFlashReady();
  public void setMode(int mode); 
}

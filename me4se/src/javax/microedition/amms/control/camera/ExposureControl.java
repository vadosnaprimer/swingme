package javax.microedition.amms.control.camera;

public interface ExposureControl {
  
  public int getExposureCompensation(); 
  
  public int getExposureTime(); 
  
  public int getExposureValue(); 
  
  public int getFStop(); 
  
  public int getISO(); 
  
  public String getLightMetering(); 
  
  public int getMaxExposureTime(); 
  
  public int getMinExposureTime(); 
  
  public int[] getSupportedExposureCompensations(); 
  
  public int[] getSupportedFStops();
  
  public int[] getSupportedISOs(); 
  
  public String[] getSupportedLightMeterings(); 
  
  public void setExposureCompensation(int ec); 
  
  public int setExposureTime(int time); 
  
  public void setFStop(int aperture); 
  
  public void setISO(int iso); 
  
  public void setLightMetering(String metering); 
}

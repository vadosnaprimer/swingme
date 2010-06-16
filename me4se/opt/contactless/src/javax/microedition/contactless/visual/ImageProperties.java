package javax.microedition.contactless.visual;

public interface ImageProperties {

	public static final int PHYSICAL_SIZE_UNIT_INCH = 2;
	public static final int PHYSICAL_SIZE_UNIT_MILLIMETER = 3;
	public static final int PHYSICAL_SIZE_UNIT_PIXEL = 1;
	
	public int getLogicalUnitSize();
	
	public double getPhysicalHeigth();
	
	public int getPhysicalSizeUnit();
	
	public double getPhysicalWidth();
	
	public Object getProperty(int key);
	
	public int[] getPropertyKeys();
	
	public double getResolution();
	
	public String getSymbology();
	
	public void setLogicalUnitSize(int size);

	public void setPhysicalHeight(double height);
	
	public void setPhysicalSizeUnit(int sizeUnit);

	public void setPhysicalWidth(double width);
	
	public void setProperty(int key, Object value);

	public void setResolution(double resolution);

	public void setSymbology(java.lang.String symbology);
	
}
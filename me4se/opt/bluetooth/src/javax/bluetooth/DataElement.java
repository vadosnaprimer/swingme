package javax.bluetooth;

public class DataElement {

	public static int BOOL = 0x28;
	public static int DATALT = 0x38;
	public static int DATSEQ = 0x30;
	public static int INT_1 = 0x10;
	public static int INT_16 = 0x14;
	public static int INT_2 = 0x11;
	public static int INT_4 = 0x12;
	public static int INT_8 = 0x13;
	public static int NULL = 0x00;
	public static int STRING = 0x20;
	public static int U_INT_1 = 0x08;
	public static int U_INT_16 = 0x0C;
	public static int U_INT_2 = 0x09;
	public static int U_INT_4 = 0x0A;
	public static int U_INT_8 = 0x0B;
	public static int URL = 0x40;
	public static int UUID = 0x18;

	public DataElement(boolean bool) {
		
	}
	
	public DataElement(int valueType) {
		
	}
	
	public DataElement(int valueType, long value) {
		
	}

	public DataElement(int valueType, java.lang.Object value) {
		
	}
	
	public void addElement(DataElement elem) {
		
	}
	
    public boolean getBoolean() {
    	return false;
    }
    

    public int getDataType() {
    	return Integer.MIN_VALUE;
    }
    
    public long getLong() {
    	return Long.MIN_VALUE;
    }
    
    public int getSize() {
    	return Integer.MIN_VALUE;
    }

    public Object getValue() {
    	return null;
    }
    
    public void insertElementAt(DataElement elem, int index) {
    	
    }
    
    public boolean removeElement(DataElement elem) {
    	return false;
    }
}
package javax.microedition.lcdui;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public interface Choice {

	/**
	 * @API MIDP-1.0
	 */
    public static final int EXCLUSIVE = 1;
    
    /**
     * @API MIDP-1.0
     */
    public static final int MULTIPLE = 2;
    
    /**
     * @API MIDP-1.0
     */
    public static final int IMPLICIT = 3;

	/**
	 * POPUP is a choice having exactly one element selected 
	 * at a time. The selected element is always shown. The other 
	 * elements should be hidden until the user performs a particular 
	 * action to show them. When the user performs this action, all 
	 * elements become accessible. For example, an implementation could 
	 * use a popup menu to display the elements of a ChoiceGroup of type POPUP.
	 * 
	 * The POPUP type is not valid for List objects.
	 * 
	 * Value 4 is assigned to POPUP.
	 * 
	 * @API MIDP-2.0
	 */
	public static final int POPUP = 4;
	
	/**
	 * Field has the value 0.
	 *
	 * @API MIDP-2.0
	 */
	public static final int TEXT_WRAP_DEFAULT = 0;
	
	/**
	 * Field has the value 1.
	 *
	 * @API MIDP-2.0 
	 */
	public static final int TEXT_WRAP_ON = 1;
	
	/**
	 * Field has the value 2.
	 * 
	 * @API MIDP-2.0
	 */
	public static final int TEXT_WRAP_OFF = 2;
	
	/**
	 * @API MIDP-1.0 
	 */
    public int append(String s, Image i);
   
	/**
	 * @API MIDP-1.0 
	 */
    public void delete(int index);
    
	/**
	 * @API MIDP-1.0 
	 */
    public Image getImage(int index);

	/**
	 * @API MIDP-1.0 
	 */
    public int getSelectedFlags(boolean[] flags);

	/**
	 * @API MIDP-1.0 
	 */
    public int getSelectedIndex();
    
	/**
	 * @API MIDP-1.0 
	 */
    public String getString(int index);

	/**
	 * @API MIDP-1.0 
	 */
    public void insert(int index, String stringItem, Image imageItem);

	/**
	 * @API MIDP-1.0 
	 */
    public boolean isSelected(int index);

	/**
	 * @API MIDP-1.0 
	 */
    public void set(int index, String str, Image img);
 
	/**
	 * @API MIDP-1.0 
	 */
    public void setSelectedFlags(boolean [] flags);

	/**
	 * @API MIDP-1.0 
	 */
    public void setSelectedIndex(int i, boolean state);
    
    /**
     * @API MIDP-1.0 
     */
    public int size();
    
    /**
     * @API MIDP-2.0
     */
	public void deleteAll();

	/**
	 * @API MIDP-2.0
	 */
	public void setFitPolicy(int fitPolicy);

	/**
	 * @API MIDP-2.0
	 */
	public int getFitPolicy();
	
	/**
	 * @API MIDP-2.0
	 */
	public void setFont(int elementNum,	Font font);
	
	/**
	 * @API MIDP-2.0
	 */
	public Font getFont(int elementNum);
}
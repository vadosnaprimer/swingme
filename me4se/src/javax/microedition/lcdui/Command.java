package javax.microedition.lcdui;

import javax.microedition.midlet.ApplicationManager;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public class Command {

  /** special center command assignement blocker */  
  static final int CENTERBLOCKER = 0;
    
	/**
	 * @API MIDP-1.0
	 */
	public static final int SCREEN = 1;

	/**
	 * @API MIDP-1.0
	 */
	public static final int BACK = 2;

	/**
	 * @API MIDP-1.0
	 */
	public static final int CANCEL = 3;

	/**
	 * @API MIDP-1.0
	 */
	public static final int OK = 4;

	/**
	 * @API MIDP-1.0
	 */
	public static final int HELP = 5;

	/**
	 * @API MIDP-1.0
	 */
	public static final int STOP = 6;

	/**
	 * @API MIDP-1.0
	 */
	public static final int EXIT = 7;

	/**
	 * @API MIDP-1.0
	 */
	public static final int ITEM = 8;

	
	static final String[] TYPE_NAMES = {"CENTERBLOCKER", "SCREEN", "BACK", "CANCEL", "OK", 
			"HELP", "STOP", "EXIT", "ITEM"};

 //   static final String[] SIEMENS_TYPE_CHARS = {"", null, "\uE426", "\uE44C", "\uE44E", 
  //          "\uE44B", "\uE451", "\uE44C", null};

    static final String[] SIEMENS_TYPE_CHARS = {"", null, null, null, null, 
        null, null, null, null};

    String[] label = new String[3];
	int type;
	private int priority;

	/**
	 * @API MIDP-1.0
	 */
	public Command(String label, int type, int priority) {
		this.label[1] = label;
		this.type = type;
		if (type <= 0 || type > 8)
			throw new IllegalArgumentException();
		this.priority = priority;
        
        if(ApplicationManager.getInstance().getFlag("SiemensCK")) {
            this.label[0] = SIEMENS_TYPE_CHARS[type];
        }
	}

	/**
	 * @API MIDP-2.0
	 */
	public Command(String shortLabel, String longLabel, int commandType, int priority) {
		this(shortLabel, commandType, priority);
		this.label[2] = longLabel;
	}

	/**
	 * @API MIDP-1.0
	 */
	public String getLabel() {
		return label[1];
	}

	/**
	 * Gets the long label of the command.
	 * @return the Command's long label, or null if the Command has no long label
	 * 
	 * @API MIDP-2.0
	 */
	public String getLongLabel() {
		return label[2];
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getCommandType() {
		return type;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getPriority() {
		return priority;
	}

	String getTypeName() {
		return TYPE_NAMES[type];
	}
}
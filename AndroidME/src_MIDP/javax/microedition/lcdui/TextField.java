package javax.microedition.lcdui;

public class TextField {
    public static final int ANY             = 0x000;
    public static final int EMAILADDR       = 0x001;
    public static final int NUMERIC         = 0x002;
    public static final int PHONENUMBER     = 0x003;
    public static final int URL             = 0x004;
    public static final int DECIMAL         = 0x005;
    public static final int CONSTRAINT_MASK = 0xFFFF;

    public static final int PASSWORD                = 0x10000;
    public static final int UNEDITABLE              = 0x20000;
    public static final int SENSITIVE               = 0x40000;
    public static final int NON_PREDICTIVE          = 0x80000;
    public static final int INITIAL_CAPS_WORD       = 0x100000;
    public static final int INITIAL_CAPS_SENTENCE   = 0x200000;

	public TextField(String label, String text, int maxSize, int constraints) {
		throw new RuntimeException("TextField: Not implemented.");
	}
}

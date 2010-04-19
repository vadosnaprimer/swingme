package javax.microedition.pim;

import net.yura.android.pim.AndroidPim;


public abstract class PIM {

    public static final int CONTACT_LIST = 1;
    public static final int EVENT_LIST = 2;
    public static final int TODO_LIST = 3;

    public static final int READ_ONLY       = 0x01;
    public static final int WRITE_ONLY      = 0x02;
    public static final int READ_WRITE      = 0x03;


    private static PIM INSTANCE;

    public static final PIM getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AndroidPim();
        }
        return INSTANCE;
    }

    protected PIM() {

    }

    public abstract PIMList openPIMList(int pimListType, int mode) throws PIMException;

    public abstract PIMList openPIMList(int pimListType, int mode, java.lang.String name) throws PIMException;

    public abstract java.lang.String[] listPIMLists(int pimListType);

    public abstract PIMItem[] fromSerialFormat(java.io.InputStream is, java.lang.String enc) throws PIMException, java.io.UnsupportedEncodingException;

    public abstract void toSerialFormat( PIMItem item, java.io.OutputStream os, java.lang.String enc, java.lang.String dataFormat) throws PIMException, java.io.UnsupportedEncodingException;

    public abstract java.lang.String[] supportedSerialFormats(int pimListType);
}

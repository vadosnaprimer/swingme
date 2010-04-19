package javax.microedition.pim;

public interface PIMItem {

    int BINARY = 0;
    int BOOLEAN = 1;
    int DATE = 2;
    int INT = 3;
    int STRING = 4;
    int STRING_ARRAY = 5;
    int ATTR_NONE = 0;
    int EXTENDED_FIELD_MIN_VALUE = 16777216;
    int EXTENDED_ATTRIBUTE_MIN_VALUE = 16777216;

    public PIMList getPIMList();

    public void commit() throws PIMException;

    public boolean isModified();

    public int[] getFields();

    public byte[] getBinary(int field, int index);

    public void addBinary(int field, int attributes, byte[] value, int offset,  int length);

    public void setBinary(int field, int index, int attributes, byte[] value, int offset, int length);

    public long getDate(int field, int index);

    public void addDate(int field, int attributes, long value);

    public void setDate(int field, int index, int attributes, long value);

    public int getInt(int field, int index);

    public void addInt(int field, int attributes, int value);

    public void setInt(int field, int index, int attributes, int value);

    public String getString(int field, int index);

    public void addString(int field, int attributes, String value);

    public void setString(int field, int index, int attributes, String value);

    public boolean getBoolean(int field, int index);

    public void addBoolean(int field, int attributes, boolean value);

    public void setBoolean(int field, int index, int attributes, boolean value);

    public String[] getStringArray(int field, int index);

    public void addStringArray(int field, int attributes, String[] value);

    public void setStringArray(int field, int index, int attributes, String[] value);

    public int countValues(int field);

    public void removeValue(int field, int index);

    public int getAttributes(int field, int index);

    public void addToCategory(String category) throws PIMException;

    public void removeFromCategory(String category);

    public String[] getCategories();

    public int maxCategories();
}

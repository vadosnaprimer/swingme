package javax.microedition.pim;

/**
 * @author Stefan Haustein
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public interface PIMItem {

  public static final int ATTR_NONE = 0;
  public static final int BINARY = 0;
  public static final int BOOLEAN = 1;
  public static final int DATE = 2;
  public static final int EXTENDED_ATTRIBUTE_MIN_VALUE = 16777216;
  public static final int EXTENDED_FIELD_MIN_VALUE = 16777216;
  public static final int INT = 3;
  public static final int STRING = 4;
  public static final int STRING_ARRAY = 5;

  void addBinary(int field, int attributes, byte[] value, int offset, int length);

  // Adds a binary data value to a field in the item.
  void addBoolean(int field, int attributes, boolean value);

  // Adds a boolean value to a field in the item.
  void addDate(int field, int attributes, long value);

  // Adds a date value to a field in the item.
  void addInt(int field, int attributes, int value);

  // Adds an integer value to a field in the item.
  void addString(int field, int attributes, java.lang.String value);

  // Adds a String value to a field in the item.
  void addStringArray(int field, int attributes, java.lang.String[] value);

  // Adds an array of related string values as a single entity to a field in the
  // item.
  void addToCategory(java.lang.String category) throws PIMException;

  // Adds a category to this item.
  void commit() throws PIMException;

  // This method persists the data in the item to its PIM list.
  int countValues(int field);

  // Returns the number of data values currently set in a particular field.
  int getAttributes(int field, int index);

  // Gets the actual attributes associated with the data value at the given
  // index for the indicated field.
  byte[] getBinary(int field, int index);

  // Get a binary data value for a field from the item.
  boolean getBoolean(int field, int index);

  // Get a boolean value from a field in the item.
  java.lang.String[] getCategories();

  // Returns all the categories for that the item belongs to.
  long getDate(int field, int index);

  // Get a date value from a field in the item.
  int[] getFields();

  // Returns all fields in the item that have data stored for them.
  int getInt(int field, int index);

  // Get an integer value from a field in the item.
  PIMList getPIMList();

  // Returns the PIMList associated with this item.
  java.lang.String getString(int field, int index);

  // Get a String value from a field in the item.
  java.lang.String[] getStringArray(int field, int index);

  // Get an array of related values from a field in the item.
  boolean isModified();

  // This method returns a boolean indicating whether any of this item's fields
  // have been modified since the item was retrieved or last committed.
  int maxCategories();

  // Returns the maximum number of categories that this item can be assigned to.
  void removeFromCategory(java.lang.String category);

  // Remove a category from this item.
  void removeValue(int field, int index);

  // Removes the value at the given index for the indicated field in this item.
  void setBinary(int field, int index, int attributes, byte[] value, int offset, int length);

  // Sets an existing binary data value in a field to a new value.
  void setBoolean(int field, int index, int attributes, boolean value);

  // Sets an existing boolean data value in a field to a new value.
  void setDate(int field, int index, int attributes, long value);

  // Sets an existing date data value in a field to a new value.
  void setInt(int field, int index, int attributes, int value);

  // Sets an existing int data value in a field to a new value.
  void setString(int field, int index, int attributes, java.lang.String value);

  // Sets an existing String data value in a field to a new value.
  void setStringArray(int field, int index, int attributes, java.lang.String[] value);
  // Sets an existing String array data value in a field to a new value.
}

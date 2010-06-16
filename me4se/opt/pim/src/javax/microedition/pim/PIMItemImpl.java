package javax.microedition.pim;

import java.util.Date;

import org.kobjects.pim.*;

/**
 * @author Stefan Haustein
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
abstract class PIMItemImpl implements PIMItem {

  PimItem item;
  PIMListImpl list;
  boolean modified;

  PIMItemImpl(PIMListImpl list, PimItem item) {
    this.list = list;
    this.item = item;
  }

  String fieldToName(int id) {
    return ((PIMListImpl) list).fieldToName(id);
  }

  String attrToName(int id) {
    return ((PIMListImpl) list).attrToName(id);
  }

  abstract void copyItem();

  void addObject(int fieldId, int attr, Object data) {
    if (!modified) {
      copyItem();
      modified = true;
    }
    String name = fieldToName(fieldId);
    PimField field = new PimField(name);
    item.addField(field);
    setObject(fieldId, item.getFieldCount(name) - 1, attr, data);
  }

  Object getObject(int fieldId, int index) {
    return item.getField(fieldToName(fieldId), index).getValue();
  }

  /**
   * @see javax.microedition.pim.PIMItem#addBinary(int, int, byte, int, int)
   */
  public void addBinary(int field, int attributes, byte[] value, int offset, int length) {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMItem#addBoolean(int, int, boolean)
   */
  public void addBoolean(int field, int attributes, boolean value) {
    addObject(field, attributes, value ? Boolean.TRUE : Boolean.FALSE);
  }

  /**
   * @see javax.microedition.pim.PIMItem#addDate(int, int, long)
   */
  public void addDate(int field, int attributes, long value) {
    addObject(field, attributes, new Long(value));
  }

  /**
   * @see javax.microedition.pim.PIMItem#addInt(int, int, int)
   */
  public void addInt(int field, int attributes, int value) {
    addObject(field, attributes, new Integer(value));
  }

  /**
   * @see javax.microedition.pim.PIMItem#addString(int, int, java.lang.String)
   */
  public void addString(int field, int attributes, String value) {
    addObject(field, attributes, value);
  }

  /**
   * @see javax.microedition.pim.PIMItem#addStringArray(int, int,
   *      java.lang.String)
   */
  public void addStringArray(int field, int attributes, String[] value) {
    addObject(field, attributes, value);
  }

  /**
   * @see javax.microedition.pim.PIMItem#addToCategory(java.lang.String)
   */
  public void addToCategory(String category) {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMItem#countValues(int)
   */
  public int countValues(int field) {
    return item.getFieldCount(fieldToName(field));
  }

  public int maxCategories() {
    return 0;
  }

  /**
   * @see javax.microedition.pim.PIMItem#getAttributes(int, int)
   */
  public int getAttributes(int id, int index) {
    PimField field = item.getField(fieldToName(id), index);

    int[] attrs = list.getSupportedAttributes(id);
    int set = 0;
    for (int i = 0; i < attrs.length; i++) {
      if (field.getAttribute(attrToName(attrs[i])))
        set |= attrs[i];
    }
    return set;
  }

  /**
   * @see javax.microedition.pim.PIMItem#getBinary(int, int)
   */
  public byte[] getBinary(int field, int index) {
    return (byte[]) getObject(field, index);
  }

  /**
   * @see javax.microedition.pim.PIMItem#getBoolean(int, int)
   */
  public boolean getBoolean(int field, int index) {
    return ((Boolean) getObject(field, index)).booleanValue();
  }

  /**
   * @see javax.microedition.pim.PIMItem#getCategories()
   */
  public String[] getCategories() {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMItem#getDate(int, int)
   */
  public long getDate(int field, int index) {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMItem#getFields()
   */
  public int[] getFields() {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMItem#getInt(int, int)
   */
  public int getInt(int field, int index) {
    return ((Integer) getObject(field, index)).intValue();
  }

  /**
   * @see javax.microedition.pim.PIMItem#getPIMList()
   */
  public PIMList getPIMList() {
    return list;
  }

  /**
   * @see javax.microedition.pim.PIMItem#getString(int, int)
   */
  public String getString(int field, int index) {
    return (String) getObject(field, index);
  }

  /**
   * @see javax.microedition.pim.PIMItem#getStringArray(int, int)
   */
  public String[] getStringArray(int field, int index) {
    return (String[]) getObject(field, index);
  }

  /**
   * @see javax.microedition.pim.PIMItem#isModified()
   */
  public boolean isModified() {
    return modified;
  }

  /**
   * @see javax.microedition.pim.PIMItem#removeFromCategory(java.lang.String)
   */
  public void removeFromCategory(String category) {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMItem#removeValue(int, int)
   */
  public void removeValue(int field, int index) {
    if (!modified) {
      copyItem();
      modified = true;
    }
    item.removeField(fieldToName(field), index);
  }

  /**
   * @see javax.microedition.pim.PIMItem#setBinary(int, int, int, byte, int,
   *      int)
   */
  public void setBinary(int field, int index, int attributes, byte[] value, int offset, int length) {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMItem#setBoolean(int, int, int, boolean)
   */
  public void setBoolean(int field, int index, int attributes, boolean value) {
    setObject(field, index, attributes, new Boolean(value));
  }

  /**
   * @see javax.microedition.pim.PIMItem#setDate(int, int, int, long)
   */
  public void setDate(int field, int index, int attributes, long value) {
    setObject(field, index, attributes, new Date(value));
  }

  /**
   * @see javax.microedition.pim.PIMItem#setInt(int, int, int, int)
   */
  public void setInt(int field, int index, int attributes, int value) {
    setObject(field, index, attributes, new Integer(value));
  }

  void setObject(int fieldID, int index, int attributes, Object value) {
    if (!modified) {
      copyItem();
      modified = true;
    }

    PimField field = item.getField(fieldToName(fieldID), index);
    field.setValue(value);
    int[] supp = list.getSupportedAttributes(fieldID);
    for (int i = 0; i < supp.length; i++) {
      field.setAttribute(attrToName(supp[i]), (attributes & supp[i]) != 0);
    }
  }

  /**
   * @see javax.microedition.pim.PIMItem#setString(int, int, int,
   *      java.lang.String)
   */
  public void setString(int field, int index, int attributes, String value) {
    setObject(field, index, attributes, value);
  }

  /**
   * @see javax.microedition.pim.PIMItem#setStringArray(int, int, int,
   *      java.lang.String)
   */
  public void setStringArray(int field, int index, int attributes, String[] value) {
    setObject(field, index, attributes, value);
  }

}

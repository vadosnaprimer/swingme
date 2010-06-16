package javax.microedition.pim;

import java.util.Hashtable;

/**
 * @author haustein
 */
abstract class PIMListImpl implements PIMList {

  Hashtable items;

  PIMListImpl(Hashtable items) {
    this.items = items;
  }

  /**
   * @see javax.microedition.pim.PIMList#addCategory(java.lang.String)
   */
  public final void addCategory(String category) {
    throw new RuntimeException("categories not supported");
  }

  /**
   * @see javax.microedition.pim.PIMList#close()
   */
  public final void close() {
  }

  /**
   * @see javax.microedition.pim.PIMList#deleteCategory(java.lang.String,
   *      boolean)
   */
  public final void deleteCategory(String category, boolean deleteUnassignedItems) {
    throw new RuntimeException("categories not supported");
  }

  /**
   * @see javax.microedition.pim.PIMList#getCategories()
   */
  public final String[] getCategories() {
    return new String[0];
  }

  /**
   * @see javax.microedition.pim.PIMList#getName()
   */
  public final String getName() {
    return "default";
  }

  /**
   * @see javax.microedition.pim.PIMList#isCategory(java.lang.String)
   */
  public final boolean isCategory(String category) {
    return false;
  }

  abstract String fieldToName(int id);

  abstract String attrToName(int id);

  public boolean isSupportedField(int field) {
    int[] flds = getSupportedFields();
    for (int i = 0; i < flds.length; i++) {
      if (flds[i] == field)
        return true;
    }
    return false;
  }

  public boolean isSupportedAttribute(int field, int attr) {
    int[] flds = getSupportedAttributes(field);
    for (int i = 0; i < flds.length; i++) {
      if (flds[i] == attr)
        return true;
    }
    return false;
  }

  /**
   * @see javax.microedition.pim.PIMList#maxCategories()
   */
  public final int maxCategories() {
    return 0;
  }

  /**
   * @see javax.microedition.pim.PIMList#maxValues(int)
   */
  public int maxValues(int field) {
    return 1024;
  }

  /**
   * @see javax.microedition.pim.PIMList#renameCategory(java.lang.String,
   *      java.lang.String)
   */
  public final void renameCategory(String currentCategory, String newCategory) {
    throw new RuntimeException("categories not supported");
  }
}
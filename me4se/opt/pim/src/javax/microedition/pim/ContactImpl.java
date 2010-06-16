package javax.microedition.pim;

import org.kobjects.pim.*;

/**
 * @author Stefan Haustein
 */
class ContactImpl extends PIMItemImpl implements Contact {

  ContactImpl(PIMListImpl list, PimItem item) {
    super(list, item);
  }

  public int getFieldDataType(int field) {
    switch (field) {
    case ADDR:
    case NAME:
      return STRING_ARRAY;
    default:
      return STRING;
    }
  }

  public int[] getFields() {
    int[] supp = list.getSupportedFields();

    int[] act = new int[supp.length];
    int cnt = 0;

    for (int i = 0; i < supp.length; i++) {
      if (item.getFieldCount(fieldToName(supp[i])) > 0) {
        act[cnt++] = supp[i];
      }
    }

    int[] ret = new int[cnt];
    System.arraycopy(act, 0, ret, 0, cnt);
    return ret;
  }

  public String getFieldLabel(int field) {
    switch (field) {
    case ADDR:
      return "Address";
    case NAME:
      return "Address";
    case FORMATTED_NAME:
      return "Formatted Name";
    case URL:
      return "URL";
    default:
      return "Field " + field;
    }
  }

  /**
   * @see javax.microedition.pim.PIMItem#getCustomLabel(int, int)
   */
  public String getCustomLabel(int field, int index) {
    return null;
  }

  public int getPreferredIndex(int field) {
    return 0;
  }

  /**
   * @see javax.microedition.pim.PIMItemImpl#modifying()
   */
  void copyItem() {
    item = new VCard((VCard) item);
  }

  /**
   * @see javax.microedition.pim.PIMItem#commit()
   */
  public void commit() {
    if (modified) {
      PIM.commit(list.items, item);
    }
  }
}

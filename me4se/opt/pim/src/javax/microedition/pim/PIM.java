package javax.microedition.pim;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.kobjects.pim.PimField;
import org.kobjects.pim.PimItem;
import org.kobjects.pim.PimParser;
import org.kobjects.pim.VCard;

/**
 * @author Stefan Haustein
 */
public class PIM {

  public static final int CONTACT_LIST = 1;
  public static final int EVENT_LIST   = 2;
  public static final int READ_ONLY    = 1;
  public static final int READ_WRITE   = 3;
  public static final int TODO_LIST    = 3;
  public static final int WRITE_ONLY   = 2;

  /**
   * Creates and fills one or more PIM items from data provided in the given
   * InputStream object where the data is expressed in a valid data format
   * supported by this platform.
   */

  static PIM pim;

  /**
   * Stores PimItems; cannot store Contact directly because of back pointer to
   * the list
   */

  Hashtable contacts;

  protected PIM() {
    ContactList dummy = new ContactListImpl(null);
    contacts = new Hashtable();
    try {
      InputStream is = getClass().getResourceAsStream("/vcards.txt");

      InputStreamReader fr = new InputStreamReader(is);
      PimParser pp = new PimParser(fr, VCard.class);

      while (true) {
        PimItem item = pp.readItem();
        if (item == null)
          break;
        //System.out.println("item: " + item);

        String uid;
        while (item.getFieldCount("uid") > 1) {
          item.removeField("uid", item.getFieldCount("uid") - 1);
        }
        commit(contacts, item);
      }
    } catch (IOException e) {
      throw new RuntimeException(e.toString());
    }

    System.out.println("ME4SE: pim loaded from 'javax/microedition/pim/vcards.txt'");
  }

  static void commit(Hashtable list, PimItem item) {
    String uid = null;
    if (item.getFieldCount("uid") == 0) {
      do {
        uid = "" + System.currentTimeMillis();
      } while (list.get(uid) != null);
      PimField field = new PimField("uid");
      field.setValue(uid);
      item.addField(field);
    } else
      uid = (String) item.getField("uid", 0).getValue();
    list.put(uid, item);

  }

  public PIMItem[] fromSerialFormat(java.io.InputStream is, java.lang.String enc) throws PIMException {
    System.out.println("ME4SE: PIM.fromSerialFormat(InputStream is='" + is + "', String enc='" + enc + "') NYI !");
    return null;
  }

  public static PIM getInstance() {
    System.out.println("ME4SE: PIM.getInstance()");
    if (pim == null) {
      pim = new PIM();
    }
    return pim;
  }

  public java.lang.String[] listPIMLists(int pimListType) {
    System.out.println("ME4SE: PIM.listPIMLists(int pimListType='" + pimListType + "')");
    if (pimListType == CONTACT_LIST)
      return new String[] { "default" };

    return new String[0];
  }

  public PIMList openPIMList(int pimListType, int mode) throws PIMException {
    System.out.println("ME4SE: PIM.openPIMList(int pimListType='" + pimListType + "', int mode='" + mode + "')");
    return openPIMList(pimListType, mode, "default");
  }

  public PIMList openPIMList(int pimListType, int mode, java.lang.String name) throws PIMException {
    System.out.println("ME4SE: PIM.openPIMList(int pimListType='" + pimListType + "', int mode='" + mode + "', String name='" + name + "')");
    if (pimListType != CONTACT_LIST) {
      throw new RuntimeException("Currently, only contactList supported");
    }
    return new ContactListImpl(contacts);
  }

  public java.lang.String[] supportedSerialFormats(int pimListType) {
    System.out.println("ME4SE: PIM.supportedSerialFormats(int pimListType='" + pimListType + "') NYI !");
    return null;
  }

  public void toSerialFormat(PIMItem item, java.io.OutputStream os, java.lang.String enc, java.lang.String dataFormat) throws PIMException {
    System.out.println("ME4SE: PIM.toSerialFormat(PIMItem item='" + item + "', OutputStream os='" + os + "', String enc='" + enc + "', String dataFormat='" + dataFormat+ "') NYI !");
    throw new RuntimeException("NYI");
  }
}

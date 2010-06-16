package javax.microedition.pim;

import java.util.Enumeration;
import java.util.Hashtable;

import org.kobjects.pim.*;

public class ContactListImpl extends PIMListImpl implements ContactList {

  public ContactListImpl(Hashtable contacts) {
    super(contacts);
  }

  public int[] getSupportedAttributes(int field) {
    switch (field) {
    case Contact.TEL:
      return new int[] { Contact.ATTR_ASST, Contact.ATTR_AUTO, Contact.ATTR_FAX, Contact.ATTR_HOME,
          Contact.ATTR_MOBILE, Contact.ATTR_PAGER, Contact.ATTR_PREFERRED, Contact.ATTR_SMS, Contact.ATTR_WORK };
    case Contact.ADDR:
    case Contact.FORMATTED_ADDR:
    case Contact.EMAIL:
    case Contact.NICKNAME:
    case Contact.URL:
    case Contact.PHOTO:
    case Contact.PHOTO_URL:
      return new int[] { Contact.ATTR_ASST, Contact.ATTR_HOME, Contact.ATTR_PREFERRED, Contact.ATTR_WORK };
    case Contact.NAME:
    case Contact.FORMATTED_NAME:
    default:
      return new int[0];
    }
  }

  public int[] getSupportedArrayElements(int field) {

    if (field == Contact.ADDR) {
      return new int[] { 0, 1, 2, 3, 4, 5 };
    } else if (field == Contact.NAME) {
      return new int[] { 0, 1, 2, 3, 4 };
    } else
      throw new RuntimeException("Not an array field");
  }

  public Contact createContact() {
    return new ContactImpl(this, new VCard());
  }

  public void removeContact(Contact contact) {
    items.remove(contact.getString(Contact.UID, 0));
  }

  public void deleteCategory(String category) {
    throw new RuntimeException("NYI");
  }

  String attrToName(int attr) {
    switch (attr) {
    case Contact.ATTR_ASST:
      return "asst";
    case Contact.ATTR_AUTO:
      return "auto";
    case Contact.ATTR_FAX:
      return "fax";
    case Contact.ATTR_HOME:
      return "home";
    case Contact.ATTR_MOBILE:
      return "mobile";
    case Contact.ATTR_PAGER:
      return "pager";
    case Contact.ATTR_PREFERRED:
      return "pref";
    case Contact.ATTR_SMS:
      return "sms";
    case Contact.ATTR_WORK:
      return "work";
    default:
      throw new RuntimeException("illegatl attr!");
    }
  }

  /**
   * @see javax.microedition.pim.ItemImpl#idToName(int)
   */
  String fieldToName(int id) {
    switch (id) {
    case Contact.ADDR:
      return "adr";
    case Contact.NAME:
      return "n";
    case Contact.FORMATTED_NAME:
      return "fn";
    case Contact.ORG:
      return "org";
    case Contact.TEL:
      return "tel";
    case Contact.EMAIL:
      return "email";
    case Contact.UID:
      return "uid";
    case Contact.URL:
      return "url";
    case Contact.NICKNAME:
      return "nickname";
    }

    throw new RuntimeException("Unknown ID:" + id);
  }

  public String getFieldLabel(int fieldID) {
    switch (fieldID) {
    case Contact.BIRTHDAY:
      return "Birthday";
    case Contact.ORG:
      return "Organization";
    case Contact.EMAIL:
      return "Email";
    case Contact.NAME:
      return "Name";
    case Contact.FORMATTED_NAME:
      return "Formatted Name";
    case Contact.NICKNAME:
      return "Nickname";
    case Contact.NOTE:
      return "Note";
    case Contact.PHOTO:
      return "Photo";
    case Contact.PUBLIC_KEY:
      return "Public Key";
    case Contact.REVISION:
      return "Revision";
    case Contact.TEL:
      return "Telephone";
    case Contact.TITLE:
      return "Title";
    case Contact.UID:
      return "UID";
    case Contact.URL:
      return "URL";
    default:
      return fieldToName(fieldID);
    }
  }

  public int[] getSupportedFields() {
    return new int[] { Contact.UID, Contact.FORMATTED_NAME, Contact.NICKNAME, Contact.NAME, Contact.ADDR, Contact.TEL,
        Contact.EMAIL, Contact.URL };
  }

  public boolean isSupportedArrayElement(int i, int j) {
    return j >= 0 && ((i == Contact.NAME && j < 5) || (i == Contact.ADDR && j < 6));
  }

  public Contact importContact(Contact contact) {
    throw new RuntimeException("NYI");
  }

  /**
   * @see javax.microedition.pim.PIMList#supportsCustomLabels(int)
   */
  public int supportsCustomLabels(int field) {
    return 0;
  }

  public Enumeration items() {
    return new ItemEnumeration(this, PIM.pim.contacts.elements(), null, null, null);
  }

  public Enumeration items(String s) {
    return new ItemEnumeration(this, PIM.pim.contacts.elements(), null, s, null);
  }

  public Enumeration itemsByCategory(String category) {
    return new ItemEnumeration(this, PIM.pim.contacts.elements(), null, null, category);
  }

  public Enumeration items(PIMItem matching) {
    return new ItemEnumeration(this, PIM.pim.contacts.elements(), matching, null, null);
  }

  /**
   * @see javax.microedition.pim.PIMList#getArrayElementLabel(int, int)
   */
  public String getArrayElementLabel(int fieldId, int index) {
    if (fieldId == Contact.NAME) {
      switch (index) {
      case Contact.NAME_PREFIX:
        return "Name Prefix";
      case Contact.NAME_GIVEN:
        return "Given Name";
      case Contact.NAME_OTHER:
        return "Other Name";
      case Contact.NAME_FAMILY:
        return "Family Name";
      case Contact.NAME_SUFFIX:
        return "Name Suffix";
      }
    } else if (fieldId == Contact.ADDR) {
      switch (index) {
      case Contact.ADDR_STREET:
        return "Street";
      case Contact.ADDR_POBOX:
        return "PoBox";
      case Contact.ADDR_POSTALCODE:
        return "ZIP";
      case Contact.ADDR_LOCALITY:
        return "City";
      case Contact.ADDR_REGION:
        return "Region";
      case Contact.ADDR_COUNTRY:
        return "Country";
      case Contact.ADDR_EXTRA:
        return "Extra";
      }
    }

    throw new RuntimeException("illegal field or array element " + getFieldLabel(fieldId) + " index " + index);
  }

  /**
   * @see javax.microedition.pim.PIMList#getAttributeLabel(int)
   */
  public String getAttributeLabel(int attribute) {
    switch (attribute) {
    case Contact.ATTR_ASST:
      return "Assistant";
    case Contact.ATTR_AUTO:
      return "Auto";
    case Contact.ATTR_FAX:
      return "Fax";
    case Contact.ATTR_HOME:
      return "Home";
    case Contact.ATTR_MOBILE:
      return "Mobile";
    case Contact.ATTR_NONE:
      return "None";
    case Contact.ATTR_OTHER:
      return "Other";
    case Contact.ATTR_PAGER:
      return "Pager";
    case Contact.ATTR_PREFERRED:
      return "Pref.";
    case Contact.ATTR_SMS:
      return "SMS";
    case Contact.ATTR_WORK:
      return "Work";
    }
    throw new RuntimeException("Unknown ATTR");
  }

  /**
   * @see javax.microedition.pim.PIMList#getFieldDataType(int)
   */
  public int getFieldDataType(int field) {
    switch (field) {
    case Contact.BIRTHDAY:
      return PIMItem.DATE;
    case Contact.ADDR:
    case Contact.NAME:
      return PIMItem.STRING_ARRAY;
    case Contact.PUBLIC_KEY:
    case Contact.PHOTO:
      return PIMItem.BINARY;
    default:
      return PIMItem.STRING;
    }
  }

  public int maxValues(int field) {
    switch (field) {
    case Contact.UID:
    case Contact.NAME:
    case Contact.FORMATTED_NAME:
      return 1;
    default:
      return 1024;
    }
  }

}

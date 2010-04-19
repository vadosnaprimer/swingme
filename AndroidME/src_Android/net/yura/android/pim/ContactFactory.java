package net.yura.android.pim;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Contacts;
//import android.provider.ContactsContract;
import android.provider.Contacts.People;
import javax.microedition.pim.Contact;
import javax.microedition.pim.PIMItem;

public class ContactFactory {

	/**
	 *
	 * @param personCursor the cursor object must be at the right position. It is not cleaned up afterwards. The caller is responsible for that.
	 * @return the contact
	 */
	public static ContactImpl getContactFromCursor(ContentResolver contentResolver, ContactListImpl contactListImpl, Cursor personCursor) {
		int columnIndex;

		columnIndex = personCursor.getColumnIndex(People._ID);
		int id = personCursor.getInt(columnIndex);

		ContactImpl contactImpl = new ContactImpl(id,contactListImpl);

		contactImpl.addString(Contact.UID, PIMItem.ATTR_NONE, "" + id);
		putNameIntoContact(personCursor,contactImpl);
		putAddressIntoContact(contentResolver,id,contactImpl);
		putDisplayNameIntoContact(personCursor, contactImpl);
		putNumbersIntoContact(contentResolver,id,contactImpl);
		putNoteIntoContact(personCursor,contactImpl);

		contactImpl.setModified(false);
		return contactImpl;
	}

	private static void putNameIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.NAME);
//JP		int columnIndex = personCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		String name = personCursor.getString(columnIndex);
		String[] names = new String[ContactListImpl.CONTACT_NAME_FIELD_INFO.numberOfArrayElements];
		names[Contact.NAME_OTHER] = name;
		contactImpl.addStringArray(Contact.NAME,PIMItem.ATTR_NONE, names);
	}

	private static void putNumbersIntoContact(ContentResolver contentResolver, int id, ContactImpl contactImpl) {
		String where = Contacts.ContactMethods.PERSON_ID + " == " + id;
		Cursor phoneCursor = contentResolver.query(Contacts.Phones.CONTENT_URI, null, where, null, null);
		int dataColumn = phoneCursor.getColumnIndex(Contacts.Phones.NUMBER);
		int typeColumn = phoneCursor.getColumnIndex(Contacts.Phones.TYPE);
		while(phoneCursor.moveToNext()) {
			int androidType = phoneCursor.getInt(typeColumn);
			Integer attribute = convertPhoneTypeToAttribute(androidType);
			String number = phoneCursor.getString(dataColumn);
			contactImpl.addString(Contact.TEL, attribute.intValue(), number);
		}
		phoneCursor.close();
	}

	private static void putAddressIntoContact(ContentResolver contentResolver, int id, ContactImpl contact) {
		String where;
		int dataColumn;
		int typeColumn;

		where = Contacts.ContactMethods.PERSON_ID + " == " + id + " AND " + Contacts.ContactMethods.KIND + " == " + Contacts.KIND_POSTAL;

		Cursor addressCursor = contentResolver.query(Contacts.ContactMethods.CONTENT_URI, null, where, null, null);

		dataColumn = addressCursor.getColumnIndex(Contacts.ContactMethods.DATA);
		typeColumn = addressCursor.getColumnIndex(Contacts.ContactMethods.TYPE);

		while(addressCursor.moveToNext()) {
			int androidType = addressCursor.getInt(typeColumn);
			Integer attribute = convertAddressTypeToAttribute(androidType);
			String address = addressCursor.getString(dataColumn);
			String[] value = new String[ContactListImpl.CONTACT_ADDR_FIELD_INFO.numberOfArrayElements];
			value[Contact.ADDR_EXTRA] = address;
			contact.addStringArray(Contact.ADDR, attribute.intValue(), value);
		}
		addressCursor.close();
	}

	/**
	 * TODO: Collaps this method and putNoteIntoContact so the code duplication is gone.
	 * @param personCursor
	 * @param contactImpl
	 */
	private static void putDisplayNameIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.DISPLAY_NAME);
		String name = personCursor.getString(columnIndex);
		if(name != null){
			contactImpl.addString(Contact.FORMATTED_NAME, PIMItem.ATTR_NONE, name);
		}
	}

	private static void putNoteIntoContact(Cursor personCursor, ContactImpl contactImpl) {
		int columnIndex = personCursor.getColumnIndex(People.NOTES);
		while(personCursor.moveToNext()) {
			String name = personCursor.getString(columnIndex);
			if(name != null)
				contactImpl.addString(Contact.NOTE, PIMItem.ATTR_NONE, name);
		}
	}

//	private static int convertAttrToAddressType(int attribute) {
//		if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
//			return Contacts.ContactMethods.TYPE_HOME;
//		}
//		if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
//			return Contacts.ContactMethods.TYPE_HOME;
//		}
//		if((attribute & Contact.ATTR_OTHER) == Contact.ATTR_OTHER){
//			return Contacts.ContactMethods.TYPE_OTHER;
//		}
//		return Contacts.ContactMethods.TYPE_OTHER;
//	}
//
//	private static int convertAttrToTelType(int attribute) {
//		if((attribute & Contact.ATTR_MOBILE) == Contact.ATTR_MOBILE){
//			return Contacts.People.Phones.TYPE_MOBILE;
//		}
//		if((attribute & Contact.ATTR_FAX) == Contact.ATTR_FAX){
//			if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
//				return Contacts.People.Phones.TYPE_FAX_HOME;
//			}
//			if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
//				return Contacts.People.Phones.TYPE_FAX_WORK;
//			}
//			return Contacts.People.Phones.TYPE_FAX_WORK;
//		}
//		if((attribute & Contact.ATTR_HOME) == Contact.ATTR_HOME){
//			return Contacts.People.Phones.TYPE_HOME;
//		}
//		if((attribute & Contact.ATTR_WORK) == Contact.ATTR_WORK){
//			return Contacts.People.Phones.TYPE_WORK;
//		}
//		if((attribute & Contact.ATTR_PAGER) == Contact.ATTR_PAGER){
//			return Contacts.People.Phones.TYPE_PAGER;
//		}
//		if((attribute & Contact.ATTR_OTHER) == Contact.ATTR_OTHER){
//			return Contacts.People.Phones.TYPE_OTHER;
//		}
//		return Contacts.People.Phones.TYPE_OTHER;
//	}

	/**
	 *
	 * @param addressType
	 * @return returns the Contact.ATTR_ value.
	 */
	private static Integer convertAddressTypeToAttribute(int addressType) {
		switch(addressType) {
		case Contacts.ContactMethods.TYPE_HOME: return new Integer(Contact.ATTR_HOME);
		case Contacts.ContactMethods.TYPE_WORK: return new Integer(Contact.ATTR_WORK);
		case Contacts.ContactMethods.TYPE_OTHER: return new Integer(Contact.ATTR_OTHER);
		case Contacts.ContactMethods.TYPE_CUSTOM: return new Integer(Contact.ATTR_OTHER);
		}
		return new Integer(PIMItem.ATTR_NONE);
	}

	/**
	 *
	 * @param addressType
	 * @return returns the Contact.ATTR_ value.
	 */
	private static Integer convertPhoneTypeToAttribute(int addressType) {
		switch(addressType) {
			case Contacts.Phones.TYPE_HOME: return new Integer(Contact.ATTR_HOME);
			case Contacts.Phones.TYPE_WORK: return new Integer(Contact.ATTR_WORK);
			case Contacts.Phones.TYPE_OTHER: return new Integer(Contact.ATTR_OTHER);
			case Contacts.Phones.TYPE_MOBILE: return new Integer(Contact.ATTR_MOBILE);
			case Contacts.Phones.TYPE_FAX_HOME: return new Integer(Contact.ATTR_FAX|Contact.ATTR_HOME);
			case Contacts.Phones.TYPE_FAX_WORK: return new Integer(Contact.ATTR_FAX|Contact.ATTR_WORK);
			case Contacts.Phones.TYPE_PAGER: return new Integer(Contact.ATTR_PAGER);
		}
		return new Integer(PIMItem.ATTR_NONE);
	}
}

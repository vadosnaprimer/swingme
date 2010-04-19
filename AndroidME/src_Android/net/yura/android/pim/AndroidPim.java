package net.yura.android.pim;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

public class AndroidPim extends PIM {

	public static final String DEFAULT_PIMLIST_NAME_CONTACTS = "contacts";
	private static Hashtable<Integer, ContactListImpl> contactListInstances = new Hashtable<Integer, ContactListImpl>();

	@Override
	public PIMItem[] fromSerialFormat(InputStream is, String enc) throws PIMException, UnsupportedEncodingException {
		throw new UnsupportedEncodingException("At the moment no encoding is supported.");
	}

	@Override
	public String[] listPIMLists(int pimListType) {
		switch(pimListType) {
			case PIM.CONTACT_LIST:
				return new String[] {DEFAULT_PIMLIST_NAME_CONTACTS};
			default:
				return new String[0];
		}
	}

	@Override
	public PIMList openPIMList(int pimListType, int mode) throws PIMException {
		switch(pimListType) {
		case PIM.CONTACT_LIST:
			return openPIMList(pimListType, mode,DEFAULT_PIMLIST_NAME_CONTACTS);
		default:
			throw new PIMException("The pimListType '"+pimListType+"' is not supported.");
		}

	}

	@Override
	public PIMList openPIMList(int pimListType, int mode, String name) throws PIMException {
		switch(pimListType) {
		case PIM.CONTACT_LIST:
			if( ! DEFAULT_PIMLIST_NAME_CONTACTS.equals(name)) {
				throw new PIMException("A PIMList with name '"+name+"' and type '"+pimListType+"' does not exist.");
			}

			ContactListImpl contactList = contactListInstances.get(mode);
			if (contactList == null) {
//JP: The list is being re-used... Once open in one mode, it can no longer change... at least have one per mode...
//  			    contactListInstance = new ContactListImpl(name,mode);

			    contactList = new ContactListImpl(name, mode);
			    contactListInstances.put(mode, contactList);
			}
			return contactList;
		default:
			throw new PIMException("The pimListType '"+pimListType+"' is not supported.");
		}
	}

	@Override
	public String[] supportedSerialFormats(int pimListType) {
		return new String[0];
	}

	@Override
	public void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat) throws PIMException, UnsupportedEncodingException {
		throw new UnsupportedEncodingException("At the moment no encoding is supported.");
	}

}

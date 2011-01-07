package net.yura.android.pim;

import java.util.Enumeration;

import net.yura.android.AndroidMeApp;

import android.content.ContentResolver;
import android.database.Cursor;
//import android.provider.ContactsContract;
import android.provider.Contacts.People;

public class ContactEnumeration implements Enumeration{

	private Cursor peopleCursor;
	private ContentResolver contentResolver;
	private final ContactDao contactDao;
	private final int count;
	private int position;

	public ContactEnumeration(ContactDao contactDao) {
		this.contactDao = contactDao;
		this.contentResolver = AndroidMeApp.getContext().getContentResolver();
		this.peopleCursor = this.contentResolver.query(People.CONTENT_URI, null, null, null, null);
//JP		this.peopleCursor = this.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		this.count = this.peopleCursor.getCount();
		this.position = -1;
	}

	public boolean hasMoreElements() {
		return this.position < this.count -1;
	}

	public Object nextElement() {
		if(this.peopleCursor.isClosed()) {
			return null;
		}
		this.position++;
		// Do not use moveToNext() as it will break if items are removed while iterating the cursor.
		this.peopleCursor.moveToPosition(this.position);
		ContactImpl contactFromCursor = this.contactDao.getContactFromCursor(this.peopleCursor);
		return contactFromCursor;
	}

}

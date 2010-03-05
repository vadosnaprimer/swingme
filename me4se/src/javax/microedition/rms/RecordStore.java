// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors: Michael Kroll
//
// STATUS: 
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package javax.microedition.rms;

import org.me4se.impl.rms.*;

/**
  * @API MIDP-1.0 
 * @API MIDP-2.0
 */

public abstract class RecordStore {

    public static final int AUTHMODE_PRIVATE = 0;
    public static final int AUTHMODE_ANY = 1;
    
    
	/**
	 * @ME4SE INTERNAL
	 */
	protected RecordStore() {
	}

	/** 
	 * 
	 * @API MIDP-1.0
	 */
	public abstract int addRecord(byte[] data, int offset, int count) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException;

	/**
	 *
	 * @API MIDP-1.0
	 */
	public abstract void addRecordListener(RecordListener listener);

	/** 
	 *
	 * @API MIDP-1.0
	 */

	public abstract void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException;

	/** 
	 * @API MIDP-1.0 
	 */

	public abstract void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException;

	/** 
	 * @API MIDP-1.0
	 */
	public static void deleteRecordStore(String recordStoreName) throws RecordStoreException, RecordStoreNotFoundException {
		((AbstractRecordStore) openRecordStore(recordStoreName, false)).deleteRecordStoreImpl();
		AbstractRecordStore.recordStores.remove(recordStoreName);
	}

	/**
	 * @API MIDP-1.0	 
	 */
	public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated) throws RecordStoreNotOpenException {
		return new RecordEnumerationImpl((AbstractRecordStore) this, filter, comparator, keepUpdated);
	}

	/** 
	 * @API MIDP-1.0 
	 */

	public abstract long getLastModified() throws RecordStoreNotOpenException;

	/**
	 * @API MIDP-1.0
	 */
	public abstract String getName() throws RecordStoreNotOpenException;

	/**
	 * @API MIDP-1.0
	 */
	public abstract int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException;

	/**
	 * @API MIDP-1.0
	 */
	public abstract int getNumRecords() throws RecordStoreNotOpenException;

	/**
	 * @API MIDP-1.0
	 */
	public abstract byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException;

	/** 
	 * @API MIDP-1.0 
	 */
	public abstract int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, ArrayIndexOutOfBoundsException;

	/**	
	 * @API MIDP-1.0
	 */
	public abstract int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException;

	/**
	 * @API MIDP-1.0
	 */
	public abstract int getSize() throws RecordStoreNotOpenException;

	/**
	 * @API MIDP-1.0
	 */
	public abstract int getSizeAvailable() throws RecordStoreNotOpenException;

	/**
	 * @API MIDP-1.0
	 */
	public abstract int getVersion() throws RecordStoreNotOpenException;

	/**
	 * @API MIDP-1.0
	 */
	public static String[] listRecordStores() {

		return AbstractRecordStore.listRecordStoresImpl();

	}

	/**
	 * @API MIDP-1.0
	 */
	public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {

		AbstractRecordStore store = (AbstractRecordStore) AbstractRecordStore.recordStores.get(recordStoreName);

		if (store == null) {
			store = AbstractRecordStore.newInstance();
			AbstractRecordStore.recordStores.put(recordStoreName, store);
		}

		store.open(recordStoreName, createIfNecessary);
		return store;
	}

	/**
	 * Open (and possibly create) a record store that can be shared with other MIDlet 
	 * suites. The RecordStore is owned by the current MIDlet suite. The authorization 
	 * mode is set when the record store is created, as follows: 
	 * AUTHMODE_PRIVATE - Only allows the MIDlet suite that created the RecordStore 
	 *                    to access it. This case behaves identically to 
	 *                    openRecordStore(recordStoreName, createIfNecessary). 
	 * AUTHMODE_ANY - Allows any MIDlet to access the RecordStore. Note that this 
	 *                makes your recordStore accessible by any other MIDlet on the device. 
	 *                This could have privacy and security issues depending on the data 
	 *                being shared. Please use carefully. 
	 * 
	 * The owning MIDlet suite may always access the RecordStore and always has access 
	 * to write and update the store. If this method is called by a MIDlet when the record 
	 * store is already open by a MIDlet in the MIDlet suite, this method returns a reference 
	 * to the same RecordStore object.
	 * 
	 * @param recordStoreName the MIDlet suite unique name for the record store, 
	 *                        consisting of between one and 32 Unicode characters inclusive.
	 * @param createIfNecessary if true, the record store will be created if necessary
	 * @param authmode the mode under which to check or create access. Must be one of 
	 *                 AUTHMODE_PRIVATE or AUTHMODE_ANY. This argument is ignored if 
	 *                 the RecordStore exists.
	 * @param writable true if the RecordStore is to be writable by other MIDlet suites 
	 *                 that are granted access. This argument is ignored if the RecordStore exists.
	 * @return RecordStore object for the record store
	 * @throws RecordStoreException if a record store-related exception occurred
	 * @throws RecordStoreNotFoundException if the record store could not be found
	 * @throws RecordStoreFullException if the operation cannot be completed because the 
	 *                                  record store is full
	 * @throws IllegalArgumentException if authmode or recordStoreName is invalid
	 * 
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED
	 */
	public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary, int authmode, boolean writable) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
		System.out.println("RecordStore.openRecordStore(authmode, wriable) called with no effect!");
		return openRecordStore(recordStoreName, createIfNecessary);
	}

	/**
	 * Open a record store associated with the named MIDlet suite. The MIDlet 
	 * suite is identified by MIDlet vendor and MIDlet name. Access is granted only 
	 * if the authorization mode of the RecordStore allows access by the current 
	 * MIDlet suite. Access is limited by the authorization mode set when the record 
	 * store was created: 
	 * AUTHMODE_PRIVATE - Succeeds only if vendorName and suiteName identify the 
	 *                    current MIDlet suite; this case behaves identically to 
	 *                    openRecordStore(recordStoreName, createIfNecessary). 
	 * AUTHMODE_ANY - Always succeeds. Note that this makes your recordStore accessible 
	 *                by any other MIDlet on the device. This could have privacy and 
	 *                security issues depending on the data being shared. Please use 
	 *                carefully. Untrusted MIDlet suites are allowed to share data but 
	 *                this is not recommended. The authenticity of the origin of untrusted 
	 *                MIDlet suites cannot be verified so shared data may be used unscrupulously. 
	 * 
	 * If this method is called by a MIDlet when the record store is already open by a MIDlet 
	 * in the MIDlet suite, this method returns a reference to the same RecordStore object.
	 * 
  	 * If a MIDlet calls this method to open a record store from its own suite, the behavior is 
  	 * identical to calling: openRecordStore(recordStoreName, false)
	 * @param recordStoreName the MIDlet suite unique name for the record store, consisting 
	 *                        of between one and 32 Unicode characters inclusive.
	 * @param vendorName the vendor of the owning MIDlet suite
	 * @param suiteName the name of the MIDlet suite
	 * @return RecordStore object for the record store
	 * @throws RecordStoreException if a record store-related exception occurred
	 * @throws RecordStoreNotFoundException if the record store could not be found
	 * @throws SecurityException if this MIDlet Suite is not allowed to open the 
	 *                           specified RecordStore.
	 * @throws IllegalArgumentException if recordStoreName is invalid
	 * 
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED
	 */
	public static RecordStore openRecordStore(String recordStoreName, String vendorName, String suiteName) throws RecordStoreException, RecordStoreNotFoundException {
		System.out.println("RecordStore.openRecordStore(vendorName, suiteName) called with no effect!");
		return openRecordStore(recordStoreName, false);
	}

	/**
	 * Removes the specified RecordListener.
	 * 
	 * @param listener the RecordChangedListener
	 * 
	 * @API MIDP-1.0
	 */
	public abstract void removeRecordListener(RecordListener listener);

	/**
	 * Sets the data in the given record to that passed in.
	 * 
	 * @param recordId The ID of the record store.
	 * @param newdata The new data buffer to store in the record.
	 * @param offset The index into the data buffer of the first new byte to be stored in the record.
	 * @param count The number of bytes of the data buffer to use for this record.
	 * 
	 * @API MIDP-1.0
	 */
	public abstract void setRecord(int recordId, byte[] newData, int offset, int count) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException;

	/**
	 * Changes the access mode for this RecordStore. The authorization mode choices are:
	 * 
	 * AUTHMODE_PRIVATE - Only allows the MIDlet suite that created the RecordStore 
	 *                    to access it. This case behaves identically to 
	 *                    openRecordStore(recordStoreName, createIfNecessary). 
	 * AUTHMODE_ANY - Allows any MIDlet to access the RecordStore. Note that this makes 
	 *                your recordStore accessible by any other MIDlet on the device. This 
	 *                could have privacy and security issues depending on the data being shared. 
	 *                Please use carefully.	The owning MIDlet suite may always access the RecordStore 
	 *                and always has access to write and update the store. Only the owning MIDlet 
	 *                suite can change the mode of a RecordStore.
	 * 
	 * @param authmode the mode under which to check or create access. Must be one 
	 *                 of AUTHMODE_PRIVATE or AUTHMODE_ANY.
	 * @param writable - true if the RecordStore is to be writable by other MIDlet suites that are granted access
	 * Throws:
	 * RecordStoreException - if a record store-related exception occurred
	 * SecurityException - if this MIDlet Suite is not allowed to change the mode of the RecordStore
	 * IllegalArgumentException - if authmode is invalid
	 * Since: 
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED
	 */
	public abstract void setMode(int authmode, boolean writable) throws RecordStoreException;

}

/*
 * $Log: RecordStore.java,v $
 * Revision 1.1  2007/07/29 19:12:29  haustein
 * Initial checkin of contents moved from the kobjects.org me4se module...
 *
 * Revision 1.20  2006/10/20 15:33:09  haustein
 * recordstore cleanup
 *
 * Revision 1.19  2005/01/16 21:29:25  haustein
 * synced with fixes for siemens
 *
 * Revision 1.18  2003/11/08 15:01:23  mkroll
 * added api tags
 *
 * Revision 1.17  2003/11/07 23:41:03  mkroll
 * changed runtimeexceptions in ne midp2 stuff to simple system outs.
 *
 * Revision 1.16  2003/11/07 21:45:46  mkroll
 * *** empty log message ***
 *
 * Revision 1.15  2003/11/07 21:27:35  mkroll
 * *** empty log message ***
 *
 * Revision 1.14  2003/11/07 18:04:04  mkroll
 * added new API tags for JavaDoc generation
 *
 * Revision 1.13  2003/11/07 17:38:53  mkroll
 * added new API tags for JavaDoc generation
 *
 * Revision 1.12  2003/01/30 19:02:49  haustein
 * deleted sources restored from local history
 *
 * Revision 1.10  2002/12/05 23:30:25  mkroll
 * Removed some unneccessary imports.
 *
 * Revision 1.9  2002/07/15 22:56:24  mkroll
 * Added last fixes after moving CommConnection to org.me4se.impl.gcf
 *
 * Revision 1.8  2002/07/15 22:46:52  haustein
 * moved jad and chooser back, moved rms and gcf stuff to appropriate subpackages
 *
 * Revision 1.7  2002/06/17 11:13:55  haustein
 * rms listrecordstore exception removed
 *
 * Revision 1.6  2002/05/31 19:42:51  haustein
 * recordstoreexception added for listrecordstores
 *
 * Revision 1.5  2002/05/31 19:19:22  haustein
 * gcf preparations for file/serial
 *
 * Revision 1.4  2002/01/18 13:58:02  haustein
 * changed deleteRecordStore functionaly back to previously fixed version(cvs update -d -P!)
 *
 * Revision 1.3  2002/01/18 13:22:14  mkroll
 * Added deteled record support and deleteRecordStore()
 *
 */
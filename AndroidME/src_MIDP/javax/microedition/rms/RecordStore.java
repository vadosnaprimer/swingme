package javax.microedition.rms;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

import net.yura.android.rms.RecordEnumerationImpl;

import android.app.Activity;
import android.content.Context;

public class RecordStore {
	public static final int AUTHMODE_PRIVATE = 0;

	public static final int AUTHMODE_ANY = 1;

	private final static String RECORD_STORE_SUFFIX = ".rs";

	public Hashtable records = new Hashtable();

	private String recordStoreName;
	private int version = 0;
	private long lastModified = 0;
	private int nextRecordID = 1;

	// TODO - Jose
	private static Activity activity = MIDlet.DEFAULT_MIDLET.getActivity();

	private transient boolean open;

	private transient Vector recordListeners = new Vector();

	public RecordStore(String recordStoreName) {
		if (recordStoreName.length() <= 32) {
			this.recordStoreName = recordStoreName;
		} else {
			this.recordStoreName = recordStoreName.substring(0, 32);
		}
		this.open = false;
	}

	public RecordStore(DataInputStream dis) throws IOException {
		this.recordStoreName = dis.readUTF();
		this.version = dis.readInt();
		this.lastModified = dis.readLong();
		this.nextRecordID = dis.readInt();

		try {
			while (true) {
				int recordId = dis.readInt();
				byte[] data = new byte[dis.readInt()];
				dis.read(data, 0, data.length);
				this.records.put(new Integer(recordId), data);
			}
		} catch (EOFException ex) {
		}
	}

	public void write(DataOutputStream dos) throws IOException {
		dos.writeUTF(recordStoreName);
		dos.writeInt(version);
		dos.writeLong(lastModified);
		dos.writeInt(nextRecordID);

		Enumeration en = records.keys();
		while (en.hasMoreElements()) {
			Integer key = (Integer) en.nextElement();
			dos.writeInt(key.intValue());
			byte[] data = (byte[]) records.get(key);
			dos.writeInt(data.length);
			dos.write(data);
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void closeRecordStore() throws RecordStoreNotOpenException,
			RecordStoreException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		if (recordListeners != null) {
			recordListeners.removeAllElements();
		}

		open = false;
	}

	public String getName() throws RecordStoreNotOpenException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		return recordStoreName;
	}

	public int getVersion() throws RecordStoreNotOpenException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		synchronized (this) {
			return version;
		}
	}

	public int getNumRecords() throws RecordStoreNotOpenException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		synchronized (this) {
			return records.size();
		}
	}

	public int getSize() throws RecordStoreNotOpenException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		int size = 0;
		for (Enumeration keys = records.keys(); keys.hasMoreElements();) {

			size += ((byte[]) records.get(keys.nextElement())).length;
		}
		return size;
	}

	public int getSizeAvailable() throws RecordStoreNotOpenException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		try {
			String storeDir = activity.getFilesDir().getPath();
			android.os.StatFs fs = new android.os.StatFs(storeDir);
			long fsSize = fs.getFreeBlocks() * fs.getBlockSize();

			fsSize = (fsSize / 100) * 80;
			fsSize = Math.min(fsSize, Integer.MAX_VALUE);

			return (int) fsSize;
		} catch (Throwable e) {

		}

		return 4 * 1024 * 1024;
	}

	public long getLastModified() throws RecordStoreNotOpenException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		synchronized (this) {
			return lastModified;
		}
	}

	public void addRecordListener(RecordListener listener) {
		if (!recordListeners.contains(listener)) {
			recordListeners.addElement(listener);
		}
	}

	public void removeRecordListener(RecordListener listener) {
		recordListeners.removeElement(listener);
	}

	public int getNextRecordID() throws RecordStoreNotOpenException,
			RecordStoreException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		synchronized (this) {
			return nextRecordID;
		}
	}

	public int addRecord(byte[] data, int offset, int numBytes)
			throws RecordStoreNotOpenException, RecordStoreException,
			RecordStoreFullException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}
		if (data == null && numBytes > 0) {
			throw new NullPointerException();
		}
		if (numBytes > getSizeAvailable()) {
			throw new RecordStoreFullException();
		}

		byte[] recordData = new byte[numBytes];
		if (data != null) {
			System.arraycopy(data, offset, recordData, 0, numBytes);
		}

		int curRecordID;
		synchronized (this) {
			records.put(new Integer(nextRecordID), recordData);
			version++;
			curRecordID = nextRecordID;
			nextRecordID++;
			lastModified = System.currentTimeMillis();
		}

		saveChanges(this);

		return curRecordID;
	}

	public void deleteRecord(int recordId) throws RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		synchronized (this) {
			if (records.remove(new Integer(recordId)) == null) {
				throw new InvalidRecordIDException();
			}
			version++;
			lastModified = System.currentTimeMillis();
		}

		saveChanges(this);
	}

	public int getRecordSize(int recordId) throws RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		synchronized (this) {
			byte[] data = (byte[]) records.get(new Integer(recordId));
			if (data == null) {
				throw new InvalidRecordIDException();
			}

			return data.length;
		}
	}

	public int getRecord(int recordId, byte[] buffer, int offset)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		int recordSize;
		synchronized (this) {
			recordSize = getRecordSize(recordId);
			System.arraycopy(records.get(new Integer(recordId)), 0, buffer,
					offset, recordSize);
		}

		return recordSize;
	}

	public byte[] getRecord(int recordId) throws RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		byte[] data;

		synchronized (this) {
			data = new byte[getRecordSize(recordId)];
			getRecord(recordId, data, 0);
		}

		return data.length < 1 ? null : data;
	}

	public void setRecord(int recordId, byte[] newData, int offset, int numBytes)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException, RecordStoreFullException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		// FIXME fixit
		if (numBytes > getSizeAvailable()) {
			throw new RecordStoreFullException();
		}

		byte[] recordData = new byte[numBytes];
		System.arraycopy(newData, offset, recordData, 0, numBytes);

		synchronized (this) {
			Integer id = new Integer(recordId);
			if (records.remove(id) == null) {
				throw new InvalidRecordIDException();
			}
			records.put(id, recordData);
			version++;
			lastModified = System.currentTimeMillis();
		}

		saveChanges(this);
	}

	public RecordEnumeration enumerateRecords(RecordFilter filter,
			RecordComparator comparator, boolean keepUpdated)
			throws RecordStoreNotOpenException {
		if (!open) {
			throw new RecordStoreNotOpenException();
		}

		return new RecordEnumerationImpl(this, filter, comparator, keepUpdated);
	}

	// TODO there should be a public constructors
	// private RecordStore() {
	//
	// }

	public static void deleteRecordStore(String recordStoreName)
			throws RecordStoreException, RecordStoreNotFoundException {
		String storeFileName = getSuiteName() + "." + recordStoreName
				+ RECORD_STORE_SUFFIX;
		activity.deleteFile(storeFileName);
	}

	public static String[] listRecordStores() {
		String[] result = activity.fileList();
		if (result != null) {
			if (result.length == 0) {
				result = null;
			} else {
				String prefix = getSuiteName() + ".";
				for (int i = 0; i < result.length; i++) {
					int startIdx = (result[i].startsWith(prefix)) ? prefix
							.length() : 0;
					int endIdx = result[i].length()
							- RECORD_STORE_SUFFIX.length();

					result[i] = result[i].substring(startIdx, endIdx);
				}
			}
		}
		return result;
	}

	public static RecordStore openRecordStore(String recordStoreName,
			boolean createIfNecessary) throws RecordStoreException,
			RecordStoreFullException, RecordStoreNotFoundException {
		String storeFileName = getSuiteName() + "." + recordStoreName
				+ RECORD_STORE_SUFFIX;

		RecordStore recordStore;
		try {
			recordStore = loadFromDisk(storeFileName);
		} catch (FileNotFoundException e) {
			if (!createIfNecessary) {
				throw new RecordStoreNotFoundException(recordStoreName);
			}
			recordStore = new RecordStore(recordStoreName);
			saveToDisk(getSuiteName() + "." + recordStoreName
					+ RECORD_STORE_SUFFIX, recordStore);
		}
		recordStore.setOpen(true);

		return recordStore;
	}

	public static RecordStore openRecordStore(String recordStoreName,
			boolean createIfNecessary, int authmode, boolean writable)
			throws RecordStoreException, RecordStoreFullException,
			RecordStoreNotFoundException {
		// TODO Not yet implemented
		return openRecordStore(recordStoreName, createIfNecessary);
	}

	public static RecordStore openRecordStore(String recordStoreName,
			String vendorName, String suiteName) throws RecordStoreException,
			RecordStoreNotFoundException {
		// TODO Not yet implemented
		return openRecordStore(recordStoreName, false);
	}

	private static void saveToDisk(String recordStoreFileName,
			final RecordStore recordStore) throws RecordStoreException {
		try {
			DataOutputStream dos = new DataOutputStream(activity
					.openFileOutput(recordStoreFileName, Context.MODE_PRIVATE));
			recordStore.write(dos);
			dos.close();
		} catch (IOException e) {
			throw new RecordStoreException(e.getMessage());
		}
	}

	private static RecordStore loadFromDisk(String recordStoreFileName)
			throws FileNotFoundException {
		RecordStore store = null;
		try {
			DataInputStream dis = new DataInputStream(activity
					.openFileInput(recordStoreFileName));
			store = new RecordStore(dis);
			dis.close();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return store;
	}

	private static String getSuiteName() {
		return activity.getComponentName().getPackageName();
	}

	private void saveChanges(RecordStore recordStore)
			throws RecordStoreNotOpenException, RecordStoreException {
		saveToDisk(getSuiteName() + "." + recordStore.getName()
				+ RECORD_STORE_SUFFIX, recordStore);
	}

}

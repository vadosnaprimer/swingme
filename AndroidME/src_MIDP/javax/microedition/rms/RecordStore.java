package javax.microedition.rms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.yura.android.rms.SqlDao;
import net.yura.android.rms.SqlRecordEnumeration;


public class RecordStore extends Object {

    public static final int AUTHMODE_PRIVATE = 0;
    public static final int AUTHMODE_ANY = 1;


    private static SqlDao sqlDao;

    // This mapping contains all open record stores by name. If a record store is not in this mapping, it is not open.
    private static HashMap<String, RecordStore> openedRecordStores = new HashMap<String, RecordStore>();

    // The unique name of this record store.
    private String name;

    // The version of this object. It changes whenever it is modified.
    private int version;

    // The id of this record store in the database.
    private long recordStorePk;
    // TODO: Use the next to field.
//    private String vendorName;
//    private String suiteName;
    private int numRecords;
    private int size;
    private long sizeAvailable = 4 * 1024 * 1024;

    // TODO: Manage lastModified everywhere.
    private long lastModified;
    private int nextRecordID = 1;

    /**
     * The number of times this record store was opened. If this number is 0 then this record store is closed.
     */
    private int openCount = 0;
    // TODO: Manage authMode everywhere.
    private int authMode;
    // TODO: Manage writable everywhere.
//    private boolean writable = false;

    private List<RecordListener> listeners = new ArrayList<RecordListener>();

    /**
     * @param name the unique name of this record store within a midlet suite
     * @param pk the database primary key of this store
     */
    public RecordStore(String name, long pk) {
        this.name = name;
        this.recordStorePk = pk;
    }


    public static void deleteRecordStore(String recordStoreName) throws RecordStoreException, RecordStoreNotFoundException {
        RecordStore recordStore = getOpenedRecordStoreFromCache(recordStoreName);
        if (recordStore != null) {
            throw new RecordStoreException("The record store '" + recordStoreName + "' is not closed.");
        }
        sqlDao.deleteRecordStore(recordStoreName);

    }


    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        init();
        if (recordStoreName == null) {
            throw new IllegalArgumentException("Parameter 'recordStoreName' must not be null or empty.");
        }

        if (recordStoreName.length() < 1 || recordStoreName.length() > 32) {
            throw new IllegalArgumentException("Parameter 'recordStoreName' must have a length between 1 and 32.");
        }

        // Try the cache.
        RecordStore recordStore = openRecordStoreFromCache(recordStoreName);
        if (recordStore != null) {
            return recordStore;
        }

        // Try the datadabase.
        recordStore = sqlDao.getRecordStore(recordStoreName);
        if (recordStore != null) {
            cacheRecordStore(recordStoreName,recordStore);
            return recordStore;
        }
        if (!createIfNecessary) {
            throw new RecordStoreNotFoundException("No record store with name '" + recordStoreName + "' found.");
        }

        // Create the database.
        recordStore = sqlDao.createRecordStore(recordStoreName);
        if (recordStore == null) {
            throw new RecordStoreException("Could not create record store with name '" + recordStoreName + "'. Reason: The method 'SqlDao.createRecordStore' returned null although it is not allowed to do so.");
        }
        cacheRecordStore(recordStoreName, recordStore);

        return recordStore;
    }


    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary, int authmode, boolean writable) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        return openRecordStore(recordStoreName, createIfNecessary);
    }


    public static RecordStore openRecordStore(String recordStoreName, String vendorName, String suiteName) throws RecordStoreException, RecordStoreNotFoundException {
        return openRecordStore(recordStoreName, true);
    }


    public void setMode(int authmode, boolean writable) throws RecordStoreException {
        // TODO implement setMode
    }


    public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
        if(isClosed()) {
            throw new RecordStoreNotOpenException();
        }
        boolean closed = closeChachedRecordStore();

        // When there are no more open instances of this record store around, inform the listeners.
        if(closed) {
            synchronized (this.listeners) {
                this.listeners.clear();
            }

            for (int i = 0; i < CACHE_SIZE; i++) {
                cachedIds[i] = 0;
                cachedValues[i] = null;
            }
        }
    }


    public static String[] listRecordStores() {
        init();
        String[] listRecordStores = sqlDao.listRecordStores();
        if (listRecordStores.length == 0) {
            return null;
        }
        return listRecordStores;
    }


    public String getName() throws RecordStoreNotOpenException {
        return this.name;
    }


    public int getVersion() throws RecordStoreNotOpenException {
        return this.version;
    }


    public int getNumRecords() throws RecordStoreNotOpenException {
        return this.numRecords;
    }


    public int getSize() throws RecordStoreNotOpenException {
        return this.size;
    }


    public int getSizeAvailable() throws RecordStoreNotOpenException {

        if (this.sizeAvailable > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) this.sizeAvailable;
    }


    public long getLastModified() throws RecordStoreNotOpenException {
        return this.lastModified;
    }


    public void addRecordListener(RecordListener listener) {
        synchronized (this.listeners) {
            if (!this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }
    }


    public void removeRecordListener(RecordListener listener) {
        synchronized (this.listeners) {
            if (!this.listeners.contains(listener)) {
                this.listeners.remove(listener);
            }
        }
    }


    public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
        if (isClosed()) {
            throw new RecordStoreNotOpenException("");
        }
        return this.nextRecordID;
    }


    public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {

        if (isClosed()) {
            throw new RecordStoreNotOpenException("The record store is not open because it was closed. This RecordStore object is invalid and will stay so.");
        }
        if (data == null) {
            data = new byte[0];
        }
        if (data.length != 0 && offset >= data.length) {
            throw new RecordStoreException("The offset '" + offset + "' is beyond the size of the data array of '" + data.length + "'");
        }
        if (numBytes < 0) {
            throw new RecordStoreException("The number of bytes '" + numBytes + "' must not be negative.");
        }
        if (offset < 0) {
            throw new RecordStoreException("The offset '" + offset + "' must not be negative.");
        }
        if (offset + numBytes > data.length) {
            throw new RecordStoreException("The Parameter numBytes with value '" + numBytes + "' exceeds the number of available bytes if counted from offset '" + offset + "'");
        }
        byte[] actualData = new byte[numBytes];
        System.arraycopy(data, offset, actualData, 0, numBytes);
        int recordId = sqlDao.addRecord(getPk(), actualData);
        // The addRecord method will increment the nextRecordId in the database.
        // So we update the cache accordingly.
        RecordStore recordStore = sqlDao.getRecordStore(getPk());
        updateRecordStoreInstance(recordStore);
        fireRecordAddedEvent(recordId);
        return recordId;
    }

    // TODO: What about concurrent updates?
    private void updateRecordStoreInstance(RecordStore recordStore) throws RecordStoreException {
        this.name = recordStore.name;
        this.nextRecordID = recordStore.nextRecordID;
        this.numRecords = recordStore.numRecords;
        this.size = recordStore.size;
        this.version = recordStore.version;
        this.recordStorePk = recordStore.recordStorePk;
        this.authMode = recordStore.authMode;
    }


    public void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        if (isClosed()) {
            throw new RecordStoreNotOpenException();
        }
        if (recordId < 0) {
            throw new InvalidRecordIDException();
        }
        sqlDao.removeRecord(getPk(), recordId);
        RecordStore recordStore = sqlDao.getRecordStore(getPk());
        updateRecordStoreInstance(recordStore);
        fireRecordDeletedEvent(recordId);
    }


    public int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {

        byte[] data = getRecord(recordId);
        return (data == null) ? 0 : data.length;
    }


    public int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        byte[] data = getRecord(recordId);

        System.arraycopy(data, 0, buffer, offset, data.length);

        return data.length - offset;
    }

    private final int CACHE_SIZE = 5;
    private int cachedIdx;
    private int[] cachedIds = new int[CACHE_SIZE];
    private byte[][] cachedValues = new byte[CACHE_SIZE][];

    public byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        if (isClosed()) {
            throw new RecordStoreNotOpenException();
        }
        if (recordId < 0) {
            throw new InvalidRecordIDException();
        }

        for (int i = 0; i < CACHE_SIZE; i++) {
            if (recordId == cachedIds[i]) {
                return cachedValues[i];
            }
        }

        byte[] record = sqlDao.getRecord(getPk(), recordId);

        cachedValues[cachedIdx] = record;
        cachedIds[cachedIdx] = recordId;
        cachedIdx = (cachedIdx + 1) % CACHE_SIZE;

        return record;
    }

    public void setRecord(int recordId, byte[] newData, int offset, int numBytes) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {
        if (isClosed()) {
            throw new RecordStoreNotOpenException();
        }
        if (recordId < 0) {
            throw new InvalidRecordIDException("The parameter 'recordId' must not be negative.");
        }
        if (newData == null) {
            newData = new byte[0];
        }

        byte[] data = new byte[numBytes];
        System.arraycopy(newData, offset, data, 0, numBytes);

        sqlDao.setRecord(getPk(), recordId, data);
        RecordStore recordStore = sqlDao.getRecordStore(getPk());
        updateRecordStoreInstance(recordStore);
        fireRecordChangedEvent(recordId);
    }


    public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated) throws RecordStoreNotOpenException {
        SqlRecordEnumeration sqlRecordEnumeration = new SqlRecordEnumeration(this, filter, comparator, keepUpdated);
        return sqlRecordEnumeration;
    }

    private static void init() {
        if (sqlDao == null) {
            sqlDao = SqlDao.getInstance();
        }
    }


    private void fireRecordAddedEvent(int recordId) {
        synchronized (this.listeners) {
            for (Iterator<RecordListener> iterator = this.listeners.iterator(); iterator.hasNext();) {
                RecordListener recordListener = iterator.next();
                recordListener.recordAdded(this, recordId);
            }
        }
    }

    private void fireRecordChangedEvent(int recordId) {
        synchronized (this.listeners) {
            for (Iterator<RecordListener> iterator = this.listeners.iterator(); iterator.hasNext();) {
                RecordListener recordListener = iterator.next();
                recordListener.recordChanged(this, recordId);
            }
        }
    }

    private void fireRecordDeletedEvent(int recordId) {
        synchronized (this.listeners) {
            for (Iterator<RecordListener> iterator = this.listeners.iterator(); iterator.hasNext();) {
                RecordListener recordListener = iterator.next();
                recordListener.recordDeleted(this, recordId);
            }
        }
    }

    // Android Only
    public synchronized boolean isClosed() {
        return this.openCount <= 0;
    }


    // Android Only
    public long getPk() {
        return this.recordStorePk;
    }

    // Android Only
    public void setVersion(int version) {
        this.version = version;
    }

    // Android Only
    public void setNextId(int nextRecordId) {
        this.nextRecordID = nextRecordId;
    }

    // Android Only
    public void setNumberOfRecords(int numberOfRecords) {
        this.numRecords = numberOfRecords;
    }

    // Android Only
    public void setSize(int size) {
        this.size = size;
    }

    private static RecordStore getOpenedRecordStoreFromCache(String recordStoreName) {
        return openedRecordStores.get(recordStoreName);
    }


    private static void cacheRecordStore(String recordStoreName,RecordStore recordStore) {
        openedRecordStores.put(recordStoreName, recordStore);
        recordStore.openCount++;
    }

    /**
     * Returns a cached record store and increases the open count.
     * @param recordStoreName
     * @return
     */
    private static RecordStore openRecordStoreFromCache(String recordStoreName) {
        RecordStore recordStore = openedRecordStores.get(recordStoreName);
        if(recordStore != null) {
            recordStore.openCount++;
        }
        return recordStore;
    }

    private boolean closeChachedRecordStore() {
        this.openCount--;
        if (this.openCount > 0) {
            return false;
        }
        openedRecordStores.remove(this.name);
        return true;
    }

}

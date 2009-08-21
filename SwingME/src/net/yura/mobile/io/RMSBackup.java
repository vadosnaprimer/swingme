package net.yura.mobile.io;

import java.util.Vector;
import java.util.Hashtable;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import java.io.InputStream;
import java.io.OutputStream;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public class RMSBackup extends QueueProcessorThread {

    public interface RMSBackupHelper {
        Object getObjectId(Object objToSave);
        Object loadObject(InputStream is) throws Exception;
        void saveObject(OutputStream out, Object obj) throws Exception;
        void rmsSaveFailed();
    }


    private String rmsName;
    private RMSBackupHelper helper;

    public RMSBackup(RMSBackupHelper help,String n) {
        rmsName = n;
        helper = help;
    }

    public void clearBackup() {
        try {
            RecordStore.deleteRecordStore(rmsName);
        }
        catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("can not del RMS");
        }
        table.clear();
    }


    public Vector recover() throws Exception {

        Vector current = new Vector();

        // if there was a crash we may need to recover data
        // check if we need to recover anything from RMS
        RecordStore recordStore = RecordStore.openRecordStore(rmsName, true);

        if (recordStore.getNumRecords() != 0) {

                if (!isRunning()) {
                    start();
                }

                RecordEnumeration re = recordStore.enumerateRecords(null, null, true);
                while (re.hasNextElement()) {
                        // Get an input stream for the data in the record store.
                        int id = re.nextRecordId();
                        ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(id));
                        // as we now have bookings in RMS, we need to update the bookingID->rmsID lookup table
                        Object currentBooking = helper.loadObject(bais);
                        table.put( helper.getObjectId(currentBooking), new Integer(id) );
                        current.addElement(currentBooking);
                }

        }

        recordStore.closeRecordStore();

        return current;
    }

    public void backup(Object obj) {

        if (!isRunning()) {
            try {
                start();
            }
            catch(IllegalThreadStateException ex) { } // TODO, find a better way
        }
        addToInbox(obj);

    }

    public void remove(Object obj) {

            Object bookingId = helper.getObjectId(obj);
            Integer i = (Integer)table.get(bookingId);

            try {
                RecordStore recordStore = RecordStore.openRecordStore(rmsName, true);
                recordStore.deleteRecord(i.intValue());
                recordStore.closeRecordStore();
            }
            catch(Exception ex) {
                // this should really never happen
                ex.printStackTrace();
            }

    }

    private Hashtable table = new Hashtable();

    public void process(Object obj) throws Exception {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        helper.saveObject(baos, obj);
        byte[] b = baos.toByteArray();


        RecordStore recordStore = RecordStore.openRecordStore(rmsName, true);

        int size = recordStore.getSizeAvailable();

        if (size<=b.length) {

            recordStore.closeRecordStore();

            // if this fails, god knows what will happen
            helper.rmsSaveFailed();

            // just in case we save the current record to RSM again
            // should not need this if the save worked
            // TODO: inbox.insertElementAt(booking, 0);

            // if the rms does not have enough space for a single booking
            // then we are screwed
        }
        else {
            Object bookingId = helper.getObjectId(obj);
            Integer i = (Integer)table.get(bookingId);
            if (i==null) {
                Integer newRecordId = new Integer( recordStore.addRecord(b, 0, b.length) );
                table.put(bookingId, newRecordId);
            }
            else {
                recordStore.setRecord( i.intValue() , b, 0, b.length);
            }

            recordStore.closeRecordStore();
        }

    }

}
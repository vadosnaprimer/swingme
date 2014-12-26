package net.yura.mobile.io;

import java.util.Vector;
import java.util.Hashtable;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import java.io.InputStream;
import java.io.OutputStream;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public class RMSBackup extends QueueProcessorThread {

    public interface RMSBackupHelper {
        Object getObjectId(Object objToSave);
        Object loadObject(InputStream is) throws Exception;
        void saveObject(OutputStream out, Object obj) throws Exception;
        void rmsSaveFailed(String error);
    }


    private String rmsName;
    private RMSBackupHelper helper;

    public RMSBackup(RMSBackupHelper help,String n) {
        super("RMSBackup-"+n);
        rmsName = n;
        helper = help;
    }

    public void clearBackup() {
        try {
            RecordStore.deleteRecordStore(rmsName);
        }
        catch (Exception ex) {
            //#debug info
            Logger.info("can not del RMS");
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

                RecordEnumeration re = recordStore.enumerateRecords(null, null, false);
                while (re.hasNextElement()) {

                    int id = re.nextRecordId();
                    try {
                        // Get an input stream for the data in the record store.
                        ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(id));
                        // as we now have bookings in RMS, we need to update the bookingID->rmsID lookup table
                        Object currentBooking = helper.loadObject(bais);
                        table.put( helper.getObjectId(currentBooking), new Integer(id) );
                        current.addElement(currentBooking);
                    }
                    catch(Exception ex) {
                        // we can not read this record, lets remove it from the store!
                        recordStore.deleteRecord(id);

                        //#debug warn
                        Logger.warn("Error loading RMS: " + rmsName + " record id=" + id, ex);
                    }
                }

        }

        recordStore.closeRecordStore();

        return current;
    }

    public void backup(Object obj) {
        makeSureIsRunning();
        addToInbox( new ServiceLink.Task("save", obj) );
    }

    public void remove(Object objId) {
        makeSureIsRunning();
        addToInbox( new ServiceLink.Task("del", objId) );
    }

    private void makeSureIsRunning() {
        if (!isRunning()) {
            try {
                start();
            }
            catch(IllegalThreadStateException ex) {
              Logger.info(null, ex);
            } // TODO, find a better way
        }
    }

    private Hashtable table = new Hashtable();

    public void process(Object param) throws Exception {

        ServiceLink.Task task = (ServiceLink.Task)param;
        Object obj = task.getObject();
        String method = task.getMethod();

        if ("del".equals(method)) {

            //Object bookingId = helper.getObjectId(obj);
            Integer i = (Integer)table.remove(obj);

            if (i!=null) {
                RecordStore recordStore = RecordStore.openRecordStore(rmsName, true);
                recordStore.deleteRecord(i.intValue());
                recordStore.closeRecordStore();

                //System.out.println("[RMSBackup] del done: "+obj);
            }
            else {
                //this problem happens when the initial sync is happening,
                //and there is lots of saves happening as lots of new booking are loaded onto the client,
                //if at this point a new booking is created and saved,
                //when it actually gets saved it has already got its realID from the server,
                //and so no booking with the fakeID can be found in the RMS.
                //this will be solved when RMS saving is made super fast!

                System.err.println("[RMSBackup] ERROR: can not del "+obj+" from "+table);
            }
        }
        else if ("save".equals(method)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            helper.saveObject(baos, obj);
            byte[] b = baos.toByteArray();


            RecordStore recordStore = RecordStore.openRecordStore(rmsName, true);

            int size = recordStore.getSizeAvailable();

            if (size<=b.length) {

                recordStore.closeRecordStore();

                // if this fails, god knows what will happen
                helper.rmsSaveFailed("no space: availableSize="+size+" recordSize="+b.length);

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

                //System.out.println("[RMSBackup] save done: "+obj);
            }
        }
        else {
            System.out.println("unknwon method in RMSBackup queue: "+method);
        }
    }

}

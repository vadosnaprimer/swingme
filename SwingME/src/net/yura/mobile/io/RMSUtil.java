package net.yura.mobile.io;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public class RMSUtil {

	public static void save(String name,Object vals) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {

		// Delete the file in the recordStore
                try {
                    RecordStore.deleteRecordStore(name);
                }
                catch (Exception ex) {
                    //#debug info
                    Logger.info("can not del RMS, this should not happen", ex);
                }

		// Open the record store.
		RecordStore recordStore = RecordStore.openRecordStore(name, true);

		// Create a byte array output stream to write data into.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Save the game data into the byte array
		save(baos,vals);

		// Get the byte array and write it into the record store.
		byte[] b = baos.toByteArray();

		recordStore.addRecord(b, 0, b.length);

		// Close the record store.
		recordStore.closeRecordStore();
	}

	public static Object load(String name) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {

                Object vals=null;

		// Open the record store.
		RecordStore recordStore = RecordStore.openRecordStore(name, true);

		// Ensure the record store is not empty.
		if (recordStore.getNumRecords() != 0) {
			RecordEnumeration re = recordStore.enumerateRecords(null, null, false);

			if (re.hasNextElement()) {
				// Get an input stream for the data in the record store.
				int id = re.nextRecordId();
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(id));

                                //#mdebug info
                                if (vals!=null) {
                                    System.err.println("vals already has a value: "+vals);
                                }
                                //#enddebug

				vals = load(bais);

			}
		}

		// Close the record store.
		recordStore.closeRecordStore();

                return vals;
	}


	public static Object load(InputStream is) throws IOException {
            BinUtil util = new BinUtil();
            return util.load(is);
	}


	public static void save(OutputStream os,Object names) throws IOException {
            BinUtil util = new BinUtil();
            util.save(os, names);
	}

}

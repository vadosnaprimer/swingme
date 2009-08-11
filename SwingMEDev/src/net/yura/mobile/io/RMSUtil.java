package net.yura.mobile.io;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * @author Yura Mamyrin
 */
public class RMSUtil {

	public static void save(String name,Vector vals) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {

		// Delete the file in the recordStore
                try {
                    RecordStore.deleteRecordStore(name);
                }
                catch (Exception ex) {
                    //ex.printStackTrace();
                    System.out.println("can not del RMS");
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

	public static Vector load(String name) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {

            Vector vals=null;

		// Open the record store.
		RecordStore recordStore = RecordStore.openRecordStore(name, true);

		// Ensure the record store is not empty.
		if (recordStore.getNumRecords() != 0) {
			RecordEnumeration re = recordStore.enumerateRecords(null, null, true);

			if (re.hasNextElement()) {
				// Get an input stream for the data in the record store.
				int id = re.nextRecordId();
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(id));

				vals = load(bais);

			}
		}

		// Close the record store.
		recordStore.closeRecordStore();

                // avoid any nullpointers
                return vals==null?new Vector():vals;
	}


	public static Vector load(InputStream is) throws IOException {

                Vector names = new Vector();

                DataInputStream dis = new DataInputStream(is);

                int num = dis.readInt();


                for (int c=0;c<num;c++) {
                        String name = dis.readUTF();
                        names.addElement(name);
                }

                return names;
	}


	public static void save(OutputStream os,Vector names) throws IOException {

                DataOutputStream dos = new DataOutputStream(os);

                dos.writeInt(names.size());

                for (int c=0;c<names.size();c++) {

                        dos.writeUTF( (String)names.elementAt(c) );

                }

	}

}

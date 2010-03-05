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

package org.me4se.psi.j2se.rms;

import javax.microedition.rms.*;

import org.me4se.impl.rms.AbstractRecordStore;

import java.util.*;
import java.io.*;

public class RecordStoreImpl extends AbstractRecordStore  {

	public static String decode(String query){
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < query.length(); i++){
			if(query.charAt(i) == '('){
				int cut = query.indexOf(')', i+1);
				while(true){
					int c2 = query.indexOf('-', i+1);
					if(c2 == -1 || c2 >= cut){
						c2 = cut;
					}
					int code = Integer.parseInt(query.substring(i+1, c2), 36);
					sb.append((char) code);
					if(c2 == cut) break;
					i = c2;
				}
				i = cut;
			}
			else{
				sb.append(query.charAt(i));
			}
		}
			
		return sb.toString();
	}

	public static String encode(String query) {
		StringBuilder sb = new StringBuilder();
		
		boolean pending = false;
		
		for(int i = 0; i < query.length(); i++){
			char c = query.charAt(i);
			if(c == '-' || c == '_' || c == '.' || (c >= '0' && c <= '9') 
					|| (c >= 'a' && c <= 'z') 
					|| (c >= 'A' && c <= 'Z')){
				if(pending){
					sb.append(')');
					pending = false;
				}
				sb.append(c);
			}
			else {
				String uc = Integer.toString(c, 36);
				sb.append(pending ? '-' : '(');
				sb.append(uc);
				pending = true;
			}
		}
		if(pending){
			sb.append(')');
		}
		return sb.toString();
	}
	

	
	
	int version = 0;
	long lastModified = 0L;

	static final byte[] RECORD_INVALID = new byte[0];

	Vector records;
	File file;

	// If Applet, set this variable to "null" explicitly.

	// The directory to the RecordStore home directory can 
	// be specified by e.g. -Drms.home="."

	public static File rmsDir = null;//new File("."); // null for applet

	boolean isApplet() {
		return rmsDir == null;
	}


	private void changeVersion() {
		version++;
		lastModified = System.currentTimeMillis();
	}

	private void writeToFile() throws RecordStoreException {
		//System.out.println ("Writing records to file...");

		if (isApplet())
			return;

		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));

			dos.writeInt(version);
			dos.writeLong(lastModified);
			int cnt = records.size();
			dos.writeInt(cnt);

			for (int i = 0; i < cnt; i++) {
				Object obj = records.elementAt(i);

				if (obj == RECORD_INVALID) {
					dos.writeInt(-2);
				} else if (obj == null) {
					dos.writeInt(-1);
				} else {
					byte[] buffer = (byte[]) obj;
					dos.writeInt(buffer.length);
					dos.write(buffer, 0, buffer.length);
				}
			}

			dos.flush();
			dos.close();
		} catch (IOException ioe) {
			throw new RecordStoreException("Error writing Records to file!");
		}
		//System.out.println ("finished.");
	}

	public void open(String recordStoreName, boolean create) throws RecordStoreException {

		// Moved from the static block into open() in order to avoid
		// the creation of a rms/ dir although rms is not used in a MIDlet.
	
		refCount++;

		//System.out.println("RecordStore.openRecordStore("+recordStoreName+", "+create + "); refCount: "+refCount);

		if (records != null)
			return;

		this.recordStoreName = recordStoreName;

		if (isApplet()) {
			if (!create) {
				refCount = 0;
				throw new RecordStoreNotFoundException();
			}
			records = new Vector();
		} 
		else {
            rmsDir.mkdirs();
            
			file = new File(rmsDir, encode(this.recordStoreName) + ".rms");

			try {
				DataInputStream dis = new DataInputStream(new FileInputStream(file));

				version = dis.readInt();
				lastModified = dis.readLong();
				int count = dis.readInt();
				records = new Vector();

				for (int i = 0; i < count; i++) {
					int length = dis.readInt();
					if (length >= 0) {
						byte[] buffer = new byte[length];
						dis.readFully(buffer, 0, length);
						records.addElement(buffer);
					} else if (length == -2) {
						records.addElement(RECORD_INVALID);
					} else if (length == -1) {
						records.addElement(null);
					}
				}
				dis.close();
			} catch (Exception ioe) {

				if (!create) {
					refCount = 0;
					throw new RecordStoreNotFoundException();
				}

				records = new Vector();
			
				writeToFile();
			}
		}
	}

	public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {

		checkOpen();

		if (data == null)
			records.addElement(null);
		else {
			byte[] newData = new byte[numBytes];
			System.arraycopy(data, offset, newData, 0, numBytes);
			records.addElement(newData);
		}

		changeVersion();

		if (listeners != null) {
			for (int i = 0; i < listeners.size(); i++) {
				((RecordListener) listeners.elementAt(i)).recordAdded(this, records.size());
			}
		}

		writeToFile();

		return records.size();
	}

	public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {

		if (refCount > 0)
			refCount--;
		
		writeToFile();
	}

	public void deleteRecordStoreImpl() throws RecordStoreException {
		if (refCount > 0){
			//System.out.println("!!! Deleting open RecordStore; refCount: "+refCount);
		}
		
		if (!isApplet() && !file.delete()){
			throw new RecordStoreException("Cannot delete Store " + file.getName());
		}
		
		records = null;
	}

	public void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {

		checkId(recordId);
		records.setElementAt(RECORD_INVALID, recordId - 1);

		writeToFile();

		changeVersion();

		if (listeners != null) {
			for (int i = 0; i < listeners.size(); i++) {
				((RecordListener) listeners.elementAt(i)).recordDeleted(this, recordId);
			}
		}
	}

	public long getLastModified() throws RecordStoreNotOpenException {
		checkOpen();
		return lastModified;
	}

	public String getName() throws RecordStoreNotOpenException {
		checkOpen();
		return recordStoreName;
	}

	public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
		checkOpen();
		return records.size() + 1;
	}

	public int getNumRecords() throws RecordStoreNotOpenException {
		checkOpen();
		int result = 0;

		for (int i = 0; i < records.size(); i++) {
			Object data = records.elementAt(i);
			if (data != RECORD_INVALID)
				result++;
		}
		return result;
	}

	public byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {

		checkId(recordId);
		byte[] buffer = (byte[]) records.elementAt(recordId - 1);

		if (buffer == RECORD_INVALID)
			throw new InvalidRecordIDException("Record ID " + recordId + " is already deleted");

		return (buffer);
	}

	public int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, ArrayIndexOutOfBoundsException {

		byte[] data = getRecord(recordId);
		if (data == RECORD_INVALID)
			throw new InvalidRecordIDException("Record ID " + recordId + " is already deleted.");

		System.arraycopy(data, 0, buffer, offset, data.length);
		return data.length;
	}

	public int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {

		return getRecord(recordId).length;
	}

	public int getSize() throws RecordStoreNotOpenException {
		checkOpen();

		int size = 0;

		// version
		size += 4;
		// last modified
		size += 8;

		for (int i = 0; i < records.size(); i++) {
			Object obj = records.elementAt(i);
			// size of buffer
			size += 4;
			// the buffer
			if (obj != null)
				size += ((byte[]) obj).length;

		}

		return size;
	}

	public int getSizeAvailable() throws RecordStoreNotOpenException {
		return Integer.MAX_VALUE;
	}

	public int getVersion() throws RecordStoreNotOpenException {
		checkOpen();
		return version;
	}



	public void setRecord(int recordId, byte[] data, int offset, int numBytes) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {

		checkId(recordId);

		byte[] oldData = getRecord(recordId);

		if (oldData == RECORD_INVALID)
			throw new InvalidRecordIDException("Record " + recordId + " is already deleted.");

		if (data != null) {
			byte[] newData = new byte[numBytes];

			System.arraycopy(data, offset, newData, 0, numBytes);

			records.setElementAt(newData, recordId - 1);
		} 
		else {
			records.setElementAt(null, recordId - 1);
		}

		writeToFile();

		changeVersion();

		if (listeners != null) {
			for (int i = 0; i < listeners.size(); i++) {
				((RecordListener) listeners.elementAt(i)).recordChanged(this, recordId);
			}
		}
	}

	public void setMode(int authmode, boolean writable) throws RecordStoreException {
		System.out.println("RecordStore.setMode() called with no effect!");
	}
}
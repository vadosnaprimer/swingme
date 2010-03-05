package org.me4se.psi.j2se.rms;

import java.io.File;
import java.util.Enumeration;

import javax.microedition.midlet.ApplicationManager;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.me4se.Initializer;
import org.me4se.impl.rms.AbstractRecordStore;

public class RecordStoreInitializer implements Initializer {

	public void initialize(ApplicationManager am) {
		
        if (am.applet != null ) {
            RecordStoreImpl.rmsDir = null;
        }
        else {
        	RecordStoreImpl.rmsDir = new File(am.getProperty("rms.home", ".rms"));

        	if(RecordStoreImpl.rmsDir.exists()){
        		String[] files = RecordStoreImpl.rmsDir.list();
            
        		if(files != null){
        			for(int i = 0; i < files.length; i++){
        				String fileName = files[i];
        				if(fileName.endsWith(".rms")){
        					String name = RecordStoreImpl.decode(fileName.substring(0, fileName.length()-4));
        					AbstractRecordStore.recordStores.put(name, new RecordStoreImpl());
        				}
        			}
        		}
            }
		}
	}
}
package org.me4se.psi.j2se.rms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Enumeration;

import javax.microedition.midlet.ApplicationManager;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import javax.swing.JOptionPane;
import org.me4se.Initializer;
import org.me4se.impl.rms.AbstractRecordStore;

public class RecordStoreInitializer implements Initializer {

	public void initialize(ApplicationManager am) {
		
            if (am.applet != null ) {
                RecordStoreImpl.rmsDir = null;
            }
            else {

        	RecordStoreImpl.rmsDir = new File(am.getProperty("rms.home", ".rms"));

                if(RecordStoreImpl.rmsDir.exists()) {

                        try {
                            final File lockFile = new File( RecordStoreImpl.rmsDir , "in.use");
                            if (lockFile.exists()) {
                                System.err.println("[RecordStoreInitializer] in.use file found! "+lockFile);
                                // on OS X a locked file can still be deleted, so we have to just crash out
                                if (System.getProperty("os.name").toLowerCase().indexOf( "mac" ) >= 0) {
                                    throw new Exception("in.use file found");
                                }
                                lockFile.delete();
                            }
                            FileOutputStream lockFileOS = new FileOutputStream(lockFile); // create the file
                            lockFileOS.close();
                            final FileChannel lockChannel = new RandomAccessFile(lockFile,"rw").getChannel();
                            final FileLock lock = lockChannel.tryLock();
                            if (lock==null) throw new Exception("Unable to obtain lock");


                            Runtime.getRuntime().addShutdownHook(new Thread() {
                                // destroy the lock when the JVM is closing
                                @Override
                                public void run() {
                                    try {
                                        lock.release();
                                        lockChannel.close();
                                        lockFile.delete();
                                    }
                                    catch (Throwable th) { th.printStackTrace(); } // this should not throw
                                }
                            });



                        }
                        catch (Exception e) {
                            JOptionPane.showMessageDialog(null,"An instance of ME4SE is already running.","Warning",JOptionPane.WARNING_MESSAGE);
                            //e.printStackTrace();
                            System.exit(0);
                        }



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
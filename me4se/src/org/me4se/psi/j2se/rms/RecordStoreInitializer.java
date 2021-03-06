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

            // we have already initialized, do not need to do it again!
            if (RecordStoreImpl.rmsDir!=null) {
                return;
            }

            if (am.applet != null ) {
                RecordStoreImpl.rmsDir = null;
            }
            else {

        	RecordStoreImpl.rmsDir = new File(ApplicationManager.getProperty("rms.home", ".rms"));

                boolean hasRms;
                try {
                    hasRms = RecordStoreImpl.rmsDir.exists();
                }
                catch (Throwable th) {
                    hasRms = false;
                }

                if(hasRms) {

                    // TODO
                    // TODO This only happens at start up, but if we make the rms dir diring running of the app, we do NOT put a in.use file there
                    // TODO

                        try {
                            final File lockFile = new File( RecordStoreImpl.rmsDir , "in.use");
                            if (lockFile.exists()) {
                                System.err.println("[RecordStoreInitializer] in.use file found! "+lockFile);
                                // on OS X a locked file can still be deleted, so we have to just crash out
                                if (System.getProperty("os.name").toLowerCase().indexOf( "mac" ) >= 0) {
                                    int result = JOptionPane.showConfirmDialog(null, "ME4SE may already be running, are you sure you want to run anyway?", "Question?", JOptionPane.YES_NO_OPTION);
                                    if (result==JOptionPane.NO_OPTION) {
                                        System.exit(0);
                                    }
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
                                //@Override
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
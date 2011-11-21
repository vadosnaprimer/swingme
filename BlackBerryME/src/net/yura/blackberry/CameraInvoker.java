package net.yura.blackberry;

import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.EventInjector;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.yura.mobile.gui.Midlet;

/**
 * @author Anton
 * This class invokes the Native BlackBerry camera. It extends net.rim.device.api.ui.container.MainScreen because we
 * are interested in knowing when the app takes focus again after a picture has either been taken or the camera has been
 * dismissed without a picture being taken.
 * 
 * onExposed() is called when the app takes focus. The result (either the file path or null is no picture was taken) is 
 * sent to AddPhotosViewController and the main screen closes itself.
 */
public class CameraInvoker extends MainScreen implements FileSystemJournalListener {
	
	long _lastUSN;
	String capturedImgPath = null;

	public CameraInvoker() {
		super();
		UiApplication.getUiApplication().addFileSystemJournalListener(this);
		_lastUSN = FileSystemJournal.getNextUSN();
		
		show();
	}
	
	protected void onExposed(){
		super.onExposed();
		close();
		Midlet.getMidlet().onResult(0, -1, capturedImgPath);
	}
	
	private void show() {
		Application.getApplication().invokeLater (new Runnable() {
    	    public void run() {
    	    	UiApplication.getUiApplication().pushScreen(CameraInvoker.this);
    	    }
		});		
		Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA, new CameraArguments());
	}

	public void fileJournalChanged() {
		long nextUSN = FileSystemJournal.getNextUSN();
		String msg = null;
		String path = null;
		for (long lookUSN = nextUSN - 1; lookUSN >= _lastUSN && msg == null; --lookUSN) {
			FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
			if (entry == null) {
				break;
			}
			path = entry.getPath();
			if (entry.getEvent() == FileSystemJournalEntry.FILE_ADDED) {
				switch (entry.getEvent()) {
				case FileSystemJournalEntry.FILE_ADDED:
					if (path.indexOf(".jpg") > 0) {
						capturedImgPath = path;
						UiApplication.getUiApplication().removeFileSystemJournalListener(this);
						closeCamera();
					}
					break;
				}
			}
		}
		_lastUSN = nextUSN;
	}

	
	
	private void closeCamera() {
		try {
			EventInjector.KeyEvent inject = new EventInjector.KeyEvent(EventInjector.KeyEvent.KEY_DOWN, Characters.ESCAPE,  50);
			inject.post();
			inject.post();
		} catch (Exception e) {
		}
	}

}

package net.yura.blackberry;


import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.EventInjector;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.logging.Logger;

public class CameraInvoker implements com.badoo.mobile.BlackBerryNativeScreen , FileSystemJournalListener {
	
	long _lastUSN;
	String capturedImgPath = "";
	byte[] ImageData = null;
	String full_path;

	public CameraInvoker() {
		super();
		UiApplication.getUiApplication().addFileSystemJournalListener(this);
		_lastUSN = FileSystemJournal.getNextUSN();
	}
	
	public void show() {
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
						Logger.info("New image captured with camera: " + path);
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
		Midlet.getMidlet().onResult(-1, capturedImgPath);
		try {
			EventInjector.KeyEvent inject = new EventInjector.KeyEvent(EventInjector.KeyEvent.KEY_DOWN, Characters.ESCAPE,  50);
			inject.post();
			inject.post();
		} catch (Exception e) {
		}
	}

	public Object getResult() {
		return null;
	}
}

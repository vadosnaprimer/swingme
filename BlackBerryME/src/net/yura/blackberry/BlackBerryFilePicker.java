package net.yura.blackberry;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.picker.FilePicker;
import net.yura.mobile.gui.Midlet;

/**
 * @author Anton
 */
public class BlackBerryFilePicker extends MainScreen {
	
	FilePicker fp = FilePicker.getInstance();
	
	public BlackBerryFilePicker() {
		super();
		
		show();
	}
	
	private void show() {		
         FilePickListener fileListener = new FilePickListener();
         fp.setListener(fileListener);
         fp.setPath(System.getProperty("fileconn.dir.photos"));
         fp.setFilter(".jpg");
         //fp.setView(FilePicker.VIEW_PICTURES);              
         fp.show();
	}

   class FilePickListener implements FilePicker.Listener 
    {   
        public void selectionDone(String str)
        {        	
        	close(-1, str);
        }
    }
   
   public void close() {
       close(-1, null);
   }
   
   private void close(int responseCode, Object res) {
       Midlet.getMidlet().onResult(-1, responseCode, res);
       super.close();
   }

}

package net.yura.blackberry;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.logging.Logger;

public class BlackBerryOptionPane extends OptionPane {

	public static void init() {
		OptionPane.optionPaneClass = BlackBerryOptionPane.class;
	}
	
	
    public void setVisible(boolean b) {
    	Object obj = getMessage();
        if (obj == null || obj instanceof String) {
            if (b) {
                
            	final Button[] buttons = getOptions();
            	final String[] options = new String[buttons.length];
            	int defaultChoice=-1; // ?????? default
            	for (int c=0;c<buttons.length;c++) {
            		options[c] = buttons[c].getText();
            		if (buttons[c]==getInitialValue()) {
            			defaultChoice = c;
            		}
            	}
            	final int d=defaultChoice;
            	//final int result;
            	
            	// if the message is null, try use the title
            	if (obj==null || "".equals(obj) ) {
            		obj = getTitle();
            	}
            	
            	final String string = (String)obj;
            	
            	Application.getApplication().invokeLater (new Runnable() {
            	    public void run() {
            	// synchronized with EventLock does not work, as when you click ok, the thread gets stuck 
            	//synchronized(Application.getEventLock()) {
            	    	try {
            	    		String command=null;
            	    		loop: while (true) { // this will keep bringing the dialog up until the user selects a valid option
		            	    	int result = Dialog.ask( string, (Object[])options, d );
		            	    	if (result<0) { // if the user dismissed the dialog with the 'back' key we get -1
		            	    		for (int c=0;c<buttons.length;c++) {
		            	    			if (buttons[c].getMnemonic()==KeyEvent.KEY_END || buttons[c].getMnemonic()==KeyEvent.KEY_SOFTKEY2) {
		            	    				command = buttons[c].getActionCommand();
		            	    				break loop;
		            	    			}
		            	    		}
		            	    	}
		            	    	else {
		            	    		command = buttons[result].getActionCommand();
		            	    		break loop;
		            	    	}
            	    		}
	            	    	ActionListener al = getActionListener();
	            	    	if (al!=null) {
		            	    	al.actionPerformed( command );
	            	    	}
            	    	}
            	    	catch(Exception ex) {
            	    		//#debug warn
            	    		Logger.warn(ex);
            	    	}
            	    	catch(Error er) {
            	    		//#debug warn
            	    		Logger.warn(er);
            	    	}
            	//}
            	    }
            	});

            	// for some crazy BlackBerry reason it does not repaint the screen unless i do this
            	Application.getApplication().invokeLater (new Thread() );
            	
            }
            else {
            	//#debug warn
                Logger.warn("why is this happening???? setVisible(false) in BlackBerryOptionPane");
                super.setVisible(b); // in case something went wrong with opening the native OptionPane, we want to be able to close it
            }
        }
        else {
            super.setVisible(b);
        }
    }
}

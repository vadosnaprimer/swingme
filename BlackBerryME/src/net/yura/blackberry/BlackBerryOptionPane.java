package net.yura.blackberry;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.logging.Logger;

public class BlackBerryOptionPane extends OptionPane {

	public static void init() {
		OptionPane.optionPaneClass = BlackBerryOptionPane.class;
	}
	
	
    public void setVisible(boolean b) {
        if (getMessage() instanceof String) {
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
            	
            	Application.getApplication().invokeLater (new Runnable() {
            	    public void run() {
            	// synchronized with EventLock does not work, as when you click ok, the thread gets stuck 
            	//synchronized(Application.getEventLock()) {
            	    	int result = Dialog.ask( (String)getMessage(), (Object[])options, d );
            	    	
            	    	ActionListener al = getActionListener();
            	    	if (al!=null) {
            	    		al.actionPerformed( buttons[result].getActionCommand() );
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
            }
        }
        else {
            super.setVisible(b);
        }
    }
	
}

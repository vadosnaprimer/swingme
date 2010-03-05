package org.me4se.impl;

import java.io.IOException;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.ApplicationManager;
import javax.microedition.midlet.MIDlet;

import org.me4se.JadFile;

/**
 * This is a helper class for the ApplicationManager that allows the user 
 * to select one of several MIDlets from a MIDlet suite. Once a MIDlet is 
 * chosen, the new MIDlet is instantiated an passed to the ApplicationManager 
 * as the active MIDlet.
 */

public class MIDletChooser extends MIDlet implements CommandListener { //, Runnable {

    static boolean firstLaunch = true;
    
    
	// set this to 0 in order to remove the Evaluation message in the 
	// info Alert screen
    private static final String INFO_TITLE = "MobileRunner J2ME "+ApplicationManager.ME4SE_VERSION_NUMBER;
    private static final String INFO_TEXT =
		//"Please visit http://www.me4se.org for further information about the device emulation.";
		//"J2ME Emulator (C) 2003, 2004, 2005 Stefan Haustein, Michael Kroll, Joerg Pleumann. For commercial use, please contact wap3.de";
		//	"J2ME Emulator (C) 2003 - 2006 Stefan Haustein, Michael Kroll, Joerg Pleumann. For commercial use, please contact contact@kroll-haustein.com";
		"This Emulator is protected by Copyright (C); contact: wap3 Technologies GmbH, mobilerunner@wap3.net; authors: S. Haustein, M. Kroll, J. Pleumann.";
	
	
	/**
	 * The list screen where the MIDlet names are displayed.
	 */
	private List list = new List("Choose MIDlet", Choice.IMPLICIT);

    private Alert infoAlert;
    
	/**
	 * The launch command.
	 */
	private static final Command CMD_START = new Command("Start", Command.SCREEN, 0);

    /**
     * The launch command.
     */
    private static final Command CMD_RESTART = new Command("Restart", Command.SCREEN, 0);


    /**
     * The "hard" launch command.
     */
    private static final Command CMD_RELOAD = new Command("Reload", Command.SCREEN, 0);

    
	/**
	 * The info command.
	 */
	private static final Command CMD_INFO = new Command("Info", Command.SCREEN, 0);


	/**
	 * Creates the MIDlet.
	 */
	public MIDletChooser() {
		JadFile jadFile = ApplicationManager.getInstance().jadFile;

        try {
            Image img = Image.createImage("/wap3-logo.png");
            infoAlert = new Alert(INFO_TITLE, INFO_TEXT, img, AlertType.INFO);
        } 
        catch (IOException ex) {
           ex.printStackTrace();
           throw new RuntimeException(ex.toString());
        }

        
        
        if(jadFile.getMIDletCount() == 1){
            infoAlert.addCommand(CMD_RESTART);
            infoAlert.setCommandListener(this);
        }
        
		for (int i = 1; i <= jadFile.getMIDletCount(); i++) {
			list.append(jadFile.getMIDlet(i).getName(), null);
		}

		//list.addCommand(LAUNCH);
		list.addCommand(CMD_INFO);
		list.setCommandListener(this);
	}

	/**
	 * Is called when the MIDletChooser is started, activates the list screen.
	 */
	protected void startApp() {
		if(ApplicationManager.getInstance().getBooleanProperty("me4se.autorestart", false)){
//        if ("com.google.googlenav.GoogleNav".equals(ApplicationManager.manager.jadFile.getMIDlet(1).getClassName())){
            commandAction(CMD_RESTART, infoAlert);
        }
        else {
            Display.getDisplay(this).setCurrent(list.size() == 1 ? (Displayable) infoAlert : list);
        }
      //  new Thread(this).start();
	}

	/**
	 * Does nothing.
	 */
	protected void pauseApp() {
	}

	/**
	 * Does nothing.
	 */
	protected void destroyApp(boolean unconditional) {
	}

	/**
	 * Handles the LAUNCH command.
	 */
	public void commandAction(Command cmd, Displayable dsp) {
		
		if (cmd == CMD_START || cmd == List.SELECT_COMMAND || cmd == CMD_RESTART || cmd == CMD_RELOAD) {
			try {

			    Form launchForm = new Form("Terminated");
			    launchForm.append("If the Application does not restart automatically, please press the reload button.");
			    launchForm.addCommand(CMD_RELOAD);
			    launchForm.setCommandListener(this);
			    
                Display.getDisplay(this).setCurrent(launchForm);
                
				// Get the name of the selected MIDlet and create a MIDlet instance
				// for it.
                
                ApplicationManager manager = ApplicationManager.getInstance();
                
                if(cmd == CMD_RELOAD || 
                		(manager.applet != null 
                		 && !manager.getBooleanProperty("me4se.restartable", true)
                     && (!firstLaunch || manager.jadFile.getMIDletCount() == 1))){

                    manager.applet.getAppletContext().showDocument(manager.applet.getDocumentBase());
                }
                else {                
                    firstLaunch = false;
                    
                    int i = list.getSelectedIndex();
                    if(i < 0 || i >= list.size()){
                        i = 0;
                    }
                    
                    manager.launch(manager.jadFile, i + 1);
                }
			} catch (Exception error) {
				error.printStackTrace();
			}
		} 
        else if (cmd == CMD_INFO) {
            Display.getDisplay(this).setCurrent(infoAlert, list);
		}
	}
    
    /*
	public void run(){
        try {
            Thread.sleep(4000);
        }
        catch(Exception e){
            
        }
        if(!launching){
            commandAction(CMD_RESTART, infoAlert);
        }
    }
    */
}

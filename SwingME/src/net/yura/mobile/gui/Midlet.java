package net.yura.mobile.gui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public abstract class Midlet extends MIDlet {
	
	private DesktopPane rootpane;
	
	public Midlet() {

		rootpane = makeNewRootPane();
	
	}
	/**
	 * THIS METHOD WILL MAKE A NEW ROOTPANE
	 * but nothing can be set up here as things like getHeight wont work
	 */
	protected abstract DesktopPane makeNewRootPane();
	
	/**
	 * this will set up everything needed to start the
	 * app like the size and stuff
	 */
	protected abstract void initialize(DesktopPane rootpane);
	
	protected void destroyApp(boolean arg0) {

		rootpane.kill();
		
		Display.getDisplay(this).setCurrent((Displayable)null);
		
		notifyDestroyed();

	}


	protected void pauseApp() {
		// TODO 
		//System.out.println("pauseApp");
	}

	protected void startApp() {
		// TODO come out of pause
		//System.out.println("startApp");
	}

}

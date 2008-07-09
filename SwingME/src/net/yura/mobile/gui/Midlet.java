/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

/**
 * @author Yura Mamyrin
 */
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

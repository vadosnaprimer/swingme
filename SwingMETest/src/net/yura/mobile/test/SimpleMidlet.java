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

package net.yura.mobile.test;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;

/**
 * @author Yura Mamyrin
 */
public class SimpleMidlet extends Midlet implements ActionListener {

	private DesktopPane rootpane;
	
	protected DesktopPane makeNewRootPane() {
		return new DesktopPane(this,0,null);
	}
	
	protected void initialize(DesktopPane rp) {

		this.rootpane = rp;

		rootpane.setLookAndFeel( new MetalLookAndFeel() );
		
                Window mainWindow = new Window();
                
                mainWindow.setActionListener(this);
		mainWindow.setWindowCommand(1, new CommandButton("Exit","exit") );
                
		mainWindow.add( new Label("Hello World!") );
		mainWindow.setMaximum(true);
                mainWindow.setVisible(true);
	}

	public void actionPerformed(String actionCommand) {
		
		if ("exit".equals(actionCommand)) {
			
			rootpane.exit();
			
		}
		
	}

}

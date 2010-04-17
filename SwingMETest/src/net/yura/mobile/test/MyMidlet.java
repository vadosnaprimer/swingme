/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.test;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.DesktopPane;

/**
 * @author Yura Mamyrin
 */
public class MyMidlet extends Midlet {

	protected DesktopPane makeNewRootPane() {
		return new MainPane(this);
	}

	protected void initialize(DesktopPane rp) {
	    MainPane mainPane = (MainPane) rp;
	    mainPane.initialize();
	    mainPane.setMainSection(new MainTest(mainPane));
	}

}
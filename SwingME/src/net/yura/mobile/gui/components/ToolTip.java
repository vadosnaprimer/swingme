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

package net.yura.mobile.gui.components;

import net.yura.mobile.gui.DesktopPane;

/**
 * @author Yura Mamyrin
 */
public class ToolTip extends Label {

    private boolean waiting;
    private boolean showing;
    
    public void animate() throws InterruptedException {
        try {
	    waiting = true;
            wait(1000);
	    showing = true;
            DesktopPane.getDesktopPane().softkeyRepaint();
            wait(2000);
        }
        finally {
            if (showing) {
                showing = false;
                DesktopPane.getDesktopPane().fullRepaint();
            }
	    waiting = false;
        }
    }
    
    public String getDefaultName() {
        return "ToolTip";
    }

    public boolean isWaiting() {
	return waiting;
    }
    public boolean isShowing() {
	return showing;
    }
    
}

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

    private boolean isShowing;
    
    public void animate() throws InterruptedException {
        try {
            wait(1000);
            isShowing = true;
            DesktopPane.getDesktopPane().softkeyRepaint();
            wait(2000);
        }
        finally {
            isShowing = false;
            super.setText(null);
            DesktopPane.getDesktopPane().fullRepaint();
        }
    }
    
    public void setText(String text) {
        super.setText(text);
        DesktopPane.getDesktopPane().animateComponent(this);
    }
    
    public String getName() {
        return "ToolTip";
    }

    public boolean showToolTip() {
        return isShowing;
    }
    
}

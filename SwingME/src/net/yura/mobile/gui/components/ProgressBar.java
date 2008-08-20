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

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JProgressBar
 */
public class ProgressBar extends Component {

	protected boolean go;
	protected int wait;
	
	protected int loaded;
	protected int loading;
	
	
	// goes from 0 to loaded (inclusive)
	public ProgressBar() {

		wait = 50;
                loaded = 100;
	}
	
	public void paintComponent(Graphics g) {
            
                g.setColor(foreground);
		g.drawRect(0, 0, (width*loading)/loaded, height);

	}

	public void animate() {
		
		loading = 0;
		
		while (go) {
			
			repaint();
			wait(wait);
			
			if (loading == loaded) { loading=0; }
			else { loading ++; }
		}
		
	}

	public void start() {
		
		go = true;
		DesktopPane.getDesktopPane().animateComponent(this);
	}

	public void stop() {
		
		go = false;
	}

    public void workoutSize() {
        // TODO: what to put here?
        height = 20;
        width = 50;
    }

    public String getName() {
        return "ProgressBar";
    }
	
}

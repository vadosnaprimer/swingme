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

    /**
     * Creates a horizontal progress bar that displays a border but no progress string.
     * The initial and minimum values are 0, and the maximum is 100.
     * @see javax.swing.JProgressBar#JProgressBar() JProgressBar.JProgressBar
     * goes from 0 to loaded (inclusive)
     */
    public ProgressBar() {

            wait = 50;
            loaded = 100;
    }

    public void paintComponent(Graphics g) {

            g.setColor(foreground);
            
            if (go) {
                int thickness = 20;
                
                // pos goes from 0 to loaded/2
                int pos = (loading > loaded/2)? loaded-loading : loading;

                g.fillRect( ( (width-thickness) * pos)/ (loaded/2) , 0, thickness, height);
            }
            else {
                g.fillRect(0, 0, (width*loading)/loaded, height);
            }

    }

    /**
     *  goes from 0 to maximum, then goes back to 0 and start again
     */
    public void animate() throws InterruptedException {

            loading = 0;

            while (go) {

                    repaint();
                    wait(wait);

                    if (loading == loaded) { loading=0; }
                    else { loading ++; }
            }

    }

    /**
     * @param v TRUE to start the animation, FLASE to end the animation
     * @see javax.swing.JProgressBar#setIndeterminate(boolean) JProgressBar.setIndeterminate
     */
    public void setIndeterminate(boolean v) {

        if (v) {
		go = true;
		DesktopPane.getDesktopPane().animateComponent(this);
        }
        else {
                go = false;
        }
        
    }
        
    /**
     * @see javax.swing.JProgressBar#getMaximum() JProgressBar.getMaximum
     */
    public int getMaximum() {
        return loaded;
    }
    
    /**
     * @see javax.swing.JProgressBar#setMaximum(int) JProgressBar.setMaximum
     */
    public void setMaximum(int max) {
        loaded = max;
    }
    
    /**
     * @see javax.swing.JProgressBar#getValue() JProgressBar.getValue
     */
    public int getValue() {
        return loading;
    }
    
    /**
     * @param v The new value
     * @see javax.swing.JProgressBar#setValue(int) JProgressBar.setValue
     */
    public void setValue(int v) {
        loading = v;
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

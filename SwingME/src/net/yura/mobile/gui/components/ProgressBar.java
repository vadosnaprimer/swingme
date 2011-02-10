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

import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JProgressBar
 */
public class ProgressBar extends Component {

    protected boolean go;
    protected int wait;

    protected int loaded;
    protected int loading;

    private Sprite sprite;

    /**
     * Creates a horizontal progress bar that displays a border but no progress string.
     * The initial and minimum values are 0, and the maximum is 100.
     * @see javax.swing.JProgressBar#JProgressBar() JProgressBar.JProgressBar
     */
    public ProgressBar() {
            focusable = false;
            wait = 50;
            loaded = 100;
    }

    public ProgressBar(Sprite sp) {

            sprite = sp;

            wait = 50;
            loaded = sprite.getFrameSequenceLength()-1;

    }

    public void paintComponent(Graphics2D g) {

        if (sprite!=null) {

		g.drawSprite(sprite, loading, (width-sprite.getWidth())/2, (height-sprite.getHeight())/2);
        }
        else {

            g.setColor( getForeground() );

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
    }

    /**
     *  goes from 0 to loaded (inclusive), then goes back to 0 and start again
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
     * @param v TRUE to start the animation, FALSE to end the animation
     * @see javax.swing.JProgressBar#setIndeterminate(boolean) JProgressBar.setIndeterminate
     */
    public void setIndeterminate(boolean v) {

        if (v) {
		go = true;
		getDesktopPane().animateComponent(this);
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
    public Object getValue() {
        return new Integer(loading);
    }

    /**
     * @param v The new value
     * @see javax.swing.JProgressBar#setValue(int) JProgressBar.setValue
     */
    public void setValue(int v) {
        loading = v;
    }

    public void workoutMinimumSize() {

        if (sprite!=null) {
            width = sprite.getWidth();
            height = sprite.getHeight();
        }
        else {
            // TODO: what to put here?
            height = 20;
            width = 50;
        }
    }

    public String getDefaultName() {
        return "ProgressBar";
    }

    public void updateUI() {
        super.updateUI();
        sprite = (Sprite)theme.getProperty("sprite", Style.ALL);
    }

}

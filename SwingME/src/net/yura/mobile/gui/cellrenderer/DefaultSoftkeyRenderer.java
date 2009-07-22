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

package net.yura.mobile.gui.cellrenderer;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.plaf.Style;

/**
 * This method can render a normal commandButton
 * and also wraps any Menu it may have
 * @author Yura Mamyrin
 */
public class DefaultSoftkeyRenderer extends Label implements ListCellRenderer {

    private Image button;
    private Image bottomRight;
    private Image bottomLeft;
    
    private boolean top,left;
    private Button component;
    
    public DefaultSoftkeyRenderer() {
    }
    /**
     * if its just a normal commandButton we will draw it,
     * otehrwise we will use the Button to draw
     */
    public Component getListCellRendererComponent(List list, Object value, int index, boolean top, boolean left) {
        if (value==null) return null;

        this.top = top;
        this.left = left;
        component = ((CommandButton)value).getButton();

            // This is only used when component is NULL
            setText(value.toString());
            setHorizontalAlignment(left?Graphics.LEFT:Graphics.RIGHT);

        //text = (top?"top":"bottom") + " " + (left?"left":"right");
        return this;
    }

    public void paintComponent(Graphics2D g) {
        
        Image b=getImage();
        if (b!=null) {
            g.drawImage(b, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
        
        if (component==null) {
            super.paintComponent(g);
        }
        else {
            // HACK
            if (component instanceof Menu) {
                ((Menu)component).setArrowDirection( (top?Graphics.BOTTOM:Graphics.TOP) | (left?Graphics.RIGHT:Graphics.LEFT) );
            }
            
            component.workoutSize();
            
            int x = left?0:width-component.getWidth();
            int y = (height - component.getHeight()) / 2;

            g.translate(x, y);
            component.paintComponent(g);
            g.translate(-x, -y);
        }

    }
    public void workoutSize() {
        
        Image b=getImage();
        
        if (b==null) {
            height = getFont().getHeight();
            width = DesktopPane.getDesktopPane().getWidth()/2 - 10;
        }
        else {
            height = b.getHeight();
            width = b.getWidth();
        }
        
    }
    
    private Image getImage() {
        Image b = button;
        
        if (!top && !left && bottomRight!=null) {
            b = bottomRight;
        }
        else if (!top && left && bottomLeft!=null) {
            b = bottomLeft;
        }
        return b;
    }

    public String getName() {
        return "SoftkeyRenderer";
    }
    public void updateUI() {
        super.updateUI();
        
        Style theme = DesktopPane.getDefaultTheme(this);
        
        button = (Image)theme.getProperty("button", Style.ALL);
        bottomRight = (Image)theme.getProperty("bottomRight", Style.ALL);
        bottomLeft = (Image)theme.getProperty("bottomLeft", Style.ALL);
    }

}

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
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 */
public class DefaultSoftkeyRenderer extends Label implements ListCellRenderer {

    private Image button;
    private Image bottomRight;
    private Image bottomLeft;
    
    private boolean top,left,menu;
    
    public DefaultSoftkeyRenderer() {
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean top, boolean left) {
        if (value==null) return null;

        this.top = top;
        this.left = left;
        
        setText(value.toString());
        menu = ((CommandButton)value).getMenu()!=null;
        // TODO, not JUST menu should allow for a icon
        if (menu) {
            setIcon( ((CommandButton)value).getMenu().getIcon() );
        }
        setHorizontalAlignment(left?Graphics.LEFT:Graphics.RIGHT);
        //text = (top?"top":"bottom") + " " + (left?"left":"right");
        return this;
    }

    public void paintComponent(Graphics g) {
        
        Image b=getImage();
        if (b!=null) {
            g.drawImage(b, 0, 0, Graphics.TOP | Graphics.LEFT);
        }
        
        super.paintComponent(g);
        
        // draw a arrow as this is a menu
        if (menu) {
            int w = getFont().getHeight();
            int x = getCombinedWidth(getText(), getIcon()) + padding*2;
            int y = (height-(w/2))/2;
            
            if (!left) { x = width-x-w; }
            if (top) {
                ScrollPane.drawDownArrow(g, x, y, w, w/2);
            }
            else {
                ScrollPane.drawUpArrow(g, x, y, w, w/2);
            }
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
        bottomRight = (Image)theme.getProperty("bottomright", Style.ALL);
        bottomLeft = (Image)theme.getProperty("bottomleft", Style.ALL);
    }

}

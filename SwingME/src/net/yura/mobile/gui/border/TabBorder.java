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

package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class TabBorder implements Border {

    private int orient;
    private int color1;
    private int color2;
    private int color3;
    private int color4;
    
    public TabBorder(int a) {
        color1 = 0x00FFFFFF;
        color2 = 0x00AAAAAA;
        color3 = 0x00000000;
        color4 = 0x00EEEEEE;
        orient = a;
    }
    
    public void paintBorder(Component c, Graphics g, int width, int height) {
        
              int x=-2;
              int y=-2;
              int xx = width +1;
              int yy = height +1;

             g.setColor (color1);
             
             switch (orient)
             {
               case Graphics.TOP:
               {
 
                   
                 g.drawLine (x + 3, y, xx-1, y);
                 g.drawLine (x, y + 3, x, yy);
                 g.drawLine (x, y + 3,x + 3, y);

                 g.setColor (color2);
                 g.drawLine (xx - 1, y + 1, xx - 1, yy);
                 g.setColor (color3);
                 g.drawLine (xx, y + 2, xx, yy);
                 
                 
                     g.setColor(color4);
                     g.drawLine(-1,height+1,width-1,height+1);

                 
               }  break;
               case Graphics.BOTTOM:
               {
                   
                 g.drawLine (x, y, x, yy-3);

                 g.setColor (color2);
                 g.drawLine (xx - 1, y, xx - 1, yy - 1);
                 g.drawLine (x + 3, yy-1, xx-1, yy-1);
                 g.drawLine (x, yy-4, x + 4, yy);

                 g.setColor (color3);
                 g.drawLine (xx, y, xx, yy);
                 g.drawLine (x + 3, yy, xx-1, yy);
                 g.drawLine (x, yy-3, x + 3, yy);
                 

                     g.setColor(color4);
                     g.drawLine(-1,-2,width-1,-2);

                 
                 
               } break;
               case Graphics.LEFT:
               {

                 g.drawLine (x + 3, y, xx, y);
                 g.drawLine (x, y + 3, x, yy-3);
                 g.drawLine (x, y + 3,x + 3, y);

                 g.setColor (color2);
                 g.drawLine (x + 3, yy - 1, xx, yy - 1);
                 g.drawLine (x, yy-4, x + 4, yy);

                 g.setColor (color3);
                 g.drawLine (x + 3, yy, xx, yy);
                 g.drawLine (x, yy-3, x + 3, yy);
                 
                 

                     g.setColor(color4);
                     g.drawLine(width+1,-1,width+1,height-1);

                 
               } break;
               case Graphics.RIGHT:
               {

                 g.drawLine (x, y, xx - 3, y);

                 g.setColor (color2);
                 g.drawLine (xx - 4, y, xx, y + 4);
                 g.drawLine (xx - 1, y + 4, xx - 1, yy - 4);
                 g.drawLine (xx, yy - 4, xx - 4, yy);
                 g.drawLine (x, yy-1, xx - 4, yy-1);

                 g.setColor (color3);
                 g.drawLine (xx - 3, y, xx, y + 3);
                 g.drawLine (xx, y + 3, xx, yy - 3);
                 g.drawLine (xx, yy - 3, xx - 3, yy);
                 g.drawLine (x, yy, xx - 3, yy);
                 
                     g.setColor(color4);
                     g.drawLine(-2,-1,-2,height-1);

                 
                 
               } break;
             }


        
        
    }

    public int getTop() {
        return 2;
    }

    public int getBottom() {
        return 2;
    }

    public int getRight() {
        return 2;
    }

    public int getLeft() {
        return 2;
    }
    
    public boolean isBorderOpaque() {
        return false;
    }

}

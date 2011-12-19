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

package net.yura.mobile.gui.plaf;

import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class MetalIcon extends Icon {

    private int type;
    private int foreground;
    private int background;

    public MetalIcon(int size, int type,int fore, int back) {
//        if ((size%2)==0) {
//            size++;
//        }
        width = size;
        height = size;

        this.type = type;
        foreground = fore;
        background = back;
    }

    public void paintIcon(Component c, Graphics2D g, int x, int y) {

        int fcolor = foreground==Style.NO_COLOR?c.getForeground():foreground;
        g.setColor( fcolor );

        if (type == LookAndFeel.ICON_CHECKBOX) {

                int w = getIconWidth();
                int h = getIconHeight();

                g.setColor( background );
                g.fillRect(x, y, w, h);
                g.setColor( fcolor );
                g.drawRect(x, y, w-1, h-1);

                if ( ( c.getCurrentState() & Style.SELECTED)!=0 ) {

                    for (int pad=3;pad<6;pad++) {
                        g.drawLine(x+pad, y+h/2, x+w/3, y+h-pad);
                        g.drawLine(x+w/3, y+h-pad,x+w-pad,y+pad);
                    }
                }

        }
        else if (type == LookAndFeel.ICON_RADIO) {

                int w = getIconWidth();
                int h = getIconHeight();

                g.setColor( background );
                g.fillArc(x, y, w, h, 0, 360);
                g.setColor( fcolor );
                g.drawArc(x, y, w-1, h-1, 0, 360);

                if ( ( c.getCurrentState() & Style.SELECTED)!=0 ) {

                    int w2 = w/4;
                    int h2 = h/4;
                    g.fillArc(x+w2, y+h2, w-(w2*2), h-(h2*2), 0, 360);
                }
        }
        else if (type == LookAndFeel.ICON_COMBO) {
            g.drawLine(x, (c.getHeight()-height)/2, x , height);
            drawSelectionArrow(c, g, x, y, Sprite.TRANS_NONE);
        }
        else if (type == LookAndFeel.ICON_SPINNER_LEFT || type == LookAndFeel.ICON_ARROW_LEFT) {
            g.setColor( fcolor );
            int gp = 2; // gap between arrow and sides
            int side = x+((width/2)-2);
            int iconHeight = height-(gp*2);
            g.fillTriangle(side,y+gp+(iconHeight/2)+1,
                           side+5,y+gp+iconHeight,
                           side+5,y+gp);

        }
        else if (type == LookAndFeel.ICON_SPINNER_RIGHT || type == LookAndFeel.ICON_ARROW_RIGHT) {

            g.setColor( fcolor );
            int gp = 2; // gap between arrow and sides
            int side = x+((width/2)-2);
            int iconHeight = height-(gp*2);
            g.fillTriangle(side+5,y+gp+(iconHeight/2)+1,
                           side,y+gp+iconHeight,
                           side,y+gp);
        }
        else if (type == LookAndFeel.ICON_ARROW_UP) {

            //g.setColor( background );
            //g.fillRect(x, y, width, height);

            g.setColor( fcolor );
            int gp = 2; // gap between arrow and sides
            int top = y+((height/2)-2);
            int iconWidth = width-(gp*2);
            g.fillTriangle(x+gp+(iconWidth/2)+1, top,
                           x+gp+iconWidth, top+5,
                           x+gp, top+5);

        }
        else if (type == LookAndFeel.ICON_ARROW_DOWN) {

            //g.setColor( background );
            //g.fillRect(x, y, width, height);

            g.setColor( fcolor );
            int gp = 2; // gap between arrow and sides
            int top = y+((height/2)-2);
            int iconWidth = width-(gp*2);
            g.fillTriangle(x+gp+(iconWidth/2)+1, top+5,
                           x+gp+iconWidth, top,
                           x+gp, top);

        }

    }

    private void drawSelectionArrow(Component c, Graphics2D g, int x, int y, int transformation) {
        // Default arrow has point facing downwards
        g.translate(x, y);

        int trans = g.getTransform();
        g.setTransform(transformation);

        int gp = 2; // gap between arrow and sides
        int top = (height/2)-2;
        int iconWidth = width-(gp*2);
        g.fillTriangle(gp+(iconWidth/2)+1, top+5,
                       gp+iconWidth, top,
                       gp, top);

        g.setTransform(trans);

        g.translate(-x, -y);
    }
}

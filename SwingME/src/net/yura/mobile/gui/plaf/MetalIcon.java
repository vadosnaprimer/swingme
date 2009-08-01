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

        g.setColor( foreground==-1?c.getCurrentForeground():foreground );

        if (type == LookAndFeel.ICON_CHECKBOX) {

                int w = getIconWidth();
                int h = getIconHeight();

                g.drawRect(x, y, w-1, h-1);

                if (c instanceof Button) {
                    Button b = (Button)c;
                    if (b.isSelected()) {

                        for (int pad=3;pad<6;pad++) {
                            g.drawLine(x+pad, y+h/2, x+w/3, y+h-pad);
                            g.drawLine(x+w/3, y+h-pad,x+w-pad,y+pad);
                        }
                    }
                }
                


        }
        
        if (type == LookAndFeel.ICON_RADIO) {

                int w = getIconWidth();
                int h = getIconHeight();

                g.drawArc(x, y, w-1, h-1, 0, 360);

                if (c instanceof Button) {
                    Button b = (Button)c;
                    if (b.isSelected()) {
                        if (b.isSelected()){

                            int w2 = w/4;
                            int h2 = h/4;
                            g.fillArc(x+w2, y+h2, w-(w2*2), h-(h2*2), 0, 360);
                        }
                    }
                }
        }

        if (type == LookAndFeel.ICON_COMBO) {
            g.drawLine(x, (c.getHeight()-height)/2, x , height);
            drawSelectionArrow(c, g, x, y, Sprite.TRANS_NONE);
        }

        if (type == LookAndFeel.ICON_SPINNER_LEFT) {
            // TODO: Change color of spinner if pressed
            g.setColor(foreground);
            drawSelectionArrow(c, g, x+getIconWidth(), y+getIconHeight(),Sprite.TRANS_MIRROR_ROT90);
        }

        if (type == LookAndFeel.ICON_SPINNER_RIGHT) {
            // TODO: Change color of spinner if pressed
            g.setColor(foreground);
            drawSelectionArrow(c, g, x, y, Sprite.TRANS_ROT90);
        }

        if (type == LookAndFeel.ICON_TRACK_FILL) {

            g.setColor( background );
            g.fillRect(x, y, width, height);

            // draw the lines either side
            g.setColor( foreground );
            g.drawLine( x , y, x , y+height );
            g.drawLine(x+width-1, y, x+width-1, y+height);

        }

        if (type == LookAndFeel.ICON_THUMB_FILL) {

            g.setColor( foreground );
            g.fillRect(x+2, y, width-4, height);

        }

        if (type == LookAndFeel.ICON_TRACK_TOP) {

            g.setColor( background );
            g.fillRect(x, y, width, height);

            g.setColor(foreground);
            g.drawRect(x, y, width-1, height-1);

            int gp = 2; // gap between arrow and sides
            int top = y+((height/2)-2);
            int iconWidth = width-(gp*2);
            g.fillTriangle(x+gp+(iconWidth/2)+1, top,
                           x+gp+iconWidth, top+5,
                           x+gp, top+5);

        }

        if (type == LookAndFeel.ICON_TRACK_BOTTOM) {

            g.setColor( background );
            g.fillRect(x, y, width, height);

            g.setColor(foreground);
            g.drawRect(x, y, width-1, height-1);

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

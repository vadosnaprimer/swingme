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

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class MetalIcon extends Icon {

    int type;

    public MetalIcon(int size, int type) {
        width = size;
        height = size;
        this.type = type;
    }

    public void paintIcon(Component c, Graphics2D g, int x, int y) {
        
        if (type == LookAndFeel.ICON_CHECKBOX) {
                g.setColor(c.getForeground());

                int w = getIconWidth();
                int h = getIconHeight();

                g.drawRect(x, y, w-1, h-1);

                if (c instanceof Button) {
                    Button b = (Button)c;
                    if (b.isSelected()) {

                        //g.fillRect(x+3, y+3, size-6, size-6);

                        for (int pad=3;pad<6;pad++) {
                            g.drawLine(x+pad, y+h/2, x+w/3, y+h-pad);
                            g.drawLine(x+w/3, y+h-pad,x+w-pad,y+pad);
                        }
                    }
                }
                


        }
        
        if (type == LookAndFeel.ICON_RADIO) {
                g.setColor(c.getForeground());

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
                int w = c.getWidth() - width;
                g.setColor( c.getForeground() );

                if ((w%2)==0) {
                    w++;
                }

                g.drawLine(w , (c.getHeight()-height)/2, w , height);

                int gp = 2; // gap between arrow and sides                
                int top = (c.getHeight()/2)-2;
                int iconWidth = width-(gp*2);
                g.fillTriangle(w+gp+(iconWidth/2)+1, top+5, 
                               w+gp+iconWidth, top,
                               w+gp, top);
        }
    }
}

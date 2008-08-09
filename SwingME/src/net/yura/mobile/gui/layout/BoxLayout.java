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

package net.yura.mobile.gui.layout;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Panel;

/**
 * @author Yura Mamyrin
 * @see java.awt.BoxLayout
 */
public class BoxLayout implements Layout {

    private int axis;
    
    /**
     * @param axis the axis to lay out components along, can be Graphics.VCENTRE or Graphics.HCENTRE
     * @see javax.swing.BoxLayout#BoxLayout(java.awt.Container, int) BoxLayout.BoxLayout
     */
    public BoxLayout(int axis) {
        this.axis = axis;
    }
    
    /**
     * @see javax.swing.BoxLayout#layoutContainer(java.awt.Container) BoxLayout.layoutContainer
     */
    public void layoutPanel(Panel panel) {

                    Vector components = panel.getComponents();

                    int height = panel.getHeight();
                    int width = panel.getWidth();

                    int offset=0;
                    for (int c=0;c<components.size();c++) {

                            Component comp = (Component)components.elementAt(c);

                            comp.setBoundsWithBorder((axis==Graphics.HCENTER)?offset:0 , (axis==Graphics.HCENTER)?0:offset, (axis==Graphics.HCENTER)?comp.getWidthWithBorder():width, (axis==Graphics.HCENTER)?height:comp.getHeightWithBorder() );
                            offset = offset + ((axis==Graphics.HCENTER)?comp.getWidthWithBorder():comp.getHeightWithBorder());
                    }

    }

    public int getPreferredHeight(Panel panel) {
                    Vector components = panel.getComponents();
                    int height=0;
                    for (int c=0;c<components.size();c++) {

                            Component comp = (Component)components.elementAt(c);

                            if (axis == Graphics.HCENTER) {
                                if (height < comp.getHeightWithBorder()) {
                                    height = comp.getHeightWithBorder();
                                }
                            }
                            else {
                                height = height + comp.getHeightWithBorder();
                            }

                    }
                    return height;
    }

    public int getPreferredWidth(Panel panel) {
                    Vector components = panel.getComponents();
                    int width=0;
                    for (int c=0;c<components.size();c++) {

                            Component comp = (Component)components.elementAt(c);

                            if (axis == Graphics.HCENTER) {
                                width = width + comp.getWidthWithBorder();
                            }
                            else {
                                if (width < comp.getWidthWithBorder()) {
                                    width = comp.getWidthWithBorder();
                                }
                            }

                    }
                    return width;
    }

}

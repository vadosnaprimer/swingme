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
 * @see java.awt.BorderLayout
 */
public class BorderLayout implements Layout {

    /**
     * @see java.awt.BorderLayout#layoutContainer(java.awt.Container) BorderLayout.layoutContainer
     */
    public void layoutPanel(Panel panel, Hashtable cons) {

        	Vector components = panel.getComponents();

                int top=0;
                int bottom=0;
                int right=0;
                int left=0;
                
                int w1=0;
                int w2=0;
                int h1=0;
                int h2=0;
                
                for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);
                        Integer pos = (Integer)cons.get(comp);

                        if (pos!=null) {
                            switch (pos.intValue()) {
                                case Graphics.TOP:
                                    top = comp.getHeightWithBorder();
                                    if (w1<comp.getWidthWithBorder()) {
                                        w1 = comp.getWidthWithBorder();
                                    }
                                    h2 = h2+ comp.getHeightWithBorder();
                                    break;
                                case Graphics.BOTTOM:
                                    bottom = comp.getHeightWithBorder();
                                    if (w1<comp.getWidthWithBorder()) {
                                        w1 = comp.getWidthWithBorder();
                                    }
                                    h2 = h2+ comp.getHeightWithBorder();
                                    break;
                                case Graphics.RIGHT:
                                    right = comp.getWidthWithBorder();
                                    w2 = w2 + comp.getWidthWithBorder();
                                    if (h1<comp.getHeightWithBorder()) {
                                        h1 = comp.getHeightWithBorder();
                                    }
                                    break;
                                case Graphics.LEFT:
                                    left = comp.getWidthWithBorder();
                                    w2 = w2 + comp.getWidthWithBorder();
                                    if (h1<comp.getHeightWithBorder()) {
                                        h1 = comp.getHeightWithBorder();
                                    }
                                    break;
                                default:
                                    w2 = w2 + comp.getWidthWithBorder();
                                    if (h1<comp.getHeightWithBorder()) {
                                        h1 = comp.getHeightWithBorder();
                                    }
                                    break;

                            }
                        }

                }
                
                int width=Math.max( w1,Math.max(panel.getWidth(),w2)  );
                int height=Math.max(panel.getHeight(),h1+h2);
                
                for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);
                        Integer pos = (Integer)cons.get(comp);
                        int p = (pos==null)?0:pos.intValue();
                        
                        switch (p) {
                            case Graphics.TOP:
                                comp.setBoundsWithBorder(0, 0, width, top);
                                break;
                            case Graphics.BOTTOM:
                                comp.setBoundsWithBorder(0, height-bottom, width, bottom);
                                break;
                            case Graphics.RIGHT:
                                comp.setBoundsWithBorder(width-right, top, right, height-top-bottom);
                                break;
                            case Graphics.LEFT:
                                comp.setBoundsWithBorder(0, top, left, height-top-bottom);
                                break;
                            default:
                                comp.setBoundsWithBorder(left, top, width-right-left, height-top-bottom);
                                break;
                                        
                        }

                }
                
                panel.setSize(width,height);
                        
    }

}

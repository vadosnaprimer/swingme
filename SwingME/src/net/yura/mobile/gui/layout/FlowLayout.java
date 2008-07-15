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
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Panel;


/**
 * This class is like the java.awt.FlowLayout but can also do FlowLayout vertically
 * @author Yura Mamyrin
 * @see java.awt.FlowLayout
 */

public class FlowLayout implements Layout {

	private int padding;
	private int align;
        
        /**
         * @see java.awt.FlowLayout#FlowLayout() FlowLayout.FlowLayout
         */
	public FlowLayout() {
		
            this( Graphics.HCENTER  );
	}
        /**
         * @param a can be Graphics.HCENTER or Graphics.VCENTER
         */
        public FlowLayout(int a) {
		
            this( a, DesktopPane.getDefaultTheme().defaultSpace );
	}
        /**
         * @param a can be Graphics.HCENTER or Graphics.VCENTER
         * @param p the padding to be used
         */
	public FlowLayout(int a,int p) {
		
            align = a;
            padding = p;
	}
	
        /**
         * @see java.awt.FlowLayout#layoutContainer(java.awt.Container) FlowLayout.layoutContainer
         */
	public void layoutPanel(Panel panel) {

            int width = panel.getWidth();
            int height = panel.getHeight();

            Vector components = panel.getComponents();
            
            if (align==Graphics.HCENTER) {

                    // need to get this to know where to centre components
                    int fullwidth = getPreferredWidth(panel);
                
                    int offset = (width - fullwidth)/2 + padding;

                    for (int c=0;c<components.size();c++) {

                            Component comp = (Component)components.elementAt(c);

                            comp.setBoundsWithBorder(offset , (height-comp.getHeightWithBorder())/2, comp.getWidthWithBorder(), comp.getHeightWithBorder() );
                            offset = offset + comp.getWidthWithBorder() + padding;
                    }

            }
            else {

		int down=padding;
		
		for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);
			
			int cheight = comp.getHeightWithBorder();
			int cwidth = comp.getWidthWithBorder();
				
			int offset = (width - cwidth)/2;

			comp.setBoundsWithBorder(offset,down, cwidth, cheight );
			
			if (cheight!=0) {
				down = down + cheight + padding;
			}
			
		}

            }
                    
	}

    public int getPreferredHeight(Panel panel) {
        
            Vector components = panel.getComponents();
        
            if (align==Graphics.HCENTER) {
                
                    int fullheight=0;

                    for (int c=0;c<components.size();c++) {

                            Component comp = (Component)components.elementAt(c);

                            if (fullheight < comp.getHeightWithBorder()+padding*2) {

                                    fullheight = comp.getHeightWithBorder()+padding*2;
                            }

                    }
                    
                    return fullheight;
                
            }
            else {
                
                    int fullheight=(components.size()>0)?((components.size()+1)*padding):0;

                    for (int c=0;c<components.size();c++) {

                            Component comp = (Component)components.elementAt(c);

                            fullheight = fullheight + comp.getHeightWithBorder();

                    }

                    return fullheight;
                
            }
    }

    public int getPreferredWidth(Panel panel) {
        
            Vector components = panel.getComponents();
        
            if (align==Graphics.HCENTER) {

                    int fullwidth=(components.size()>0)?((components.size()+1)*padding):0;

                    for (int c=0;c<components.size();c++) {

                            Component comp = (Component)components.elementAt(c);

                            fullwidth = fullwidth + comp.getWidthWithBorder();



                    }

                    return fullwidth;
        
            }
            else {
                
                int fullwidth=0;
		
		for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);

                        // we DONT use +padding*2 even though really we should
			if ( comp.getWidthWithBorder() > fullwidth) {
				
				fullwidth = comp.getWidthWithBorder();
			}
		}
                
                return fullwidth;
            }

    }

}

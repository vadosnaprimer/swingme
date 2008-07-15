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

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Panel;
/**
 * @author Yura Mamyrin
 * @see java.awt.GridLayout
 */
public class GridLayout implements Layout {

	private int across;
	private int padding;
	private int down;
        
        /**
         * @param rows the rows, with the value zero meaning any number of rows
         * @param cols the columns, with the value zero meaning any number of columns
         * @see java.awt.GridLayout#GridLayout(int, int) GridLayout.GridLayout
         */
	public GridLayout(int rows, int cols) {
		
		this(rows, cols,DesktopPane.getDefaultTheme().defaultSpace );
	}
	
        /**
         * @param rows the rows, with the value zero meaning any number of rows
         * @param cols the columns, with the value zero meaning any number of columns
         * @param p the padding
         * @see java.awt.GridLayout#GridLayout(int, int, int, int) GridLayout.GridLayout
         */
	public GridLayout(int rows, int cols,int p) {
		
		across = cols;
		down = rows;
                padding = p;
	}
	
        /**
         * @see java.awt.GridLayout#layoutContainer(java.awt.Container) GridLayout.layoutContainer
         */
	public void layoutPanel(Panel panel) {

		Vector components = panel.getComponents();
		
                int ac = getCols(components);
                int de = getRows(components);
                
		int cwidth = (panel.getWidth() -(ac*padding) -padding) /ac;
		int cheight = (panel.getHeight() -(de*padding) -padding) /de;

		int a=0;
		int d=0;

		for (int i=0;i<components.size();i++) {
			
			Component comp = (Component)components.elementAt(i);
			comp.setBoundsWithBorder((a*cwidth)+padding+(padding*a), (d*cheight)+padding+(padding*d), cwidth, cheight);
			a++;
                        
                        // when it gets to the end of the row it adds 1
			if (a==ac && i!=(components.size()-1)) {
				a=0;
				d++;
			}
		}

	}

    public int getPreferredHeight(Panel panel) {
        
            Vector components = panel.getComponents();
            int cheight = 0;
            int de = getRows(components);
            
            for (int i=0;i<components.size();i++) {
			Component comp = (Component)components.elementAt(i);
			if (comp.getHeightWithBorder() > cheight) {
				cheight = comp.getHeightWithBorder(); 
			}

            }
            return de * cheight + de*padding +padding;
        
    }

    public int getPreferredWidth(Panel panel) {
        
            Vector components = panel.getComponents();
        
            int cwidth = 0;
            int ac = getCols(components);
            
            for (int i=0;i<components.size();i++) {
			Component comp = (Component)components.elementAt(i);
			if (comp.getWidthWithBorder() > cwidth) {
				cwidth = comp.getWidthWithBorder();
			}
            }
        
            return ac * cwidth + ac*padding +padding;
    }
    
    private int getCols(Vector components) {

                int ac;
                if (across!=0) {
                    ac=across;
                }
                else {
                    ac = (components.size()+(down-1)) / down;
                }
                
                if (components.size() < ac) { ac=components.size(); }
                
                return ac;
    }
    private int getRows(Vector components) {
                int de;
                if (down!=0) {
                    de=down;
                }
                else {
                    de = (components.size()+(across-1)) / across;
                }
                
                if (components.size() < de) { de=components.size(); }
                
                return de;
    }

}

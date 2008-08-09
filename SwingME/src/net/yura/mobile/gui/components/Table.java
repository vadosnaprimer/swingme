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

package net.yura.mobile.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JTable
 */
public class Table extends Panel {

    private Vector rows;
    private Vector columnNames;
    private Vector editors;
    private Vector renderers;
    
    private int row,col;
    
    public Table(Vector data, Vector names) {
        rows=data;
        columnNames=names;
    }
    
    
    public void breakOutAction(final Component component, final int direction, final boolean scrolltothere) {

    }
    
    
        
    public void paintComponent(Graphics g) {
        for (int c=0;c<rows.size();c++) {
            
            Vector crow = (Vector)rows.elementAt(c);
            
            for (int a=0;a<crow.size();a++) {
                
                  Object object = crow.elementAt(a);
                
                  ListCellRenderer renderer = (ListCellRenderer)renderers.elementAt(a);
                  Component comp = renderer.getListCellRendererComponent(null, object, c, false, row==c && col==a);
                          
                  
                  
            }
            

                    
            
        }
    }

    public void setDefaultEditors(Vector e) {
        editors = e;
    }

    public void setDefaultRenderer(Vector r) {
        renderers = r;
    }

    public void workoutSize() {
        
    }
    
    public void doLayout() {
        
    }
/*
    public int getColumnCount() {
        
    }

    public String getColumnName(int columnIndex) {
        
    }

    public int getRowCount() {
        
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
    }
    */
    
    
    


}

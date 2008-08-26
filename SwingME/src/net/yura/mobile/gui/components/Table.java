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

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.celleditor.DefaultCellEditor;
import net.yura.mobile.gui.celleditor.TableCellEditor;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JTable
 */
public class Table extends Panel {

    private Hashtable renderers;
    private Hashtable editors;
    
    private Vector colWidths;
    
    /**
     * @see javax.swing.JTable#rowHeight JTable.rowHeight
     */
    protected int rowHeight;
    
     /**
     * @see javax.swing.JTable#editingRow JTable.editingRow
     */
    protected int editingRow;
    
    /**
     * @see javax.swing.JTable#editingColumn JTable.editingColumn
     */
    protected int editingColumn;
    

    /**
     * @see javax.swing.JTable#cellEditor JTable.cellEditor
     */
    protected TableCellEditor cellEditor;
    
    public Table() {

        colWidths = new Vector();
        rowHeight = 20; // default value
        
        renderers = new Hashtable();
        editors = new Hashtable();
                
        selectable = true;
    }
    
    public Table(Vector data, Vector names) {
        this();
        
        rows=data;
        columnNames=names;
        
        // default renderer
        setDefaultRenderer(Object.class, new DefaultListCellRenderer());
        setDefaultEditor(Object.class, new DefaultCellEditor( new TextField() ));

    }
    
    
    public void breakOutAction(final Component component, final int direction, final boolean scrolltothere) {

        removeAll();
        DesktopPane.getDesktopPane().setFocusedComponent(this);

        setValueAt(cellEditor.getCellEditorValue(),editingRow, editingColumn);
        
        moveSelection(direction);
        
        selectable = true;
        
    }
    
    private void moveSelection(int d) {




    }
    
    public void focusLost() {
        repaint();
    }
    public void focusGained() {
        repaint();
    }
     
    public boolean keyEvent(KeyEvent event) {
        
        //int key = event.getIsDownKey();
        
        if (event.isDownAction(Canvas.DOWN)) {
            if (editingRow < (getRowCount()-1)) {
                editingRow++;
            }
            else {
                return false;
            }
        }
        else if (event.isDownAction(Canvas.UP)) {
            if (editingRow > 0) {
                editingRow--;
            }
            else {
                return false;
            }
        }
        else if (event.isDownAction(Canvas.RIGHT)) {
            if (editingColumn < (getColumnCount()-1)) {
                editingColumn++;
            }
            else {
                return false;
            }
        }
        else if (event.isDownAction(Canvas.LEFT)) {
            if (editingColumn >0) {
                editingColumn--;
            }
            else {
                return false;
            }
        }
        else if (event.getIsDownKey()!=0) { // if (event.isDownAction(Canvas.FIRE))

            if (isCellEditable(editingRow, editingColumn)) {
                
                cellEditor = getCellEditor(editingRow, editingColumn);
                
                int x=0,y=0;
                int currentRowHeight=0,currentColWidth=0;
                for (int c=0;c<=editingRow;c++) {
                    y = y + currentRowHeight;
                    currentRowHeight = getRowHeight(c);
                }
                for (int a=0;a<=editingColumn;a++) {
                    x = x + currentColWidth;
                    currentColWidth = getColumnWidth(a);
                }

                Component component = cellEditor.getTableCellEditorComponent(this, getValueAt(editingRow, editingColumn), true, editingRow, editingColumn);
                
                component.setBoundsWithBorder(x, y, currentColWidth, currentRowHeight );
                add(component);
                DesktopPane.getDesktopPane().setFocusedComponent(component);
                selectable = false;
            }
            
        }
        
        // TODO scroll to there
        //scrollRectToVisible( ,  ,  ,  , false);
        repaint();
        return true;
    }

    public void paintComponent(Graphics g) {
        int x=0,y=0;
        
        int cols = getColumnCount();
        int rowc = getRowCount();
        
        boolean editOpen = getComponents().size()==1;
        
        for (int c=0;c<rowc;c++) {
            
            int currentRowHeight = getRowHeight(c);
            
            for (int a=0;a<cols;a++) {
                
                  int currentColWidth = getColumnWidth(a);

                  if (!editOpen || editingRow!=c || editingColumn!=a) {

                  	Object object = getValueAt(c, a);
                
                  	ListCellRenderer renderer = getCellRenderer(c,a);

                        boolean sel = editingRow==c && editingColumn==a;
                        Component comp = renderer.getListCellRendererComponent(null, object, c, sel ,sel && isFocused() );
                        comp.setBoundsWithBorder(x, y, currentColWidth, currentRowHeight);
                        int xoff=comp.getX();
                        int yoff=comp.getY();
                        g.translate(xoff, yoff);
                        comp.paint(g);
                        g.translate(-xoff, -yoff);
                  }
                  
                  x = x + currentColWidth;
                  
            }
            
            y = y + currentRowHeight;
            x=0;
                    
            
        }
    }

    public void workoutSize() {

        int w = 0,h = 0;
        
        int c = getColumnCount();
        for (int a=0;a<c;a++) {
            w = w + getColumnWidth(a);
        }
        
        width = w;
        height = h * getRowCount();
        
    }
    
    public void doLayout() {
        
    }

    /**
     * @see javax.swing.JTable#setDefaultEditor(java.lang.Class, javax.swing.table.TableCellEditor) JTable.setDefaultEditor
     */
    public void setDefaultEditor(Class columnClass, TableCellEditor editor) {
        editors.put(columnClass, editor);
    }

    /**
     * @see javax.swing.JTable#setDefaultRenderer(java.lang.Class, javax.swing.table.TableCellRenderer) JTable.setDefaultRenderer
     */
    public void setDefaultRenderer(Class columnClass, ListCellRenderer renderer) {
        renderers.put(columnClass, renderer);
    }

    /**
     * @see javax.swing.JTable#getCellEditor(int, int) JTable.getCellEditor
     */
    public TableCellEditor getCellEditor(int row, int column) {

                TableCellEditor currentEditor = ((TableCellEditor)editors.get( getColumnClass(column) ));
                if (currentEditor==null) currentEditor = (TableCellEditor)editors.get( Object.class );
		return currentEditor;

    }

    /**
     * @see javax.swing.JTable#getCellRenderer(int, int) JTable.getCellRenderer
     */
    public ListCellRenderer getCellRenderer(int row, int column) {

                ListCellRenderer renderer = (ListCellRenderer)renderers.get( getColumnClass(column) );
                if (renderer==null) renderer = (ListCellRenderer)renderers.get(Object.class);
		return renderer;

    }


    // #########################################################################
    // ############################# Size stuff ################################
    // #########################################################################

    /**
     * Sets the height for row to rowHeight, revalidates, and repaints.
     * @param rowHeight
     * @see javax.swing.JTable#setRowHeight(int) JTable.setRowHeight
     */
    public void setRowHeight(int rowHeight) {
        
        this.rowHeight = rowHeight;
        
        if (parent!=null) {
            parent.revalidate();
            parent.repaint();
        }

    }
    
    public void setColumnWidth(int col,int colHeight) {

         while (colWidths.size() <= col) {
                colWidths.addElement( null );
         }
         colWidths.setElementAt(new Integer(colHeight), col);
    }
          
    /**
     * @return the current width of a column
     * @see javax.swing.table.TableColumn#getMinWidth() TableColumn.getMinWidth
     */
    public int getColumnWidth(int c) {
         if (colWidths.size() > c) {
             Integer i = ((Integer)colWidths.elementAt(c));
             if (i!=null) {
                return i.intValue();
             }
             
         }
         // return a default vlaue
         return (DesktopPane.getDesktopPane().getWidth() - DesktopPane.getDesktopPane().defaultWidthOffset )/getColumnCount();
    }
    
    public int getRowHeight(int row) {
        return rowHeight;
    }



    // #########################################################################
    // ############################# TableModel ################################
    // #########################################################################
    
    private Vector rows;
    private Vector columnNames;
    
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0,columnIndex).getClass();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector)rows.elementAt(rowIndex)).elementAt(columnIndex);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // true by default
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ((Vector)rows.elementAt(rowIndex)).setElementAt(aValue, columnIndex);
    }

    public int getColumnCount() {
        if (columnNames==null) {
            if (rows.size()==0) return 0;
            return ((Vector)rows.elementAt(0)).size();
        }
        return columnNames.size();
    }

    public String getColumnName(int columnIndex) {
        return (String)columnNames.elementAt(columnIndex);
    }

    public int getRowCount() {
        return rows.size();
    }


}

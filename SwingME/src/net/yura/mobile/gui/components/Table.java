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
    
    /**
     * @see javax.swing.JTable#editorComp JTable.editorComp
     */
    protected Component editorComp;
    
    public Table() {

        setName("Table");
        
        colWidths = new Vector();
        rowHeight = 20; // default value
        
        renderers = new Hashtable();
        editors = new Hashtable();
        
        // should ALWAYS be selectable to that pointer event can give it focus
        focusable = true;
    }
    
    public Table(Vector data, Vector names) {
        this();
        
        dataVector=data;
        columnIdentifiers=names;
        
        // default renderer
        setDefaultRenderer(Object.class, new DefaultListCellRenderer());
        setDefaultEditor(Object.class, new DefaultCellEditor( new TextField() ));

    }
    
    // the current editor has finished editing
    public void breakOutAction(final Component component, final int direction, final boolean scrolltothere,final boolean forceFocus) {
        
        boolean done = (component==editorComp)?moveSelection(direction): false;
        
        if (!done) {
            super.breakOutAction(component, direction, scrolltothere,forceFocus);
        }
        
    }
    
    private boolean moveSelection(int d) {

        if (d == Canvas.DOWN) {
            if (editingRow < (getRowCount()-1)) {
                setSelectedCell(editingRow+1,editingColumn);
                return true;
            }
        }
        else if (d == Canvas.UP) {
            if (editingRow > 0) {
                setSelectedCell(editingRow-1,editingColumn);
                return true;
            }
        }
        else if (d == Canvas.RIGHT) {
            if (editingColumn < (getColumnCount()-1)) {
                setSelectedCell(editingRow,editingColumn+1);
                return true;
            }
        }
        else if (d == Canvas.LEFT) {
            if (editingColumn >0) {
                setSelectedCell(editingRow,editingColumn-1);
                return true;
            }
        }

        return false;

    }
    
    public void pointerEvent(int type, int x, int y) {
        super.pointerEvent(type, x, y);
        
        if (type == DesktopPane.PRESSED || type == DesktopPane.DRAGGED ) {
            
                int x1 = 0,y1 = 0;
                int currentRow = -1,currentCol = -1;
                for (int c=0;c<getRowCount();c++) {
                    y1 = y1 + getRowHeight(c);
                    if (y1 > y) {
                        currentRow = c;
                        break;
                    }
                }
                for (int a=0;a<getColumnCount();a++) {
                    x1 = x1 + getColumnWidth(a);
                    if (x1 > x) {
                        currentCol = a;
                        break;
                    }
                }
                
                if (currentCol==-1 || currentRow==-1) { return; }
                
                //if (editingColumn == currentCol && editingRow == currentRow)
                if (type == DesktopPane.PRESSED) {
                    editCellAt(currentRow,currentCol);
                    
                    // dont pass clicks onto textComponents
                    if (editorComp!=null) { //  && !(editorComp instanceof TextComponent)
                        // now pass on the event onto the component
                        DesktopPane.getDesktopPane().pointerPressed(x+getXOnScreen(), y+getYOnScreen());
                    }
                    //editorComp.pointerEvent(type, x, y);
                }
                else {
                    setSelectedCell(currentRow,currentCol);
                }
            
        }
        
    }
    
    public void setSelectedCell(int pRow, int pCol) {
        removeEditor();
        editingRow = pRow;
        editingColumn = pCol;
        
        int x=getCellX(editingColumn);
        int y=getCellY(editingRow);
        int currentRowHeight=getRowHeight(editingRow);
        int currentColWidth=getColumnWidth(editingColumn);

        scrollRectToVisible( x, y , currentColWidth , currentRowHeight , false);
        repaint();
    }
    
    public void focusLost() {
        repaint();
    }
    public void focusGained() {
        if (editorComp!=null) {
            editorComp.requestFocusInWindow();
        }
        
        repaint();
    }
     
    public boolean keyEvent(KeyEvent event) {
        
        int key = event.getIsDownKey();
        int action = event.getKeyAction(key);
        
        if (    action==Canvas.UP ||
                action==Canvas.DOWN ||
                action==Canvas.LEFT ||
                action==Canvas.RIGHT
                ) {
            return moveSelection(action);
        }
        else if (key!=0) { // if (event.isDownAction(Canvas.FIRE))
            editCellAt(editingRow, editingColumn);
            
            // dont pass on fire to text components as that will open the native editor
            if (editorComp!=null && !(editorComp instanceof TextComponent && action == Canvas.FIRE)) {
                // now pass the current event onto that component
                editorComp.keyEvent(event);
            }
        }

        return true;
    }

    /**
     * @param col the Column
     * @return the x position of a cell in the column
     * @see javax.swing.JTable#getCellRect(int, int, boolean) JTable.getCellRect
     */
    public int getCellX(int col) {
        int x=0;
        for (int a=0;a<col;a++) {
            x = x + getColumnWidth(a);
        }
        return x;
    }
    
    /**
     * @param row the Row
     * @return the y position of a cell in the row
     * @see javax.swing.JTable#getCellRect(int, int, boolean) JTable.getCellRect
     */
    public int getCellY(int row) {
        int y=0;
        for (int c=0;c<row;c++) {
            y = y + getRowHeight(c);
        }
        return y;
    }
    
    /**
     * @see javax.swing.JTable#editCellAt(int, int) JTable.editCellAt
     */
    public void editCellAt(int row, int column) {
        
        removeEditor();
        
        editingRow = row;
        editingColumn = column;
        
        if (isCellEditable(editingRow, editingColumn)) {
        
            cellEditor = getCellEditor(editingRow, editingColumn);

            editorComp = cellEditor.getTableCellEditorComponent(this, getValueAt(editingRow, editingColumn), true, editingRow, editingColumn);

            add(editorComp);
            
            doLayout();
            
            if (getWindow().getFocusOwner() == this) {
                editorComp.requestFocusInWindow();
            }
            //selectable = false;
        }
        repaint();
    }
    /**
     * @see javax.swing.JTable#removeEditor() JTable.removeEditor
     */
    public void removeEditor() {
        
        if (cellEditor!=null) {
            removeAll();
            setValueAt(cellEditor.getCellEditorValue(),editingRow, editingColumn);
            
            if (getWindow().getFocusOwner() == editorComp) {
                editorComp = null;
                requestFocusInWindow();
            }
            else {
                editorComp = null;
            }
            //selectable = true;

            cellEditor = null;

        }
        
    }
    
    public void paintComponent(Graphics g) {
        int x=0,y=0;

        int cols = getColumnCount();
        int rowc = getRowCount();
        
        boolean editOpen = getComponents().size()==1;
        
        boolean good1=false;
        for (int r=0;r<rowc;r++) {
            
            int currentRowHeight = getRowHeight(r);

            if (y < g.getClipY()+g.getClipHeight() &&
                y + currentRowHeight > g.getClipY()
            ) {
                good1 = true;
                boolean good2=false;
                for (int c=0;c<cols;c++) {

                      int currentColWidth = getColumnWidth(c);

                      if (x < g.getClipX()+g.getClipWidth() &&
                            x + currentColWidth > g.getClipX()
                      ) {
                          good2 = true;

                          if (!editOpen || editingRow!=r || editingColumn!=c) {

                                Component comp = getComponentFor(r,c);
                                if (comp!=null) {

                                    comp.setBoundsWithBorder(x, y, currentColWidth, currentRowHeight);

                                    int xoff=comp.getX();
                                    int yoff=comp.getY();
                                    g.translate(xoff, yoff);
                                    comp.paint(g);
                                    g.translate(-xoff, -yoff);
                                }
                          }
                      }
                      else if (good2) {
                          break;
                      }

                      x = x + currentColWidth;

                }
            }
            else if (good1) {
                break;
            }
            
            y = y + currentRowHeight;
            x=0;
        }
    }
    
    private Component getComponentFor(int r,int c) {
        
        Object object = getValueAt(r, c);

        ListCellRenderer renderer = getCellRenderer(r,c);

        boolean sel = editingRow==r && editingColumn==c;
        Component comp = renderer.getListCellRendererComponent(null, object, r, sel ,sel && isFocusOwner() );

        return comp;
        
    }

    public void workoutSize() {
        super.workoutSize();

        //int w = 0;
        int h = 0;
        
        //int c = getColumnCount();
        //for (int a=0;a<c;a++) {
        //    w = w + getColumnWidth(a);
        //}
        
        int r = getRowCount();
        for (int a=0;a<r;a++) {
            h = h + getRowHeight(a);
        }
        
        //width = w;
        if (height==0) { // no prefured height
            height = h;
        }
        
    }
    
    public void doLayout() {
        
        if (editorComp!=null) {
            int x=getCellX(editingColumn);
            int y=getCellY(editingRow);
            int currentRowHeight=getRowHeight(editingRow);
            int currentColWidth=getColumnWidth(editingColumn);
            editorComp.setBoundsWithBorder(x, y, currentColWidth, currentRowHeight );
        }
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

    public String getToolTipText() {
        if (editingRow!=-1 && editingColumn!=-1) {
            Component c = getComponentFor(editingRow,editingColumn);
            return (c==null)?null:c.getToolTipText();
        }
        return super.getToolTipText();
    }
    
    public int getToolTipLocationX() {
        
        if (editingRow!=-1 && editingColumn!=-1) {
            Component c = getComponentFor(editingRow,editingColumn);
            return getCellX(editingColumn) + c.getToolTipLocationX();
        }
        
        return super.getToolTipLocationX();
    }
    public int getToolTipLocationY() {
        
        if (editingRow!=-1 && editingColumn!=-1) {
            Component c = getComponentFor(editingRow,editingColumn);
            return getCellY(editingRow) + c.getToolTipLocationY();
        }
        
        return super.getToolTipLocationY();
    }

    /**
     * @see javax.swing.JTable#getSelectedRow() JTable.getSelectedRow
     * @see javax.swing.JTable#getEditingRow() JTable.getEditingRow
     */
    public int getSelectedRow() {
        return editingRow;
    }
    
    /**
     * @see javax.swing.JTable#getSelectedColumns() JTable.getSelectedColumns
     * @see javax.swing.JTable#getEditingColumn() JTable.getEditingColumn
     */
    public int getSelectedColumn() {
        return editingColumn;
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

         return width / getColumnCount();

         // return a default vlaue
//        if (parent instanceof ScrollPane) {
//                return ((ScrollPane)parent).getViewPortWidth() / getColumnCount();
//        }
//        else {
//                return DesktopPane.getDesktopPane().getWidth() /getColumnCount();
//        }

    }
    
    public int getRowHeight(int row) {
        return rowHeight;
    }



    // #########################################################################
    // ############################# TableModel ################################
    // #########################################################################
    
    /**
     * @see javax.swing.table.DefaultTableModel#dataVector DefaultTableModel.dataVector
     */
    protected Vector dataVector;
    
    /**
     * @see javax.swing.table.DefaultTableModel#columnIdentifiers DefaultTableModel.columnIdentifiers
     */
    protected Vector columnIdentifiers;
    
    /**
     * @param columnIndex
     * @return the class of the column
     * @see javax.swing.table.TableModel#getColumnClass(int) TableModel.getColumnClass
     */
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0,columnIndex).getClass();
    }
    
    /**
     * @param rowIndex
     * @param columnIndex
     * @return the value
     * @see javax.swing.table.TableModel#getValueAt(int, int) TableModel.getValueAt
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector)dataVector.elementAt(rowIndex)).elementAt(columnIndex);
    }

    /**
     * @param rowIndex
     * @param columnIndex
     * @return true if the cell is editable
     * @see javax.swing.table.TableModel#isCellEditable(int,int) TableModel.isCellEditable
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // true by default
    }

    /**
     * @param aValue
     * @param rowIndex
     * @param columnIndex
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int) TableModel.setValueAt
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ((Vector)dataVector.elementAt(rowIndex)).setElementAt(aValue, columnIndex);
    }

    /**
     * @return the number of columns
     * @see javax.swing.table.TableModel#getColumnCount() TableModel.getColumnCount
     */
    public int getColumnCount() {
        if (columnIdentifiers==null) {
            if (dataVector.size()==0) return 0;
            return ((Vector)dataVector.elementAt(0)).size();
        }
        return columnIdentifiers.size();
    }

    /**
     * @param columnIndex
     * @return The name
     * @see javax.swing.table.TableModel#getColumnName(int) TableModel.getColumnName
     */
    public String getColumnName(int columnIndex) {
        return (String)columnIdentifiers.elementAt(columnIndex);
    }
    
    /**
     * @return the number of rows
     * @see javax.swing.table.TableModel#getRowCount() TableModel.getRowCount
     */
    public int getRowCount() {
        return dataVector.size();
    }


}

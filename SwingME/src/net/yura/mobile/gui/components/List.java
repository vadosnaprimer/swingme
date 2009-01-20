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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;


/**
 * @author Yura Mamyrin
 * @see javax.swing.JList
 */
public class List extends Component implements ActionListener {

    private static CommandButton selectButton = new CommandButton("Select","select");

    public static void setSelectButtonText(String a) {

        selectButton = new CommandButton(a,selectButton.getActionCommand());

    }
    private boolean useSelectButton;

    private ListCellRenderer renderer;
    private int current;

    private ActionListener al;
    private ChangeListener chl;
    private String actionCommand;

    private boolean loop;
    private boolean horizontal;
    private boolean doubleClick;

    /**
     * @see javax.swing.JList#JList() JList.JList
     */
    public List() {
        this((Vector)null);
    }

    /**
     * @param a
     * @see javax.swing.JList#JList(java.util.Vector) JList.JList
     */
    public List(Vector a) {
        this(a,new DefaultListCellRenderer(),false);
    }

    public List(ListCellRenderer a) {
        this(null,a,false);
    }

    // real constructor!
    public List(Vector a,ListCellRenderer b,boolean h) {
        items = a;
        if (items==null) {
            items = new Vector();
        }
        setCellRenderer(b);
        horizontal = h;
        setSelectedIndex(-1);
    }

    /**
     * @param a
     * @see javax.swing.JList#setListData(java.util.Vector) JList.setListData
     */
    public void setListData(Vector a) {

        items = a;
        if (a == null || current >= a.size()){
            setSelectedIndex(-1);
        }

        if (isFocusOwner() && current==-1 && a.size() > 0) {
            setSelectedIndex(0);
        }

    }

    /**
     * @param a the object to select
     * @see javax.swing.JList#setSelectedValue(java.lang.Object, boolean) JList.setSelectedValue
     */
    public void setSelectedValue(Object a) {

        setSelectedIndex( items.indexOf(a) );
    }

    public Vector getItems() {
        return items;
    }

    public String toString() {
        return super.toString() + items;
    }


    public void workoutSize() {

        //if (items!=null) {

            int totalHeight = 0;
            int totalWidth = 0;

            for(int i = 0; i < getSize(); i++){
                Object item = getElementAt(i);
                synchronized (renderer)
                {
                    Component c = renderer.getListCellRendererComponent(this, item, i, i == current, false);
                    c.workoutSize();
                    if (horizontal) {
                        if (totalHeight<c.getHeightWithBorder()) {
                            totalHeight=c.getHeightWithBorder();
                        }
                    }
                    else {
                        totalHeight = totalHeight + c.getHeightWithBorder();
                    }

                    if (horizontal) {
                        totalWidth = totalWidth + c.getWidthWithBorder();
                    }
                    else {
                        if (totalWidth<c.getWidthWithBorder()) {
                            totalWidth=c.getWidthWithBorder();
                        }
                    }
                }
            }

            setSize(totalWidth,totalHeight);
        //}
    }



    /**
     * @param horizontal
     * @see javax.swing.JList#setLayoutOrientation(int) JList.setLayoutOrientation
     */
    public void setLayoutOrientation(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public void addActionListener(ActionListener l) {

        al = l;

    }
    public void removeActionListener(ActionListener l) {

        if (al == l) { al = null; }
    }

    public void setActionCommand(String ac) {

        actionCommand=ac;
    }

    public void setLoop(boolean l) {
        loop = l;
    }
    public void setDoubleClick(boolean b){
        doubleClick = b;
    }

    /**
     * @param cellRenderer
     * @see javax.swing.JList#setCellRenderer(javax.swing.ListCellRenderer) JList.setCellRenderer
     */
    public void setCellRenderer(ListCellRenderer cellRenderer) {

        renderer = cellRenderer;
        //workoutItemSize();
    }

    public void paintComponent(Graphics g) {

        int offset=0;
        boolean good=false;
        for(int i = 0; i < getSize(); i++){
            synchronized (renderer)
            {
                Component c = getComponentFor(i,offset);

                offset = offset + ((horizontal)?c.getWidthWithBorder():c.getHeightWithBorder());
                //g.setColor(0x0000FF00);
                //g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());

                int x = c.getXWithBorder();
                int y = c.getYWithBorder();

                if (x < g.getClipX()+g.getClipWidth() &&
                    x + c.getWidthWithBorder() > g.getClipX() &&
                    y < g.getClipY()+g.getClipHeight() &&
                    y + c.getHeightWithBorder() > g.getClipY()
                ) {
                        x=c.getX();
                        y=c.getY();

                        good=true;
                        g.translate(x, y);
                        c.paint(g);
                        g.translate(-x, -y);
                }
                else if (good) {
                    // if we have done out painting but now things are out of the clip area
                    break;
                }
            }
        }


    }

    public void addChangeListener(ChangeListener aThis) {
        chl = aThis;
    }

    /**
     * @return the ListCellRenderer
     * @see javax.swing.JList#getCellRenderer() JList.getCellRenderer
     */
    public ListCellRenderer getCellRenderer() {
        return renderer;
    }

    private Component getComponentFor(int a) {

        int offset=0;

        for(int i = 0; true; i++){
            Component c = getComponentFor(i,offset);

                if (i==a) {
                    return c;
                }

                offset = offset + ((horizontal)?c.getWidthWithBorder():c.getHeightWithBorder());

        }
    }

    private Component getComponentFor(int i,int offset) {

        Object item = getElementAt(i);

        Component c = renderer.getListCellRendererComponent(this, item, i, i == current, isFocusOwner() && i == current);
        c.workoutSize();
        c.setBoundsWithBorder(
                ((horizontal)?offset:0),
                ((horizontal)?0:offset),
                ((horizontal)?c.getWidthWithBorder():width),
                ((horizontal)?height:c.getHeightWithBorder())
                );
        return c;

    }

    public void focusLost() {
        super.focusLost();
        if (useSelectButton) {
            DesktopPane.getDesktopPane().setComponentCommand(0, null);
        }
                repaint();

    }

        public void focusGained() {
            super.focusGained();
            if (getSize() != 0 ) {
                if (current==-1) { current=0; }
                setSelectedIndex(current);
            }

            if (useSelectButton) {
                DesktopPane.getDesktopPane().setComponentCommand(0, selectButton);
            }

        }

	private long doubleClickTime;
	private int doubleClickX,doubleClickY;

    public void pointerEvent(int type, int x, int y) {
        super.pointerEvent(type, x, y);

        if (type == DesktopPane.PRESSED || type == DesktopPane.DRAGGED) {

            int offset=0;
            synchronized (renderer)
            {
                for(int i = 0; i < getSize(); i++){
                    Component c = getComponentFor(i,offset);

                    int cw=c.getWidthWithBorder();
                    int ch=c.getHeightWithBorder();

                    offset = offset + ((horizontal)?cw:ch);

                    int cx = c.getXWithBorder();
                    int cy = c.getYWithBorder();

                    if (
                            x>=cx && x <=(cw+cx) &&
                            y>=cy && y <=(ch+cy)
                    ) {
                        setSelectedIndex(i);
                        return;
                    }
                }
            }
        }
        else if (type == DesktopPane.RELEASED) {

            if (doubleClick) {
		long time = System.currentTimeMillis();

		if (time < doubleClickTime + 300 && x>(doubleClickX-5) && x<(doubleClickX+5) && y>(doubleClickY-5) && y<(doubleClickY+5) ) {

			fireActionPerformed();
		}

		doubleClickTime = time;
		doubleClickX = x;
		doubleClickY = y;
            }
            else {
                fireActionPerformed();
            }
        }


    }

    public boolean keyEvent(KeyEvent keypad) {

        if (current==-1) { return false; }

        int next = current+1;
        int prev = current-1;

        int size = getSize();

        if (loop) {
            if (next>=size) { next = (size==0)?-1:0; }
            if (prev<0) { prev = size-1; }
        }
        else {
            if (next>=size) { next=-1; }
        }

            synchronized (renderer) {


		int keyCode = keypad.getIsDownKey();

		if (keyCode > Character.MIN_VALUE && keyCode < Character.MAX_VALUE) {

			keyCode = keypad.getKeyChar(keyCode, keypad.getChars( (char)keyCode,javax.microedition.lcdui.TextField.ANY ) ,false);

			for(int i = 0; i < getSize(); i++) {

				String item = String.valueOf( getElementAt(i) );

				item = (item==null)?"null":item.toLowerCase();

				if (!"".equals(item) && item.charAt(0) == keyCode) {

					setSelectedIndex(i);
					return true;

				}
			}

			return false;

		}
                else if (keypad.isDownAction(Canvas.DOWN)) {

                    if (!horizontal && next!=-1) {
                        setSelectedIndex(next);
                        return true;
                    }
                    //else {
                        if (horizontal && current!=-1) {
                            Component c = getComponentFor(current);
                            int y = getYOnScreen();
                            scrollRectToVisible(c.getXWithBorder(),c.getHeightWithBorder()-1,c.getWidthWithBorder(),1,true);
                            if (y!=getYOnScreen()) {
                                return true;
                            }
                        }

                        return false;
                    //}

                }
                else if (keypad.isDownAction(Canvas.UP)) {

                    if (!horizontal && prev!=-1) {
                        setSelectedIndex(prev);
                        return true;
                    }
                    //else {
                        if (horizontal && current!=-1) {
                            Component c = getComponentFor(current);
                            int y = getYOnScreen();
                            scrollRectToVisible(c.getXWithBorder(),0,c.getWidthWithBorder(),1,true);
                            if (y!=getYOnScreen()) {
                                return true;
                            }
                        }
                        return false;
                    //}

                }
                else if (keypad.isDownAction(Canvas.RIGHT)) {

                    if (horizontal && next!=-1) {
                        setSelectedIndex(next);
                        return true;
                    }
                    //else {
                        // TODO could get rid of this check and add the X pos to the width
                        // so you can scroll right even in horizontal mode
                        if (!horizontal && current!=-1) {
                            Component c = getComponentFor(current);
                            int x = getXOnScreen();
                            scrollRectToVisible(c.getWidthWithBorder()-1,c.getYWithBorder(),1,c.getHeightWithBorder(),true);
                            if (x!=getXOnScreen()) {
                                return true;
                            }
                        }

                        return false;
                    //}

                }
                else if (keypad.isDownAction(Canvas.LEFT)) {

                    if (horizontal && prev!=-1) {
                        setSelectedIndex(prev);
                        return true;
                    }
                    //else {
                        if (!horizontal && current!=-1) {
                            Component c = getComponentFor(current);
                            int x = getXOnScreen();
                            scrollRectToVisible(0,c.getYWithBorder(),1,c.getHeightWithBorder(),true);
                            if (x!=getXOnScreen()) {
                                return true;
                            }
                        }

                        return false;
                    //}

                }
                else if (keypad.justPressedAction(Canvas.FIRE) || keypad.justPressedKey('\n')) {

                    return fireActionPerformed();

                }
                else {
                    //if we did not consume the event
                    return false;
                }
            }

    }

    public boolean fireActionPerformed() {

        if (al!=null) {
            al.actionPerformed(actionCommand);
            return true;
        }
        return false;

    }

        /**
         * @return the first selected value, or null if the selection is empty
         * @see javax.swing.JList#getSelectedValue() JList.getSelectedValue
         */
    public Object getSelectedValue() {

            if (current==-1) return null;
            return getElementAt(current);
    }

        /**
         * @return the first selected index; returns -1 if there is no selected item
         * @see javax.swing.JList#getSelectedIndex() JList.getSelectedIndex
         */
    public int getSelectedIndex(){
        return current;
    }



        /**
         * @param a the index of the one cell to select
         * @see javax.swing.JList#setSelectedIndex(int) JList.setSelectedIndex
         */
    public void setSelectedIndex(int a) {

        int old = current;

        current = a;
        if (current!=-1) {
            synchronized (renderer)
            {
                    Component c = getComponentFor(a);
                    // good, but too simple
                    // what if we are scrolled right already?
                    //scrollTo(c);

                    // THIS WILL NOT WORK if list in inside a panel inside a scrollpane
                    // as posX and posY will be wrong!
                    // ALSO WILLNOT WORK IN BOXLAYOUT HCENTRE IF NOT THE FIRST COMPONENT
                    if (horizontal) {
                        scrollRectToVisible( c.getXWithBorder(), -posY, c.getWidthWithBorder(), 1,false);
                    }
                    else {
                        scrollRectToVisible( -posX, c.getYWithBorder(), 1, c.getHeightWithBorder(),false);
                    }

            }
                    if (chl!=null && old!=current) {
                        chl.changeEvent(a);
                    }
                    // TODO as scroll to always does a repaint
                    // we dont need it here
                    // BUT what if we are not in a scrollPane??
                    repaint();
        }
    }

    public void actionPerformed(String actionCommand) {

        if(selectButton.getActionCommand().equals(actionCommand)) {

            fireActionPerformed();
        }

    }

    public boolean isUseSelectButton() {
        return useSelectButton;
    }

    public void setUseSelectButton(boolean useSelectButton) {
        this.useSelectButton = useSelectButton;
    }

    public String getName() {
        return "List";
    }


    public void updateUI() {
        super.updateUI();
        if (renderer!=null) {
            // TODO ??? what to do here ???
            // find out how swing does it
        }
    }

    public String getToolTipText() {
        if (current!=-1) {
            synchronized (renderer)
            {
                Component c = getComponentFor(current);
                return c.getToolTipText();
            }
        }
        return super.getToolTipText();
    }

    public int getToolTipLocationX() {

        if (current!=-1) {
            synchronized (renderer)
            {
                Component c = getComponentFor(current);
                return c.getX() + c.getToolTipLocationX();
            }
        }

        return super.getToolTipLocationX();
    }
    public int getToolTipLocationY() {

        if (current!=-1) {
            synchronized (renderer)
            {
                Component c = getComponentFor(current);
                return c.getY() + c.getToolTipLocationY();
            }
        }

        return super.getToolTipLocationY();
    }

    // #########################################################################
    // ########################## DefaultListModel #############################
    // #########################################################################

    private Vector items;

    /**
     * @param index
     * @return the element
     * @see javax.swing.ListModel#getElementAt(int) ListModel.getElementAt
     */
    public Object getElementAt(int index) {
        return (items != null) ? items.elementAt(index) : null;
	// && items.size()>index // this method SHOULD throw array index out of bounds if it is
    }

    /**
     * @return the size of the list
     * @see javax.swing.ListModel#getSize() ListModel.getSize
     */
    public int getSize() {
        return items.size();
    }

    /**
     * @param a
     * @see javax.swing.DefaultListModel#addElement(java.lang.Object) DefaultListModel.addElement
     */
    public void addElement(Object a) {

        items.addElement(a);

    }

    /**
     * @param i
     * @see javax.swing.DefaultListModel#removeElementAt(int) DefaultListModel#removeElementAt
     */
    public void removeElementAt(int i) {

        items.removeElementAt(i);
        if (current == i) {
            current = -1;
        }
        else if (current > i) {
            current--;
        }

    }

}

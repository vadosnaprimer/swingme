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
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.logging.Logger;

/**
 * if you would like the list to wrap the items then please use GridList
 * @author Yura Mamyrin
 * @see FileChooser.GridList
 * @see javax.swing.JList
 */
public class List extends Component implements ActionListener {

    private Button selectButton;
//
//    static {
//        selectButton = new Button("Select");
//        selectButton.setActionCommand( "select" );
//        selectButton.setMnemonic(KeyEvent.KEY_SOFTKEY1);
//    }
//
//    public static void setSelectButtonText(String a) {
//        selectButton.setText(a);
//    }
    private boolean useSelectButton;

    private ListCellRenderer renderer;
    private int current;

    private ActionListener al;
    private ChangeListener chl;
    private String actionCommand;

    private boolean loop;
    private boolean horizontal;
    private boolean doubleClick;

    private int fixedCellHeight = -1;
    private int fixedCellWidth = -1;
  
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
        contentsChanged();
    }

    /**
     * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent) ListDataListener.contentsChanged
     */
    public void contentsChanged() {

        if ( current >= getSize()){
            setSelectedIndex(-1);
        }
        else if (current==-1 && getSize() > 0 && isFocusOwner()) {
            setSelectedIndex(0);
        }
    }

    /**
     * @param a the object to select
     * @see javax.swing.JList#setSelectedValue(java.lang.Object, boolean) JList.setSelectedValue
     */
    public void setSelectedValue(Object a) {
        setSelectedIndex( indexOf(a) );
    }

    /**
     * not swing
     */
    public Vector getItems() {
        return items;
    }

    //#mdebug debug
    public String toString() {
        return super.toString() + items;
    }
    //#enddebug

    public void workoutMinimumSize() {

        if (fixedCellWidth!=-1 && fixedCellHeight!=-1) {
            int s = getSize();
            if (horizontal) {
                width = fixedCellWidth*s;
                height = fixedCellHeight;
            }
            else {
                width = fixedCellWidth;
                height = fixedCellHeight*s;
            }
        }
        else {
            int totalHeight = 0;
            int totalWidth = 0;

            for(int i = 0; i < getSize(); i++){
                Object item = getElementAt(i);

                Component c = renderer.getListCellRendererComponent(this, item, i, i == current, false);
                c.workoutSize();
                int w = fixedCellWidth==-1?c.getWidthWithBorder():fixedCellWidth;
                int h = fixedCellHeight==-1?c.getHeightWithBorder():fixedCellHeight;

                if (horizontal) {
                    if (totalHeight<h) {
                        totalHeight=h;
                    }
                }
                else {
                    totalHeight = totalHeight + h;
                }

                if (horizontal) {
                    totalWidth = totalWidth + w;
                }
                else {
                    if (totalWidth<w) {
                        totalWidth=w;
                    }
                }

            }

            width = totalWidth;
            height = totalHeight;
        }
    }

    public void setSize(int w,int h) {
        super.setSize(w, h);

        // if we have changed size since the last request to make a index visible
        // then we must try again
        if (ensureIndexIsVisible!=-1) {
        	if (ensureIndexIsVisible >= getSize()) {
        		ensureIndexIsVisible = -1;
        		return;
        	}
            Component c = getRendererComponentFor(ensureIndexIsVisible);
            boolean yes = isRectVisible( c.getXWithBorder(), c.getYWithBorder(), c.getWidthWithBorder(), c.getHeightWithBorder());
            if (!yes) {
                ensureIndexIsVisible(ensureIndexIsVisible);
            }
        }

    }

    /**
     * @param horizontal
     * @see javax.swing.JList#setLayoutOrientation(int) JList.setLayoutOrientation
     */
    public void setLayoutOrientation(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * @see javax.swing.JList#getLayoutOrientation() JList.getLayoutOrientation
     */
    public boolean getLayoutOrientation() {
        return horizontal;
    }

    public void addActionListener(ActionListener l) {
        //#mdebug warn
        if (al!=null) {
            Logger.warn("trying to add a ActionListener when there is already one registered");
        }
        if (l==null) {
            Logger.warn("trying to add a null ActionListener");
        }
        //#enddebug
        al = l;
    }
    public void removeActionListener(ActionListener l) {

        if (al == l) { al = null; }
        //#mdebug warn
        else {
           Logger.warn("trying to remove a ActionListener that is not registered");
        }
        if (l==null) {
            Logger.warn("trying to remove a null ActionListener");
        }
        //#enddebug
    }

    public void setActionCommand(String ac) {

        actionCommand=ac;
    }
    public String getActionCommand() {
        return actionCommand;
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

    private int[] getComponentAt(int x,int y) {

        int i = -1;
        int offset=0;

        if ((horizontal && x<=0) || (!horizontal && y<=0)) {
            // dont do anything
        }
        else if (x > width || y > height) {
            i = getSize(); // skip everything
        }
        else if (horizontal && fixedCellWidth!=-1) {
            i = x/fixedCellWidth;
            offset = i*fixedCellWidth;
        }
        else if (!horizontal && fixedCellHeight!=-1) {
            i = y/fixedCellHeight;
            offset = i*fixedCellHeight;
        }
        else {

            Component comp=null;

            for(i=0; i < getSize(); i++){
                comp = getComponentFor(i,offset);

                int cw=comp.getWidthWithBorder();
                int ch=comp.getHeightWithBorder();

                int cx = comp.getXWithBorder();
                int cy = comp.getYWithBorder();

                if (
                        (horizontal && x>=cx && x <=(cw+cx) ) ||
                        (!horizontal && y>=cy && y <=(ch+cy))
                ) {
                    break;
                }

                offset = offset + ((horizontal)?cw:ch);
            }

        }

        return new int[] {i,offset} ;
    }

    public void paintComponent(Graphics2D g) {

        ensureIndexIsVisible = -1;

        boolean good=false;
        int clipx = g.getClipX();
        int clipy = g.getClipY();

        int[] objects=getComponentAt(clipx,clipy);
        int i = objects[0];
        int offset = objects[1];
        if (i<0) { // if clip is too up/left
            i=0;
            offset=0;
        }
        for(; i < getSize(); i++){

            Component c = getComponentFor(i,offset);

            //g.setColor(0x0000FF00);
            //g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());

            int x = c.getXWithBorder();
            int y = c.getYWithBorder();

            if (x < clipx+g.getClipWidth() &&
                x + c.getWidthWithBorder() > clipx &&
                y < clipy+g.getClipHeight() &&
                y + c.getHeightWithBorder() > clipy
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

            offset = offset + ((horizontal)?c.getWidthWithBorder():c.getHeightWithBorder());

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

    public Component getRendererComponentFor(int a) {

    	if (horizontal && fixedCellWidth!=-1) {
            return getComponentFor(a,a*fixedCellWidth);
        }
        else if (!horizontal && fixedCellHeight!=-1) {
            return getComponentFor(a,a*fixedCellHeight);
        }

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

        Component c = renderer.getListCellRendererComponent(this, item, i, isSelectedIndex(i), isFocusOwner() && i == current);
        c.workoutSize();

        int w = fixedCellWidth!=-1?fixedCellWidth:c.getWidthWithBorder();
        int h = fixedCellHeight!=-1?fixedCellHeight:c.getHeightWithBorder();

        c.setBoundsWithBorder(
                ((horizontal)?offset:0),
                ((horizontal)?0:offset),
                ((horizontal)?w:width),
                ((horizontal)?height:h)
                );
        return c;

    }

    public void focusLost() {
        super.focusLost();
        if (useSelectButton) {
            getWindow().removeCommand(selectButton);
            selectButton = null;
        }
        repaint();
    }

    public void focusGained() {
        super.focusGained();
        if (getSize() != 0 ) {
            if (current==-1 || current>=getSize()) {
                setSelectedIndex(0);
            }
            else {
                ensureIndexIsVisible(current);
                repaint(); // repaint even if we have not scrolled
            }
        }

        if (useSelectButton) {
            selectButton = new Button( (String)DesktopPane.get("selectText") );
            selectButton.setActionCommand("select");
            selectButton.setMnemonic( KeyEvent.KEY_SOFTKEY1 );
            selectButton.addActionListener(this);
            getWindow().addCommand(selectButton);
        }

    }

    private long doubleClickTime;
    private int doubleClickX,doubleClickY;

    public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
        super.processMouseEvent(type, x, y, keys);

        if (type == DesktopPane.PRESSED || type == DesktopPane.DRAGGED) {
            int i = getComponentAt(x, y)[0];

            if (i>=0 && i<getSize()) {
                selectNewPointer(i,keys);
            }
        }
        else if (type == DesktopPane.RELEASED) {

            if (keys.isDownKey(KeyEvent.KEY_EDIT)) {
                return;
            }
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

    private boolean clearSelectionOnClick=false;
    private void selectNewPointer(int i,KeyEvent keys) {
        if (keys.isDownKey(KeyEvent.KEY_EDIT)) {
            toggleHelper(current,selected==null,false);
            toggleHelper(i,true,true);
        }
        else if (selected!=null && (clearSelectionOnClick || selected.isEmpty())) {
            selected = null;
        }
        setSelectedIndex(i);
    }

    private void selectNewKey(int i,KeyEvent keys) {
        if (keys.isDownKey(KeyEvent.KEY_EDIT) || keys.isDownKey('#')) {
            toggleHelper(current, i==current || addMode , i==current || !addMode  );
            toggleHelper(i, i!=current && addMode , i!=current && !addMode);
        }
        else if (selected!=null && selected.isEmpty()) {
            selected = null;
        }
        setSelectedIndex(i);
    }
    private boolean addMode;
    private void toggleHelper(int i,boolean addTest,boolean removeTest) {
        if (i<0 || (!addTest && !removeTest)) return;
        if (selected==null) {
            selected = new Vector();
        }
        Object obj = getElementAt(i);
        if (selected.contains(obj)) {
            if (removeTest) {
                selected.removeElement(obj);
            }
        }
        else if (addTest) {
            selected.addElement(obj);
        }
    }

    public boolean processKeyEvent(KeyEvent keypad) {

        int size = getSize();

        if (size==0) {
            if (keypad.justPressedAction(Canvas.FIRE) || keypad.justPressedKey('\n')) {
                return fireActionPerformed();
            }
            return false;
        }

        if (keypad.justPressedKey(KeyEvent.KEY_EDIT) || keypad.justPressedKey('#')) {
            addMode = selected == null || !isSelectedIndex(current);
        }

        int next = current+1;
        int prev = current-1;

        if (loop) {
            if (next>=size) { next = (size==0)?-1:0; }
            if (prev<0) { prev = size-1; }
        }
        else {
            if (next>=size) { next=-1; }
        }

        int keyCode = keypad.getIsDownKey();

        if (keyCode > Character.MIN_VALUE && keyCode < Character.MAX_VALUE && keyCode!='#') {

                keyCode = keypad.getKeyChar(keyCode, KeyEvent.getChars( (char)keyCode,javax.microedition.lcdui.TextField.ANY ) ,false);

                for(int i = 0; i < getSize(); i++) {

                        String item = String.valueOf( getElementAt(i) );

                        item = (item==null)?"null":item.toLowerCase();

                        if (!"".equals(item) && item.charAt(0) == keyCode) {

                                selectNewKey(i,keypad);
                                return true;

                        }
                }

                return false;

        }
        else if (keypad.isDownAction(Canvas.DOWN)) {

            if (!horizontal && next>-1) {
                selectNewKey(next,keypad);
                return true;
            }
            //else {
                if (horizontal && current!=-1) {
                    Component c = getRendererComponentFor(current);
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

            if (!horizontal && prev>-1) {
                selectNewKey(prev,keypad);
                return true;
            }
            //else {
                if (horizontal && current!=-1) {
                    Component c = getRendererComponentFor(current);
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

            if (horizontal && next>-1) {
                selectNewKey(next,keypad);
                return true;
            }
            //else {
                // TODO could get rid of this check and add the X pos to the width
                // so you can scroll right even in horizontal mode
                if (!horizontal && current!=-1) {
                    Component c = getRendererComponentFor(current);
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

            if (horizontal && prev>-1) {
                selectNewKey(prev,keypad);
                return true;
            }
            //else {
                if (!horizontal && current!=-1) {
                    Component c = getRendererComponentFor(current);
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
            if (keypad.isDownKey(KeyEvent.KEY_EDIT) || keypad.isDownKey('#')) {
                selectNewKey(current,keypad);
                return true;
            }
            else {
                return fireActionPerformed();
            }
        }
        else {
            //if we did not consume the event
            return false;
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
        setSelectedIndex(a, true);
    }

    /**
     *
     * @param a the index ofthe one cell to select
     * @param moveScroll if true the scroll positon of the list will be
     * modified to make the newly selected index visible
     */
    public void setSelectedIndex(int a, boolean moveScroll) {
    	
        int old = current;

        current = a;
        
        if (current!=-1) {

            if (moveScroll) {
                ensureIndexIsVisible(current);
            }

            if (chl!=null && old!=current) {
                chl.changeEvent(this,current);
            }
            // TODO as scroll to always does a repaint
            // we dont need it here
            // BUT what if we are not in a scrollPane??
            repaint();            
        }
    }

    private int ensureIndexIsVisible=-1;
    /**
     * @see javax.swing.JList#ensureIndexIsVisible(int) JList.ensureIndexIsVisible
     */
    public void ensureIndexIsVisible(int i) {
    
            Component c = getRendererComponentFor(i);
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
                //calc x in relation to the viewport
                int x = posX;
                Component p = parent;
                while (p!=null) {
                    if (p instanceof ScrollPane) {
                        break;
                    }
                    x+=p.posX;
                    p = p.parent;
                } // TODO take into accountthe viewPortX

                scrollRectToVisible( -x, c.getYWithBorder(), 1, c.getHeightWithBorder(),false);
            }
            ensureIndexIsVisible = i;
    }

    public void actionPerformed(String actionCommand) {

        if(selectButton.getActionCommand().equals(actionCommand)) {
            fireActionPerformed();
        }
        //#mdebug warn
        else {
            Logger.warn("unknown command in List actionPerformed: " + actionCommand);
        }
        //#enddebug

    }

    public boolean isUseSelectButton() {
        return useSelectButton;
    }

    public void setUseSelectButton(boolean useSelectButton) {
        this.useSelectButton = useSelectButton;
    }

    public String getDefaultName() {
        return "List";
    }

    public void updateUI() {
        super.updateUI();
        // as this is called by the default Component constructor
        // the renderer would not have been set the very first time it is called
        if (renderer!=null) {
            // real Swing does a check if renderer is instanceof Component
            // and only then calls updateUI on it, this will fail on renderers that
            // are not instances of a component, so thats why this method has been
            // added into the interface, and can then call updateUI on its internal
            // components or many of them if it has more then 1
            renderer.updateUI();
        }
        if (selectButton!=null) {
            selectButton.updateUI();
        }
    }

    public String getToolTipText() {
        if (current!=-1) {
            Component c = getRendererComponentFor(current);
            return c.getToolTipText();
        }
        return super.getToolTipText();
    }

    public int getToolTipLocationX() {
        if (current!=-1) {
            Component c = getRendererComponentFor(current);
            return c.getX() + c.getToolTipLocationX();
        }
        return super.getToolTipLocationX();
    }
    public int getToolTipLocationY() {
        if (current!=-1) {
                Component c = getRendererComponentFor(current);
                return c.getY() + c.getToolTipLocationY();
        }
        return super.getToolTipLocationY();
    }

    /**
     * @param height
     * @see javax.swing.JList#setFixedCellHeight(int) JList.setFixedCellHeight
     */
    public void setFixedCellHeight(int height) {
        fixedCellHeight = height;
    }

    public int getFixedCellHeight(){
        return fixedCellHeight;
    }

    /**
     * @param width
     * @see javax.swing.JList#setFixedCellWidth(int) JList.setFixedCellWidth
     */
    public void setFixedCellWidth(int width) {
        fixedCellWidth = width;
    }

    public Vector getSelectedValues() {
        if (selected==null) {
            Vector v = new Vector(1);
            if (current>=0) {
                v.addElement(getElementAt(current));
            }
            return v;
        }
        return selected;
    }

    public void setSelectedValues(Vector v) {
        selected = v;
    }

//    /**
//     * @see javax.swing.JList#indexToLocation(int) JList.indexToLocation
//     */
//    public int[] indexToLocation(int index) {
//        Component comp = getRendererComponentFor(index);
//        return new int[] { comp.getXWithBorder(), comp.getYWithBorder() };
//    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== DefaultListModel ====================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    private Vector items;

    /**
     * @see javax.swing.DefaultListModel#indexOf(java.lang.Object) DefaultListModel.indexOf
     */
    public int indexOf(Object a) {

        int i=-1,s=getSize();
        for (int c=0;c<s;c++) {
            Object b = getElementAt(c);
            if (a == b || (a!=null && a.equals(b))) {
                i=c;
                break;
            }
        }
        return i;
    }

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
     * @param index
     * @return the element
     * @see javax.swing.DefaultListModel#setElementAt(int) ListModel.setElementAt
     */
    public void setElementAt(Object object, int index) {
        items.setElementAt(object, index);
    }

    /**
     * @param index
     * @return the element
     * @see javax.swing.DefaultListModel#insertElementAt(int) ListModel.insertElementAt
     */
    public void insertElementAt(Object object, int index) {
        items.insertElementAt(object, index);
    }

    /**
     * @return the size of the list
     * @see javax.swing.ListModel#getSize() ListModel.getSize
     */
    public int getSize() {
        return (items != null) ? items.size() : -1;
    }

    /**
     * @param a
     * @see javax.swing.DefaultListModel#addElement(java.lang.Object) DefaultListModel.addElement
     */
    public void addElement(Object a) {
    	items.addElement(a);
        contentsChanged();
    }

    /**
     * @param obj the object to remove
     * @see javax.swing.DefaultListModel#removeAllElements(java.lang.Object) DefaultListModel#removeElement
     */
    public boolean removeElement(Object obj) {
        int idx = indexOf(obj);
        if (idx >= 0) {
            removeElementAt(idx);
            return true;
        }

        return false;
    }

    /**
     * @see javax.swing.DefaultListModel#removeAllElements(java.lang.Object) DefaultListModel#removeAllElements
     */
    public void removeAllElements() {
        items.removeAllElements();
        contentsChanged();
    }

    /**
     * @param i
     * @see javax.swing.DefaultListModel#removeElementAt(int) DefaultListModel#removeElementAt
     */
    public void removeElementAt(int i) {
        items.removeElementAt(i);
        if (current > i) { //  we have removed something before the current position
            current--;
        }
        else if (current == i) { // we have removed the current item
            setSelectedIndex(-1);
        }
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== ListSelectionModel ==================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    private Vector selected;

    /**
     * @see javax.swing.ListSelectionModel#isSelectedIndex(int) ListSelectionModel.isSelectedIndex
     */
    public boolean isSelectedIndex(int index) {
        return selected==null?current == index:selected.contains(getElementAt(index));
    }

}

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

    /**
     * @see javax.swing.JList#VERTICAL JList.VERTICAL
     */
    public static final int VERTICAL = 0;

    /**
     * Not supported in Swing
     */
    public static final int HORIZONTAL = 3;

    /**
     * @see javax.swing.JList#VERTICAL_WRAP JList.VERTICAL_WRAP
     */
    //public static final int VERTICAL_WRAP = 1;// same as Swing

    /**
     * @see javax.swing.JList#HORIZONTAL_WRAP JList.HORIZONTAL_WRAP
     */
    //public static final int HORIZONTAL_WRAP = 2; // same as Swing

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
    private int layoutOrientation;
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
        this(a,new DefaultListCellRenderer(),VERTICAL);
    }

    public List(ListCellRenderer a) {
        this(null,a,VERTICAL);
    }

    // real constructor!
    public List(Vector a,ListCellRenderer b,int h) {
        items = a;
        if (items==null) {
            items = new Vector();
        }
        setCellRenderer(b);
        layoutOrientation = h;
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

    protected void workoutMinimumSize() {

        if (fixedCellWidth!=-1 && fixedCellHeight!=-1) {
            int s = getSize();
            if (layoutOrientation==HORIZONTAL) {
                width = fixedCellWidth*s;
                height = fixedCellHeight;
            }
            else {
                width = fixedCellWidth;
                height = fixedCellHeight*s;
            }
        }
        else {
            //TODO should we NOT do this if pref size is set?

            int totalHeight = 0;
            int totalWidth = 0;

            for(int i = 0; i < getSize(); i++){
                Object item = getElementAt(i);

                Component c = renderer.getListCellRendererComponent(this, item, i, i == current, false);
                c.workoutPreferredSize();
                int w = fixedCellWidth==-1?c.getWidthWithBorder():fixedCellWidth;
                int h = fixedCellHeight==-1?c.getHeightWithBorder():fixedCellHeight;

                if (layoutOrientation==HORIZONTAL) {
                    if (totalHeight<h) {
                        totalHeight=h;
                    }
                }
                else {
                    totalHeight = totalHeight + h;
                }

                if (layoutOrientation==HORIZONTAL) {
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
     * @see javax.swing.JList#setLayoutOrientation(int) JList.setLayoutOrientation
     */
    public void setLayoutOrientation(int layoutOrientation) {
        this.layoutOrientation = layoutOrientation;
    }

    /**
     * @see javax.swing.JList#getLayoutOrientation() JList.getLayoutOrientation
     */
    public int getLayoutOrientation() {
        return layoutOrientation;
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

    public ActionListener getActionListener() {
        return al;
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

    /**
     * will return a int[] of size 2, at 0 is the index, at 1 is the offset of that index.
     * will return -1 if no cell is found
     * @see javax.swing.JList#locationToIndex(java.awt.Point) JList.locationToIndex
     */
    public int[] locationToIndex(int x,int y) {

        int ri = -1;
        int roffset=0;

        int size = getSize();

        if (size==0 ||
                (layoutOrientation==HORIZONTAL && x<0) ||
                (layoutOrientation==VERTICAL && y<0) ||
                (layoutOrientation==HORIZONTAL && x>width) ||
                (layoutOrientation==VERTICAL && y>height)
                ) {
            // dont do anything
        }
        else if (layoutOrientation==HORIZONTAL && fixedCellWidth!=-1) {
            ri = x/fixedCellWidth;
            if (ri<0||ri>=size) {
                ri = -1;
            }
            else {
                roffset = ri*fixedCellWidth;
            }
        }
        else if (layoutOrientation==VERTICAL && fixedCellHeight!=-1) {
            ri = y/fixedCellHeight;
            if (ri<0||ri>=size) {
                ri = -1;
            }
            else {
                roffset = ri*fixedCellHeight;
            }
        }
        else {

            Component comp=null;

            for(int i=0,offset=0; i < size; i++){
                comp = getComponentFor(i,offset);

                int cw=comp.getWidthWithBorder();
                int ch=comp.getHeightWithBorder();

                int cx = comp.getXWithBorder();
                int cy = comp.getYWithBorder();

                if (
                        (layoutOrientation==VERTICAL || x>=cx && x<(cx+cw) ) &&
                        (layoutOrientation==HORIZONTAL || y>=cy && y<(cy+ch))
                ) {
                    ri = i;
                    roffset = offset;
                    break;
                }

                offset = offset + ((layoutOrientation==HORIZONTAL)?cw:ch);
            }

        }

        return new int[] {ri,roffset} ;
    }

    /**
     * @see Table#paintComponent(net.yura.mobile.gui.Graphics2D)
     */
    public void paintComponent(Graphics2D g) {

        //System.out.println("list "+getFirstVisibleIndex()+"-"+getLastVisibleIndex());

        ensureIndexIsVisible = -1;

        boolean good=false;
        int clipx = g.getClipX();
        int clipy = g.getClipY();
        int clipw = g.getClipWidth();
        int cliph = g.getClipHeight();

        int[] objects=locationToIndex(clipx,clipy);
        int i = objects[0];
        int offset = objects[1];
        if (i<0) { // if clip is too up/left or bottom/right
            if ((layoutOrientation==HORIZONTAL && clipx<0) || (layoutOrientation==VERTICAL && clipy<0)) {
                i=0;
                offset=0;
            }
            else {
                return;
            }
        }
        for(; i < getSize(); i++){
            Component c = getComponentFor(i,offset);

            //g.setColor(0x0000FF00);
            //g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());

            int x = c.getXWithBorder();
            int y = c.getYWithBorder();

            if (x < clipx+clipw &&
                x + c.getWidthWithBorder() > clipx &&
                y < clipy+cliph &&
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

            offset = offset + ((layoutOrientation==HORIZONTAL)?c.getWidthWithBorder():c.getHeightWithBorder());

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

    	if (layoutOrientation==HORIZONTAL && fixedCellWidth!=-1) {
            return getComponentFor(a,a*fixedCellWidth);
        }
        else if (layoutOrientation==VERTICAL && fixedCellHeight!=-1) {
            return getComponentFor(a,a*fixedCellHeight);
        }

        int offset=0;

        // only need to do this looping for HORIZONTAL or VERTICAL
        if (layoutOrientation==HORIZONTAL || layoutOrientation==VERTICAL) {
            for(int i = 0; true; i++){
                Component c = getComponentFor(i,offset);
                if (i==a) {
                    return c;
                }
                offset = offset + ((layoutOrientation==HORIZONTAL)?c.getWidthWithBorder():c.getHeightWithBorder());
            }
        }

        // this is used by android menus and all other wrapping lists
        // if we are not HORIZONTAL or VERTICAL
        return getComponentFor(a,offset); // offset is ZERO

    }

    protected Component getComponentFor(int i,int offset) {

        Object item = getElementAt(i);

        Component c = renderer.getListCellRendererComponent(this, item, i, isSelectedIndex(i), isFocusOwner() && i == current);

        // if we need to put this back, someone needs to write WHY we do
        if (fixedCellWidth==-1||fixedCellHeight==-1) {
            c.workoutPreferredSize();
        }

        int w = fixedCellWidth!=-1?fixedCellWidth:c.getWidthWithBorder();
        int h = fixedCellHeight!=-1?fixedCellHeight:c.getHeightWithBorder();

        c.setBoundsWithBorder(
                ((layoutOrientation==HORIZONTAL)?offset:0),
                ((layoutOrientation==HORIZONTAL)?0:offset),
                ((layoutOrientation==HORIZONTAL)?w:width),
                ((layoutOrientation==HORIZONTAL)?height:h)
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

        if (!isFocusable()) {
            // if we are disabled then we do not do anything!
        }
        else if (type == DesktopPane.PRESSED || type == DesktopPane.DRAGGED) {
            int i = locationToIndex(x, y)[0];

            if (i>=0) { // if (ri<0||ri>=size) { this check is not needed any more
                selectNewPointer(i,keys);
            }
            else {
                setSelectedIndex(-1);
            }
        }
        else if (type == DesktopPane.RELEASED) {

            if (isCtrlKeyDown(keys)) {
                return;
            }
            if (doubleClick) {
                long time = System.currentTimeMillis();

                if (time < doubleClickTime + 300 && x>(doubleClickX-5) && x<(doubleClickX+5) && y>(doubleClickY-5) && y<(doubleClickY+5) ) {
                    mouseClicked(x,y);
                }

                doubleClickTime = time;
                doubleClickX = x;
                doubleClickY = y;
            }
            else {
                mouseClicked(x,y);
            }
        }
    }

    /**
     * works for normal and double click mode
     * @see #setDoubleClick(boolean)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent) MouseListener.mouseClicked
     */
    public void mouseClicked(int x,int y) {
        // TODO maybe only fire the action if we are clicking on a cell and not an empty space
        fireActionPerformed();
    }

    public boolean consumesMotionEvents() {
        ScrollPane sp = (ScrollPane)DesktopPane.getAncestorOfClass(ScrollPane.class, this);
        return (sp==null || (sp.getViewPortHeight() >= sp.getView().getHeight() && sp.getViewPortWidth() >= sp.getView().getWidth()));
    }

    private boolean clearSelectionOnClick=false;
    private void selectNewPointer(int i,KeyEvent keys) {
        if (isCtrlKeyDown(keys)) {
            toggleHelper(current,selected==null,false);
            toggleHelper(i,true,true);
        }
        else if (selected!=null && (clearSelectionOnClick || selected.isEmpty()) && selectedCreated) {
            clearSelection();
        }
        setSelectedIndex(i);
    }
    private void selectNewKey(int i,KeyEvent keys) {
        if (isCtrlKeyDown(keys)) {
            toggleHelper(current, i==current || addMode , i==current || !addMode  );
            toggleHelper(i, i!=current && addMode , i!=current && !addMode);
        }
        else if (selected!=null && selected.isEmpty() && selectedCreated) {
            clearSelection();
        }
        setSelectedIndex(i);
    }
    private boolean isCtrlKeyDown(KeyEvent keys) {
        return keys.isDownKey(KeyEvent.KEY_EDIT) || keys.isDownKey('#');
    }

    private boolean addMode,selectedCreated;
    private void toggleHelper(int i,boolean addTest,boolean removeTest) {
        if (i<0 || (!addTest && !removeTest)) return;
        if (selected==null) {
            selectedCreated = true;
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
        //if (isCtrlKeyDown(keypad)) { // CAN NOT USE THIS, as this checks isDownKey and we need justPressedKey
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

        if (keyCode >= KeyEvent.MIN_INPUT_VALUE && keyCode < Character.MAX_VALUE && keyCode!='#' && keyCode!='\n') {

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

            if (layoutOrientation!=HORIZONTAL && next>-1) {
                selectNewKey(next,keypad);
                return true;
            }
            //else {
                if (layoutOrientation!=VERTICAL && current!=-1) {
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

            if (layoutOrientation!=HORIZONTAL && prev>-1) {
                selectNewKey(prev,keypad);
                return true;
            }
            //else {
                if (layoutOrientation!=VERTICAL && current!=-1) {
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

            if (layoutOrientation!=VERTICAL && next>-1) {
                selectNewKey(next,keypad);
                return true;
            }
            //else {
                // TODO could get rid of this check and add the X pos to the width
                // so you can scroll right even in horizontal mode
                if (layoutOrientation!=HORIZONTAL && current!=-1) {
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

            if (layoutOrientation!=VERTICAL && prev>-1) {
                selectNewKey(prev,keypad);
                return true;
            }
            //else {
                if (layoutOrientation!=HORIZONTAL && current!=-1) {
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
            if (isCtrlKeyDown(keypad)) {
                selectNewKey(current,keypad);
                return true;
            }
            return fireActionPerformed();
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

        if (current!=-1 && moveScroll) {
            ensureIndexIsVisible(current);
        }
        if (current!=old) {
            // TODO as scroll to always does a repaint
            // we dont need it here
            // BUT what if we are not in a scrollPane??
            repaint();
            if (chl!=null) {
                chl.changeEvent(this,current);
            }
        }
    }

    public void makeVisible() {
        // as we use ensureIndexIsVisible to make the current item visible, we do not need makeVisible
        // TODO, as components can have components inside them that have focus themselves, maybe they should handle the makeVisible themselves
        //#debug debug
        System.out.println("skip makeVisible");
    }

    private int ensureIndexIsVisible=-1;
    /**
     * @see javax.swing.JList#ensureIndexIsVisible(int) JList.ensureIndexIsVisible
     */
    public void ensureIndexIsVisible(int i) {

            int pos,size;
            if (layoutOrientation==HORIZONTAL && fixedCellWidth!=-1) {
                pos = i*fixedCellWidth;
                size = fixedCellWidth;
            }
            else if (layoutOrientation==VERTICAL && fixedCellHeight!=-1) {
                pos = i*fixedCellHeight;
                size = fixedCellHeight;
            }
            else {
                // only get the RendererComponent if we really need to
                Component c = getRendererComponentFor(i);
                if (layoutOrientation==HORIZONTAL) {
                    pos = c.getXWithBorder();
                    size = c.getWidthWithBorder();
                }
                else {
                    pos = c.getYWithBorder();
                    size = c.getHeightWithBorder();
                }
            }

            // good, but too simple
            // what if we are scrolled right already?
            //scrollTo(c);

            // THIS WILL NOT WORK if list in inside a panel inside a scrollpane
            // as posX and posY will be wrong!
            // ALSO WILLNOT WORK IN BOXLAYOUT HCENTRE IF NOT THE FIRST COMPONENT
            if (layoutOrientation==HORIZONTAL) {
                scrollRectToVisible( pos, -posY, size, 1,false);
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

                scrollRectToVisible( -x, pos, 1, size,false);
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
        //#mdebug debug
        if (height==0) {
            throw new RuntimeException("trying to setFixedCellHeight to 0");
        }
        //#enddebug
        fixedCellHeight = height;
    }

    /**
     * @see javax.swing.JList#getFixedCellHeight() JList.getFixedCellHeight
     */
    public int getFixedCellHeight(){
        return fixedCellHeight;
    }

    /**
     * @param width
     * @see javax.swing.JList#setFixedCellWidth(int) JList.setFixedCellWidth
     */
    public void setFixedCellWidth(int width) {
        //#mdebug debug
        if (width==0) {
            throw new RuntimeException("trying to setFixedCellWidth to 0");
        }
        //#enddebug
        fixedCellWidth = width;
    }

    /**
     * @see javax.swing.JList#getFixedCellWidth() JList.getFixedCellWidth
     */
    public int getFixedCellWidth(){
        return fixedCellWidth;
    }

    /**
     * maybe should be in list, but is used in a few places
     * @see javax.swing.JList#setPrototypeCellValue(java.lang.Object) JList.setPrototypeCellValue
     */
    public void setPrototypeCellValue(Object prototypeCellValue)  {

        Component c = renderer.getListCellRendererComponent(this, prototypeCellValue, 0, false, false);
        c.workoutPreferredSize();
        // TODO is is enough that we set one and not the other?
        if (getLayoutOrientation()==List.VERTICAL) {
            setFixedCellHeight( c.getHeightWithBorder() );
        }
        else {
            setFixedCellWidth( c.getWidthWithBorder() );
        }
    }

    /**
     * if either getFirstVisibleIndex OR getLastVisibleIndex return -1 then nothing is visible
     * @see javax.swing.JList#getFirstVisibleIndex() JList.getFirstVisibleIndex
     */
    public int getFirstVisibleIndex() {
        int[] v = getVisibleRect();
        int x = v[0];
        int y = v[1];
        int i = locationToIndex(x,y)[0];
        if (i<0 && ((layoutOrientation==HORIZONTAL && x<0) || (layoutOrientation==VERTICAL && y<0)) ) {
            return 0;
        }
        return i;
    }

    /**
     * if either getFirstVisibleIndex OR getLastVisibleIndex return -1 then nothing is visible
     * @see javax.swing.JList#getLastVisibleIndex() JList.getLastVisibleIndex
     */
    public int getLastVisibleIndex() {
        int[] v = getVisibleRect();
        int x = (v[0]+v[2])-1;
        int y = (v[1]+v[3])-1;
        int i = locationToIndex(x,y)[0];
        if (i<0 && ((layoutOrientation==HORIZONTAL && x>=0) || (layoutOrientation==VERTICAL && y>=0)) ) {
            return getSize()-1;
        }
        return i;
    }

    /**
     * @see javax.swing.JList#getSelectedValues() JList.getSelectedValues
     */
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

    /**
     * @see javax.swing.JList#setSelectedValue(java.lang.Object, boolean) JList.setSelectedValue
     */
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

    /**
     * @see javax.swing.JList#clearSelection() JList.clearSelection
     * @see javax.swing.ListSelectionModel#clearSelection() ListSelectionModel.clearSelection
     */
    public void clearSelection() {
        selected = null;
    }

}

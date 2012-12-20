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
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 */
public class MenuBar extends List implements ActionListener {

    MenuBar owner;

    public MenuBar() {
        setLayoutOrientation(List.HORIZONTAL);
        setCellRenderer( new MenuItemRenderer() );
        setActionCommand("activate");
        addActionListener(this);
    }

    public boolean processKeyEvent(KeyEvent keyEvent) {
        if (getLayoutOrientation()==List.VERTICAL && getSelectedValue() instanceof Menu && keyEvent.justPressedAction(Canvas.RIGHT)) {
            fireActionPerformed();
            return true;
        }
        return super.processKeyEvent(keyEvent);
    }

    public boolean isVisible() {

        // TODO not sure if this should be here or frame
        // if we are on a phone, and the window is not maximised, this bar is not visible
        if (getDesktopPane().SOFT_KEYS && isFrameMenuBar() && !((Frame)getWindow()).isMaximum()) {
            return false;
        }
        return super.isVisible();
    }

    public String getDefaultName() {
        return "MenuBar";
    }

    /**
     * @see Panel#add(net.yura.mobile.gui.components.Component)
     * @see java.awt.Container#add(java.awt.Component) Container.add
     */
    public void addImpl(Component button,Object cons, int index) {
    	//#mdebug debug
    	if (getItems().contains(button)) {
    		throw new RuntimeException("can not add the same button twice: "+button);
    	}
    	//#enddebug

        super.addImpl(button, cons, index);

        if (index==-1) {
            super.addElement(button);
        }
        else {
            setSelectedIndex(-1); // TODO can be done better
            getItems().insertElementAt(button, index);
        }

        if (isFrameMenuBar() && button instanceof Button) {
            autoMnemonic();
        }


        // hack to make menu items have left alignment
        if (button instanceof Button && button.getName().equals("Button")){
            ((Button)button).setName("MenuItem");
            ((Button)button).setHorizontalAlignment(Graphics.LEFT);
        }

    }

    void autoMnemonic() {
        if (getSize()>0 && getElementAt(0) instanceof Menu && ((Button)getElementAt(0)).getMnemonic()==0) {
            ((Button)getElementAt(0)).setMnemonic(KeyEvent.KEY_MENU);
        }
    }

    /**
     * @see Panel#removeAll()
     * @see java.awt.Container#removeAll()
     */
    public void removeAll() {
        while (super.getSize()>0) {
            remove( (Component)super.getElementAt( super.getSize()-1 ) );
        }
    }

    /**
     * @see Panel#remove(net.yura.mobile.gui.components.Component)
     * @see java.awt.Container#remove(java.awt.Component) Container.remove
     */
    public void remove(Component c) {
        setSelectedIndex(-1); // TODO can be done better
        getItems().removeElement(c); // we can not call removeElement as that will not work on non-visible items
        c.removeParent(this);
    }

    /**
     * @see java.awt.Container#getComponentCount() Container.getComponentCount
     */
    public int getComponentCount() {
        return super.getSize();
    }

    //#mdebug debug
    public boolean removeElement(Object obj) {
        throw new RuntimeException("do not use this as it will fail on removing non-visible components, use remove() instead");
    }
    public void addElement(Object a) {
        throw new RuntimeException("do not use this as it will fail, use add() instead");
    }
    //#enddebug

    public void actionPerformed(String actionCommand) {
        if ("activate".equals(actionCommand)) {
            int index = getSelectedIndex();
            if (index >= 0) {
                Button button = (Button)getElementAt(index);
                Component comp = getRendererComponentFor( index );
                button.setBoundsWithBorder(comp.getXWithBorder(), comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
                if (!(button instanceof Menu)) {
                    actionPerformed(Frame.CMD_CLOSE);
                }
                button.fireActionPerformed();
            }
        }
        else if (Frame.CMD_CLOSE.equals(actionCommand)) {

            setSelectedIndex(-1);

            // if we are the frame menu bar then we want to unfocus us
            if (isFrameMenuBar()) {
                Window w = getWindow();
                if (w.getFocusOwner() == this) {
                    w.setFocusedComponent(null);
                }
            }

            Window win = getWindow();
            if (win.closeOnFocusLost) {
                win.setVisible(false);
            }

            if (owner!=null) {
                owner.actionPerformed(Frame.CMD_CLOSE);
            }

        }
        else {
            super.actionPerformed(actionCommand);
        }
    }

    public Button findMneonicButton(int mnu) {
        Vector items = getItems();
        for(int i = 0; i < items.size(); i++) {
            Object component = items.elementAt(i);
            if (component instanceof Button) {
                Button button = (Button)component;
                if (button.getMnemonic() == mnu) {
                    if (button.isVisible()) {
                        Component comp = getRendererComponentFor( indexOf(button) ); // we need pass the visible index, not the index in the Vector
                        button.setBoundsWithBorder(comp.getXWithBorder(), comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
                    }
                    return button;
                }
                else if (component instanceof Menu) {
                    Button button1 = ((Menu)component).findMneonicButton(mnu);
                    if (button1!=null) {
                        return button1;
                    }
                }
            }
        }
        return null;

    }

    // we can only allow focusable components to be selected
    public void setSelectedIndex(int a) {
        if (a>=0) {
            int current = getSelectedIndex();
            Component c;
            while (true) {
                c = (Component)getElementAt( a ); // TODO should check if in range
                if (c==null || c.isFocusable()) {
                    break;
                }
                if ( current > a) {
                    a--;
                }
                else if (current < a) {
                    a++;
                }
            }
            if (c==null) {
                a = current;
            }
        }
        super.setSelectedIndex(a);
    }

    public void paintComponent(Graphics2D g) {
        DesktopPane dp = getDesktopPane();
        int off = 0;
        // if we are on a phone, and the window is not maximised, this bar is not visible
        if (dp.SOFT_KEYS && isFrameMenuBar() ) {
            Button b1 = dp.getSelectedFrame().findMnemonicButton(KeyEvent.KEY_SOFTKEY1);
            if (b1!=null) {
                off = b1.getWidthWithBorder();
            }
            Button b2 = dp.getSelectedFrame().findMnemonicButton(KeyEvent.KEY_MENU);
            if (b2!=null) {
                off = b2.getWidthWithBorder();
            }

            if (dp.isSideSoftKeys()) {
                int minWidth=0;
                ListCellRenderer renderer = getCellRenderer();
                int current = getSelectedIndex();
                int size = getSize();
                for(int i = 0; i < size; i++){
                    Object item = getElementAt(i);
                    Component c = renderer.getListCellRendererComponent(this, item, i, i == current, false);
                    c.workoutPreferredSize();
                    minWidth = minWidth + c.getWidthWithBorder();
                }
                off = width - off - minWidth;
            }
        }
        g.translate(off, 0);
        super.paintComponent(g);
        g.translate(-off, 0);

        paintDividers(g);
    }

    private boolean isFrameMenuBar() {
        Window w = getWindow();
        return (w instanceof Frame && ((Frame)w).getMenuBar() == this );
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== MODEL ===============================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    public Object getElementAt(int index) {
        Vector items = getItems();
        int count=0;
        for (int c=0;c<items.size();c++) {
            if ( ((Component)items.elementAt(c)).isVisible() ) {
                if (count == index) {
                    return items.elementAt(c);
                }
                count++;
            }
        }
        return null;
    }

    public int getSize() {
        Vector items = getItems();
        int count=0;
        for (int c=0;c<items.size();c++) {
            if ( ((Component)items.elementAt(c)).isVisible() ) {
                count++;
            }
        }
        return count;
    }


    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== ANDROID =============================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    private int cols;

    private boolean firstMenu() {
        return (owner != null && owner.isFrameMenuBar() );
    }

    protected Component getComponentFor(int i,int offset) {

        Component c = super.getComponentFor(i, offset);

        if (getDesktopPane().GRID_MENU && firstMenu()) {

            final int size = getSize();

            final int topRowCols = size % cols; // items in the top row!
            final int h = (getHeight()+getDividerHeight())/((size/cols)+(topRowCols==0?0:1));

            final int ypos,mycols;

            if (i<topRowCols) {
                ypos = 0;
                mycols = topRowCols;
            }
            else {
                int x = i-topRowCols;
                i = x%cols; // i is now being defined as meaning the current col that i am on in thie row!!!
                mycols = cols;
                ypos = h*(x/cols)+(topRowCols==0?0:h);
            }

            final int w = (getWidth() + getDividerWidth()) /mycols;

            final int widthMinusDiv = (i==(mycols-1)) ? (getWidth() - w*i) : (w - getDividerWidth() );
            final int heightMinusDiv = h - getDividerHeight();

            c.setBoundsWithBorder(w*i, ypos, widthMinusDiv, heightMinusDiv);
        }

        return c;
    }

    protected void workoutMinimumSize() {

        DesktopPane dp = getDesktopPane();

        // in softkey mode if the bar is at the bottom of a window
        // we do not want it getting focus when we click on it
        // unless we have something focusable in it, but thats rare
        // TODO, if we mix focuable AND softkeys, this will make the
        // menu bar draw over the top of the softkeys, should not.
        if (dp.SOFT_KEYS && isFrameMenuBar() ) {
            boolean focus=false;
            int size = getSize();
            for(int i = 0; i < size; i++){
                Component item = (Component)getElementAt(i);
                if (item.isFocusable()) {
                    focus = true;
                    break;
                }
            }
            this.focusable = focus;
            //setFocusable(focus); // we can NOT call this method, as if we do, it tries to setup the newFocusedComponent, and it will get this wrong
                                   // as it has not layed out the panel yet, as we are only in the workoutMinimumSize stage at this time
        }

        if (dp.GRID_MENU) {

            int size = getSize();
            ListCellRenderer renderer = getCellRenderer();

            int w=0,h=0; // max width and height

            boolean icon =false;
            for(int i = 0; i < size; i++){
                Object item = getElementAt(i);
                if (item instanceof Button) {
                    Button button = ((Button)item);

                    // hack to change position of icons on buttons
                    if ( firstMenu() ) {
                        button.setHorizontalAlignment(Graphics.HCENTER);
                        button.setVerticalAlignment(Graphics.VCENTER);
                        button.setHorizontalTextPosition(Graphics.HCENTER);
                        button.setVerticalTextPosition(Graphics.BOTTOM);
                    }

                    // this means the icon makes this item extra tall, so we will not need to
                    // TODO the default android icons are very tall and so stretch this to be very tall even though they are positioned to the side of the text
                    if (button.getIcon()!=null ) { // && (button.getVerticalTextPosition() == Graphics.TOP || button.getVerticalTextPosition() == Graphics.BOTTOM )) {
                        icon = true;
                    }
                }

                Component c = renderer.getListCellRendererComponent(this, item, i, false, false);
                c.workoutPreferredSize();

                if (w < c.getWidthWithBorder()) {
                    w = c.getWidthWithBorder();
                }
                if (h < c.getHeightWithBorder()) {
                    h = c.getHeightWithBorder();
                }
            }

            if (!icon) { // if we have no icons, we need extra height for fat fingers
                h = h + h/2;
            }
            if (w<h) { // if the width is too small we want to make it wider
                w = h;
            }

            // we should NEVER set a size to 0, so instead we use -1
            setFixedCellHeight(h==0?-1:h);
            setFixedCellWidth(w==0?-1:w);

            if (firstMenu()) {

                getWindow().setName("AndroidMenu");
                setLayoutOrientation( -1 );

                int in = 0;
                for (Component p=this;p!=null;p=p.getParent()) {
                    in = p.getInsets().getRight() + p.getInsets().getLeft();
                }
                width = dp.getWidth() -in;

                cols = Math.max(Math.min( (width+getDividerWidth()) / (w+getDividerWidth() ),size),1); // TODO very long buttons will be truncated, is this ok?

                int rows = ((size / cols)+( (size%cols==0) ?0:1));

                cols = size/rows + ( (size%rows==0) ?0:1); // this is to not have a HUGE top row but if possible split the total cells into a nice grid

                height = (h*rows) + ((rows-1)*getDividerHeight());

                return;
            }
        }

        super.workoutMinimumSize();

    }

    protected void paintDividers(Graphics2D g) {

        int size = getSize();

        if (size==0) return;

        int rows=size;

        if (getDesktopPane().GRID_MENU && firstMenu()) {
            int topRowCols = size % cols; // items in the top row!
            rows = ((size/cols)+(topRowCols==0?0:1));

            if (verticalDivider!=null) {
                int topRowHeight = 0;
                if (topRowCols>0) {
                    topRowHeight = getHeight()/rows;
                    int w = (getWidth()+getDividerWidth())/topRowCols;
                    for (int c=1;c<topRowCols;c++) {
                        int x = c*w;
                        int y = verticalDivider.getLeft();
                        g.translate(x, y);
                        verticalDivider.paintBorder(this, g, 0, topRowHeight);
                        g.translate(-x, -y);
                    }
                }

                int w = (getWidth()+getDividerWidth())/cols;
                for (int c=1;c<cols;c++) {
                    int x = c*w;
                    int y = topRowHeight + verticalDivider.getLeft();
                    g.translate(x, y);
                    verticalDivider.paintBorder(this, g, 0, getHeight()-topRowHeight);
                    g.translate(-x, -y);
                }
            }
        }

        if (divider!=null) {
            int h = (getHeight()+getDividerHeight())/rows;
            for (int c=1;c<rows;c++) {
                int x = divider.getLeft();
                int y = c*h;
                g.translate(x, y);
                divider.paintBorder(this, g, width, 0);
                g.translate(-x, -y);
            }
        }
    }

    private int getDividerHeight() {
        return (divider!=null)?divider.getTop():0;
    }
    private int getDividerWidth() {
        return (verticalDivider!=null)?verticalDivider.getLeft():0;
    }

    private Border divider,verticalDivider;
    public void updateUI() {
        super.updateUI();

        divider = (Border) theme.getProperty("divider", Style.ALL);
        verticalDivider = (Border) theme.getProperty("verticalDivider", Style.ALL);
    }

}

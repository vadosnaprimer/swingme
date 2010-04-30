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
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public class MenuBar extends List implements ActionListener {

    public MenuBar() {
        setLayoutOrientation(true);
        setCellRenderer( new MenuItemRenderer() );
        setActionCommand("activate");
        addActionListener(this);
    }

    public boolean processKeyEvent(KeyEvent keyEvent) {
        if (!getLayoutOrientation() && getSelectedValue() instanceof Menu && keyEvent.justPressedAction(Canvas.RIGHT)) {
            fireActionPerformed();
            return true;
        }
        return super.processKeyEvent(keyEvent);
    }

    public boolean isVisible() {

        // TODO not sure if this should be here or frame
        Window w = getWindow();

        // if we are on a phone, and the window is not maximised, this bar is not visible
        if (w instanceof Frame && !((Frame)w).isMaximum() && ((Frame)w).getMenuBar() == this && getDesktopPane().SOFT_KEYS) {
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
    public void add(Component button) {
    	//#mdebug
    	if (getItems().contains(button)) {
    		throw new RuntimeException("can not add the same button twice: "+button);
    	}
    	//#enddebug
        button.setParent(this);
        addElement(button);
        Window w = getWindow();
        if (w!=null && w instanceof Frame && ((Frame)w).getMenuBar() == this && button instanceof Button) {
            autoMnemonic( getItems() );
        }
    }

    /**
     * @see Panel#removeAll()
     * @see java.awt.Container#removeAll()
     */
    public void removeAll() {
        setSelectedIndex(-1);
        getItems().removeAllElements();
        // TODO reset parents
    }

    /**
     * @see Panel#remove(net.yura.mobile.gui.components.Component)
     * @see java.awt.Container#remove(java.awt.Component) Container.remove
     */
    public void remove(Component c) {
        setSelectedIndex(-1); // TODO can be done better
        getItems().removeElement(c);
        c.removeParent(this);
    }

    /**
     * @see Panel#insert(net.yura.mobile.gui.components.Component, int)
     * @see java.awt.Container#add(java.awt.Component, int) Container.add
     */
    public void insert(Component component,int index) {
        setSelectedIndex(-1); // TODO can be done better
        getItems().insertElementAt(component, index);
    }

    public static void autoMnemonic(Vector items) {
        for (int c=0;c<items.size();c++) {
            Component button = (Component)items.elementAt(c);
            // this is same as in optionpane
            if (button instanceof Button && ((Button)button).getMnemonic() == 0) {
                switch(c) {
                    // TODO make sure this mnemonic is not used for another button
                    case 0:
                        //#debug debug
                        Logger.debug("Button 1 should already have Mnemonic "+button);
                        ((Button)button).setMnemonic(KeyEvent.KEY_SOFTKEY1);
                        break;
                    case 1:
                        //#debug debug
                        Logger.debug("Button 2 should already have Mnemonic "+button);
                        ((Button)button).setMnemonic(KeyEvent.KEY_SOFTKEY2);
                        break;
                    //case 2: ((Button)button).setMnemonic(KeyEvent.KEY_SOFTKEY3); break;
                }
            }
        }
    }

    public void actionPerformed(String actionCommand) {
        if ("activate".equals(actionCommand)) {
            int index = getSelectedIndex();
            if (index >= 0) {
                Button button = (Button)getElementAt(index);
                Component comp = getRendererComponentFor( index );
                button.setBoundsWithBorder(comp.getXWithBorder(), comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
                button.fireActionPerformed();
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
                        Component comp = getRendererComponentFor(i);
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
        Window w = getWindow();
        DesktopPane dp = w.getDesktopPane();
        int off = 0;
        // if we are on a phone, and the window is not maximised, this bar is not visible
        if (dp.SOFT_KEYS && w instanceof Frame && ((Frame)w).getMenuBar() == this ) {
            Button b1 = dp.getSelectedFrame().findMneonicButton(KeyEvent.KEY_SOFTKEY1);
            if (b1!=null) {
                off = b1.getWidthWithBorder();
            }
            Button b2 = dp.getSelectedFrame().findMneonicButton(KeyEvent.KEY_MENU);
            if (b2!=null) {
                off = b2.getWidthWithBorder();
            }

            if (dp.isSideSoftKeys()) {
                int minWidth=0;
                ListCellRenderer renderer = getCellRenderer();
                int current = getSelectedIndex();
                for(int i = 0; i < getSize(); i++){
                    Object item = getElementAt(i);
                    Component c = renderer.getListCellRendererComponent(this, item, i, i == current, false);
                    c.workoutSize();
                    minWidth = minWidth + c.getWidthWithBorder();
                }
                off = width - off - minWidth;
            }
        }
        g.translate(off, 0);
        super.paintComponent(g);
        g.translate(-off, 0);
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

}

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
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;

/**
 * @author Yura Mamyrin
 */
public class MenuBar extends List implements ActionListener {

    public MenuBar() {

        setLayoutOrientation(true);

        MenuItemRenderer renderer = new MenuItemRenderer();

        setCellRenderer(renderer);

        setActionCommand("activate");
        addActionListener(this);
    }

    public boolean isVisible() {

        // TODO not sure if this should be here or frame
        Window w = getWindow();

        // if we are on a phone, and the window is not maximised, this bar is not visible
        if (!DesktopPane.me4se && w instanceof Frame && !((Frame)w).isMaximum() ) {
            
            if (w!=null && ((Frame)w).getMenuBar() == this ) {
                return false;
            }
        }
        return super.isVisible();
    }

    public String getDefaultName() {
        return "MenuBar";
    }

    public void add(Button button) {
        Window w = getWindow();
        if (w!=null && w instanceof Frame && ((Frame)w).getMenuBar() == this ) {
            // this is same as in optionpane
            if (button.getMnemonic() == 0) {
                switch(getItems().size()) {
                    // TODO make sure this mnemonic is not used for another button
                    case 0: button.setMnemonic(KeyEvent.KEY_SOFTKEY1); break;
                    case 1: button.setMnemonic(KeyEvent.KEY_SOFTKEY2); break;
                    case 2: button.setMnemonic(KeyEvent.KEY_SOFTKEY3); break;
                }
            }
        }
        addElement(button);
    }
    /**
     * @see java.awt.Container#removeAll()
     */
    public void removeAll() {
        getItems().removeAllElements();
    }

    public void actionPerformed(String actionCommand) {
        if ("activate".equals(actionCommand)) {
            int index = getSelectedIndex();
            Button button = (Button)getElementAt(index);
            Component comp = getRendererComponentFor( index );
            button.setBoundsWithBorder(getXOnScreen()+comp.getXWithBorder(), getYOnScreen()+comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
            button.fireActionPerformed();
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
                            button.setBoundsWithBorder(getXOnScreen()+comp.getXWithBorder(), getYOnScreen()+comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
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

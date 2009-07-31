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

package net.yura.mobile.gui.cellrenderer;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 */
public class DefaultTabRenderer extends Label implements ListCellRenderer {

    private int tabPlacement;

//    private Border normal;
//    private Border selected;
//    private Border selectedAndFocus;
//    private Border selectedAndDisabled;
    private int state;

    public DefaultTabRenderer() {
        setPadding(1);
    }
    
    public DefaultTabRenderer(int a) {
        this();
        setTabPlacement(a);
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        setValue(value);

//        if (list!=null && !list.isFocusable() && isSelected) {
//            setBorder(selectedAndDisabled);
//        }
//        else {
//            setBorder( cellHasFocus?selectedAndFocus:(isSelected?selected:normal) );
//        }

        state=Style.ALL;
        if ( list!=null ) {
            if (list.isFocusable()) {
                //state |= Style.ENABLED;
            }
            else {
                state |= Style.DISABLED;
            }
        }
        if (cellHasFocus) {
            state |= Style.FOCUSED;
        }
        if (isSelected) {
            state |= Style.SELECTED;
        }

        return this;
    }

    public String getDefaultName() {
        return "TabRenderer";
    }
    
    /**
     * @param a
     * @see javax.swing.JTabbedPane#setTabPlacement(int) JTabbedPane.setTabPlacement
     */
    public void setTabPlacement(int a) {
        tabPlacement = a;
        
        String n = "TabRenderer";
        
        switch(tabPlacement) {
            case Graphics.TOP:
                setName(n+"Top");
                break;
            case Graphics.BOTTOM:
                setName(n+"Bottom");
                break;
            case Graphics.RIGHT:
                setName(n+"Right");
                break;
            case Graphics.LEFT:
                setName(n+"Left");
                break;
                    
        }

    }
    public int getCurrentState() {
            return state;
    }

//    public void updateUI() {
//            super.updateUI();
//
//            Style theme = DesktopPane.getDefaultTheme(this);
//
//            normal = theme.getBorder(Style.ALL);
//            selected = theme.getBorder(Style.SELECTED);
//            selectedAndFocus = theme.getBorder(Style.SELECTED | Style.FOCUSED);
//            selectedAndDisabled = theme.getBorder(Style.SELECTED | Style.DISABLED);
//    }
}

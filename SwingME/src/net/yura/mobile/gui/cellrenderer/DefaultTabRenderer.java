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
    
    private Border focus;
    private Border none;
    private Border open;
    private Border selectedAndDisabled;
    
    public DefaultTabRenderer() {
        setPadding(1);
    }
    
    public DefaultTabRenderer(int a) {
        this();
        setTabPlacement(a);
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        setValue(value);

        if (list!=null && !list.isSelectable() && isSelected) {
            setBorder(selectedAndDisabled);
        }
        else {
            setBorder( cellHasFocus?focus:(isSelected?open:none) );
        }
        return this;
    }
    private String name;
    public void setName(String n) {
        name = n;
        updateUI();
    }
    public String getName() {
        return name==null?"":name;
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
    public void updateUI() {
            super.updateUI();
            
            Style theme = DesktopPane.getDefaultTheme(this);
            
            focus = theme.getBorder(Style.FOCUSED);
            none = theme.getBorder(Style.ALL);
            open = theme.getBorder(Style.SELECTED);
            selectedAndDisabled = theme.getBorder(Style.SELECTED | Style.DISABLED);
            
            System.out.println(open);
    }
}

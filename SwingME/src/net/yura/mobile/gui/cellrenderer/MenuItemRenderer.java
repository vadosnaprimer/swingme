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

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Button;

/**
 * This method can render a normal commandButton
 * and also wraps any Menu it may have
 * @author Yura Mamyrin
 */
public class MenuItemRenderer extends Component implements ListCellRenderer {

    private Component component;
    
    public MenuItemRenderer() {
    }

    public Component getListCellRendererComponent(Component list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Button) {
            component = (Component)value;
            setupState(component, isSelected, cellHasFocus);
            return this;
        }
        else if (value instanceof Component) {
            return (Component)value;
        }
        return null;
    }

    public void paintComponent(Graphics2D g) {
        component.setSize( getWidth() , getHeight() );
        component.setForeground( getForeground() );
        component.paintComponent(g);
    }

    protected void workoutMinimumSize() {
        component.workoutPreferredSize();
        width = component.getWidth();
        height = component.getHeight();
    }

    public String getDefaultName() {
        return "MenuRenderer";
    }

    //#mdebug debug
    public String toString() {
        return super.toString()+"["+component+"]";
    }
    //#enddebug

}

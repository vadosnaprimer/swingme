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
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.plaf.Style;

/**
 * This method can render a normal commandButton
 * and also wraps any Menu it may have
 * @author Yura Mamyrin
 */
public class MenuItemRenderer extends Component implements ListCellRenderer {

    private Button component;
    private int state;
    
    public MenuItemRenderer() {
    }
    /**
     * if its just a normal commandButton we will draw it,
     * otehrwise we will use the Button to draw
     */
    public Component getListCellRendererComponent(Component list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value==null) return null;


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



//        this.top = top;
//        this.left = left;

            component = (Button)value;


		
			
            // This is only used when component is NULL
            //setText(value.toString());
            //setHorizontalAlignment(left?Graphics.LEFT:Graphics.RIGHT);

        //text = (top?"top":"bottom") + " " + (left?"left":"right");
        return this;
    }

    public int getState() {
        return state;
    }

    public void paintComponent(Graphics2D g) {

            // HACK
            //if (component instanceof Menu) {
            //    ((Menu)component).setArrowDirection( (top?Graphics.BOTTOM:Graphics.TOP) | (left?Graphics.RIGHT:Graphics.LEFT) );
            //}
            
            component.workoutSize();
			component.setForeground( getCurrentForeground() );
            
            int x = 0; //left?0:width-component.getWidth();
            int y = (height - component.getHeight()) / 2;

            g.translate(x, y);
            component.paintComponent(g);
            g.translate(-x, -y);

    }
    public void workoutMinimumSize() {
        
        component.workoutSize();
        width = component.getWidth();
        height = component.getHeight();
        
    }

    public String getDefaultName() {
        return "MenuItemRenderer";
    }

}

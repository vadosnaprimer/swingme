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

import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.DefaultListCellRenderer
 */
public class DefaultListCellRenderer implements ListCellRenderer {

//        private int colorNormal,colorSelected,foregroundNormal,foregroundSelected;
//	protected Border normal,selected,focusedAndSelected;
    //private int state;
        protected Component component;
	
        /**
         * @see javax.swing.DefaultListCellRenderer#DefaultListCellRenderer() DefaultListCellRenderer.DefaultListCellRenderer
         */
        public DefaultListCellRenderer() {
            component = new Label();
            component.setName("ListRenderer");
        }

        public DefaultListCellRenderer(Component c) {
            component = c;
        }

	/**
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean) DefaultListCellRenderer.getListCellRendererComponent
         */
	public Component getListCellRendererComponent(Component list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                component.setValue(value);

                int state=Style.ALL;
                if ( list!=null && !list.isFocusable()) {
                    state |= Style.DISABLED;
                }
                if (cellHasFocus) {
                    state |= Style.FOCUSED;
                }
                if (isSelected) {
                    state |= Style.SELECTED;
                }

                component.setState(state);

		return component;
	}
}

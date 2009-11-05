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

/**
 * @author Yura Mamyrin
 * @see javax.swing.DefaultListCellRenderer
 */
public class DefaultListCellRenderer extends Label implements ListCellRenderer {

        /**
         * @see javax.swing.DefaultListCellRenderer#DefaultListCellRenderer() DefaultListCellRenderer.DefaultListCellRenderer
         */
        public DefaultListCellRenderer() {
            setName("ListRenderer");
        }

	/**
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean) DefaultListCellRenderer.getListCellRendererComponent
         */
	public Component getListCellRendererComponent(Component list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setValue(value);
            setupState(list, isSelected, cellHasFocus);
            return this;
	}

        public int getMaxWidth() {
            return 1000;
        }

        /**
         * maybe should be in list, but is used in a few places
         * @see javax.swing.JList#setPrototypeCellValue(java.lang.Object) JList.setPrototypeCellValue
         */
        public static int setPrototypeCellValue(Object prototypeCellValue,ListCellRenderer renderer)  {

            Component c = renderer.getListCellRendererComponent(null, prototypeCellValue, 0, false, false);
            c.workoutSize();
            return c.getHeightWithBorder();

        }

}

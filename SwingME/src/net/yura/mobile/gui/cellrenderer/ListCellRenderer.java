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
import net.yura.mobile.gui.components.List;

/**
 * @author Yura Mamyrin
 * @see javax.swing.ListCellRenderer
 */
public interface ListCellRenderer {

    /**
     * @param list The List we're painting
     * @param value The value returned by list.getElementAt(index)
     * @param index The cells index
     * @param isSelected True if the specified cell was selected
     * @param cellHasFocus True if the specified cell has the focus
     * @return A component whose paint() method will render the specified value
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean) ListCellRenderer.getListCellRendererComponent
     */
	 Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected, boolean cellHasFocus);
	
}

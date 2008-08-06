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

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.DefaultListCellRenderer
 */
public class DefaultListCellRenderer extends Label implements ListCellRenderer {

        private int colorNormal,colorSelected,foregroundNormal,foregroundSelected;
	protected Border normal,selected,focusedAndSelected;
	
        /**
         * @see javax.swing.DefaultListCellRenderer#DefaultListCellRenderer() DefaultListCellRenderer.DefaultListCellRenderer
         */
        public DefaultListCellRenderer() {
        }

	/**
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean) DefaultListCellRenderer.getListCellRendererComponent
         */
	public Component getListCellRendererComponent(List list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

                setValue(value);
                setBorder(cellHasFocus?focusedAndSelected:(isSelected?selected:normal));
                setBackground(isSelected?colorSelected:colorNormal);
                setForeground(isSelected?foregroundSelected:foregroundNormal);
                
		return this;
	}
        // max width!
        public int getMaxTextWidth() {
            return 10000;
        }
        
        public String getName() {
            return "ListRenderer";
        }
        
        public void updateUI() {
                super.updateUI();
                Style st = DesktopPane.getDefaultTheme(this);
                normal = st.getBorder( Style.ENABLED );
                focusedAndSelected = st.getBorder( Style.FOCUSED | Style.SELECTED);
                selected = st.getBorder( Style.SELECTED );
                
                colorNormal = st.getBackground( Style.ALL );
                colorSelected = st.getBackground( Style.SELECTED );
                
                foregroundNormal = st.getForeground( Style.ALL );
                foregroundSelected = st.getForeground( Style.SELECTED );
        }
	
}

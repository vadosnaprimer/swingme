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

package net.yura.mobile.gui.celleditor;

import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Table;

/**
 * @author Yura Mamyrin
 */
public class ButtonEditor extends Button implements ListCellRenderer, TableCellEditor {

        private Object obj;
        private String label;

        public ButtonEditor(String s) {
            super(s);
            label = s;
        }

        public Component getListCellRendererComponent(Component component, Object arg1, int arg2, boolean isSelected, boolean cellHasFocus) {
            if (label==null) {
                setValue(arg1);
            }
            setupState(component,false,cellHasFocus);
            return this;
        }

        public Component getTableCellEditorComponent(Table arg0, Object arg1, boolean arg2, int arg3, int arg4) {
            if (label==null) {
                setValue(arg1);
            }

            obj = arg1;
            return this;
        }
        public Object getCellEditorValue() {
            return obj;
        }
}

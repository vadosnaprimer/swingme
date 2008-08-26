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

import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Spinner;
import net.yura.mobile.gui.components.Table;
import net.yura.mobile.gui.components.TextField;

/**
 * @author Yura Mamyrin
 * @see javax.swing.DefaultCellEditor
 */
public class DefaultCellEditor implements TableCellEditor {

    private Component component;
    
    public DefaultCellEditor(CheckBox checkBox) {
        component = checkBox;
    }
    public DefaultCellEditor(ComboBox comboBox) {
        component = comboBox;
    }
    public DefaultCellEditor(Spinner spinner) {
        component = spinner;
    }
    public DefaultCellEditor(TextField textField) {
        component = textField;
    }
    
    public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column) {
        
        if (component instanceof CheckBox) {
            ((CheckBox)component).setSelected( value instanceof Boolean?((Boolean)value).booleanValue() : false );
        }
        else if (component instanceof Spinner) {
            ((Spinner)component).setValue(value);
        }
        else if (component instanceof ComboBox) {
             ((ComboBox)component).setSelectedItem(value);
        }
        else if (component instanceof TextField) {
            ((TextField)component).setText(String.valueOf(value));
        }
        
        return component;
    }
    
}

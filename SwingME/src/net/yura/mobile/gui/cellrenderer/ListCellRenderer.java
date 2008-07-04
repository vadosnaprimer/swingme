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

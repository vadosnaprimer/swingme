package net.yura.mobile.gui.cellrenderer;

import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.List;

public interface ListCellRenderer {

	 Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected, boolean cellHasFocus);
	
}

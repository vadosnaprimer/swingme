package net.yura.mobile.gui.layout;

import java.util.Hashtable;

import net.yura.mobile.gui.components.Panel;

/**
 * @author Yura Mamyrin
 * @see java.awt.LayoutManager
 */

public interface Layout {

	/**
	* @see java.awt.LayoutManager#layoutContainer(java.awt.Container) LayoutManager.layoutContainer
	*/
	void layoutPanel(Panel panel, Hashtable cons);
	
}

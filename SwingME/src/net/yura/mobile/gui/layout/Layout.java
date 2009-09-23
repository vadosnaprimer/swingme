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

package net.yura.mobile.gui.layout;

import net.yura.mobile.gui.components.Panel;

/**
 * @author Yura Mamyrin
 * @see java.awt.LayoutManager
 */
public interface Layout {

	/**
	* @see java.awt.LayoutManager#layoutContainer(java.awt.Container) LayoutManager.layoutContainer
	*/
	void layoutPanel(Panel panel);

        /**
         * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container) LayoutManager.preferredLayoutSize
         */
        int getPreferredHeight(Panel panel);

        /**
         * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container) LayoutManager.preferredLayoutSize
         */    
        int getPreferredWidth(Panel panel);
	
}

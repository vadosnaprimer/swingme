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

package net.yura.mobile.gui;

import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;

public class Theme {
	
	public int scrollBarCol=0x00FFFFFF;
	public int scrollTrackCol=0x00000000;
	
	// Font object
	public Font font;

	// Items info
	public int background;
	public int foreground;
        public int activeForeground;
        public int disabledForeground;
	
	public Border normalBorder;
	public Border activeBorder;
	
	public int defaultWidthOffset;
        public int defaultSpace;
        
        public ListCellRenderer softkeyRenderer;
        public Border menuBorder;

        
	public Theme() {
		
		background = 0x00FFFFFF;
		foreground = 0x00000000;
                activeForeground = 0x000000FF;
                disabledForeground = 0x00808080;
		
		normalBorder = new LineBorder(0x00808080);
		activeBorder = new LineBorder(0x00000000);
	}

}


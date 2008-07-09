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

package net.yura.mobile.util;

import java.util.Vector;

import net.yura.mobile.gui.components.Button;

/**
 * @author Yura Mamyrin
 * @see javax.swing.ButtonGroup
 */
public class ButtonGroup {

	 private Vector buttons = new Vector();
	 private Button selected;
	 
         /**
          * @param b the button to be added
          * @see javax.swing.ButtonGroup#add(javax.swing.AbstractButton) ButtonGroup.add
          */
	 public void add(Button b) {
		 buttons.addElement(b);
		 b.setButtonGroup(this);
		 
	 }

         /**
          * @see javax.swing.ButtonGroup#getButtonCount() ButtonGroup.getButtonCount
          */
	 public int getButtonCount() {
		 
		 return buttons.size();
	 }
	 
         /**
          * @see javax.swing.ButtonGroup#getSelection() ButtonGroup.getSelection
          */
	 public Button getSelection() {
		 
		 return selected;
	 }

         /**
          * @see javax.swing.ButtonGroup#setSelected(javax.swing.ButtonModel, boolean) ButtonGroup.setSelected
          */
	public void setSelected(Button button) {
		
		selected = button;
		
		for (int c=0;c<buttons.size();c++) {
			
			Button b = (Button)buttons.elementAt(c);
			
			if (b!=button && b.isSelected()) {
				
				b.setSelected(false);
			}
		}
		
	}
	
}

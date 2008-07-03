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

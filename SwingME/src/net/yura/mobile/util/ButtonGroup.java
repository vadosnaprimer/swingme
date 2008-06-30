package net.yura.mobile.util;

import java.util.Vector;

import net.yura.mobile.gui.components.Button;

public class ButtonGroup {

	 private Vector buttons = new Vector();
	 private Button selected;
	 
	 public void add(Button b) {
		 buttons.addElement(b);
		 b.setButtonGroup(this);
		 
	 }

	 public int getButtonCount() {
		 
		 return buttons.size();
	 }
	 
	 public Button getSelection() {
		 
		 return selected;
	 }

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

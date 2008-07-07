package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;

import javax.microedition.lcdui.Image;

public class CheckBox extends RadioButton {
	
        private boolean oldState;
    
        public CheckBox(String label,Image a,Image b) {
            super(label,a,b);
        }
    
	public CheckBox(String label){
            super(label);
	}
	
        public void fireActionPerformed() {
                
                if (oldState) selected=false;
                
                super.fireActionPerformed();

                oldState = selected;
                
	}
	

	public void paintExtra(Graphics g) {

            int size = getFont().getHeight();
            
            int x=padding;
            int y = (height-size)/2;

            g.drawRect(x, y, size-1, size-1);

            if (isSelected()) {

                //g.fillRect(x+3, y+3, size-6, size-6);
                
                for (int pad=3;pad<6;pad++) {
                    g.drawLine(x+pad, y+size/2, x+size/3, y+size-pad);
                    g.drawLine(x+size/3, y+size-pad,x+size-pad,y+pad);
                }

            }
	}

	
}
package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.KeyEvent;

public class RadioButton extends Button {

	protected Image selectedImage;
	protected Image nonSelectedImage;
	
        public RadioButton(String label,Image a,Image b) {
            this(label);
            selectedImage=a;
            nonSelectedImage=b;
            
        }
            
	public RadioButton(String label) {
		super(label);
		
		setBorder(null);
		background = -1;
		transparent = true;
		
	}
	
	protected int getCombinedWidth() {
            
            int w = super.getCombinedWidth();
            if (w!=0) { w=w+gap; }
            
            if (nonSelectedImage!=null) {
                w = w + nonSelectedImage.getWidth();
            }
            else {
                w = w + getFont().getHeight();
            }
	    return w;
	}
        
        protected int getCombinedHeight() {
              
             int h = super.getCombinedHeight();
             if (nonSelectedImage!=null && nonSelectedImage.getHeight() > h) {
                 h = nonSelectedImage.getHeight();
             }
             return h;
        }

        public void paintComponent(Graphics g){
        
            
                if (isFocused()){
			g.setColor(activeBorderColor);
		}
		else{
			g.setColor(borderColor);
		}

		if (isSelected()){
			if (selectedImage == null ){

                                paintExtra(g);
				
			}
			else{
				g.drawImage(selectedImage, padding, (height-selectedImage.getHeight())/2 , 0 );
			}
		}
		else{
			if (nonSelectedImage == null){

                                paintExtra(g);

			}
			else{
				g.drawImage(nonSelectedImage, padding, (height-nonSelectedImage.getHeight())/2 , 0 );
			}
		}
		
                int offset = gap;
            
                if (nonSelectedImage!=null) {
                    offset = offset + nonSelectedImage.getWidth();
                }
                else {
                    offset = offset + getFont().getHeight();
                }
                
		g.translate(+ offset, 0);
		super.paintComponent(g);
		g.translate(- offset, 0);

        }
        
	public void paintExtra(Graphics g){

                int size = getFont().getHeight()-1;
                // the -1 is ONLY there so it looks better on the emulator
                
                int x = padding;
                int y = (height-size)/2;
                
                g.drawArc(x, y, size-1, size-1, 0, 360);

		if (isSelected()){

                    int size2 = size/4;      
	            g.fillArc(x+size2, y+size2, size-(size2*2), size-(size2*2), 0, 360);
                }
	}

	
	public boolean keyEvent(KeyEvent keyEvent){
		boolean consumed = handelKeyEvent(keyEvent);
		if (consumed) {
                    repaint();
                }
		return consumed;
	}
	
	public Image getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(Image selectedImage) {
		this.selectedImage = selectedImage;
	}

	public Image getNonSelectedImage() {
		return nonSelectedImage;
	}

	public void setNonSelectedImage(Image nonSelectedImage) {
		this.nonSelectedImage = nonSelectedImage;
	}
	
}
package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.KeyEvent;

public class CheckBox extends Button{

	private Image selectedImage;
	private Image nonSelectedImage;
	
	public CheckBox(String label){
		super(label);
		
		setBorder(null);
		background = -1;
		transparent = true;
	}

	public void workoutSize(){
		width = (getFont().getWidth('E') + 1) * 2 + (getFont().getWidth( getText() ) + 2);
		height = getFont().getHeight();
	}
	
	public boolean keyEvent(KeyEvent keypad){
		boolean sel = selected;
		boolean response = super.keyEvent(keypad);
		if (response && sel)
		{
			selected = false;
		}
		repaint();
		return response;
	}
	
	/**
	 * Draws the button at given y position with set alignment
	 * @param Graphics - the graphics object
	 * @param int - Y position
	 * @return int - height of the item
	 */
	public void paintComponent(Graphics g){
		int radius = getFont().getWidth('E') + 1;
		//Calculate boxX
		int x = 2;
		int y = 2;

		// is radio button selected
		if (isSelected() == true){
			if (getSelectedImage() == null ){
				setValidatedColor(g);
				g.drawRect(x-1, y-1, radius+1, radius+1);
				g.fillRect(x+1, y+1, radius-2, radius-2);
			}
			else{
				g.setColor(borderColor);
				g.drawImage(selectedImage, (height-selectedImage.getHeight())/2, (height-selectedImage.getHeight())/2 , 0 );
			}
		}
		else{
			if (getNonSelectedImage() == null){
				setValidatedColor(g);
				g.drawRect(x-1, y-1, radius+1, radius+1);
			}
			else{
				g.setColor(borderColor);
				g.drawImage(nonSelectedImage, (height-nonSelectedImage.getHeight())/2, (height-nonSelectedImage.getHeight())/2 , 0 );
			}
		}
		
		g.translate(+ radius * 2, 0);
		super.paintComponent(g);
		g.translate(- radius * 2, 0);
	}
	
	private void setValidatedColor(Graphics g){
		if (isFocused()){
			g.setColor(activeBorderColor);
		}
		else{
			g.setColor(borderColor);
		}
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
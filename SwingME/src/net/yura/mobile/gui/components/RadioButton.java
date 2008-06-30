package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.KeyEvent;

public class RadioButton extends Button {

	private Image selectedImage;
	private Image nonSelectedImage;
	
	public RadioButton(String label) {
		super(label);
		
		setBorder(null);
		background = -1;
		transparent = true;
		
	}
	
	public void workoutSize(){
		width = (getFont().getWidth('E') + 1) * 2 + (getFont().getWidth( getText() ) + 2);
		height = getFont().getHeight() + 4;
	}
	/**
	 * TODO!!! 
	 * There is an issue with the component/panel that this radio button is drawn on.
	 * For some reason the g.drawArc does not work, so it has been necessary to use 
	 * g.fillArc instead.
	 * 
	 * The solution for drawing a radio button without using draw arc, involves 
	 * drawing 3 filled arcs with the largest one being drawn first, followed by the
	 * second largest, and then the smallest. The most recent drawn arc will sit on top 
	 * of a previously drawn arc.
	 * TODO Look at solving why the drawArc doesn't work anymore. 
	 */
	/**
	 * Draws the button at given y position with set alignment
	 * @param Graphics - the graphics object
	 * @param int - Y position
	 * @return int - height of the item
	 */
	public void paintComponent(Graphics g){
		
		int radius = getFont().getWidth('E') + 1;

		// is radio button selected
		if (isSelected()){
			if (getSelectedImage() == null ){
				setValidatedColor(g);
				g.drawArc(0, 0, radius+2, radius+2, 0, 360);
				//g.setColor(0);
				//g.fillArc(1, 1, radius, radius, 0, 360);
				setValidatedColor(g);
				g.fillArc(2, 2, radius-1, radius-1, 0, 360);
//				g.drawArc(2, 2, radius+1, radius+1, 0, 360);
				
			}
			else{
				g.setColor(borderColor);
				g.drawImage(selectedImage, (height-selectedImage.getHeight())/2, (height-selectedImage.getHeight())/2 , 0 );
			}
		}
		else{
			if (getNonSelectedImage() == null){
				setValidatedColor(g);
				g.drawArc(0, 0, radius+2, radius+2, 0, 360);
				//g.setColor(0);
				//g.fillArc(1, 1, radius, radius, 0, 360);

//				g.drawArc(2, 2, radius+1, radius+1, 0, 360);
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
	
	public boolean keyEvent(KeyEvent keypad){
		boolean bob = super.keyEvent(keypad);
		repaint();
		return bob;
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
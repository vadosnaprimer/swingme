package net.yura.mobile.gui.components;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.util.Option;

public class Spinner extends Label {

	private Image selectedImage;
	private Image nonSelectedImage;
	private int textWidth; // TODO WRONG! should not be kept
	private int index = 0;
	
	private Vector list;

	private int borderColor;
	private int activeBorderColor;
	private boolean continuous = false;
	private boolean leftPress = false;
	private boolean rightPress = false;

	public Spinner() {
		this(new Vector(), false);
	}
	
	public Spinner(Vector vec, boolean cont) {
		super((String)null);
		continuous = cont;
		setData(vec);
		
		setForegroundByFontColorIndex(0);
		
		borderColor = RootPane.getDefaultStyle().itemBorderColor;
		activeBorderColor = RootPane.getDefaultStyle().itemActiveBorderColor;
		
		selectable = true;
	}
	
	public void workoutSize() {
		if (list!=null && !list.isEmpty()) {

                                int count = 0;
                            
				for (int i = 0; i < list.size(); i ++){
					
                                    Object obj = list.elementAt(i);

                                    int len = getCombinedWidth(String.valueOf(obj),(obj instanceof Option)?((Option)obj).getIcon():null);
      
				    if (count < len){
                                        count = len;
				    }
					
				}
				textWidth = count + 2;
				
				int w = textWidth + (getFont().getWidth('E') + 1) * 2 + 5;
				int h = getFont().getHeight() + 1;
				
				if (w > width) width = w;
				if (h > height) height = h;
			}

	}
	
	public boolean keyEvent(KeyEvent keypad){
		if (keypad.justPressedAction(Canvas.LEFT)){
			leftPress = true;
		}
		else if (keypad.justReleasedAction(Canvas.LEFT)){
			leftPress = false;
		}
		else if (keypad.justPressedAction(Canvas.RIGHT)){
			rightPress = true;
		}
		else if (keypad.justReleasedAction(Canvas.RIGHT)){
			rightPress = false;
		}
		
		if (keypad.justPressedAction(Canvas.LEFT) && index == 0) {
			if (continuous == true){
				if (index == 0){
					index = list.size()-1;
					setIndex(index);
				}
			}
		}
		else if (keypad.justPressedAction(Canvas.LEFT)){
			setIndex(index-1);
		}
		else if (keypad.justPressedAction(Canvas.RIGHT) && index == list.size()-1) {
			if (continuous == true){
				if (index == list.size() - 1){
					index = 0;
					setIndex(index);
				}
			}
		} 
		else if (keypad.justPressedAction(Canvas.RIGHT)){
			setIndex(index+1);
		}
		
		repaint();
		return leftPress || rightPress || keypad.justReleasedAction(Canvas.LEFT) || keypad.justReleasedAction(Canvas.RIGHT);
	}
	
	/**
	 * Draws the button at given y position with set alignment
	 * @param Graphics - the graphics object
	 * @param int - Y position
	 * @return int - height of the item
	 */
	public void paintComponent(Graphics g){
		
		int boxX = getFont().getWidth('E') + 1;
		int x = 0;
		int y = 0;

		if (getNonSelectedImage() == null){
			// check focused, and set colour accordingly
			if (isFocused()){
				g.setColor(activeBorderColor);
			}
			else{
				g.setColor(borderColor);
			}
			g.drawRect(boxX, y, textWidth + 4, height-1);
			
			// check if left button pressed, then set colour accordingly
			if (leftPress){
				g.setColor(activeBorderColor);
			}
			else{
				g.setColor(borderColor);
			}
			ScrollPane.drawLeftArrow(g, x, y+1, boxX-1, height-2);
			
			// check if right button pressed, then set colour accordingly			
			if (rightPress){
				g.setColor(activeBorderColor);
			}
			else{
				g.setColor(borderColor);
			}
			ScrollPane.drawRightArrow(g, boxX + textWidth + 6, y+1, boxX-1, height-2);
		}
		else{
			g.setColor(borderColor);
			g.drawImage(nonSelectedImage, (height-nonSelectedImage.getHeight())/2, (height-nonSelectedImage.getHeight())/2 , 0 );
			ScrollPane.drawLeftArrow(g, x, y+1, boxX-1, height-4);
			g.drawRect(boxX, y, textWidth + 4, height-1);
			ScrollPane.drawRightArrow(g, boxX + textWidth + 6, y+1, boxX-1, height-4);
		}
		g.translate(x + boxX + 1, 0);
		super.paintComponent(g);
		g.translate(-x - boxX - 1, 0);
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

	public void reset() {
		setIndex(0);
	}
	
	public void setData(Vector data) {
		this.list = data;
		if (data == null || data.size() == 0) {
			return;
		}
		workoutSize();
		setIndex(0);
	}
	
	public void setIndex(int i) {
            
            index = i;
            
            if (list!=null && !list.isEmpty()) {
                setValue( list.elementAt(index) );
            }

	    repaint();
	}
        public int getMaxTextWidth() {
            return textWidth; // todo: should be width -leftArrow -rightArrow
        }

	public void focusLost() {
		setForegroundByFontColorIndex(0);
		repaint();
	}

	public void focusGained() {
		setForegroundByFontColorIndex(1);
		repaint();
	}

	public int getActiveBorderColor() {
		return activeBorderColor;
	}

	public void setActiveBorderColor(int activeBorderColor) {
		this.activeBorderColor = activeBorderColor;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}
		
	/**
	 * This throws an IllegalArgumentException and you should use the 
	 * setIndex method instead
	 */
	public void setText(String a) {
		throw new IllegalArgumentException();
	}
}

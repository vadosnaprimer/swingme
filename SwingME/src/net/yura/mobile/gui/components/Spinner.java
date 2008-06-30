package net.yura.mobile.gui.components;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.util.Option;

public class Spinner extends Label {

        private static final int lineTickness = 1;
    
	private Image selectedImage;
	private Image nonSelectedImage;
        
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

                setHorizontalAlignment(Graphics.HCENTER);
	}
	
	public void doLayout() {
            
            if (list!=null && !list.isEmpty()) {

                    int maxWidth = 0;
                    int maxHeight=0;
                    
                    for (int i = 0; i < list.size(); i ++){

                        Object obj = list.elementAt(i);

                        int len = getCombinedWidth(String.valueOf(obj),(obj instanceof Option)?((Option)obj).getIcon():null);

                        if (maxWidth < len){
                            maxWidth = len;
                        }
                        
                        int hi = getCombinedHeight( (obj instanceof Option)?((Option)obj).getIcon():null );
                                
                        if (maxHeight < hi){
                            maxHeight = hi;
                        }
                                

                    }

                    int w = maxWidth + getArrowWidth()*2 + gap*2 + lineTickness*2 + padding*2;
                    int h = maxHeight + lineTickness*2 + padding*2;

                    if (w > width) width = w;
                    if (h > height) height = h;

                    if (width > RootPane.getDefaultStyle().defaultWidth) {
                        width = RootPane.getDefaultStyle().defaultWidth;
                    }
                    
                    setIndex(index);
                }

	}
        
        private int getArrowWidth() {
            
            if (nonSelectedImage!=null) {
                return nonSelectedImage.getWidth();
            }
            else {
                return getFont().getHeight()/2;
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
		
		if (leftPress && index == 0) {
			if (continuous == true){
				setIndex(list.size()-1);

			}
		}
		else if (leftPress) {
			setIndex(index-1);
		}
		else if (rightPress && index == list.size()-1) {
			if (continuous == true){
				setIndex(0);
			}
		} 
		else if (rightPress){
			setIndex(index+1);
		}
		
                boolean letgo = keypad.justReleasedAction(Canvas.LEFT) || keypad.justReleasedAction(Canvas.RIGHT);
                
		if (letgo) repaint();
		return leftPress || rightPress || letgo;
	}

	public void paintComponent(Graphics g){

            int arrowWidth = getArrowWidth();
            
            if (isFocused()){
                    g.setColor(activeBorderColor);
            }
            else{
                    g.setColor(borderColor);
            }
            g.drawRect(arrowWidth+gap, 0, width -gap*2 - arrowWidth*2 -lineTickness, height-lineTickness);

            if (nonSelectedImage != null) {
                
                    // TODO: Finish
                    g.drawImage(nonSelectedImage, 0, (height-nonSelectedImage.getHeight())/2 , 0 );
                    g.drawImage(selectedImage, width-arrowWidth, (height-nonSelectedImage.getHeight())/2 , 0 );
            }
            else {

                    int arrowHeight = getFont().getHeight();
                
                    if (leftPress){
                            g.setColor(activeBorderColor);
                    }
                    else{
                            g.setColor(borderColor);
                    }
                    ScrollPane.drawLeftArrow(g, 0, (height-arrowHeight)/2, arrowWidth, arrowHeight);

                    if (rightPress){
                            g.setColor(activeBorderColor);
                    }
                    else{
                            g.setColor(borderColor);
                    }
                    ScrollPane.drawRightArrow(g, width-arrowWidth, (height-arrowHeight)/2, arrowWidth, arrowHeight);
            }

            int xoffset = 0;

            if (horizontalAlignment==Graphics.LEFT) {
                xoffset = arrowWidth+gap+lineTickness;
            }
            else if (horizontalAlignment==Graphics.RIGHT) {
                xoffset = -arrowWidth-gap-lineTickness;
            }
            
            int yoffset=0;
            if (verticalAlignment==Graphics.TOP) {
                yoffset = lineTickness;
            }
            else if (verticalAlignment==Graphics.BOTTOM) {
                yoffset = -lineTickness;
            }
            
            g.translate(xoffset, yoffset);
            super.paintComponent(g);
            g.translate(-xoffset, -yoffset);
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

	
	public void setData(Vector data) {
		this.list = data;
		index=0;
	}
	
	public void setIndex(int i) {
            
            index = i;
            
            if (list!=null && !list.isEmpty()) {
                setValue( list.elementAt(index) );
            }

	    repaint();
	}

        public int getMaxTextWidth() {
            return width - getArrowWidth()*2 - gap*2 - lineTickness*2 - padding*2;
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

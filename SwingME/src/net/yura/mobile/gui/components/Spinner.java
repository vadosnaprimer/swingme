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

package net.yura.mobile.gui.components;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Style;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.util.Option;

public class Spinner extends Label {
    
        // TODO:  may need more images
	private Image selectedImage;
	private Image nonSelectedImage;
        
	private int index = 0;
	
	private Vector list;

	private Border normalBorder;
	private Border activeBorder;
        
	private boolean continuous = false;
        
	private boolean leftPress = false;
	private boolean rightPress = false;

        private int normalForeground;
        private int activeForeground;
        private int disabledForeground;
        
	public Spinner() {
		this(new Vector(), false);
	}
	
	public Spinner(Vector vec, boolean cont) {
		super((String)null);
		continuous = cont;
		setData(vec);
		
		normalBorder = DesktopPane.getDefaultTheme(this).getBorder(Style.ALL);
		activeBorder = DesktopPane.getDefaultTheme(this).getBorder(Style.FOCUSED);
		
                activeForeground = DesktopPane.getDefaultTheme(this).getForeground(Style.FOCUSED);
                disabledForeground = DesktopPane.getDefaultTheme(this).getForeground(Style.DISABLED);
                normalForeground = DesktopPane.getDefaultTheme(this).getForeground(Style.ALL);
                
		selectable = true;

                setHorizontalAlignment(Graphics.HCENTER);

	}
	
        public void setSelectable(boolean s) {
		if (s) {
                    foreground = normalForeground;
                }
                else {
                    foreground = disabledForeground;
                }
                super.setSelectable(s);
	}
        
	public void workoutSize() {
            
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

                    int w = maxWidth + getArrowWidth()*2 + gap*2 + normalBorder.getLeft() + normalBorder.getRight() + padding*2;
                    int h = maxHeight + normalBorder.getTop() + normalBorder.getBottom() + padding*2;

                    if (w > width) width = w;
                    if (h > height) height = h;

                    // dont allow the spinner to get too big
                    if (width > DesktopPane.getDesktopPane().getWidth() - DesktopPane.getDesktopPane().defaultWidthOffset) {
                        width = DesktopPane.getDesktopPane().getWidth() - DesktopPane.getDesktopPane().defaultWidthOffset;
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
            
            Border b = isFocused()?activeBorder:normalBorder;

            g.translate(arrowWidth+gap+normalBorder.getLeft(), normalBorder.getTop());
            b.paintBorder(this, g, width -gap*2 - arrowWidth*2 -normalBorder.getLeft() - normalBorder.getRight(), height-normalBorder.getTop() - normalBorder.getBottom());
            g.translate(-arrowWidth-gap-normalBorder.getLeft(), -normalBorder.getTop());


            if (nonSelectedImage != null) {
                
                    // TODO: Finish
                    g.drawImage(nonSelectedImage, 0, (height-nonSelectedImage.getHeight())/2 , 0 );
                    g.drawImage(selectedImage, width-arrowWidth, (height-nonSelectedImage.getHeight())/2 , 0 );
            }
            else {

                    int arrowHeight = getFont().getHeight();
                
                    if (leftPress){
                            g.setColor(0);
                    }
                    else{
                            g.setColor(0x00808080);
                    }
                    ScrollPane.drawLeftArrow(g, 0, (height-arrowHeight)/2, arrowWidth, arrowHeight);

                    if (rightPress){
                            g.setColor(0);
                    }
                    else{
                            g.setColor(0x00808080);
                    }
                    ScrollPane.drawRightArrow(g, width-arrowWidth, (height-arrowHeight)/2, arrowWidth, arrowHeight);
            }

            int xoffset = 0;

            if ((alignment&Graphics.LEFT)!=0) {
                xoffset = arrowWidth+gap+normalBorder.getLeft();
            }
            else if ((alignment&Graphics.RIGHT)!=0) {
                xoffset = -arrowWidth-gap-normalBorder.getRight();
            }
            
            int yoffset=0;
            if ((alignment&Graphics.TOP)!=0) {
                yoffset = normalBorder.getTop();
            }
            else if ((alignment&Graphics.BOTTOM)!=0) {
                yoffset = -normalBorder.getBottom();
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
            return width - getArrowWidth()*2 - gap*2 - normalBorder.getLeft() - normalBorder.getRight() - padding*2;
        }

	public void focusLost() {
                super.focusLost();
		foreground = normalForeground;
		repaint();
	}

	public void focusGained() {
                super.focusGained();
		foreground = activeForeground;
		repaint();
	}

	public Border getActiveBorder() {
		return activeBorder;
	}

	public void setActiveBorder(Border activeBorderColor) {
		this.activeBorder = activeBorderColor;
	}

	public Border getNormalBorder() {
		return normalBorder;
	}

	public void setNormalBorderColor(Border borderColor) {
		this.normalBorder = borderColor;
	}
		
	/**
	 * This throws an IllegalArgumentException and you should use the 
	 * setIndex method instead
	 */
	public void setText(String a) {
		throw new IllegalArgumentException();
	}
}

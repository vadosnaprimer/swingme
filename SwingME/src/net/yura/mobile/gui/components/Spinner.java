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
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.util.Option;

public class Spinner extends Label {
    
	private Icon leftSelectedIcon;
	private Icon leftUnselectedIcon;
	private Icon rightSelectedIcon;
	private Icon rightUnselectedIcon;
        
	private int index = 0;
	
	private Vector list;

//	private Border normalBorder;
//	private Border activeBorder;
        
	private boolean continuous = false;
        
	private boolean leftPress = false;
	private boolean rightPress = false;

//        private int normalForeground;
//        private int activeForeground;
//        private int disabledForeground;

        private ChangeListener chl;
        
	public Spinner() {
		this(new Vector(), false);
	}
	
	public Spinner(Vector vec, boolean cont) {
		super((String)null);
		continuous = cont;
		setData(vec);

		focusable = true;

                setHorizontalAlignment(Graphics.HCENTER);

	}

        public void addChangeListener(ChangeListener aThis) {
            chl = aThis;
        }

        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
            super.processMouseEvent(type, x, y, keys);

            if (!focusable) return;

            if (type == DesktopPane.PRESSED) {
                int offset=0;
//                if (nonSelectedImage != null) {
//                    // TODO
//                }
//                else {
                        offset = (getWidthWithBorder() - getWidth())/2;
//                }
                if (x <0 && x > -offset) {
                    leftPress = true;
                }
                else if ( x> width && x< width + offset) {
                    rightPress = true;
                }
                fire();
            }
            else if (type == DesktopPane.RELEASED) {
                if (leftPress || rightPress) {
                    leftPress = false;
                    rightPress = false;
                    repaint();
                }
            }
        }

//        public void setFocusable(boolean s) {
//		if (s) {
//                    foreground = normalForeground;
//                }
//                else {
//                    foreground = disabledForeground;
//                }
//                super.setFocusable(s);
//	}
        
	public void workoutMinimumSize() {
            
            if (list!=null && !list.isEmpty()) {

                    int maxWidth = 0;
                    int maxHeight=0;
                    
                    for (int i = 0; i < list.size(); i ++){

                        Object obj = list.elementAt(i);

                        Icon img = (obj instanceof Option)?((Option)obj).getIcon():null;
                        
                        int len = getCombinedWidth(String.valueOf(obj),img!=null?img.getIconWidth():0);

                        if (maxWidth < len){
                            maxWidth = len;
                        }
                        
                        int hi = getCombinedHeight( img!=null?img.getIconHeight():0 );
                                
                        if (maxHeight < hi){
                            maxHeight = hi;
                        }
                                

                    }

                    // ((normalBorder==null)?getArrowWidth()*2:0)
                    int w = maxWidth + gap*2 + padding*2; // normalBorder.getLeft() + normalBorder.getRight()
                    int h = maxHeight + padding*2; // normalBorder.getTop() + normalBorder.getBottom()

                    if (w > width) width = w;
                    if (h > height) height = h;

                    // dont allow the spinner to get too big
                    if (width > DesktopPane.getDesktopPane().getWidth()) {
                        width = DesktopPane.getDesktopPane().getWidth();
                    }
                    
                    setIndex(index);
                }

	}
        
//        private int getArrowWidth() {
//
//            if (nonSelectedImage!=null) {
//                return nonSelectedImage.getWidth();
//            }
//            else {
//                return getFont().getHeight()/2;
//            }
//        }
        
	
	public boolean processKeyEvent(KeyEvent keypad){
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
		
                fire();
		
                boolean letgo = keypad.justReleasedAction(Canvas.LEFT) || keypad.justReleasedAction(Canvas.RIGHT);
                
		if (letgo) repaint();
		return leftPress || rightPress || letgo;
	}

        private void fire() {
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
        }

	public void paintComponent(Graphics2D g){
            super.paintComponent(g);

            Border insets = getInsets();

            if (leftPress && (leftSelectedIcon!=null)) {
                leftSelectedIcon.paintIcon(this, g, -insets.getLeft(), (height-leftSelectedIcon.getIconHeight())/2);
            }
            else if (leftUnselectedIcon != null) {
                leftUnselectedIcon.paintIcon(this, g, -insets.getLeft(), (height-leftUnselectedIcon.getIconHeight())/2);
            }

            if (rightPress && (rightSelectedIcon!=null)) {
                rightSelectedIcon.paintIcon(this, g, width+insets.getRight()-rightUnselectedIcon.getIconWidth(), (height-rightUnselectedIcon.getIconHeight())/2);
            }
            else if (rightUnselectedIcon != null) {
                rightUnselectedIcon.paintIcon(this, g, width+insets.getRight()-rightUnselectedIcon.getIconWidth(), (height-rightUnselectedIcon.getIconHeight())/2);
            }
            
            
            //int arrowWidth = getArrowWidth();
            
            //Border b = isFocused()?activeBorder:normalBorder;

            //g.translate(arrowWidth+gap+normalBorder.getLeft(), normalBorder.getTop());
            //b.paintBorder(this, g, width -gap*2 - arrowWidth*2 -normalBorder.getLeft() - normalBorder.getRight(), height-normalBorder.getTop() - normalBorder.getBottom());
            //g.translate(-arrowWidth-gap-normalBorder.getLeft(), -normalBorder.getTop());

//
//            if (nonSelectedImage != null) {
//
//                    // TODO: Finish
//                    //g.drawImage(nonSelectedImage, 0, (height-nonSelectedImage.getHeight())/2 , 0 );
//                    //g.drawImage(selectedImage, width-arrowWidth, (height-nonSelectedImage.getHeight())/2 , 0 );
//            }
//            else {
//
//                    int arrowHeight = getFont().getHeight();
//                    int arrowWidth = arrowHeight/2;
//                    int offset = (getWidthWithBorder() - getWidth())/2;
//
//                    if (leftPress){
//                            g.setColor(0);
//                    }
//                    else{
//                            g.setColor(0x00808080);
//                    }
//                    ScrollPane.drawLeftArrow(g, -arrowWidth -(offset-arrowWidth)/2, (height-arrowHeight)/2, arrowWidth, arrowHeight);
//
//                    if (rightPress){
//                            g.setColor(0);
//                    }
//                    else{
//                            g.setColor(0x00808080);
//                    }
//                    ScrollPane.drawRightArrow(g, width + (offset-arrowWidth)/2, (height-arrowHeight)/2, arrowWidth, arrowHeight);
//            }

	}

	
	public void setData(Vector data) {
		this.list = data;
		index=0;
	}
	
        /**
         * @return the current value
         * @see javax.swing.JSpinner#getValue() JSpinner.getValue
         */
        public Object getValue() {
            return list.elementAt(index);
        }
        
	public void setIndex(int i) {

            int old = index;

            index = i;
            
            if (list!=null && !list.isEmpty()) {
                super.setValue( list.elementAt(index) );
            }

            if (chl!=null && old!=index) {
                chl.changeEvent(this,index);
            }

	    repaint();
	}

        /**
         * @param value
         * @see javax.swing.JSpinner#setValue(java.lang.Object) JSpinner.setValue
         */
        public void setValue(Object value) {
            setIndex( list.indexOf(value) );
        }
        
	public void focusLost() {
                super.focusLost();
//		foreground = normalForeground;
//                super.setBorder(normalBorder);
		repaint();
	}

	public void focusGained() {
                super.focusGained();
//		foreground = activeForeground;
//                super.setBorder(activeBorder);
		repaint();
	}

//	public Border getActiveBorder() {
//		return activeBorder;
//	}
//
//	public void setActiveBorder(Border activeBorderColor) {
//		this.activeBorder = activeBorderColor;
//	}
//
//	public Border getBorder() {
//		return normalBorder;
//	}
//
//	public void setBorder(Border borderColor) {
//		this.normalBorder = borderColor;
//                super.setBorder(borderColor);
//	}
	//#mdebug
	/**
	 * This throws an IllegalArgumentException and you should use the 
	 * setIndex method instead
	 */
	public void setText(String a) {
		throw new IllegalArgumentException();
	}
        //#enddebug
        public String getDefaultName() {
            return "Spinner";
        }
        public void updateUI() {
                super.updateUI();
                //Style theme = DesktopPane.getDefaultTheme(this);
//            	normalBorder = theme.getBorder(Style.ALL);
//		activeBorder = theme.getBorder(Style.FOCUSED);
//
//                activeForeground = theme.getForeground(Style.FOCUSED);
//                disabledForeground = theme.getForeground(Style.DISABLED);
//                normalForeground = theme.getForeground(Style.ALL);

                leftSelectedIcon = (Icon) theme.getProperty("iconLeft", Style.SELECTED);
                leftUnselectedIcon = (Icon) theme.getProperty("iconLeft", Style.ALL);
                rightSelectedIcon = (Icon) theme.getProperty("iconRight", Style.SELECTED);
                rightUnselectedIcon = (Icon) theme.getProperty("iconRight", Style.ALL);
                
        }
}

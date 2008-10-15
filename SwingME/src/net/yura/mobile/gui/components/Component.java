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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JComponent
 */
public abstract class Component {
	
	protected int posX,posY,width,height;
	protected boolean focusable;

	protected Panel parent;

	protected int background;
	protected int foreground;
	protected Border border;
        
        private String tooltip;
        
        /**
         * @see javax.swing.JComponent#JComponent() JComponent.JComponent
         */
	public Component() {
		focusable = true;
		updateUI();
	}
	
        /**
         * @see java.awt.Component#isFocusOwner() Component.isFocusOwner
         */
	public boolean isFocusOwner() {
		return DesktopPane.getDesktopPane().getSelectedFrame().getFocusOwner() == this;
	}

        /**
         * @see java.awt.Component#requestFocusInWindow()
         */
        public void requestFocusInWindow() {
            Window w = getWindow();
            if (w!=null) {
                w.setFocusedComponent(this);
            }
        }

        /**
         * @see java.awt.Component#isFocusable() Component.isFocusable
         */
	public boolean isFocusable() {
		return focusable;
	}

        /**
         * @see java.awt.Component#setFocusable(boolean) Component.setFocusable
         */
	public void setFocusable(boolean selectable) {
		this.focusable = selectable;
                
                Window w = getWindow();
                if (w!=null) {
                    Component c = w.getMostRecentFocusOwner();
                    if (!focusable && c==this) {
                        w.setFocusedComponent(null);
                        w.getMostRecentFocusOwner();
                    }
                }

	}

        /**
         * @see java.awt.Component#transferFocus() Component.transferFocus
         * @see java.awt.Component#transferFocusBackward() Component.transferFocusBackward
         * @param direction can be up down right or left
         * This method WILL use small scroll, so if the component is too far then it wont gain focus
         */
        public void transferFocus(int direction) {
            parent.breakOutAction(this, direction, true,false);
        }
        
        /**
         * used by Panel when something is added or removed
         * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
         */
	protected void setParent(Panel p) {
            parent = p;
	}

        /**
         * @see java.awt.Component#getParent() Component.getParent
         */
	public Panel getParent() {
		return parent;
	}

        public Window getWindow() {

            if (parent == null) { return (this instanceof Window)?(Window)this : null; }
            return parent.getWindow();
        }

    /**
     * @return the current x coordinate of the component's origin
     * @see javax.swing.JComponent#getX() JComponent.getX
     */
    public int getX(){
        return posX;
    }
    
    /**
     * @return the current y coordinate of the component's origin
     * @see javax.swing.JComponent#getY() JComponent.getY
     */
    public int getY(){
        return posY;
    }
    /**
     * @return the current width of this component
     * @see javax.swing.JComponent#getWidth() JComponent.getWidth
     */
    public int getWidth(){
        return width;
    }
    /**
     * @return the current height of this component
     * @see javax.swing.JComponent#getHeight() JComponent.getHeight
     */
    public int getHeight(){
        return height;
    }
    
    /**
     * @see java.awt.Component#setBounds(int, int, int, int) Component.setBounds
     */
    public void setBounds(int posX, int posY, int width, int height) {
    	setLocation(posX,posY);
    	setSize(width,height);
    }
    
    /**
     * @see java.awt.Component#setSize(int, int) Component.setSize
     */
    public void setSize(int width, int height){
    	this.width = width;
    	this.height = height;
    }
    
    /**
     * @see java.awt.Component#setLocation(int, int) Component.setLocation
     */
    public void setLocation(int posX, int posY){
    	this.posX = posX;
    	this.posY = posY;
    }
	/**
         * override and call super when things HAVE to be painted
         * @see java.awt.Component#paint(java.awt.Graphics) Component.paint
         */
	public void paint(Graphics g) {
		//System.out.println("paint "+this);
		if (border != null) {
			
			border.paintBorder(this, g,width,height);
			
		}
		
		if (background!=-1) {
			
			g.setColor(background);
			g.fillRect(0, 0, width, height);
			
		}
		
		paintComponent(g);
		
	}
    
	public abstract void paintComponent(Graphics g);
    
	public boolean keyEvent(KeyEvent keypad) {
		
		return false;
	}

	public void pointerEvent(int type, int x, int y) {
            if (focusable) {
                if (type == DesktopPane.PRESSED) {
                    if(!isFocusOwner()) { requestFocusInWindow(); }
                }
            }
            else if (parent!=null) {
                parent.pointerEvent(type,x+posX,y+posY);
            }
            //else {
            //    owner.pointerEvent(type,x+getXInWindow(),y+getYInWindow());
            //}
        }
	
	public void animate() throws InterruptedException { }
	
	public void focusLost() { }

	public void focusGained() {
        
            // default focusGained action, make me visible
            makeVisible();
        
        }

        public void makeVisible() {
                        scrollRectToVisible(
                            (border!=null)?-border.getLeft():0, 
                            (border!=null)?-border.getTop():0,
                            getWidthWithBorder(),
                            getHeightWithBorder(),
                            false);
        }
        /**
         * This sets the width and height of this component
         * to the MINIMUM that is needed for this component
         */
        public abstract void workoutSize();
        
        
        /**
         * @return this component's name
         * @see java.awt.Component#getName() Component.getName
         */
        public abstract String getName();
        
        
        /**
         * @param a The color of the background of the component (-1 for no color to be used)
         * @see javax.swing.JComponent#setBackground(java.awt.Color) JComponent.setBackground
         */
	public void setBackground(int a) {
	
		background = a;
		
	}
	
        /**
         * Opaque means it fully paint all its pixels
         * @return true if it is NOT transparent
         * @see javax.swing.JComponent#isOpaque() JComponent.isOpaque
         */
        public boolean isOpaque() {
            if (background!=-1) return true;
            if (border!=null) {
                return border.isBorderOpaque();
            }
            return false;
        }
        
        /**
         * @see java.awt.Component#repaint() Component.repaint
         */
	public void repaint() {
            
                // if we are not in a window, do nothing
                Window w = getWindow();
		if (w==null || !w.isVisible()) return;
		
		if (!isOpaque()) {
			
			Panel p=parent;
			
			while (p!=null) {
				
				if (!p.isOpaque()) {
					p = p.parent;
				}
				else {
					break;
				}
				
			}
			// if we have reached the nothingness
			if (p == null) {
				DesktopPane.getDesktopPane().fullRepaint();
			}
			else {
				DesktopPane.getDesktopPane().repaintComponent(p);
			}
		}
		else {
			DesktopPane.getDesktopPane().repaintComponent(this);
		}
	}

	public String toString() {
		return this.getClass().getName();
	}

        /**
         * @return the border object for this component
         * @see javax.swing.JComponent#getBorder() JComponent.getBorder
         */
	public Border getBorder() {
		return border;
	}

        /**
         * @param border the border to be rendered for this component
         * @see javax.swing.JComponent#setBorder(javax.swing.border.Border) JComponent.setBorder
         */
	public void setBorder(Border border) {
		this.border = border;
	}
	
	public int getWidthWithBorder() {
		
		int w = getWidth();
		
		if (border!=null) {
	
			w = w + border.getRight() + border.getLeft();
		}
		return w;
	}
	public int getHeightWithBorder() {
		
		int h = getHeight();

		if (border!=null) {
			
			h = h + border.getTop() + border.getBottom();

		}
		return h;
	}

	public int getXWithBorder() {
		
		int x = getX();
		
		if (border!=null) {
	
			x = x - border.getLeft();
		}
		return x;
	}
	public int getYWithBorder() {
		
		int y = getY();

		if (border!=null) {
			
			y = y - border.getTop();

		}
		return y;
	}
	
	public void setBoundsWithBorder(int x,int y,int w,int h) {
		
		if (border!=null) {
			
			x = x + border.getLeft();
			y = y + border.getTop();
			w = w - ( border.getRight() + border.getLeft() );
			h = h - ( border.getTop() + border.getBottom() );
			
		}
		setBounds(x,y,w,h);
	}
	
        /**
         * @see java.awt.Component#getLocationOnScreen() Component.getLocationOnScreen
         */
	public int getXOnScreen() {
		
		int x = posX;
		
		Panel p=parent;
		
		while (p!=null) {
			
			x = x+p.posX;
			p = p.parent;
		}
		
		return x;
		
	}
	
        /**
         * @see java.awt.Component#getLocationOnScreen() Component.getLocationOnScreen
         */
	public int getYOnScreen() {
		
		int y = posY;
		
		Panel p=parent;
		
		while (p!=null) {
			
			y = y+p.posY;
			p = p.parent;
		}
		
		return y;
	}
	
	public void wait(int a) throws InterruptedException {

                synchronized (DesktopPane.getDesktopPane()) {
                        DesktopPane.getDesktopPane().wait(a);
                }

	}

	public int getForeground() {
		return foreground;
	}

	public void setForeground(int foreground) {
		this.foreground = foreground;
	}

	public int getBackground() {
		return background;
	}
	
	
	
	
	private ScrollPane scroller;
	
	public void setScrollPanel(ScrollPane s) {
		
		scroller = s;
	}
	
	/**
         * @param a X position inside CURRENT component
         * @param b Y position inside CURRENT component
         * @param c Width of area inside CURRENT component
         * @param d Height of area inside CURRENT component
         * @param smart use smart scroll, if true and the component is too far it will only scroll a bit and not all the way
         * @return if smart was on, returns true if the scroll did reach its destination
         * @see javax.swing.JComponent#scrollRectToVisible(java.awt.Rectangle) JComponent.scrollRectToVisible
         */
	public boolean scrollRectToVisible(int x,int y,int w,int h,boolean smart) {
		
		if (scroller!=null) {
			return scroller.makeVisible(x,y,w,h,smart);
		}
		
		if (parent!=null) {
			
			return parent.scrollRectToVisible(getX()+x,getY()+y,w,h,smart);
		}
		
		return true;
	}
        
        public boolean isRectVisible(int x,int y,int w,int h) {
		
		if (scroller!=null) {
			return scroller.isRectVisible(x,y,w,h);
		}
		
		if (parent!=null) {
			
			return parent.isRectVisible(getX()+x,getY()+y,w,h);
		}
		
		return true;
	}
	
	public boolean scrollUpDown(int d) {
            
            int oldx = posX;
            int oldy = posY;
            
		if (d==Canvas.RIGHT) {	
			scroller.makeVisible(width-1,-posY+scroller.getViewPortY(),1,1,true);
		}
                else if (d==Canvas.LEFT) {
                        scroller.makeVisible(0,-posY+scroller.getViewPortY(),1,1,true);
                }
                else if (d==Canvas.UP) {
                        scroller.makeVisible(-posX+scroller.getViewPortX(),0,1,1,true);
                }  
		else { // DOWN
			scroller.makeVisible(-posX+scroller.getViewPortX(),height-1,1,1,true);
		}
            return oldx!=posX || oldy!=posY;
	}

        /**
         * @see javax.swing.JComponent#updateUI() JComponent.updateUI
         */
        public void updateUI() {
            
            Style theme = DesktopPane.getDefaultTheme(this);
            background = theme.getBackground(Style.ALL);
            foreground = theme.getForeground(Style.ALL);
            border = theme.getBorder(Style.ALL);
        }

        /**
         * @see javax.swing.JComponent#getToolTipText() JComponent.getToolTipText
         * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent) JComponent.getToolTipText
         */
        public String getToolTipText() {
            return tooltip;
        }
        
        /**
         * @param text The Text to use as the tooltip
         * @see javax.swing.JComponent#setToolTipText(java.lang.String) JComponent.setToolTipText
         */
        public void setToolTipText(String text) {
            tooltip = text;
        }
        /**
         * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent) JComponent.getToolTipLocation
         */
        public int getToolTipLocationX() {
            return 5;
        }
        /**
         * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent) JComponent.getToolTipLocation
         */
        public int getToolTipLocationY() {
            return 5;
        }
        
}
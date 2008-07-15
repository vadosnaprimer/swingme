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

/**
 * @author Yura Mamyrin
 * @see javax.swing.JComponent
 */
public abstract class Component {
	
	protected int posX,posY,width,height;
	protected boolean selectable;
	protected Window owner;
	protected Panel parent;
	protected int background;
	protected int foreground;
	protected Border border;
        
        /**
         * @see javax.swing.JComponent#JComponent() JComponent.JComponent
         */
	public Component() {
		
		selectable = true;
		background = -1;
	}
	
	public boolean isFocused() {
		return DesktopPane.getDesktopPane().getFocusedComponent() == this;
	}


	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
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
    
    public void setBounds(int posX, int posY, int width, int height) {
    	setLocation(posX,posY);
    	setSize(width,height);
    }
    
    public void setSize(int width, int height){
    	this.width = width;
    	this.height = height;
    }
    
    public void setLocation(int posX, int posY){
    	this.posX = posX;
    	this.posY = posY;
    }
	
    // override and call super when things HAVE to be painted
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
            if (selectable) {
                if (type == DesktopPane.PRESSED) {
                    DesktopPane.getDesktopPane().setFocusedComponent(this);
                }
            }
            else if (parent!=null) {
                parent.pointerEvent(type,x+posX,y+posY);
            }
            else {
                owner.pointerEvent(type,x+getXInWindow(),y+getYInWindow());
            }
        }
	
	public void animate() { }
	
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
        
        public void workoutSize() { }
        
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
        
	public void repaint() {
            
                // if we are not in a window, do nothing
		if (owner==null) return;
		
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
			// if we have reached the window
			if (p == null) {
				owner.repaint();
			}
			else {
				DesktopPane.getDesktopPane().repaintComponent(p);
			}
		}
		else {
			DesktopPane.getDesktopPane().repaintComponent(this);
		}
	}

	public void setOwnerAndParent(Window ow,Panel p) {
		parent = p;
		owner = ow;
	}

	public Window getOwner() {
		return owner;
	}
	
	public Panel getParent() {
		
		return parent;
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
	
	public int getXInWindow() {
		
		int x = posX;
		
		Panel p=parent;
		
		while (p!=null) {
			
			x = x+p.posX;
			p = p.parent;
		}
		
		return x;
		
	}
	
	public int getYInWindow() {
		
		int y = posY;
		
		Panel p=parent;
		
		while (p!=null) {
			
			y = y+p.posY;
			p = p.parent;
		}
		
		return y;
	}
	
	public void wait(int a) {

		try {
			//if (owner!=null) {
				synchronized (DesktopPane.getDesktopPane()) {
					DesktopPane.getDesktopPane().wait(a);
				}
			//}

		} catch (InterruptedException e) {
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
	public boolean scrollRectToVisible(int a,int b,int c,int d,boolean smart) {
		
		if (scroller!=null) {
			return scroller.makeVisible(a,b,c,d,smart);
		}
		
		if (parent!=null) {
			
			return parent.scrollRectToVisible(getX()+a,getY()+b,c,d,smart);
		}
		
		return true;
	}
	
	public void scrollUpDown(int d) {
            
		if (d==Canvas.RIGHT) {
					
			scroller.makeVisible(width-1,-posY,1,1,true);
			
		}
                else if (d==Canvas.LEFT) {
                    
                        scroller.makeVisible(0,-posY,1,1,true);
                        
                }
                else if (d==Canvas.UP) {
                        scroller.makeVisible(-posX,0,1,1,true);
                }  
		else { // DOWN
			
			scroller.makeVisible(-posX,height-1,1,1,true);

		}
	}
        
        
        public Panel getRootPane() {
            if (parent == null) { return (Panel)this; }
            return parent.getRootPane();
        }

}
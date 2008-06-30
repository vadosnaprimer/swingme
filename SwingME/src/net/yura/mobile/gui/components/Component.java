package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.KeyEvent;

public abstract class Component {
	
	protected int posX,posY,width,height;
	protected boolean transparent,selectable;
	protected Window owner;
	protected Panel parent;
	protected int background;
	protected int foreground;
	protected Border border;
        
	public Component() {
		
		selectable = true;
		transparent = true;
		background = -1;
	}
	
	public boolean isFocused() {
		return RootPane.getRootPane().getActiveComponent() == this;
	}


	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}


    public int getX(){
        return posX;
    }
    

    public int getY(){
        return posY;
    }
	
    public int getWidth(){
        return width;
    }
      
    public int getHeight(){
        return height;
    }
    
    public void setBounds(int posX, int posY, int width, int height) {
    	setPosition(posX,posY);
    	setSize(width,height);
    }
    
    public void setSize(int width, int height){
    	this.width = width;
    	this.height = height;
    }
    
    public void setPosition(int posX, int posY){
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

	public void pointerEvent(int type, int x, int y) { }
	
	public void animate() { }
	
	public void focusLost() { }

	public void focusGained() { }

	public void setBackground(int a) {
	
		background = a;
		
	}
	
	public void repaint() {

                // if we are not in a window, do nothing
		if (owner==null) return;
		
		if (transparent) {
			
			Panel p=parent;
			
			while (p!=null) {
				
				if (p.transparent) {
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
				RootPane.getRootPane().repaintComponent(p);
			}
		}
		else {
			RootPane.getRootPane().repaintComponent(this);
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


	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		this.border = border;
	}
	
	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
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
				synchronized (RootPane.getRootPane()) {
					RootPane.getRootPane().wait(a);
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
	
	// override this to NOT to scroll, or to scroll to the label, etc
	public boolean scrollTo(Component newone) {
		
		return scrollRectToVisible( newone.getXWithBorder(),newone.getYWithBorder(),newone.getWidthWithBorder(),newone.getHeightWithBorder() , true);
	}
	
	// if smart was on, returns true if the scroll did reach its destination
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
	
}
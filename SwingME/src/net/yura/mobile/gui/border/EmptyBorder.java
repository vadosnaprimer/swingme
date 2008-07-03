package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.components.Component;

public class EmptyBorder implements Border {

	protected int top,bottom,left,right;
	
        public EmptyBorder(Border b) {
            this( -b.getTop(),-b.getLeft(),-b.getBottom(),-b.getRight() );
        }
        
	public EmptyBorder(int top, int left, int bottom, int right) {
		
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		
	}
	
	
	public int getBottom() {

		return bottom;
	}


	public int getLeft() {

		return left;
	}


	public int getRight() {

		return right;
	}


	public int getTop() {

		return top;
	}


	public void paintBorder(Component c, Graphics g, int width,int height) { }

}

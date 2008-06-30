package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.components.Component;

public class LineBorder implements Border {

	private int color;
	private int thickness;
	private int stroke;
	// this is how constructors SHOULD LOOK!!!!!!!!
	
	public LineBorder() {
		this(0);
	}
	
	public LineBorder(int c) {
		
		this(c,1);
	}
	
	public LineBorder(int c,int t) {
		
		this(c,t,Graphics.SOLID);
	}
	
	public LineBorder(int c,int t,int s) {
		
		color=c;
		thickness = t;
		stroke = s;
	}
	
	public void paintBorder(Component c, Graphics g,
            int width,
            int height) {
	
		g.setColor(color);
		
		if (thickness==1) {
			
			g.setStrokeStyle(stroke);
			
			g.drawRect(-1, -1, width+1, height+1);
			
			g.setStrokeStyle(Graphics.SOLID);
		}
		else {
			g.fillRect(-thickness, -thickness, width+(thickness*2), thickness);
			g.fillRect(-thickness, height, width+(thickness*2), thickness);
			
			g.fillRect(-thickness, 0, thickness, height );
			g.fillRect(width, 0, thickness, height );
		}
	}

	public int getBottom() {
		return thickness;
	}

	public int getLeft() {
		return thickness;
	}

	public int getRight() {
		return thickness;
	}

	public int getTop() {
		return thickness;
	}

	
	
	
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
	
	

}

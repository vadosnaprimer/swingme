package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.components.Component;

public class CurvedBorder implements Border {

	private int size;
	private int borderColor;
	private int insideColor;
	
	public CurvedBorder(int w,int c1,int c2) {
		
		size = w;
		borderColor = c1;
		insideColor = c2;
		
	}
	
	public int getBottom() {

		return size;
	}

	public int getLeft() {

		return size;
	}

	public int getRight() {

		return size;
	}

	public int getTop() {

		return size;
	}


	public void paintBorder(Component c, Graphics g, int cw, int ch) {
		
		int a = size*4;
		
		g.setColor(insideColor);
		g.fillRoundRect(-size,-size,cw+(2*size)-1,ch+(2*size)-1,a,a);
		
		g.setColor(borderColor);
		g.drawRoundRect(-size,-size,cw+(2*size)-1,ch+(2*size)-1,a,a);
	}

}

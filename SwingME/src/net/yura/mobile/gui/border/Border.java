package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.components.Component;

public interface Border {

	void paintBorder(Component c, Graphics g,int width,int height);
	int getTop();
	int getBottom();
	int getRight();
	int getLeft();
}

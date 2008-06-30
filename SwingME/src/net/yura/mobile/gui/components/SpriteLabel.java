package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

public class SpriteLabel extends ProgressBar {

	private Sprite sprite;

	public SpriteLabel(Sprite sp) {
		
		sprite = sp;
		
		width = sprite.getWidth();
		height = sprite.getHeight();
		
		loaded = sprite.getFrameSequenceLength()-1;
		
	}
	
	public void paintComponent(Graphics g) {

		sprite.setFrame(loading);
		sprite.paint(g);

	}

}

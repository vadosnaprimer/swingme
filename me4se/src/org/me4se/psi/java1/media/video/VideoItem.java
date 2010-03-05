package org.me4se.psi.java1.media.video;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;

/**
 * 
 * @author Stefan Haustein
 *
 */


public class VideoItem extends CustomItem {

	VideoControlImpl videoControl;
	
	VideoItem(VideoControlImpl videoControl) {
		super(null);  // no label 
		this.videoControl = videoControl;
	}

	protected int getMinContentHeight() {
		// TODO Auto-generated method stub
		return videoControl.getDisplayHeight();
	}

	protected int getMinContentWidth() {
		// TODO Auto-generated method stub
		return videoControl.getDisplayWidth();
	}

	protected int getPrefContentHeight(int width) {
		return getMinContentHeight();
	}

	protected int getPrefContentWidth(int height) {
		return getMinContentHeight();
	}

	protected void paint(Graphics g, int w, int h) {
		g.drawImage(videoControl.getCurrentImage(), w/2,h/2, Graphics.HCENTER|Graphics.VCENTER); 
	}

	public void repaint(){
		super.repaint();
	}
	
}

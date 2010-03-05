package com.nokia.mid.ui;

import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @API NOKIAUI
 */
public class DirectUtils {

	private DirectUtils() {
	}

	/**
	 * @API NOKIAUI
	 */
	public static DirectGraphics getDirectGraphics(Graphics g) {
		return new DirectGraphicsImpl(g);
	}

	// What is the difference to new Image(byte[] data, offset, len) ?????

	/**
	 * @API NOKIAUI
	 * @remark: Transparency is lost when graphic operations except from drawImage are called		
	 */
	public static Image createImage(byte imageData[], int imageOffset, int imageLength) {
		Image loaded = Image.createImage(imageData, imageOffset, imageLength);
		Image image = createImage(loaded.getWidth(), loaded.getHeight(), 0); // fully transp.

		image.getGraphics().drawImage(loaded, 0, 0,Graphics.TOP|Graphics.LEFT);

		return image;
	}

	/**
	 * @API NOKIAUI
	 * @remark: Transparency is lost when graphic operations except from drawImage are called	
	 */
	public static Image createImage(int width, int height, int ARGBcolor) {

		Image img = Image.createImage(width, height);

		if (ARGBcolor >= 0) { // transp.
			int[] data = new int[width];
			for (int i = 0; i < data.length; i++)
				data[i] = ARGBcolor;

			img._image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for(int i = 0; i < height; i++){
				img._image.setRGB(0, i, width, 1, data, 0, width);
			}
			img._transparent = true;
		}
		else {
			Graphics g = img.getGraphics();
			g.setColor(ARGBcolor);
			g.fillRect(0, 0, width + 1, height + 1);
		}
		return img;
	}
}

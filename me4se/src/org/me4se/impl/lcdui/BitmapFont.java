package org.me4se.impl.lcdui;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.HashMap;

import org.me4se.impl.skins.Skin;
import javax.microedition.midlet.ApplicationManager;

/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */

public class BitmapFont extends PhysicalFont { //implements ImageObserver {

	/**
	 * Constructor for BitmapFontInfo.
	 * @param type
	 */


//	private Skin skin;
	private BufferedImage image;
	private int[] data;
//	private BufferedImage colorImage;
//	private int colorCache = -1;
	private String fileName;
	HashMap cache = new HashMap();

	private int[] pos = new int[0xff00];

	public BitmapFont(Skin skin, String file) throws IOException {

	//	this.skin = skin;
		//      this.fileName = file;

		// read property file 
		// load image

		ApplicationManager manager = ApplicationManager.getInstance();

		BufferedReader reader = new BufferedReader(new InputStreamReader(manager.openInputStream(skin.getName(file))));

		while (true) {
			String s = reader.readLine();
			if (s == null)
				break;

			int cut = s.indexOf('=');
			if (cut == -1)
				continue;

			String key = s.substring(0, cut).trim().toLowerCase();
			String value = s.substring(cut + 1).trim();

			if (key.equals("font_image")) {
				fileName = skin.getName(value);
				image = manager.getImage(fileName);
				//	skin.prepareImage(image, null);
			} else if (key.equals("font_height"))
				height = Integer.parseInt(value);
			else if (key.equals("font_ascent"))
				ascent = Integer.parseInt(value);
			else if (key.equals("font_descent"))
				descent = Integer.parseInt(value);
			else if (key.equals("font_leading"))
				leading = Integer.parseInt(value);
			else if (key.startsWith("ascii_x-")) {
				int code = Integer.parseInt(key.substring(8));
				pos[code] = Integer.parseInt(value);
			} 
            else if (key.startsWith("ascii_w-")) {
				int code = Integer.parseInt(key.substring(8));
				int w = Integer.parseInt(value);
				for (int i = code + 1; i < pos.length; i++) {
					pos[i] = pos[i - 1] + w;
				}
			}
            else if(key.startsWith("0x")){
                int code = Integer.parseInt(key.substring(2), 16);
                pos[code] = Integer.parseInt(value);
            }
            else
				System.err.println("unrecognized: " + key + "=" + value);
		}

		if (image == null)
			throw new RuntimeException("Image not loaded!");
	}

	public int charWidth(char c) {
		if (c >= pos.length - 1)
			c = '?';
		return pos[((int) c) + 1] - pos[(int) c];
	}

	/* dont ask why this is necessary.... :-(
	
	public void recurse (Container c) {
	   for (int i = 0; i < c.getComponentCount(); i++) {
	        Component c2 = c.getComponent(i);
	       if (c2 instanceof Container) 
	           recurse ((Container) c2);
	       else 
	           c2.repaint ();
	   }
	}
	*/

	public void drawChar(Graphics g, char c, int x, int y) {
		//g.drawRect (x, y-ascent, charWidth (c)-1,height-1);
		if (c >= pos.length)
			c = '?';

		int ix = pos[((int) c)];
		int w = charWidth(c);

		int color = g.getColor().getRGB() | (g.getColor().getAlpha() << 24);
		BufferedImage img;
		// caused extremely poor performance on OSX....
//		if (color == 0x0ff000000){
//			img = image;
//		}
//		else 
		if ((color & 0x0ff000000) == 0){
			return;
		}
		else {
			
			Integer ci = new Integer(color);
			img = (BufferedImage) cache.get(ci);
			
			if(img == null){
				// TODO MK FontFolor commented out
				//System.out.println("Font color "+color + " n/a ");
				
				if(cache.size() > 256){
					System.out.println("Clearing font cache (size > 256)");
				}
				
				System.out.println("Font color "+color + " not in cache, building.... ");
				
				if(data == null){
					data = new int[image.getHeight()*image.getWidth()];
					image.getRGB(0, 0, image.getWidth(), image.getHeight(), data, 0, image.getWidth());
				}

				img= new BufferedImage(image.getWidth(), image.getHeight(),
						BufferedImage.TYPE_INT_ARGB);
				
				for(int i = 0; i < data.length; i++){
				
					if((data[i] & 0x0ff000000) != 0){
						data[i] =  color;
					}
				}
				img.setRGB(0, 0, image.getWidth(), image.getHeight(), data, 0, image.getWidth());

				cache.put(ci, img);
			}
			
		}

		g.drawImage(img, x, y - ascent, x + w, y - ascent + height, ix, 0, ix + w, height, null);
	}

	public void drawString(Graphics g, String s, int x, int y) {

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			drawChar(g, c, x, y);
			x += charWidth(c);
		}
	}
}

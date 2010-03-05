// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS:
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package javax.microedition.lcdui;

import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.midlet.ApplicationManager;


import org.me4se.impl.lcdui.PhysicalFont;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0 
 */
public class Graphics {

	/**
	 * @API MIDP-1.0
	 */
	public static final int HCENTER = 1;

	/**
	 * @API MIDP-1.0
	 */
	public static final int VCENTER = 2;

	/**
	 * @API MIDP-1.0
	 */
	public static final int LEFT = 4;

	/**
	 * @API MIDP-1.0
	 */
	public static final int RIGHT = 8;

	/**
	 * @API MIDP-1.0
	 */
	public static final int TOP = 16;

	/**
	 * @API MIDP-1.0
	 */
	public static final int BOTTOM = 32;

	/**
	 * @API MIDP-1.0
	 */
	public static final int BASELINE = 64;

	/**
	 * @API MIDP-1.0
	 */
	public static final int SOLID = 0;

	/**
	 * @API MIDP-1.0
	 */
	public static final int DOTTED = 1;

	/**
	 * @ME4SE INTERNAL
	 */
	public int translateX = 0;

	/**
	 * @ME4SE INTERNAL
	 */
	public int translateY = 0;

	/**
	 * @ME4SE INTERNAL
	 */
	public int strokeStyle = SOLID;

//	boolean stale;

	private java.awt.Graphics awtGraphics;

	Font font;
	public int _argbColor;
//	java.awt.Color currentColorObject = java.awt.Color.white;

	/** @ME4SE INTERNAL 
	 * @remark Required for Nokia direct Graphics support */

//	public int _argbColor = 0x0ffffffff;
	Canvas canvas;
	BufferedImage image;
	BufferedImage tmpImg;


	/**
	 * @ME4SE INTERNAL
	 */
	Graphics(Canvas canvas, BufferedImage image, java.awt.Graphics g) {
		this.awtGraphics = g;
		this.canvas = canvas;
		this.image = image;
		setFont(Font.getDefaultFont());
	}

	/**
	 * Returns an AWT graphics object. Indirect access is neccessary to support modifyable
	 * transparent images to some extent.
	 * 
	 * @ME4SE INTERNAL
	 */

	public java.awt.Graphics _getAwtGraphics() {
		if (awtGraphics == null) {

//			if (image._transparent) { // getGraphics will destroy transparency...
//				BufferedImage mutable =
//					 new BufferedImage(
//						image.getWidth(),
//						image.getHeight(), BufferedImage.TYPE_INT_ARGB);
//
//				awtGraphics = mutable.getGraphics();
//
//				ApplicationManager.manager.drawImage(awtGraphics, image._image, 0, 0, image.name);
//				image._image = mutable;
//				image._transparent = false;
//			}
//			else
				awtGraphics = image.getGraphics();
		}

		return awtGraphics;
	}

	/**
	 * @ME4SE INTERNAL 
	 * @remark Added in order to allow pixel grabbing in the Nokia API*/

	public BufferedImage _getAwtImage(int[] xy0) {
		return image;

	}

    /* no longer needed -- graphics is always buffered
	private void checkStale() {
		if (stale && canvas != null) {
			canvas.component.paint(canvas.component.getGraphics());
		}
	}
    */

	/**
	 * @API MIDP-1.0
	 */
	public void drawArc(int x, int y, int w, int h, int sa, int aa) {
		

		_getAwtGraphics().drawArc(x, y, w, h, sa, aa);
		//checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawChars(char[] chars, int ofs, int len, int x, int y, int align) {
		drawString(new String(chars, ofs, len), x, y, align);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawImage(Image img, int x, int y, int align) {
		ApplicationManager manager = ApplicationManager.getInstance();

		int w = img._image.getWidth();
		int h = img._image.getHeight();
		x = normalizeX(x, w, align);
		y = normalizeY(y, h, align);

//		if (image != null && image._transparent) {
//			// TODO: Test this... is it correct to add the translation? what about clipping?
//
//			x += getTranslateX();
//			y += getTranslateY();
//
//			int[] data = new int[w * h];
//			img.getRGB(data, 0, w, 0, 0, w, h);
//
//			image._image =
//				java.awt.Toolkit.getDefaultToolkit().createImage(
//					new java.awt.image.FilteredImageSource(
//						image._image.getSource(),
//						new DrawImageFilter(data, x, y, w, h)));
//		}
//		else {
			_getAwtGraphics().drawImage(img._image, x, y, null);
//		}

		//g.drawRect (x, y, img.getWidth (), img.getHeight ());

		//System.out.println ("drawimg stale: "+stale);
		//checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawLine(int x0, int y0, int x1, int y1) {
		if (strokeStyle == SOLID) {
			_getAwtGraphics().drawLine(x0, y0, x1, y1);
		}
		else {
			int dx = x1 - x0;
			int dy = y1 - y0;
			int steps = Math.max(Math.abs(dx), Math.abs(dy)) / 4;
			dx = (dx << 16) / steps;
			dy = (dy << 16) / steps;
			x0 = x0 << 16;
			y0 = y0 << 16;

			while (steps > 0) {
				_getAwtGraphics().drawLine(x0 >> 16, y0 >> 16, (x0 + dx) >> 16, (y0 + dy) >> 16);
				x0 += dx + dx;
				y0 += dy + dy;
				steps -= 2;
			}
		}
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawString(String text, int x, int y, int align) {
    
	  // the vertical alignment
	  int va = align & (TOP | BASELINE | BOTTOM);
    // the horizontal alignment
	  int ha = align & (LEFT | HCENTER | RIGHT);

	  //both values must be set
    if ((va != 0 && ha == 0) || (va == 0 && ha != 0)) {
      throw new IllegalArgumentException("Graphics: Invalid Anchor. Both anchors (v and h)  must be set. Anchor is: " + align);
    }
    
		if (text == null)
			return;

		PhysicalFont fm = font.info.font;

		int cut = text.indexOf('\n');
		if (cut != -1) {
			drawString(text.substring(cut + 1), x, y + fm.height, align);
			text = text.substring(0, cut);
		}

		//y--;
		switch (align & (TOP | BASELINE | BOTTOM )) {
			case 0:
			case TOP :
				y += fm.ascent;
				break;
			case BASELINE :
				break;
      case BOTTOM :
        y -= fm.descent;
        break;
      //case LEFT:
      //  break;
      //case HCENTER:
      //  break;
      //case RIGHT:
      //  break;
			// VCENTER is not supported for Strings !!! (MK)
      //case VCENTER :
			//	y = y + fm.ascent - fm.height / 2;
			//	break;
			default :
				throw new IllegalArgumentException("Graphics: Invalid Anchor. Anchor is: " + align);
		}

		font.info.drawString(
			_getAwtGraphics(),
			text,
			normalizeX(x, fm.stringWidth(text), align),
			y);
		//checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawRect(int x, int y, int w, int h) {
       
        _getAwtGraphics().drawRect(x, y, w, h);
        //checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawRoundRect(int x, int y, int w, int h, int r1, int r2) {
		
		_getAwtGraphics().drawRoundRect(x, y, w, h, r1, r2);
		//checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void fillArc(int x, int y, int w, int h, int sa, int aa) {
	
		_getAwtGraphics().fillArc(x, y, w, h, sa, aa);
		//checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void fillRect(int x, int y, int w, int h) {
        
//        if((_argbColor & 0x0ff000000) != 0x0ff000000){
//            
//            int[] data = new int[w];
//            for(int i = 0; i < data.length; i++){
//                data[i] = _argbColor;
//            }
//            
//            Image row = Image.createRGBImage(data, w, 1, true);
//            for(int i = 0; i < h; i++){
//                drawImage(row, x , y+i, 0);
//            }
//        }
//        else{
		_getAwtGraphics().fillRect(x, y, w, h);
       
		//checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void fillRoundRect(int x, int y, int w, int h, int r1, int r2) {
		_getAwtGraphics().fillRoundRect(x, y, w, h, r1, r2);
		//checkStale();
	}

	/**
	 * @API MIDP-1.0
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getClipX() {
		java.awt.Rectangle r = _getAwtGraphics().getClipBounds();
		return r == null ? -translateX : r.x;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getClipY() {
		java.awt.Rectangle r = _getAwtGraphics().getClipBounds();
		return r == null ? -translateY : r.y;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getClipWidth() {
		java.awt.Rectangle r = _getAwtGraphics().getClipBounds();
		return r == null ? (canvas == null ? image.getWidth() : canvas.getWidth()) : r.width;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getClipHeight() {
		java.awt.Rectangle r = _getAwtGraphics().getClipBounds();
		return r == null ? (canvas == null ? image.getHeight() : canvas.getHeight()) : r.height;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getColor() {
		return _argbColor & 0x0ffffff;
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setFont(Font font) {
		if (font == null)
			font = Font.getDefaultFont();
		//g.setFont(font.info.font);
		this.font = font;
	}

	/**
	 * @API MIDP-1.0
	 */
	public void translate(int x, int y) {
		translateX += x;
		translateY += y;
		
		_getAwtGraphics().translate(x, y);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void clipRect(int x, int y, int w, int h) {
		_getAwtGraphics().clipRect(x, y, w, h);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setClip(int x, int y, int w, int h) {
		_getAwtGraphics().setClip(x, y, w, h);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setColor(int c) {

		if((c | 0x0ff000000) != _argbColor){
			_argbColor = c | 0x0ff000000;
			_getAwtGraphics().setColor(new java.awt.Color(ApplicationManager.getInstance().getDeviceColor(c) & 0x0ffffff));
		}
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setColor(int cr, int cg, int cb) {
		setColor((cr << 16) | (cg << 8) | cb);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setGrayScale(int gsc) {
		setColor(gsc, gsc, gsc);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {

		drawString(str.substring(offset, offset + len), x, y, anchor);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void drawChar(char character, int x, int y, int anchor) {
		char characters[] = new char[1];
		characters[0] = character;
		drawString(new String(characters), x, y, anchor);
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getBlueComponent() {
		return _getAwtGraphics().getColor().getBlue();
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getGreenComponent() {
		return _getAwtGraphics().getColor().getGreen();
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getRedComponent() {
		return _getAwtGraphics().getColor().getRed();
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getGrayScale() {
		return getRedComponent();
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getTranslateX() {
		return translateX;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getTranslateY() {
		return translateY;
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setStrokeStyle(int style) {
		strokeStyle = style;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getStrokeStyle() {
		return strokeStyle;
	}

	private int normalizeX(int x, int w, int anchor) {

		switch (anchor & (LEFT | RIGHT | HCENTER)) {
			case 0 :
			case LEFT :
				return x;
			case RIGHT :
				return x - w;
			case HCENTER :
				return x - w / 2;
		}
		throw new IllegalArgumentException();
	}

	private int normalizeY(int y, int h, int anchor) {

		switch (anchor & (TOP | BOTTOM | BASELINE | VCENTER)) {
			case 0 :
			case TOP :
				return y;
			case BOTTOM :
				return y - h;
			case VCENTER :
				return y - h / 2;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * @API MIDP-2.0
	 */
	public void drawRegion(
		Image src,
		int x_src,
		int y_src,
		int width,
		int height,
		int transform,
		int x_dest,
		int y_dest,
		int anchor) {
        
        if(transform == Sprite.TRANS_NONE){

            x_dest = normalizeX(x_dest, width, anchor);
            y_dest = normalizeY(y_dest, height, anchor);
            
            int cx = getClipX();
            int cy = getClipY();
            int cw = getClipWidth();
            int ch = getClipHeight();
            clipRect(x_dest, y_dest, width, height);
            drawImage(src, x_dest - x_src, y_dest - y_src, 0);
            setClip(cx, cy, cw, ch);
        }
        else{
         //   System.out.println("falling back to image creation; transf: "+transform);
   
            Image trans = Image.createImage(src, x_src, y_src, width, height, transform);
            drawImage(trans, x_dest, y_dest, anchor);
        }
	}

	/**
	 * @API MIDP-2.0
	 */
	public void copyArea(
		int x_src,
		int y_src,
		int width,
		int height,
		int x_dest,
		int y_dest,
		int anchor) {
		if (image == null)
			throw new RuntimeException("Only valid for images");
        
    //    System.out.println("Graphics.copyArea() w="+width+"h="+height);
        
		_getAwtGraphics().copyArea(
			x_src,
			y_src,
			width,
			height,
			normalizeX(x_dest, width, anchor),
			normalizeY(y_dest, height, anchor));
	}

	/**
	 * @API MIDP-2.0
	 */
	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		_getAwtGraphics().fillPolygon(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, 3);
	}

	/**
	 * @API MIDP-2.0
	 */
	public synchronized void drawRGB(
		int[] rgbData,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height,
		boolean processAlpha) {
	  
	  if(width == 0 || height == 0) return;

//        System.out.println("drawRGB x="+x+" y="+y +"w="+width+" h="+height);
		
		if(image != null && !processAlpha){
			image.setRGB(x, y, width, height, rgbData, 0, scanlength);
		}
		else{
        if(tmpImg == null || tmpImg.getWidth() < width || tmpImg.getHeight() < height){
        	tmpImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        tmpImg.setRGB(0, 0, width, height, rgbData, offset, scanlength);

        java.awt.Graphics g = _getAwtGraphics();
        Shape clip = g.getClip();
        g.clipRect(x, y, width, height);
        g.drawImage(tmpImg, x, y, null);
        g.setClip(clip);
		}
	}

	/**
	 * @API MIDP-2.0
	 */
	public int getDisplayColor(int color) {
		return ApplicationManager.getInstance().getDeviceColor(color);
	}

}
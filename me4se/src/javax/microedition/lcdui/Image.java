// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors: Geoff Hubbard, Andre Gerard
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

import java.awt.image.*;
import java.io.*;

import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.lcdui.Color2GrayFilter;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0 
 */
public class Image {

	/** 
	 * bit 2 (value 4) seems to indicate odd rotation (90,270=w/h swap). 
	 * Constants copied here to avoid Sprite dependency */

	private static final int TRANS_NONE = 0;
	private static final int TRANS_MIRROR_ROT180 = 1;
	private static final int TRANS_MIRROR = 2;
	private static final int TRANS_ROT180 = 3;
	private static final int TRANS_MIRROR_ROT270 = 4;
	private static final int TRANS_ROT90 = 5;
	private static final int TRANS_ROT270 = 6;
	private static final int TRANS_MIRROR_ROT90 = 7;

    
	/** 
	 * Required for Siemens and Nokia API support. 
	 * Please note: Drawing operations on images with transparentcy 
	 * require to obtain a mutable image (without transparency).
	 *
     * This is best checked in a general "check" method before
     * executing drawing operations in the Graphics class.
	 * 	 
	 * @ME4SE INTERNAL 
	 */
	public boolean _transparent;

	/** 
	 * The underlying AWT image, but adopted to the device LCD color. 
     * May contain transparency if not mutable.
	 * 
	 * @ME4SE INTERNAL
	 */

    
	public BufferedImage _image;

	boolean mutable;

	// IMPROVE: helper does not seem to be referenced anywhere. Is it really needed??
	static java.awt.Component helper = new java.awt.Panel();
	String name;

    static int imageCreationCount;
    
    
	/**
	 * Attention: this call performs the gray filter step!!!
	 * 
	 * @ME4SE INTERNAL 
	 */
	protected Image(BufferedImage image, boolean mutable, boolean toGray, String name) {
		Display.check();
        
        // Give VM some time to cleanup every n images... 
//        if(((++imageCreationCount) & 7) == 0){
//            try{
//                Thread.sleep(4);
//            }
//            catch(InterruptedException e){
//                
//            }
//        }
        
//		if(toGray){
//		    this._image = java.awt.Toolkit.getDefaultToolkit().createImage(
//					new FilteredImageSource(
//						image.getSource(),
//						Color2GrayFilter.instance));
//        
//		}
//		else{
			this._image = image;
//		}
		this.mutable = mutable;
		this.name = name;
	}

	/** 
	 * MIDP spec requires that every pixel is white. 
	 * 
	 * @API MIDP-1.0 
	 */
	public static Image createImage(int w, int h) {
		Display.check();

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			ApplicationManager.getInstance().awtContainer.createImage(w, h);
		java.awt.Graphics g = img.getGraphics();
		g.setColor(ApplicationManager.getInstance().bgColor);
		g.fillRect(0, 0, w, h);

		return new Image(img, true, false, "createImage(x,y)");
	}

	/**
	 * @API MIDP-1.0 
	 */
	public static Image createImage(byte[] data, int start, int len) {

	  try{
		return new Image(
			ApplicationManager.getInstance().createImage(data, start, len),
			false, true, "createImage(byte[] data, start, len)");
	  } catch (IllegalArgumentException e) {
	    throw e;
	  } catch (ArrayIndexOutOfBoundsException e) {
	    throw e;
	  } catch (NullPointerException e) {
	    throw e;
	  } catch (Exception e) {
	    throw new IllegalArgumentException(e.toString());
	  }
	}

	/**
	 * @API MIDP-1.0 
	 */
	public static Image createImage(String name) throws IOException {
		return
			new Image(ApplicationManager.getInstance().getImage(name), false, true, name);
	}

	/*
	 * Additional siemes method
	 * @API SIEMENS 

	public static Image createImageFromBitmap(
		byte[] bytes,
		int width,
		int height) {

		Display.check();

		int pix[] = new int[width * height];
		int index = 0;

		int srcPos = 0;
		int dstPos = 0;

		int bgcolor = ApplicationManager.manager.bgColor.getRGB();

		for (int y = 0; y < height; y++) {
			int mask = 128;
			for (int x = 0; x < width; x++) {
				pix[dstPos++] =
				 ((((bytes[srcPos] & mask) == 0) ? 0x0ffffffff : 0x0ff000000));

				mask = mask >> 1;
				if (mask == 0) {
					mask = 128;
					srcPos++;
				}
			}

			if (mask != 128)
				srcPos++;
		}

		return new Image(
			java.awt.Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height, pix, 0, width)),
			false,
			true,
			"createImageFromBitmap(..)");
	}

	/**
	 * Additional siemes method
	 * 
	 * @API SIEMENS 
	 
	public static Image createTransparentImageFromBitmap(
		byte[] bytes,
		int width,
		int height) {

		Display.check();

		int pix[] = new int[width * height];
		int index = 0;

		int srcPos = 0;
		int dstPos = 0;

		int[] colors =
			{
				0x000ffffff,
				0x0ffffffff,
				0x0ff000000,
				0x0ff000000 };

		for (int y = 0; y < height; y++) {
			int shift = 6;
			for (int x = 0; x < width; x++) {
				pix[dstPos++] = colors[(bytes[srcPos] >> shift) & 3];

				shift -= 2;
				if (shift < 0) {
					shift = 6;
					srcPos++;
				}
			}
			if (shift != 6)
				srcPos++;
		}

		Image ret =
			new Image(
				java.awt.Toolkit.getDefaultToolkit().createImage(
					new MemoryImageSource(width, height, pix, 0, width)),
				false, true, "createTransparentImageFromBitmap");
		ret._transparent = true;
		return ret;
	}
	 */

	
	/**
	 * @API MIDP-1.0 
	 */
	public static Image createImage(Image source) {
		Display.check();

		if (source == null){
			throw new NullPointerException();
        }
		if (!source.isMutable()){
			return source;
        }

		Image copy = createImage(source.getWidth(), source.getHeight());
		copy.getGraphics().drawImage(
			source,
			0,
			0,
			Graphics.TOP | Graphics.LEFT);

		copy.mutable = false;
		return copy;
	}

	/**
	 * @API MIDP-1.0 
	 */
	public Graphics getGraphics() {
		if (!mutable)
			throw new IllegalStateException();
			
		return new Graphics(null, _image, null);
	}

	/**
	 * @API MIDP-1.0 
	 */
	public int getWidth() {
		return _image.getWidth();
	}

	/**
	 * @API MIDP-1.0 
	 */
	public int getHeight() {
		return _image.getHeight();
	}

	/**
	 * @API MIDP-1.0 
	 */
	public boolean isMutable() {
		return mutable;
	}

	/**
	 * Creates an immutable image using pixel data from the specified region 
	 * of a source image, transformed as specified. The source image may be mutable 
	 * or immutable. For immutable source images, transparency information, if any, 
	 * is copied to the new image unchanged. On some devices, pre-transformed images 
	 * may render more quickly than images that are transformed on the fly using drawRegion. 
	 * However, creating such images does consume additional heap space, so this technique 
	 * should be applied only to images whose rendering speed is critical.
	 * 
	 * The transform function used must be one of the following, as defined in the Sprite class:
	 * Sprite.TRANS_NONE - causes the specified image region to be copied unchanged
	 * Sprite.TRANS_ROT90 - causes the specified image region to be rotated clockwise by 90 degrees.
	 * Sprite.TRANS_ROT180 - causes the specified image region to be rotated clockwise by 180 degrees.
	 * Sprite.TRANS_ROT270 - causes the specified image region to be rotated clockwise by 270 degrees.
	 * Sprite.TRANS_MIRROR - causes the specified image region to be reflected about its vertical center.
	 * Sprite.TRANS_MIRROR_ROT90 - causes the specified image region to be reflected about its vertical 
	 *                             center and then rotated clockwise by 90 degrees.
	 * Sprite.TRANS_MIRROR_ROT180 - causes the specified image region to be reflected about its vertical 
	 *                              center and then rotated clockwise by 180 degrees.
	 * Sprite.TRANS_MIRROR_ROT270 - causes the specified image region to be reflected about its vertical 
	 *                              center and then rotated clockwise by 270 degrees.
	 * 
	 * The size of the returned image will be the size of the specified region with the 
	 * transform applied. For example, if the region is 100 x 50 pixels and the transform 
	 * is TRANS_ROT90, the returned image will be 50 x 100 pixels.
	 * 
	 * Note: If all of the following conditions are met, this method may simply return 
	 * the source Image without creating a new one:
	 * 
	 * the source image is immutable;
	 * the region represents the entire source image; and
	 * the transform is TRANS_NONE.
	 * 
	 * @param image the source image to be copied from
	 * @param x the horizontal location of the region to be copied
	 * @param y  the vertical location of the region to be copied
	 * @param width the width of the region to be copied
	 * @param height the height of the region to be copied
	 * @param transform the transform to be applied to the region
	 * @return the new, immutable image
	 * @throws NullPointerException if image is null
	 * @throws IllegalArgumentException if the region to be copied exceeds the bounds of the source image
	 * @throws IllegalArgumentException if either width or height is zero or less
	 * @throws IllegalArgumentException if the transform is not valid
	 * 
	 * @API MIDP-2.0
	 * @remaks Transformations unimplemented, but no problem given the image data
	 */
	public static Image createImage(
		Image image,
		int x,
		int y,
		int width,
		int height,
		int transform) {
		int[] buf = new int[width * height];
		image.getRGB(buf, 0, width, x, y, width, height);

		int th;
		int tw;

		if ((transform & 4) != 0) {
			th = width;
			tw = height;
		} else {
			th = height;
			tw = width;
		}

		if (transform != 0) {
			int[] trans = new int[buf.length];
			int sp = 0;
			for (int sy = 0; sy < height; sy++) {
				int tx;
				int ty;
				int td;

				switch (transform) {
					case TRANS_ROT90 :
						tx = tw - sy - 1;
						ty = 0;
						td = tw;
						break;
					case TRANS_ROT180 :
						tx = tw - 1;
						ty = th - sy - 1;
						td = -1;
						break;
					case TRANS_ROT270 :
						tx = sy;
						ty = th - 1;
						td = -tw;
						break;
					case TRANS_MIRROR : // mirror horizontal
						tx = tw-1;
						ty = sy;
						td = -1;
						break;
					case TRANS_MIRROR_ROT90 :
						tx = tw-sy-1;
						ty = th-1;
						td = -tw;
						break;
					case TRANS_MIRROR_ROT180 :
						tx = 0;
						ty = th - sy - 1;
						td = 1;
						break;
					case TRANS_MIRROR_ROT270 :
						tx = sy;
						ty = 0;
						td = tw;
						break;
					default :
						throw new RuntimeException(
							"illegal transformation: " + transform);
				}

				int tp = ty * tw + tx;
				for (int sx = 0; sx < width; sx++) {
					trans[tp] = buf[sp++];
					tp += td;
				}
			}
			buf = trans;
		}

		return createRGBImage(buf, tw, th, true);
	}

	/**
	 * Creates an immutable image from decoded image data obtained from an InputStream. 
	 * This method blocks until all image data has been read and decoded. After this method 
	 * completes (whether by returning or by throwing an exception) the stream is left open 
	 * and its current position is undefined.
	 * 
	 * @param stream the name of the resource containing the image data in one of the supported image formats
	 * @return the created image
	 * @throws NullPointerException if stream is null
	 * @throws IOException  if an I/O error occurs, if the image data cannot be loaded, 
	 *                      or if the image data cannot be decoded
	 * 
	 * @API MIDP-2.0
	 */
	public static Image createImage(InputStream stream) throws IOException {
		Display.check();

		Image img =
			new Image(
				ApplicationManager.getInstance().createImage(stream),
				false, true,
				"createImage(InputStream)");
		return img;
	}

	/**
	 * Creates an immutable image from a sequence of ARGB values, specified as 0xAARRGGBB. 
	 * The ARGB data within the rgb array is arranged horizontally from left to right within 
	 * each row, row by row from top to bottom. If processAlpha is true, the high-order byte 
	 * specifies opacity; that is, 0x00RRGGBB specifies a fully transparent pixel and 0xFFRRGGBB 
	 * specifies a fully opaque pixel. Intermediate alpha values specify semitransparency. 
	 * If the implementation does not support alpha blending for image rendering operations, 
	 * it must replace any semitransparent pixels with fully transparent pixels. (See Alpha 
	 * Processing for further discussion.) If processAlpha is false, the alpha values are 
	 * ignored and all pixels must be treated as fully opaque. 
	 * Consider P(a,b) to be the value of the pixel located at column a and row b of the Image, 
	 * where rows and columns are numbered downward from the top starting at zero, and columns 
	 * are numbered rightward from the left starting at zero. This operation can then be defined as:
	 * 
	 * P(a, b) = rgb[a + b * width];
	 * 
	 * for
	 * 
	 * 0 <= a < width
	 * 0 <= b < height
	 * 
	 * @param rgb an array of ARGB values that composes the image
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param processAlpha true if rgb has an alpha channel, false if all pixels are fully opaque
	 * @return the created image
	 * @throws NullPointerException if rgb is null.
	 * @throws IllegalArgumentException if either width or height is zero or less
	 * @throws ArrayIndexOutOfBoundsException if the length of rgb is less than width * height.
	 * 
	 * @API MIDP-2.0
	 */
	public static Image createRGBImage(
		int[] rgb,
		int width,
		int height,
		boolean processAlpha) {
		if (!processAlpha) {
			int[] opaque = new int[rgb.length];
			for (int i = 0; i < rgb.length; i++) {
				opaque[i] = rgb[i] | 0x0ff000000;
			}
			rgb = opaque;
		}
        
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bi.setRGB(0, 0, width, height, rgb, 0, width);
		return new Image(bi,	false, true,
			"createRGBImage");
	}

	/**
	 * Obtains ARGB pixel data from the specified region of this image and stores it in the 
	 * provided array of integers. Each pixel value is stored in 0xAARRGGBB format, where 
	 * the high-order byte contains the alpha channel and the remaining bytes contain color 
	 * components for red, green and blue, respectively. The alpha channel specifies the opacity 
	 * of the pixel, where a value of 0x00 represents a pixel that is fully transparent and a value 
	 * of 0xFF represents a fully opaque pixel.
	 * 
	 * The returned values are not guaranteed to be identical to values from the original source,
	 *  such as from createRGBImage or from a PNG image. Color values may be resampled to reflect 
	 * the display capabilities of the device (for example, red, green or blue pixels may all be 
	 * represented by the same gray value on a grayscale device). On devices that do not support 
	 * alpha blending, the alpha value will be 0xFF for opaque pixels and 0x00 for all other pixels 
	 * (see Alpha Processing for further discussion.) On devices that support alpha blending, alpha 
	 * channel values may be resampled to reflect the number of levels of semitransparency supported.
	 * 
	 * The scanlength specifies the relative offset within the array between the corresponding pixels 
	 * of consecutive rows. In order to prevent rows of stored pixels from overlapping, the absolute 
	 * value of scanlength must be greater than or equal to width. Negative values of scanlength are 
	 * allowed. In all cases, this must result in every reference being within the bounds of the 
	 * rgbData array.
	 * 
	 * Consider P(a,b) to be the value of the pixel located at column a and row b of the Image, 
	 * where rows and columns are numbered downward from the top starting at zero, and columns 
	 * are numbered rightward from the left starting at zero. This operation can then be defined as:
	 * 
	 * rgbData[offset + (a - x) + (b - y) * scanlength] = P(a, b);
	 * 
	 * for
	 * 
	 * x <= a < x + width
	 * y <= b < y + height
	 * 
	 * The source rectangle is required to not exceed the bounds of the image. This means:
	 * 
	 * x >= 0
	 * y >= 0
	 * x + width <= image width
	 * y + height <= image height
	 * 
	 * If any of these conditions is not met an IllegalArgumentException is thrown. Otherwise, in 
	 * cases where width <= 0 or height <= 0, no exception is thrown, and no pixel data is copied 
	 * to rgbData.
	 * 
	 * @param rgbData an array of integers in which the ARGB pixel data is stored
	 * @param offset the index into the array where the first ARGB value is stored
	 * @param scanlength the relative offset in the array between corresponding pixels 
	 *                   in consecutive rows of the region
	 * @param x the x-coordinate of the upper left corner of the region
	 * @param y the y-coordinate of the upper left corner of the region
	 * @param width the width of the region
	 * @param height the height of the region
	 * @throws ArrayIndexOutOfBoundsException if the requested operation would attempt 
	 *                                        to access an element in the rgbData array 
	 *                                        whose index is either negative or beyond its 
	 *                                        length (the contents of the array are unchanged)
	 * @throws IllegalArgumentException if the area being retrieved exceeds the bounds of the source image
	 * @throws IllegalArgumentException if the absolute value of scanlength is less than width
	 * @throws NullPointerException if rgbData is null
	 * 
	 * @API MIDP-2.0
	 * 
	 */
	public void getRGB(
		int[] rgbData,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height) {

		PixelGrabber pg =
			new PixelGrabber(
				_image,
				x,
				y,
				width,
				height,
				rgbData,
				offset,
				scanlength);
		boolean ok = false;
		do {
			try {
				pg.grabPixels(0);
				ok = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!ok);
		// TODO convert device colors back to RGB values(!)

	}
}

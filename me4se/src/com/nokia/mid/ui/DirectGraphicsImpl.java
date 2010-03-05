/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.nokia.mid.ui;


import java.awt.Color;
import java.awt.image.PixelGrabber;

import javax.microedition.lcdui.*;


/**
 * @author Michael Kroll, Stefan Haustein
 *
 * DirectGraphics contains some graphics extensions for MIDP Graphics, 
 * with which polygons and triangles can be drawn and filled, images 
 * can be rotated or flipped, alpha channel color supported and raw 
 * pixel data can be directly obtained from the graphics context 
 * or drawn to it. DirectUtils.getDirectGraphics(Graphics g) method 
 * can be used to convert lcdui Graphics to Nokia DirectGraphics object: 
 * <code>
 * DirectGraphics dg = DirectUtils.getDirectGraphics(g);
 * </code>
 * The operations to DirectGraphics reference will also affect the original 
 * graphics context, for example, setting the color with DirectGraphics will 
 * change the current painting color for Graphics and vice versa. In fact, 
 * the developer can see DirectGraphics as a new way to do calls to Graphics. 
 * DirectGraphics does not inherit the standard Graphics because of API dependency. 
 * Following methods in Graphics affect also rendering operations of DirectGraphics: 
 * <ul>
 * <li><code>clipRect(int x, int y, int width, int height)</code></li> 
 * <li><code>setClip(int x, int y, int width, int height)</code></li> 
 * <li><code>setColor(int RGB)</code></li> 
 * <li><code>setColor(int red, int green, int blue)</code></li> 
 * <li><code>setGrayScale(int value)</code></li> 
 * <li><code>setStrokeStyle(int style)</code></li> 
 * <li><code>translate(int x, int y)</code></li> 
 * <ul>
 * 
 * Following method in DirectGraphics affect rendering operations via Graphics: 
 * <code>setARGBColor(int argbColor)</code> 
 * All rendering operations via Graphics or DirectGraphics draw on same graphics context. 
 * ARGB values used with some methods of this interface are interpreted with the least 
 * significant eight bits giving the blue component, the next eight more significant bits, 
 * the green component, the next eight more significant bits, the red component, 
 * and the next eight more significant bits, the alpha component. In other words, 
 * the color component is specified in the form of 0xAARRGGBB. This corresponds to 
 * the native format specified by TYPE_INT_8888_ARGB. 
 * 
 * Manipulation parameters to drawPixels are formed by bitwise ORring FLIP_HORIZONTAL 
 * or FLIP_VERTICAL with a degree value: ROTATE_90, ROTATE_180 or ROTATE_270. Currently 
 * only these fixed rotation values are supported. The image is rotated counter-clockwise. 
 * The result from a combined manipulation is that first the rotation will be done, 
 * then the vertical flip, and finally the horizontal flip. Since the manipulation 
 * parameters are passed in a single integer the MIDlet cannot dictate the order of 
 * flips or rotation. There is always at most a single rotation, a single horizontal 
 * flip and a single vertical flip done. For instance, if a manipulation argument is: 
 * <code>(FLIP_HORIZONTAL | FLIP_VERTICAL | ROTATE_90 )</code>, the image is rotated 
 * 90 degrees counter-clockwise and after that it is flipped vertically and then horizontally. 
 * In methods that have anchor point parameter the manipulation is done before any anchor 
 * point is considere and the anchor point is applied after the manipulation. 
 * 
 * Alpha channel
 * 
 * High-order bits in color values specify opacity. A value of 0 means fully transparent 
 * pixel, non-zero values are treated as non-transparent (largest possible value means 
 * fully opaque and values in between either fully opaque or semi-opaque). As an example, 
 * in int based color values 0x00RRGGBB specifies a fully transparent pixel and 0xFFRRGGBB 
 * specifies a fully opaque pixel. Implementations must treat any pixel with a non-zero top 
 * byte as being non-transparent. For on-screen graphics contexts the get operations will 
 * return transparency information as fully opaque. On rendering operations Porter-Duff 
 * Source Over Destination rule is used (T. Porter and T. Duff, 
 * "Compositing Digital Images", SIGGRAPH 84, 253-259). 
 * 
 * Note on alpha use with MIDP Graphics operations
 * 
 * Alpha value set with setARGBColor(int) is used in javax.microedition.lcdui.Graphics 
 * drawing methods that use the current color of Graphics. The alpha value is set to 
 * fully opaque when calls to following Graphics methods are made: 
 * <ul>
 * <li><code>setColor(int RGB)</code></li>
 * <li><code>setGrayScale(int value)</code></li> 
 * <li><code>setColor(int red, int green, int blue)</code></li>
 * </ul> 
 */
class DirectGraphicsImpl implements DirectGraphics {

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

	private Graphics g;
	//private java.awt.Graphics awtGraphics;

	DirectGraphicsImpl(Graphics g) {
		this.g = g;
		//this.awtGraphics = g._getAwtGraphics();
	}

	/**
	 * Sets the current color (and alpha) to the specified ARGB value (0xAARRGGBB). 
	 * All subsequent rendering operations will use this color (and alpha) 
	 * in associated Graphics. Note that since all rendering operations defined 
	 * in DirectGraphics define the color value as a parameter in the call, 
	 * using this method affects only rendering operations of Graphics class. 
	 * It should also be noted that the use of this method is reasonable only 
	 * if alpha blending is supported by the device in Graphics operations. 
	 * For setting a fully opaque drawing color applications should use methods 
	 * of Graphics class, like Graphics setColor(int RGB). 
	 * See also note about relation to Graphics set color methods. 
	 * High-order byte specifies opacity; that is, 0x00xxxxxx specifies a 
	 * fully transparent pixel and 0xffxxxxxx specifies a fully opaque pixel. 
	 * Implementations must treat any pixel with a nonzero top byte as being non- 
	 * transparent. I.e. If alpha-blending is not supported then non-zero top byte 
	 * means a fully opaque color. Most implementations treat non- zero top byte 
	 * as fully opaque, i.e. alpha blending is not implemented. 
	 * Note: Following two calls both results a fully transparent red color, i.e. nothing is drawn: 
	 * <ul> 
	 * <li><code>setARGBColor(0xFF0000);</code></li>
	 * <li><code>setARGBColor(0x00FF0000);</code></li>
	 * </ul>
		 * @param argbColor - the color being set  
	
	 *	@remark The alpha value is treated as boolean in ME4SE.
	 *	 */

	public void setARGBColor(int argbColor) {
	//	if((argbColor & 0x0ff000000) != 0x0ff000000){
	//		System.out.println("unspupp. transp.: "+ (((argbColor & 0x0ff000000) >> 24) &255));
	//	}
		g._argbColor = argbColor;
		g._getAwtGraphics().setColor(new Color(
				(argbColor >>> 16) & 255,
				(argbColor >>> 8) & 255,
				argbColor & 255,
				(argbColor >>> 24) & 255));
		
	}

	/**
	 * Draws an image to the graphics context. Does common image manipulations during the 
	 * drawing of an image. Manipulation can be 0 if no manipulation is done. Draws the 
	 * specified image by using the anchor point - the anchor point is applied after the 
	 * manipulation. Anchor values are defined in Graphics. The image can be drawn in 
	 * different positions relative to the anchor point by passing the appropriate 
	 * position constants. 
	 * @param img the image specified to be drawn
	 * @param x the x-coordinate of the anchor point
	 * @param y the y-coordinate of the anchor point
	 * @param anchor the anchor point for positioning the image
	 * @param manipulation flip or rotate value or a combination of values, 0 if none
	 * @throws java.lang.IllegalArgumentException if anchor is not a legal value or 
	 *                                            manipulation is not supported
	 * @throws java.lang.NullPointerException if img is null
	 */
	public void drawImage(Image img, int x, int y, int anchor, int manipulation) {

		//System.out.println("drawImage ("+img+", "+x+", "+y+", "+anchor+", "+manipulation);
		
		
		g.drawRegion(
			img,
			0,
			0,
			img.getWidth(),
			img.getHeight(),
			midpTransformation(manipulation),
			x,
			y,
			anchor);
		/*
				java.awt.Image paintImg = img._image;
		
				boolean flipH = ((manipulation & FLIP_HORIZONTAL) != 0);
				if (flipH)
					manipulation -= FLIP_HORIZONTAL;
				
				boolean flipV = ((manipulation & FLIP_VERTICAL) != 0);
				if (flipV)
					manipulation -= FLIP_VERTICAL;
		
				if (manipulation == 90) {
					FilteredImageSource fis = new FilteredImageSource(paintImg.getSource(), getRotateFilter(90f));
					java.awt.Image newImg = ApplicationManager.manager.awtContainer.createImage(fis);
					MediaTracker tracker = new MediaTracker(ApplicationManager.manager.awtContainer);
					tracker.addImage(newImg, 0);
					try {
						tracker.waitForID(0);
					} catch (Exception ex) {
					}
					paintImg = newImg;
				} else if (manipulation == 180) {
					FilteredImageSource fis = new FilteredImageSource(paintImg.getSource(), getRotateFilter(180f));
					java.awt.Image newImg = ApplicationManager.manager.awtContainer.createImage(fis);
					MediaTracker tracker = new MediaTracker(ApplicationManager.manager.awtContainer);
					tracker.addImage(newImg, 0);
					try {
						tracker.waitForID(0);
					} catch (Exception ex) {
					}
					paintImg = newImg;
				} else if (manipulation == 270) {
					FilteredImageSource fis = new FilteredImageSource(paintImg.getSource(), getRotateFilter(270f));
					java.awt.Image newImg = ApplicationManager.manager.awtContainer.createImage(fis);
					MediaTracker tracker = new MediaTracker(ApplicationManager.manager.awtContainer);
					tracker.addImage(newImg, 0);
					try {
						tracker.waitForID(0);
					} catch (Exception ex) {
					}
					paintImg = newImg;
				}
		
				if (flipV) {
					FilteredImageSource fis = new FilteredImageSource(paintImg.getSource(), getVFlipFilter(paintImg.getSource()));
					java.awt.Image newImg = ApplicationManager.manager.awtContainer.createImage(fis);
					MediaTracker tracker = new MediaTracker(ApplicationManager.manager.awtContainer);
					tracker.addImage(newImg, 0);
					try {
						tracker.waitForID(0);
					} catch (Exception ex) {
					}
					paintImg = newImg;
				}
		
				if (flipH) {
					FilteredImageSource fis = new FilteredImageSource(paintImg.getSource(), getHFlipFilter(paintImg.getSource()));
					java.awt.Image newImg = ApplicationManager.manager.awtContainer.createImage(fis);
					MediaTracker tracker = new MediaTracker(ApplicationManager.manager.awtContainer);
					tracker.addImage(newImg, 0);
					try {
						tracker.waitForID(0);
					} catch (Exception ex) {
					}
					paintImg = newImg;
				}
		
				awtGraphics.drawImage(paintImg, x, y, null);*/
	}

	/**
	 * Draws a closed triangle defined by coordinates. 
	 * Note that the method uses the color passed as a 
	 * parameter for drawing the triangle and not the current 
	 * active color of graphics context. The call does not 
	 * change the current active drawing color of the graphics context. 
	 *
	 * @param x1 the x-coordinate of the first vertex
	 * @param y1 the y-coordinate of the first vertex
	 * @param x2 the x-coordinate of the second vertex
	 * @param y2 the y-coordinate of the second vertex
	 * @param x3 the x-coordinate of the third vertex
	 * @param y3 the y-coordinate of the third vertex
	 * @param argbColor the ARGB color value used in painting the triangle
	 */
	public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
		drawPolygon(new int[] { x1, x2, x3 }, 0, new int[] { y1, y2, y3 }, 0, 3, argbColor);
	}

	/**
	 * Fills a closed triangle defined by coordinates. 
	 * Note that the method uses the color passed as a parameter 
	 * for drawing the triangle and not the current active color 
	 * of the graphics context. The call does not change the current 
	 * active drawing color of the graphics context. 
	 * @param x1 the x-coordinate of the first vertex
	 * @param y1 the y-coordinate of the first vertex
	 * @param x2 the x-coordinate of the second vertex
	 * @param y2 the y-coordinate of the second vertex
	 * @param x3 the x-coordinate of the third vertex
	 * @param y3 the y-coordinate of the third vertex
	 * @param argbColor the ARGB color value used in painting the triangle
	 */
	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) {
		fillPolygon(new int[] { x1, x2, x3 }, 0, new int[] { y1, y2, y3 }, 0, 3, argbColor);
	}

	/**
	 * Draws a closed polygon defined by the arrays of the x- and y-coordinates. Each pair 
	 * of (x, y) coordinates defines a point. This method draws the polygon defined by nPoint 
	 * line segments, where the first nPoint - 1 line segments are line segments 
	 * from (xPoints[xOffset + i - 1], yPoints[yOffset + i - 1]) to (xPoints[xOffset + i], 
	 * yPoints[yOffset + i]), for 1 <= i <= nPoints. 
	 * The figure is automatically closed by drawing a line connecting the final point 
	 * to the first point if those points are different. 
	 * @param xPoints an array of x-coordinates
	 * @param xOffset an offset to first x point in xPoints
	 * @param yPoints an array of y-coordinates
	 * @param yOffset an offset to first y point in yPoints
	 * @param nPoints the total number of points
	 * @param argbColor the ARGB color value used in painting the polygon 
	 * @throws java.lang.NullPointerException if xPoints or yPoints array is null. 
	 * @throws java.lang.ArrayIndexOutOfBoundsException if requested to access xPoints or yPoints beyond the length of the arrays or with a negative index
	 */
	public void drawPolygon(
		int[] xPoints,
		int xOffset,
		int[] yPoints,
		int yOffset,
		int nPoints,
		int argbColor) {

		int save = g._argbColor;
		setARGBColor(argbColor);

		for (int i = 0; i < nPoints; i++) {
			int j = (i + 1) % nPoints;
			g.drawLine(
				xPoints[xOffset + i],
				yPoints[yOffset + i],
				xPoints[xOffset + j],
				yPoints[yOffset + j]);
		}
		setARGBColor(save);
	}

	/**
	 * Fills a closed polygon defined by the arrays of the x- and y-coordinates. 
	 * This method draws the polygon defined by nPoint line segments, where the 
	 * first nPoint - 1 line segments are line segments from (xPoints[xOffset + i - 1], 
	 * yPoints[yOffset + i - 1]) to (xPoints[xOffset + i], yPoints[yOffset + i]), 
	 * for 1 <= i <= nPoints. The figure is automatically closed by drawing a 
	 * line connecting the final point to the first point if those points are different. 
		 * The area inside the polygon is defined using an even-odd fill rule, 
		 * which is also known as the alternating rule. 
	 *
	 * @param xPoints an array of x-coordinates
	 * @param xOffset an offset to first x point in xPoints
	 * @param yPoints an array of y-coordinates
	 * @param yOffset an offset to first y point in yPoints
	 * @param nPoints the total number of points
	 * @param argbColor the ARGB color value used in painting the polygon 
	 * @throws java.lang.NullPointerException if xPoints or yPoints array is null. 
	 * @throws java.lang.ArrayIndexOutOfBoundsException if requested to access xPoints or yPoints beyond the length of the arrays or with a negative index
	 */
	public void fillPolygon(
		int[] xPoints,
		int xOffset,
		int[] yPoints,
		int yOffset,
		int nPoints,
		int argbColor) {

		int[] x = new int[nPoints];
		int[] y = new int[nPoints];
		System.arraycopy(xPoints, xOffset, x, 0, nPoints);
		System.arraycopy(yPoints, yOffset, y, 0, nPoints);

        int save = g._argbColor;
        setARGBColor(argbColor);

        if(nPoints == 4 
                && x[0] == x[3] && x[1] == x[2]
                && y[0] == y[1] && y[2] == y[3]){
            g.fillRect(x[0], y[0], x[2]-x[0], y[2]-y[0]);
        }
        else{
            if(getAlphaComponent() != 255){
                System.out.println("FillPoly issue with alpha:"+getAlphaComponent());
            }
            g._getAwtGraphics().fillPolygon(x, y, nPoints);
            setARGBColor(save);
        }
	}

	/** 
	 * Copies or draws the pixel data directly to the graphics context to 
	 * a specific location. The pixels are passed in the format defined by
	 * the format parameter. If an implementation does not support the format, 
	 * an IllegalArgumentException is thrown. This method accepts only byte-based 
	 * formats. Passing all other formats will result in an IllegalArgumentException. 
	 * The operation is subject to the current clip region and translation for this Graphics 
	 * object. Bytes in pixels and transparencyMask arrays will be passed in the same 
	 * format. The transparencyMask can be null. If the transparency mask is null, the 
	 * image is considered fully opaque. 
	 * For pixel formats TYPE_BYTE_1_GRAY and TYPE_BYTE_1_GRAY_VERTICAL bit value 0 means 
	 * fully transparent pixel, 1 means fully opaque pixel. For other byte formats the 
	 * transparency information is as wide as color information for one pixel, for example, 
	 * in TYPE_BYTE_2_GRAY 2 bits are used for stroring transparency information in 
	 * transparencyMask. The semantics for mask values are same as in alpha channel use. 
	 * Zero value indicates fully transparent pixel, non-zero values are treated as non-transparent 
	 * (largest possible value means fully opaque and values in between either fully opaque or semi-opaque). 
	 * 
	 * Note that scanlength and offset parameters indicate the scanlength and offset in number
	 * of pixels. This is not necessarily the same as array indices since multiple pixels may be 
	 * stored in a byte. 
	 * @param pixels an array of pixel values
	 * @param transparencyMask an array defining the transparency mask
	 * @param offset the index of the first pixel and the mask value
	 * @param scanlength the relative array offset between the corresponding pixels and the mask value in consecutive rows
	 * @param x the horizontal rendering location in the graphics context
	 * @param y the vertical rendering location in the graphics context
	 * @param width the width of the region to be rendered
	 * @param height the height of the region to be rendered
	 * @param manipulation the manipulation done to the image before the draw, 0 means none.
	 * @param format the format which the pixels are provided in 
	 * @throws java.lang.NullPointerException if pixels is null 
	 * @throws java.lang.ArrayIndexOutOfBoundsException if requested to access beyond 
	 *                                                  the length of array or with negative index 
	 * @throws java.lang.IllegalArgumentException if manipulation is unknown, width or height is 
	 *                                            negative, or the format is wrong or unsupported
	 */
	public void drawPixels(
		byte[] pixels,
		byte[] transparencyMask,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height,
		int manipulation,
		int format) {
		
	//	System.out.println("drawPixels ()");

		int[] dst = new int[width * height];
		int dstOfs = 0;
		for (int i = 0; i < height; i++) {
			convertTo8888(pixels, transparencyMask, format, offset, dst, dstOfs, width);
			offset += scanlength;
			dstOfs += width;
		}

		drawPixels(dst, true, 0, width, x, y, width, height, manipulation, TYPE_INT_8888_ARGB);
		/*			
				System.out.println("DirectGraphics.drawPixels(byte[]) called with no effect!");
				System.out.println("	pixels.length 			= " + pixels.length);
				System.out.println("	transparanceMask.length = " + transparencyMask.length);
				System.out.println("	offset 				    = " + offset);
				System.out.println("	scanlength				= " + scanlength);
				System.out.println("	x,y 				    = " + x + "," + y);
				System.out.println("	width, height           = " + width + "," + height);
				System.out.println("	manipulation, format    = " + manipulation + ", " + format);*/
	}

	/**
	 * Copies or draws the pixel data directly to the graphics context 
	 * to a specific location from the array starting from the specified offset. 
	 * The pixels are passed in the format defined by the format parameter. 
	 * If an implementation does not support the format, an IllegalArgumentException is thrown. 
	 * This method accepts only int-based formats. Passing all other formats will result 
	 * in an IllegalArgumentException. Note that it is possible that no int-based pixel 
	 * format is supported by an implementation. 
	 * The operation is subject to the current clip region and translation for this Graphics object. 
	 * The boolean value transparency will indicate whether the pixel's transparency value will be 
	 * checked. If the transparency argument is false, the pixels are set to the graphics context 
	 * without comparing the transparency values. If the transparency argument is true, the pixel's 
	 * transparency value is checked and it will affect the drawing of a pixel. 
	 * The drawPixels paints the pixel data in the graphics context in the following fashion: 
	 * P(x1, y1) = pixels[offset + (x1 - x) + (y1 - y) * scanlength], for each P(x1, y1), 
	 * where (x <= x1 < x + width) and (y <= y1 < y + height).
	 *
	 * @param pixels an array of pixel values (0xAARRGGBB)
	 * @param transparency true if the pixel's transparency value will be checked
	 * @param offset the index of the first pixel value
	 * @param scanlength the relative array offset between the corresponding pixels in consecutive rows
	 * @param x the horizontal rendering location in the graphics context
	 * @param y the vertical rendering location in the graphics context
	 * @param width the width of the region to be rendered
	 * @param height the height of the region to be rendered
	 * @param manipulation the manipulation done to the image before the draw, 0 means none.
	 * @param format the format which the pixels are provided in 
	 * @throws java.lang.NullPointerException if pixels is null
	 * @throws java.lang.ArrayIndexOutOfBoundsException if requested to access pixels
	 *                                                  beyond the length of array or with negative index 
	 * @throws java.lang.IllegalArgumentException if manipulation is unknown, width or height is negative, or the format is wrong or unsupported
	*/
	public void drawPixels(
		int[] pixels,
		boolean transparency,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height,
		int manipulation,
		int format) {

		//System.out.println("drawPixels ("+pixels+", "+transparency+", "+offset+", "+scanlength+", "+x+", "+y+", "+width+", "+height+ ", "+manipulation+", "+format);
		
		if(manipulation != 0){
			System.err.println("DirectGraphics.drawPixels() Unsupp. manip.: "+manipulation);
		}
		
		if (format == TYPE_INT_888_RGB)
			transparency = false;
		else if (format != TYPE_INT_8888_ARGB)
			System.err.println("DirectGraphics.drawPixels() Unsupp. format " + format);

		g.drawRGB(pixels, offset, scanlength, x, y, width, height, transparency);
	}

	/**
	 * Copies or draws the pixel data directly to the graphics context to specific 
	 * a location. The pixels are passed in the format defined by the format parameter. 
	 * If an implementation does not support the format, an IllegalArgumentException is thrown. 
	 * This method accepts only short-based formats. Passing all other formats will result 
	 * in an IllegalArgumentException. Note that it is possible that no short-based pixel format 
	 * is supported by an implementation. The operation is subject to the current clip region and 
	 * translation for this Graphics object. The boolean value transparency will indicate whether 
	 * the pixel's transparency value will be checked. If the transparency argument is false,
	 * the pixels are set to the graphics context without comparing the transparency values. 
	 * If the transparency argument is true, the pixel's transparency value is checked and it 
	 * will affect the drawing of a pixel. 
	 * The drawPixels paints the pixel data in the graphics context in the following fashion: 
	 * P(x1, y1) = pixels[offset + (x1 - x) + (y1 - y) * scanlength],
	 * for each P(x1, y1), where (x <= x1 < x + width) and (y <= y1 < y + height).
	 *
	 *
	 * @param pixels an array of pixel values
	 * @param transparency true if the pixel's transparency value will be checked
	 * @param offset the index of the first pixel value
	 * @param scanlength the relative array offset between the corresponding pixels in consecutive rows
	 * @param x the horizontal rendering location in the graphics context
	 * @param y the vertical rendering location in the graphics context
	 * @param width the width of the region to be rendered
	 * @param height the height of the region to be rendered
	 * @param manipulation the manipulation done to the image before the draw, 0 means none.
	 * @param format the format which the pixels are provided in 
	 * @throws java.lang.NullPointerException if pixels is null 
	 * @throws java.lang.ArrayIndexOutOfBoundsException if requested to access beyond the length 
	 *                                                  of array or with negative index 
	 * @throws java.lang.IllegalArgumentException if manipulation is unknown, width or height is 
	 *                                            negative, or the format is wrong or unsupported
	*/
	public void drawPixels(
		short[] pixels,
		boolean transparency,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height,
		int manipulation,
		int format) {

	/*	System.out.println("DirectGraphics.drawPixels(short[]) called ");
		System.out.println("	pixels.length 		 = " + pixels.length);
		System.out.println("	transparancy         = " + transparency);
		System.out.println("	offset 				 = " + offset);
		System.out.println("	scanlength			 = " + scanlength);
		System.out.println("	x,y 				 = " + x + "," + y);
		System.out.println("	width, height        = " + width + "," + height);
		System.out.println("	manipulation, format = " + manipulation + ", " + format + "\n\n");*/


		int[] dst = new int[width * height];
		int dstOfs = 0;
		for (int i = 0; i < height; i++) {
			convertTo8888(pixels, format, offset, dst, dstOfs, width);
			offset += scanlength;
			dstOfs += width;
		}

        g.drawRGB(dst, 0, width, x, y, width, height, transparency);

	}

	/**
	 * Copies the pixel (including any transparency mask) values of the graphics context 
	 * from a specific location to an array of byte values. The pixels will be passed in 
	 * the format defined by format parameter. If an implementation doesn't support the format 
	 * an IllegalArgumentException is thrown. 
	 * This method returns only byte-based formats. Requesting all other formats will result in 
	 * an IllegalArgumentException.
	 * Throws ArrayIndexOutOfBoundsException if array size is too small for image pixels 
	 * or transparency mask. The argument transparencyMask can be null if the caller is not 
	 * interested in getting the mask. See transparencyMask definition on drawPixels(...). 
	 * Note that the scanlength and offset parameters indicate the scanlength and offset in 
	 * number of pixels. This is not necessarily the same as array indices since multiple 
	 * pixels may be stored in a byte. 
	 * The current clip region does not affect pixel values stored in the pixels array, 
	 * i.e. even if a clip region is set and the region intersects the region queried with 
	 * this method, also the clip region pixels are stored in the pixels array. The operation 
	 * is subject to the current translation for this Graphics object. 
	 * 
	 * @param pixels an array which the pixel information will be stored in 
	 * @param transparencyMask an array which the transparency mask will be stored in
	 * @param offset offset in the array where the first pixel and mask value will be stored
	 * @param scanlength the relative offset in the array between the corresponding pixels and 
	 *                   the mask value in consecutive rows
	 * @param x the x-coordinate of the upper left corner of the region in the graphics context
	 * @param y the y-coordinate of the upper left corner of the region in the graphics context
	 * @param width the width of the region in the graphics context
	 * @param height the height of the region in the graphics context
	 * @param format the format which the pixels are requested in 
	 * @throws java.lang.NullPointerException if pixels is null 
	 * @throws java.lang.ArrayIndexOutOfBoundsException if array size is too small for the image pixels 
	 *                                                  or negative index access is attempted, contents 
	 *                                                  of the array remain unchanged 
	 * @throws java.lang.IllegalArgumentException if x, y, width or height is negative, or the format is 
	 *                                            wrong or unsupported
	 */
	public void getPixels(
		byte[] pixels,
		byte[] transparencyMask,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height,
		int format) {

        System.err.println("gerPixels(byte...) not supported!");
	}

	/**
	 * Copies the pixel values of the graphics context from a specific location to an array of 
	 * int values. The pixels will be passed in the format defined by format parameter. If an 
	 * implementation does not support the format, an IllegalArgumentException is thrown. Note 
	 * that it is possible that only the native format is supported via the appropriate version 
	 * of getPixels method. This method returns only int-based formats. Requesting all other 
	 * formats will result in an IllegalArgumentException. 
	 * Throws ArrayIndexOutOfBoundsException if array size is too small for image pixels. 
	 * The current clip region does not affect pixel values stored in the pixels array, 
	 * i.e. even if a clip region is set and the region intersects the region queried with 
	 * this method, also the clip region pixels are stored in the pixels array. The operation 
	 * is subject to the current translation for this Graphics object. 
	 * The getPixels methods stores the pixel data to the pixels array in the following fashion: 
	 * pixels[offset + (x1 - x) + (y1 - y) * scanlength] = P(x1, y1),
	 * for each P(x1, y1), where (x <= x1 < x + width) and (y <= y1 < y + height).
	 *
	 * @param pixels an array which the pixel information will be stored in
	 * @param offset the index to the pixels array where the first pixel value will be stored
	 * @param scanlength the relative offset in the pixels array between corresponding pixels in
	 *                   consecutive rows
	 * @param x the x-coordinate of the upper left corner of the region in the graphics context
	 * @param y the y-coordinate of the upper left corner of the region in the graphics context
	 * @param width the width of the region in graphics context
	 * @param height the height of the region in graphics context
	 * @param format the format which the pixels are requested in 
	 * @throws java.lang.NullPointerException if pixels is null 
	 * @throws java.lang.ArrayIndexOutOfBoundsException if array size is too small for the image 
	 *                                                  pixels or negative index access is attempted, 
	 *                                                  contents of the array remain unchanged 
	 * @throws java.lang.IllegalArgumentException if x, y, width or height is negative, or the format 
	 *                                            is wrong or unsupported
	 * 
	 */
	public void getPixels(
		int[] pixels,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height,
		int format) {

		int dxy[] = new int[2];

		java.awt.Image awtImage = g._getAwtImage(dxy);

		PixelGrabber pg =
			new PixelGrabber(
				awtImage,
				x + dxy[0],
				y + dxy[1],
				width,
				height,
				pixels,
				offset,
				scanlength);

		boolean ok = false;
		do {
			try {
				pg.grabPixels(0);
				ok = true;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (!ok);
		// TODO convert device colors back to RGB values(!)
	}

	/**
	 * Copies the pixel values of the graphics context from a specific location to an array 
	 * of short values. The pixels will be passed in the format defined by format parameter. 
	 * If an implementation does not support the format, an IllegalArgumentException is thrown. 
	 * Note that it is possible that only the native format is supported via the appropriate version 
	 * of getPixels method. This method returns only short-based formats. Requesting all other 
	 * formats will result in an IllegalArgumentException. 
	 * Throws ArrayIndexOutOfBoundsException if array size is too small for image pixels. 
	 * The current clip region does not affect pixel values stored in the pixels array, i.e. 
	 * even if a clip region is set and the region intersects the region queried with this method, 
	 * also the clip region pixels are stored in the pixels array. The operation is subject to 
	 * the current translation for this Graphics object. The getPixels methods stores the pixel 
	 * data to the pixels array in the following fashion: 
	 * pixels[offset + (x1 - x) + (y1 - y) * scanlength] = P(x1, y1),
	 * for each P(x1, y1), where (x <= x1 < x + width) and (y <= y1 < y + height).
	 * @param pixels an array which the pixel information will be stored in
	 * @param offset the index in the array where the first pixel value will be stored
	 * @param scanlength the relative offset in the array between the corresponding pixels in consecutive rows
	 * @param x the x-coordinate of the upper left corner of the region in the graphics context
	 * @param y the y-coordinate of the upper left corner of the region in the graphics context
	 * @param width the width of the region in the graphics context
	 * @param height the height of the region in the graphics context
	 * @param format the format which the pixels are requested in 
	 * @throws java.lang.NullPointerException if pixels is null 
	 * @throws java.lang.ArrayIndexOutOfBoundsException if array size is too small for the image 
	 *                                                  pixels or negative index access is attempted, 
	 *                                                  contents of the array remain unchanged 
	 * @throws java.lang.IllegalArgumentException if x, y, width or height is negative, or the format 
	 *                                            is wrong or unsupported
	 */

	public void getPixels(
		short[] pixels,
		int offset,
		int scanlength,
		int x,
		int y,
		int width,
		int height,
		int format) {
        
        int[] ipixels = new int[width*height];
        
        getPixels(ipixels, 0, width, x, y, width, height, TYPE_INT_888_RGB);

        int srcOfs = 0;
        
        for(int i = 0; i < height; i++){
            convertTo4444(ipixels, TYPE_INT_888_RGB, srcOfs, pixels, offset, width);
            srcOfs += width;
            offset += scanlength;
        }
	}

	/**
	 * Returns the native pixel format of an implementation. The method returns the pixel format, for example, TYPE_BYTE_1_GRAY or TYPE_USHORT_4444_ARGB. The native format is the most efficient format supported by the drawPixels and getPixels methods of specific implementation. An implementation must support the format that it returns from this method. Implementation may support also other pixel formats. 
	 * @return the native format of specific implementation.
	 */
	public int getNativePixelFormat() {
		return TYPE_INT_8888_ARGB;
	}

	/**
	 * Gets the alpha component of the current color. 
	 * @return integer value in range 0-255
	 */
	public int getAlphaComponent() {
		return (g._argbColor >> 24) & 255;
	}



	/** Converts a NOKIA manipulation constant to a MIDP-NG manipulation constant. */

	private int midpTransformation(int manipulation) {

		switch (manipulation) {
			case 0 :
				return TRANS_NONE;
				
			case ROTATE_90 :
				return TRANS_ROT90;
				
			case ROTATE_180 :
				return TRANS_ROT180;
				
			case ROTATE_270 :
				return TRANS_ROT270;

			case FLIP_HORIZONTAL: 
				return TRANS_MIRROR;
				
			default :
				System.err.println(
					"Nokia (mirror) manipulation constant " + manipulation + " not yet supported");
				return midpTransformation(manipulation & 511);
		}
	}

	static final int[] val1 = { 0, 255 };
	static final int[] val2 = fill(4);
	static final int[] val3 = fill(8);
	static final int[] val4 = fill(16);
//	static final int[] val5 = fill(32);
//	static final int[] val6 = fill(64);

	static int[] fill(int steps) {
		int[] result = new int[steps];

		int add = 255 / (steps - 1);
		int val = 0;
		for (int i = 0; i < steps; i++) {
			result[i] = val;
			val += add;
		}
		result[steps - 1] = 255;
		return result;
	}

	// IMPROVE: add new formats as needed

	private void convertTo8888(
		short[] src,
		int format,
		int srcOffset,
		int[] dst,
		int dstOffset,
		int count) {
		int r;
		int g;
		int b;
		int a = 255;

		for (int i = 0; i < count; i++) {
			int bi = src[srcOffset++];
			switch (format) {
				case TYPE_USHORT_4444_ARGB :
					a = val4[(bi >> 12) & 15]; // fall-through
				case TYPE_USHORT_444_RGB :
					r = val4[(bi >> 8) & 15];
					g = val4[(bi >> 4) & 15];
					b = val4[bi & 15];
					break;
				default :
                    System.err.println("convert8888 Unsupp. src format: "+format);
                    return;
			}
			dst[dstOffset++] = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
		}
	}

    
    private void convertTo4444(
            int[] src,
            int srcFormat,
            int srcOffset,
            short[] dst,
            int dstOffset,
            int count) {
            int r;
            int g;
            int b;
            int a = 15;

            for (int i = 0; i < count; i++) {
                int bi = src[srcOffset++];
                switch (srcFormat) {
                    case TYPE_INT_8888_ARGB :
                        a = (bi >> 28) & 15; // fall-through
                    case TYPE_INT_888_RGB :
                        r = (bi >> 20) & 15;
                        g = (bi >> 12) & 15;
                        b = (bi >> 4) & 15;
                        break;
                    default :
                        System.err.println("conv4444 Unsupported format: "+srcFormat);
                       return;
                }
                dst[dstOffset++] = (short) ((a << 12) | (r << 8) | (g << 4) | b);
            }
        }

    
    
    
    
	// IMPROVE: add new formats as needed

	private void convertTo8888(
		byte[] src,
		byte[] transparency,
		int format,
		int srcOffset,
		int[] dst,
		int dstOffset,
		int count) {

		int r;
		int g;
		int b;
		int a = 255;

		for (int i = 0; i < count; i++) {
			a = transparency == null ? 255 : transparency[srcOffset];
			int bi = src[srcOffset++];
			switch (format) {
				case TYPE_BYTE_332_RGB :
					r = val3[(bi >> 5) & 7];
					g = val3[(bi >> 2) & 2];
					b = val2[bi & 3];
					break;
				case TYPE_BYTE_8_GRAY :
					r = bi;
					g = bi;
					b = bi;
					break;
				default :
					throw new RuntimeException("Unsupported format: " + format);
			}
			dst[dstOffset++] = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
		}
	}
}

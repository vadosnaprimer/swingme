package com.nokia.mid.ui;

import javax.microedition.lcdui.Image;

/**
 * @API NOKIAUI
 */
public interface DirectGraphics {

	/**
	 * @API NOKIAUI
	 */
	public static final int FLIP_HORIZONTAL = 8192;

	/**
	 * @API NOKIAUI
	 */
	public static final int FLIP_VERTICAL = 16384;

	/**
	 * @API NOKIAUI
	 */
	public static final int ROTATE_90 = 90;

	/**
	 * @API NOKIAUI
	 */
	public static final int ROTATE_180 = 180;

	/**
	 * @API NOKIAUI
	 */
	public static final int ROTATE_270 = 270;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_BYTE_1_GRAY = 1;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_BYTE_1_GRAY_VERTICAL = -1;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_BYTE_2_GRAY = 2;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_BYTE_4_GRAY = 4;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_BYTE_8_GRAY = 8;

	/**
	 * @API NOKIAUI
	 */
	public static final int TYPE_BYTE_332_RGB = 332;

	/**
	 * @API NOKIAUI
	 */
	public static final int TYPE_USHORT_4444_ARGB = 4444;

	/**
	 * @API NOKIAUI
	 */
	public static final int TYPE_USHORT_444_RGB = 444;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_USHORT_555_RGB = 555;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_USHORT_1555_ARGB = 1555;

	/**
	 * @API NOKIAUI
	 * @remark Not supported in ME4SE
	 */
	public static final int TYPE_USHORT_565_RGB = 565;

	/**
	 * @API NOKIAUI
	 */
	public static final int TYPE_INT_888_RGB = 888;

	/**
	 * @API NOKIAUI
	 */
	public static final int TYPE_INT_8888_ARGB = 8888;

	/**
	 * @API NOKIAUI
	 * @remark The alpha value is treated as boolean in ME4SE
	 */
	public abstract void setARGBColor(int argbColor);

	/**
	 * @API NOKIAUI
	 */
	public abstract void drawImage(Image img, int x, int y, int anchor, int manipulation);

	/**
	 * @API NOKIAUI
	 */
	public abstract void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor) ;

	/**
	 * @API NOKIAUI
	 */
	public abstract void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor);

	/**
	 * @API NOKIAUI
	 */
	public void drawPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor); 
  
	/**
	 * @API NOKIAUI
	 */
	public void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor); 
 
	/**
	 * @API NOKIAUI
	 */
	public abstract void drawPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format); 
 
	/**
	 * @API NOKIAUI
	 */
	public abstract void drawPixels(int[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format); 
  
	/**
	 * @API NOKIAUI
	 */
	public abstract void drawPixels(short[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format); 

	/**
	 * @API NOKIAUI
	 * @ME4SE UNIMPLEMENTED
	 */
	public abstract void getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int width, int height, int format); 
			    
	/**
	 * @API NOKIAUI
	 * @ME4SE UNIMPLEMENTED
	 * @remark May be possible to implement this based on g.image if needed.
	 */
	public void getPixels(int[] pixels, int offset, int scanlength, int x, int y, int width, int height, int format); 
			    
	/**
	 * @API NOKIAUI
	 * @ME4SE UNIMPLEMENTED
	 */
	public abstract void getPixels(short[] pixels, int offset, int scanlength, int x, int y, int width, int height, int format);

	/**
	 * @API NOKIAUI
	 */
	public abstract int getNativePixelFormat();

	/**
	 * @API NOKIAUI
	 */
	public abstract int getAlphaComponent();
}

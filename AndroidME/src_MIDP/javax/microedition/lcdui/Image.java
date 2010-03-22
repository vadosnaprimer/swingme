package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;


public class Image
{

	public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
		// TODO AndroidDisplayGraphics.drawRegion code is similar
		if (image == null)
			throw new NullPointerException();
		if (x + width > image.getWidth() || y + height > image.getHeight() || width <= 0 || height <= 0 || x < 0
				|| y < 0)
			throw new IllegalArgumentException("Area out of Image");

		int[] rgbData = new int[height * width];
		int[] rgbTransformedData = new int[height * width];
		image.getRGB(rgbData, 0, width, x, y, width, height);

		int colIncr, rowIncr, offset;

		switch (transform) {
		case Sprite.TRANS_NONE: {
			offset = 0;
			colIncr = 1;
			rowIncr = 0;
			break;
		}
		case Sprite.TRANS_ROT90: {
			offset = (height - 1) * width;
			colIncr = -width;
			rowIncr = (height * width) + 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_ROT180: {
			offset = (height * width) - 1;
			colIncr = -1;
			rowIncr = 0;
			break;
		}
		case Sprite.TRANS_ROT270: {
			offset = width - 1;
			colIncr = width;
			rowIncr = -(height * width) - 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_MIRROR: {
			offset = width - 1;
			colIncr = -1;
			rowIncr = width << 1;
			break;
		}
		case Sprite.TRANS_MIRROR_ROT90: {
			offset = (height * width) - 1;
			colIncr = -width;
			rowIncr = (height * width) - 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_MIRROR_ROT180: {
			offset = (height - 1) * width;
			colIncr = 1;
			rowIncr = -(width << 1);
			break;
		}
		case Sprite.TRANS_MIRROR_ROT270: {
			offset = 0;
			colIncr = width;
			rowIncr = -(height * width) + 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		default:
			throw new IllegalArgumentException("Bad transform");
		}

		// now the loops!
		for (int row = 0, i = 0; row < height; row++, offset += rowIncr) {
			for (int col = 0; col < width; col++, offset += colIncr, i++) {
/*			    int a = (rgbData[offset] & 0xFF000000);
			    int b = (rgbData[offset] & 0x00FF0000) >>> 16;
			    int g = (rgbData[offset] & 0x0000FF00) >>> 8;
			    int r = (rgbData[offset] & 0x000000FF);

			    rgbTransformedData[i] = a | (r << 16) | (g << 8) | b;*/
				rgbTransformedData[i] = rgbData[offset];
			}
		}
		// to aid gc
		rgbData = null;
		image = null;

		return createRGBImage(rgbTransformedData, width, height, true);
	}






    public static Image createImage( InputStream stream )
	    throws IOException
	{
	    Bitmap bitmap = BitmapFactory.decodeStream( stream );
	    return new Image(bitmap);
	}

	public static Image createImage( int width, int height )
	{
	    Bitmap bitmap = Bitmap.createBitmap( width, height, Config.ARGB_8888 );
	    try {
	    	return new Image(bitmap);
		} catch (Exception e) {
			return null;
		}
	}

	public static Image createImage( String resource )
	    throws IOException
	{
	    return createImage( Image.class.getResourceAsStream( resource ) );
	}

	public static Image createImage( byte[] imageData, int imageOffset, int imageLength )
	{
	    Bitmap bitmap = BitmapFactory.decodeByteArray( imageData, imageOffset, imageLength );
	    try {
	    	return new Image(bitmap);
		} catch (Exception e) {
			return null;
		}
	}

	public static final Image createRGBImage( int[] rgb, int width, int height, boolean processAlpha )
	{
		Bitmap.Config config;
		if( processAlpha )
		{
			config = Bitmap.Config.ARGB_4444;
		}
		else
		{
			config = Bitmap.Config.RGB_565;
		}
	    Bitmap bitmap = Bitmap.createBitmap( rgb, width, height, config );
	    try {
	    	return new Image(bitmap);
		} catch (Exception e) {
			return null;
		}
	}

	private Bitmap bitmap;

	private Image(Bitmap bitmap) throws IOException
	{
		if (bitmap == null) throw new IOException();
	    this.bitmap = bitmap;
	}

	public Bitmap getBitmap()
	{
	    return this.bitmap;
	}

	public int getWidth()
	{
	    return this.bitmap.getWidth();
	}

	public int getHeight()
	{
	    return this.bitmap.getHeight();
	}

	public Graphics getGraphics()
	{
		if (!bitmap.isMutable()) {
			throw new IllegalStateException("Image is immutable");
		}

		Canvas canvas = new Canvas(bitmap);
        canvas.clipRect(0, 0, getWidth(), getHeight());
        Graphics displayGraphics = new Graphics(canvas);
		displayGraphics.setColor(0x00000000);
		displayGraphics.translate(-displayGraphics.getTranslateX(), -displayGraphics.getTranslateY());

		return displayGraphics;
	}

	public void getRGB( int[] rgb, int offset, int scanlength, int x, int y, int width, int height )
	{
	    this.bitmap.getPixels( rgb, offset, scanlength, x, y, width, height );
	}

	public boolean isMutable() {
		return bitmap.isMutable();
	}
}

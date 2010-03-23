package javax.microedition.lcdui;


import javax.microedition.lcdui.game.Sprite;

import net.yura.android.lcdui.FontManager;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;

public class Graphics {
	public static final int BASELINE = 0x01;
	public static final int BOTTOM = 0x02;
	public static final int LEFT = 0x04;
	public static final int RIGHT = 0x08;
	public static final int TOP = 0x10;
	public static final int VCENTER = 0x20;
	public static final int HCENTER = 0x40;

	public static final int DOTTED = 0x01;
	public static final int SOLID = 0x02;

	private android.graphics.Canvas canvas;
	private javax.microedition.lcdui.Font font;
	private Paint paint = new Paint();

	private int tx, ty;
	private int stroke;

	public Graphics(android.graphics.Canvas canvas) {
		setFont(Font.getDefaultFont());
		setCanvas(canvas);
	}

	public android.graphics.Canvas getCanvas() {
		return this.canvas;
	}

	public void setCanvas(android.graphics.Canvas canvas) {
		this.canvas = canvas;
		if (canvas != null) {
            canvas.save();
        }
	}

	public int getClipX() {
		return this.canvas.getClipBounds().left;
	}

	public int getClipY() {
		return this.canvas.getClipBounds().top;
	}

	public int getClipWidth() {
		return this.canvas.getClipBounds().width();
	}

	public int getClipHeight() {
		return this.canvas.getClipBounds().height();
	}

	public int getColor() {
		return this.paint.getColor() & 0x00FFFFFF;
	}

	public void setColor(int color) {
		this.paint.setColor(0xFF000000 | color);
	}

	public void fillRect(int x, int y, int width, int height) {
		this.canvas.drawRect(x, y, x + width, y + height, this.paint);
	}

	public void fillRoundRect(int x, int y, int width, int height, int rx,
			int ry) {
		this.canvas.drawRoundRect(new RectF(x, y, x + width, y + height), rx,
				ry, this.paint);
	}

	public void drawImage(javax.microedition.lcdui.Image image, int x, int y,
			int anchor) {
		int ax;
		int ay;
		if ((anchor & LEFT) != 0) {
			ax = x;
		} else if ((anchor & HCENTER) != 0) {
			ax = x - image.getWidth() / 2;
		} else {
			ax = x - image.getWidth();
		}
		if ((anchor & TOP) != 0) {
			ay = y;
		} else if ((anchor & VCENTER) != 0) {
			ay = y - image.getHeight() / 2;
		} else {
			ay = y - image.getHeight();
		}
		this.canvas.drawBitmap(image.getBitmap(), ax, ay, null);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			x1++;
		} else {
			x2++;
		}
		if (y1 > y2) {
			y1++;
		} else {
			y2++;
		}
		this.canvas.drawLine(x1, y1, x2, y2, this.paint);
	}

	public void drawRect(int x, int y, int width, int height) {
		Paint outlinePaint = new Paint(this.paint);
		outlinePaint.setStyle(Style.STROKE);
		this.canvas.drawRect(x, y, x + width, y + height, outlinePaint);
	}

	public void drawRoundRect(int x, int y, int width, int height, int rx,
			int ry) {
		Paint outlinePaint = new Paint(this.paint);
		outlinePaint.setStyle(Style.STROKE);
		this.canvas.drawRoundRect(new RectF(x, y, x + width, y + height), rx,
				ry, outlinePaint);
	}

	public javax.microedition.lcdui.Font getFont() {
		return this.font;
	}

	public void setFont(javax.microedition.lcdui.Font font) {
		this.font = font;
	}

	public void drawString(String str, int x, int y, int anchor) {
		int newx = x;
		int newy = y;

		if (anchor == 0) {
			anchor = javax.microedition.lcdui.Graphics.TOP
					| javax.microedition.lcdui.Graphics.LEFT;
		}

		FontManager androidFont = FontManager.getFont(font);
		Paint paint = androidFont.getPaint();

		if ((anchor & javax.microedition.lcdui.Graphics.TOP) != 0) {
			newy -= paint.getFontMetricsInt().ascent;
		} else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
			newy -= paint.getFontMetricsInt().descent;
		}
		if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
			newx -= paint.measureText(str) / 2;
		} else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
			newx -= paint.measureText(str);
		}

		paint.setColor(paint.getColor());
		canvas.drawText(str, newx, newy, paint);
	}

	public void clipRect(int x, int y, int w, int h) {
		this.canvas.clipRect(x, y, x + w, y + h);
	}

	public void setClip(int x, int y, int w, int h) {
		this.canvas.restore();
		this.canvas.save();
		this.canvas.translate(this.tx, this.ty);
		this.canvas.clipRect(x, y, x + w, y + h);

	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {

		paint.setStyle(Paint.Style.FILL);
	    RectF rect = new RectF(x, y, x + width, y + height);
	    canvas.drawArc(rect, startAngle, arcAngle, false, paint);

//JP		// TODO : do something to the paint to make it fill!!
//		this.canvas.drawArc(new RectF(x, y, x + width, y + height), startAngle,
//				arcAngle, true, this.paint);
	}

	public void translate(int x, int y) {
		this.tx += x;
		this.ty += y;
		this.canvas.translate(x, y);
	}

	public int getTranslateX() {
		return this.tx;
	}

	public int getTranslateY() {
		return this.ty;
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		paint.setStyle(Paint.Style.STROKE);
		RectF rect = new RectF(x, y, x + width, y + height);
		canvas.drawArc(rect, startAngle, arcAngle, false, paint);
	}

	public void drawChar(char character, int x, int y, int anchor) {
		char[] carr = new char[1];
		carr[0] = character;
		drawString(new String(carr), x, y, anchor);
	}

	public void drawRegion(Image src, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
        // may throw NullPointerException, this is ok
        if (x_src + width > src.getWidth() || y_src + height > src.getHeight() || width < 0 || height < 0 || x_src < 0
                || y_src < 0)
//JP            throw new IllegalArgumentException("Area out of Image");
        	return;

        // this cannot be done on the same image we are drawing
        // check this if the implementation of getGraphics change so
        // as to return different Graphic Objects on each call to
        // getGraphics
        if (src.isMutable() && src.getGraphics() == this)
            throw new IllegalArgumentException("Image is source and target");

        Bitmap img = src.getBitmap();

        Matrix matrix = new Matrix();
        int dW = width, dH = height;
        switch (transform) {
        case Sprite.TRANS_NONE: {
            break;
        }
        case Sprite.TRANS_ROT90: {
        	matrix.preRotate(90);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, matrix, true);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_ROT180: {
            matrix.preRotate(180);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, matrix, true);
            break;
        }
        case Sprite.TRANS_ROT270: {
            matrix.preRotate(270);
            img = Bitmap.createBitmap(img, x_src, y_src, width, height, matrix, true);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR: {
        	// TODO
            break;
        }
        case Sprite.TRANS_MIRROR_ROT90: {
        	// TODO
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR_ROT180: {
        	// TODO
            break;
        }
        case Sprite.TRANS_MIRROR_ROT270: {
        	// TODO
            dW = height;
            dH = width;
            break;
        }
        default:
            throw new IllegalArgumentException("Bad transform");
        }

        // process anchor and correct x and y _dest
        // vertical
        boolean badAnchor = false;

        if (anchor == 0) {
            anchor = TOP | LEFT;
        }

        if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
            badAnchor = true;

        if ((anchor & TOP) != 0) {
            if ((anchor & (VCENTER | BOTTOM)) != 0)
                badAnchor = true;
        } else if ((anchor & BOTTOM) != 0) {
            if ((anchor & VCENTER) != 0)
                badAnchor = true;
            else {
                y_dst -= dH - 1;
            }
        } else if ((anchor & VCENTER) != 0) {
            y_dst -= (dH - 1) >>> 1;
        } else {
            // no vertical anchor
            badAnchor = true;
        }

        // horizontal
        if ((anchor & LEFT) != 0) {
            if ((anchor & (HCENTER | RIGHT)) != 0)
                badAnchor = true;
        } else if ((anchor & RIGHT) != 0) {
            if ((anchor & HCENTER) != 0)
                badAnchor = true;
            else {
                x_dst -= dW - 1;
            }
        } else if ((anchor & HCENTER) != 0) {
            x_dst -= (dW - 1) >>> 1;
        } else {
            // no horizontal anchor
            badAnchor = true;
        }

        if (badAnchor) {
            throw new IllegalArgumentException("Bad Anchor");
        }

        Rect srcRect = new Rect(x_src, y_src, x_src + width, y_src + height);
        Rect dstRect = new Rect(x_dst, y_dst, x_dst + width, y_dst + height);
        canvas.drawBitmap(img, srcRect, dstRect, paint);
	}

	public void setStrokeStyle(int stroke) {
		this.stroke = stroke;
	}

	public int getStrokeStyle() {
		return stroke;
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		paint.setStyle(Paint.Style.FILL);
		Path path = new Path();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.lineTo(x1, y1);
		canvas.drawPath(path, paint);
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x,
			int y, int width, int height, boolean processAlpha) {
		if (rgbData == null)
			throw new NullPointerException();

		if (width == 0 || height == 0) {
			return;
		}

		int l = rgbData.length;
		if (width < 0
				|| height < 0
				|| offset < 0
				|| offset >= l
				|| (scanlength < 0 && scanlength * (height - 1) < 0)
				|| (scanlength >= 0 && scanlength * (height - 1) + width - 1 >= l)) {
			throw new ArrayIndexOutOfBoundsException();
		}

		// FIXME MIDP allows almost any value of scanlength, drawBitmap is more
		// strict with the stride
		if (scanlength == 0) {
			scanlength = width;
		}
		canvas.drawBitmap(rgbData, offset, scanlength, x, y, width, height,
				processAlpha, paint);
	}
}

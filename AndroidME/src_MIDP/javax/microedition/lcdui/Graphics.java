package javax.microedition.lcdui;


import javax.microedition.lcdui.game.Sprite;

import net.yura.android.lcdui.FontManager;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

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
        return canvas;
    }
    public Paint getPaint() {
        return paint;
    }

    public void setCanvas(android.graphics.Canvas canvas) {
        this.canvas = canvas;
        if (canvas != null) {
            canvas.save();
        }
    }

    public void reset() {
        tx = 0;
        ty = 0;

        if (canvas != null) {
            canvas.restore();
            canvas.save();
        }

        paint.setColor(0xFF000000);
    }

    public int getClipX() {
        return canvas.getClipBounds().left;
    }

    public int getClipY() {
        return canvas.getClipBounds().top;
    }

    public int getClipWidth() {
        return canvas.getClipBounds().width();
    }

    public int getClipHeight() {
        return canvas.getClipBounds().height();
    }

    public int getColor() {
        return paint.getColor() & 0x00FFFFFF;
    }

    public void setColor(int color) {
        paint.setColor(0xFF000000 | color);
    }

    public void fillRect(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public void fillRoundRect(int x, int y, int width, int height, int rx,
            int ry) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(x, y, x + width, y + height), rx, y, paint);
    }

    public void drawImage(javax.microedition.lcdui.Image image, int x, int y, int anchor) {

        if (anchor == 0) {
            anchor = TOP | LEFT;
        }

        int ax;
        if ((anchor & LEFT) != 0) {
            ax = x;
        } else if ((anchor & HCENTER) != 0) {
            ax = x - image.getWidth() / 2;
        } else {
            ax = x - image.getWidth();
        }

        int ay;
        if ((anchor & TOP) != 0) {
            ay = y;
        } else if ((anchor & VCENTER) != 0) {
            ay = y - image.getHeight() / 2;
        } else {
            ay = y - image.getHeight();
        }
        canvas.drawBitmap(image.getBitmap(), ax, ay, null);
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
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void drawRect(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public void drawRoundRect(int x, int y, int width, int height, int rx,
            int ry) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(x, y, x + width, y + height), rx, ry, paint);
    }

    public javax.microedition.lcdui.Font getFont() {
        return font;
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
        Paint paintFont = androidFont.getPaint();

        if ((anchor & javax.microedition.lcdui.Graphics.TOP) != 0) {
            newy -= paintFont.getFontMetricsInt().ascent;
        } else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
            newy -= paintFont.getFontMetricsInt().descent;
        }
        if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= paintFont.measureText(str) / 2;
        } else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= paintFont.measureText(str);
        }

        paintFont.setColor(paint.getColor());
        canvas.drawText(str, newx, newy, paintFont);
    }

    public void clipRect(int x, int y, int w, int h) {
        canvas.clipRect(x, y, x + w, y + h);
    }

    public void setClip(int x, int y, int w, int h) {
        canvas.restore();
        canvas.save();
        canvas.translate(tx, ty);
        canvas.clipRect(x, y, x + w, y + h);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {

        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF(x, y, x + width, y + height);
        canvas.drawArc(rect, startAngle, arcAngle, false, paint);
    }

    public void translate(int x, int y) {
        tx += x;
        ty += y;
        canvas.translate(x, y);
    }

    public int getTranslateX() {
        return tx;
    }

    public int getTranslateY() {
        return ty;
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

    public void drawRegion(Image src, int xSrc, int ySrc, int width,
            int height, int transform, int x_dst, int y_dst, int anchor) {

        int rotate;
        boolean mirror;

        switch (transform) {
            case Sprite.TRANS_NONE: {
                rotate = 0;
                mirror = false;
                break;
            }
            case Sprite.TRANS_ROT90: {
                rotate = 90;
                mirror = false;
                break;
            }
            case Sprite.TRANS_ROT180: {
                rotate = 180;
                mirror = false;
                break;
            }
            case Sprite.TRANS_ROT270: {
                rotate = 270;
                mirror = false;
                break;
            }
            case Sprite.TRANS_MIRROR: {
                rotate = 0;
                mirror = true;
                break;
            }
            case Sprite.TRANS_MIRROR_ROT90: {
                rotate = 90;
                mirror = true;
                break;
            }
            case Sprite.TRANS_MIRROR_ROT180: {
                rotate = 180;
                mirror = true;
                break;
            }
            case Sprite.TRANS_MIRROR_ROT270: {
                rotate = 270;
                mirror = true;
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad transform");
            }
        }

        drawRegion(src, xSrc, ySrc, width, height, rotate, mirror, x_dst, y_dst, anchor);
    }

    public void drawRegion(Image src, int xSrc, int ySrc, int width,
            int height, int rotate, boolean mirror, int xDst, int yDst, int anchor) {

        // may throw NullPointerException, this is ok
        if (xSrc + width > src.getWidth() || ySrc + height > src.getHeight() ||
            width < 0 || height < 0 || xSrc < 0 || ySrc < 0) {
            throw new IllegalArgumentException("Area out of Image");
        }

        if (anchor == 0) {
            anchor = TOP | LEFT;
        }

        // Create a matrix and apply the rotation and mirroring (scale == -1)
        Matrix matrix = new Matrix();
        matrix.preRotate(rotate);
        matrix.preScale(mirror ? -1.0f : 1.0f, 1.0f);

        // Get the destination rectangle after rotation and mirroring...
        RectF r = new RectF(0, 0,  width,  height);
        matrix.mapRect(r);


        float anchorX = (r.right - r.left);
        int nRefs = 0;

        // Process horizontal anchor (LEFT, RIGHT and HCENTER)
        if ((anchor & LEFT) > 0) {
            nRefs++;
            anchorX = 0.0f;
        }
        if ((anchor & HCENTER) > 0) {
            nRefs++;
            anchorX = anchorX / 2.0f;
        }
        if ((anchor & RIGHT) > 0) {
            nRefs++;
        }

        // Can only have one horizontal anchor
        if (nRefs > 1) {
            throw new IllegalArgumentException("Bad Anchor");
        }

        float anchorY = (r.bottom - r.top);
        nRefs = 0;

        // Process vertical anchor (BASELINE, BOTTOM, TOP and VCENTER)
        if ((anchor & BASELINE) > 0) {
            nRefs++;
        }
        if ((anchor & BOTTOM) > 0) {
            nRefs++;
        }
        if ((anchor & TOP) > 0) {
            nRefs++;
            anchorY = 0.0f;
        }
        if ((anchor & VCENTER) > 0) {
            nRefs++;
            anchorY = anchorY / 2.0f;
        }

        // Can only have one horizontal anchor
        if (nRefs > 1) {
            throw new IllegalArgumentException("Bad Anchor");
        }

        canvas.save();

        // Translate for new destination, destination rectangle and anchor
        canvas.translate((xDst - r.left - anchorX), (yDst - r.top - anchorY));

        // Apply the transformation matrix
        canvas.concat(matrix);

        // Draw the image
        Rect srcRect = new Rect(xSrc, ySrc, xSrc + width, ySrc + height);
        Rect dstRect = new Rect(0, 0,  width,  height);
        canvas.drawBitmap(src.getBitmap(), srcRect, dstRect, null);

        canvas.restore();
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

        canvas.drawBitmap(rgbData, offset, scanlength, x, y, width, height, processAlpha, null);
    }

    public void scale(double sx, double sy) {
        canvas.scale((float)sx, (float)sy);
    }

}

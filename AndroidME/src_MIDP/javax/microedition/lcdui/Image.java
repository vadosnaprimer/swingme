package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.Bitmap.Config;


public class Image {

    private Bitmap bitmap;

    public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
        if (image == null) {
            throw new NullPointerException();
        }

        if (x + width > image.getWidth() || y + height > image.getHeight() ||
            width <= 0 || height <= 0 || x < 0 || y < 0) {
            throw new IllegalArgumentException("Area out of Image");
        }

        // MIDP: the result image width/height depends on the transform
        Image res;
        switch (transform) {
            case Sprite.TRANS_NONE:
                Bitmap bmp = Bitmap.createBitmap(image.bitmap, x, y, width, height);
                res = new Image(bmp);
                break;
            case Sprite.TRANS_ROT90:
            case Sprite.TRANS_ROT270:
            case Sprite.TRANS_MIRROR_ROT90:
            case Sprite.TRANS_MIRROR_ROT270: {
                res = Image.createTransparentImage(height, width);
                break;
            }
            default: {
                res = Image.createTransparentImage(width, height);
            }
        }

        if (transform!=Sprite.TRANS_NONE) {
            Graphics g = res.getGraphics();
            g.drawRegion(image, x, y, width, height, transform, 0, 0, 0);
        }

        return res;
    }

    public static Image createImage(InputStream stream) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        if (bitmap==null) {
            throw new IOException();
        }
        return new Image(bitmap);
    }

    private static Image createTransparentImage(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        return new Image(bitmap);
    }

    public static Image createImage(int width, int height) {
        Image res = createTransparentImage(width, height);

        // MIDP: All pixels should be white
        Graphics g = res.getGraphics();
        g.setColor(0xFFFFFFFF);
        g.fillRect(0, 0, width, height);

        return res;
    }

    public static Image createImage(String resource) throws IOException {
        return createImage(Image.class.getResourceAsStream(resource));
    }

    public static Image createImage(byte[] imgData, int offset, int length) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, offset, length);
        if (bitmap==null) {
            return null;
        }
        return new Image(bitmap);
    }

    public static final Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        Bitmap.Config config = (processAlpha) ? Bitmap.Config.ARGB_4444 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(rgb, width, height, config);
        return new Image(bitmap);
    }

    private Image(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException();
        }
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public Graphics getGraphics() {

        if (!bitmap.isMutable()) {
            throw new IllegalStateException("Image is immutable");
        }

        Canvas canvas = new Canvas(bitmap);
        Graphics displayGraphics = new Graphics(canvas);
        displayGraphics.setColor(0xFF000000);

        return displayGraphics;
    }

    public void getRGB(int[] rgb, int offset, int scanlength, int x, int y, int width, int height) {
        bitmap.getPixels(rgb, offset, scanlength, x, y, width, height);
    }

    public boolean isMutable() {
        return bitmap.isMutable();
    }

    public void setRGB(int x, int y, int color) {
        bitmap.setPixel(x, y, color);
    }

    public static void filter(Image source, Image bm, ColorMatrix cm) {

        // Image bm = createImage(source.getWidth(), source.getHeight());

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColorFilter(new android.graphics.ColorMatrixColorFilter(cm));

        // bm.getGraphics().getCanvas().drawBitmap(source.bitmap, 0, 0, paint);
        new Canvas(bm.bitmap).drawBitmap(source.bitmap, 0, 0, paint);

        // return bm;
    }
}

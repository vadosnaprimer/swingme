package javax.microedition.lcdui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.lcdui.game.Sprite;

import net.yura.android.AndroidMeApp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * for saving of image please use
 * @see net.yura.mobile.util.ImageUtil#saveImage(Image, OutputStream)
 */
public class Image {

    private Bitmap bitmap;

    public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
        if (image == null) {
            throw new NullPointerException();
        }

        if (x + width > image.getWidth() || y + height > image.getHeight() ||
            width <= 0 || height <= 0 || x < 0 || y < 0) {
            throw new IllegalArgumentException("Area out of Image: "+image.getWidth()+"x"+image.getHeight()+" area: "+x+","+y+" "+width+"x"+height);
        }

        // MIDP: the result image width/height depends on the transform
        Image res;
        switch (transform) {
            case Sprite.TRANS_NONE:
                Bitmap bmp;
                try {
                    bmp = Bitmap.createBitmap(image.bitmap, x, y, width, height);

                } catch (OutOfMemoryError e) {
                    cleanMem();
                    bmp = Bitmap.createBitmap(image.bitmap, x, y, width, height);
                }
                res = new Image(bmp);

                break;
            case Sprite.TRANS_ROT90:
            case Sprite.TRANS_ROT270:
            case Sprite.TRANS_MIRROR_ROT90:
            case Sprite.TRANS_MIRROR_ROT270: {
                res = Image.createImage(height, width, image.bitmap.getConfig());
                break;
            }
            default: {
                res = Image.createImage(width, height, image.bitmap.getConfig());
            }
        }

        if (transform!=Sprite.TRANS_NONE) {
            Graphics g = res.getGraphics();
            g.drawRegion(image, x, y, width, height, transform, 0, 0, 0);
        }

        return res;
    }

    public static Image createImage(InputStream stream) throws IOException {
        Bitmap bitmap;
        if (stream instanceof ResourceInputStream) {
            try {
                bitmap = ((ResourceInputStream) stream).getBitmap();
            }
            catch (Throwable e) {
                IOException ex = new IOException();
                ex.initCause(e);
                throw ex;
            }

            if (bitmap == null) {
                throw new IOException("ResourceInputStream.getBitmap returned null "+stream);
            }
        }
        else {
            int size = Math.max(stream.available(), 8 * 1024);
            BufferedInputStream buffInput = new BufferedInputStream(stream, size);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inDither = false;
            options.inPurgeable = true; // http://www.droidnova.com/2d-sprite-animation-in-android-addendum,505.html

            try {
                bitmap = BitmapFactory.decodeStream(buffInput,null,options);
            } catch (OutOfMemoryError e) {
                cleanMem();
                buffInput.reset();
                buffInput.mark(1024);
                bitmap = BitmapFactory.decodeStream(buffInput,null,options);
            }

            if (bitmap == null) {
                throw new IOException("BitmapFactory.decodeStream returned null "+buffInput+" "+options);
            }
        }
        return new Image(bitmap);
    }

    private static Image createImage(int width, int height, Config config) {
        Bitmap bitmap;
        OutOfMemoryError error=null;
        try {
            bitmap = Bitmap.createBitmap(width, height, config);
            // for some reason this sometimes just returns null instead of throwing a error
            if (bitmap==null) {
                throw new OutOfMemoryError("Bitmap.createBitmap returned null");
            }
        }
        catch (OutOfMemoryError e) {
            cleanMem();
            error = e;
            bitmap = Bitmap.createBitmap(width, height, config);
        }
        if (bitmap==null) {
            throw new OutOfMemoryError("Bitmap.createBitmap returned null for w="+width+" h="+height+" config="+config+" error="+error );
        }
        return new Image(bitmap);
    }

    public static Image createImage(int width, int height) {
        Image res = createImage(width, height, Config.ARGB_8888);

        // MIDP: All pixels should be white
        Graphics g = res.getGraphics();
        g.setColor(0xFFFFFFFF);
        g.fillRect(0, 0, width, height);

        return res;
    }

    public static Image createImage(String resource) throws IOException {
        InputStream in = getResourceAsStream(resource);// try and load from android res
        if (in==null) {
            in = Image.class.getResourceAsStream(resource);
            if (in == null) {
                throw new IOException("can not find: "+resource);
            }
        }
        return createImage(in);
    }

    public static Image createImage(byte[] imgData, int offset, int length) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
            BitmapFactory.Options.class.getField("inPurgeable").set(opts, true);
        } catch (Exception e) {
            // inPurgeable not supported
            // http://www.droidnova.com/2d-sprite-animation-in-android-addendum,505.html
        }

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeByteArray(imgData, offset, length, opts);
        } catch (OutOfMemoryError e) {
            cleanMem();
            bitmap = BitmapFactory.decodeByteArray(imgData, offset, length, opts);
        }
        if (bitmap==null) {
            return null;
        }
        return new Image(bitmap);
    }

    public static final Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        Bitmap.Config config = (processAlpha) ? Bitmap.Config.ARGB_4444 : Bitmap.Config.RGB_565;
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(rgb, width, height, config);
        } catch (OutOfMemoryError e) {
            cleanMem();
            bitmap = Bitmap.createBitmap(rgb, width, height, config);
        }

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

    @Override
    public String toString() {
        return "Image"+bitmap.toString();
    }

    public static void filter(Image source, Image bm, ColorMatrix cm) {

        // Image bm = createImage(source.getWidth(), source.getHeight());

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColorFilter(new android.graphics.ColorMatrixColorFilter(cm));

        // bm.getGraphics().getCanvas().drawBitmap(source.bitmap, 0, 0, paint);
        new Canvas(bm.bitmap).drawBitmap(source.bitmap, 0, 0, paint);

        // return bm;
    }

    private static void cleanMem() {
        System.gc();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
    }

    public static ResourceInputStream getResourceAsStream(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        name = name.replace(".9.png", "").replace(".png", "").replace(".jpg", "").replace(".gif", "");

        Resources res = AndroidMeApp.getContext().getResources();

        int id = res.getIdentifier(name, "drawable", AndroidMeApp.getContext().getPackageName() );

        if (id > 0) {
            return new ResourceInputStream(res, id);
        }
        return null;
    }

    public static class ResourceInputStream extends InputStream {
        private Resources res;
        private int resId;

        public ResourceInputStream(Resources res, int resId) {
            this.res = res;
            this.resId = resId;
        }

        @Override
        public int read() throws IOException {
            throw new RuntimeException("Not implemented");
        }

        public Drawable getDrawable() {
            return res.getDrawable(resId);
        }

        public Bitmap getBitmap() {
            return ((BitmapDrawable)getDrawable()).getBitmap();
        }

        @Override
        public String toString() {
            return "RIS{res="+res+",id="+resId+"}";
        }
    }
}

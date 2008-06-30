package net.yura.mobile.util;

import javax.microedition.lcdui.Image;

/**
 * @author ymamyrin
 */
public class ImageUtil {

    public static final void imageColor(int ai[], int i) {
        int j = (i & 0xff0000) >> 16;
        int k = (i & 0xff00) >> 8;
        int l = (i & 0xff) >> 0;
        for(int i1 = 0; i1 < ai.length; i1++)
        {
            int j1 = (ai[i1] & 0xff000000) >> 24;
            int k1 = (ai[i1] & 0xff) >> 0;
            ai[i1] = j1 << 24 | (k1 * j) / 255 << 16 | (k1 * k) / 255 << 8 | (k1 * l) / 255;
        }

    }

    public static final Image imageColor(Image image, int i) {
        
        int ai[] = new int[image.getWidth() * image.getHeight()];
        image.getRGB(ai, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        imageColor(ai, i);
        return Image.createRGBImage(ai, image.getWidth(), image.getHeight(), true);
    }
    
    
}

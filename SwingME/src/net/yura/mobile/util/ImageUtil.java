/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.util;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;

/**
 * @author Yura Mamyrin
 */
public class ImageUtil {

    public static Image makeImage(int w,int h,int color) {

                int[] rgbBuff = new int[w*h];

                for (int i = 0; i < rgbBuff.length; i++) {

                    rgbBuff[i] = color;

                }

                return Image.createRGBImage(rgbBuff, w, h, true);

    }

    public static final void imageColor(int pixels[], int color) {
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = (color & 0xff) >> 0;
        for(int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] & 0xff000000) >> 24;
            int blue = (pixels[i] & 0xff) >> 0;
            pixels[i] = alpha << 24 | (blue * r) / 255 << 16 | (blue * g) / 255 << 8 | (blue * b) / 255;
        }

    }

    /**
     * replaces all values of the blue channel with a color
     */
    public static final Image imageColor(Image image, int i) {

        int ai[] = new int[image.getWidth() * image.getHeight()];
        image.getRGB(ai, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        imageColor(ai, i);
        return Image.createRGBImage(ai, image.getWidth(), image.getHeight(), true);
    }

    public static Image colorize(Image original, int newColor) {
        int[] rgba = new int[original.getWidth()*original.getHeight()];
        original.getRGB(rgba, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());

        for (int i=0; i< rgba.length;i++) {
            int alpha = ((rgba[i] >> 24) & 0xFF);
            rgba[i] = (newColor | (alpha << 24));
        }

        return Image.createRGBImage(rgba, original.getWidth(), original.getHeight(), true);
    }

    public static Image scaleImage(Image img, int newW, int newH) {
        try {
            // Ensure we have 3D API, otherwise throws exception
            Class.forName("javax.microedition.m3g.Background");
            return scaleImage3D(img, newW, newH);
        }
        catch (Throwable e) {
            // Do nothing. Converting with 3D API failed. Use sampling.
        }

        return null;
    }

    private static Image scaleImage3D(Image img, int newW, int newH) {

        // Create a mutable image with the requested size
        Image resImg = Image.createImage(newW, newH);
        Graphics g = resImg.getGraphics();

        Image2D image2D = new Image2D(Image2D.RGB, img);
        Background background = new Background();
        background.setColor(0xffffcc); // set the background color
        background.setImage(image2D);

        // get the singleton Graphics3D instance
        Graphics3D iG3D = Graphics3D.getInstance();
        try {
            iG3D.bindTarget(g, true, Graphics3D.TRUE_COLOR);
            iG3D.setViewport(0, 0, newW, newH);
            // clear the color and depth buffers
            iG3D.clear(background);
        }
        finally
        {
            // flush
            iG3D.releaseTarget();
        }

        return resImg;
    }
}

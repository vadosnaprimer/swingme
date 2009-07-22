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

import net.yura.mobile.gui.Graphics2D;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * @author Yura Mamyrin
 */
public class ImageUtil {


    public static void fillArea(Graphics2D g,Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h,int t) {
        
        if (src_w==0 || src_h==0 || dest_w==0 || dest_h==0) return;
        
        final int cx = g.getClipX();
        final int cy = g.getClipY();
        final int cw = g.getClipWidth();
        final int ch = g.getClipHeight();
        
        g.clipRect(dest_x,dest_y,dest_w,dest_h);
        
        boolean normal = (t==Sprite.TRANS_NONE || t==Sprite.TRANS_MIRROR || t==Sprite.TRANS_ROT180 || t==Sprite.TRANS_MIRROR_ROT180);
        int a = normal?src_w:src_h;
        int b = normal?src_h:src_w;
        
        for (int pos_x=dest_x;pos_x<(dest_x+dest_w);pos_x=pos_x+a) {
            for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+b) {
                g.drawRegion(img, src_x,  src_y, src_w, src_h, t, pos_x, pos_y,Graphics.TOP|Graphics.LEFT);
            }
        }

        g.setClip(cx,cy,cw,ch);
    }

//
//    public static void fillArea(Graphics g,Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h) {
//
//        fillArea(new Graphics2D(g,Sprite.TRANS_NONE), img, src_x, src_y, src_w, src_h, dest_x, dest_y, dest_w, dest_h, Sprite.TRANS_NONE );
//
//    }
    
    public static Image makeImage(int w,int h,int color) {

                int[] rgbBuff = new int[w*h];

                for (int i = 0; i < rgbBuff.length; i++) {

                    rgbBuff[i] = color;

                }

                return Image.createRGBImage(rgbBuff, w, h, true);

    }
    
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

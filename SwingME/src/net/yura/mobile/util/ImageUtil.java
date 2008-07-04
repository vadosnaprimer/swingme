package net.yura.mobile.util;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * @author Yura Mamyrin
 */
public class ImageUtil {


    public static void fillArea(Graphics g,Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h) {
        
        if (src_w==0 || src_h==0 || dest_w==0 || dest_h==0) return;
        
        final int cx = g.getClipX();
        final int cy = g.getClipY();
        final int cw = g.getClipWidth();
        final int ch = g.getClipHeight();
        
        g.clipRect(dest_x,dest_y,dest_w,dest_h);
        
        for (int pos_x=dest_x;pos_x<(dest_x+dest_w);pos_x=pos_x+src_w) {
            for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+src_h) {
                g.drawRegion(img, src_x,  src_y, src_w, src_h, Sprite.TRANS_NONE, pos_x, pos_y,Graphics.TOP|Graphics.LEFT);
            }
        }
        
        g.setClip(cx,cy,cw,ch);
        
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

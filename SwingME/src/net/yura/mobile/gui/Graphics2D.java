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

package net.yura.mobile.gui;

import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see java.awt.Graphics2D
 */
public class Graphics2D {

        private Font font;
        private Graphics g;
        private int trans;

        /**
         * @see java.awt.Graphics#Graphics() Graphics.Graphics
         */
        public Graphics2D(Graphics g) {
            setGraphics(g);
            font = Font.getDefaultSystemFont();
        }

        /**
         * @see java.awt.Graphics#getColor() Graphics.getColor
         */
        public int getColor() {
            return g.getColor();
        }

        public void setTransform(int t) {
            trans = t;
        }

        /**
         * @see java.awt.Graphics#setColor(java.awt.Color) Graphics.setColor
         */
        public void setColor(int c) {
                //#mdebug debug
                if (isTransparent(c)) {
                    Logger.warn("trying to set a transparent color: "+Integer.toHexString(c));
                    Logger.dumpStack();
                }
                //#enddebug
                g.setColor(c);
        }

        /**
         * @see java.awt.Graphics#drawRect(int, int, int, int) Graphics.drawRect
         */
        public void drawRect(int x,int y,int w,int h) {

                g.drawRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }

        /**
         * @see java.awt.Graphics#fillRect(int, int, int, int) Graphics.fillRect
         */
        public void fillRect(int x,int y,int w,int h) {

                g.fillRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }

        /**
         * @see java.awt.Graphics#drawLine(int, int, int, int) Graphics.drawLine
         */
        public void drawLine(int x1,int y1,int x2,int y2) {

                g.drawLine(
                        convertTrans(x1,y1),
                        convertTrans(y1,x1),
                        convertTrans(x2,y2),
                        convertTrans(y2,x2));

        }

        public void fillTriangle(int x1, int y1,int x2,int y2,int x3,int y3) {

                g.fillTriangle(
                        convertTrans(x1,y1),
                        convertTrans(y1,x1),
                        convertTrans(x2,y2),
                        convertTrans(y2,x2),
                        convertTrans(x3,y3),
                        convertTrans(y3,x3));

        }

        public void drawRegion(Image src, int x_src, int y_src, int width, int height, int x_dest, int y_dest) {

                int x1 = convertTrans(x_dest,y_dest);
                int y1 = convertTrans(y_dest,x_dest);

                int x=x_src;
                int y=y_src;
                int w=width;
                int h=height;

                //#mdebug warn
                if (w <= 0 || h <= 0) {
                    Logger.warn("trying to draw Region with width="+w+" height="+h);
                    Logger.dumpStack();
                }
                //#enddebug

                g.drawRegion(src, x, y, w, h, trans , x1, y1, Graphics.TOP|Graphics.LEFT );
        }

        /**
         * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver) Graphics.drawImage
         */
        public void drawImage(Image src, int x,int y) {
                drawRegion(src, 0, 0, src.getWidth(), src.getHeight(), x, y);
        }

        /**
         * unlike the real swing, this will tile the image and not streach it
         * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver) Graphics.drawImage
         */
        public void drawImage(Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h) {

            if (src_w<=0 || src_h<=0 || dest_w<=0 || dest_h<=0) {
                //#debug debug
                //Logger.debug("calling tile on a area of size less then 0: src_w=" +src_w  +" src_h="+src_h +" dest_w="+ dest_w +" dest_h="+dest_h );
                return;
            }

            final int[] c = getClip();
            clipRect(dest_x,dest_y,dest_w,dest_h);

            int start_x=(dest_x>=c[0]?dest_x:  dest_x+ (((c[0]-dest_x)/src_w)*src_w)  );
            int start_y=(dest_y>=c[1]?dest_y:  dest_y+ (((c[1]-dest_y)/src_h)*src_h)  );
            int end_x = (dest_x+dest_w)<=(c[0]+c[2])? (dest_x+dest_w) : (c[0]+c[2]) ;
            int end_y = (dest_y+dest_h)<=(c[1]+c[3])? (dest_y+dest_h) : (c[1]+c[3]) ;

            for (int pos_x=start_x;pos_x<end_x;pos_x=pos_x+src_w) {
                for (int pos_y=start_y;pos_y<end_y;pos_y=pos_y+src_h) {
                    drawRegion(img, src_x,  src_y, src_w, src_h, pos_x, pos_y);
                }
            }

            setClip(c);

            //#mdebug info
            if (Midlet.getPlatform() != Midlet.PLATFORM_ME4SE) {
                int tile = (( (end_x-start_x) /src_w)*( (end_y-start_y) /src_h));
                if ( tile>15 ) {
                    Logger.info("going to tile a very small image "+tile+" times: src_w=" +src_w+" src_h="+src_h+" dest_w="+ dest_w +" dest_h="+dest_h );

                    if ( tile>30 ) {
                      Logger.info("###########################################################");
                        g.setColor( new Random().nextInt() );
                        for (int pos_x=start_x;pos_x<end_x;pos_x=pos_x+src_w) {
                            g.drawLine(pos_x, start_y, pos_x, end_y);
                        }
                        for (int pos_y=start_y;pos_y<end_y;pos_y=pos_y+src_h) {
                            g.drawLine(start_x, pos_y, end_x, pos_y);
                        }
                    }
                }
            }
            //#enddebug

        }

        static int maxView = -1;
        static {
            try {
                // Ensure we have 3D API, otherwise throws exception
                Class.forName("javax.microedition.m3g.Background");

                maxView = Integer.MAX_VALUE;
                try {
                    Object o = javax.microedition.m3g.Graphics3D.getProperties().get("maxViewportDimension");
                    maxView = ((Integer) o).intValue();
                } catch (Throwable e) {
                    // TODO: handle exception
                }
            }
            catch (Throwable e) {
                //#debug debug
                Logger.warn("please use fake 3D api for scaled image drawing", e);
            }
        }

        public void drawScaledImage(Image img, int x, int y, int w, int h) {
            if (maxView > 0) {
                javax.microedition.m3g.Image2D image2D = new javax.microedition.m3g.Image2D(javax.microedition.m3g.Image2D.RGB, img);
                javax.microedition.m3g.Background background = new javax.microedition.m3g.Background();
                background.setColor(0xffffffcc); // set the background color
                background.setImage(image2D);

                // get the singleton Graphics3D instance
                javax.microedition.m3g.Graphics3D iG3D = javax.microedition.m3g.Graphics3D.getInstance();
                try {
                    iG3D.bindTarget(g, true, javax.microedition.m3g.Graphics3D.TRUE_COLOR);
                    iG3D.setViewport(x, y, Math.min(maxView, w), Math.min(maxView, h));
                    // clear the color and depth buffers
                    iG3D.clear(background);
                }
                finally {
                    // flush
                    iG3D.releaseTarget();
                }
            }
            else {
                // if we cant scale we will draw none-scalled but centered
                drawImage(img, x+w/2-img.getWidth()/2, y+h/2-img.getHeight()/2);
            }
        }

        /**
         * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver) Graphics.drawImage
         */
        public void drawSprite(Sprite src, int frame, int x,int y) {
            src.setFrame(frame);
            src.setPosition(convertTrans(x,y), convertTrans(y,x));
            // TODO: check what the current translate of the sprite is!
            src.setTransform(trans);
            src.paint(g);
        }

        private int convertTrans(int a,int b) {
                return convertTransform(a,b,trans);
        }

        private static int convertTransform(int a,int b,int transform) {

                if (transform == Sprite.TRANS_NONE || transform == Sprite.TRANS_MIRROR_ROT180) {
                        return a;
                }
                if (transform == Sprite.TRANS_ROT180 || transform == Sprite.TRANS_MIRROR) {
                        return -a;
                }
                if (transform == Sprite.TRANS_ROT90 || transform == Sprite.TRANS_MIRROR_ROT270) {
                        return b;
                }
                //if (transform == Sprite.TRANS_MIRROR_ROT90 || transform == Sprite.TRANS_ROT270) {
                        return -b;
                //}
        }

        public int getClipX() {

                final int cx = g.getClipX();
                final int cy = g.getClipY();

                return convertTrans(cx,cy);

        }

        public int getClipY() {

                final int cx = g.getClipX();
                final int cy = g.getClipY();

                return convertTrans(cy,cx);

        }

        public int getClipWidth() {

                final int cw = g.getClipWidth();
                final int ch = g.getClipHeight();

                return convertTrans(cw,ch);
        }

        public int getClipHeight() {

                final int cw = g.getClipWidth();
                final int ch = g.getClipHeight();

                return convertTrans(ch,cw);

        }

        /**
         * @see java.awt.Graphics#clipRect(int, int, int, int) Graphics.clipRect
         */
        public void clipRect(int x, int y, int w, int h) {

                g.clipRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }

        /**
         * @see java.awt.Graphics#setClip(int, int, int, int) Graphics.setClip
         */
        public void setClip(int x, int y, int w, int h) {

                g.setClip(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }

    /**
     * @see java.awt.Graphics#translate(int, int) Graphics.translate
     */
    public void translate(int x, int y) {
        g.translate( convertTrans(x,y), convertTrans(y,x));
    }
    public int getTranslateX() {
        return convertTrans(g.getTranslateX(),g.getTranslateY());
    }
    public int getTranslateY() {
        return convertTrans(g.getTranslateY(),g.getTranslateX());
    }

    /**
     * @see java.awt.Graphics#drawString(java.lang.String, int, int) Graphics.drawString
     */
    public void drawString(String drawString, int tx, int ty) {
        font.drawString(g,drawString, tx,ty, Graphics.TOP | Graphics.LEFT  );
    }

    /**
     * @see java.awt.Graphics#setFont(java.awt.Font) Graphics.setFont
     */
    public void setFont(Font font) {
        this.font = font;
    }

    public int getStrokeStyle() {
        return g.getStrokeStyle();
    }

    public void setStrokeStyle(int stroke) {
        g.setStrokeStyle(stroke);
    }

    /**
     * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int) Graphics.fillRoundRect
     */
    public void fillRoundRect(int x, int y, int w, int h, int a1, int a2) {
                g.fillRoundRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w),
                        a1,a2);
    }

    /**
     * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int) Graphics.drawRoundRect
     */
    public void drawRoundRect(int x, int y, int w, int h, int a1, int a2) {
                g.drawRoundRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w),
                        a1,a2);
    }

    /**
     * @see java.awt.Graphics#fillArc(int, int, int, int, int, int) Graphics.fillArc
     */
    public void fillArc(int x, int y, int width, int height, int angle, int arc) {
        g.fillArc(
                convertTrans(x,y),
                convertTrans(y,x),
                convertTrans(width,height),
                convertTrans(height,width), angle, arc);
    }

    /**
     * @see java.awt.Graphics#fillOval(int, int, int, int) Graphics.fillOval
     */
    public void fillOval(int x, int y, int width, int height) {
        fillArc(x, y, width, height, 0, 360);
    }

    /**
     * @see java.awt.Graphics#drawArc(int, int, int, int, int, int) Graphics.drawArc
     */
    public void drawArc(int x, int y, int width, int height, int angle, int arc) {
        g.drawArc(
                convertTrans(x,y),
                convertTrans(y,x),
                convertTrans(width,height),
                convertTrans(height,width), angle, arc);
    }

    /**
     * @see java.awt.Graphics#drawOval(int, int, int, int) Graphics.drawOval
     */
    public void drawOval(int x, int y, int width, int height)  {
        drawArc(x, y, width, height, 0, 360);
    }

    void setGraphics(Graphics gtmp) {
        g = gtmp;
    }

    public Graphics getGraphics() {
        return g;
    }

    public int getTransform() {
        return trans;
    }

    /**
     * @see java.awt.Graphics#getClipBounds() Graphics.getClipBounds
     * @see java.awt.Graphics#getClip() Graphics.getClip
     */
    public int[] getClip() {
        return new int[] {getClipX(),getClipY(),getClipWidth(),getClipHeight()};
    }

    /**
     * @see java.awt.Graphics#setClip(java.awt.Shape) Graphics.setClip
     */
    public void setClip(int[] clip) {
        setClip(clip[0], clip[1], clip[2], clip[3]);
    }

    /**
     * @see java.awt.Graphics#getFont() Graphics.getFont
     */
    public Font getFont() {
        return font;
    }

    public static boolean isTransparent(int color) {
        return (color & 0xFF000000) == 0;
    }

    public static boolean isOpaque(int color) {
        return (color & 0xFF000000) == 0xFF000000;
    }

    public static int parseColor(String value,int base) {

        boolean addAlpha=false;

        if (value.startsWith("#")) {
            base=16;
            value=value.substring(1);
        }
        else if (value.startsWith("0x")) {
            base=16;
            value=value.substring(2);
        }

        if (base==16) {
            addAlpha = value.length()==6;
        }
        // need to use long instead of int coz #FFFFFFFF throws NumberFormatException
        int r = (int)Long.parseLong(value, base);
        return addAlpha?(0xFF000000|r):r;
    }

}

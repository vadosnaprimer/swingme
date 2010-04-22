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

        Graphics2D() {
        }

        public Graphics2D(Graphics g) {
            setGraphics(g);
        }

        public int getColor() {
            return g.getColor();
        }

        public void setTransform(int t) {
            trans = t;
        }

        public void setColor(int c) {

                g.setColor(c);

        }

        public void drawRect(int x,int y,int w,int h) {

                g.drawRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }

        public void fillRect(int x,int y,int w,int h) {

                g.fillRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }


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

        public void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest) {

                int x1 = convertTrans(x_dest,y_dest);
                int y1 = convertTrans(y_dest,x_dest);

                int x=x_src;
                int y=y_src;
                int w=width;
                int h=height;

                // this is bad, but i cant work out the proper way easily

                if (transform!=Sprite.TRANS_NONE) {
                        src = Image.createImage(src, x_src, y_src, width, height, transform);
                        x = 0;
                        y = 0;
                        w = src.getWidth();
                        h = src.getHeight();
                }

                g.drawRegion(src, x, y, w, h, trans , x1, y1, Graphics.TOP|Graphics.LEFT );

        }

        /**
         * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver) Graphics.drawImage
         */
        public void drawImage(Image src, int x,int y) {
                drawRegion(src, 0, 0, src.getWidth(), src.getHeight(), Sprite.TRANS_NONE , x, y);
        }

        /**
         * unlike the real swing, this will tile the image and not streach it
         * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver) Graphics.drawImage
         */
        public void drawImage(Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h,int t) {

            if (src_w<=0 || src_h<=0 || dest_w<=0 || dest_h<=0) {
                // #debug
                //Logger.debug("calling tile on a area of size less then 0: src_w=" +src_w  +" src_h="+src_h +" dest_w="+ dest_w +" dest_h="+dest_h );
                return;
            }

            final int[] c = getClip();
            g.clipRect(dest_x,dest_y,dest_w,dest_h);

            boolean normal = (t==Sprite.TRANS_NONE || t==Sprite.TRANS_MIRROR || t==Sprite.TRANS_ROT180 || t==Sprite.TRANS_MIRROR_ROT180);
            int a = normal?src_w:src_h;
            int b = normal?src_h:src_w;

            for (int pos_x=dest_x;pos_x<(dest_x+dest_w);pos_x=pos_x+a) {
                for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+b) {
                    drawRegion(img, src_x,  src_y, src_w, src_h, t, pos_x, pos_y);
                }
            }

            setClip(c);



            //#mdebug info
            int tile = ((dest_w/a)*(dest_h/b));
            if ( tile>15 ) {
                Logger.info("going to tile a very small image "+tile+" times: src_w=" +a+" src_h="+b+" dest_w="+ dest_w +" dest_h="+dest_h );

                if ( tile>30 ) {
                  Logger.info("###########################################################");
                    g.setColor( new Random().nextInt() );
                    for (int pos_x=dest_x;pos_x<(dest_x+dest_w);pos_x=pos_x+a) {
                        g.drawLine(pos_x, dest_y, pos_x, (dest_y+dest_h));
                    }
                    for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+b) {
                        g.drawLine(dest_x, pos_y, (dest_x+dest_w), pos_y);
                    }
                }
            }
            //#enddebug

        }


        /**
         * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver) Graphics.drawImage
         */
        public void drawSprite(Sprite src, int x,int y) {
            // TODO check translate!
            src.setPosition(x, y);
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

        public void clipRect(int x, int y, int w, int h) {

                g.clipRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }

        public void setClip(int x, int y, int w, int h) {

                g.setClip(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w));

        }

    public void translate(int x, int y) {
        g.translate( convertTrans(x,y), convertTrans(y,x));
    }
    public int getTranslateX() {
        return convertTrans(g.getTranslateX(),g.getTranslateY());
    }
    public int getTranslateY() {
        return convertTrans(g.getTranslateY(),g.getTranslateX());
    }

    public void drawString(String drawString, int tx, int ty) {
        font.drawString(g,drawString, tx,ty, Graphics.TOP | Graphics.LEFT  );
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getStrokeStyle() {
        return g.getStrokeStyle();
    }

    public void setStrokeStyle(int stroke) {
        g.setStrokeStyle(stroke);
    }

    public void fillRoundRect(int x, int y, int w, int h, int a1, int a2) {
                g.fillRoundRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w),
                        a1,a2);
    }

    public void drawRoundRect(int x, int y, int w, int h, int a1, int a2) {
                g.drawRoundRect(
                        convertTrans(x,y),
                        convertTrans(y,x),
                        convertTrans(w,h),
                        convertTrans(h,w),
                        a1,a2);
    }

    public void fillArc(int x, int y, int width, int height, int angle, int arc) {
        g.fillArc(
                convertTrans(x,y),
                convertTrans(y,x),
                convertTrans(width,height),
                convertTrans(height,width), angle, arc);
    }

    public void drawArc(int x, int y, int width, int height, int angle, int arc) {
        g.drawArc(
                convertTrans(x,y),
                convertTrans(y,x),
                convertTrans(width,height),
                convertTrans(height,width), angle, arc);
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

    public int[] getClip() {
        return new int[] {getClipX(),getClipY(),getClipWidth(),getClipHeight()};
    }

    public void setClip(int[] clip) {
        setClip(clip[0], clip[1], clip[2], clip[3]);
    }

    public Font getFont() {
        return font;
    }

}

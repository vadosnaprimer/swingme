package net.yura.mobile.gui.plaf.aether;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;

/**
 * @author Nathan
 */
public class AetherBorder implements Border {

    int marginX;
    int marginY;

    int paddingX;
    int paddingY;

    Vector borders;

    int clip = CLIP_NONE;

    int gradientOrientation = ORIENTATION_VERT;

    public AetherBorder(Vector borders, int marginX, int marginY, int paddingX, int paddingY, int corner, int clip, int gradientOrientation) {
        this.borders = borders;
        this.marginX = marginX;
        this.marginY = marginY;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        this.clip = clip;
        this.gradientOrientation = gradientOrientation;
    }

    private void drawRoundedGradientRect(int c1, int c2, Graphics2D g, int x, int y, int w, int h, int corner, double reflection) {
        drawRoundedGradientRect(c1,c2,g,x,y,w,h,corner,reflection,clip,gradientOrientation);

    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {

        if (borders == null) return;

        int ch = g.getClipHeight();
        int cw = g.getClipWidth();
        int cx = g.getClipX();
        int cy = g.getClipY();

        int x = -getLeft()+marginX;
        int y = -getTop()+marginY;
        int w = width+getLeft()+getRight()-marginX*2;
        int h = height+getTop()+getBottom()-marginY*2;

        g.clipRect(x, y, w, h);

        if (clip != CLIP_NONE) {
            // Determine required crop size
            int corner = 0;
            for (int b = 0 ; b<borders.size() ; b++) {
                AetherBorderSetting border = (AetherBorderSetting) borders.elementAt(b);
                if (border.corner > corner) {
                    corner = border.corner;
                }
            }
            corner += borders.size();

            switch (clip) {
                case CLIP_BOTTOM: h = h+corner; break;
                case CLIP_TOP: h = h+corner; y=y-corner; break;
                case CLIP_RIGHT: w = w +corner; break;
                case CLIP_LEFT: w = w +corner; x=x-corner; break;
            }
        }
        
        // Draw the borders
        for (int b = 0 ; b<borders.size() ; b++) {
            AetherBorderSetting border = (AetherBorderSetting) borders.elementAt(b);
            drawRoundedGradientRect(border.color1,border.color2,g,x+b,y+b,w-(2*b),h-(2*b),border.corner,border.reflection);
        }

        g.setClip(cx, cy, cw, ch);
    }

    public int getTop() {
        return marginY+borders.size()-1+paddingY;
    }

    public int getBottom() {
        return marginY+borders.size()-1+paddingY;
    }

    public int getRight() {
        return marginX+borders.size()-1+paddingX;
    }

    public int getLeft() {
        return marginX+borders.size()-1+paddingX;
    }

    public boolean isBorderOpaque() {
        return true;
    }











        public static final int CLIP_NONE = 0;
    public static final int CLIP_TOP = 1;
    public static final int CLIP_LEFT = 2;
    public static final int CLIP_BOTTOM = 3;
    public static final int CLIP_RIGHT = 4;

    public static final int ORIENTATION_VERT = 0;
    public static final int ORIENTATION_HORI = 90;

    private static int getRed(int rgb) {
        return ((rgb >> 16) & 0xFF);
    }

    private static int getGreen(int rgb) {
        return ((rgb >> 8) & 0xFF);
    }

    private static int getBlue(int rgb) {
        return ((rgb >> 0) & 0xFF);
    }

    private static int getRGB(int r, int b, int g) {
        r = ((r & 0xFF) << 16);
        g = ((g & 0xFF) << 8);
        b = ((b & 0xFF) << 0);
        return (r|g|b);
    }

    public static int getGradientColor(int c1, int c2, int pos, int total,double reflection) {

        double max = (int) (total * reflection);

        double r1 = getRed(c1);
        double g1 = getGreen(c1);
        double b1 = getBlue(c1);

        double r2 = getRed(c2);
        double g2 = getGreen(c2);
        double b2 = getBlue(c2);

        double incrR = (r1-r2)/max;
        double incrG = (g1-g2)/max;
        double incrB = (b1-b2)/max;

        //System.out.println(incrR);

        int r;
        int g;
        int b;
        int offset;

        if (pos <= max) {
            offset = pos;
        }
        else {
            offset =(int) (max-(pos-max));
        }

        r = (int) (r1-(incrR*offset));
        g = (int) (g1-(incrG*offset));
        b = (int) (b1-(incrB*offset));

        if (r1>=255||g1>=255||b1>=255) {
            //System.out.println("white");
            //return 0x00FFFFFF;
        }

        //System.out.println(r+" "+g+" "+b);

        return getRGB(r, b, g);
    }

    public static int getShade(int color, double perc) {
        int r1 = getRed(color);
        int g1 = getGreen(color);
        int b1 = getBlue(color);

        r1 = (int) (r1*perc);
        g1 = (int) (g1*perc);
        b1 = (int) (b1*perc);

        //System.out.println(perc+" -> "+r1+" "+g1+" "+b1);

        if (r1>=255||g1>=255||b1>=255) {
            //System.out.println("white");
            return 0x00FFFFFF;
        }

        return getRGB(r1, b1, g1);
    }

    private static final int[] corner8 = { 0,4,2,1,1 };

    private static int getCornerLineWidth(int pos, int corner) {
        //double w = (double) (corner*(1-Math.cos(MathUtil.asin(pos/corner))));

        switch (corner) {
            case 0: return 0;
            case 1: return 1;
            case 2: return 1;
            case 3: return 1;
            case 4: return corner8[pos];
            //case 3: return corner3[pos];
        }

        //System.out.println(corner+" "+pos+" = "+w);

        //return (int) (corner-w);
//        if (pos == 0) return corner;
//        if (pos<corner) {
//            return 1;
//        }
        return 0;
    }

    public static void drawRoundedGradientRect(int c1, int c2, Graphics2D g, int x, int y, int width, int height, int corner, double reflection, int clip, int gradientOrientation) {

        //System.out.println("-----------------------");

//        if (c1 == c2) {
//            g.setColor(c1);
//            g.fillRoundRect(x, y, width, height, corner, corner);
//        }
//        else {
            int axis;
            int maxSize;
            if (gradientOrientation == ORIENTATION_VERT) {
                axis = height;
                maxSize = width;
            } else {
                axis = width;
                maxSize = height;
            }
            for (int i=0;i<axis;i++) {
                int w = maxSize;
                int offset = 0;
                if (i<corner) {
                    offset = getCornerLineWidth(i+1,corner);
                    w = maxSize-(offset*2);
                }
                if ((axis-i)<=corner) {
                    offset = getCornerLineWidth(axis-i,corner);
                    w = maxSize-(offset*2);
                }
                if ((c1 == c2) && ((i>=corner) && ((axis-i)>corner))) {
                    // FILL SOLID BLOCK
                    g.setColor(c1);
                    if (gradientOrientation == ORIENTATION_VERT) {
                        g.fillRect(x, i+y, maxSize, axis-(corner*2));
                    }
                    else {
                        g.fillRect(x+i, y, axis-(corner*2), maxSize);
                    }
                    i = axis-corner-1;
                }
                else {
                    // FILL GRADIENT LINE
                    w = w-1;
                    g.setColor(getGradientColor(c1,c2,i,axis,reflection));
                    if (gradientOrientation == ORIENTATION_VERT) {
                        g.drawLine(x+offset, y+i, x+offset+w, i+y);
                    }
                    else {
                        g.drawLine(x+i, y+offset, x+i, y+offset+w);
                    }
                }
            }
//        }

    }

    public static void drawRoundedGradientRect(int c1, int c2, Graphics2D g, int x, int y, int width, int height, int corner, int gradientOrientation) {
        drawRoundedGradientRect(c1, c2, g, x, y, width, height, corner,1.0,CLIP_NONE,gradientOrientation);
    }

    public static void drawRoundedGradientRect(int c1, int c2, Graphics2D g, int x, int y, int width, int height, int corner, double reflection, int gradientOrientation) {
        drawRoundedGradientRect(c1, c2, g, x, y, width, height, corner,reflection,CLIP_NONE,gradientOrientation);
    }

    public static void drawGradientRect(int c1, int c2, Graphics g, int x, int y, int width, int height, double reflection, int gradientOrientation) {
        if (c1 == c2) {
            g.setColor(c1);
            g.fillRect(x, y, width, height);
        }
        else {
            for (int i=0;i<height;i++) {
                g.setColor(getGradientColor(c1,c2,i,height,reflection));
                g.fillRect(x, i+y, width, 1);
            }
        }
    }
}


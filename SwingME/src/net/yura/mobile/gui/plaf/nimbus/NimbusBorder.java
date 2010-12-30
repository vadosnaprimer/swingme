package net.yura.mobile.gui.plaf.nimbus;

import java.util.Vector;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Nathan
 */
public class NimbusBorder implements Border {

    public static final int ORIENTATION_VERT = 0;
    public static final int ORIENTATION_HORI = 90;

    int marginX;
    int marginY;

    int paddingX;
    int paddingY;

    Vector borders;
    int[] borderSizes = new int[4];

    int gradientOrientation = ORIENTATION_VERT;

    public NimbusBorder(Vector borders, int marginX, int marginY, int paddingX, int paddingY) {
        this.borders = borders;
        this.marginX = marginX;
        this.marginY = marginY;
        this.paddingX = paddingX;
        this.paddingY = paddingY;

        initialise();
    }

    NimbusBorder(Vector borderSettings) {
        this(borderSettings,0,0,0,0);
    }

    private void initialise() {
        borderSizes[NimbusBorderSetting.TOP] = borderSize(NimbusBorderSetting.TOP);
        borderSizes[NimbusBorderSetting.BOTTOM] = borderSize(NimbusBorderSetting.BOTTOM);
        borderSizes[NimbusBorderSetting.LEFT] = borderSize(NimbusBorderSetting.LEFT);
        borderSizes[NimbusBorderSetting.RIGHT] = borderSize(NimbusBorderSetting.RIGHT);
    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {

        if (borders == null) return;

        int x = -getLeft()+marginX;
        int y = -getTop()+marginY;
        int w = width+getLeft()+getRight()-marginX*2;
        int h = height+getTop()+getBottom()-marginY*2;

        paintBorders(borders, g, x, y, w, h);
    }

    public static void paintBorders(Vector borders, Graphics2D g, int x, int y, int w, int h) {

        if (borders == null || w<=0 || h<=0) return;

        // Draw the borders
        int borderX = x;
        int borderY = y;
        int borderWidth = w;
        int borderHeight = h;

        for (int b = 0 ; b<borders.size() ; b++) {
            NimbusBorderSetting border = (NimbusBorderSetting) borders.elementAt(b);


            // TODO: only draw visible part of border?
            drawRoundedGradientRect(border.color1, border.color2,
                    g,
                    borderX,
                    borderY,
                    borderWidth,
                    borderHeight,
                    border.corner, border.reflection, border.gradientOrientation);

            // Set sizes for the next border
            borderX += border.thickness[NimbusBorderSetting.RIGHT];
            borderY += border.thickness[NimbusBorderSetting.TOP];
            borderWidth -= border.thickness[NimbusBorderSetting.RIGHT]+border.thickness[NimbusBorderSetting.LEFT];
            borderHeight -= border.thickness[NimbusBorderSetting.TOP]+border.thickness[NimbusBorderSetting.BOTTOM];

        }
    }

    private int borderSize(int position) {
        // Remember inner border conts as background, hence -1...
        int size = 0;
        for (int i=0;i<borders.size()-1;i++) {
            NimbusBorderSetting border = (NimbusBorderSetting)borders.elementAt(i);
            size += border.thickness[position];
        }
        return size;
    }

    public int getTop() {
        return marginY+borderSizes[NimbusBorderSetting.TOP]+paddingY;
    }

    public int getBottom() {
        return marginY+borderSizes[NimbusBorderSetting.BOTTOM]+paddingY;
    }

    public int getRight() {
        return marginX+borderSizes[NimbusBorderSetting.RIGHT]+paddingX;
    }

    public int getLeft() {
        return marginX+borderSizes[NimbusBorderSetting.LEFT]+paddingX;
    }

    public boolean isBorderOpaque() {
        return true;
    }





    public static int getRed(int rgb) {
        return ((rgb >> 16) & 0xFF);
    }

    public static int getGreen(int rgb) {
        return ((rgb >> 8) & 0xFF);
    }

    public static int getBlue(int rgb) {
        return ((rgb >> 0) & 0xFF);
    }

    public static int getRGB(int r, int b, int g) {
        r = ((r & 0xFF) << 16);
        g = ((g & 0xFF) << 8);
        b = ((b & 0xFF) << 0);
        return r|g|b | 0xFF000000;
    }

    public static int[] getGradientColors(int c1, int c2, int total, double reflection) {

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

        //Logger.debug(incrR);

        int r;
        int g;
        int b;
        int offset;
        int[] gradient = new int[total];

        for (int i=0;i<total;i++) {
            if (i <= max) {
                offset = i;
            }
            else {
                offset =(int) (max-(i-max));
            }

            r = (int) (r1-(incrR*offset));
            g = (int) (g1-(incrG*offset));
            b = (int) (b1-(incrB*offset));
            gradient[i] = getRGB(r, b, g);

            //Logger.debug(r+" "+g+" "+b);
        }
        
        return gradient;
    }

    private static final int[] corner2 = { 0,2,1 };
    private static final int[] corner3 = { 0,3,1,1 };
    private static final int[] corner4 = { 0,4,2,1,1 };

    private static int getCornerLineWidth(int pos, int corner) {

        switch (corner) {
            case 0: return 0;
            case 1: return 1;
            case 2: return corner2[pos];
            case 3: return corner3[pos];
            case 4: return corner4[pos];
        }

        return 0;
    }

    /**
     *
     * @param c1
     * @param c2
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     * @param corners: top left, top right, bottom right, bottom left
     * @param reflection
     * @param clip
     * @param gradientOrientation
     */
    public static void drawRoundedGradientRect(int c1, int c2, Graphics2D g, int x, int y, int width, int height, int[] corners, double reflection, int gradientOrientation) {

        //Logger.debug("-----------------------");

        if ((c1 == Style.NO_COLOR) && (c2 == Style.NO_COLOR)) {
            return;
        }

        int axis;
        int maxSize;
        int tl; int tr; int bl; int br;

        if (gradientOrientation == ORIENTATION_VERT) {
            axis = height;
            maxSize = width;
            tl = 0;
            tr = 1;
            bl = 2;
            br = 3;
        } else {
            axis = width;
            maxSize = height;
            tl = 0;
            tr = 2;
            bl = 1;
            br = 3;
        }

        int maxCornerPre = (corners[tl]>corners[tr])?corners[tl]:corners[tr];
        int maxCornerPost = (corners[bl]>corners[br])?corners[bl]:corners[br];

        int[] gradient = getGradientColors(c1, c2, axis, reflection);

        for (int i=0;i<axis;i++) {
            boolean rounded = false; // is current line in the rounded section
            int w = maxSize;
            int offsetPre = 0;
            int offsetPost = 0;
            if (i<maxCornerPre) {
                offsetPre = getCornerLineWidth(i+1,corners[tl]);
                offsetPost = getCornerLineWidth(i+1,corners[tr]);
                rounded = true;
            }
            if ((axis-i)<=maxCornerPost) {
                offsetPre = getCornerLineWidth(axis-i,corners[bl]);
                offsetPost = getCornerLineWidth(axis-i,corners[br]);
                rounded = true;
            }
            w = maxSize-offsetPre-offsetPost;
            if ((c1 == c2) && (!rounded)) {
                // FILL SOLID BLOCK
                g.setColor(c1);
                int blockSize = axis-maxCornerPre-maxCornerPost;
                if (gradientOrientation == ORIENTATION_VERT) {
                    g.fillRect(x, i+y, maxSize, blockSize);
                }
                else {
                    g.fillRect(x+i, y, blockSize, maxSize);
                }
                i = axis-maxCornerPost-1;
            }
            else {
                // FILL GRADIENT LINE
                w = w-1;
                g.setColor(gradient[i]);
                if (gradientOrientation == ORIENTATION_VERT) {
                    g.drawLine(x+offsetPre, y+i, x+offsetPre+w, i+y);
                }
                else {
                    g.drawLine(x+i, y+offsetPre, x+i, y+offsetPre+w);
                }
            }
        }

    }

    public static void drawRoundedGradientRect(int c1, int c2, Graphics2D g, int x, int y, int width, int height, int corner, double reflection, int gradientOrientation) {
        int[] corners = {corner,corner,corner,corner};
        drawRoundedGradientRect(c1, c2, g, x, y, width, height, corners, reflection, gradientOrientation);
    }

    public static void drawRoundedGradientRect(int c1, int c2, Graphics2D g, int x, int y, int width, int height, int corner, int gradientOrientation) {
        drawRoundedGradientRect(c1, c2, g, x, y, width, height, corner,1.0,gradientOrientation);
    }

}


package com.nokia.mid.ui;

import javax.microedition.lcdui.Graphics;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;

public class DirectGraphics {

    private Graphics g;

    public DirectGraphics(Graphics graphics) {
        g = graphics;
    }

    public void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor) {

        int tx = g.getTranslateX();
        int ty = g.getTranslateY();
        
        Path path = new Path();

        path.moveTo( tx + xPoints[xOffset++], ty + yPoints[yOffset++]);
        for (int c=1;c<nPoints;c++) {
            path.lineTo( tx + xPoints[xOffset++], ty + yPoints[yOffset++]);
        }
        path.close();

        Paint paint = new Paint();
        paint.setColor(argbColor);
        paint.setStyle(Style.FILL);

        g.getCanvas().drawPath(path, paint);
    }

    public void setARGBColor(int rgb) {
        g.getPaint().setColor(rgb);
    }

}

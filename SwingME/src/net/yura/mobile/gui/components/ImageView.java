package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;

public class ImageView extends Component {

    private Icon bgImage;
    protected int imgW, imgH;
    boolean consumingMotionEvents;

    private int startPinchSize;
    private double startPinchX, startPinchY;
    private double ratio = 1.0;

//    private int[] px;
//    private int[] py;

    public ImageView() {
        focusable = false;
    }


    public void setBackgroundImage(Icon backgroundImage) {
        this.bgImage = backgroundImage;

        if (bgImage != null) {
            imgW = bgImage.getIconWidth();
            imgH = bgImage.getIconHeight();
        }

    }

    public Icon getBackgroundImage() {
        return bgImage;
    }

    // Override
    protected void workoutMinimumSize() {
        width = (bgImage == null) ? 1 : 100;
        height = (bgImage == null) ? 1 : 100;
    }

    // Override
    public boolean consumesMotionEvents() {
        return consumingMotionEvents;
    }

    protected double getScale() {
        return Math.min(getHeight()/(double)imgH,getWidth()/(double)imgW);
    }

    protected int getImgX(double s) {
        return (int) (getWidth() - (imgW * s)) / 2;
    }

    protected int getImgY(double s) {
        return (int) (getHeight() - (imgH * s)) / 2;
    }

    // Override
    public void paintComponent(Graphics2D g) {

        if (bgImage != null) {

            double ratio = getScale();
            int imgX = getImgX(ratio);
            int imgY = getImgY(ratio);
            Image img = bgImage.getImage();

            if (img == null) {
                bgImage.paintIcon(this, g, imgX, imgY);
            } else {
                // in current version of BB, we can not scale images on the fly
                //if (Midlet.getPlatform()==Midlet.PLATFORM_BLACKBERRY) {
                //	System.out.println(">> Ratio: " + Double.toString(ratio));
                //    if (ratio > 1.1 || ratio < 0.9) {
                //        img = ImageUtil.scaleImage(img, (int)(imgW * ratio), (int)(imgH * ratio) );
                //        setBackgroundImage( new Icon(img) );
                //    }
                //    g.drawImage(img, imgX, imgY);
                //}
                //else {
                    g.drawScaledImage( img, imgX, imgY, (int)(imgW * ratio), (int)(imgH * ratio));
                //}
            }            
        }


//        g.translate(imgX, imgY);
//        g.getGraphics().scale(ratio, ratio);
//        bgImage.paintIcon(this, g, 0, 0);
//        g.getGraphics().scale(1 / ratio, 1 / ratio);
//        g.translate(-imgX, -imgY);
//
//        if (px != null) {
//            g.setColor(0xFFFF0000);
//            g.drawRect(Math.min(px[0], px[1]),
//                       Math.min(py[0], py[1]),
//                       Math.max(1, Math.abs(px[1] - px[0])),
//                       Math.max(1, Math.abs(py[1] - py[0])));
//        }
    }

    // Override
    public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
        // We handle all our Mouse events
    }

    // Override
    public void processMultitouchEvent(int[] type, int[] x, int[] y) {

        consumingMotionEvents = (type[0] != DesktopPane.RELEASED );

        if (type.length >= 2) {
//            px = x;
//            py = y;

            if (type[0] == DesktopPane.PRESSED || type[1] == DesktopPane.PRESSED) {

                // Stop any animation
                animateToFit(false);

                ratio = getScale();

                startPinchSize = getDistance(x, y);

                int imgX = getImgX(ratio);
                int imgY = getImgY(ratio);

                startPinchX = (((x[0] + x[1]) / 2) - imgX) / ratio;
                startPinchY = (((y[0] + y[1]) / 2) - imgY) / ratio;

                setLocation(posX + imgX, posY + imgY);

                width = (int) (imgW * ratio);
                height = (int) (imgH * ratio);

                //System.out.println("PRESSED " + startPinchSize);
            }
            else {
                int pinchSize = getDistance(x, y);
                int pinchDiff = (pinchSize - startPinchSize);

                //System.out.println("DRAGGED/RELEASED " + pinchSize);

                int newW = (int)(imgW*ratio) + pinchDiff;
                int newH = (imgH * newW) / imgW;

                // TODO: Should check the new w/h bounds... e.g. not zooming more
                // than 2x screen size, or img size... and less than 90% of the
                // sreen?

                width = newW;
                height = newH;

                // here we assume that the panel is already the same size as the image!!
                double nratio = getScale(); // TODO ????  width/(double)imgW;

                int endPinchX = (x[0] + x[1]) / 2;
                int endPinchY = (y[0] + y[1]) / 2;

                int difx = (int)(startPinchX*nratio - endPinchX);
                int dify = (int)(startPinchY*nratio - endPinchY);

                setLocation(posX - difx, posY - dify);
            }

            if (type[0] == DesktopPane.RELEASED || type[1] == DesktopPane.RELEASED) {
                // Start animation
                animateToFit(true);
            }
        }

        // TODO: Remove?
        Component cmp = (getParent() == null) ? this : getParent();
        cmp.repaint();
    }

    private int getDistance(int[] x, int[] y) {
        int dx = x[0] - x[1];
        int dy = y[0] - y[1];
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    // Override
    protected String getDefaultName() {
        return "ImageView";
    }

    private void animateToFit(boolean startAnimation) {
        Component sp = getParent();
        if (sp instanceof ScrollPane) {
            ((ScrollPane)sp).animateToFit(startAnimation);
        }
    }

}

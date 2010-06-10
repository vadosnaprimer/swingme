package net.yura.mobile.gui.components;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;

public class ImageView extends Component {

    private Icon bgImage;
    private Icon bgScaledImage;
    private int imgX, imgY;
    private int imgW, imgH;
    private int imgScaledW, imgScaledH;
    boolean consumingMotionEvents;


    public void setBackgroundImage(Icon backgroundImage) {
        this.bgImage = backgroundImage;
        this.bgScaledImage = null;

        //DELETE:
        setBackground(0xFF0000FF);
    }

    public Icon getBackgroundImage() {
        return bgImage;
    }

    // Override
    public void workoutMinimumSize() {
        // TODO Auto-generated method stub
        width = 1000;
        height = 1000;
    }

    // Override
    public boolean consumesMotionEvents() {
        return consumingMotionEvents;
    }

    // Override
    public void paintComponent(Graphics2D g) {

        // TODO: bgScaledImage is not thread safe...

        if (bgScaledImage == null && bgImage != null) {
            this.bgScaledImage = bgImage;

            imgW = bgImage.getIconWidth();
            imgH = bgImage.getIconHeight();
            imgScaledW = imgW;
            imgScaledH = imgH;

//            imgX = (getWidth() - imgScaledW) / 2;
//            imgY = (getHeight() - imgScaledH) / 2;
        }

        if (bgScaledImage != null) {
            bgScaledImage.paintIcon(this, g, imgX, imgY);
        }

        g.setColor(0xFF00FF00);
        g.drawRect(imgX, imgY, imgScaledW, imgScaledH);

        if (px != null) {
            g.setColor(0xFFFF0000);
            g.drawRect(Math.min(px[0], px[1]),
                       Math.min(py[0], py[1]),
                       Math.max(1, Math.abs(px[1] - px[0])),
                       Math.max(1, Math.abs(py[1] - py[0])));
        }
    }

    int startPinchSize;

    int[] px;
    int[] py;

    // Override
    public void processMultitouchEvent(int[] type, int[] x, int[] y) {
        System.out.println("ImageView: pointerEvent");

        consumingMotionEvents = (type[0] != DesktopPane.RELEASED);

        if (type.length >= 2) {
            px = x;
            py = y;

            if (type[0] == DesktopPane.PRESSED || type[1] == DesktopPane.PRESSED) {

                startPinchSize = getDistance(x, y);

                System.out.println("PRESSED " + startPinchSize);
            }
            else {
                int pinchSize = getDistance(x, y);
                int pinchDiff = (pinchSize - startPinchSize);

                if (pinchDiff > 2 || pinchDiff < -2) {

                    int newW = imgScaledW + pinchDiff;
                    int newH = (imgH * newW) / imgW;

                    if (newW > 20 && newW < getWidth() &&
                        newH > 20 && newW < getHeight()) {

                        imgScaledW = newW;
                        imgScaledH = newH;
                        //imgX += pinchDiff / 2;
                        //imgY += pinchDiff;
                    }
                    startPinchSize = pinchSize;
                }

                System.out.println("DRAGGED/RELEASED " + pinchSize);
            }
        }

        // TODO: Remove?
        repaint();
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
}

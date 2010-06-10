package net.yura.mobile.gui.components;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;

public class ImageView extends Component {

    private Icon bgImage;
    private int imgW, imgH;
    boolean consumingMotionEvents;


    public void setBackgroundImage(Icon backgroundImage) {
        this.bgImage = backgroundImage;

        if (bgImage != null) {
            imgW = bgImage.getIconWidth();
            imgH = bgImage.getIconHeight();
        }

        //DELETE:
        setBackground(0xFF0000FF);
    }

    public Icon getBackgroundImage() {
        return bgImage;
    }

    // Override
    public void workoutMinimumSize() {
        // TODO Auto-generated method stub
        width = 100;
        height = 100;
    }

    // Override
    public boolean consumesMotionEvents() {
        return consumingMotionEvents;
    }

    // Override
    public void paintComponent(Graphics2D g) {

        double ratio = Math.min(getHeight()/(double)imgH,getWidth()/(double)imgW);
        int imgX = (int) (getWidth() - (imgW * ratio)) / 2;
        int imgY = (int) (getHeight() - (imgH * ratio)) / 2;

        g.translate(imgX, imgY);
        g.getGraphics().scale(ratio, ratio);

        bgImage.paintIcon(this, g, 0, 0);

        g.getGraphics().scale(1 / ratio, 1 / ratio);
        g.translate(-imgX, -imgY);

        g.setColor(0xFF00FF00);
        g.drawRect(0, 0, width, height);

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

        consumingMotionEvents = (type[0] != DesktopPane.RELEASED);

        System.out.println("ImageView: pointerEvent " + " consumingMotionEvents = " + consumingMotionEvents);

        if (type.length >= 2) {
            px = x;
            py = y;

            int pinchDiff = 0;

            double ratio = Math.min(getHeight()/(double)imgH,getWidth()/(double)imgW);

            if (type[0] == DesktopPane.PRESSED || type[1] == DesktopPane.PRESSED) {

                startPinchSize = getDistance(x, y);

                int imgX = (int) (getWidth() - (imgW * ratio)) / 2;
                int imgY = (int) (getHeight() - (imgH * ratio)) / 2;

                posX = posX + imgX;
                posY = posY + imgY;

                System.out.println("PRESSED " + startPinchSize);
            }
            else {
                int pinchSize = getDistance(x, y);
                pinchDiff = (pinchSize - startPinchSize);
                startPinchSize = pinchSize;

                System.out.println("DRAGGED/RELEASED " + pinchSize);
            }

            int newW = (int)(imgW*ratio) + pinchDiff;
            int newH = (imgH * newW) / imgW;

            // TODO: Should check the new w/h bounds... e.g. not zooming more
            // than 2x screen size, or img size... and less than 90% of the
            // sreen?

            width = newW;
            height = newH;

            if (type[0] == DesktopPane.RELEASED || type[1] == DesktopPane.RELEASED) {

                if (getParent() instanceof ScrollPane) {
                    ((ScrollPane)getParent()).animateToFit();
                }

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
}

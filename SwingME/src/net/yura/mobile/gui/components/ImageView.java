package net.yura.mobile.gui.components;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;

public class ImageView extends Panel {
    private Icon bgImage;
    private Icon bgScaledImage;

    public void setBackgroundImage(Icon backgroundImage) {
        this.bgImage = backgroundImage;
        this.bgScaledImage = backgroundImage;

        //DELETE:
        setBackground(0xFF0000FF);
    }

    public Icon getBackgroundImage() {
        return bgImage;
    }

    // Override
    public void paintComponent(Graphics2D g) {

        if (bgScaledImage != null) {
            int x = (getWidth() - bgScaledImage.getIconWidth()) / 2;
            int y = (getHeight() - bgScaledImage.getIconHeight()) / 2;
            bgScaledImage.paintIcon(this, g, x, y);
        }

        g.setColor(0xFF000000);
        g.drawRect(0, 0, 100, 100);
        g.setColor(0xFF00FF00);

        g.drawRect(0, 0, imagePinchChange + 100, imagePinchChange + 100);

        g.setColor(0xFFFF0000);
        g.drawRect(Math.min(px[0], px[1]),
                   Math.min(py[0], py[1]),
                   Math.max(1, Math.abs(px[1] - px[0])),
                   Math.max(1, Math.abs(py[1] - py[0])));

        super.paintComponent(g);
    }

    // Override
    public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
        System.out.println("ImageView: processMouseEvent");
        // TODO Auto-generated method stub
        super.processMouseEvent(type, x, y, keys);
    }

    int startPinchSize;
    int startPinchChange;
    int imagePinchChange;

    int[] px = new int[2];
    int[] py = new int[2];

    // Override
    public void pointerEvent(int[] type, int[] x, int[] y) {
        System.out.println("ImageView: pointerEvent");

        if (type.length >= 2) {
            px[0] = x[0];
            px[1] = x[1];

            py[0] = y[0];
            py[1] = y[1];

            if (type[0] == DesktopPane.PRESSED || type[1] == DesktopPane.PRESSED) {

                startPinchSize = getDistance(x, y);
                startPinchChange = imagePinchChange;

                System.out.println("PRESSED " + startPinchSize);
            }
            else if (type[0] == DesktopPane.RELEASED || type[1] == DesktopPane.RELEASED) {
                int pinchSize = getDistance(x, y);
                imagePinchChange = startPinchChange + (pinchSize - startPinchSize);

                System.out.println("RELEASED " + pinchSize);
            }
            else { // DRAGGED
                int pinchSize = getDistance(x, y);
                imagePinchChange = startPinchChange + (pinchSize - startPinchSize);

                System.out.println("DRAGGED " + pinchSize);
            }

            //TODO: needs upper/lower limit
            imagePinchChange = Math.min(imagePinchChange, getWidth() - 110);
            imagePinchChange = Math.max(imagePinchChange, 0);
        }

        repaint();
        // TODO Auto-generated method stub
        super.pointerEvent(type, x, y);
    }

    private int getDistance(int[] x, int[] y) {
        int dx = x[0] - x[1];
        int dy = y[0] - y[1];
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
}

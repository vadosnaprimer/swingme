package net.yura.mobile.test;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.test.MainPane.Section;


/**
 *
 * @author Administrator
 */
public class GraphicsTest extends Section {

    private Panel info;


    public void createTests() {
        // service link
        addTest("Graphics Draw Region", "drawRegion");
        addTest("Water Ripple", "waterRipple");
        addTest("Draw Offscreen", "drawOffscreen");
    }

    public void openTest(String actionCommand) {
        if (info == null) {
            info = new Panel(new BorderLayout());
        } else {
            info.removeAll();
        }

        if ("drawRegion".equals(actionCommand)) {
            DrawRegionPanel drawRegionPanel = new DrawRegionPanel();

            Button prevButton = new Button("< Prev");
            prevButton.setActionCommand("Prev");
            prevButton.addActionListener(drawRegionPanel);

            Button nextButton = new Button("Next >");
            nextButton.setActionCommand("Next");
            nextButton.addActionListener(drawRegionPanel);

            Panel topPanel = new Panel(new FlowLayout());
            topPanel.add(prevButton);
            topPanel.add(drawRegionPanel.getTitle());
            topPanel.add(nextButton);

            info.add(topPanel, Graphics.TOP);
            info.add(drawRegionPanel);

            addToScrollPane(info, null );
        }
        else if ("waterRipple".equals(actionCommand)) {
            System.out.println(">>> waterRipple");

            WaterPanel drawRegionPanel = new WaterPanel();
            info.add(drawRegionPanel);

            addToScrollPane(info, null );
        }
        else if ("drawOffscreen".equals(actionCommand)) {

            Image img = Image.createImage(50, 50);

            Graphics g = img.getGraphics();

            g.drawImage( mainPane.image.getImage() , 5, 5, 0);
            g.setColor(0xFFFF0000);
            g.drawLine(0, 0, 50, 50);

            Label drawRegionPanel = new Label(new Icon(img));
            addToScrollPane(drawRegionPanel, null );
            
        }
    }

    class DrawRegionPanel extends Panel implements ActionListener {
        Image img;
        int transfIdx = 0;
        Label title = new Label("NONE");

        final int[] ANCHORS = {
                Graphics.TOP | Graphics.LEFT, Graphics.TOP | Graphics.HCENTER, Graphics.TOP | Graphics.RIGHT,
                Graphics.VCENTER | Graphics.LEFT, Graphics.VCENTER | Graphics.HCENTER, Graphics.VCENTER | Graphics.RIGHT,
                Graphics.BOTTOM | Graphics.LEFT, Graphics.BOTTOM | Graphics.HCENTER, Graphics.BOTTOM | Graphics.RIGHT
        };
        final String[] ANCHORS_STRS = {
                "top|left", "top|hcen", "top|right",
                "vcen|left", "vcen|hcen", "vcen|right",
                "bot|left", "bot|hcen", "bot|right"
        };

        final int[] TRANSFORMS = {
                Sprite.TRANS_NONE, Sprite.TRANS_ROT90, Sprite.TRANS_ROT180, Sprite.TRANS_ROT270,
                Sprite.TRANS_MIRROR, Sprite.TRANS_MIRROR_ROT90, Sprite.TRANS_MIRROR_ROT180, Sprite.TRANS_MIRROR_ROT270,
        };
        final String[] TRANSFORMS_STRS = {
                "NONE", "ROT90", "ROT180", "ROT270",
                "MIRROR", "MIRROR_90", "MIRROR_180", "MIRROR_270",
        };


        public void paint(Graphics2D g) {
            super.paint(g);

            if (img == null) {
                try {
                    img = Image.createImage("/TestSprite.png");
                } catch (Exception e) {
                    return;
                }
            }

            for (int i = 0; i < ANCHORS_STRS.length; i++) {
                int x = (i % 3) * (getWidth() / 3);
                int y = (i / 3) * (getHeight() / 3);

                g.setColor(0xFF000000);
                g.drawString(ANCHORS_STRS[i], x, y);

                x += (getWidth() / 6);
                y += (getHeight() / 6);
                drawImage(g, x, y, img, TRANSFORMS[transfIdx], ANCHORS[i]);
            }
        }

        private void drawImage(Graphics2D g, int x, int y, Image img, int transform, int anchor) {

            g.getGraphics().drawRegion(img, 4, 4, img.getWidth() - 8, img.getHeight() - 8, transform, x, y, anchor);

            g.drawLine(x, y - 5, x, y + 5);
            g.drawLine(x - 5, y, x + 5, y);
        }

        public void actionPerformed(String actionCommand) {
            if ("Prev".equals(actionCommand)) {
                transfIdx--;
            }
            else if ("Next".equals(actionCommand)) {
                transfIdx++;
            }

            if (transfIdx < 0) {
                transfIdx = TRANSFORMS_STRS.length - 1;
            }
            else if (transfIdx >= TRANSFORMS_STRS.length) {
                transfIdx = 0;
            }

            title.setText(TRANSFORMS_STRS[transfIdx]);
            getWindow().revalidate();
            getWindow().repaint();
        }

        public Label getTitle() {
            return title;
        }
    }


    class WaterPanel extends Panel {

        private Image img;
        private short ripplemap1[], ripplemap2[];
        private int texture[];
        private int ripple[];

        private int imgWidth, imgHeight;


        WaterPanel() {
            try {
                img = Image.createImage("/swingme_logo.png");
                imgWidth = img.getWidth();
                imgHeight = img.getHeight();

                ripplemap1 = new short[imgWidth * imgHeight];
                ripplemap2 = new short[imgWidth * imgHeight];
                ripple = new int[imgWidth * imgHeight];

                texture = new int[imgWidth * imgHeight];
                img.getRGB(texture, 0, imgWidth, 0, 0, imgWidth, imgHeight);

                disturb(100, 100);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            System.out.println("img = " + img);
        }

        private void disturb(int dx, int dy) {
            int riprad = 3;

            for (int j = dy - riprad; j < dy + riprad; j++) {
                for (int k = dx - riprad; k < dx + riprad; k++) {
                    if (j >= 0 && j < imgHeight && k >= 0 && k < imgWidth) {
                        ripplemap1[(j * imgWidth) + k] += 512;
                    }
                }
            }
        }

        private void newframe() {

            short[] tmp = ripplemap1;
            ripplemap1 = ripplemap2;
            ripplemap2 = tmp;

            for (int y = 1; y < imgHeight - 1; y++) {
                int i = y * imgWidth;
                for (int x = 1; x < imgWidth - 1; x++) {
                    i++;
                    int data =
                        ripplemap2[i-1]+
                        ripplemap2[i+1]+
                        ripplemap2[i-imgWidth]+
                        ripplemap2[i+imgWidth];

                    data = (data >> 1) - ripplemap1[i];
                    data -= (data >> 5);
                    ripplemap1[i] = (short) data;

                    data = data >> 4;
                    // offsets
                    int a = x + data;
                    int b = y + data;

                    // bounds check
                    if (data > 0) {
                        if (a >= imgWidth)
                            a = imgWidth - 1;

                        if (b >= imgHeight)
                            b = imgHeight - 1;
                    } else {
                        if (a < 0)
                            a = 0;
                        if (b < 0)
                            b = 0;
                    }

                    ripple[i] = texture[a + (b * imgWidth)];
                }
            }
        }

        public boolean isOpaque() {
            return true;
        }

        public void paintComponent(Graphics2D g) {
            // TODO Auto-generated method stub
//            super.paintComponent(g);

//            long t0 = System.currentTimeMillis();
            newframe();
//            long t1 = System.currentTimeMillis();
            g.getGraphics().drawRGB(ripple, 0, imgWidth, 0, 0, imgWidth, imgHeight, false);
//            long t2 = System.currentTimeMillis();

//            System.out.println("t1 = " + (t1 - t0) + " t2 = " + (t2 - t1));

            repaint();
        }

        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
            if (type == DesktopPane.PRESSED || type == DesktopPane.DRAGGED) {
                disturb(x, y);
            }
        }
    }
}

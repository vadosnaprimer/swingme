package net.yura.mobile.test;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Graphics2D;
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

}

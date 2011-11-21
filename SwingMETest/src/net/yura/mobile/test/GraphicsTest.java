package net.yura.mobile.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.io.FileUtil;
import net.yura.mobile.io.HTTPClient;
import net.yura.mobile.io.SocketClient;
import net.yura.mobile.test.MainPane.Section;
import net.yura.mobile.util.ImageUtil;

/**
 *
 * @author Yura & Jane
 */
public class GraphicsTest extends Section {

    private Panel info;


    public void createTests() {
        // service link
        addTest("Graphics Draw Region", "drawRegion");
        addTest("Graphics Draw Image", "drawImage");
        addTest("Water Ripple", "waterRipple");
        addTest("Draw Offscreen", "drawOffscreen");
        addTest("Scale Image", "scaleImage");
        addTest("Image from URL", "urlImage");
    }

    public void openTest(String actionCommand) {
        if (info == null) {
            info = new Panel(new BorderLayout());
        } else {
            info.removeAll();
        }

        boolean isDrawRegion = "drawRegion".equals(actionCommand);
        if (isDrawRegion || "drawImage".equals(actionCommand)) {

            DrawRegionPanel drawRegionPanel = new DrawRegionPanel(isDrawRegion);

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

            g.drawImage(mainPane.image.getImage(), 5, 5, 0);
            g.setColor(0xFFFF0000);
            g.drawLine(0, 0, 50, 50);


            // test saving file to disk
            try {
                //OutputStream out=FileUtil.getWriteFileConnection("file:///root1/img.jpg").openOutputStream();

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                ImageUtil.saveImage(img,out);
                out.close();

                img = Image.createImage(out.toByteArray(), 0, out.size());
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }


            // testing Nokia DirectGraphics
            //com.nokia.mid.ui.DirectGraphics g2 = com.nokia.mid.ui.DirectUtils.getDirectGraphics(g);
            //g2.fillPolygon(new int[] { 25,35,45,25,5},0,new int[] { 25,5,25,45,35},0,5,0xAA115599);

            Label drawRegionPanel = new Label(new Icon(img));
            addToScrollPane(drawRegionPanel, null);
        }
        else if ("scaleImage".equals(actionCommand)) {

            Panel scaleImagePanel = new Panel(new BoxLayout(Graphics.VCENTER));
            try {
                Image img = Image.createImage("/swingme_logo.png");
                Image icon = ImageUtil.scaleImage(img, img.getWidth() / 3, img.getHeight() / 3);

                scaleImagePanel.add(new Label(new Icon(img)));
                scaleImagePanel.add(new Label(new Icon(icon)));

            } catch (Exception e) {
                scaleImagePanel.add(new Label("Can't load image..."));
            }

            addToScrollPane(scaleImagePanel, null);
        }
        else if ("urlImage".equals(actionCommand)) {

            final Panel scaleImagePanel = new ScrollPane(new Label("loading..."));



            new Thread() {
            	public void run() {

                    try {
                        final Label l1 = new Label();

                        String url = "http://swingme.sourceforge.net/swingme_logo.png";

                        Image img = loadImage(url);
                        l1.setIcon( new Icon(img) ) ;

                        scaleImagePanel.add(l1);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();

                        TextArea ta = new TextArea();
                        ta.setLineWrap(true);
                        ta.setFocusable(false);
                    	ta.setText(ex.toString());

                        scaleImagePanel.add(ta);
                    }
                    scaleImagePanel.revalidate();
                    scaleImagePanel.repaint();

            	};
            }.start();



            addToScrollPane(scaleImagePanel, null);
        }
        else {
            OptionPane.showMessageDialog(null, "unknown action: "+actionCommand, "error", OptionPane.WARNING_MESSAGE);
        }
    }

    public static Image loadImage(String url) throws Exception {
        HttpConnection hpc = null;
        InputStream dis = null;
        try {

            if (SocketClient.connectAppend!=null) {
                url = url + SocketClient.connectAppend;
            }

          hpc = (HttpConnection) Connector.open(url);
          int code = hpc.getResponseCode();
          if (code == HttpConnection.HTTP_OK) {
              int length = (int) hpc.getLength();
              dis = hpc.openInputStream();
              byte[] data = net.yura.mobile.util.SystemUtil.getData(dis, length);
              return Image.createImage(data, 0, data.length);
          }
          else {
              throw new Exception("HTTP ERROR WITH CODE: "+code);
          }
        }
        finally {
          if (hpc != null)
            hpc.close();
          if (dis != null)
            dis.close();
        }
  }

    class DrawRegionPanel extends Panel implements ActionListener {
        Image img;
        int transfIdx = 0;
        Label title = new Label("NONE");
        boolean isDrawRegion;

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

        DrawRegionPanel(boolean isDrawRegion) {
            this.isDrawRegion = isDrawRegion;
        }

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

            int x_off=4;
            int y_off=9;

            if (isDrawRegion) {
                g.getGraphics().drawRegion(img, x_off, y_off, img.getWidth() - 2*x_off, img.getHeight() - 2*y_off, transform, x, y, anchor);
            } else {
                Image img1 = Image.createImage(img, x_off, y_off, img.getWidth() - 2*x_off, img.getHeight() - 2*y_off, transform);
                g.getGraphics().drawImage(img1, x, y, anchor);
            }

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

        long lastDrawTime;
        int fps;
        int nFrames;


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

                lastDrawTime = System.currentTimeMillis();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            System.out.println("img = " + img);
        }

        private void disturb(int dx, int dy) {
            int riprad = 3;

            for (int j = dy - riprad; j < dy + riprad; j++) {
                for (int k = dx - riprad; k < dx + riprad; k++) {
                    if (j > 0 && j < imgHeight - 1 && k > 0 && k < imgWidth - 1) {
                        ripplemap1[(j * imgWidth) + k] += 512;
                    }
                }
            }
        }

        private void newframe() {

            // Speed up: access all class fields, using local method variables
            int imgW = imgWidth;
            int imgWMax = imgWidth - 1;
            int imgHMax = imgHeight - 1;
            int[] ripple = this.ripple;
            int[] texture = this.texture;

            // Swap
            short[] map1 = ripplemap2;
            short[] map2 = ripplemap1;
            ripplemap1 = map1;
            ripplemap2 = map2;

            for (int y = 1; y < imgHMax; y++) {
                int i = y * imgW;
                for (int x = 1; x < imgWMax; x++) {
                    i++;
                    int data = map2[i-1] + map2[i+1] + map2[i-imgW] + map2[i+imgW];

                    data = (data >> 1) - map1[i];
                    data -= (data >> 5);
                    map1[i] = (short) data;

                    // Calculate refraction
                    data = data >> 4;
                    int a = x + data;
                    int b = y + data;

                    try {
                        // Speed up: by default assume we will be in bounds...
                        ripple[i] = texture[a + (b * imgW)];
                    }
                    catch (IndexOutOfBoundsException e) {
                        // We were wrong... Don't use refraction
                        ripple[i] = texture[i];
                    }
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

            nFrames++;
            long timeNow = System.currentTimeMillis();
            int timeDiff = (int) (timeNow - lastDrawTime);
            if (timeDiff > 1000) {
                fps = nFrames * 10000 / timeDiff;
                lastDrawTime = timeNow;
                nFrames = 0;
            }

            String s = (fps / 10) + "." + (fps % 10) + "fps";
            int w = g.getFont().getWidth(s) + 2;
            int h = g.getFont().getHeight() + 2;
            g.setColor(0xFF000000);
            g.fillRect(2, 5, w, h);
            g.setColor(0xFFFFFFFF);
            g.drawString(s, 3, 6);

            //repaint();
            // only want to repaint a section of screen
            getDesktopPane().repaint(getXOnScreen(), getYOnScreen(), imgWidth, imgHeight);
        }

        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
            if (type == DesktopPane.PRESSED || type == DesktopPane.DRAGGED) {
                disturb(x, y);
            }
        }
    }
}

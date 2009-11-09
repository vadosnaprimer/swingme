/**
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

//package net.yura.mobile.gui.components;

package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.util.StringUtil;


/**
 * @author Jane
 */
public class Camera extends Component implements Runnable, PlayerListener
{

    private String snapshotEncoding;
    private String snapshotFileExt;
    private String playerLocator;
    private Thread cameraThread;
    private byte[] photoData;
    private boolean requestCapture;
    private Object uiLock = new Object();

    private ActionListener actionListener;
    private String actionCommand;

    public Camera() {

        System.out.println("CameraPanel() constructor");

        focusable = true;
    }


    protected String getDefaultName() {
        return "Camera";
    }

    public void workoutMinimumSize() {
        width = 10;
        height = 10;
    }

    public void paintComponent(Graphics2D g) {

System.out.println("paintComponent: " + getWidth() + "x" + getHeight());

//        g.setColor(0x0000FF);
//        g.fillRect(0, 0, getWidth(), getHeight());

        if (cameraThread == null) {
            cameraThread = new Thread(this);
            cameraThread.start();
        }
    }

    public void focusLost() {
        super.focusLost();
System.out.println(">> focusLost()");
        cameraThread = null;
        synchronized (uiLock) {
            uiLock.notifyAll();
        }

System.out.println("focusLost2");
    }

    public void setActionListener(ActionListener l) {
        this.actionListener = l;
    }

    public void setActionCommand(String ac) {
        this.actionCommand = ac;
    }

    public void capture() {
        synchronized (uiLock) {
            requestCapture = true;
            uiLock.notifyAll();
        }
    }

    public byte[] getSnapshotData() {
        byte[] res = photoData;
        photoData = null; // Allow GC to collect this memory

        return res;
    }

    public String getFileExtention() {
        return snapshotFileExt;
    }

    // Player Thread
    public void run() {

        VideoControl videoCtrl = null;
        Player player = null;

        try {
            while (true) {
System.out.println(".");
                synchronized (uiLock) {
System.out.println(".2");

                    if (Thread.currentThread() != cameraThread) {
                        break;
                    }

                    if (player == null) {
                        Thread.yield();
                        init();
                        player = Manager.createPlayer(playerLocator);
                        player.prefetch();
                        player.addPlayerListener(this);

                        videoCtrl = initVideoControl(player);

                        player.start();

                        Display.getDisplay(DesktopPane.getMidlet()).setCurrent(new DummyCanvas());
                    }

                    if (requestCapture && actionListener != null) {
                        requestCapture = false;

                        if (videoCtrl != null) {
                            photoData = videoCtrl.getSnapshot(snapshotEncoding);
                        }

                        actionListener.actionPerformed(actionCommand);
                    }

                    uiLock.wait(2500);

//System.out.println("ASK PAINT");
//                    repaint();
//                    Canvas playerCanvas = DesktopPane.getDesktopPane();
//                    playerCanvas.repaint();
//                    playerCanvas.serviceRepaints();
//
//                    Display.getDisplay(DesktopPane.getMidlet()).setCurrent(playerCanvas);
//
//System.out.println("ASK PAINT2");
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

        if (videoCtrl !=null) {
            videoCtrl.setVisible(false);
        }

        if (player != null) {
            player.close();
            player = null;
        }

        System.out.println("Camera Thread GONE.");
    }

    // From PlayerListener Interface
    public void playerUpdate(Player player, String event, Object obj) {
        System.out.println("playerUpdate: " + event);

        if (PlayerListener.CLOSED.equals(event)) {
            DesktopPane.getDesktopPane().fullRepaint();
        }
    }

    /**
     * Initialise snapshot encoding, file ext and player locator
     */
    private void init() {
        // Start with some sensible defaults
        snapshotEncoding = "encoding=png";
        snapshotFileExt = "png";
        playerLocator = "capture://video";

        // Lets try to find better values:
        String prop = System.getProperty("video.snapshot.encodings");
        if (prop != null) {

            String[][] encodinTable = {
                    {"jpeg", "jpg"},
                    {"jpg",  "jpg"},
                    {"png",  "png"},
                    {"gif",  "gif"},
                    {"bmp",  "bmp"},
            };

            String encodings = prop.toLowerCase().trim();
            String[] supportedEncs = StringUtil.split(encodings, ' ');

            for (int i = 0; i < encodinTable.length; i++) {
                String format = encodinTable[i][0];
                String enc = getHighestResolutionEncoding(format, supportedEncs);

                if (enc != null && enc.length() > 0) {
                    snapshotEncoding = enc;
                    snapshotFileExt = encodinTable[i][1];
                    break;
                }
            }
        }

        // Some phones (i.e. Nokia S40) need a capture://image locator
        String[] contentTypes = Manager.getSupportedContentTypes("capture");
        for (int i = 0; i < contentTypes.length; i++) {
System.out.println("SupportedContentType = capture://" + contentTypes[i]);
            if ("image".equals(contentTypes[i])) {
                playerLocator = "capture://image";
                snapshotEncoding += "&width=320&height=240";
//                break;
            }
        }
    }

    /**
     * Initialise the Video Controller for the specified Player.
     */
    private VideoControl initVideoControl(Player player) throws MediaException {
        VideoControl videoCtrl = (VideoControl) player.getControl("VideoControl");
        if (videoCtrl != null) {

            Canvas playerCanvas = DesktopPane.getDesktopPane();

            videoCtrl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, playerCanvas);
            videoCtrl.setVisible(false);
            videoCtrl.setDisplayFullScreen(false);
            videoCtrl.setDisplayLocation(getXOnScreen(), getYOnScreen());
            videoCtrl.setDisplaySize(getWidth(), getHeight());

            System.out.println("Video Size = " + getWidth() + "x" + getHeight());

            videoCtrl.setVisible(true);
        }

        return videoCtrl;
    }

    private static String getHighestResolutionEncoding(String format, String[] supportedEncs) {

        String highestResEncoding = "";
        int prevHighResWidth  = 0;
        int prevHighResHeight = 0;

        for (int i = 0; i < supportedEncs.length; i++) {
            String encoding = supportedEncs[i];

            int encodingWidth = getEncodingParameterInteger(encoding, "width");
            int encodingHeight = getEncodingParameterInteger(encoding, "height");
            String encodingType = getEncodingParamString(encoding, "encoding") .toLowerCase();

            if ( encodingWidth  >= prevHighResWidth  &&
                 encodingHeight >= prevHighResHeight &&
                 ( encodingType.equals(format           ) ||    // encoding=png      , encoding=PNG
                   encodingType.equals("image/" + format))) { // encoding=image/png, encoding=IMAGE/PNG

                highestResEncoding = encoding;
                prevHighResWidth   = encodingWidth;
                prevHighResHeight  = encodingHeight;
            }
        }

        System.out.println( "getHighestResolutionEncoding - determined highest resolution encoding format \"" +
                            (format == null ? "UNSPECIFIED" : format )                                        +
                            "\" to be \""                                                                     +
                            highestResEncoding                                                         +
                            "\""                                                                              );

        return highestResEncoding;
    }

    private static String getEncodingParamString(String encoding, String prefix) {
        String result = "";

        int prefixIdx = encoding.indexOf(prefix);
        if (prefixIdx > -1) {
            int ampersandIdx = encoding.indexOf('&', prefixIdx);
            if (ampersandIdx < 0) {
                ampersandIdx = encoding.length();
            }
            result = encoding.substring(prefixIdx + prefix.length() + 1,  ampersandIdx);
        }

        return result;
    }

    private static int getEncodingParameterInteger(String encoding, String prefix) {
        try {
            String s = getEncodingParamString(encoding, prefix);
            return Integer.parseInt(s);
        }
        catch (Exception exception) {
            return 0;
        }
    }


    private static class DummyCanvas extends Canvas {

        protected void paint(Graphics arg0) {
            Canvas playerCanvas = DesktopPane.getDesktopPane();
            Display.getDisplay(DesktopPane.getMidlet()).setCurrent(playerCanvas);
        }
    }
}

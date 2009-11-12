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
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.util.StringUtil;


/**
 * @author Jane
 */
public class Camera extends Component implements Runnable, PlayerListener
{

    private int defaultCaptureHeight = 480;
    private int defaultCaptureWidth = 640;
    private Font font;
    private String waitingMessage = "";
    private int waitingMessageLength;
    private String snapshotEncoding;
    private String snapshotFileExt;
    private String playerLocator;
    private Thread cameraThread;
    private byte[] photoData;
    private boolean requestCapture;
    private final Object uiLock = new Object();
    private boolean running = true;
    VideoControl videoCtrl = null;
    Player player = null;

    private ActionListener actionListener;
    private String actionCommand;

    public Camera() {

        System.out.println("CameraPanel() constructor");

        focusable = true;

        font = theme.getFont(Style.ALL);
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
        int msgPosX = (getWidth() / 2) - (waitingMessageLength / 2);
        int msgPosY = (getHeight() / 2) - (font.getHeight() / 2);
        g.setColor(foreground);
        g.drawString(waitingMessage, msgPosX, msgPosY);

        if (cameraThread == null && running) {
            cameraThread = new Thread(this);
            cameraThread.start();
        }
    }

    public void setWaitingMessage (String message){
        waitingMessage = message;
        waitingMessageLength =  font.getWidth(waitingMessage);
    }

    public void focusGained() {
        running = true;
        super.focusGained();
    }



    public void focusLost() {
        super.focusLost();
System.out.println(">> focusLost()");
        cameraThread = null;
        running = false;
        synchronized (uiLock) {
            uiLock.notifyAll();
        }
System.out.println("focusLost2");
    }

    public void close() {
        if (!running) {
            return;
        }
        cameraThread = null;
        running = false;
        closePlayer();
        synchronized (uiLock) {
            uiLock.notifyAll();
            try {
                uiLock.wait(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
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

    public void setDefaultCaptureResolution(int width, int height) {
        defaultCaptureHeight = height;
        defaultCaptureWidth = width;
    }

    // Player Thread
    public void run() {

       

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

                        // WORK-AROUND: Some SE phones, will rotate the view                        // finder if init video is called with player state                        // bigger than realise (i.e. prefetch())                        player = Manager.createPlayer(playerLocator);
                        player = Manager.createPlayer(playerLocator);
                        player.realize();
                        player.addPlayerListener(this);

                        videoCtrl = initVideoControl(player);

                        player.start();

                        // WORK-AROUND: some SE phones don't display the view
                        // finder, if there is no "Canvas transition"
                        Display.getDisplay(DesktopPane.getMidlet()).setCurrent(new DummyCanvas());
                    }

                    if (requestCapture && actionListener != null) {
                        requestCapture = false;

                        if (videoCtrl != null) {
                            photoData = null;
                            // some devices will not return the supported size even though its supported
                            if (snapshotEncoding.indexOf("width") < 0) {
                                try {
                                    photoData = videoCtrl.getSnapshot(snapshotEncoding + "&width=" + defaultCaptureWidth + "&height=" + defaultCaptureHeight);
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                            if (photoData == null) {
                                photoData = videoCtrl.getSnapshot(snapshotEncoding);
                            }
                        }

                        actionListener.actionPerformed(actionCommand);
                    }

                    uiLock.wait(2500);
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

        closePlayer();
        System.out.println("Camera Thread GONE.");
    }

    private synchronized void closePlayer(){
        if (videoCtrl !=null) {
            videoCtrl.setVisible(false);
        }

        if (player != null) {
            player.close();
            player = null;
        }
    }

    // From PlayerListener Interface
    public void playerUpdate(Player player, String event, Object obj) {
        System.out.println("playerUpdate: " + event);

        if (PlayerListener.CLOSED.equals(event)) {
            synchronized(uiLock){
                uiLock.notifyAll();
            }
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
                break;
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
            videoCtrl.setDisplayLocation(getXOnScreen(), getYOnScreen());
            videoCtrl.setDisplaySize(getWidth(), getHeight());

            System.out.println("Video Size = " + getWidth() + "x" + getHeight());

            videoCtrl.setVisible(true);
        }

        return videoCtrl;
    }

    private String getHighestResolutionEncoding(String format, String[] supportedEncs) {

        String highestResEncoding = "";
        int prevHighResWidth  = 0;
        int prevHighResHeight = 0;

        for (int i = 0; i < supportedEncs.length; i++) {
            String encoding = supportedEncs[i];

            int encodingWidth = getEncodingParameterInteger(encoding, "width");
            int encodingHeight = getEncodingParameterInteger(encoding, "height");
            String encodingType = getEncodingParamString(encoding, "encoding") .toLowerCase();

            if ( encodingWidth  >= prevHighResWidth  && encodingWidth <= defaultCaptureWidth &&
                 encodingHeight >= prevHighResHeight && encodingHeight <= defaultCaptureHeight &&
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

    public static boolean isCameraSupported(){
        return System.getProperty("video.snapshot.encodings") != null;
    }
}

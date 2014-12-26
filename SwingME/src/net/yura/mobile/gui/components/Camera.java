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

package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import javax.microedition.midlet.MIDlet;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.StringUtil;

/**
 * @author Jane
 */
public class Camera extends Component implements Runnable, PlayerListener {

    private static int cameraPermission;


    private int defaultCaptureHeight = 480;
    private int defaultCaptureWidth = 640;
    private int[][] knownEncodingDimensions = {{640, 480}, {480, 640}};
    private String[] knownEncodingFormats = {"jpeg", "jpg", "png", "gif", "bmp"};
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
    private VideoControl videoCtrl;
    private Player player;
    private ActionListener actionListener;
    private String actionCommand;
    private boolean seBug = true;

    public Camera() {

        //#debug debug
        Logger.debug("CameraPanel() constructor");

        focusable = true;

        font = theme.getFont(Style.ALL);
    }

    protected String getDefaultName() {
        return "Camera";
    }

    protected void workoutMinimumSize() {
        width = 10;
        height = 10;
    }

    public void paintComponent(Graphics2D g) {

        //#debug debug
        Logger.debug("paintComponent: " + getWidth() + "x" + getHeight());

        int msgPosX = (getWidth() / 2) - (waitingMessageLength / 2);
        int msgPosY = (getHeight() / 2) - (font.getHeight() / 2);
        g.setColor( getForeground() );
        g.drawString(waitingMessage, msgPosX, msgPosY);

        if (running) {
            if (cameraThread == null) {
                cameraThread = new Thread(this);
                cameraThread.start();
            }
            else {
                // Location and size, may change... notify the player thread
                synchronized (uiLock) {
                    uiLock.notifyAll();
                }
            }
        }
    }

    public void setWaitingMessage(String message) {
        waitingMessage = message;
        waitingMessageLength = font.getWidth(waitingMessage);
    }

    public void focusGained() {
        running = true;
        super.focusGained();
    }

// TODO: JP - Cannot rely on  focusGained/focusLost. If "capture" is done by a
// Button instead of a soft-key (like Android), the panel will lose focus...
//    public void focusLost() {
//        super.focusLost();
//        //#debug debug
//        Logger.debug(">> focusLost()");
//        cameraThread = null;
//        running = false;
//        synchronized (uiLock) {
//            uiLock.notifyAll();
//        }
//        //#debug debug
//        Logger.debug("focusLost2");
//    }

    // Overloads Component.processKeyEvent
    public boolean processKeyEvent(KeyEvent keypad) {

        // try to guess the "camera key" code, and execute capture
        int keycode = keypad.getJustPressedKey();
        if (actionListener != null &&
           (keycode == -24 || keycode == -25 || keycode == -26)) {
            capture();
            return true;
        }

        return false;
    }

    public void close() {
        if (!running) {
            return;
        }
        cameraThread = null;
        running = false;
        closePlayer();
//JP        synchronized (uiLock) {
//            uiLock.notifyAll();
//            try {
//                uiLock.wait(5000);
//            } catch (InterruptedException ex) {
//                Logger.info(ex);
//            }
//        }
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

    /**
     * Get the requested width used to take the snapshot. If available, that
     * will avoid the creation of an Image Object to return it's width.
     * @return Requested snapshot width, or zero if not available.
     */
    public int getSnapshotWidth() {
        return getEncodingParameterInteger(snapshotEncoding, "width");
    }

    /**
     * Get the requested height used to take the snapshot. If available, that
     * will avoid the creation of an Image Object to return it's height.
     * @return Requested snapshot height, or zero if not available.
     */
    public int getSnapshotHeight() {
        return getEncodingParameterInteger(snapshotEncoding, "height");
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

                synchronized (uiLock) {
                    if (Thread.currentThread() != cameraThread) {
                        break;
                    }
                    uiLock.wait(1000);
                }

                if (player == null) {
                    Thread.yield();
                    init();

                    player = Manager.createPlayer(playerLocator);

                    // WORK-AROUND1: Some SE phones, rotate the view finder
                    // if init video is called with player state bigger than
                    // realise (i.e. prefetch())
                    // WORK-AROUND2: Nokia S40 hangs for a long time, if
                    // prefetch() is not called before start()

                    if (seBug) {
                        player.realize();
                    } else {
                        player.prefetch();
                    }

                    videoCtrl = initVideoControl(player);
                    player.addPlayerListener(this);

                    System.gc();
                    player.start();

                    // WORK-AROUND: WTK don't display the view
                    // finder, if there is no "Canvas transition"
                    if (Midlet.getPlatform() == Midlet.PLATFORM_WTK) {
                        Display.getDisplay(Midlet.getMidlet()).setCurrent(new DummyCanvas());
                    }
                }

                if (videoCtrl != null) {

                    setupBounds(videoCtrl);

                }

                if (requestCapture && actionListener != null) {
                    requestCapture = false;

                    if (videoCtrl != null) {
                        photoData = null;
                        // some devices will not return the supported size even though its supported
                        if (snapshotEncoding.indexOf("width") < 0) {

                            String encodingString = getEncodingStringFromRMS();

                            if (encodingString == null || encodingString.length() < 1) {
                                encodingString = snapshotEncoding + "&width=" + defaultCaptureWidth + "&height=" + defaultCaptureHeight;
                            }

                            photoData = getSnapshot(encodingString);
                            if (photoData == null) {
                                // Worst case scenario. Need to cycle through all
                                // possible encoding configurations and see which one works
                                photoData = discoverEncodingDimensions(videoCtrl);
                            }
                        }

                        if (photoData == null) {
                            photoData = getSnapshot(snapshotEncoding);
                        }
                    }

                    // If camera permission is -1, we just took a dummy
                    // picture, to show the security prompt
                    if (cameraPermission < 0) {
                        photoData = null;
                    }
                    else {
                        try {
                            actionListener.actionPerformed(actionCommand);
                        }
                        catch (Exception e) {
                          Logger.warn("error calling " + actionListener + " " + actionCommand, e);
                        }
                    }

                    // From now on, we don't take any more "dummy pictures"
                    cameraPermission = 1;
                }
            }
        }
        catch (Throwable e) {
            Logger.error(null, e);
        }
        finally {

            try {
                closePlayer();
            }
            catch (Exception e) {
                Logger.warn(null, e);
            }
            //#debug debug
            Logger.debug("Camera Thread GONE.");
        }
    }

    private synchronized void closePlayer() {
        if (videoCtrl != null) {
            videoCtrl.setVisible(false);
        }

        if (player != null) {
            player.close();
            player = null;
        }
    }

    private int getCameraPermission() {

        MIDlet midlet = Midlet.getMidlet();
        int perm = midlet.checkPermission("javax.microedition.media.control.VideoControl.getSnapshot");

        return perm;
    }

    private byte[] getSnapshot(String encoding) {
        byte[] data = null;
        try {
            //#debug debug
            Logger.debug("getSnapshot: Trying " + encoding);
            data = videoCtrl.getSnapshot(encoding);
        } catch (Exception e) {
            Logger.warn("error encoding " + encoding, e);
        }

        if (data != null) {
            // If the snapshot was successful, save the encoding.
            this.snapshotEncoding = encoding;
        }

        //#debug debug
        Logger.debug("getSnapshot: " + ((data == null) ? "FAIL." : "OK."));
        return data;
    }

    // From PlayerListener Interface
    public void playerUpdate(Player player, String event, Object obj) {
        //#debug debug
        Logger.debug("playerUpdate: " + event);

        if (PlayerListener.STARTED.equals(event)) {
            if (cameraPermission < 0) {
                //#debug debug
                Logger.debug("cameraPermission: " + cameraPermission);
                if (getCameraPermission() < 0) {
                    // Take dummy picture, to show user the security prompt
                    capture();
                }
            }
        }
        else if (PlayerListener.CLOSED.equals(event)) {
            synchronized (uiLock) {
                uiLock.notifyAll();
            }
            // by the time we receive the close event
            // the camera might not belong to a window
            getDesktopPane().repaint();
        }
    }

    public void setKnownEncodingDimensions(int[][] knownEncodingDimensions) {
        this.knownEncodingDimensions = knownEncodingDimensions;
    }

    /**
     * Attempt to take a snapshot with a set of known encoding dimesions. If
     * successful then log this to RMS for future reference. The list of encoding
     * dimensions should have the preferred dimension at the start of the array as a
     * successful snapshot will result in the for loop returning at that point.
     *
     * @param videoCtrl
     * @param snapshotEncoding
     * @return
     */
    private byte[] discoverEncodingDimensions(VideoControl videoCtrl) {
        byte[] data = null;

        for (int f = 0; f < knownEncodingFormats.length; f++) {
            for (int i = 0; i < knownEncodingDimensions.length; i++) {

                String encodingStr = "encoding=" + knownEncodingFormats[f] + "&width=" + knownEncodingDimensions[i][0] + "&height=" + knownEncodingDimensions[i][1];
                data = getSnapshot(encodingStr);

                if (data != null) {
                    setEncodingStringInRMS(encodingStr);
                    break;
                }
            }
        }

        return data;
    }

    /**
     * Initialise snapshot encoding, file ext and player locator.
     *
     * This method attempts to discover the best encodings from the advertised
     * supported encodings.
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
                {"jpg", "jpg"},
                {"png", "png"},
                {"gif", "gif"},
                {"bmp", "bmp"},};

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
            //#debug debug
            Logger.debug("SupportedContentType = capture://" + contentTypes[i]);
            if ("image".equals(contentTypes[i])) {
                playerLocator = "capture://image";

                // WORK-AROUND: a 640x480 on Nokia S40 takes half of the memory heap (1Mb)
                snapshotEncoding = "encoding=image/jpeg&width=320&height=240";
                seBug = false;
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

            Canvas playerCanvas = getDesktopPane();

            videoCtrl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, playerCanvas);
            setupBounds(videoCtrl);

            //#debug debug
            Logger.debug("Video Size = " + getWidth() + "x" + getHeight());

            videoCtrl.setVisible(true);
        }

        return videoCtrl;
    }

    private String getHighestResolutionEncoding(String format, String[] supportedEncs) {

        String highestResEncoding = "";
        int prevHighResWidth = 0;
        int prevHighResHeight = 0;

        for (int i = 0; i < supportedEncs.length; i++) {
            String encoding = supportedEncs[i];

            int encodingWidth = getEncodingParameterInteger(encoding, "width");
            int encodingHeight = getEncodingParameterInteger(encoding, "height");
            String encodingType = getEncodingParamString(encoding, "encoding").toLowerCase();

            if (encodingWidth >= prevHighResWidth && encodingWidth <= defaultCaptureWidth &&
                    encodingHeight >= prevHighResHeight && encodingHeight <= defaultCaptureHeight &&
                    (encodingType.equals(format) || // encoding=png      , encoding=PNG
                    encodingType.equals("image/" + format))) { // encoding=image/png, encoding=IMAGE/PNG

                highestResEncoding = encoding;
                prevHighResWidth = encodingWidth;
                prevHighResHeight = encodingHeight;
            }
        }
        //#mdebug debug
        Logger.debug("getHighestResolutionEncoding - determined highest resolution encoding format \"" +
                (format == null ? "UNSPECIFIED" : format) +
                "\" to be \"" +
                highestResEncoding +
                "\"");
        //#enddebug

        return highestResEncoding;
    }

    private void setupBounds(VideoControl videoCtrl) {

        int dispW = getWidth();
        int dispH = getHeight();

        if (videoCtrl.getDisplayWidth() != dispW ||
            videoCtrl.getDisplayHeight() != dispH) {

            try {
                videoCtrl.setDisplaySize(dispW,dispH);
            }
            catch (MediaException mex) {
                //#debug debug
                Logger.debug("cant set size " + dispW + "," + dispH, mex);
                //resizing is not supported or the operation failed due to hardware or software limitations.
            }

        }

        int dispX = getXOnScreen()+(getWidth()-videoCtrl.getDisplayWidth())/2;
        int dispY = getYOnScreen()+(getHeight()-videoCtrl.getDisplayHeight())/2;

        if (videoCtrl.getDisplayX() != dispX ||
            videoCtrl.getDisplayY() != dispY) {

            videoCtrl.setDisplayLocation(dispX, dispY);
        }
    }

    private static String getEncodingParamString(String encoding, String prefix) {
        String result = "";

        int prefixIdx = encoding.indexOf(prefix);
        if (prefixIdx > -1) {
            int ampersandIdx = encoding.indexOf('&', prefixIdx);
            if (ampersandIdx < 0) {
                ampersandIdx = encoding.length();
            }
            result = encoding.substring(prefixIdx + prefix.length() + 1, ampersandIdx);
        }

        return result;
    }

    private static int getEncodingParameterInteger(String encoding, String prefix) {
        try {
            String s = getEncodingParamString(encoding, prefix);
            if (s!= null && s.length() > 0) {
                return Integer.parseInt(s);
            }
        }
        catch (Exception e) {
            Logger.warn("error " + encoding + " " + prefix, e);
        }
        return 0;
    }

    private static class DummyCanvas extends Canvas {

        protected void paint(Graphics arg0) {
            Canvas playerCanvas = DesktopPane.getDesktopPane();
            Display.getDisplay(Midlet.getMidlet()).setCurrent(playerCanvas);
        }
    }

    public static boolean isCameraSupported() {
        return System.getProperty("video.snapshot.encodings") != null;
    }
    private static final String CAPTURE_ENCODING_STRING_RECORD_STORE = "cap_enc_str_rs";

    private void setEncodingStringInRMS(String encodingString) {
        try {
            RecordStore rs = RecordStore.openRecordStore(CAPTURE_ENCODING_STRING_RECORD_STORE, true);
            rs.addRecord(encodingString.getBytes(), 0, encodingString.length());
        }
        catch (RecordStoreException ex) {
            // this is just a best effort to persist dimensions to rms
            Logger.info("error setting " + encodingString, ex);
        }
    }

    private String getEncodingStringFromRMS() {
        try {
            RecordStore rs = RecordStore.openRecordStore(CAPTURE_ENCODING_STRING_RECORD_STORE, false);
            byte[] encodingStringBytes = rs.getRecord(1);

            if (encodingStringBytes != null && encodingStringBytes.length > 0) {
                return new String(encodingStringBytes);
            }
        }
        catch (Exception ex) {
            // again just a best effort to look up dimensions
            Logger.info("error getting", ex);
        }
        return null;
    }
}

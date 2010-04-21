package javax.microedition.media;

import java.io.IOException;

import javax.microedition.media.player.CameraPlayer;

public class Manager {
    public static final String TONE_DEVICE_LOCATOR = "tone_device";

    public static Player createPlayer(java.io.InputStream ins, String type)
            throws IOException, MediaException {
        return null;
    }

    public static Player createPlayer(String locator) throws IOException,
            MediaException {
        Player player;
        if (locator == null) {
            player = null;
        }
        else if (locator.startsWith("capture://video")) {
            player = new CameraPlayer();
        }
        else if (TONE_DEVICE_LOCATOR.equals(locator)) {
            player = new TonePlayer();
        } else {
            player = null;
        }

        return player;
    }

    public static void playTone(int note, int duration, int volume)
            throws MediaException {
        // do nothing
    }

    public static String[] getSupportedContentTypes(String protocol) {
        String[] res;
        if (protocol == null || "capture".equals(protocol)) {
            res = new String[] {"video"};
        }
        else {
            res = new String[0];
        }
        return res;
    }

    public static String[] getSupportedProtocols(String contentType) {
        String[] res;
        if (contentType == null || "video".equals(contentType)) {
            res = new String[] {"capture"};
        }
        else {
            res = new String[0];
        }
        return res;
    }
}

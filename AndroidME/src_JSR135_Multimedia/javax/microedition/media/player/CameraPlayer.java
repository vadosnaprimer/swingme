package javax.microedition.media.player;

import javax.microedition.media.Control;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;

public class CameraPlayer implements Player, VideoControl, Controllable {

    // @Override
    public Control getControl(String controlType) {
        if ("VideoControl".equalsIgnoreCase(controlType) ||
            "GUIControl".equalsIgnoreCase(controlType)) {
            return this;
        } else {
            return null;
        }
    }

    // @Override
    public Control[] getControls() {
        return new Control[] { this };
    }

    // @Override
    public void addPlayerListener(PlayerListener playerListener) {
        // TODO Auto-generated method stub

    }

    // @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    // @Override
    public void deallocate() {
        // TODO Auto-generated method stub
    }

    // @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    // @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 0;
    }

    // @Override
    public long getMediaTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    // @Override
    public int getState() {
        // TODO Auto-generated method stub
        return 0;
    }

    // @Override
    public void prefetch() {
        // TODO Auto-generated method stub
    }

    // @Override
    public void realize() {
        // TODO Auto-generated method stub
    }

    // @Override
    public void removePlayerListener(PlayerListener playerListener) {
        // TODO Auto-generated method stub
    }

    // @Override
    public void setLoopCount(int count) {
        // TODO Auto-generated method stub
    }

    // @Override
    public long setMediaTime(long now) {
        // TODO Auto-generated method stub
        return 0;
    }

    // @Override
    public void start() {
        // TODO Auto-generated method stub
    }

    // @Override
    public void stop() {
        // TODO Auto-generated method stub
    }

    public int getDisplayHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getDisplayWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getDisplayX() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getDisplayY() {
        // TODO Auto-generated method stub
        return 0;
    }

    public byte[] getSnapshot(String s) throws MediaException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSourceHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getSourceWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Object initDisplayMode(int i, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDisplayFullScreen(boolean flag) throws MediaException {
        // TODO Auto-generated method stub
    }

    public void setDisplayLocation(int i, int j) {
        // TODO Auto-generated method stub
    }

    public void setDisplaySize(int i, int j) throws MediaException {
        // TODO Auto-generated method stub
    }

    public void setVisible(boolean flag) {
        // TODO Auto-generated method stub
    }
}

package javax.microedition.media.player;

import javax.microedition.media.Control;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VideoControl;

import net.yura.android.lcdui.Toolkit;

public class CameraPlayer extends BasicPlayer implements VideoControl, Controllable {


    public CameraPlayer(Toolkit toolKit) {
        super(toolKit);
    }

    @Override
    protected void doClose() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doDeallocate() {
        // TODO Auto-generated method stub

    }

    @Override
    protected Control doGetControl(String type) {
        if ("VideoControl".equalsIgnoreCase(type) ||
            "GUIControl".equalsIgnoreCase(type)) {
            return this;
        }

        return null;
    }

    @Override
    protected Control[] doGetControls() {
        return new Control[] { this };
    }

    @Override
    protected long doGetDuration() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected long doGetMediaTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void doPrefetch() throws MediaException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doRealize() throws MediaException {
        // TODO Auto-generated method stub

    }

    @Override
    protected long doSetMediaTime(long now) throws MediaException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected boolean doStart() throws MediaException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void doStop() {
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

    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }


}

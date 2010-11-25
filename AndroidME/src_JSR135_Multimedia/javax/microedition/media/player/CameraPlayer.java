package javax.microedition.media.player;

import java.io.IOException;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VideoControl;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

import net.yura.android.lcdui.Toolkit;

public class CameraPlayer extends BasicPlayer implements VideoControl {

    private Preview preview;
    private Camera camera;
    private javax.microedition.lcdui.Canvas canvas;
    private int dispX, dispY;
    private int dispW, dispH;
    private boolean fullScreen;

    public CameraPlayer(Toolkit toolKit) {
        super(toolKit);
    }

    @Override
    protected void doClose() {
        if (camera != null) {
            preview.close();
        }
    }

    @Override
    protected void doDeallocate() {
        // Do nothing...
    }

    @Override
    protected Control doGetControl(String type) {
        if ("javax.microedition.media.control.VideoControl".equalsIgnoreCase(type) ||
            "javax.microedition.media.control.GUIControl".equalsIgnoreCase(type)) {
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
        // Do nothing...
    }

    @Override
    protected void doRealize() throws MediaException {
        // Do nothing...
    }

    @Override
    protected long doSetMediaTime(long now) throws MediaException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void doStart() throws MediaException {
        if (preview != null && camera != null) {
            camera.startPreview();
        }
    }

    @Override
    protected void doStop() {
        if (preview != null && camera != null) {
            camera.stopPreview();
        }
    }

    public int getDisplayHeight() {
        checkDisplayState();

        return preview.getHeight();
    }

    public int getDisplayWidth() {
        checkDisplayState();

        return preview.getWidth();
    }

    public int getDisplayX() {
        return dispX;
    }

    public int getDisplayY() {
        return dispY;
    }

    private byte[] snapshotBytes;
    private Object snapshotLock = new Object();
    public byte[] getSnapshot(String s) throws MediaException {
        checkDisplayState();

        System.out.println("Camera: getSnapshot " + camera);

        synchronized (snapshotLock) {
            if (camera != null) {

                // Create a call back, that stores jpeg data and wakes up thread
                Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera camera) {
                        System.out.println("Camera: Got Picture");
                        synchronized (snapshotLock) {
                            snapshotBytes = data;
                            camera.startPreview();
                            snapshotLock.notifyAll();
                        }
                    }
                };

                System.out.println("Camera: Request Picture");
                snapshotBytes = null;
                camera.takePicture(null, null, jpegCallback);
                try {
                    snapshotLock.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return snapshotBytes;
    }

    public int getSourceHeight() {
        // If we can't record video, this method is meaningless...
        // Return screen height for now.
        return toolKit.getScreenHeight();
    }

    public int getSourceWidth() {
        // If we can't record video, this method is meaningless...
        // Return screen width for now.
        return toolKit.getScreenWidth();
    }

    public Object initDisplayMode(int mode, Object obj) {

        if (preview != null) {
            throw new IllegalStateException("initDisplayMode: called before");
        }

        if (mode != USE_DIRECT_VIDEO || !(obj instanceof javax.microedition.lcdui.Canvas)) {
            throw new IllegalArgumentException("initDisplayMode: Only USE_DIRECT_VIDEO and Canvas are supported");
        }

        this.canvas = (javax.microedition.lcdui.Canvas) obj;

        try {
            sendPlayerEvent("doInitDisplayMode", null);
        } catch (Exception e) {
        }

        return null;
    }

    public void setDisplayFullScreen(boolean fullScreen) throws MediaException {
        checkDisplayState();

        if (this.fullScreen != fullScreen) {
            this.fullScreen = fullScreen;
            updateView();
        }
    }

    public void setDisplayLocation(int x, int y) {
        checkDisplayState();

        dispX = x;
        dispY = y;

        updateView();
    }

    public void setDisplaySize(int w, int h) throws MediaException {
        checkDisplayState();

        dispW = w;
        dispH = h;

        updateView();
    }

    public void setVisible(boolean flag) {
        checkDisplayState();

        try {
            sendPlayerEvent("doSetVisible", flag);
        }
        catch (MediaException e)
        {}
    }

    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    private void checkDisplayState() {
        if (preview == null) {
            throw new IllegalStateException("initDisplayMode not called yet");
        }
    }

    @Override
    void doPlayerEvent(String evt, Object evtData) throws MediaException {
        super.doPlayerEvent(evt, evtData);

        if (evt == "doInitDisplayMode") {
            Context context = canvas.getView().getContext();
            preview = new Preview(context);

            // Add a dummy layout, to later change dimensions (onPreviewFrame)
            preview.setLayoutParams(new LayoutParams(100, 100));

            if (dispW <= 0) {
                dispW = canvas.getWidth();
            }

            if (dispH <= 0) {
                dispH = canvas.getHeight();
            }

            updateView();
        }
        else if (evt == "doSetVisible") {
            boolean flag = (Boolean) evtData;
            if (flag) {
                canvas.addOverlayView(preview);
            }
            else {
                canvas.removeOverlayView(preview);
            }
        }
    }

    void doListenerEvent(String evt, Object evtData) {
        if (evt == "updateView") {
            if (preview != null) {
                if (fullScreen) {
                    preview.layout(0, 0, canvas.getWidth(), canvas.getHeight());
                }
                else {
                    preview.layout(dispX, dispY, dispX + dispW, dispY + dispH);
                }
            }
        }
        else {
            super.doListenerEvent(evt, evtData);
        }
    }


    private void updateView() {
        if (preview != null) {
            sendListenerEvent("updateView", null);
        }
    }


    class Preview extends SurfaceView implements SurfaceHolder.Callback {

        Preview(Context context) {
            super(context);

            System.out.println(">>>> SurfaceView constructor <<<<");

            // Add callback to be notified of surface created/destroyed.
            SurfaceHolder mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void close() {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            System.out.println(">>>> Camera:surfaceCreated <<<<");
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println(">>>> Camera:surfaceDestroyed <<<<");
            close();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.

            if (camera == null) {
                camera = Camera.open();

                //TODO: Need to get this from the snapshot locator...
                Parameters params = camera.getParameters();
                params.setPictureSize(640, 480);

                try {
                    camera.setParameters(params);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            try {
                camera.setPreviewDisplay(holder);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

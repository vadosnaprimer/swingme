package javax.microedition.media.player;

import java.io.IOException;

import javax.microedition.media.Control;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VideoControl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

import net.yura.android.AndroidMeMIDlet;
import net.yura.android.lcdui.Toolkit;

public class CameraPlayer extends BasicPlayer implements VideoControl, Controllable {

    private Preview preview;
    private Camera camera;
    private javax.microedition.lcdui.Canvas canvas;
    private int displayX, displayY;
    private int displayW, displayH;
    private boolean fullScreen;

    public CameraPlayer(Toolkit toolKit) {
        super(toolKit);
    }

    @Override
    protected void doClose() {
        if (preview != null) {
            setVisible(false);
            preview = null;
        }

        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
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
        return displayX;
    }

    public int getDisplayY() {
        return displayY;
    }

    public byte[] getSnapshot(String s) throws MediaException {
        checkDisplayState();


        // TODO Auto-generated method stub
        return null;
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

        displayX = x;
        displayY = y;

        updateView();
    }

    public void setDisplaySize(int w, int h) throws MediaException {
        checkDisplayState();

        displayW = w;
        displayH = h;

        updateView();
    }

    public void setVisible(boolean flag) {
        checkDisplayState();

        try {
            sendPlayerEvent("doSetVisible", flag);
        } catch (MediaException e)
        {
        }
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

    private void updateView() {
        if (fullScreen) {
            preview.layout(0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            preview.layout(displayX, displayY, displayX + displayW, displayY + displayH);
        }
    }

    @Override
    void doPlayerEvent(String evt, Object evtData) throws MediaException {
        super.doPlayerEvent(evt, evtData);

        if (evt == "doInitDisplayMode") {
            Context context = canvas.getView().getContext();
            preview = new Preview(context);
            preview.setLayoutParams(new LayoutParams(200, 100));

            if (displayW <= 0) {
                displayW = canvas.getWidth();
            }

            if (displayH <= 0) {
                displayH = canvas.getHeight();
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

            System.out.println(">>>>>>>>>>>>>>> Invalidate > " + flag);
            preview.invalidate();
            canvas.getView().getRootView().invalidate();
        }
    }


    class Preview extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {

        Preview(Context context) {
            super(context);

            System.out.println(">>>> SurfaceView constructor <<<<");

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            SurfaceHolder mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            System.out.println(">>>> surfaceCreated <<<<");
        }

        public void surfaceDestroyed(SurfaceHolder holder) {

            System.out.println(">>>> surfaceDestroyed <<<<");

            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.

            System.out.println(">>>>1a surfaceChanged <<<<");
            if (camera == null) {
                camera = Camera.open();
                camera.setPreviewCallback(this);
            }
            System.out.println(">>>>4a surfaceChanged");

            System.out.println(">>>>1 surfaceChanged");

            try {
                System.out.println(">>>>2a surfaceChanged");
                camera.setPreviewDisplay(holder);
                System.out.println(">>>>3a surfaceChanged");

            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            System.out.println(">>>>2 surfaceChanged");
        }

        @Override
        public void draw(Canvas canvas) {
            System.out.println(">>>>>>>>>>>>>>> draw");
            super.draw(canvas);
            Paint p = new Paint(Color.RED);
            canvas.drawText("PREVIEW", canvas.getWidth() / 2, canvas.getHeight() / 2, p);
        }


        int x, y;
        public void onPreviewFrame(byte[] data, Camera arg1) {
            //invalidate();
            //System.out.println("...");
            x++;
            y++;
            if (x > 100)  x = 1;
            if (y > 100)  y = 1;
            super.layout(x, y, getWidth() + x, getHeight() + y);
            //super.setPadding(x, y, 0, 0);
        }
    }


}

package javax.microedition.lcdui;

import java.util.Vector;
import javax.microedition.midlet.MIDlet;

import net.yura.android.AndroidMeMIDlet;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public abstract class Canvas extends Displayable {
    public static final int UP = 1;
    public static final int DOWN = 6;
    public static final int LEFT = 2;
    public static final int RIGHT = 5;
    public static final int FIRE = 8;

    public static final int GAME_A = 9;
    public static final int GAME_B = 10;
    public static final int GAME_C = 11;
    public static final int GAME_D = 12;

    public static final int KEY_NUM0 = 48;
    public static final int KEY_NUM1 = 49;
    public static final int KEY_NUM2 = 50;
    public static final int KEY_NUM3 = 51;
    public static final int KEY_NUM4 = 52;
    public static final int KEY_NUM5 = 53;
    public static final int KEY_NUM6 = 54;
    public static final int KEY_NUM7 = 55;
    public static final int KEY_NUM8 = 56;
    public static final int KEY_NUM9 = 57;
    public static final int KEY_STAR = 42;
    public static final int KEY_POUND = 35;

    private CanvasView canvasView;
    private Bitmap graphicsBitmap;

    protected Canvas() {
        this.canvasView = new CanvasView(AndroidMeMIDlet.DEFAULT_ACTIVITY);
    }

    public void setFullScreenMode(boolean fullScreen) {
        // do nothing, is this possible on android?
    }

    @Override
    public int getWidth() {
        return MIDlet.DEFAULT_MIDLET.getToolkit().getScreenWidth();
    }

    @Override
    public int getHeight() {
        return MIDlet.DEFAULT_MIDLET.getToolkit().getScreenHeight();
    }

    public int getGameAction(int keyCode) {
        int res;
        switch (keyCode) {
            case -5:
            case -10:
                res = Canvas.FIRE;
                break;
            case -1:
                res = Canvas.UP;
                break;
            case -2:
                res = Canvas.DOWN;
                break;
            case -3:
                res = Canvas.LEFT;
                break;
            case -4:
                res = Canvas.RIGHT;
                break;
            case '7':
                res = Canvas.GAME_A;
                break;
            case '9':
                res = Canvas.GAME_B;
                break;
            case '*':
                res = Canvas.GAME_C;
                break;
            case '#':
                res = Canvas.GAME_D;
                break;
            default:
                res = 0;
                break;
        }
        return res;
    }

    public int getKeyCode(int gameAction) {
        int res;
        switch (gameAction) {
            case Canvas.FIRE:
                res = -5;
                break;
            case Canvas.UP:
                res = -1;
                break;
            case Canvas.DOWN:
                res = -2;
                break;
            case Canvas.LEFT:
                res = -3;
                break;
            case Canvas.RIGHT:
                res = -4;
                break;
            case Canvas.GAME_A:
                res = '7';
                break;
            case Canvas.GAME_B:
                res = '9';
                break;
            case Canvas.GAME_C:
                res = '*';
                break;
            case Canvas.GAME_D:
                res = '#';
                break;
            default:
                res = 0;
                break;
        }
        return res;
    }

    public void repaint(int x, int y, int w, int h) {
        repaint();
    }

    public synchronized void repaint() {
        if (this.canvasView != null) {
            this.canvasView.postInvalidate();
        }
    }

    protected void keyPressed(int keyCode) {
    }

    protected void keyReleased(int keyCode) {

    }

    protected void keyRepeated(int keyCode) {

    }

    protected abstract void paint(javax.microedition.lcdui.Graphics g);

    public boolean hasPointerEvents() {
        return true;
    }

    protected void pointerPressed(int x, int y) {

    }

    protected void pointerReleased(int x, int y) {

    }

    protected void pointerDragged(int x, int y) {

    }

    @Override
    public View getView() {
        return this.canvasView;
    }

    @Override
    public void disposeDisplayable() {
//        this.canvasView = null;
    }

    @Override
    public void initDisplayable(MIDlet midlet) {
        if (this.canvasView == null) {
            this.canvasView = new CanvasView(midlet.getActivity());
        }
    }

    protected javax.microedition.lcdui.Graphics getGraphics() {
        return this.canvasView.graphics;
    }

    private class CanvasView extends View {
        javax.microedition.lcdui.Graphics graphics = new Graphics(new android.graphics.Canvas());

        public CanvasView(Context context) {
            super(context);

            setFocusable(true);
            setFocusableInTouchMode(true);
        }

        long time = System.currentTimeMillis();

        @Override
        protected void onDraw(android.graphics.Canvas androidCanvas) {

            // If Possible, try to not use more than 50% on CPU time on painting...
            long elapsed = System.currentTimeMillis() - time;

            if (elapsed < 10) {
                try {
                    System.out.println("paint: elapsed " + elapsed);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }

            if (graphicsBitmap != null && androidCanvas != null) {

                // Check for size changes...
                if (graphicsBitmap.getWidth() != androidCanvas.getWidth()
                        || graphicsBitmap.getHeight() != androidCanvas
                                .getHeight()) {

                    graphicsBitmap = null;
                }
            }

            if (graphicsBitmap == null) {

                graphicsBitmap = Bitmap.createBitmap(androidCanvas.getWidth(),
                        androidCanvas.getHeight(), Bitmap.Config.ARGB_8888);

                graphics.setCanvas(new android.graphics.Canvas(graphicsBitmap));
            }

            graphics.reset();

            paint(graphics);
            androidCanvas.drawBitmap(graphicsBitmap, 0, 0, null);

            time = System.currentTimeMillis();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            invalidate();
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            keyCode = getKeyCode(event);

            if (event.getRepeatCount() == 0) {
                keyPressed(keyCode);
            } else {
                keyRepeated(keyCode);
            }
            return true;
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {

            keyCode = getKeyCode(event);
            keyReleased(keyCode);
            return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // System.out.println(
            // "("+(int)event.getX()+","+(int)event.getY()+","+event.getAction()+")"
            // );
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Canvas.this.pointerPressed(Math.round(event.getX()), Math
                        .round(event.getY()));
                break;
            case MotionEvent.ACTION_UP:
                Canvas.this.pointerReleased(Math.round(event.getX()), Math
                        .round(event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas.this.pointerDragged(Math.round(event.getX()), Math
                        .round(event.getY()));
                break;
            }

            return true;
        }

        private int getKeyCode(KeyEvent keyEvent) {
            // TODO implement as lookup table
            int deviceKeyCode = keyEvent.getKeyCode();

            int resultKeyCode;
            switch (deviceKeyCode) {
            case KeyEvent.KEYCODE_DPAD_UP :
                resultKeyCode = -1;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN :
                resultKeyCode = -2;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT :
                resultKeyCode = -3;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT :
                resultKeyCode = -4;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER :
                resultKeyCode = -5;
                break;
            case KeyEvent.KEYCODE_MENU :
                resultKeyCode = -6; // Left Soft-Key
                break;
            case KeyEvent.KEYCODE_BACK :
                resultKeyCode = -7; // Right Soft-Key
                break;
            case KeyEvent.KEYCODE_DEL :
                resultKeyCode = -8; // Backspace ascii
                break;
            case KeyEvent.KEYCODE_CALL :
                resultKeyCode = -10;
                break;
            case KeyEvent.KEYCODE_ENDCALL :
                resultKeyCode = -11;
                break;
            case KeyEvent.KEYCODE_VOLUME_UP :
                resultKeyCode = -36;
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN :
                resultKeyCode = -37;
                break;
            default:
                resultKeyCode = keyEvent.getUnicodeChar();
                if (resultKeyCode == 0) {
                     resultKeyCode = -deviceKeyCode;
                }
            }

            return resultKeyCode;
        }
    }

    protected void hideNotify() {

    }

    public void serviceRepaints() {
        AndroidMeMIDlet.DEFAULT_ACTIVITY.invokeAndWait(new Thread());
    }

    public String getKeyName(int keyCode) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isShown() {
        // TODO Auto-generated method stub
        return Display.getDisplay(AndroidMeMIDlet.DEFAULT_ACTIVITY.getMIDlet()).getCurrent() == this;
    }

    public void setTitle(Object object) {
        // TODO Auto-generated method stub
    }
}

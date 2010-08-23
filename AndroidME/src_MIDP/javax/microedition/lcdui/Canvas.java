package javax.microedition.lcdui;

import java.util.Arrays;

import javax.microedition.midlet.MIDlet;

import net.yura.android.AndroidMeMIDlet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

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

    private LinearLayout linearLayout;
    private CanvasView canvasView;
    private Bitmap graphicsBitmap;

    protected Canvas() {
        this.linearLayout = new LinearLayout(AndroidMeMIDlet.DEFAULT_ACTIVITY);
        this.canvasView = new CanvasView(AndroidMeMIDlet.DEFAULT_ACTIVITY);

        canvasView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        linearLayout.addView(canvasView);
    }

    public void setFullScreenMode(boolean fullScreen) {
        // do nothing, is this possible on android?
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

    public void repaint() {
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

    // Multi-touch. Not available in MIDP
    public void multitouchEvent(int[] type, int[] x, int[] y) {

    }

    protected void sizeChanged(int w, int h) {

    }

    @Override
    public View getView() {
        return this.linearLayout;
    }

    @Override
    public void disposeDisplayable() {
// JP        this.canvasView = null;
    }

    @Override
    public void initDisplayable(MIDlet midlet) {
// JP        if (this.canvasView == null) {
//            this.canvasView = new CanvasView(midlet.getActivity());
//        }
    }

    protected javax.microedition.lcdui.Graphics getGraphics() {
        return this.canvasView.graphics;
    }

    @Override
    public int getHeight() {
        int h = canvasView.canvasH;
        return (h > 0) ? h : super.getHeight();
    }


    class MyGestureDetector extends SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int keyCode = -1;

            if (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                int dist = (int) (e1.getX() - e2.getX());

                if (dist > SWIPE_MIN_DISTANCE) {
                    keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
                } else if (-dist > SWIPE_MIN_DISTANCE) {
                    keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
                }

                if (keyCode != -1) {
                    canvasView.onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                    canvasView.onKeyUp(keyCode, new KeyEvent(KeyEvent.ACTION_UP, keyCode));
                }
            }

            return (keyCode != -1);
        }
    }


    class CanvasView extends View {
        javax.microedition.lcdui.Graphics graphics = new Graphics(new android.graphics.Canvas());
        private int canvasY;
        private int canvasH;
        private int keyMenuCount;
        private View inputConnectionView;
        private GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector());

        public CanvasView(Context context) {
            super(context);

            setFocusable(true);
            setFocusableInTouchMode(true);
        }

        // Override > 2.1 only
        public boolean isOpaque() {
            return true;
        }

        long time = System.currentTimeMillis();

        @Override
        protected void onDraw(android.graphics.Canvas androidCanvas) {

            // Sanity check...
            if (androidCanvas == null || this.getWidth() <= 0 || this.getHeight() <= 0) {
                return;
            }

            // If Possible, try to not use more than 50% on CPU time on painting...
            long elapsed = System.currentTimeMillis() - time;

            if (elapsed < 10) {
                try {
//                    System.out.println("paint: elapsed " + elapsed);
                    Thread.sleep(10 - elapsed);
                } catch (InterruptedException e) {
                }
            }

            // WorkArround: View Re-size not done by the platform on landscape
            // virtual keyboard... Find what bit of the canvas are visible.

            // 1 - Get location in window, and keep top corner if not
            // displaying the virtual keyboard.
            int[] location = {0, 0};
            getLocationInWindow(location);

            if (location[1] > 0) {
                canvasY = location[1];
                canvasH = getHeight();
            } else {
                // 2 - Visible height if displaying the virtual keyboard
                canvasH = getHeight() + location[1] - canvasY;
            }

            if (graphicsBitmap != null) {

                // Check for size changes...
                if (graphicsBitmap.getWidth() != this.getWidth() ||
                    graphicsBitmap.getHeight() != canvasH) {

                    // Notify Canvas clients
                    try {
                        sizeChanged(Canvas.this.getWidth(), Canvas.this.getHeight());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    graphicsBitmap.recycle();
                    graphicsBitmap = null;
                }
            }

            if (graphicsBitmap == null) {

                // Help the GC to collect any previous graphicsBitmap
                graphics.setCanvas(null);

                try {
                    graphicsBitmap = Bitmap.createBitmap(this.getWidth(), canvasH, Bitmap.Config.RGB_565);
                } catch (OutOfMemoryError e) {
                    System.gc();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                    }
                    graphicsBitmap = Bitmap.createBitmap(this.getWidth(), canvasH, Bitmap.Config.RGB_565);
                }
                graphics.setCanvas(new android.graphics.Canvas(graphicsBitmap));
            }

            graphics.reset();

            paint(graphics);

            int graphicsY = getHeight() - canvasH;
            androidCanvas.drawBitmap(graphicsBitmap, 0, graphicsY, null);

//            if (touchDebug != null) {
//                Paint paint = new Paint();
//                paint.setStyle(Paint.Style.STROKE);
//                androidCanvas.drawCircle(touchDebug[0], touchDebug[1] + 0*graphicsY, 50, paint);
//                androidCanvas.drawCircle(touchDebug[2], touchDebug[3] + 0*graphicsY, 50, paint);
//            }


            showFramesPerSec(androidCanvas);

            time = System.currentTimeMillis();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            fixVirtualKeyboard();
            invalidate();

            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            System.out.println("onKeyDown -> " + keyCode);

            int keyCount = event.getRepeatCount();
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                keyMenuCount = keyCount;
                if (keyMenuCount == 1) {
                    toggleNativeTextInput();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK && keyCount == 1) {
                // kill the application on a "long back key" press
                // TODO: Should show some native Android UI for confirmation
                AndroidMeMIDlet.DEFAULT_ACTIVITY.getMIDlet().notifyDestroyed();
            }
            else {
                int meKeyCode = getKeyCode(event);
                if (keyCount == 0) {
                    keyPressed(meKeyCode);
                } else {
                    keyRepeated(meKeyCode);
                }
            }

            return true;
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {

            int meKeyCode = getKeyCode(event);
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (keyMenuCount == 0) {
                    // Simulate a press on menu
                    keyPressed(meKeyCode);
                    keyReleased(meKeyCode);
                }
            } else {
                keyReleased(meKeyCode);
            }
            return true;
        }

        private static final int POINTER_DRAGGED = 0;
        private static final int POINTER_PRESSED = 1;
        private static final int POINTER_RELEASED = 2;

        private static final int MULTI_TOUCH_MIN_DIST = 5;

        private int eventX;
        private int eventY;
        private int eventType = -1;

        private int[]  touchType;
        private int[]  touchX;
        private int[]  touchY;

//        private int[] touchDebug;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

//            if (gestureDetector.onTouchEvent(event)) {
//                return true;
//            }

            int actionCode = event.getAction() & 0xFF;

            int pointerCount = getPointerCount(event);
            int ySlide = getHeight() - canvasH;
            int x = Math.round(event.getX());
            int y = Math.round(event.getY() - ySlide);

            int action;
            switch (actionCode) {
                case MotionEvent.ACTION_DOWN: //$FALL-THROUGH$
                case 0x5:                     // ACTION_POINTER_DOWN (API Level 5)
                    action = POINTER_PRESSED;
                    break;
                case MotionEvent.ACTION_MOVE:
                    action = POINTER_DRAGGED;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    // A cancel can happen if the virtual keyboard is displayed.
                    // x & y will be zero, so we simulate a POINTER_RELEASED
                    // using the last known x/y values.
                    action = POINTER_RELEASED;
                    x = eventX;
                    y = eventY;
                    break;
                default:
                    // Handles ACTION_UP, ACTION_CANCEL, ACTION_OUTSIDE, etc...
                    action = POINTER_RELEASED;
                    break;
            }

            // Rounding can create "repeated" events... Ignore them.
            if (action != eventType || x != eventX || y != eventY) {

                eventType = action;
                eventX = x;
                eventY = y;

                try {
                    switch (action) {
                        case POINTER_PRESSED:
                            if (pointerCount == 1) {
                                Canvas.this.pointerPressed(x, y);
                            }
                            break;
                        case POINTER_DRAGGED:
                            Canvas.this.pointerDragged(x, y);
                            break;
                        default:
                            if (pointerCount == 1) {
                                Canvas.this.pointerReleased(x, y);
                            }
                            break;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            if (pointerCount > 1) {
                try {
                    // TODO: This code is hardcoded to only handle the first two points
                    // The HTC hardware for Nexus and Desire have serious limitation,
                    // sinse they are NOT true multitouch displays. They suffer from
                    // "shadowing" and are unreliable if the pressure between the two
                    // fingers are different...  Sensibility in x/y is also diferent.
                    // See: http://goo.gl/PMX9, http://goo.gl/g7it and http://goo.gl/RfWN

                    if (touchType == null) {

                        touchType = new int[2];
                        touchX = new int[2];
                        touchY = new int[2];

                        Arrays.fill(touchType, POINTER_RELEASED);
                    }

                    int pX0 = Math.round(getX(event, 0));
                    int pY0 = Math.round(getY(event, 0) - ySlide);

                    int pX1 = Math.round(getX(event, 1));
                    int pY1 = Math.round(getY(event, 1) - ySlide);

//                    touchDebug = new int[] {pX0, pY0, pX1, pY1};
//                    invalidate();

                    boolean fwEvent = false;

                    if (action == POINTER_DRAGGED &&  touchType[0] == POINTER_RELEASED) {
                        // POINTER_PRESSED was ignored
                        action = POINTER_PRESSED;
                    }
                    else if (action == POINTER_RELEASED &&  touchType[0] != POINTER_RELEASED) {
                        // POINTER_RELEASED can not be ignored, if there was a POINTER_PRESSED
                        fwEvent = true;
                    }

                    fwEvent |= updateMultitouch(touchX, pX0, pX1);
                    fwEvent |= updateMultitouch(touchY, pY0, pY1);

                    if (fwEvent) {
                        touchType[0] = action;
                        touchType[1] = action;
                        Canvas.this.multitouchEvent(clone(touchType),
                                clone(touchX), clone(touchY));
                    }
                }
                catch (Throwable e) {
                }
            }

            return true;
        }

        private boolean updateMultitouch(int[] touchPos, int p0, int p1) {

            // WORK-ARROUD: Nexus, Desire, etc.
            // 1 - Close points are unreliable. Any difference bigger than
            //     MIN_DIST automatically makes the points valid.
            // 2 - If both points (fingers) move at the same time, then they
            //     are also valid.
            boolean fwEvent = false;
            if (touchPos[0] != p0 || touchPos[1] != p1) {
                if (Math.abs(p0 - p1) > MULTI_TOUCH_MIN_DIST ||
                   (Math.abs(touchPos[0] - p0) > MULTI_TOUCH_MIN_DIST  &&
                    Math.abs(touchPos[1] - p1) > MULTI_TOUCH_MIN_DIST)) {

                    fwEvent = true;
                    touchPos[0] = p0;
                    touchPos[1] = p1;
                }
            }

            return fwEvent;
        }

        private int[] clone(int[] a) {

            int[] b = new int[a.length];
            for (int i = 0; i < b.length; i++) {
                b[i] = a[i];
            }
            return b;
        }

        // Android 1.6 helper method (getPointerCount() is API Level 5)
        private int getPointerCount(MotionEvent event) {
            try {
                return (Integer) event.getClass().getMethod("getPointerCount").invoke(event);
            }
            catch (Throwable ex) {
                return 1;
            }
        }

        // Android 1.6 helper method (getX(int) is API Level 5)
        private float getX(MotionEvent event, int pointerIndex) throws Exception {
            return (Float) MotionEvent.class.getMethod("getX", Integer.TYPE).invoke(event, pointerIndex);
        }

        // Android 1.6 helper method (getY(int) is API Level 5)
        private float getY(MotionEvent event, int pointerIndex) throws Exception {
            return (Float) MotionEvent.class.getMethod("getY", Integer.TYPE).invoke(event, pointerIndex);
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
                resultKeyCode = -12;
                break;
            case KeyEvent.KEYCODE_BACK :
                resultKeyCode = -11;
                break;
            case KeyEvent.KEYCODE_DEL :
                resultKeyCode = -8; // Backspace ascii
                break;
            case KeyEvent.KEYCODE_CALL :
                resultKeyCode = -10;
                break;
            case KeyEvent.KEYCODE_ENDCALL :
                resultKeyCode = -11; // Never called on Android...
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

        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            return (inputConnectionView == null) ? null : inputConnectionView.onCreateInputConnection(outAttrs);
        }

        private InputMethodManager getInputManager() {
            return (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        private void fixVirtualKeyboard() {
            getHandler().postDelayed(new Runnable() {
                public void run() {

                    // WorkArround: View Re-size not done by the platform on landscape
                    // virtual keyboard... Ask to scroll to the bottom of the view.
                    requestRectangleOnScreen(new Rect(0, getHeight() - 1, 1, getHeight()));

                    int h = getHeight();
                    if (requestRectangleOnScreen(new Rect(0, h - 1, 1, h), true)) {
                        invalidate();
                    }
                }
            }, 500);
        }

        public void showNativeTextInput() {
            fixVirtualKeyboard();
            getInputManager().showSoftInput(this, InputMethodManager.SHOW_FORCED);
        }

        public void hideNativeTextInput() {
            fixVirtualKeyboard();
            getInputManager().hideSoftInputFromWindow(getWindowToken(), 0);
        }

        public void toggleNativeTextInput() {
            fixVirtualKeyboard();
            getInputManager().toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        public void setInputConnectionView(View view) {
            if (inputConnectionView != view) {
                this.inputConnectionView = view;
                getInputManager().restartInput(this);
            }
        }

        public void sendText(CharSequence text) {
            int count = text.length();
            for (int i = 0; i < count; i++) {
                int meKeyCode = text.charAt(i);
                keyPressed(meKeyCode);
                keyReleased(meKeyCode);
            }
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
        return Display.getDisplay(AndroidMeMIDlet.DEFAULT_ACTIVITY.getMIDlet()).getCurrent() == this;
    }

    public void setTitle(Object object) {
        // TODO Auto-generated method stub
    }

    public void addOverlayView(View v) {
        linearLayout.addView(v);
    }

    public void removeOverlayView(View v) {
        linearLayout.removeView(v);
    }


    // -- debug code ---
    private long lastDrawTime;
    private String fpsStr = "0.0fps";
    private int nFrames;

    private void showFramesPerSec(android.graphics.Canvas androidCanvas) {
        nFrames++;
        long timeNow = System.currentTimeMillis();
        long timeDiff = timeNow - lastDrawTime;
        if (timeDiff > 1000) {
            long fps = nFrames * 10000 / timeDiff;
            fpsStr = (fps / 10) + "." + (fps % 10) + "fps";
            lastDrawTime = timeNow;
            nFrames = 0;
        }

        Paint paint = new Paint();

        int w = (int)(paint.measureText(fpsStr) + 2.0f);
        int h = paint.getFontMetricsInt(paint.getFontMetricsInt()) + 2;

        paint.setStyle(Paint.Style.FILL);
        androidCanvas.drawRect(2, 5, 2 + w, 5 + h, paint);

        paint.setColor(0xFFFFFFFF);
        androidCanvas.drawText(fpsStr, 3, 1 + h, paint);
    }
}

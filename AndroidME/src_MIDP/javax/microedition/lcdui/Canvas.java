package javax.microedition.lcdui;

import javax.microedition.midlet.MIDlet;

import net.yura.android.AndroidMeMIDlet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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

    class CanvasView extends View {
        javax.microedition.lcdui.Graphics graphics = new Graphics(new android.graphics.Canvas());
        private int canvasY;
        private int canvasH;
        private int keyMenuCount;
        private View inputConnectionView;

        public CanvasView(Context context) {
            super(context);

            setFocusable(true);
            setFocusableInTouchMode(true);
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
                    System.out.println("paint: elapsed " + elapsed);
                    Thread.sleep(10);
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
                        sizeChanged(this.getWidth(), canvasH);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    graphicsBitmap = null;
                }
            }

            if (graphicsBitmap == null) {

                // Help the GC to collect any previous graphicsBitmap
                graphics.setCanvas(null);

                graphicsBitmap = Bitmap.createBitmap(this.getWidth(), canvasH, Bitmap.Config.ARGB_8888);
                graphics.setCanvas(new android.graphics.Canvas(graphicsBitmap));
            }

            graphics.reset();

            paint(graphics);

            int graphicsY = getHeight() - canvasH;
            androidCanvas.drawBitmap(graphicsBitmap, 0, graphicsY, null);

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

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = Math.round(event.getX());
            int y = Math.round(event.getY() - getHeight() + canvasH);

            // System.out.println("(" + x + "," + y + "," + event.getAction() + ")");
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Canvas.this.pointerPressed(x, y);
                break;
            case MotionEvent.ACTION_UP:
                Canvas.this.pointerReleased(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas.this.pointerDragged(x, y);
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
}

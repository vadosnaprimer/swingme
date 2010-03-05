/*
 * Created on 27.06.2004 by Stefan Haustein
 */
package javax.microedition.lcdui;

import javax.microedition.midlet.ApplicationManager;

/**
 * @author Stefan Haustein
 * @API MIDP-2.0
 */
public abstract class CustomItem extends Item {

  /**
   * @API MIDP-2.0
   */

  protected static final int NONE = 0;

  /**
   * @API MIDP-2.0
   */
  protected static final int TRAVERSE_HORIZONTAL = 1;

  /**
   * @API MIDP-2.0
   */
  protected static final int TRAVERSE_VERTICAL = 2;

  /**
   * @API MIDP-2.0
   */
  protected static final int KEY_PRESS = 4;

  /**
   * @API MIDP-2.0
   */
  protected static final int KEY_RELEASE = 8;

  /**
   * @API MIDP-2.0
   */
  protected static final int KEY_REPEAT = 0x010;

  /**
   * @API MIDP-2.0
   */
  protected static final int POINTER_PRESS = 0x020;

  /**
   * @API MIDP-2.0
   */
  protected static final int POINTER_RELEASE = 0x040;

  /**
   * @API MIDP-2.0
   */
  protected static final int POINTER_DRAG = 0x040;

  /**
   * @API MIDP-2.0
   */
  protected CustomItem(String label) {
    super(label);
    lines.addElement(new ScmCustomItem(this));
  }

  /**
   * @API MIDP-2.0
   */

  public int getGameAction(int keyCode) {
    return ApplicationManager.getInstance().getGameAction(keyCode);
  }

  /**
   * @API MIDP-2.0
   */
  protected int getInteractionModes() {
    return 0;
  }

  /**
   * @API MIDP-2.0
   */
  protected abstract int getMinContentHeight();

  /**
   * @API MIDP-2.0
   */
  protected abstract int getMinContentWidth();

  /**
   * @API MIDP-2.0
   */
  protected abstract int getPrefContentHeight(int width);

  /**
   * @API MIDP-2.0
   */
  protected abstract int getPrefContentWidth(int height);

  /**
   * @API MIDP-2.0
   */
  protected void hideNotify() {

  }

  /**
   * @ME4SE UNSUPPORTED
   * 
   */

  protected void invalidate() {
    // TODO throw new RuntimeException("NYI");
  }

  /**
   * @API MIDP-2.0
   */
  protected void keyPressed(int keyCode) {
  }

  /**
   * @API MIDP-2.0
   */
  protected void keyReleased(int keyCode) {
  }

  /**
   * @API MIDP-2.0
   */
  protected void keyRepeated(int keyCode) {
  }

  /**
   * @API MIDP-2.0
   */
  protected abstract void paint(Graphics g, int w, int h);

  /**
   * @API MIDP-2.0
   */
  protected void pointerDragged(int x, int y) {
  }

  /**
   * @API MIDP-2.0
   */
  protected void pointerPressed(int x, int y) {
  }

  /**
   * @API MIDP-2.0
   */
  protected void pointerReleased(int x, int y) {
  }

  /**
   * @API MIDP-2.0
   */
  protected void repaint() {
    if (form != null && form.container != null) {
      form.container.repaint();
    }
  }

  /**
   * @API MIDP-2.0
   */
  protected void repaint(int x, int y, int w, int h) {
    repaint();
  }

  /**
   * @API MIDP-2.0
   */
  protected void showNotify() {

  }

  /**
   * @API MIDP-2.0
   */
  protected void sizeChanged(int w, int h) {

  }

  /**
   * @API MIDP-2.0
   */
  protected boolean traverse(int dir, int viewportWidth, int viewportHeight,
      int[] visRect_inout) {
    return false;
  }

  /**
   * @API MIDP-2.0
   */
  protected void traverseOut() {
  }
}
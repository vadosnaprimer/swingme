package javax.microedition.lcdui;

import java.util.Vector;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public abstract class Item {

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_DEFAULT = 0;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_LEFT = 1;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_RIGHT = 2;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_CENTER = 3;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_TOP = 4;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_BOTTOM = 0x20;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_VCENTER = 0x30;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_NEWLINE_BEFORE = 0x100;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_NEWLINE_AFTER = 0x200;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_SHRINK = 0x400;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_EXPAND = 0x800;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_VSHRINK = 0x1000;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_VEXPAND = 0x2000;

  /**
   * @API MIDP-2.0
   */
  public static final int LAYOUT_2 = 0x4000;

  /**
   * @API MIDP-2.0
   */
  public static final int PLAIN = 0;

  /**
   * @API MIDP-2.0
   */
  public static final int HYPERLINK = 1;

  /**
   * @API MIDP-2.0
   */
  public static final int BUTTON = 2;

  Form form;
  Form saveForm;
  Vector commands = null;
  int layout;
  ItemCommandListener listener;
  Command defaultCommand;
  int preferredWidth;
  int preferredHeight;

  ScmDeviceLabel label = new ScmDeviceLabel("label", null, false);

  /**
   * The lines are stored without ScmContainer and are inserted separately in
   * the form component in order to simplify scrolling
   */

  Vector lines = new Vector();
  int appearanceMode;

  Item() {
    this(null, PLAIN);
  }

  Item(String lbl) {
    this(lbl, PLAIN);
    setLabel(lbl);
  }

  Item(String lbl, int appearanceMode) {
    this.appearanceMode = appearanceMode;
    setLabel(lbl);
    label.compact = true;
    label.highlight = false;
    lines.addElement(label);
  }

  int delete() {
    saveForm = form;
    return form == null ? -1 : form.delete(this);
  }

  void notifyChanged() {
    if (form != null && form.itemStateListener != null)
      form.itemStateListener.itemStateChanged(this);
  }

  void readd(int index) {
    if (index == -1)
      return;
    saveForm.insert(index, this);
  }

  /**
   * @API MIDP-1.0
   */
  public void setLabel(String lbl) {
    label.setText(lbl == null ? null : lbl.replace('\n', ' '));
    label.invalidate();
    update();
  }

  /**
   * @API MIDP-1.0
   */
  public String getLabel() {
    return label.getText();
  }

  /**
   * Gets the layout directives used for placing the item.
   * 
   * @return a combination of layout directive values
   * 
   * @API MIDP-2.0
   */
  public int getLayout() {
    return layout;
  }

  /**
   * @API MIDP-2.0
   * @remark Currently the layout is stored, but it has no effect.
   */
  public void setLayout(int layout) {
    this.layout = layout;
    update();
  }

  /**
   * @API MIDP-2.0
   */
  public void addCommand(Command cmd) {
    System.out.println("Adding item command: " + cmd);
    if (commands == null)
      commands = new Vector();

    if (commands.indexOf(cmd) == -1) {
      commands.addElement(cmd);
    }

    update();
  }

  /**
   * @API MIDP-2.0
   */
  public void removeCommand(Command cmd) {
    if (commands != null)
      commands.remove(cmd);

    update();
  }

  /**
   * @API MIDP-2.0
   */
  public void setItemCommandListener(ItemCommandListener l) {
    listener = l;
  }

  /**
   * @API MIDP-2.0
   */
  public int getPreferredWidth() {
    return preferredWidth;
  }

  /**
   * @API MIDP-2.0
   */
  public int getPreferredHeight() {
    return preferredHeight;
  }

  /**
   * @API MIDP-2.0
   */
  public void setPreferredSize(int width, int height) {
    this.preferredHeight = height;
    this.preferredWidth = width;

  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNIMPLEMENTED
   */
  public int getMinimumWidth() {
    System.out.println("Item.getMinimumWidth() called with no effect!");
    return -4711;
  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNIMPLEMENTED
   */
  public int getMinimumHeight() {
    return -4711;
  }

  /**
   * @API MIDP-2.0
   */
  public void setDefaultCommand(Command cmd) {
    if (defaultCommand != null) {
      removeCommand(defaultCommand);
    }
    if (cmd != null) {
      addCommand(cmd);
    }

    defaultCommand = cmd;
  }

  void update() {
    if (form != null)
      form.container.updateButtons();
  }

  /**
   * @API MIDP-2.0
   * @ME4SE UNIMPLEMENTED
   */
  public void notifyStateChanged() {
    System.out.println("Item.notyfyStateChanged() called with no effect!");
  }
}

/*
 * Created on 06.11.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package javax.microedition.lcdui;

import java.awt.Color;

/**
 * 
 * @author haustein
 * @ME4SE INTERNAL
 */

class ScmImage extends ScmDeviceComponent {
  
  Image image;
  int layout;
  boolean focusGained;

  ScmImage(Item item, Image image, int layout) {
    super(item, "image", false);
    this.image = image;
    this.layout = layout;
    // if the appereance mode is set to button or hyperlik, 
    // lets make the ImageItem selectable. Otherwise not.
    if (item.appearanceMode == Item.BUTTON || item.appearanceMode == Item.HYPERLINK) {
      setFocusable(true);  
    }
  }

  public void paint(java.awt.Graphics g) {
    if (image != null) {
      int xp = 0;

      if ((layout & 2) != 0)
        xp = getParent().getWidth() - image.getWidth();

      if ((layout & ImageItem.LAYOUT_CENTER) == ImageItem.LAYOUT_CENTER)
        xp /= 2;

      g.drawImage(image._image, xp, 0, null);
      if (getFocusOwner() != null && this == getFocusOwner()) {
        g.setColor(java.awt.Color.BLACK);
        g.drawRect(xp, 0, image._image.getWidth()-1, image._image.getHeight()-1);
      }
    }
  }

  public java.awt.Dimension getMinimumSize() {
    if (image == null)
      return new java.awt.Dimension(0, 0);

    return new java.awt.Dimension(image.getWidth(), image.getHeight());
  }

  public void focusGained() {
    getParent().focusGained();
  }

  public java.awt.Dimension getPreferredSize() {
    return getMinimumSize();
  }
}
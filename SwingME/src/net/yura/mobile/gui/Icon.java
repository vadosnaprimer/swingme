/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui;

import java.io.IOException;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 * @see javax.swing.Icon
 * @see javax.swing.ImageIcon
 */
public class Icon {

    protected int px,py,width,height;

    private Object image;

    public Icon() {

    }

    /**
     * @see javax.swing.ImageIcon#ImageIcon(java.awt.Image) ImageIcon.ImageIcon
     */
    public Icon(Image img) {
        image = img;
        height = img.getHeight();
        width = img.getWidth();
    }

    /**
     * @see javax.swing.ImageIcon#ImageIcon(java.lang.String) ImageIcon.ImageIcon
     * @see java.lang.Class#getResourceAsStream(java.lang.String) Class.getResourceAsStream
     */
    public Icon(String imageName) throws IOException {
        this( Image.createImage( Midlet.getResourceAsStream(imageName) ) );
    }

    /**
     * @see javax.swing.Icon#getIconHeight() Icon.getIconHeight
     */
    public int getIconHeight() {
        return height;
    }

    /**
     * @see javax.swing.Icon#getIconWidth() Icon.getIconWidth
     */
    public int getIconWidth() {
        return width;
    }

    /**
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int) Icon.paintIcon
     */
    public void paintIcon(Component c, Graphics2D g, int x, int y) {
        if (image instanceof Image) {
            g.drawRegion((Image)image, px, py, width, height, 0, x, y);
        }
    }

    /**
     * This method will only work if this Icon is actually a ImageIcon
     * otherwise it will return null
     * @see javax.swing.ImageIcon#getImage() ImageIcon.getImage
     */
    public Image getImage() {
        if (image instanceof Image) {
            Image img = (Image)image;
            if (px==0&&py==0&&width==img.getWidth()&&height==img.getHeight()) {
                return img;
            }
            else {
                return Image.createImage(img, px, py, width, height, 0);
            }
        }
        return null;
    }

    /**
     * @see java.awt.image.BufferedImage#getSubimage(int, int, int, int)
     */
    public Icon getSubimage(int x, int y, int w, int h) {
        Icon i = new Icon( (Image)image );
        i.px = px + x;
        i.py = py + y;
        i.width = w;
        i.height = h;
        return i;
    }
}

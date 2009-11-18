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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class Icon {

    protected int height;
    protected int width;

    private Object image;

    public Icon() {
        
    }

    public Icon(Image img) {
        image = img;
        height = img.getHeight();
        width = img.getWidth();
    }

    public Icon(String imageName) throws IOException {
        this( Image.createImage(imageName) );
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
            g.drawImage((Image)image, x, y);
        }
    }

    public Image getImage() {
        if (image instanceof Image) {
            return (Image)image;
        }
        return null;
    }

}

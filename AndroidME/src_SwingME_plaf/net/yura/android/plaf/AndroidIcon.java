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

package net.yura.android.plaf;


import android.graphics.drawable.Drawable;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Component;

/**
 * @author Yura Mamyrin
 */
public class AndroidIcon extends Icon {
    private Drawable drawable;

    public AndroidIcon(Drawable drawable) {
        this.drawable= drawable;

        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();
    }

    public AndroidIcon(Drawable drawable, int h, int w) {
    	this.drawable= drawable;
    	width = w;
    	height = h;
    }

    public void paintIcon(Component c, Graphics2D g, int x, int y) {
        AndroidBorder.setDrawableState( c, drawable);
        android.graphics.Canvas canvas = g.getGraphics().getCanvas();
        int tx = g.getGraphics().getTranslateX();
        int ty = g.getGraphics().getTranslateY();
        drawable.setBounds(tx+x, ty+y, tx+x+width, ty+y+height);
        drawable.draw(canvas);
    }
    
    public Drawable getDrawable() {
        return drawable;
    }
}

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
    private Drawable  drawable;

    public AndroidIcon(Drawable drawable) {
        this.drawable= drawable;

        //TODO:
        width = Math.max(20, drawable.getIntrinsicWidth());
        height = Math.max(20, drawable.getIntrinsicHeight());
    }

    public void paintIcon(Component c, Graphics2D g, int x, int y) {

        int state = c.getCurrentState();
        AndroidBorder.setDrawableState(state, drawable);

        android.graphics.Canvas canvas = g.getGraphics().getCanvas();
        drawable.setBounds(x, y, width, height);
        drawable.draw(canvas);
    }
}

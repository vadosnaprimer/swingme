/* Copyright (c) 2002,2003, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.me4se.scm;

import java.awt.*;

public class ScmScrollBar extends ScmComponent {

	//    public static final int HORIZONTAL = 1;
	//    public static final int VERTICAL = 2;

	int value;
	int maximum = 100;
	int visibleAmount = 1;
	int minimumHandleSize = 5;

	//   int orientation;

	Color foreground = Color.black;

	/*    public ScmScrollBar () {
	
	#	if (orientation != HORIZONTAL 
	    && orientation != VERTICAL) throw new
		IllegalArgumentException ("orientation");
	this.orientation = orientation;
	}
	*/

	public int getMaximum() {
		return maximum;
	}

	public int getValue() {
		return value;
	}

	public void paint(Graphics g) {
		super.paint(g);

		if (maximum == 0)
			return;

		ScmComponent c = this;
		while (c.background == null && c.parent != null) {
			c = c.parent;
		}

		Color bg = c.background == null ? Color.white : c.background;

		g.setColor(new Color((foreground.getRed() + bg.getRed()) / 2, (foreground.getGreen() + bg.getGreen()) / 2, (foreground.getBlue() + bg.getBlue()) / 2));

		int range = getHeight() - minimumHandleSize;
		int pos = (range * value) / (maximum) + minimumHandleSize / 2;

		int size = range * visibleAmount / maximum;

		g.fillRect(2, 0, w - 2, pos - 1);
		g.fillRect(2, pos + size + 1, w - 2, h - pos - size - 1);

		g.setColor(foreground);

		//	if (orientation == HORIZONTAL) {
		//	    int mx = (w * value) / maximum;
		//    g.fillRect (0, 0, mx, h);
		//  	}
		//else {

		g.fillRect(1, pos, w - 1, size);

		//  } 
	}

	public void setForeground(Color fg) {
		foreground = fg;
	}

	public void setMaximum(int mx) {
		this.maximum = mx;
		//repaint(); @MH commented out to avoid endless paint loop 
	}

	public void setValue(int value) {
		this.value = value;
		//repaint(); @MH commented out to avoid endless paint loop 
	}

	public void setVisibleAmount(int visibleAmount) {
		this.visibleAmount = visibleAmount;
		//repaint(); @MH commented out to avoid endless paint loop 
	}

	public Dimension getMinimumSize() {
		return new Dimension(4, 10);
	}
}

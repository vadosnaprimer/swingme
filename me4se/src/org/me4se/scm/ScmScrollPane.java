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

/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */

import java.awt.*;

public class ScmScrollPane extends ScmContainer {

    ScmComponent contentPane;
    ScmScrollBar verticalBar;

    public boolean scrollRequest(int x, int y, int w, int h) {
        if (y < 0) {
            contentPane.y -= y;
            repaint();
            return true;
        }

        if (y + h > this.h) {
            contentPane.y += this.h - y - h;
            repaint();
            return true;
        }

        return false;
    }

    public void setVerticalBar(ScmScrollBar verticalBar) {
        if (this.verticalBar != null)
            super.remove(verticalBar);

        this.verticalBar = verticalBar;

        if (verticalBar != null)
            super.add(verticalBar, getComponentCount());
    }

    public void add(ScmComponent c, int index) {
        if (contentPane != null)
            throw new RuntimeException("Only one component allowed");
        contentPane = c;
        super.add(c, index);
    }

    public void remove(int index) {
        if (getComponent(index) == contentPane)
            contentPane = null;

        super.remove(index);
    }

    public void paint(Graphics g) {
        if (verticalBar != null && contentPane != null) {
            verticalBar.setValue(-contentPane.y);
            verticalBar.setMaximum(contentPane.h);
            verticalBar.setVisibleAmount(h);
        }

        super.paint(g);
    }

    public void doLayout() {

        int w = this.w;

        contentPane.h = contentPane.getMinimumSize().height;

        if (verticalBar != null) {
            if (contentPane.h > h) {
                Dimension d = verticalBar.getMinimumSize();
                w -= d.width;
                verticalBar.setBounds(w, 0, d.width, h);
                //verticalBar.setValue (-contentPane.y);
                //verticalBar.setMaximum (contentPane.h);
                //verticalBar.setVisibleAmount (h);
            }
            else {
                verticalBar.setBounds(w, 0, 0, h);
            }
        }

        contentPane.w = w;

        contentPane.doLayout();
    }

}

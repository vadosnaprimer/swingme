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

public class ScmList extends ScmContainer {

  public void doLayout() {

    // Dimension ms = ;
    // int hf = (256 * h) / getMinimumSize ().height;

    int y = 0;
    int w = getWidth();
    for (int i = 0; i < childCount; i++) {
      ScmComponent c = children[i];
      int h = c.getMinimumSize().height;
      c.setBounds(0, y, w, h);
      y += h;
      c.doLayout();
    }
  }

  public Dimension getMinimumSize() {
    Dimension result = new Dimension();

    for (int i = 0; i < childCount; i++) {
      Dimension cMin = children[i].getMinimumSize();
      result.height += cMin.height;
      result.width = Math.max(cMin.width, result.width);
    }
    return result;
  }

}

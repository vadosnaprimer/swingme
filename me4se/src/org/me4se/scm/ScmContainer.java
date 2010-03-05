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

import java.awt.Graphics;

public class ScmContainer extends ScmComponent {

    int childCount;
    boolean valid;

    ScmComponent[] children = new ScmComponent[10];
    ScmComponent mouseOwner;
    ScmComponent focus;

    public void paint(java.awt.Graphics g) {

        //        if (!valid) validate ();

        super.paint(g);

        java.awt.Rectangle r = g.getClipBounds();
   //     System.out.println("r: "+r);
        for (int i = 0; i < childCount; i++) {
            ScmComponent c = children[i];
            
            // workaround necessary for linux awt clipping bug (!!!)
            
		 	if(c.invisible || (r != null && (c.x > r.x+r.width || c.y > r.y+r.height 
				|| c.x+c.w < r.x || c.y+c.h < r.y)))
					continue;
				
            Graphics g2 = g.create(c.x, c.y, c.w, c.h);
//			g2.drawLine(0,0,c.w, c.h);
//			g2.drawString(""+r, w-20, 10); 
//            g.translate(c.x, c.y);
 //           g.clipRect(0, 0, c.w, c.h);
            children[i].paint(g2);
  //          g.translate(-c.x, -c.y);
     //       if (r != null)
   //             g.setClip(r.x, r.y, r.width, r.height);
   //         else
    //            g.setClip(0, 0, w, h);
        }
    }

    /*   getFocusOwner() {
           if (super.getFocusOwner == this)   
       }*/

    public void add(ScmComponent component) {
        add(component, childCount);
    }

    public void add(ScmComponent component, int index) {

        if (index < 0 || index > childCount)
            throw new IndexOutOfBoundsException();

//        if (component.parent != null)
//            throw new RuntimeException("child is owned");

        if (childCount >= children.length) {
            ScmComponent[] save = children;
            children = new ScmComponent[childCount + 4];
            System.arraycopy(save, 0, children, 0, childCount);
        }

        if (index < childCount)
            System.arraycopy(
                children,
                index,
                children,
                index + 1,
                childCount - index);

        children[index] = component;
        childCount++;
        component.parent = this;
        invalidate();
    }

    public ScmComponent getComponentAt(int x, int y) {
        for (int i = childCount - 1; i >= 0; i--) {
            ScmComponent c = children[i];
            if (c.contains(x - c.x, y - c.y))
                return c;
        }
        return null;
    }

    public int getComponentCount() {
        return childCount;
    }

    public int indexOf(ScmComponent c) {
        for (int i = 0; i < childCount; i++) {
            if (c == children[i]) {
                return i;
            }
        }
        return -1;
    }

    public boolean keyPressed(String keyCode) {
        return focus != null ? focus.keyPressed(keyCode) : false;
    }

    public boolean keyReleased(String keyCode) {
        return focus != null ? focus.keyReleased(keyCode) : false;
    }

    public boolean keyRepeated(String key) {
        return focus != null ? focus.keyRepeated(key) : false;
    }

    public void remove(int index) {
        children[index].parent = null;
        System.arraycopy(
            children,
            index + 1,
            children,
            index,
            (--childCount) - index);
        
      
    }

    public void remove(ScmComponent c) {
        int i = indexOf(c);
        if (i != -1)
            remove(i);
    }

    public ScmComponent getComponent(int index) {
        if (index < 0 || index >= childCount)
            throw new IndexOutOfBoundsException();
        return children[index];
    }

    /*
    public void validate() {
    doLayout ();
        for (int i = 0; i < childCount; i++) {
            if (children [i] instanceof ScmContainer) {
                ((ScmContainer) children [i]).validate();
            }   
        }        
        valid = true;
    }
    */

    public void doLayout() {
        for (int i = 0; i < childCount; i++) {
            if (children[i] instanceof ScmContainer) {
                  ScmComponent child = (ScmContainer) children[i];
                  // YURA if child is fullscreen then resize it to fill all the screen
                  if (child.fullScreen) {
                      child.setBounds(0, 0, w, h);
                  }
                  child.doLayout();
            }
        }
    }

    public boolean mouseClicked(
        int button,
        int x,
        int y,
        int mask,
        int clicks) {
        ScmComponent c = getComponentAt(x, y);
        return (c != null)
            ? c.mouseClicked(button, x - c.x, y - c.y, mask, clicks)
            : false;
    }

    public boolean mouseReleased(int button, int x, int y, int mask) {
        return (mouseOwner != null)
            ? mouseOwner.mouseReleased(
                button,
                x - mouseOwner.x,
                y - mouseOwner.y,
                mask)
            : false;
    }

    public boolean mousePressed(int button, int x, int y, int mask) {
        ScmComponent c = getComponentAt(x, y);
        return (c != null)
            ? c.mousePressed(button, x - c.x, y - c.y, mask)
            : false;
    }

    public boolean mouseDragged(int x, int y, int mask) {
        return mouseOwner != null
            ? mouseOwner.mouseDragged(x - mouseOwner.x, y - mouseOwner.y, mask)
            : false;

    }

    public boolean mouseMoved(int x, int y, int mask) {
        //("mm " + x + "," + y);

        ScmComponent newOwner = getComponentAt(x, y);

        if (newOwner != mouseOwner) {
            if (mouseOwner != null)
                mouseOwner.mouseExited();
            if (newOwner != null)
                newOwner.mouseEntered();
            mouseOwner = newOwner;
        }

        return mouseOwner != null
            ? mouseOwner.mouseMoved(x - mouseOwner.x, y - mouseOwner.y, mask)
            : false;
    }

}

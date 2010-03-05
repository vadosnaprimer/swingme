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

public class ScmComponent {

    ScmContainer parent;
    int x;
    int y;
    int w;
    int h;
    Color background = null;//new Color((int) (Math.random() * 0x0ffffff));
    boolean focusable;
    
    
    /** Allows to simply hide a component by setting visible to false */
    
    boolean invisible;
    
    /** 
     * Returns true when the given coordinates are contained in this component,
     * false otherwise. The default implementation covers the rectangular
     * area described by the component with and height. For components with
     * other shapes, please overwrite this method accordingly. 
     */

    protected boolean fullScreen;
    
    public boolean contains (int cx, int cy) {
        return cx >= 0 && cx <= w 
             && cy >= 0 && cy <= h;
    }


    /** 
     * Returns the component in the same tree that currently owns the focus.
     * If this component has no parent container, or the focus is not set,
     * null is returned */

    public ScmComponent getFocusOwner() {
        ScmContainer p = parent;
        if (p == null) return null;

        while (p.parent != null) 
            p = p.parent;
            
        // topmost parent
        
        if (p.focus == null) return null;
        
        while (true) {
            ScmComponent f = p.focus;            
            if (f == null) return p;
            if (!(f instanceof ScmContainer))
                return f; 
            p = (ScmContainer) f;
        }                              
    }

    /** Layouts this component and all child components */

    public void doLayout() {
    }

    /** 
     * Returns the horizontal position of this component relative
     * to the upper left corner of the parent container */ 

    public int getX() {
        return x;
    }

    /** 
     * Returns the vertical position of this component relative
     * to the upper left corner of the parent container */ 

    public int getY() {
        return y;
    }

    /** 
     * Returns the width of this component  */

    public int getWidth() {
        return w;
    }

    /** 
     * Returns the height of this component  */

    public int getHeight() {
        return h;
    }

    /** Returns the parent container of this component */
   
    public ScmContainer getParent() {
        return parent;
    }

   
    /** 
     * Returns true if this component is the focus owner,
     * false otherwise */
    
    public boolean hasFocus () {
        return this == getFocusOwner ();
    }

    /** 
     * Invalidates the layout of this component and all parent
     * components. Requests a repaint of all parents.
     * doLayout() is called automatically before the next
     * call to paint */

    public void invalidate() {
        //        valid = false;
        if (parent != null)
            parent.invalidate();
    }

    /** 
     * Paints this component. If the background color is not null,
     * the rectangular area covered by this component is filled
     * with the background color. If this component is a container,
     * all child components are painted. */

    public void paint(java.awt.Graphics g) {
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, w, h);
        }
    }

    int getDepth(){
    	return parent == null ? 1 : 1+parent.getDepth();
    }

    public boolean scrollRequest (int x, int y, int w, int h) {
        return parent != null 
            ? parent.scrollRequest(x+this.x, y+this.y, w, h)
            : false;
    }
    

    public boolean keyRepeated (String c) {
        return keyPressed(c);
    }


    public boolean keyPressed (String keyCode) {
        return false;
    }
    
    public boolean keyReleased (String keyCode) {
        return false;
    }

    /** 
     * Indicates that the mouse is in the area deteminded by contains() */

    public void mouseEntered() {
    }

    /** 
     * Indicates that the mouse has left the area deteminded by contains() */

    public void mouseExited() {
    }


    public boolean mouseClicked(int button, int x, int y, int modifiers, int clicks) {
        if (!focusable || hasFocus () || button != 1 || clicks != 1) return false;
        
        requestFocus ();
        return true;
    }


    public boolean mouseReleased(int button, int x, int y, int modifiers) {
        return false;
    }

    public boolean mousePressed(int button, int x, int y, int modifiers) {
        return false;
    }

    public boolean mouseDragged(int x, int y, int modifiers) {
        return false;
    }

    public boolean mouseMoved(int x, int y, int modifiers) {
        return false;
    }


    public Color getBackground() {
        return background;
    }

    public Graphics getGraphics() {
    	if(invisible) return null;
    	if(parent == null) return null;
        Graphics g = parent.getGraphics();
        return (g == null) ? null : g.create(x, y, w, h);
	}



    /*    	//g.drawLine(x, 0, -w, h);
            //g.translate(x, y);
            //g.clipRect(0, 0, w, h);
        }
        return g; */


    public Dimension getMinimumSize() {
        return new Dimension (0, 0);
    }

 
    public Dimension getSize() {
        return new Dimension(w, h);
    }
    
    public void requestFocus() {
     
        ScmComponent old = getFocusOwner ();
        if (old == this) return;
        
        ScmComponent f = this;
        
        while (f.parent != null) {
           f.parent.focus = f;
           f = f.parent;
        }

        if (old != null) {
            old.focusLost ();
            old.repaint ();
        }
        focusGained ();
        scrollRequest (0, 0, w, h);
        repaint ();
        // TO DO Item Repaint fixed?
        if(parent != null) parent.repaint();
//        System.out.println("focus gained by: "+this);
    }
           
    
    public void focusLost () {
    	// TODO nothing is done here?
    }
    
    public void focusGained () {
    	// TODO nothing is done here?
    }


    public void repaint() {
        if (parent != null) 
	    parent.repaint(x, y, w, h);
    }

    public void repaint(int x, int y, int w, int h) {
        if (parent != null)
            parent.repaint(this.x + x, this.y + y, w, h);
    }

    public void setBounds(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        doLayout();

        invalidate();
    }

    public void setX(int x) {
        setBounds (x, y, w, h);
    }

    public void setY(int y) {
        setBounds (x, y, w, h);
    }

    public void setWidth(int w) {
        setBounds (x, y, w, h);
    }

    public void setHeight(int h) {
        setBounds (x, y, w, h);
    }

    /** Sets the background color. If no background color is set (null value), 
    the background is transparent */

    public void setBackground(Color background) {
        this.background = background;
        repaint();
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean getFocusable() {
        return focusable && !invisible;
    }
    
    public void setInvisible(boolean iv){
    	invisible = iv;
    }
    
    public boolean getInvisible(){
    	return invisible;
    }
    
    public Rectangle getBounds () {
        return new Rectangle (x, y, w, h);
    }
    
}

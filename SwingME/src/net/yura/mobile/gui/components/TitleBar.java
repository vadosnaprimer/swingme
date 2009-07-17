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

package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.GridLayout;

/**
 * @author Yura Mamyrin
 */
public class TitleBar extends Panel implements ActionListener {

    private Label title;
    
    public TitleBar(String title,Image icon,boolean resize,boolean move,boolean hide,boolean max,boolean close) {
        super(new BorderLayout());
        setName("TitleBar");
        
        Panel buttonPanel = new Panel( new GridLayout(1,0,2) );
        if (resize) {
            Button b = new Button("#");
            buttonPanel.add(b);
            b.addActionListener(this);
            b.setActionCommand("resize");
        }
        if (move) {
            Button b = new Button("+");
            buttonPanel.add(b);
            b.addActionListener(this);
            b.setActionCommand("move");
        }
        if (hide) {
            Button b = new Button("_");
            b.addActionListener(this);
            b.setActionCommand(Window.CMD_MIN);
            buttonPanel.add(b);
        }
        if (max) {
            Button b = new Button("[]");
            b.addActionListener(this);
            b.setActionCommand(Window.CMD_MAX);
            buttonPanel.add(b);
        }
        if (close) {
            Button b = new Button("X");
            b.addActionListener(this);
            b.setActionCommand(Window.CMD_CLOSE);
            buttonPanel.add(b);

        }

        // always want it to take the style of this instead
        buttonPanel.background=-1;
        
        this.title = new Label( title,icon );
        
        add(this.title);
        add(buttonPanel,Graphics.RIGHT);

    }
    
    /**
     * @param title the title to be displayed in the frame's border
     * @see java.awt.Frame#setTitle(java.lang.String) Frame.setTitle
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }
    /**
     * @param img the icon image to be displayed
     * @see java.awt.Frame#setIconImage(java.awt.Image) Frame.setIconImage
     */
    public void setIconImage(Image img) {
        title.setIcon(img);
    }
    
    private int oldX,oldY;
    private boolean move,resize;
    private Component old;
    
    public void pointerEvent(int type, int x, int y, KeyEvent keys) {

        if (type == DesktopPane.PRESSED) {
            oldX=x;
            oldY=y;

        }
        else if (type == DesktopPane.DRAGGED) {

            Window owner = getWindow();
            
            owner.setLocation(owner.getX()+(x-oldX),owner.getY()+(y-oldY));
            DesktopPane.getDesktopPane().fullRepaint();

        }

    }

    public boolean keyEvent(KeyEvent keypad) {

            Window owner = getWindow();
        
            if (keypad.isDownAction(Canvas.LEFT)) {
                
                if (move) { owner.setLocation(owner.getX()-2,owner.getY()); }
                else if (resize) { owner.setSize(owner.getWidth()-2, owner.getHeight()); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.RIGHT)) {
                
                if (move) { owner.setLocation(owner.getX()+2,owner.getY()); }
                else if (resize) { owner.setSize(owner.getWidth()+2, owner.getHeight()); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.UP)) {
                
                if (move) { owner.setLocation(owner.getX(),owner.getY()-2); }
                else if (resize) { owner.setSize(owner.getWidth(), owner.getHeight()-2); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.DOWN)) {
                
                if (move) { owner.setLocation(owner.getX(),owner.getY()+2); }
                else if (resize) { owner.setSize(owner.getWidth(), owner.getHeight()+2); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.FIRE)) {
                
                move = false;
                resize = false;
                old.requestFocusInWindow();
                return true;
            }
            return false;
    }
    
    // if we loseFocus to some random component then resize or move can STILL be true
    public void actionPerformed(String actionCommand) {

        if (resize || move) {
                move = false;
                resize = false;
        }
        else if ("move".equals(actionCommand)) {
 
                old = getWindow().getFocusOwner();
                requestFocusInWindow();
                move = true;

        }
        else if ("resize".equals(actionCommand)) {
 
                old = getWindow().getFocusOwner();
                requestFocusInWindow();
                resize = true;

        }
        else {
            getWindow().actionPerformed(actionCommand);
        }
    }

    
}

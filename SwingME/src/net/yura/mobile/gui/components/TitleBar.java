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

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.GridLayout;

/**
 * @author Yura Mamyrin
 */
public class TitleBar extends Panel implements ActionListener {

    public static int align = Graphics.LEFT; // Graphics.HCENTER;

    private static String[] icons = new String[] {"#","+","_","[]","X"};
    private static String[] commands = new String[] {"resize","move",Frame.CMD_MIN,Frame.CMD_MAX,Frame.CMD_CLOSE};

    private Label title;
    private Panel buttonPanel;
    
    public TitleBar() {
        super(new BorderLayout());
        setName("TitleBar");
        
        buttonPanel = new Panel( new GridLayout(1,0,2) );

        // always want it to take the style of this instead
        buttonPanel.setName("WindowControlPanel");
        
        title = new Label();
        title.setName("TitleBarLabel");

        title.setHorizontalAlignment(align);
        
        add(title);
        add(buttonPanel,Graphics.RIGHT);

    }

    public void setButtonVisable(String action,boolean vis) {

        Button found = null;
        Vector comps = buttonPanel.getComponents();
        for (int c=0;c<comps.size();c++) {
            Button button = (Button)comps.elementAt(c);
            if (action.equals( button.getActionCommand() ) ) {
                found = button;
                break;
            }
        }

        if (vis && found==null) {
            String icon = "";
            int pos=0;
            for (int c=0;c<commands.length;c++) {
                if (action.equals(commands[c])) {
                    icon = icons[c];
                    pos = c;
                    break;
                }
            }
            Button b = new Button(icon);
            b.addActionListener(this);
            b.setActionCommand(action);

            // try and find a button that has a command later then our command
            for (int c=0;c<comps.size();c++) {
                Button button = (Button)comps.elementAt(c);
                for (int i=pos+1;i<commands.length;i++) {
                    if (commands[i].equals( button.getActionCommand() ) ) {
                        buttonPanel.insert(b, c);
                        return;
                    }
                }
            }
            buttonPanel.add(b);
        }
        else if (!vis && found!=null) {
            buttonPanel.remove(found);
        }
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
    public void setIconImage(Icon img) {
        title.setIcon(img);
    }
    
    private int oldX,oldY;
    private boolean move,resize;
    private Component old;
    
    public void processMouseEvent(int type, int x, int y, KeyEvent keys) {

        if (type == DesktopPane.PRESSED) {
            oldX=x;
            oldY=y;

        }
        else if (type == DesktopPane.DRAGGED) {

            Frame owner = (Frame)getWindow();

            if (!owner.isMaximum()) {

                owner.setLocation(owner.getX()+(x-oldX),owner.getY()+(y-oldY));
                DesktopPane.getDesktopPane().fullRepaint();
            }

        }

    }

    public boolean processKeyEvent(KeyEvent keypad) {

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
        else if (Frame.CMD_CLOSE.equals(actionCommand)) {
             getWindow().doClose();
         }
         else if (Frame.CMD_MIN.equals(actionCommand)) {

             //if (parent==null) {
                 Vector windows = DesktopPane.getDesktopPane().getAllFrames();
                 if (windows.size()>1) {
                     DesktopPane.getDesktopPane().setSelectedFrame((Window)windows.elementAt(windows.size()-2));
                 }
             //}

        }
        else if (Frame.CMD_MAX.equals(actionCommand)) {
             Window w = getWindow();
             // can only maximise a frame
             if (w instanceof Frame) {
                 Frame f = (Frame)w;
                 f.setMaximum( !f.isMaximum );
                 f.repaint();
             }
        }
        else {
             //#debug
            System.err.println("unknow Window command: "+actionCommand);
        }

    }

    
}

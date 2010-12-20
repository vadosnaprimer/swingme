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
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.plaf.basic.BasicInternalFrameTitlePane
 */
public class FrameTitlePane extends Panel implements ActionListener {

    private static String[] icons = new String[] {"#","+","_","[]","X"};
    private static String[] commands = new String[] {"resize","move",Frame.CMD_MIN,Frame.CMD_MAX,Frame.CMD_CLOSE};

    private Label title;
    private Panel buttonPanel;
    
    public FrameTitlePane() {
        super(new BorderLayout());
        setName("TitleBar");
        
        buttonPanel = new Panel( new GridLayout(1,0,2) );

        // always want it to take the style of this instead
        buttonPanel.setName("WindowControlPanel");
        
        title = new Label();
        title.setName("TitleBarLabel");
        
        add(title);
        add(buttonPanel,Graphics.RIGHT);

        updateUI();
    }

    public void updateUI() {
        super.updateUI();

        if (title!=null) {
            String titleAlignment = (String)theme.getProperty("titleAlignment", Style.ALL);
            if ("center".equals(titleAlignment)) {
                setTitleAlignment(Graphics.HCENTER);
            }
            else if ("trailing".equals(titleAlignment)) {
                setTitleAlignment(Graphics.RIGHT);
            }
            else { // default is leading
                setTitleAlignment(Graphics.LEFT);
            }
        }
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
     * @see java.awt.Frame#getTitle() Frame.getTitle
     */
    public String getTitle() {
        return this.title.getText();
    }

    /**
     * @param img the icon image to be displayed
     * @see java.awt.Frame#setIconImage(java.awt.Image) Frame.setIconImage
     */
    public void setIconImage(Icon img) {
        title.setIcon(img);
    }

    /**
     * this method should not really be used, please use the synth xml instead:
     * <pre>{@code
     *<synth>
     *    <style id="frameTitlePane">
     *      <state>
     *        <property key="titleAlignment" type="string" value="center"/>
     *      </state>
     *    </style>
     *    <bind style="frameTitlePane" type="region" key="TitleBar"/>
     *</synth>
     * }</pre>
     */
    public void setTitleAlignment(int a) {
        title.setHorizontalAlignment(a);
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
                move(owner,owner.getX()+(x-oldX),owner.getY()+(y-oldY));
            }
        }

    }

    private void move(Window owner,int x,int y) {
        owner.getDesktopPane().repaintHole(owner);
        owner.setLocation(x,y);
        owner.repaint();
    }
    private void resize(Window owner,int w,int h) {
        owner.getDesktopPane().repaintHole(owner);
        owner.setSize(w, h);
        owner.repaint();
    }

    public boolean processKeyEvent(KeyEvent keypad) {

            Window owner = getWindow();
        
            if (keypad.isDownAction(Canvas.LEFT)) {
                
                if (move) { move(owner,owner.getX()-2,owner.getY()); }
                else if (resize) { resize(owner,owner.getWidth()-2, owner.getHeight()); }
                return true;
            }
            if (keypad.isDownAction(Canvas.RIGHT)) {
                
                if (move) { move(owner,owner.getX()+2,owner.getY()); }
                else if (resize) { resize(owner,owner.getWidth()+2, owner.getHeight()); }
                return true;
            }
            if (keypad.isDownAction(Canvas.UP)) {
                
                if (move) { move(owner,owner.getX(),owner.getY()-2); }
                else if (resize) { resize(owner,owner.getWidth(), owner.getHeight()-2); }
                return true;
            }
            if (keypad.isDownAction(Canvas.DOWN)) {
                
                if (move) { move(owner,owner.getX(),owner.getY()+2); }
                else if (resize) { resize(owner,owner.getWidth(), owner.getHeight()+2); }
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
                 Vector windows = getDesktopPane().getAllFrames();
                 if (windows.size()>1) {
                     getDesktopPane().setSelectedFrame((Window)windows.elementAt(windows.size()-2));
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
             //#debug warn
            Logger.warn("unknow Window command: "+actionCommand);
        }

    }

    
}

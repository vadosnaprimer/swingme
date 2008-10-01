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

import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.layout.BorderLayout;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JFrame
 */
public class Window extends Panel implements ActionListener {

        public static final String CMD_MAX = "max";
        public static final String CMD_MIN = "min";
        public static final String CMD_CLOSE = "close";
    
        private static Vector allWindows = new Vector();
        public static Vector getAllWindows() {
            return allWindows;
        }
        
        private CommandButton[] windowCommands;
        private ActionListener actionListener;
        
        private Component focusedComponent;
        /**
         * @see java.awt.Window#isFocused() Window.isFocused
         */
        public boolean isFocused() {
            return DesktopPane.getDesktopPane().getSelectedFrame() == this;
        }
        /**
         * @see java.awt.Window#getFocusOwner() Window.getFocusOwner
         */
        public Component getFocusOwner() {
            if (!isFocused()) return null;
            return focusedComponent;
	}
        
        /**
         * @see java.awt.Window#getMostRecentFocusOwner() Window.getMostRecentFocusOwner
         */
        public Component getMostRecentFocusOwner() {
            if (focusedComponent==null) {
                breakOutAction(null,Canvas.DOWN,false,false);
            }
            return focusedComponent;
        }
        
        /**
         * Only used by requestFocusInWindow() in Component
         * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
         * @see Component#requestFocusInWindow()
         */
	protected void setFocusedComponent(Component ac) {

            if (isFocused() && focusedComponent != null) {
                   focusedComponent.focusLost();
            }

	    focusedComponent = ac;

            if (isFocused() && focusedComponent != null) {
                    focusedComponent.focusGained();
            }

	}
        
        /**
         * called by pack and revalidate
         * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
         */
        protected void setupFocusedComponent() {
                Component c = getMostRecentFocusOwner();
                if (c!=null && c.getWindow()!=this) {
                    setFocusedComponent(null);
                    getMostRecentFocusOwner();
                }
        }
        
        public ActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
        
        /**
         * @see javax.swing.JFrame#JFrame() JFrame.JFrame
         */
	public Window() {

            setName("Window");
            
		//contentPane = new Panel();
                setLayout( new BorderLayout() );
                
                //contentPane.setOwnerAndParent(this,null);
                
                windowCommands = new CommandButton[2];

                for (int c=0;c<allWindows.size();c++) {
                    if (((WeakReference)allWindows.elementAt(c)).get() == null) {
                        allWindows.removeElementAt(c);
                        c--;
                    }
                }
                allWindows.addElement(new WeakReference(this));
                
	}
	
	public void passScrollUpDown(int right) {
		
            breakOutAction(null,right,true,false);

	}
	
	/**
         * sets the new size and revaliates the window
         * @param width new Width
         * @param height new Height
         * @see java.awt.Component#setSize(int, int) Component.setSize
         */
        public void setSize(int width, int height) {
            super.setSize(width, height);
            revalidate();
        }

        /**
         * @see java.awt.Window#pack() Window.pack
         */
        public void pack() {
            workoutSize();
            doLayout();
            setupFocusedComponent();
        }

        /**
         * @param b true if the window is to be shown, false to hide the window
         * @see java.awt.Component#setVisible(boolean) Component.setVisible
         */
        public void setVisible(boolean b) {

            if (b) {
                DesktopPane.getDesktopPane().add(this);
            }
            else {
//                // TODO ??
//                if (parent==null) {
                    DesktopPane.getDesktopPane().remove(this);
//                 }
//                 else {
//                     parent.remove(this);
//                 }

            }
        }
        
        /**
         * @see java.awt.Component#isVisible() Component.isVisible
         */
        public boolean isVisible() {
            return DesktopPane.getDesktopPane().getAllFrames().contains(this);
        }
        
        /**
         * @param a true to maxemise the window
         * @see javax.swing.JInternalFrame#setMaximum(boolean) JInternalFrame.setMaximum
         */
        public void setMaximum(boolean a) {
            if (a) {
                setBounds(0, 0, DesktopPane.getDesktopPane().getWidth(), DesktopPane.getDesktopPane().getHeight());
            }
            // TODO else???
        }

        public void pointerEvent(int type, int x, int y) {

            // TODO to resize the window
            // if we drag next to the border
        }

        public CommandButton[] getWindowCommands() {
            return windowCommands;
        }

        /**
         * @see DesktopPane#setComponentCommand
         */
	public void setWindowCommand(int i, CommandButton softkey) {

            if (windowCommands[i]!=softkey) {
                CommandButton oldc = DesktopPane.getDesktopPane().getCurrentCommands()[i]; // get old 1
		windowCommands[i] = softkey;
                if (DesktopPane.getDesktopPane().getCurrentCommands()[i]==softkey) { // check if we are the new 1
                    if (oldc==null || softkey == null) {
                        DesktopPane.getDesktopPane().fullRepaint();
                    }
                    else {
                        DesktopPane.getDesktopPane().softkeyRepaint();
                    }
                }
            }
	}

        public void actionPerformed(String actionCommand) {
            
             if (CMD_CLOSE.equals(actionCommand)) {
                 setVisible(false);
             }
             else if (CMD_MIN.equals(actionCommand)) {

                 if (parent==null) {
                     Vector windows = DesktopPane.getDesktopPane().getAllFrames();
                     if (windows.size()>1) {
                         DesktopPane.getDesktopPane().setSelectedFrame((Window)windows.elementAt(windows.size()-2));
                     }
                 }

             }
             else if (CMD_MAX.equals(actionCommand)) {
                 setMaximum(true);
                 repaint();
             }
             else {
                 //#debug
                System.out.println("unknow Window command: "+actionCommand);
             }

        }
}

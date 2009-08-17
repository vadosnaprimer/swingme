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
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
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
        
        //private CommandButton[] windowCommands;
        private ActionListener actionListener;
        
        private Component focusedComponent;
        private boolean maximised;

        //private TitleBar titleBar;
        //private MenuBar menubar;
        //private Panel contentPane;

        /**
         * @see javax.swing.JFrame#JFrame() JFrame.JFrame
         */
	public Window() {

		//contentPane = new Panel();
                //setLayout( new BorderLayout() );

                //contentPane.setOwnerAndParent(this,null);

                //windowCommands = new CommandButton[2];

                for (int c=0;c<allWindows.size();c++) {
                    if (((WeakReference)allWindows.elementAt(c)).get() == null) {
                        allWindows.removeElementAt(c);
                        c--;
                    }
                }
                allWindows.addElement(new WeakReference(this));

                super.add(new Panel(new BorderLayout()));

	}

        /**
         * @see java.awt.Frame#setUndecorated(boolean) Frame.setUndecorated
         */
        public void setUndecorated(boolean un) {
            TitleBar tb = getTitleBar();
            if (tb==null && !un) {
                super.add(new TitleBar("", null, false, false, false, true, true));
            }
            else if (tb!=null && un) {
                super.remove(tb);
            }
        }

        /**
         * @see
         */
        public void setTitle(String newTitle) {
            TitleBar tb = getTitleBar();
            tb.setTitle(newTitle);
        }

        /**
         * @see javax.swing.JFrame#setJMenuBar(javax.swing.JMenuBar)
         */
        public void setMenuBar(MenuBar menuBar) {
            //this.menubar = menuBar;

            MenuBar mbar = getMenuBar();
            if (mbar!=null) {
                super.insert(menuBar, getComponents().indexOf(mbar) );
            }
            else {
                super.add(menuBar);
            }

        }

        /**
         * @see javax.swing.JFrame#getJMenuBar() JFrame.getJMenuBar
         */
        public MenuBar getMenuBar() {
            Vector components = getComponents();
            for (int c=0;c<components.size();c++) {
                Object obj = components.elementAt(c);
                if (obj instanceof MenuBar) {
                    return (MenuBar)obj;
                }
            }
            return null;
        }

        /**
         * not swing
         */
        public TitleBar getTitleBar() {
            Vector components = getComponents();
            for (int c=0;c<components.size();c++) {
                Object obj = components.elementAt(c);
                if (obj instanceof TitleBar) {
                    return (TitleBar)obj;
                }
            }
            return null;
        }

        public Panel getContentPane() {
            Vector components = getComponents();
            for (int c=0;c<components.size();c++) {
                Object obj = components.elementAt(c);
                if (!(obj instanceof MenuBar) && !(obj instanceof TitleBar)) {
                    return (Panel)obj;
                }
            }
            return null;
        }

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

//	public void setActionListener(ActionListener actionListener) {
//		this.actionListener = actionListener;
//	}
	
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

        public void setLocation(int x, int y) {
            super.setLocation(x, y);

            MenuBar menubar = getMenuBar();
            if (!DesktopPane.me4se && menubar!=null) {
                menubar.setBoundsWithBorder(-getXOnScreen(),DesktopPane.getDesktopPane().getHeight() - getYOnScreen() - menubar.getHeightWithBorder(),DesktopPane.getDesktopPane().getWidth(),menubar.getHeightWithBorder());
            }
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

            maximised = a;

            if (a) {
                setBounds(0, 0, DesktopPane.getDesktopPane().getWidth(), DesktopPane.getDesktopPane().getHeight());
            }
            // TODO else???
        }
        public boolean getMaximum() {
            return maximised;
        }

        public void pointerEvent(int type, int x, int y, KeyEvent keys) {

            // TODO to resize the window
            // if we drag next to the border
        }

//        public CommandButton[] getWindowCommands() {
//            return windowCommands;
//        }
//
//        /**
//         * @see DesktopPane#setComponentCommand
//         */
//	public void setWindowCommand(int i, CommandButton softkey) {
//
//            if (windowCommands[i]!=softkey) {
//                CommandButton oldc = DesktopPane.getDesktopPane().getCurrentCommands()[i]; // get old 1
//		windowCommands[i] = softkey;
//                if (DesktopPane.getDesktopPane().getCurrentCommands()[i]==softkey) { // check if we are the new 1
//                    if (oldc==null || softkey == null) {
//                        DesktopPane.getDesktopPane().fullRepaint();
//                    }
//                    else {
//                        DesktopPane.getDesktopPane().softkeyRepaint();
//                    }
//                }
//            }
//	}

        public void actionPerformed(String actionCommand) {

             if (CMD_CLOSE.equals(actionCommand)) {

                if (windowListener!=null) {
                    windowListener.actionPerformed(actionCommand);
                    return;
                }

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
                 setMaximum( !maximised );
                 repaint();
             }
             else {
                 //#debug
                System.out.println("unknow Window command: "+actionCommand);
             }

        }

        private ActionListener windowListener;
        public void addWindowListener(ActionListener al) {
            windowListener = al;
        }

        protected void breakOutAction(final Component component, final int direction, final boolean scrolltothere,final boolean forceFocus) {

            // TODO ???? is this right
            getContentPane().breakOutAction(component, direction, scrolltothere, forceFocus);

        }

        public void add(Component comp) {
            getContentPane().add(comp);
        }

        public void workoutSize() {

            int w=0;
            int h=0;

            TitleBar titleBar = getTitleBar();
            if (titleBar!=null) {
                titleBar.workoutSize();
                int tw = titleBar.getWidthWithBorder();
                int th = titleBar.getHeightWithBorder();
                if (tw>w) w = tw;
                h = h +th;
            }

            MenuBar menubar = getMenuBar();
            if (menubar!=null) {
                menubar.workoutSize();
                if (DesktopPane.me4se || maximised) {
                    int mw = menubar.getWidthWithBorder();
                    int mh = menubar.getHeightWithBorder();
                    if (mw>w) w = mw;
                    h = h + mh;
                }
            }

            Panel contentPane = getContentPane();
            if (contentPane!=null) {
                contentPane.workoutSize();
                int cw = contentPane.getWidthWithBorder();
                int ch = contentPane.getHeightWithBorder();
                if (cw>w) w = cw;
                h = h + ch;
            }

            System.out.println("size: "+w+" "+h);

            setSize(w, h);
        }

        public void doLayout() {

            int h=0;

            TitleBar titleBar = getTitleBar();
            if (titleBar!=null) {
                int th = titleBar.getHeightWithBorder();
                titleBar.setBoundsWithBorder(0, 0, width, th);
                h = h+th;
            }

            MenuBar menubar = getMenuBar();
            int mh = 0;
            if (menubar!=null) {
                mh = menubar.getHeightWithBorder();
                if (DesktopPane.me4se) {
                    menubar.setBoundsWithBorder(0, h, width,mh );
                    h = h+mh;
                }
                else {
                    menubar.setBoundsWithBorder(-getXOnScreen(),DesktopPane.getDesktopPane().getHeight() - getYOnScreen() - menubar.getHeightWithBorder(),DesktopPane.getDesktopPane().getWidth(),mh);
                }
            }

            Panel contentPane = getContentPane();
            if (contentPane!=null) {
                contentPane.setBoundsWithBorder(0, h, width, height - h - (!DesktopPane.me4se && maximised?mh:0) );
            }

            super.doLayout();
            
        }

        public Component getComponentAt(int x, int y) {
            if (!DesktopPane.me4se) {
                MenuBar bar = getMenuBar();
                if (bar!=null) {
                    int i = bar.locationToIndex(x-bar.getX(), y-bar.getY());
                    if (i>=0) {
                        return bar;
                    }
                }
            }
            return super.getComponentAt(x, y);
        }


        protected String getDefaultName() {
            return "Window";
        }

        private Vector softkeybackup;

        /**
         * @see javax.microedition.lcdui.Displayable#addCommand(javax.microedition.lcdui.Command) Displayable.addCommand
         */
        public void addCommand(Button softkey) {
            if (!DesktopPane.me4se) {
                MenuBar menubar = getMenuBar();
                if (menubar==null) {
                    menubar = new MenuBar();
                    setMenuBar(menubar);
                }
                Vector items = menubar.getItems();
                if (!items.contains(softkey)) {
                    int mne = softkey.getMnemonic();
                    if (mne!=0) {
                        for (int c=0;c<items.size();c++) {
                            Button button = (Button)items.elementAt(c);
                            if (button.getMnemonic() == mne) {
                                items.removeElement(button);
                                if (softkeybackup==null) {
                                    softkeybackup = new Vector(1);
                                }
                                softkeybackup.addElement(button);
                                break;
                            }
                        }
                    }
                    items.addElement(softkey);
                }
            }
        }

        /**
         * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command) Displayable.removeCommand
         */
        public void removeCommand(Button softkey) {
            if (!DesktopPane.me4se) {
                MenuBar menubar = getMenuBar();
                Vector items = menubar.getItems();
                items.removeElement(softkey);
                int mne = softkey.getMnemonic();
                if (menubar!=null && mne!=0 && softkeybackup!=null) {
                    for (int c=0;c<softkeybackup.size();c++) {
                        Button button = (Button)softkeybackup.elementAt(c);
                        if (button.getMnemonic() == mne) {
                            items.addElement(button);
                            break;
                        }
                    }
                }
            }
        }

        public Button findMneonicButton(KeyEvent keyevent) {
            MenuBar bar = getMenuBar();
            if (bar!=null) {

                int size = bar.getSize();
                for(int i = 0; i < size; i++) {
                    Object component = bar.getElementAt(i);
                    if (component instanceof Button) {
                        Button button = (Button)component;
                        if (button.getMnemonic() == keyevent.getJustPressedKey()) {
                            Component comp = bar.getRendererComponentOnScreen(i);
                            button.setBoundsWithBorder(comp.getXWithBorder(), comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
                            return button;
                        }
                        else if (button instanceof Menu) {
                            Button button2 = ((Menu)button).findMneonicButton(keyevent);
                            if (button2!=null) {
                                return button2;
                            }
                        }
                    }
                }
            }

            return super.findMneonicButton(keyevent);
        }


}

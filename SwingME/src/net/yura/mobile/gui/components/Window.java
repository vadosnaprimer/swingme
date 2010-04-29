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
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see java.awt.Window
 */
public class Window extends Panel {

        private DesktopPane desktop;

        private Component focusedComponent;

        private boolean closeOnFocusLost;

        private Vector softkeys;

        private ActionListener windowListener;

        /**
         * @see java.awt.Window#Window() Window.Window
         */
	public Window() {
            setLayout( new BorderLayout() );
	}

        /**
         * @see javax.swing.JInternalFrame#getDesktopPane() JInternalFrame.getDesktopPane
         */
        public DesktopPane getDesktopPane() {
            if (desktop!=null) {
                return desktop;
            }
            return DesktopPane.getDesktopPane();
        }
        public void setDesktopPane(DesktopPane a) {
            desktop = a;
        }

        /**
         * not swing
         * easy way to make menus and combo boxes close when u click outside of them
         */
        public void setCloseOnFocusLost(boolean ch) {
            closeOnFocusLost = ch;
        }


        /**
         * @see java.awt.Window#isFocused() Window.isFocused
         */
        public boolean isFocused() {
            return desktop!=null && desktop.getSelectedFrame() == this;
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
            if (focusedComponent == ac) return;

            if (isFocused() && focusedComponent != null) {
                // if we call focusLost on focusedComponent it still thinks its state is that is has focused
                // and when it decides what to repaint, it bases it on
                Component old = focusedComponent;
                focusedComponent = null;
                old.focusLost();
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
                if (c!=null) {
                    if (c.getWindow()!=this || !c.isShowing() || !c.isFocusable()) {
                        setFocusedComponent(null);
                        getMostRecentFocusOwner();
                    }
                }
        }

	public void passScrollUpDown(int right) {
            breakOutAction(null,right,true,false);
	}

//        public void setLocation(int x, int y) {
//            super.setLocation(x, y);
//
//            MenuBar menubar = getMenuBar();
//            if (!DesktopPane.me4se && menubar!=null) {
//                menubar.setBoundsWithBorder(-getXOnScreen(),DesktopPane.getDesktopPane().getHeight() - getYOnScreen() - menubar.getHeightWithBorder(),DesktopPane.getDesktopPane().getWidth(),menubar.getHeightWithBorder());
//            }
//        }

        /**
         * @see java.awt.Window#pack() Window.pack
         */
        public void pack() {
            // TODO this fails when there is a scrolpane with a panel with many textPanes inside
            workoutSize();
            doLayout();
            setupFocusedComponent();
        }

        public void setSize(int width, int height) {
            // workout size of children only, so that doLayout will work
            Vector components = getComponents();
            for(int i = 0; i < components.size(); i++) {
                    Component component = (Component)components.elementAt(i);
                    component.workoutSize();
            }
            super.setSize(width, height); // calls doLayout
            setupFocusedComponent();
        }

        /**
         * @see java.awt.Window#addWindowListener(java.awt.event.WindowListener) Window.addWindowListener
         */
        public void addWindowListener(ActionListener al) {
            windowListener = al;
        }

        /**
         * internal method that is called when a window autocloses or is closed from the titlebar
         */
        void doClose() {

                if (windowListener!=null) {
                    windowListener.actionPerformed(Frame.CMD_CLOSE);
                }
                else {
                    setVisible(false);
                }
        }

        /**
         * This method can ONLY be used if there is ONLY ONE DesktopPane
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
                    getDesktopPane().remove(this);
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
            return desktop!=null && desktop.getAllFrames().contains(this);
        }


        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {

            // TODO to resize the window
            // if we drag next to the border

//            if (!DesktopPane.me4se && type == DesktopPane.PRESSED) {
//                Button b = locationToIndex(x,y);
//                if (b!=null) {
//                    b.fireActionPerformed();
//                    return;
//                }
//            }

            if (closeOnFocusLost && type == DesktopPane.PRESSED &&
                    (x<0 || y<0 || x>width || y>height)
                    ) {
                doClose();
            }


        }

        public Component getComponentAt(final int xclick,final int yclick) {
            if (getDesktopPane().SOFT_KEYS) {

                    int x = xclick + getXOnScreen();
                    int y = yclick + getYOnScreen();

                    for (int i=0; i< 5; i++) {
                        int key=0;
                        switch (i) {
                            case 0: key = KeyEvent.KEY_SOFTKEY1; break;
                            case 1: key = KeyEvent.KEY_SOFTKEY2; break;
                            case 2: key = KeyEvent.KEY_SOFTKEY3; break;
                            case 3: key = KeyEvent.KEY_MENU; break;
                            case 4: key = KeyEvent.KEY_END; break;
                        }

                        Button b = findMneonicButton(key);
                        if (b!=null) {

                            if (
                                    (x >= b.getXWithBorder()) &&
                                    (x <= (b.getXWithBorder()+b.getWidthWithBorder())) &&
                                    (y >= b.getYWithBorder()) &&
                                    (y <= (b.getYWithBorder()+b.getHeightWithBorder()))) {

                                return b;

                            }
                        }

                    }

            }
            return super.getComponentAt(xclick, yclick);
        }

        protected String getDefaultName() {
            return "Window";
        }

        public void paint(Graphics2D g) {
            super.paint(g);
            paintSoftKeys(g);
        }

        public void paintSoftKeys(Graphics2D g) {
            Window w = getWindow();

            if (w.getDesktopPane().SOFT_KEYS && w == desktop.getSelectedFrame()) {

                int offsetX = g.getTranslateX();
                int offsetY = g.getTranslateY();
                g.translate(-offsetX,-offsetY);

                for (int i=0; i< 5; i++) {
                    int key=0;
                    switch (i) {
                        case 0: key = KeyEvent.KEY_SOFTKEY1; break;
                        case 1: key = KeyEvent.KEY_SOFTKEY2; break;
                        case 2: key = KeyEvent.KEY_SOFTKEY3; break;
                        case 3: key = KeyEvent.KEY_MENU; break;
                        case 4: key = KeyEvent.KEY_END; break;
                    }

                    Button b = getSoftkeyForMneonic(key);

                    if (b!=null) {
                        Component component = getRendererComponentOnScreen(b);

                        int x = component.getX();
                        int y = component.getY();

                        g.translate(x,y);
                        component.paint(g);
                        g.translate(-x, -y);
                    }
                }

                g.translate(offsetX,offsetY);
            }

        }

        private Button getSoftkeyForMneonic(int mnu) {
            if (softkeys != null ) {
                for (int c=softkeys.size()-1;c>=0;c--) {
                    Button button = (Button)softkeys.elementAt(c);
                    if (button.getMnemonic() == mnu) {
                        return button;
                    }
                    else if (button instanceof Menu) {
                            Button button2 = ((Menu)button).findMneonicButton(mnu);
                            if (button2!=null) {
                                return button2;
                            }
                    }
                }
            }
            return super.findMneonicButton(mnu);
        }

    public Component getRendererComponentOnScreen(Button button){

            boolean sideSoftKeys = desktop.isSideSoftKeys();

            int mnemonic = button.getMnemonic();

            int desktopWidth = desktop.getWidth();
            int desktopHeight = desktop.getHeight();

    //Logger.debug("Screen height: "+desktopHeight);

            Component component = desktop.getSoftkeyRenderer().getListCellRendererComponent(null,button,0,false,false);

            component.workoutSize();
            int componentWidth = component.getWidthWithBorder();
            int componentHeight = component.getHeightWithBorder();

    //Logger.debug("Component height: "+componentHeight);

            //button.setSize(componentWidth, componentHeight);

            int bottom = desktopHeight-componentHeight;
            int right = desktopWidth-componentWidth;

    //Logger.debug("Bottom: "+bottom);

            int x = 0, y = 0;

            if ((mnemonic == KeyEvent.KEY_SOFTKEY1 || mnemonic == KeyEvent.KEY_MENU) && (!sideSoftKeys)) {
                // Bottom left
                x=0;
                y=bottom;
            }
            else if (((mnemonic == KeyEvent.KEY_SOFTKEY2 || mnemonic == KeyEvent.KEY_END) && (!sideSoftKeys)) || ((mnemonic == KeyEvent.KEY_SOFTKEY1 || mnemonic == KeyEvent.KEY_MENU) && sideSoftKeys)) {
                // Bottom right
                x = right;
                y = bottom;
            }
            else if (mnemonic == KeyEvent.KEY_SOFTKEY3 && (!sideSoftKeys)) {
                // Bottom middle
                x = (desktopWidth/2)-(componentWidth/2);
                y = bottom;
            }
            else if ((mnemonic == KeyEvent.KEY_SOFTKEY2 || mnemonic == KeyEvent.KEY_END) && sideSoftKeys) {
                // Top right
                x = right;
                y = 0;
            }
            else if (mnemonic == KeyEvent.KEY_SOFTKEY3 && sideSoftKeys) {
                // Middle right
                x = right;
                y = (desktopHeight/2)-(componentHeight/2);
            }
            else {
                // Not a softkey... will be at 0,0. throw exception?
            }

            component.setBoundsWithBorder(x, y, componentWidth, componentHeight);

            return component;


    }

        /**
         * @see javax.microedition.lcdui.Displayable#addCommand(javax.microedition.lcdui.Command) Displayable.addCommand
         */
        public void addCommand(Button softkey) {

            // this is done in me4se mode too
            // as we want the F1 and F2 keys to work the same as in a emulator
            // even when you dont actually see the softkeys
            if (softkeys==null) {
                softkeys = new Vector(1);
            }

            if (!softkeys.contains(softkey)) {
                softkeys.addElement(softkey);
                softkey.setParent(this);
            }
            //#mdebug warn
            else {
                Logger.warn("whats this all about?");
                throw new RuntimeException();
            }
            if (softkey==null) {
                Logger.warn("trying to add null button");
                throw new RuntimeException();
            }
            //#enddebug

            softKeyRepaint();
        }

        private void softKeyRepaint() {
            DesktopPane dp = getDesktopPane();
            if (dp.SOFT_KEYS) {
                // HACK: Jane - If the soft-keys are outside the "window paint area", we need a
                // full repaint, otherwise, replacing a "big" soft-key with a "small" will
                // leave garbage pixels... However, just because a window is smaller that
                // the desktop, it does not mean the soft-keys are outside it...
                if (getWidth() < dp.getWidth() || getHeight() < dp.getHeight()) {
                    dp.fullRepaint();
                } else {
                    repaint();
                }
            }
        }

        /**
         * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command) Displayable.removeCommand
         */
        public void removeCommand(Button softkey) {

            // need to remove even in me4se mode
            if (softkeys.contains(softkey)) {
                softkeys.removeElement(softkey);
                softkey.removeParent(this);
            }
            //#mdebug warn
            else {
                Logger.warn("whats this all about?");
                throw new RuntimeException();
            }
            if (softkey==null) {
                Logger.warn("trying to remove null button");
                throw new RuntimeException();
            }
            //#enddebug

            softKeyRepaint();
        }

        public void updateUI() {
            super.updateUI();
            for (int c = 0; softkeys!=null && c < softkeys.size(); c++)
              ((Component)softkeys.elementAt(c)).updateUI();
        }

        public Button findMneonicButton(int mn) {

            Button b = getSoftkeyForMneonic(mn);
            if (b!=null && !b.isVisible() && getDesktopPane().SOFT_KEYS) {
                    Component comp = getRendererComponentOnScreen(b);
                    b.setBoundsWithBorder(comp.getXWithBorder(), comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
            }

            if (b!=null && !b.isVisible() && (Midlet.getPlatform() == Midlet.PLATFORM_ANDROID || Midlet.getPlatform() == Midlet.PLATFORM_BLACKBERRY)) {
                b.setBoundsWithBorder(0, getDesktopPane().getHeight(), getDesktopPane().getWidth(), b.getHeightWithBorder());
            }

            return b;

        }

        /**
         * @see java.awt.Window#setLocationRelativeTo(java.awt.Component) Window.setLocationRelativeTo
         */
        public void setLocationRelativeTo(Component comp) {
            if (comp == null) {

                DesktopPane dp = DesktopPane.getDesktopPane();

                setLocation((dp.getWidth() - getWidth()) /2,
                        (dp.getHeight() - getHeight()) /2
                );
            }
        }

}

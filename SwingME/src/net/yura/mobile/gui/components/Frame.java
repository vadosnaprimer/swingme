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

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JFrame
 */
public class Frame extends Window {
        public static final String CMD_MAX = "max";
        public static final String CMD_MIN = "min";
        public static final String CMD_CLOSE = "close";
        /**
         * @see javax.swing.JInternalFrame#isMaximum JInternalFrame.isMaximum
         */
        protected boolean isMaximum;

        /**
         * @see javax.swing.JFrame#JFrame() JFrame.JFrame
         */
        public Frame() {
            setUndecorated(false);
            if (getDesktopPane().MAX_CLOSE_BUTTONS) {
                setMaximizable(true);
                setClosable(true);
            }
            setContentPane(new Panel(new BorderLayout()));
            setLayout(null); // we do our own layout
        }

        /**
         * @see javax.swing.JFrame#JFrame(java.lang.String) JFrame.JFrame
         */
        public Frame(String name) {
            this();
            setTitle(name);
        }

        /**
         * @see javax.swing.JInternalFrame#setClosable(boolean) JInternalFrame.setClosable
         */
        public void setClosable(boolean b) {
            FrameTitlePane title = getTitlePane();
            if (title!=null) {
                title.setButtonVisable(CMD_CLOSE, b);
            }
        }

        /**
         * @see javax.swing.JInternalFrame#setMaximizable(boolean) JInternalFrame.setMaximizable
         */
        public void setMaximizable(boolean b) {
            FrameTitlePane title = getTitlePane();
            if (title!=null) {
                title.setButtonVisable(CMD_MAX, b);
            }
        }

        protected String getDefaultName() {
            return "Frame";
        }

        /**
         * @see java.awt.Frame#setUndecorated(boolean) Frame.setUndecorated
         */
        public void setUndecorated(boolean un) {

            // TODO, maybe should setVisable to false instead of removing it!
            FrameTitlePane tb = getTitlePane();
            if (tb==null && !un) {
                super.add(new FrameTitlePane());
            }
            else if (tb!=null && un) {
                super.remove(tb);
            }
        }

        /**
         * @see java.awt.Frame#setTitle(java.lang.String) Frame.setTitle
         */
        public void setTitle(String newTitle) {
            FrameTitlePane tb = getTitlePane();
            if (tb!=null) {
                tb.setTitle(newTitle);
            }
        }

        /**
         * @see java.awt.Frame#getTitle() Frame.getTitle
         */
        public String getTitle() {
            FrameTitlePane tb = getTitlePane();
            if (tb!=null) {
                return tb.getTitle();
            }
            return null;
        }

        /**
         * @see java.awt.Frame#setIconImage(java.awt.Image) Frame.setIconImage
         */
        public void setIconImage(Icon icon) {
            FrameTitlePane tb = getTitlePane();
            if (tb!=null) {
                tb.setIconImage(icon);
            }
        }

        /**
         * @see javax.swing.JFrame#setJMenuBar(javax.swing.JMenuBar) JFrame.setJMenuBar
         * @see java.awt.Frame#setMenuBar(java.awt.MenuBar) Frame.setMenuBar
         */
        public void setMenuBar(MenuBar menuBar) {

            MenuBar mbar = getMenuBar();
            if (mbar!=menuBar) {
                if (mbar!=null) {
                    super.remove(mbar);
                }
                if (menuBar != null) {
                    super.add(menuBar);

                    menuBar.autoMnemonic();
                }
            }

        }

        /**
         * @see javax.swing.JFrame#setContentPane(java.awt.Container) JFrame.setContentPane
         */
        public void setContentPane(Panel p) {

            Panel oldCP = getContentPane();
            if (oldCP!=p) {
                if (oldCP!=null) {
                    super.remove(oldCP);
                }
                // we insert at 0 as if everything is transparent, we want the
                // contentPane to draw first as it may have scroll clip set to false
                super.insert(p,0);
            }

        }
        /**
         * @see javax.swing.JFrame#getJMenuBar() JFrame.getJMenuBar
         * @see java.awt.Frame#getMenuBar() Frame.getMenuBar
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
        public FrameTitlePane getTitlePane() {
            Vector components = getComponents();
            for (int c=0;c<components.size();c++) {
                Object obj = components.elementAt(c);
                if (obj instanceof FrameTitlePane) {
                    return (FrameTitlePane)obj;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.JFrame#getContentPane() JFrame.getContentPane
         */
        public Panel getContentPane() {
            Vector components = getComponents();
            for (int c=0;c<components.size();c++) {
                Object obj = components.elementAt(c);
                if (!(obj instanceof MenuBar) && !(obj instanceof FrameTitlePane)) {
                    return (Panel)obj;
                }
            }
            return null;
        }

        protected void workoutMinimumSize() {

            int w=0;
            int h=0;

            FrameTitlePane titleBar = getTitlePane();
            if (titleBar!=null && titleBar.isVisible()) {
                titleBar.workoutPreferredSize();
                int tw = titleBar.getWidthWithBorder();
                int th = titleBar.getHeightWithBorder();
                if (tw>w) w = tw;
                h = h +th;
            }

            MenuBar menubar = getMenuBar();
            if (menubar!=null && menubar.isVisible()) {
                menubar.workoutPreferredSize();
                if (isMaximum) {
                    int mw = menubar.getWidthWithBorder();
                    int mh = menubar.getHeightWithBorder();
                    if (mw>w) w = mw;
                    h = h + mh;
                }
            }

            Panel contentPane = getContentPane();
            if (contentPane!=null && contentPane.isVisible()) {
                contentPane.workoutPreferredSize();
                int cw = contentPane.getWidthWithBorder();
                int ch = contentPane.getHeightWithBorder();
                if (cw>w) w = cw;
                h = h + ch;
            }

            //Logger.debug("size: "+w+" "+h);

            width = w;
            height = h;
        }

        public void doLayout() {

            FrameTitlePane titleBar = getTitlePane();
            int th=0;
            if (titleBar!=null && titleBar.isVisible()) {
                th = titleBar.getHeightWithBorder();
                titleBar.setBoundsWithBorder(0, 0, width, th);
            }

            boolean bottom = isMaximum && getDesktopPane().SOFT_KEYS;

            MenuBar menubar = getMenuBar();
            int mh = 0;
            if (bottom) {
                // leave a gap under content pane for soft key
                // if we r max and on phone and no softkey bar
                mh = getDesktopPane().getMenuHeight();
            }
            else if (menubar!=null && menubar.isVisible()) {
                mh = menubar.getHeightWithBorder();
            }

            if (menubar!=null && menubar.isVisible()) {
                menubar.setBoundsWithBorder(0, bottom?height-mh:th, width,mh );
            }

            Panel contentPane = getContentPane();
            if (contentPane!=null && contentPane.isVisible()) {
                contentPane.setBoundsWithBorder(0, th+(bottom?0:mh), width, height -th -mh );
            }

        }

        /**
         * this is the new way of doing things instead of overriding breakOutAction
         */
        public Component getNextComponent(Component component,int direction) {
            return (component==null)?getContentPane():null;
        }
/*
        protected void breakOutAction(final Component component, final int direction, final boolean scrolltothere,final boolean forceFocus) {

            Panel cp = getContentPane();
            if (component == null || component == cp) {
                cp.breakOutAction(component, direction, scrolltothere, forceFocus);
            }

        }
*/

        // java 1.5 hack
        public void add(Component comp) {
            getContentPane().add(comp);
            //#debug info
            Logger.info("try and avoid using add() on a Frame, use getContentPane().add() instead");
        }
        // java 1.5 hack
        public void add(Component comp,Object consta) {
            getContentPane().add(comp,consta);
            //#debug info
            Logger.info("try and avoid using add() on a Frame, use getContentPane().add() instead");
        }
        // java 1.5 hack
        public void add(Component comp,int consta) {
            getContentPane().add(comp,consta);
            //#debug info
            Logger.info("try and avoid using add() on a Frame, use getContentPane().add() instead");
        }

//        private int oldX,oldY,oldWidth,oldHeight;
        /**
         * @param a true to maximize the window
         * @see javax.swing.JInternalFrame#setMaximum(boolean) JInternalFrame.setMaximum
         */
        public void setMaximum(boolean a) {

            isMaximum = a;

            if (a) {
/*
                if (width!=0 && height !=0) {
                    oldX = posX;
                    oldY = posY;
                    oldWidth = width;
                    oldHeight = height;
                }
*/
                setLocation(0, 0);

                DesktopPane desktop = getDesktopPane();

                width = desktop.getWidth();
                height = desktop.getHeight();

                // we want to use revalidate as it will make sure it
                // happens in the correct thred
                // so this method remains threadsafe
                //setBounds(0, 0, desktop.getWidth(), desktop.getHeight());

            }

            revalidate();
/*
            else { // TODO
                setLocation(oldX, oldY);
                width = oldWidth;
                height = oldHeight;
                revalidate();
                repaint();
            }
*/
        }
        /**
         * @see javax.swing.JInternalFrame#isMaximum() JInternalFrame.isMaximum
         */
        public boolean isMaximum() {
            return isMaximum;
        }
}

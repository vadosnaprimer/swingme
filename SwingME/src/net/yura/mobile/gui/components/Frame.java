package net.yura.mobile.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.layout.BorderLayout;

/**
 * @author Yura Mamyrin
 */
public class Frame extends Window implements ActionListener {
        public static final String CMD_MAX = "max";
        public static final String CMD_MIN = "min";
        public static final String CMD_CLOSE = "close";
        /**
         * @see javax.swing.JInternalFrame#isMaximum JInternalFrame.isMaximum
         */
        protected boolean isMaximum;

        public Frame() {
            setUndecorated(false);
            setContentPane(new Panel(new BorderLayout()));
            setLayout(null); // we do our own layout
        }
        public Frame(String name) {
            this();
            setTitle(name);
        }

        protected String getDefaultName() {
            return "Frame";
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

            MenuBar mbar = getMenuBar();
            if (mbar!=menuBar) {
                if (mbar!=null) {
                    super.remove(mbar);
                }
                super.add(menuBar);
            }

        }

        public void setContentPane(Panel p) {

            Panel mbar = getContentPane();
            if (mbar!=p) {
                if (mbar!=null) {
                    super.remove(mbar);
                }
                super.add(p);
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
        private TitleBar getTitleBar() {
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




        public void workoutMinimumSize() {

            int w=0;
            int h=0;

            TitleBar titleBar = getTitleBar();
            if (titleBar!=null && titleBar.isVisible()) {
                titleBar.workoutSize();
                int tw = titleBar.getWidthWithBorder();
                int th = titleBar.getHeightWithBorder();
                if (tw>w) w = tw;
                h = h +th;
            }

            MenuBar menubar = getMenuBar();
            if (menubar!=null && menubar.isVisible()) {
                menubar.workoutSize();
                if (isMaximum) {
                    int mw = menubar.getWidthWithBorder();
                    int mh = menubar.getHeightWithBorder();
                    if (mw>w) w = mw;
                    h = h + mh;
                }
            }

            Panel contentPane = getContentPane();
            if (contentPane!=null && contentPane.isVisible()) {
                contentPane.workoutSize();
                int cw = contentPane.getWidthWithBorder();
                int ch = contentPane.getHeightWithBorder();
                if (cw>w) w = cw;
                h = h + ch;
            }

            //System.out.println("size: "+w+" "+h);

            setSize(w, h);
        }

        public void doLayout() {

            int h=0;

            TitleBar titleBar = getTitleBar();
            if (titleBar!=null && titleBar.isVisible()) {
                int th = titleBar.getHeightWithBorder();
                titleBar.setBoundsWithBorder(0, 0, width, th);
                h = h+th;
            }

            MenuBar menubar = getMenuBar();
            //int mh = 0;
            if (menubar!=null && menubar.isVisible()) {

                int mh = menubar.getHeightWithBorder();
                menubar.setBoundsWithBorder(0, h, width,mh );
                h = h+mh;

            }

            int mh = DesktopPane.getDesktopPane().getMenuHeight();
            Panel contentPane = getContentPane();
            if (contentPane!=null && contentPane.isVisible()) {
                                                                          // leave a gap under content pane for soft key
                contentPane.setBoundsWithBorder(0, h, width, height - h - (isMaximum && !DesktopPane.me4se?mh:0) );
            }

            super.doLayout();

        }
        
        private ActionListener windowListener;
        public void addWindowListener(ActionListener al) {
            windowListener = al;
        }

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
                 setMaximum( !isMaximum );
                 repaint();
             }
             else {
                 //#debug
                System.out.println("unknow Window command: "+actionCommand);
             }

        }


        protected void breakOutAction(final Component component, final int direction, final boolean scrolltothere,final boolean forceFocus) {

            // TODO ???? is this right
            getContentPane().breakOutAction(component, direction, scrolltothere, forceFocus);

        }

        public void add(Component comp) {
            getContentPane().add(comp);
        }
        public void add(Component comp,Object consta) {
            getContentPane().add(comp,consta);
        }

        /**
         * @param a true to maxemise the window
         * @see javax.swing.JInternalFrame#setMaximum(boolean) JInternalFrame.setMaximum
         */
        public void setMaximum(boolean a) {

            isMaximum = a;

            if (a) {
                setBounds(0, 0, DesktopPane.getDesktopPane().getWidth(), DesktopPane.getDesktopPane().getHeight());
            }
            // TODO else???
        }
        public boolean getMaximum() {
            return isMaximum;
        }
}

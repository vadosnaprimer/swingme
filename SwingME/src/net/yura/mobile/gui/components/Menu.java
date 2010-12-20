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
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JMenu
 */
public class Menu extends Button {

        // model
        private MenuBar menuItems;

        // window for scrollpane
        private Window popup;


        private boolean useAnimation=true;
        private boolean open;
        private int slide = Graphics.BOTTOM;
        private int destX;
        private int destY;
        private Icon arrowDirection;
        private Menu parentMenu;

        public Menu() {
            makeWindow();
            // TODO ???
            //arrowDirection = Graphics.RIGHT;
            //setHorizontalAlignment(Graphics.LEFT);
        }

        /**
         * @param string the text for the menu label
         * @see javax.swing.JMenu#JMenu(java.lang.String) JMenu.JMenu
         */
        public Menu(String string) {
            this();
            setText(string);
        }

        /**
         * @see javax.swing.JMenu#addSeparator() JMenu.addSeparator
         */
        public void addSeparator() {
            add( makeSeparator() );
        }
        public static Component makeSeparator() {
            Label separator = new Label();
            separator.setPreferredSize(-1, 1);
            separator.setName("Separator");
            return separator;
        }

        public void setMenuRenderer(ListCellRenderer renderer) {
            menuItems.setCellRenderer(renderer);
        }

        public void fireActionPerformed() {
            super.fireActionPerformed();
            popup.pack();
            Border insets=getInsets();
            positionMenuRelativeTo(
                    popup,
                    getXOnScreen() - insets.getLeft(), getYOnScreen()- insets.getTop(), getWidthWithBorder(),getHeightWithBorder(),
                    getDesktopPane(),
                    parentMenu==null?Graphics.TOP:Graphics.RIGHT
                    );
            openMenuAtLocation();
        }

        private static int extraWidth(Panel p) {
            Vector children = p.getComponents();
            for (int c=0;c<children.size();c++) {
                Component comp = (Component)children.elementAt(c);
                if (comp instanceof ScrollPane) {
                    return ((ScrollPane)comp).getBarThickness();
                }
                else if (comp instanceof Panel) {
                    int e = extraWidth( (Panel)comp );
                    if (e>0) {
                        return e;
                    }
                }
            }
            return 0;
        }

        public static void positionMenuRelativeTo(Window window,int x, int y, int width, int height,DesktopPane dp,int direction) {

            int w = window.getWidthWithBorder();
            int h = window.getHeightWithBorder();

            final int maxh = dp.getHeight() - dp.getMenuHeight()*2;

            if (h > maxh) {
                h = maxh;
                w = w + extraWidth(window);
            }

            // resize the popup if its bigger then the screen! if it is then shrink it
            if (w > dp.getWidth()) {
                w = dp.getWidth();
            }

            if (direction!=Graphics.RIGHT) {
                // the right x position of whatever opended me!
                int right = dp.getWidth() - x - width;
                //int bottom = dp.getHeight() - y - height;
                boolean up = (y+height/2 > dp.getHeight()/2);

                if (up) {
                    y = y-h;
                }
                else {
                    y = y+height;
                }

                // if the menu is on the right softkey, but does not touch the right side
                // when its open as its narrower then the softkey, its pushed to the edge
                if (x>0 && right==0) {
                    x = dp.getWidth() - w;
                }
                //else {
                //    x = x;
                //}
            }
            else {
                x = x+width;
            }

            window.setBoundsWithBorder(x, y, w, h);
            window.makeVisible();

        }

        public void openMenuInCentre() {

            //menuItems.workoutSize(); // what out what the needed size is
            //scroll.setPreferredSize(menuItems.getWidth(), menuItems.getHeight());
            popup.pack();

            popup.setLocationRelativeTo(null);

            // TODO, make sure it does not go over the edges
            // should be only 1 place that does this, optionpane already does this

            openMenuAtLocation();
        }


        private void openMenuAtLocation() {

            DesktopPane dp = getDesktopPane();

            if (useAnimation) {

                int x = popup.getXWithBorder();
                int y = popup.getYWithBorder();
                int w = popup.getInsets().getLeft();
                int h = popup.getInsets().getTop();

                if (slide==Graphics.BOTTOM) {
                    popup.setLocation(w+x,h+ dp.getHeight());
                }
                if (slide==Graphics.TOP) {
                    popup.setLocation(w+x, h-popup.getHeightWithBorder());
                }
                if (slide==Graphics.RIGHT) {
                    popup.setLocation(w+dp.getWidth(), h+y);
                }
                if (slide==Graphics.LEFT) {
                    popup.setLocation(w-popup.getWidthWithBorder(), h+y);
                }

                int offsetX = popup.getX() - popup.getXWithBorder();
                int offsetY = popup.getY() - popup.getYWithBorder();

                destX = x + offsetX;
                destY = y + offsetY;

                open = false;

                dp.animateComponent(this);
            }

            dp.add(popup);
        }

        /**
         * @see javax.swing.JMenu#removeAll() JMenu.removeAll
         */
        public void removeAll() {
            menuItems.removeAll();
        }

        private void makeWindow() {

            popup = new Window();
            popup.setCloseOnFocusLost(true);
            popup.addWindowListener(this);

            Button cancel = new Button( (String)DesktopPane.get("cancelText") );
            cancel.setActionCommand(Frame.CMD_CLOSE);
            cancel.addActionListener(this);
            cancel.setMnemonic(KeyEvent.KEY_END);
            popup.addCommand(cancel);

            menuItems = new MenuBar();
            menuItems.setLayoutOrientation(List.VERTICAL);
            menuItems.setLoop(true);

            if (Midlet.getPlatform()==Midlet.PLATFORM_ANDROID) {
                Button cancel2 = new Button( (String)DesktopPane.get("menuText") );
                cancel2.setActionCommand(Frame.CMD_CLOSE);
                cancel2.addActionListener(this);
                cancel2.setMnemonic(KeyEvent.KEY_MENU);
                popup.addCommand(cancel2);
            }
            else {
                menuItems.setUseSelectButton(true);
            }

            //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
            // hack, this is not the best way of doing this, but its all i can think of for now

            activateAction = menuItems.getActionCommand();
            menuItems.removeActionListener(menuItems);

            menuItems.setActionCommand("select");
            menuItems.addActionListener(this);

            //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

            popup.add(new ScrollPane(menuItems));
            popup.setName("Menu");

        }

        private String activateAction;

    	public void actionPerformed(String actionCommand) {

            // from WindowListener or cancel button
            if (Frame.CMD_CLOSE.equals(actionCommand)) {
                close();
            }
            else if ("select".equals(actionCommand)) {

                Button button = (Button)menuItems.getSelectedValue();

                menuItems.actionPerformed(activateAction);

//
//                //if (button instanceof Menu) {
//                Component comp = menuItems.getRendererComponentFor( menuItems.getSelectedIndex() );
//                button.setBoundsWithBorder(menuItems.getXOnScreen() + comp.getXWithBorder(), menuItems.getYOnScreen() + comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
                //}
                if (!(button instanceof Menu)) {
                    close();
                }

//                button.fireActionPerformed();
            }
            //#mdebug warn
            else {
                Logger.warn("unknown command in menu: "+actionCommand);
            }
            //#enddebug

        }

        private void close() {
            Component c = getParent();
            if (c instanceof MenuBar) {
                ((MenuBar)c).unselectAndUnfocus();
            }
            popup.setVisible(false);
            // cancel the parent menu
            if (parentMenu!=null) {
                parentMenu.actionPerformed(Frame.CMD_CLOSE);
            }
        }

        /**
         * @param c The component to append to the menu
         * @see javax.swing.JMenu#add(java.awt.Component) JMenu.add
         */
        protected void addImpl(Component c, Object cons, int index) {
            menuItems.insert(c,index);

            if (c instanceof Menu) {
                ((Menu)c).setParentMenu(this);
            }

            // hack to make menu items have left alignment
            if (c instanceof Button && c.getName().equals("Button")){
                ((Button)c).setName("MenuItem");
                ((Button)c).setHorizontalAlignment(Graphics.LEFT);
            }
        }

        private void setParentMenu(Menu m) {
            parentMenu = m;
        }

	public void workoutMinimumSize() {

		super.workoutMinimumSize();
		width = width + (arrowDirection!=null?(arrowDirection.getIconWidth()+gap):0);

	}

	public void paintComponent(Graphics2D g) {
            super.paintComponent(g);

            if (arrowDirection!=null) {
                arrowDirection.paintIcon(this, g, width-padding-arrowDirection.getIconWidth(), (height - arrowDirection.getIconHeight())/2 );
            }
	}

        public void animate() throws InterruptedException {


            try {

                int travelDistance = 0;
                if (slide==Graphics.BOTTOM) {
                    travelDistance = popup.getY() - destY;
                }
                else if (slide==Graphics.TOP) {
                    travelDistance = destY - popup.getY();
                }
                else if (slide==Graphics.RIGHT) {
                    travelDistance = popup.getX() - destX;
                }
                else if (slide==Graphics.LEFT) {
                    travelDistance = destX - popup.getX();
                }

            	int menuMoveSpeed = travelDistance/4 + 1;
		int step = menuMoveSpeed/10 + 1;

                while (true) {

			menuMoveSpeed = menuMoveSpeed-step;

			if(menuMoveSpeed < step) {

				menuMoveSpeed = step;
			}

			if(open) {
				// TODO close the menu and fire the action
			}
			else {

				int pX = popup.getX(),pY = popup.getY();
                                if (slide==Graphics.BOTTOM) {
                                    pY = pY - menuMoveSpeed;
                                    if (pY < destY) { pY = destY; }
                                }
                                else if (slide==Graphics.TOP) {
                                    pY = pY + menuMoveSpeed;
                                    if (pY > destY) { pY = destY; }
                                }
                                else if (slide==Graphics.RIGHT) {
                                    pX = pX - menuMoveSpeed;
                                    if (pX < destX) { pX = destX; }
                                }
                                else if (slide==Graphics.LEFT) {
                                    pX = pX + menuMoveSpeed;
                                    if (pX > destX) { pX = destX; }
                                }

                                popup.getDesktopPane().repaintHole(popup);
                                popup.setLocation(pX, pY);
				popup.repaint();

				if(pY==destY && pX == destX) {
                                    break;
				}



			}

			wait(50);

		}

            }
            finally {
                if(open) {
                    // TODO
                }
                else {
                    open=true;

                    popup.getDesktopPane().repaintHole(popup);
                    popup.setLocation(destX, destY);
                    // this is not good enough as during the animation images may have been loaded
                    // or the size of the menu could have changed, so our destination is not good enough
                    // instead what we need to do is just make sure it is on the screen
                    popup.makeVisible();
                    popup.repaint();
                }
            }

        }

        public void updateUI() {
            super.updateUI();
            if (popup!=null) {
                DesktopPane.updateComponentTreeUI(popup);
            }
            if (menuItems!=null) {
                menuItems.updateUI();
            }

            arrowDirection = (Icon)theme.getProperty("icon", Style.ALL);
        }

        public Button findMneonicButton(int mnu) {
            return menuItems.findMneonicButton(mnu);
        }

}

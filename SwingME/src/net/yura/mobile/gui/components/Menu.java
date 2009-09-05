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
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JMenu
 */
public class Menu extends Button {

        private int arrowDirection;
        private Menu parentMenu;

        private Window popup;
        private MenuBar menuItems;
        private ScrollPane scroll;

        private boolean useAnimation=true;
        private boolean open;
        private int slide = Graphics.BOTTOM;
        private int destX;
        private int destY;

        /**
         * @param string the text for the menu label
         * @see javax.swing.JMenu#JMenu(java.lang.String) JMenu.JMenu
         */
        public Menu(String string) {
            super(string);
            makeWindow();

            // TODO ???
            arrowDirection = Graphics.RIGHT;
        }

        public void fireActionPerformed() {
            super.fireActionPerformed();

            //menuItems.workoutSize(); // what out what the needed size is
            //scroll.setPreferredSize(menuItems.getWidth(), menuItems.getHeight());
            popup.pack();

            Border insets=getInsets();

            if (getWindow()!=null) {
                positionMenuRelativeTo(
                        popup,
                        getXOnScreen() - insets.getLeft(), getYOnScreen()- insets.getTop(), getWidthWithBorder(),getHeightWithBorder(),
                        Graphics.TOP
                        );
                openMenuAtLocation();
            }
            else { // if (DesktopPane.getDesktopPane().getCurrentCommands()[0]!=null) { // ???
                positionMenuRelativeTo(
                        popup,
                        getXWithBorder(),getYWithBorder(),getWidthWithBorder(),getHeightWithBorder(),
                        parentMenu==null?Graphics.TOP:Graphics.RIGHT
                        );
                openMenuAtLocation();
            }

        }

//	public Vector getComponents() {
//		return panel.getComponents();
//	}

        public static void positionMenuRelativeTo(Window window,int x, int y, int width, int height,int direction) {

            int w = window.getWidthWithBorder();
            int h = window.getHeightWithBorder();

            // resize the popup if its bigger then the screen! if it is then shrink it
            if (w > DesktopPane.getDesktopPane().getWidth()) {
                w = DesktopPane.getDesktopPane().getWidth();
            }

            int maxh = DesktopPane.getDesktopPane().getHeight() - DesktopPane.getDesktopPane().getMenuHeight()*2;

            if (h > maxh) {
                h = maxh;
            }

            if (direction!=Graphics.RIGHT) {
                // the right x position of whatever opended me!
                int right = DesktopPane.getDesktopPane().getWidth() - x - width;
                //int bottom = DesktopPane.getDesktopPane().getHeight() - y - height;
                boolean up = (y+height/2 > DesktopPane.getDesktopPane().getHeight()/2);

                if (up) {
                    y = y-h;
                }
                else {
                    y = y+height;
                }

                if (right==0) {
                    x = DesktopPane.getDesktopPane().getWidth() - w;
                }
                //else {
                //    x = x;
                //}
            }
            else {
                x = x+width;
            }

            // check we r not going off the screen if we are then move us
            if (x+w > DesktopPane.getDesktopPane().getWidth()) {
                x = DesktopPane.getDesktopPane().getWidth() - w;
            }

            int softkeyHeight = (DesktopPane.getDesktopPane().getHeight() - maxh)/2;

            if (y<softkeyHeight) {
                y=softkeyHeight;
            }
            if (y+h > maxh + softkeyHeight) {
                y = DesktopPane.getDesktopPane().getHeight() - h - softkeyHeight;
            }

            window.setBoundsWithBorder(x, y, w, h);

        }

        public void openMenuInCentre() {

            //menuItems.workoutSize(); // what out what the needed size is
            //scroll.setPreferredSize(menuItems.getWidth(), menuItems.getHeight());
            popup.pack();

            OptionPane.centre(popup);

            // TODO, make sure it does not go over the edges
            // should be only 1 place that does this, optionpane already does this

            openMenuAtLocation();
        }


        private void openMenuAtLocation() {

            if (useAnimation) {

                int x = popup.getXWithBorder();
                int y = popup.getYWithBorder();
                int w = popup.getWidthWithBorder();
                int h = popup.getHeightWithBorder();

                if (slide==Graphics.BOTTOM) {
                    popup.setBoundsWithBorder(x, DesktopPane.getDesktopPane().getHeight(), w, h);
                }
                if (slide==Graphics.TOP) {
                    popup.setBoundsWithBorder(x, -popup.getHeightWithBorder(), w, h);
                }
                if (slide==Graphics.RIGHT) {
                    popup.setBoundsWithBorder(DesktopPane.getDesktopPane().getWidth(), y, w, h);
                }
                if (slide==Graphics.LEFT) {
                    popup.setBoundsWithBorder(-popup.getWidthWithBorder(), y, w, h);
                }

                int offsetX = popup.getX() - popup.getXWithBorder();
                int offsetY = popup.getY() - popup.getYWithBorder();

                destX = x + offsetX;
                destY = y + offsetY;

                open = false;

                DesktopPane.getDesktopPane().animateComponent(this);
            }

            popup.setVisible(true);
        }

        /**
         * @see javax.swing.JMenu#removeAll() JMenu.removeAll
         */
        public void removeAll() {
            menuItems.getItems().removeAllElements();
        }

        private void makeWindow() {

            popup = new Window();
            popup.setCloseOnFocusLost(true);
            popup.addWindowListener(this);

            if (!DesktopPane.me4se) {
                //MenuBar menubar = new MenuBar();
                //Button select = new Button("Select");
                Button cancel = new Button("Cancel");
                cancel.setActionCommand(Frame.CMD_CLOSE);
                cancel.addActionListener(this);
                cancel.setMnemonic(KeyEvent.KEY_SOFTKEY2);
                //menubar.add(select);
                //menubar.add(cancel);
                //popup.setMenuBar(menubar);
                popup.addCommand(cancel);
            }

            //popup.setWindowCommand(0, );
            //popup.setWindowCommand(1, );
            //popup.setActionListener(this);
            menuItems = new MenuBar();
            menuItems.setLayoutOrientation(false);
            menuItems.addActionListener(this);
            menuItems.setUseSelectButton(true);
            menuItems.setActionCommand("select");
            scroll = new ScrollPane(menuItems);
            popup.add(scroll);
            popup.setName("Menu");
            
            // TODO!!! popup.setBorder(DesktopPane.getDefaultTheme(this).getBorder(Style.ALL));

        }

    	public void actionPerformed(String actionCommand) {

            // from WindowListener or cancel button
            if (Frame.CMD_CLOSE.equals(actionCommand)) {
                // cancel the parent menu
                popup.setVisible(false);
                if (parentMenu!=null) {
                    parentMenu.actionPerformed(actionCommand);
                }
            }
            else if ("select".equals(actionCommand)) {

                Button button = (Button)menuItems.getSelectedValue();

                //if (button instanceof Menu) {
                Component comp = menuItems.getRendererComponentFor( menuItems.getSelectedIndex() );
                button.setBoundsWithBorder(menuItems.getXOnScreen() + comp.getXWithBorder(), menuItems.getYOnScreen() + comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());
                //}
                if (!(button instanceof Menu)) {
                    popup.setVisible(false);
                    // cancel the parent menu
                    if (parentMenu!=null) {
                        parentMenu.actionPerformed(Frame.CMD_CLOSE);
                    }
                }

                button.fireActionPerformed();
            }
            //#mdebug
            else {
                System.out.println("unknown command in menu: "+actionCommand);
            }
            //#enddebug

        }

        /**
         * @param c The component to append to the menu
         * @see javax.swing.JMenu#add(java.awt.Component) JMenu.add
         */
        public void add(Component c) {
            menuItems.addElement(c);

            if (c instanceof Menu) {
                ((Menu)c).setParentMenu(this);
            }

        }

        private void setParentMenu(Menu m) {
            parentMenu = m;
        }

	public boolean keyEvent(KeyEvent keyEvent) {

            if (keyEvent.justPressedAction(Canvas.RIGHT)) {

                    fireActionPerformed();
                    return true;
            }
            return super.keyEvent(keyEvent);

	}

	public void workoutMinimumSize() {

		super.workoutMinimumSize();
		width = width + getFont().getHeight()/( ((arrowDirection & Graphics.TOP ) !=0 || (arrowDirection & Graphics.BOTTOM ) !=0) ?1:2) + padding;

	}

	public void paintComponent(Graphics2D g) {
		super.paintComponent(g);

                int s = getFont().getHeight();
		int s2 = s/2;

//                if ((arrowDirection & Graphics.RIGHT ) !=0) {
//
//                    if ((arrowDirection & Graphics.TOP ) !=0) {
//                        ScrollPane.drawUpArrow(g, width-s-padding, (height-s2)/2, s, s2);
//                    }
//                    else if ((arrowDirection & Graphics.BOTTOM ) !=0) {
//                        ScrollPane.drawDownArrow(g, width-s-padding, (height-s2)/2, s, s2);
//                    }
//                    else {
//                        ScrollPane.drawRightArrow(g, width-s2-padding, (height-s)/2, s2, s);
//                    }
//
//                }
                // TODO support left side arrows

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
                                popup.setLocation(pX, pY);

				if(pY==destY && pX == destX) {
                                    break;
				}

				popup.repaint();

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
                    popup.setLocation(destX, destY);
                    DesktopPane.getDesktopPane().fullRepaint();
                }
            }

        }

        public void updateUI() {
            super.updateUI();
            if (popup!=null) {
                //DesktopPane.updateComponentTreeUI(popup);
                // TODO: THIS IS NOT GOOD!!
            }
        }

        public void setArrowDirection(int a) {
            arrowDirection = a;
        }

        public Button findMneonicButton(int mnu) {
            if (getMnemonic() == mnu) {
                return this;
            }
            return menuItems.findMneonicButton(mnu);
        }

}

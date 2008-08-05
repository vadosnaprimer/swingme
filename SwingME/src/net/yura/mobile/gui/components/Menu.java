package net.yura.mobile.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JMenu
 */
public class Menu extends Button {

        private int arrowDirection;
    
        private Window popup;
        private Panel panel;
        private Menu parentMenu;
        private Component old;
        
        private boolean useAnimation=true;
        private boolean open;
        private int slide = Graphics.BOTTOM;
        private int destX;
        private int destY;
        
        /**
         * The menu will fire this command to the same listoner that
         * it will fire all the commands from the buttons being pressed
         * so you HAVE to set the action that u will be getting from the menu
         * as you almost always need to have a action listoner for the Menu
         * @param string the text for the menu label
         * @see javax.swing.JMenu#JMenu(java.lang.String) JMenu.JMenu
         */
        public Menu(String string,String action) {
            super(string);
            setActionCommand(action);
            makeWindow();
            arrowDirection = Graphics.RIGHT;
        }

        public void fireActionPerformed() {
            
            super.fireActionPerformed();
            
            if (owner!=null) {
                openMenu(getXInWindow()+owner.getX() -(border!=null?border.getLeft():0), getYInWindow()+owner.getY()-(border!=null?border.getTop():0), getWidthWithBorder(),getHeightWithBorder());
            }
            else if (DesktopPane.getDesktopPane().getCurrentCommands()[0]!=null) {
                openMenu(getXWithBorder(),getYWithBorder(),getWidthWithBorder(),getHeightWithBorder());
            }
        }

	public Vector getComponents() {
		return panel.getComponents();
	}
        
        private void openMenu(int x, int y, int width, int height) {

            panel.workoutSize(); // what out what the needed size is
            popup.setSize(panel.getWidthWithBorder(), panel.getHeightWithBorder());

            
            
            int w = popup.getWidthWithBorder();
            int h = popup.getHeightWithBorder();

            // resize the popup if its bigger then the screen! if it is then shrink it
            if (w > DesktopPane.getDesktopPane().getWidth()) {
                w = DesktopPane.getDesktopPane().getWidth();
            }
            if (h > DesktopPane.getDesktopPane().getHeight()) {
                h = DesktopPane.getDesktopPane().getHeight();
            }
            
            
            
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


            // check we r not going off the screen if we are then move us
            if (x+w > DesktopPane.getDesktopPane().getWidth()) {
                x = DesktopPane.getDesktopPane().getWidth() - w;
            }
            if (y+h > DesktopPane.getDesktopPane().getHeight()) {
                y = DesktopPane.getDesktopPane().getHeight() - h;
            }
            
            openMenuAtLocation(x, y, w, h);
            
        }
        
        public void openMenuInCentre() {

            panel.workoutSize(); // what out what the needed size is
            popup.setSize(panel.getWidthWithBorder(), panel.getHeightWithBorder());
            
            int w = popup.getWidthWithBorder();
            int h = popup.getHeightWithBorder();
            
            // resize the popup if its bigger then the screen! if it is then shrink it
            if (w > DesktopPane.getDesktopPane().getWidth()) {
                w = DesktopPane.getDesktopPane().getWidth();
            }
            if (h > DesktopPane.getDesktopPane().getHeight()) {
                h = DesktopPane.getDesktopPane().getHeight();
            }
            
            openMenuAtLocation((DesktopPane.getDesktopPane().getWidth()-w)/2,(DesktopPane.getDesktopPane().getHeight()-h)/2,w,h);

        }
        
        
        private void openMenuAtLocation(int x, int y, int w, int h) {
            
            old = DesktopPane.getDesktopPane().getFocusedComponent();
            
            if (useAnimation) {

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
            else {
                popup.setBoundsWithBorder(x, y, w, h);
            }
            DesktopPane.getDesktopPane().add(popup);
        }

        /**
         * @see javax.swing.JMenu#removeAll() JMenu.removeAll
         */
        public void removeAll() {
            panel.removeAll();
        }
    
        private void makeWindow() {
            
            popup = new Window();
            popup.setWindowCommand(0, new CommandButton(getText(), "select"));
            popup.setWindowCommand(1, new CommandButton("Cancel", "cancel"));
            popup.setActionListener(this);
            panel = popup.getContentPane();
            panel.setLayout(new BoxLayout(Graphics.VCENTER));
            popup.setContentPane(new ScrollPane(panel));
            popup.setName("Menu");
            // TODO!!! popup.setBorder(DesktopPane.getDefaultTheme(this).getBorder(Style.ALL));
            
        }
        
    	public void actionPerformed(String actionCommand) {
            
            DesktopPane.getDesktopPane().remove(popup);
            DesktopPane.getDesktopPane().setFocusedComponent(old);
            
            if (!"cancel".equals(actionCommand)) {
                ActionListener al = getActionListener();
                if (al!=null) {
                    al.actionPerformed(actionCommand);
                }
            }
            
            // cancel the parent menu
            if (parentMenu!=null) { parentMenu.actionPerformed("cancel"); }

        }
    
        /**
         * @param c The component to append to the menu
         * @see javax.swing.JMenu#add(java.awt.Component) JMenu.add
         */
        public void add(Component c) {
            panel.add(c);
            
            if (c instanceof Menu) {
                ((Menu)c).setParentMenu(this);
            }
            
        }
        
        private void setParentMenu(Menu m) {
            parentMenu = m;
        }
        
        /**
         * @param command
         * @param name
         * @param icon
         * @see javax.swing.JMenu#add(java.lang.String) JMenu.add
         */
        public void addMenuItem(String command,String name,Image icon) {
            
            Button b = new Button(name,icon);
            b.setActionCommand(command);
            b.addActionListener(this);
            b.setUseSelectButton(true);
            add(b);
        }

	public boolean keyEvent(KeyEvent keyEvent) {
            
            if (keyEvent.justPressedAction(Canvas.RIGHT)) {
                    
                    fireActionPerformed();
                    return true;
            }
            return super.keyEvent(keyEvent);
    
	}

	public void workoutSize() {

		super.workoutSize();
		width = width + getFont().getHeight()/( ((arrowDirection & Graphics.TOP ) !=0 || (arrowDirection & Graphics.BOTTOM ) !=0) ?1:2) + padding;

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

                int s = getFont().getHeight();
		int s2 = s/2;

                if ((arrowDirection & Graphics.RIGHT ) !=0) {
                    
                    if ((arrowDirection & Graphics.TOP ) !=0) {
                        ScrollPane.drawUpArrow(g, width-s-padding, (height-s2)/2, s, s2);
                    }
                    else if ((arrowDirection & Graphics.BOTTOM ) !=0) {
                        ScrollPane.drawDownArrow(g, width-s-padding, (height-s2)/2, s, s2);
                    }
                    else {
                        ScrollPane.drawRightArrow(g, width-s2-padding, (height-s)/2, s2, s);
                    }
                    
                }
                // TODO support left side arrows

	}
        
        public void animate() {

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
					
					open=true;
					break;
				}
				
				popup.repaint();
			}

			wait(50);
		}

                DesktopPane.getDesktopPane().fullRepaint();

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

}

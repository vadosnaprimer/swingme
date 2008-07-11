package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.layout.BoxLayout;

/**
 * @author Yura Mamyrin
 */
public class Menu extends Button {

        private Window popup;
        private Panel panel;
        private Menu parentMenu;

        public Menu(String string) {
            super(string);
            makeWindow();
        }

        public void fireActionPerformed() {
            
            openMenu(getXInWindow()+owner.getX(), getYInWindow()+owner.getY(), width, height);
            
        }
        
        public void openMenu(int x, int y, int width, int height) {
            popup.setBoundsWithBorder(0, 0, 50, 50);
            DesktopPane.getDesktopPane().add(popup);
        }
    
        private void makeWindow() {
            
            popup = new Window();
            //popup.setWindowCommand(0, new CommandButton(getText(), "select"));
            popup.setWindowCommand(1, new CommandButton("Cancel", "cancel"));
            popup.setActionListener(this);
            panel = popup.getContentPane();
            panel.setLayout(new BoxLayout(Graphics.VCENTER));
            popup.setContentPane(new ScrollPane(panel));
            
        }
        
    	public void actionPerformed(String actionCommand) {
            
            DesktopPane.getDesktopPane().remove(popup);
            super.fireActionPerformed();
            
            if (!"cancel".equals(actionCommand)) {
                ActionListener al = getActionListener();
                if (al!=null) {
                    al.actionPerformed(actionCommand);
                }
            }
            
            // cancel the parent menu
            if (parentMenu!=null) { parentMenu.actionPerformed("cancel"); }

        }
    
        public void add(Component c) {
            panel.add(c);
            
            if (c instanceof Menu) {
                ((Menu)c).setParentMenu(this);
            }
            
        }
        
        private void setParentMenu(Menu m) {
            parentMenu = m;
        }
        
        public void addMenuItem(String name,Image icon,String command) {
            
            Button b = new Button(name,icon);
            b.setActionCommand(command);
            b.addActionListener(this);
            add(b);
        }
        
}

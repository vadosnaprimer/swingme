package net.yura.mobile.gui.components;

import java.util.Vector;
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

    public Vector getComponents() {
        return panel.getComponents();
    }
        
        public void openMenu(int x, int y, int width, int height) {
            panel.doLayout(); // what out what the needed size is
            boolean up = (y+height/2 > DesktopPane.getDesktopPane().getHeight()/2);

            popup.setSize(panel.getWidthWithBorder(), panel.getHeightWithBorder());
            
            int w = popup.getWidthWithBorder();
            int h = popup.getHeightWithBorder();

            if (x+w > DesktopPane.getDesktopPane().getWidth()) {
                x = DesktopPane.getDesktopPane().getWidth() - w;
            }
            
            // TODO if height is too big make it less
            
            popup.setBoundsWithBorder(x, up?(y-h):(y+height), w, h);
            DesktopPane.getDesktopPane().add(popup);
        }

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
            popup.setBorder(DesktopPane.getDefaultTheme().menuBorder);
            
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
        
        public void addMenuItem(String command,String name,Image icon) {
            
            Button b = new Button(name,icon);
            b.setActionCommand(command);
            b.addActionListener(this);
            b.setUseSelectButton(true);
            add(b);
        }
        
}

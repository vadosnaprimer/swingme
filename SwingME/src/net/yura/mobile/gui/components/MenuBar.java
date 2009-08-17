package net.yura.mobile.gui.components;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;

/**
 * @author Yura Mamyrin
 */
public class MenuBar extends List implements ActionListener {

    public MenuBar() {
        super(null, new MenuItemRenderer(), true);
        addActionListener(this);
    }

    public String getDefaultName() {
        return "MenuBar";
    }

    public void add(Button button) {
        if (button.getMnemonic() == 0) {
            switch(getSize()) {
                // TODO make sure this mnemonic is not used for another button
                case 0: button.setMnemonic(KeyEvent.KEY_SOFTKEY1); break;
                case 1: button.setMnemonic(KeyEvent.KEY_SOFTKEY2); break;
                case 2: button.setMnemonic(KeyEvent.KEY_SOFTKEY3); break;
            }
        }
        addElement(button);
    }
    /**
     * @see java.awt.Container#removeAll()
     */
    public void removeAll() {
        getItems().removeAllElements();
    }

    public void actionPerformed(String actionCommand) {
        fireButton( getSelectedIndex() );
    }

    public void paint(Graphics2D g) {
        if (DesktopPane.me4se) {
            super.paint(g);
        }
        else {
            // menu bar is invisible on a mobile
            paintComponent(g);
        }
    }

    public void paintComponent(Graphics2D g) {
        if (DesktopPane.me4se) {
            super.paintComponent(g);
        }
        else {

            if (getWindow() == DesktopPane.getDesktopPane().getSelectedFrame()) {

                int offsetX = getXOnScreen();
                int offsetY = getYOnScreen();
                g.translate(-offsetX,-offsetY);

                for (int i=0; i< getSize(); i++) {
                    Component component = getRendererComponentOnScreen(i);

                    int x = component.getX();
                    int y = component.getY();

                    g.translate(x,y);
                    component.paint(g);
                    g.translate(-x, -y);
                }

                g.translate(offsetX,offsetY);
            }

        }
        
    }

    /**
     * @see javax.swing.JList#locationToIndex(java.awt.Point) JList.locationToIndex
     */
    public int locationToIndex(int x,int y) {

        if (!DesktopPane.me4se) {

            x = x + getXOnScreen();
            y = y + getYOnScreen();

            int size = getSize();
            for (int i=0; i< size; i++) {

                Component comp = getRendererComponentOnScreen(i);

                if (
                        (x >= comp.getXWithBorder()) &&
                        (x <= (comp.getXWithBorder()+comp.getWidthWithBorder())) &&
                        (y >= comp.getYWithBorder()) &&
                        (y <= (comp.getYWithBorder()+comp.getHeightWithBorder()))) {

                    return i;

                }
            }
            return -1;
        }
        throw new UnsupportedOperationException();
    }

    public void pointerEvent(int type, int x, int y, KeyEvent keys) {
        if (DesktopPane.me4se) {
            super.pointerEvent(type, x, y, keys);
        }
        else {
            if (type == DesktopPane.PRESSED) {
                int i = locationToIndex(x,y);
                if (i>=0) {
                    fireButton(i);
                }
            }

       }
    }

    private void fireButton(int index) {

        Component comp = getRendererComponentOnScreen(index);

        Button button = (Button)getElementAt(index);

        button.setBoundsWithBorder(comp.getXWithBorder(), comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());

        button.fireActionPerformed();

    }

    public Component getRendererComponentOnScreen(int index){

        if (DesktopPane.me4se) {

            Component comp = getRendererComponentFor( index );
            comp.setBoundsWithBorder(getXOnScreen()+comp.getXWithBorder(), getYOnScreen()+comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder() );
            return comp;
        }
        else {

            Button button = (Button)getElementAt(index);

            boolean sideSoftKeys = DesktopPane.getDesktopPane().isSideSoftKeys();

            int mnemonic = button.getMnemonic();

            int desktopWidth = DesktopPane.getDesktopPane().getWidth();
            int desktopHeight = DesktopPane.getDesktopPane().getHeight();

    //System.out.println("Screen height: "+desktopHeight);

            ListCellRenderer renderer = getCellRenderer();
            Component component = (Component) renderer.getListCellRendererComponent(this,button,0,false,false);

            component.workoutSize();
            int componentWidth = component.getWidthWithBorder();
            int componentHeight = component.getHeightWithBorder();

    //System.out.println("Component height: "+componentHeight);

            //button.setSize(componentWidth, componentHeight);

            int bottom = desktopHeight-componentHeight;
            int right = desktopWidth-componentWidth;

    //System.out.println("Bottom: "+bottom);

            int x = 0, y = 0;

            if (mnemonic == KeyEvent.KEY_SOFTKEY1 && (!sideSoftKeys)) {
                // Bottom left
                x=0;
                y=bottom;
            }
            else if ((mnemonic == KeyEvent.KEY_SOFTKEY2 && (!sideSoftKeys)) || (mnemonic == KeyEvent.KEY_SOFTKEY1 && sideSoftKeys)) {
                // Bottom right
                x = right;
                y = bottom;
            }
            else if (mnemonic == KeyEvent.KEY_SOFTKEY3 && (!sideSoftKeys)) {
                // Bottom middle
                x = (desktopWidth/2)-(componentWidth/2);
                y = bottom;
            }
            else if (mnemonic == KeyEvent.KEY_SOFTKEY2 && sideSoftKeys) {
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

    }

}

package net.yura.mobile.gui.components;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.cellrenderer.MenuItemRenderer;

/**
 * @author Yura Mamyrin
 */
public class MenuBar extends List implements ActionListener {

    public MenuBar() {

        setLayoutOrientation(true);

        MenuItemRenderer renderer = new MenuItemRenderer();

        renderer.setName("MenuRenderer");

        setCellRenderer(renderer);

        addActionListener(this);
    }

    public boolean isVisible() {

        // TODO not sure if this should be here or frame
        if (!DesktopPane.me4se) {
            Window w = getWindow();
            if (w!=null && w instanceof Frame && ((Frame)w).getMenuBar() == this ) {
                return false;
            }
        }
        return super.isVisible();
    }

    public String getDefaultName() {
        return "MenuBar";
    }

    public void add(Button button) {

        // this is same as in optionpane
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
        int index = getSelectedIndex();

        Component comp = getRendererComponentFor( index );
        //comp.setBoundsWithBorder(getXOnScreen()+comp.getXWithBorder(), getYOnScreen()+comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder() );

        //Component comp = getRendererComponentOnScreen(index);

        Button button = (Button)getElementAt(index);

        button.setBoundsWithBorder(getXOnScreen()+comp.getXWithBorder(), getYOnScreen()+comp.getYWithBorder(), comp.getWidthWithBorder(), comp.getHeightWithBorder());

        button.fireActionPerformed();

    }

        public Button findMneonicButton(int mnu) {

            int size = getSize();
            for(int i = 0; i < size; i++) {
                Object component = getElementAt(i);
                if (component instanceof Menu) {
                    Button button = ((Menu)component).findMneonicButton(mnu);
                    if (button!=null) {
                        return button;
                    }
                }
                else if (component instanceof Button) {
                    Button button = (Button)component;
                    if (button.getMnemonic() == mnu) {
                        return button;
                    }
                }
            }
            return null;

        }

}

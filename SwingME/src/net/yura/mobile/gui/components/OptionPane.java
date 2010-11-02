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

import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.layout.GridBagConstraints;
import net.yura.mobile.gui.layout.GridBagLayout;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JOptionPane
 */
public class OptionPane extends Frame implements Runnable, ActionListener {

    private static Vector allWindows = new Vector();
    public static Vector getAllWindows() {
        return allWindows;
    }

    /**
     * @see javax.swing.JOptionPane#YES_NO_OPTION JOptionPane.YES_NO_OPTION
     */
    public static final int YES_NO_OPTION = 0;
    /**
     * @see javax.swing.JOptionPane#OK_OPTION JOptionPane.OK_OPTION
     */
    public static final int OK_OPTION = 1;
    /**
     * @see javax.swing.JOptionPane#OK_CANCEL_OPTION JOptionPane.OK_CANCEL_OPTION
     */
    public static final int OK_CANCEL_OPTION = 2;

    /**
     * @see javax.swing.JOptionPane#ERROR_MESSAGE JOptionPane.ERROR_MESSAGE
     */
    public static final int ERROR_MESSAGE = 0;
    /**
     * @see javax.swing.JOptionPane#INFORMATION_MESSAGE JOptionPane.INFORMATION_MESSAGE
     */
    public static final int INFORMATION_MESSAGE = 1;
    /**
     * @see javax.swing.JOptionPane#WARNING_MESSAGE JOptionPane.WARNING_MESSAGE
     */
    public static final int WARNING_MESSAGE = 2;
    /**
     * @see javax.swing.JOptionPane#QUESTION_MESSAGE JOptionPane.QUESTION_MESSAGE
     */
    public static final int QUESTION_MESSAGE = 3;
    /**
     * @see javax.swing.JOptionPane#PLAIN_MESSAGE JOptionPane.PLAIN_MESSAGE
     */
    public static final int PLAIN_MESSAGE = -1;

    /**
     * @see javax.swing.JOptionPane#initialValue JOptionPane.initialValue
     */
    private Button defaultCommand;

    /**
     * @see javax.swing.JOptionPane#icon JOptionPane.icon
     */
    private Label icon;

    private ScrollPane scroll;
    private Panel cmdPanel;
    private Panel content;

    private int messageType;
    private Object message;
    /**
     * @see javax.swing.JOptionPane#JOptionPane() JOptionPane.JOptionPane
     */
    public OptionPane() {


        for (int c=0;c<allWindows.size();c++) {
            if (((WeakReference)allWindows.elementAt(c)).get() == null) {
                allWindows.removeElementAt(c);
                c--;
            }
        }
        allWindows.addElement(new WeakReference(this));


        setName("Dialog");

        setMaximizable(false);

        content = new Panel( new GridBagLayout(1, 3, 4, 4, 4, 4) );
        // we need to use a layout manager that has
        // * gaps betwen elements
        // * streaches components to the max width
        // * allows components of different heights
        // * has gaps round the edge

        Panel panel = getContentPane();

        //add(title,Graphics.TOP);
        //add(panel);

        icon = new Label();
        icon.setHorizontalAlignment(Graphics.HCENTER);

        Panel c = new Panel( new BoxLayout(Graphics.VCENTER) );

        if (DesktopPane.getDesktopPane().VERY_BIG_SCREEN) {
            panel.add(icon,Graphics.LEFT);
        }
        else {
            c.add(icon);
        }

        c.add(content);

        scroll = new ScrollPane(c);
        panel.add( scroll );
        cmdPanel = new Panel( new FlowLayout() );
        panel.add( cmdPanel ,Graphics.BOTTOM);
    }

    private ActionListener actionListener;

    public void setActionListener(ActionListener actionListener) {
            this.actionListener = actionListener;
    }

    public void actionPerformed(String actionCommand) {
        setVisible(false);
        if (actionListener!=null) {
            actionListener.actionPerformed(actionCommand);
        }
    }

    public boolean processKeyEvent(KeyEvent keypad) {

        if (keypad.justPressedAction(Canvas.FIRE) || keypad.justPressedKey(KeyEvent.KEY_CALL)) {

            if (defaultCommand!=null) {
                actionPerformed( defaultCommand.getActionCommand() );
            }
            return true;
        }
        return false;
    }

    /**
     * @see javax.swing.JOptionPane#setMessage(java.lang.Object) JOptionPane.setMessage
     */
    public void setMessage(Object newMessage) {
        this.message = newMessage;

        content.removeAll();
        scroll.getView().setLocation(0, 0);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;

        if (newMessage instanceof Object[]) {
            Object[] objects = (Object[])newMessage;
            for (int c=0;c<objects.length;c++) {
                content.add(getComponentFromObject(objects[c]),constraints);
            }
        }
        else if (newMessage instanceof Vector) {
            Vector objects = (Vector)newMessage;
            for (int c=0;c<objects.size();c++) {
                content.add(getComponentFromObject(objects.elementAt(c)),constraints);
            }
        }
        else {
            content.add(getComponentFromObject(newMessage),constraints);
        }

    }

    /**
     * @see javax.swing.JOptionPane#getMessage() JOptionPane.getMessage
     */
    public Object getMessage() {
        return message;
    }

    private Component getComponentFromObject(Object object) {

        DesktopPane dp = DesktopPane.getDesktopPane();

        if (object instanceof Component) {
            Component component = (Component)object;
            if (component instanceof TextField && component.getPreferredWidth() == -1) {
                component.setPreferredSize(dp.getWidth()/2, component.getPreferredHeight());
            }
            return component;
        }
        if (object instanceof Icon) {
            return new Label((Icon)object);
        }


        int space = dp.getWidth() - 12 - scroll.getBarThickness(); // padding
        if (dp.VERY_BIG_SCREEN) {
            icon.workoutSize();
            space = space - icon.getWidthWithBorder();
        }


        String txt = String.valueOf(object);
        if (txt.startsWith("<html>")) {
            TextPane tp = new TextPane();
            tp.setText(txt);
            tp.setPreferredSize(space, -1);
            return tp;
        }

        Label tmp = new Label();
        tmp.setValue(object);



        tmp.workoutSize();
        if (tmp.getWidthWithBorder() < space) {
            return tmp;
        }

        TextArea tmp2 = new TextArea(); // TODO change to TextPane and centered text
        tmp2.setLineWrap(true);
        tmp2.setFocusable(false);
        tmp2.setText(txt);
        tmp2.setPreferredSize( space, -1 );
        return tmp2;
    }

    /**
     * @see javax.swing.JOptionPane#setMessageType(int) JOptionPane.setMessageType
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
        Icon icn=null;
        switch (messageType) {
            case WARNING_MESSAGE: icn = (Icon)theme.getProperty("WARNING_MESSAGE", Style.ALL); break;
            case ERROR_MESSAGE: icn = (Icon)theme.getProperty("ERROR_MESSAGE", Style.ALL); break;
            case INFORMATION_MESSAGE: icn = (Icon)theme.getProperty("INFORMATION_MESSAGE", Style.ALL); break;
            case QUESTION_MESSAGE: icn = (Icon)theme.getProperty("QUESTION_MESSAGE", Style.ALL); break;
        }
        setIcon(icn);
    }

    /**
     * @see javax.swing.JOptionPane#getMessageType() JOptionPane.getMessageType
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * @see javax.swing.JOptionPane#setInitialValue(java.lang.Object) JOptionPane.setInitialValue
     */
    public void setInitialValue(Button initialValue) {
        defaultCommand = initialValue;
    }

    /**
     * @see javax.swing.JOptionPane#setOptions(java.lang.Object[]) JOptionPane.setOptions
     */
    public void setOptions(Button[] options) {
        cmdPanel.removeAll();
        for (int c=0;c<options.length;c++) {
            Button button = options[c];
            ActionListener[] actionListeners = button.getActionListeners();
            for(int i = 0; i < actionListeners.length; i++){
              button.removeActionListener(actionListeners[i]);
            }
            button.addActionListener(this);
            cmdPanel.add(button);
        }
        // we only want to autoMnemonic when its a simple 2 or 1 button dialog
        if (options.length <= 2) {
            // this should be removed one day
            autoMnemonic( cmdPanel.getComponents() );
        }
    }

    /**
     * @see javax.swing.JOptionPane#getOptions() JOptionPane.getOptions
     */
    public Button[] getOptions() {
        Vector v = cmdPanel.getComponents();
        Button[] buttons = new Button[v.size()];
        v.copyInto(buttons);
        return buttons;
    }

    public static void autoMnemonic(Vector items) {
        for (int c=0;c<items.size();c++) {
            Component button = (Component)items.elementAt(c);
            // this is same as in optionpane
            if (button instanceof Button && ((Button)button).getMnemonic() == 0) {
                switch(c) {
                    // TODO make sure this mnemonic is not used for another button
                    case 0:
                        //#debug debug
                        Logger.debug("Button 1 should already have Mnemonic "+button);
                        ((Button)button).setMnemonic(KeyEvent.KEY_SOFTKEY1);
                        break;
                    case 1:
                        //#debug debug
                        Logger.debug("Button 2 should already have Mnemonic "+button);
                        ((Button)button).setMnemonic(KeyEvent.KEY_SOFTKEY2);
                        break;
                    //case 2: ((Button)button).setMnemonic(KeyEvent.KEY_SOFTKEY3); break;
                }
            }
        }
    }

    /**
     * @see javax.swing.JOptionPane#setIcon(javax.swing.Icon) JOptionPane.setIcon
     */
    public void setIcon(Icon icon) {
        this.icon.setIcon(icon);
    }

    public void run() {
        try {
        //content.workoutSize(); // what out what the needed size is
        //Logger.debug("prefered size of scroll "+content.getWidth()+" "+content.getHeight());
        //scroll.setPreferredSize(content.getWidth(), content.getHeight());
        pack();

        DesktopPane dp = DesktopPane.getDesktopPane();

        int maxw = dp.getWidth();
        int maxh = dp.getHeight() - dp.getMenuHeight()*2;

        if (getHeightWithBorder() > maxh) {
            setFocusedComponent(null);
            setBoundsWithBorder(0,0,getWidthWithBorder() + scroll.getBarThickness(), maxh);
        }
        if (getWidthWithBorder() > maxw) {
            setFocusedComponent(null);
            setBoundsWithBorder(0,0,maxw, getHeightWithBorder() +((getHeightWithBorder() == maxh)?0:scroll.getBarThickness()) );
        }
        getMostRecentFocusOwner();

        setLocationRelativeTo(null);

        setVisible(true);
        }
        catch(Throwable t) {
          Logger.error(t);
        }
    }

    private static Button makeButton(String label,String actionCommand,int m) {
        Button button = new Button((String)DesktopPane.get(label));
        button.setActionCommand(actionCommand);
        button.setMnemonic(m);
        return button;
    }

    private boolean factory;

    /**
     * @see javax.swing.JOptionPane#showOptionDialog(java.awt.Component, java.lang.Object, java.lang.String, int, int, javax.swing.Icon, java.lang.Object[], java.lang.Object) JOptionPane.showOptionDialog
     */
    public static OptionPane showOptionDialog(ActionListener parent, Object message, String title, int optionType, int messageType, Icon icon, Button[] options, Button initialValue) {

        Vector myselfs = getAllWindows();
        OptionPane myself=null;

        for (int c=0;c<myselfs.size();c++) {
            Window op = (Window)((WeakReference)myselfs.elementAt(c)).get();
            if (op instanceof OptionPane && ((OptionPane)op).factory && !op.isVisible()) {
                myself = (OptionPane)op;
                break;
            }
        }

        if (myself==null) {
            myself = new OptionPane();
            myself.factory = true;
        }

        myself.setTitle(title);
        myself.setActionListener(parent);
        myself.setMessageType(messageType);
        if (icon!=null) {
            myself.setIcon(icon);
        }
        myself.setMessage(message);

        if (options==null) {
            switch (optionType) {
                case YES_NO_OPTION:
                    options = new Button[] {makeButton("yesText","yes",KeyEvent.KEY_SOFTKEY1),makeButton("noText","no",KeyEvent.KEY_SOFTKEY2)};
                    break;
                case OK_CANCEL_OPTION:
                    options = new Button[] {makeButton("okText","ok",KeyEvent.KEY_SOFTKEY1),makeButton("cancelText","cancel",KeyEvent.KEY_SOFTKEY2)};
                    break;
                case OK_OPTION:
                default:
                    // it is not clear if this should be SOFT1 or SOFT2
                    // as when u are showing a about box, u will want it on SOFT2
                    // and if you are showing a question/input dialog then SOFT1
                    options = new Button[] {makeButton("okText","ok",KeyEvent.KEY_SOFTKEY2)};
                    break;
            }
            initialValue = options[0];
        }

        myself.setOptions(options);

        myself.setInitialValue(initialValue);

        DesktopPane.invokeLater(myself);

        return myself;
    }

    /**
     * @see javax.swing.JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object, java.lang.String, int) JOptionPane.showMessageDialog
     */
    public static OptionPane showMessageDialog(ActionListener parent, Object message, String title, int messageType) {
        return showOptionDialog(parent, message, title, OK_OPTION, messageType, null, null, null);
    }

    /**
     * @see javax.swing.JOptionPane#showConfirmDialog(java.awt.Component, java.lang.Object, java.lang.String, int) JOptionPane.showConfirmDialog
     */
    public static OptionPane showConfirmDialog(ActionListener parent, Object message, String title, int optionType) {
        return showOptionDialog(parent, message, title, optionType, QUESTION_MESSAGE, null, null, null);
    }

}

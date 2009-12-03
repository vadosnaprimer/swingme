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

/**
 * @author Yura Mamyrin
 * @see javax.swing.JOptionPane
 */
public class OptionPane extends Frame implements Runnable, ActionListener {

    public static final int YES_NO_OPTION = 0;
    public static final int OK_OPTION = 1;
    public static final int OK_CANCEL_OPTION = 2;
    
    public static final int ERROR_MESSAGE = 0;
    public static final int INFORMATION_MESSAGE = 1;
    public static final int WARNING_MESSAGE = 2;
    public static final int QUESTION_MESSAGE = 3;
    public static final int PLAIN_MESSAGE = -1;
    
    
    //private TitleBar title;
    private Panel content;
    private Label icon;
    private Button defaultCommand;
    private ScrollPane scroll;
    private Panel cmdPanel;
    
    public OptionPane() {
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

        if (DesktopPane.me4se) {
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
    
    public void setMessage(Object newMessage) {
        
        content.removeAll();
        scroll.getComponent().setLocation(0, 0);
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
    
    private Component getComponentFromObject(Object object) {
        if (object instanceof Component) {
            Component component = (Component)object;
            if (component instanceof TextField && component.getPreferredWidth() == -1) {
                component.setPreferredSize(DesktopPane.getDesktopPane().getWidth()/2, component.getPreferredHeight());
            }
            return component;
        }
        if (object instanceof Icon) {
            return new Label((Icon)object);
        }


        int space = DesktopPane.getDesktopPane().getWidth() - 12 - scroll.getBarThickness(); // padding
        if (DesktopPane.me4se) {
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
    
    public void setMessageType(int messageType) {
        Icon icn=null;
        switch (messageType) {
            case WARNING_MESSAGE: icn = (Icon)theme.getProperty("WARNING_MESSAGE", Style.ALL); break;
            case ERROR_MESSAGE: icn = (Icon)theme.getProperty("ERROR_MESSAGE", Style.ALL); break;
            case INFORMATION_MESSAGE: icn = (Icon)theme.getProperty("INFORMATION_MESSAGE", Style.ALL); break;
            case QUESTION_MESSAGE: icn = (Icon)theme.getProperty("QUESTION_MESSAGE", Style.ALL); break;
        }
        setIcon(icn);
    }

    public void setInitialValue(Button initialValue) {
        defaultCommand = initialValue;
    }

    public void setOptions(Button[] options) {
        cmdPanel.removeAll();
        for (int c=0;c<options.length;c++) {
            Button button = (Button)options[c];
            button.addActionListener(this);
            cmdPanel.add(button);
        }
        MenuBar.autoMnemonic( cmdPanel.getComponents() );
    }

    public void setIcon(Icon icon) {
        this.icon.setIcon(icon);
    }

    public void run() {
        
        //content.workoutSize(); // what out what the needed size is
        //System.out.println("prefered size of scroll "+content.getWidth()+" "+content.getHeight());
        //scroll.setPreferredSize(content.getWidth(), content.getHeight());
        pack();

        int maxw = DesktopPane.getDesktopPane().getWidth();
        int maxh = DesktopPane.getDesktopPane().getHeight() - DesktopPane.getDesktopPane().getMenuHeight()*2;

        if (getHeightWithBorder() > maxh) {
            setFocusedComponent(null);
            setBoundsWithBorder(0,0,getWidthWithBorder() + scroll.getBarThickness(), maxh);
        }
        if (getWidthWithBorder() > maxw) {
            setFocusedComponent(null);
            setBoundsWithBorder(0,0,maxw, getHeightWithBorder() +((getHeightWithBorder() == maxh)?0:scroll.getBarThickness()) );
        }
        getMostRecentFocusOwner();
        
        centre(this);
        
        setVisible(true);

    }
    
    public static void centre(Window w) {
        
        w.setLocation((DesktopPane.getDesktopPane().getWidth() - w.getWidth()) /2, 
                (DesktopPane.getDesktopPane().getHeight() - w.getHeight()) /2
        );
    }
    
    private boolean factory;
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
                    options = new Button[] {makeButton("Yes","yes"),makeButton("No","no")};
                    break;
                case OK_CANCEL_OPTION:
                    options = new Button[] {makeButton("OK","ok"),makeButton("Cancel","cancel")};
                    break;
                case OK_OPTION:
                default:
                    options = new Button[] {makeButton("OK","ok")};
                    break;
            }
            initialValue = options[0];
        }
        
        myself.setOptions(options);

        myself.setInitialValue(initialValue);
        
        DesktopPane.getDesktopPane().invokeLater(myself);

        return myself;
    }

    public static OptionPane showMessageDialog(ActionListener parent, Object message, String title, int messageType) {
        
        return showOptionDialog(parent, message, title, OK_OPTION, messageType, null, null, null);
        
    }

    public static OptionPane showConfirmDialog(ActionListener parent, Object message, String title, int optionType) {
        
        return showOptionDialog(parent, message, title, optionType, QUESTION_MESSAGE, null, null, null);
        
    }

    public static Button makeButton(String label,String actionCommand) {
        Button button = new Button(label);
        button.setActionCommand(actionCommand);
        return button;
    }
    
}

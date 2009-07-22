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
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JOptionPane
 */
public class OptionPane extends Window {

    public static final int YES_NO_OPTION = 0;
    public static final int OK_OPTION = 1;
    public static final int OK_CANCEL_OPTION = 2;
    
    public static final int ERROR_MESSAGE = 0;
    public static final int INFORMATION_MESSAGE = 1;
    public static final int WARNING_MESSAGE = 2;
    public static final int QUESTION_MESSAGE = 3;
    public static final int PLAIN_MESSAGE = -1;
    
    
    private TitleBar title;
    private Panel content;
    private Label icon;
    private CommandButton defaultCommand;
    private ScrollPane scroll;
    
    public OptionPane() {
        setName("Dialog");
        
        super.setActionListener(this);
        title = new TitleBar("", null, false, false, false, false, false);
        content = new Panel( new BoxLayout(Graphics.VCENTER) );
        
        Panel panel = new Panel(new BorderLayout());
        
        add(title,Graphics.TOP);
        add(panel);
        
        icon = new Label();
        
        panel.add(icon,Graphics.LEFT);
        scroll = new ScrollPane(content);
        panel.add( scroll );
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
    
    public boolean keyEvent(KeyEvent keypad) {
		
        if (keypad.justPressedAction(Canvas.FIRE)) {
            
            if (defaultCommand!=null) {
                actionPerformed( defaultCommand.getActionCommand() );
            }
            return true;
        }
        return false;
    }
    
    public void setMessage(Object newMessage) {
        
        content.removeAll();
        content.setLocation(0, 0);
        
        if (newMessage instanceof Object[]) {
            Object[] objects = (Object[])newMessage;
            for (int c=0;c<objects.length;c++) {
                content.add(getComponentFromObject(objects[c]));
            }
        }
        else if (newMessage instanceof Vector) {
            Vector objects = (Vector)newMessage;
            for (int c=0;c<objects.size();c++) {
                content.add(getComponentFromObject(objects.elementAt(c)));
            }
        }
        else {
            content.add(getComponentFromObject(newMessage));
        }
        
        
    }
    
    private Component getComponentFromObject(Object object) {
        if (object instanceof Component) {
            return (Component)object;
        }
        if (object instanceof Icon) {
            return new Label((Icon)object);
        }
        Label tmp = new Label();
        tmp.setValue(object);
        return tmp;
    }
    
    public void setTitle(String newTitle) {
        title.setTitle(newTitle);
    }
    
    
    public void setMessageType(int messageType) {
        // TODO
    }

    public void setInitialValue(CommandButton initialValue) {
        defaultCommand = initialValue;
    }

    public void setOptions(CommandButton[] options) {
       setWindowCommand(0, options[0]);
       setWindowCommand(1, (options.length > 1)?options[1]:null);
    }

    public void setIcon(Icon icon) {
        this.icon.setIcon(icon);
    }

    private void open() {
        
        content.workoutSize(); // what out what the needed size is
        scroll.setPreferredSize(content.getWidth(), content.getHeight());
        pack();

        int maxw = DesktopPane.getDesktopPane().getWidth();
        int maxh = DesktopPane.getDesktopPane().getHeight() - DesktopPane.getDesktopPane().getSoftkeyHeight()*2;

        if (getHeightWithBorder() > maxh) {
            setFocusedComponent(null);
            setBoundsWithBorder(0,0,getWidthWithBorder() + ScrollPane.getBarThickness(scroll.getWidth(),scroll.getHeight()), maxh);
        }
        if (getWidthWithBorder() > maxw) {
            setFocusedComponent(null);
            setBoundsWithBorder(0,0,maxw, getHeightWithBorder() +((getHeightWithBorder() == maxh)?0:ScrollPane.getBarThickness(scroll.getWidth(),scroll.getHeight())) );
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
    public static void showOptionDialog(ActionListener parent, Object message, String title, int optionType, int messageType, Icon icon, CommandButton[] options, CommandButton initialValue) {

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
        myself.setMessage(message);
        myself.setTitle(title);
        myself.setActionListener(parent);
        myself.setMessageType(messageType);
        myself.setIcon(icon);
        
        if (options==null) {
            switch (optionType) {
                case YES_NO_OPTION:
                    options = new CommandButton[] {new CommandButton("Yes","yes"),new CommandButton("No","no")};
                    break;
                case OK_CANCEL_OPTION:
                    options = new CommandButton[] {new CommandButton("OK","ok"),new CommandButton("Cancel","cancel")};
                    break;
                case OK_OPTION:
                default:
                    options = new CommandButton[] {new CommandButton("OK","ok")};
                    break;
            }
            initialValue = options[0];
        }
        
        myself.setOptions(options);
        myself.setInitialValue(initialValue);
        
        
        myself.open();
    }

    public static void showMessageDialog(ActionListener parent, Object message, String title, int messageType) {
        
        showOptionDialog(parent, message, title, OK_OPTION, messageType, null, null, null);
        
    }

    public static void showConfirmDialog(ActionListener parent, Object message, String title, int optionType) {
        
        showOptionDialog(parent, message, title, optionType, QUESTION_MESSAGE, null, null, null);
        
    }
    
}

package net.yura.mobile.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;

/**
 * 
 * 
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
    
    public OptionPane() {
        
        super.setActionListener(this);
        title = new TitleBar("", null, false, false, false, false, false);
        content = new Panel( new BoxLayout(Graphics.VCENTER) );
        
        Panel panel = new Panel(new BorderLayout());
        
        getContentPane().add(title,Graphics.TOP);
        getContentPane().add(panel);
        
        icon = new Label();
        
        panel.add(icon,Graphics.LEFT);
        panel.add( new ScrollPane(content) );
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
    
    
    public String getName() {
        return "Dialog";
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
       if (options.length > 1) {
           setWindowCommand(1, options[1]);
       }
    }

    public void setIcon(Image icon) {
        this.icon.setIcon(icon);
    }
    
    
    
    
    private static OptionPane myself;
    
    public static void showOptionDialog(ActionListener parent, Object message, String title, int optionType, int messageType, Image icon, CommandButton[] options, CommandButton initialValue) {
        
        if (myself==null) {
            myself = new OptionPane();
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
        
        myself.setSize(100, 100);
        
        myself.setVisible(true);
    }

    public static void showMessageDialog(ActionListener parent, Object message, String title, int messageType) {
        
        showOptionDialog(parent, message, title, OK_OPTION, messageType, null, null, null);
        
    }

    
}

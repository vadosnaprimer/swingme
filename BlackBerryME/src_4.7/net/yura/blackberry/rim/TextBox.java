package net.yura.blackberry.rim;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class TextBox {

    String title;
    String text;
    int maxSize;
    int constraints;
    Vector commands = new Vector();
    CommandListener commandListener;
    
    public TextBox(String title, String text, int maxSize, int constraints) {
        this.title = title;
        this.text = text;
        this.maxSize = maxSize;
        this.constraints = constraints;
    }

    public String getTitle() {
        return title;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getString() {
        return text;
    }

    public int getConstraints() {
        return constraints;
    }

    public void addCommand(Command command) {
        commands.addElement(command);
    }

    public void setCommandListener(CommandListener textComponent) {
        commandListener = textComponent;
    }

    
    public Screen open() {
        
        final PopupScreen popup = new PopupScreen(new VerticalFieldManager(),PopupScreen.DEFAULT_CLOSE);
        
        final TextField editField;
        
        int input = (constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK);
        long style;
        
        switch (input) {
            case javax.microedition.lcdui.TextField.DECIMAL: style = BasicEditField.FILTER_REAL_NUMERIC; break;
            case javax.microedition.lcdui.TextField.NUMERIC: style = BasicEditField.FILTER_INTEGER; break;
            case javax.microedition.lcdui.TextField.PHONENUMBER: style = BasicEditField.FILTER_PHONE; break;
            case javax.microedition.lcdui.TextField.EMAILADDR: style = BasicEditField.FILTER_EMAIL; break;
            case javax.microedition.lcdui.TextField.URL: style = BasicEditField.FILTER_URL; break;
            //case javax.microedition.lcdui.TextField.ANY: // fall though to default
            default: style = BasicEditField.FILTER_DEFAULT; break;
        }
        
        if (style == BasicEditField.FILTER_EMAIL) {
            editField = new EmailAddressEditField("","");
        }
        else if ((javax.microedition.lcdui.TextField.PASSWORD & constraints) != 0) {
            editField = new PasswordEditField();
        }
        else {
            editField = new EditField(style);
        }
        
        editField.setText(text);
        editField.setMaxSize(maxSize);
        
        popup.add(new LabelField(title));
        popup.add(editField);
        
        HorizontalFieldManager commandsPanel = new HorizontalFieldManager(Field.FIELD_HCENTER);
        
        for (int c=0;c<commands.size();c++) {
            
            final Command command = (Command)commands.elementAt(c);
            
            ButtonField button = new ButtonField( command.getLabel() );
            button.setChangeListener(new FieldChangeListener() {
                public void fieldChanged(Field field, int context) {
                    
                    // set text back from text field
                    text = editField.getText();
                    
                    if (commandListener!=null) {
                        commandListener.commandAction(command, null);
                    }
                }
            });
            
            commandsPanel.add(button);
        }
        
        popup.add(commandsPanel);
        
        return popup;
        
    }
    
}

package net.yura.blackberry.rim;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.XYEdges;
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
import net.rim.device.api.ui.decor.BorderFactory;
import net.yura.blackberry.rim.Canvas.CanvasManager;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.plaf.Style;

public class TextBox {

    public static Class inputHelperClass = TextBox.TextBoxDialog.class;
    
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
    
    public interface InputHelper {
        public void start(TextBox tb,MIDlet midlet);
        public void stop();
        public void onDraw();
        public boolean back();
    }
    
    //public void open(MIDlet midlet) {
    //    InputHelper helper = new TextBoxDialog();
    //    helper.start(this,midlet);
    //}
    
    public static class TextBoxDialog implements InputHelper {

        public void start(final TextBox tb,MIDlet midlet) {
            
            
            final PopupScreen popup = new PopupScreen(new VerticalFieldManager(),PopupScreen.DEFAULT_CLOSE);
            
            final TextField editField = getTextFiled(tb);
            
            popup.add(new LabelField(tb.title));
            popup.add(editField);
            
            HorizontalFieldManager commandsPanel = new HorizontalFieldManager(Field.FIELD_HCENTER);
            
            for (int c=0;c<tb.commands.size();c++) {
                
                final Command command = (Command)tb.commands.elementAt(c);
                
                ButtonField button = new ButtonField( command.getLabel() );
                button.setChangeListener(new FieldChangeListener() {
                    public void fieldChanged(Field field, int context) {
                        
                        // set text back from text field
                        tb.text = editField.getText();
                        
                        if (tb.commandListener!=null) {
                            tb.commandListener.commandAction(command, null);
                        }
                    }
                });
                
                commandsPanel.add(button);
            }
            
            popup.add(commandsPanel);
            
            midlet.pushScreen(popup);
            
            
        }

        public void stop() {
            // hiding this screen is handled in "Display.setCurrent(desktoppane)"
        }

        public boolean back() {
            return false; // will never come here, as we opena whole new screen
        }

        public void onDraw() {
        }
    }
    
    public static class TextBoxKeyboard implements InputHelper {

        Canvas screen;
        TextBox textBox;
        
        public void start(TextBox tb, MIDlet midlet) {
            
            textBox = tb;
            
            screen = (Canvas)Display.getDisplay(midlet).getCurrent();

            // there does not seem to be a way to tell it what type of keyboard to open
            // the hack to get round this is to create a EditField and focus it, then open the keyboard, then hide the EditField
            net.rim.device.api.ui.VirtualKeyboard keyboard  = screen.getVirtualKeyboard();
            keyboard.setVisibility( net.rim.device.api.ui.VirtualKeyboard.SHOW );
            
        }

        public void stop() {
            net.rim.device.api.ui.VirtualKeyboard keyboard  = screen.getVirtualKeyboard();
            keyboard.setVisibility( net.rim.device.api.ui.VirtualKeyboard.HIDE );
        }
        
        public boolean back() {
            
            net.rim.device.api.ui.VirtualKeyboard keyboard  = screen.getVirtualKeyboard();
            boolean result = keyboard.getVisibility()==net.rim.device.api.ui.VirtualKeyboard.SHOW;

            if (textBox.commandListener!=null) {
                for (int c=0;c<textBox.commands.size();c++) {
                    Command command = (Command)textBox.commands.elementAt(c);
                    if (command.getCommandType()==Command.CANCEL) {
                        textBox.commandListener.commandAction(command, null); // will in turn call "setCurrent(desktoppane)" that will call "setInputHelper(null)" that will call "stop()"
                        break;
                    }
                }
            }

            return result; // we want to consume the event if the keyboard was already open
        }

        public void onDraw() {
        }
    }

    public static class TextBoxNative implements InputHelper,ChangeListener {
    
        public static final ChangeListener starter = new ChangeListener() {
            public void changeEvent(Component source, int type) {
                if (type == net.yura.mobile.gui.components.Component.FOCUS_GAINED) {
                    ((TextComponent)source).openNativeEditor();
                }
            }
        };
        
        public static void init() {
            inputHelperClass = TextBoxNative.class;
            TextComponent.staticFocusListener = starter;
        }
        
        Canvas screen;
        TextField editField;
        net.yura.mobile.gui.components.Component textField;
        TextBox textBox;
        
        public void start(final TextBox tb, MIDlet midlet) {
            
            textBox = tb;
            
            screen = (Canvas)Display.getDisplay(midlet).getCurrent();
            
            editField = getTextFiled(tb);
            
            final net.yura.mobile.gui.components.Window window = net.yura.mobile.gui.DesktopPane.getDesktopPane().getSelectedFrame();
            textField = window.getFocusOwner();
    
            editField.setFont( ((TextComponent)textField).getFont().getFont().font );
            
            editField.setCursorPosition( ((TextComponent)textField).getCaretPosition() );
            
            Border insets = textField.getInsets();
            editField.setBorder( BorderFactory.createSimpleBorder( new XYEdges(insets.getTop(),insets.getRight(),insets.getBottom(),insets.getLeft()), net.rim.device.api.ui.decor.Border.STYLE_TRANSPARENT ) );
            
            textField.setForeground( 0x00FFFFFF ); // stops the swingme component from drawing anything
            
            screen.add(editField);

            onDraw();
            
            TextComponent.staticFocusListener = this;
            
            editField.setFocus();
        }

        public void changeEvent(Component source, int num) {
            if (num==Component.FOCUS_LOST) {
                
                TextComponent.staticFocusListener = starter;
                
                // bb2midp
                textBox.text = editField.getText();
                
                // midp2swingme
                ((TextComponent)textField).setText( textBox.getString() );
                
                ((TextComponent)textField).setCaretPosition( editField.getCursorPosition() );
                
                textField.setForeground(Style.NO_COLOR);
                
                screen.delete(editField);
            }
        }
        
        public void onDraw() {
            
            Border insets = textField.getInsets();
            
            int x = textField.getXOnScreen()-insets.getLeft();
            int y = textField.getYOnScreen()-insets.getTop();
            int w = textField.getWidthWithBorder();
            int h = textField.getHeightWithBorder();
            
            CanvasManager man = (CanvasManager)screen.getDelegate();
            
            if (x!=editField.getLeft() || y!= editField.getTop()) {
                man.setPositionChild2(editField, x, y);
            }
            if (w!=editField.getWidth() || h!= editField.getHeight()) {
                man.layoutChild2(editField, w, h);
            }

        }

        public void stop() {
        }
        
        public boolean back() {
            net.rim.device.api.ui.VirtualKeyboard keyboard  = screen.getVirtualKeyboard();
            if (keyboard.getVisibility()==net.rim.device.api.ui.VirtualKeyboard.SHOW) {
                keyboard.setVisibility( net.rim.device.api.ui.VirtualKeyboard.HIDE );
                return true;
            }
            return false;
        }
    }
    
    private static TextField getTextFiled(TextBox tb) {
        
        final TextField editField;
        
        int input = (tb.constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK);
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
        else if ((javax.microedition.lcdui.TextField.PASSWORD & tb.constraints) != 0) {
            editField = new PasswordEditField();
        }
        else {
            editField = new EditField(style);
        }
        
        editField.setText(tb.text);
        editField.setMaxSize(tb.maxSize);
        
        return editField;
    }

    
}

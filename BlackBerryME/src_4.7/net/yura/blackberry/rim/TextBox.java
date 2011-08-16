package net.yura.blackberry.rim;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

public class TextBox {

    String title;
    String text;
    int maxSize;
    int constraints;
    
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
        
    }

    public void setCommandListener(CommandListener textComponent) {
        
    }

}

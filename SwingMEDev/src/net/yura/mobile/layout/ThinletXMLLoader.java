package net.yura.mobile.layout;

import java.io.InputStream;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.io.UTF8InputStreamReader;
import net.yura.mobile.io.XMLUtil;
import net.yura.mobile.util.Option;
import org.kxml2.io.KXmlParser;

/**
 * @author Yura Mamyrin
 */
public class ThinletXMLLoader extends XMLUtil {

    public static Panel load(InputStream is, ActionListener listener) throws Exception {

        ThinletXMLLoader loader = new ThinletXMLLoader();
        UIObject obj = (UIObject)loader.load(new UTF8InputStreamReader(is));

        return (Panel)obj.component;
    }

    public Object readObject(KXmlParser parser) throws Exception {

        String name = parser.getName();

        if (name.equals("panel")) {
            Panel panel = new Panel(new FlowLayout());

            // TODO add spacific stuff for panel

            return readUIObject(parser, panel);
        }
        else if (name.equals("button")) {
            Button button = new Button();

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    button.setText(value);
                }
            }

            return readUIObject(parser, button);
        }
        else if (name.equals("checkbox")) {
            CheckBox checkbox = new CheckBox();

            // TODO add spacific stuff for checkbox

            return readUIObject(parser, checkbox);
        }
        else if (name.equals("textfield")) {
            TextField textfield = new TextField();

            // TODO add spacific stuff for checkbox

            return readUIObject(parser, textfield);
        }
        else if (name.equals("label")) {
            Label label = new Label();

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    label.setText(value);
                }
            }

            return readUIObject(parser, label);
        }

        // TODO add more components

        else if (name.equals("choice") || name.equals("item") ) {
            return readOption(parser);
        }
        else {
            return readObject(parser);
        }

    }

    public Option readOption(KXmlParser parser) throws Exception {

        String text=null;
        String name = null;
        String tooltip = null;
        Icon icon = null;

        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("text".equals(key)) {
                text = value;
            }
            else if ("name".equals(key)) {
                name = value;
            }
            else if ("tooltip".equals(key)) {
                tooltip = value;
            }
            else if ("icon".equals(key)) {
                icon = new Icon(value);
            }
        }

        Option op = new Option(name, text, icon, tooltip);

        return op;
    }

    public UIObject readUIObject(KXmlParser parser,Component comp) throws Exception {


        UIObject uiobject = new UIObject();
        uiobject.component = comp;

        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("weightx".equals(key)) {
                uiobject.weightx = Integer.parseInt(value);
            }
            else if ("weighty".equals(key)) {
                uiobject.weighty = Integer.parseInt(value);
            }

            // TODO load other things into uiobject

        }

        while (parser.nextTag() != KXmlParser.END_TAG) {

            Object obj = readObject(parser);

            if (uiobject.component instanceof Panel) {
                ((Panel)uiobject.component).add(((UIObject)obj).component, obj);
            }

            // TODO add adding to other components, like tabbedpane

            else if (uiobject.component instanceof ComboBox) {
                ((ComboBox)uiobject.component).getItems().addElement(obj);
            }
            else if (uiobject.component instanceof List) {
                ((List)uiobject.component).getItems().addElement(obj);
            }
        }

        return uiobject;

    }


    public class UIObject {
        int weightx;
        int weighty;
        Component component;
    }

}

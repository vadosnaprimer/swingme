package net.yura.mobile.layout;

import java.io.InputStream;
import java.io.Reader;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.io.UTF8InputStreamReader;
import net.yura.mobile.util.Option;
import org.kxml2.io.KXmlParser;

/**
 * @author Yura Mamyrin
 */
public class XULLoader {

    public static Panel load(InputStream is, ActionListener listener) throws Exception {

        XULLoader loader = new XULLoader();
        GridBagConstraints obj = (GridBagConstraints)loader.load(new UTF8InputStreamReader(is));

        return (Panel)obj.component;
    }

    public Object load(Reader reader) throws Exception {

        //if (parser==null) {
            KXmlParser parser = new KXmlParser();
        //}

        parser.setInput(reader);
        parser.nextTag();

        return readObject(parser);
    }

    public Object readObject(KXmlParser parser) throws Exception {

        String name = parser.getName();

        if (name.equals("panel")) {
            int columns = 0;
            int gap = 0;
            int left = 0;
            int right = 0;
            int top = 0;
            int bottom = 0;
            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("columns".equals(key)) {
                    columns = Integer.parseInt(value);
                }
                if ("gap".equals(key)) {
                    gap = Integer.parseInt(value);
                }
                if ("top".equals(key)) {
                    top = Integer.parseInt(value);
                }
                if ("bottom".equals(key)) {
                    bottom = Integer.parseInt(value);
                }
                if ("left".equals(key)) {
                    left = Integer.parseInt(value);
                }
                if ("right".equals(key)) {
                    right = Integer.parseInt(value);
                }
            }
            Panel panel = new Panel(new GridBagLayout(columns,gap,top,bottom,left,right));

            // TODO add spacific stuff for panel

            return readUIObject(parser, panel);
        }
        else if (name.equals("button")) {
            Button button = new Button();

            readLabel(parser,button);

            return readUIObject(parser, button);
        }
        else if (name.equals("checkbox")) {
            CheckBox checkbox = new CheckBox();

            readLabel(parser,checkbox);
            // TODO add spacific stuff for checkbox

            return readUIObject(parser, checkbox);
        }
        else if (name.equals("textfield")) {
            TextField textfield = new TextField();
            //textfield.setPreferredWidth(0);

            // TODO add spacific stuff for checkbox

            return readUIObject(parser, textfield);
        }
        else if (name.equals("textarea")) {
            TextArea textfield = new TextArea();

            // TODO add spacific stuff for checkbox

            return readUIObject(parser, textfield);
        }
        else if (name.equals("label")) {
            Label label = new Label();

            readLabel(parser,label);

            return readUIObject(parser, label);
        }

        // TODO add more components

        else if (name.equals("choice") || name.equals("item") ) {
            return readOption(parser);
        }
        else {
            //#debug
            System.out.println("unknown object found: "+name);
            //return super.readObject(parser);
            return null;
        }

    }

    private void readLabel(KXmlParser parser, Label label) {

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    label.setText(value);
                }
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

    public GridBagConstraints readUIObject(KXmlParser parser,Component comp) throws Exception {


        GridBagConstraints uiobject = new GridBagConstraints();
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
            else if ("colspan".equals(key)) {
                System.out.println("Setting colspan to "+Integer.parseInt(value));
                uiobject.colSpan = Integer.parseInt(value);
            }
            else if ("rowspan".equals(key)) {
                uiobject.rowSpan = Integer.parseInt(value);
            }
            else if ("columns".equals(key)) {
                uiobject.column = Integer.parseInt(value);
            }
            else if ("valign".equals(key)) {
                uiobject.valign = value;
            }
            else if ("halign".equals(key)) {
                uiobject.halign = value;
            }

        }

        while (parser.nextTag() != KXmlParser.END_TAG) {

            Object obj = readObject(parser);

            if (uiobject.component instanceof Panel) {
                ((Panel)uiobject.component).add(((GridBagConstraints)obj).component, obj);
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

}

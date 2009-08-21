package net.yura.mobile.gui.layout;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ProgressBar;
import net.yura.mobile.gui.components.Spinner;
import net.yura.mobile.gui.components.TabbedPane;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.io.UTF8InputStreamReader;
import net.yura.mobile.util.Option;
import org.kxml2.io.KXmlParser;

/**
 * @author Yura Mamyrin
 */
public class XULLoader {

    public static final int VK_F1 = 112;
    public static final int VK_F2 = 113;

    public static XULLoader load(InputStream is, ActionListener listener) throws Exception {

        XULLoader loader = new XULLoader();

        loader.load(new UTF8InputStreamReader(is),listener);

        return loader;
    }

    private Hashtable components = new Hashtable();
    private Component root;

    public void load(Reader reader,ActionListener listener) throws Exception {

        //if (parser==null) {
            KXmlParser parser = new KXmlParser();
        //}

        parser.setInput(reader);
        parser.nextTag();

        GridBagConstraints rt = (GridBagConstraints)readObject(parser,listener);

        root = rt.component;
    }

    public Component find(String name) {
        return (Component)components.get(name);
    }
    public Component getRoot() {
        return root;
    }
    public Hashtable getFormData() {
        Hashtable data = new Hashtable();

        Enumeration enu = components.keys();
        while (enu.hasMoreElements()) {
            String name = (String)enu.nextElement();
            Component comp = (Component)components.get(name);

            if (comp instanceof TextComponent) {
                data.put(name, ((TextComponent)comp).getText() );
            }
        }

        return data;
    }

    public Object readObject(KXmlParser parser,ActionListener listener) throws Exception {

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

            return readUIObject(parser, panel,listener);
        }
        else if (name.equals("tabbedpane")) {
            TabbedPane tabbedpane = new TabbedPane();

            return readUIObject(parser, tabbedpane,listener);
        }
        else if (name.equals("tab")) {
            Panel tab = new Panel();

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    tab.setName(value);
                }
            }

            return readUIObject(parser, tab,listener);
        }
        else if (name.equals("progressbar")) {
            ProgressBar progress = new ProgressBar();

            return readUIObject(parser, progress,listener);
        }
        else if (name.equals("slider")) {
            Label slider = new Label("slider");

            return readUIObject(parser, slider,listener);
        }
        else if (name.equals("spinbox")) {
            Spinner spinner = new Spinner();

            return readUIObject(parser, spinner,listener);
        }
        else if (name.equals("datespinbox")) {
            Spinner spinner = new Spinner();

            return readUIObject(parser, spinner,listener);
        }
        else if (name.equals("button")) {
            Button button = new Button();

            readButton(parser,button,listener);

            return readUIObject(parser, button,listener);
        }
        else if (name.equals("checkbox")) {
            CheckBox checkbox = new CheckBox();

            readButton(parser,checkbox,listener);
            // TODO add spacific stuff for checkbox

            return readUIObject(parser, checkbox,listener);
        }
        else if (name.equals("combobox")) {
            ComboBox combobox = new ComboBox(new Vector());

            readButton(parser,combobox,listener);
            // TODO add spacific stuff for checkbox

            return readUIObject(parser, combobox,listener);
        }
        else if (name.equals("list")) {
            List list = new List();

            return readUIObject(parser, list,listener);
        }
        else if (name.equals("textfield")) {
            TextField textfield = new TextField();
            //textfield.setPreferredWidth(0);

            // TODO add spacific stuff for checkbox

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("passwordfield")) {
            TextField textfield = new TextField(TextField.PASSWORD);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("numericfield")) {
            TextField textfield = new TextField(TextField.NUMERIC);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("textarea")) {
            TextArea textfield = new TextArea();

            // TODO add spacific stuff for checkbox

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("label")) {
            Label label = new Label();

            readLabel(parser,label);

            return readUIObject(parser, label,listener);
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

    private void readButton(KXmlParser parser, Button button,ActionListener listener) {

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("action".equals(key)) {
                    button.setActionCommand(value);
                }
                else if ("mnemonic".equals(key)) {
                    int mnemonic = Integer.parseInt(value);
                    switch (mnemonic) {
                        case VK_F1: mnemonic=KeyEvent.KEY_SOFTKEY1; break;
                        case VK_F2: mnemonic=KeyEvent.KEY_SOFTKEY2; break;
                    }
                    button.setMnemonic(mnemonic);
                }
                else if ("type".equals(key)) {
                    if ("default".equals(value)) {
                        button.setMnemonic(KeyEvent.KEY_SOFTKEY1);
                    }
                    else if ("cancel".equals(value)) {
                        button.setMnemonic(KeyEvent.KEY_SOFTKEY2);
                    }
                    else if ("link".equals(value)) {
                        button.setName("Link");
                    }
                }
            }

            button.addActionListener(listener);
            readLabel(parser, button);
    }

    private void readLabel(KXmlParser parser, Label label) {

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    label.setText(value);
                }
                else if ("icon".equals(key)) {
                    try {
                        label.setIcon( new Icon(value) );
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else if ("alignment".equals(key)) {
                    if ("center".equals(value)) { // default for button
                        label.setHorizontalAlignment(Graphics.HCENTER);
                    }
                    else if ("right".equals(value)) {
                        label.setHorizontalAlignment(Graphics.RIGHT);
                    }
                    else if ("left".equals(value)) { // default for label
                        label.setHorizontalAlignment(Graphics.LEFT);
                    }
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
                try {
                    icon = new Icon(value);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        Option op = new Option(name, text, icon, tooltip);

        parser.skipSubTree();

        return op;
    }

    public GridBagConstraints readUIObject(KXmlParser parser,Component comp,ActionListener listener) throws Exception {


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
//                System.out.println("Setting colspan to "+Integer.parseInt(value));
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
            else if ("background".equals(key)) {
                comp.setBackground( Integer.parseInt(value.substring(1),16) );
            }
            else if ("foreground".equals(key)) {
                comp.setForeground( Integer.parseInt(value.substring(1),16) );
            }
            else if ("height".equals(key)) {
                comp.setPreferredSize(comp.getPreferredWidth(), Integer.parseInt(value));
            }
            else if ("width".equals(key)) {
                comp.setPreferredSize( Integer.parseInt(value),comp.getPreferredHeight());
            }
            else if ("name".equals(key)) {
                components.put(value, comp);
            }
            else if ("tooltip".equals(key)) {
                comp.setToolTipText(value);
            }

        }

        while (parser.nextTag() != KXmlParser.END_TAG) {

            Object obj = readObject(parser,listener);

            if (uiobject.component instanceof TabbedPane) {
                ((TabbedPane)uiobject.component).add( ((GridBagConstraints)obj).component );
            }
            else if (uiobject.component instanceof Panel) {
                ((Panel)uiobject.component).add(((GridBagConstraints)obj).component, obj);
            }
            else if (uiobject.component instanceof ComboBox) {
                ((ComboBox)uiobject.component).getItems().addElement(obj);
            }
            else if (uiobject.component instanceof List) {
                ((List)uiobject.component).getItems().addElement(obj);
            }
            else {
                //#debug
                System.out.println("what to do with this object: "+obj.getClass() +" "+obj+" parent="+uiobject.component);
            }
        }

        return uiobject;

    }

}

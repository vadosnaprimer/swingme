package net.yura.mobile.gui.layout;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.border.TitledBorder;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ProgressBar;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.Spinner;
import net.yura.mobile.gui.components.TabbedPane;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.io.UTF8InputStreamReader;
import net.yura.mobile.util.Option;
import net.yura.mobile.util.StringUtil;
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
    private Hashtable groups = new Hashtable();
    private Component root;

    public void swapComponent(String name,Component comp) {
        Component old = find(name);

        Panel p = old.getParent();
        GridBagConstraints constr = (GridBagConstraints)p.getConstraints().get(old);

        int index = p.getComponents().indexOf(old);
        p.remove(old);
        p.insert(comp, constr, index);

        comp.setName( old.getName() );

        components.put(name, comp);
    }

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
            else if (comp instanceof CheckBox) {
                data.put(name, String.valueOf( ((CheckBox)comp).isSelected() ) );
            }
            else if (comp instanceof ComboBox) {
                Object object= ((ComboBox)comp).getSelectedItem();
                data.put(name, ( null == object )?"null":object );
            }
        }

        return data;
    }

    public Object readObject(KXmlParser parser,ActionListener listener) throws Exception {

        String name = parser.getName();

        if (name.equals("panel")) {

            String border = null;
            String text = null;
            boolean scrollable = false;
            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {

                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("border".equals(key)) {
                    border = value;
                }
                else if ("text".equals(key)) {
                    text = value;
                }
                else if ("scrollable".equals(key)) {
                    scrollable = "true".equalsIgnoreCase(value);
                }
            }

            GridBagLayout layout = readLayout(parser);

            Panel panel = scrollable?new ScrollPane( new Panel(layout) ):new Panel(layout);

            Border border2=null;
            if (border!=null) {
                border2 = new LineBorder();
            }
            if (text!=null) {
                border2 = new TitledBorder(border2, text, new Label().getFont());
            }
            panel.setBorder(border2);

            return readUIObject(parser, panel,listener);
        }
        else if (name.equals("dialog")) {

            Frame frame = new Frame();
            frame.setClosable(false);
            frame.setMaximizable(false);

            GridBagLayout layout = readLayout(parser);
            frame.getContentPane().setLayout(layout);

            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {

                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("icon".equals(key)) {
                    frame.setIconImage( loadIcon(value) );
                }
                else if ("text".equals(key)) {
                    frame.setTitle( value );
                }
                else if ("scrollable".equals(key)) {
                    Panel p = frame.getContentPane();
                    frame.setContentPane( new ScrollPane(p) );
                }
                else if ("closable".equals(key)) {
                    frame.setClosable("true".equalsIgnoreCase(value));
                }
                else if ("maximizable".equals(key)) {
                    frame.setMaximizable("true".equalsIgnoreCase(value));
                }
//                else if ("iconifiable".equals(key)) {
//                    frame.setIconifiable("true".equalsIgnoreCase(value));
//                }
//                else if ("resizable".equals(key)) {
//                    frame.setResizable("true".equalsIgnoreCase(value));
//                }
            }

            return readUIObject(parser, frame,listener);
        }
        else if (name.equals("tabbedpane")) {
            TabbedPane tabbedpane = new TabbedPane();

            String pvalue = parser.getAttributeValue(null, "placement");
            int placement = Graphics.TOP; // default
            if ("left".equals(pvalue)) {
                placement = Graphics.LEFT;
            }
            else if ("bottom".equals(pvalue)) {
                placement = Graphics.BOTTOM;
            }
            else if ("right".equals(pvalue)) {
                placement = Graphics.RIGHT;
            }
//            else if ("stacked".equals(value)) {
//                placement = ???;
//            }
            tabbedpane.setTabPlacement(placement);

            return readUIObject(parser, tabbedpane,listener);
        }
        else if (name.equals("tab")) {
            Tab tab = new Tab();
            readOption(parser,tab);
            while (parser.nextTag() != KXmlParser.END_TAG) {
                Object obj = readObject(parser,listener);
                if (tab.component == null) {
                    tab.component = ((GridBagConstraints)obj).component;
                }
                else {
                    //#debug
                    System.out.println("ignored item in tab: "+obj);
                }
            }
            return tab;
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
        else if (name.equals("button") || "menuitem".equals(name)) {
            Button button = new Button();

            readButton(parser,button,listener);

            return readUIObject(parser, button,listener);
        }
        else if (name.equals("checkbox") || "checkboxmenuitem".equals(name)) {

            Button checkbox;
            boolean selected = false;
            String group = null;
            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("group".equals(key)) {
                    group = value;
                }
                else if ("selected".equals(key)) {
                    selected = "true".equalsIgnoreCase(value);
                }
            }
            if (group == null) {
                checkbox = new CheckBox();
            }
            else {
                checkbox = new RadioButton();
                ButtonGroup g = (ButtonGroup)groups.get(group);
                if (g==null) {
                    g = new ButtonGroup();
                    groups.put(group, g);
                }
                g.add(checkbox);
            }
            checkbox.setSelected(selected);
            readButton(parser,checkbox,listener);

            return readUIObject(parser, checkbox,listener);
        }
        else if (name.equals("combobox")) {
            ComboBox combobox = new ComboBox(new Vector());

            readButton(parser,combobox,listener);
            // TODO add spacific stuff for checkbox

            return readUIObject(parser, combobox,listener);
        }
        else if (name.equals("list")) {
            final List list = new List();

            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {

                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("action".equals(key)) {
                    list.setActionCommand(value);
                }
            }

            return readUIObject(parser, list,listener);
        }
        else if (name.equals("textfield")) {
            TextField textfield = new TextField();
            //textfield.setPreferredWidth(0);

            readTextComponent(parser,textfield);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("passwordfield")) {
            TextField textfield = new TextField(TextField.PASSWORD);

            readTextComponent(parser,textfield);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("numericfield")) {
            TextField textfield = new TextField(TextField.NUMERIC);

            readTextComponent(parser,textfield);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("textarea")) {
            TextArea textarea = new TextArea();

            readTextComponent(parser,textarea);

            return readUIObject(parser, textarea,listener);
        }
        else if (name.equals("label")) {
            Label label = new Label();

            readLabel(parser,label);

            return readUIObject(parser, label,listener);
        }
        else if (name.equals("menubar")) {
            MenuBar menubar = new MenuBar();

            return readUIObject(parser, menubar,listener);
        }
        else if (name.equals("menu")) {
            Menu menu = new Menu();

            readButton(parser,menu,listener);

            return readUIObject(parser, menu,listener);
        }
        // TODO add more components
        else if (name.equals("choice") || name.equals("item") ) {
            Option op = new Option();
            readOption(parser,op);
            parser.skipSubTree();
            return op;
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
                    button.addActionListener(listener);
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

            readLabel(parser, button);
    }

    private void readLabel(KXmlParser parser, Label label) {

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    if (!(label instanceof ComboBox)) {
                        label.setText(value);
                    }
                }
                else if ("icon".equals(key)) {
                    label.setIcon( loadIcon(value) );
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

    public Icon loadIcon(String value) {
        try {
            return new Icon(value);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private GridBagLayout readLayout(KXmlParser parser) {

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
                else if ("gap".equals(key)) {
                    gap = Integer.parseInt(value);
                }
                else if ("top".equals(key)) {
                    top = Integer.parseInt(value);
                }
                else if ("bottom".equals(key)) {
                    bottom = Integer.parseInt(value);
                }
                else if ("left".equals(key)) {
                    left = Integer.parseInt(value);
                }
                else if ("right".equals(key)) {
                    right = Integer.parseInt(value);
                }

            }

            return new GridBagLayout(columns,gap,top,bottom,left,right);

    }

    private void readTextComponent(KXmlParser parser, TextComponent text) {
            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    text.setText(value);
                }
            }
    }

    private void readOption(KXmlParser parser, Option op) {
        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("text".equals(key)) {
                op.setValue(value);
            }
            else if ("name".equals(key)) {
                op.setKey(value);
            }
            else if ("tooltip".equals(key)) {
                op.setTip(value);
            }
            else if ("icon".equals(key)) {
                op.setIcon( loadIcon(value) );
            }
        }
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
            else if("property".equals(key)) {
                String[] properties = StringUtil.split(value, ',');
                if( 0 < properties.length ) {
                    for( int x=0; x<properties.length; x++ ) {
                        String[] property = StringUtil.split(properties[x], '=');
                        if( 2 == property.length ) {
                            if( "plafname".equals(property[0]) ) {
                                comp.setName( property[1] );
                            }
                            else if( "constraint".equals( property[0] ) ) {
                                if( comp instanceof TextField ) {
                                    ((TextField)comp).setConstraints( Integer.parseInt(property[1]) );
                                }
                            }
                            else if( "maxsize".equals( property[0] ) ) {
                                if( comp instanceof TextField ) {
                                    ((TextField)comp).setMaxSize( Integer.parseInt(property[1]) );
                                }
                            }
                            //#mdebug
                            else {
                                System.out.println( "XULLoader.GridBagConstraints() - property key does not exist: " + property[0]);
                            }
                            //#enddebug
                       }
                       //#mdebug
                       else {
                            System.out.println("property does not have a key and value");
                       }
                       //#enddebug
                    }
                }
            }

        }

        while (parser.nextTag() != KXmlParser.END_TAG) {

            Object obj = readObject(parser,listener);

            if (comp instanceof TabbedPane) {
                Tab tab = (Tab)obj;
                ((TabbedPane)comp).addTab(tab.getValue(), tab.getIcon(), tab.component, tab.getToolTip());
            }
            else if (comp instanceof Frame) {
                Panel panel = ((Frame)comp).getContentPane();
                if (panel instanceof ScrollPane) { panel = (Panel)((ScrollPane)panel).getComponent(); }

                Component component = ((GridBagConstraints)obj).component;
                if (panel.getComponentCount() == 0 && component instanceof MenuBar) {
                    ((Frame)comp).setMenuBar( (MenuBar)component );
                }
                else {
                    panel.add(component, obj);
                }
            }
            else if (comp instanceof ScrollPane) {
                ((Panel)((ScrollPane)comp).getComponent()).add(((GridBagConstraints)obj).component, obj);
            }
            else if (comp instanceof Panel) {
                ((Panel)comp).add(((GridBagConstraints)obj).component, obj);
            }
            else if (comp instanceof ComboBox) {
                ((ComboBox)comp).getItems().addElement(obj);
            }
            else if (comp instanceof MenuBar) {
                ((MenuBar)comp).add( (Button) ((GridBagConstraints)obj).component );
            }
            else if (comp instanceof Menu) {
                ((Menu)comp).add( ((GridBagConstraints)obj).component );
            }
            else if (comp instanceof List) {
                ((List)comp).getItems().addElement(obj);
            }
            else {
                //#debug
                System.out.println("what to do with this object: "+obj.getClass() +" "+obj+" parent="+uiobject.component);
            }
        }

        return uiobject;

    }

    class Tab extends Option {
        private Component component;
    }

}

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
import net.yura.mobile.gui.components.Table;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.TextPane;
import net.yura.mobile.io.UTF8InputStreamReader;
import net.yura.mobile.util.Option;
import net.yura.mobile.util.Properties;
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

    public static XULLoader load(InputStream is, ActionListener listener, Properties properties) throws Exception {

        XULLoader loader = new XULLoader();

        loader.load(new UTF8InputStreamReader(is),listener,properties);

        return loader;
    }

    private Hashtable components = new Hashtable();
    private Hashtable groups = new Hashtable();
    private Component root;
    private Properties i18nProperties;
    
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

    public void load(Reader reader,ActionListener listener,Properties properties) throws Exception {
        this.i18nProperties = properties;
        load(reader,listener);
    }

    /**
     * read this:<a href="http://thinlet.sourceforge.net/properties.html">Thinlet Properties</a>
     */
    public static Hashtable getProperties(String value) {
        String[] cProperties = StringUtil.split(value, ';');
        Hashtable properties = new Hashtable(cProperties.length);
        if( 0 < cProperties.length ) {
            for( int x=0; x<cProperties.length; x++ ) {
                String[] property = StringUtil.split(cProperties[x], '=');
                if( 2 == property.length ) {
                    properties.put(property[0], property[1]);
                }
                //#mdebug
                else {
                    System.err.println("property does not have a key and value");
                }
                //#enddebug
            }
        }
        return properties;
    }

    private String getPropertyText(String key,boolean i18n) {
        if (i18n) {
            if (i18nProperties != null) {
                String translated = i18nProperties.getProperty(key);
                if (translated != null) {
                    return translated;
                }
            }
            return "???"+key+"???";
        }
        return key;
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
            else if (comp instanceof ComboBox) {
                Object object= ((ComboBox)comp).getSelectedItem();
                String value = null;
                if (object instanceof String) {
                    value = (String) object;
                }
                else if (object instanceof Option) {
                    value = ((Option)object).getKey();
                }
                else {
                    value = String.valueOf( object );
                }
                data.put(name, value );
            }
            else if (comp instanceof Button) {
                data.put(name, String.valueOf( ((Button)comp).isSelected() ) );
            }
        }

        return data;
    }

    public void setFormData(Hashtable data) {
        Enumeration componentNames = data.keys();
        //Enumeration values = data.elements(); // can not do this
        while (componentNames.hasMoreElements()) {
            String componentName = (String) componentNames.nextElement();
            Object value = data.get( componentName );
            Component component = (Component) find(componentName);
            if (component != null) {
                if (component instanceof TextComponent) {
                    ((TextComponent) component).setText((String)value);
                }
                else if (component instanceof ComboBox) {
                    if (!"null".equals(value)) {
                        int index = -1;
                        ComboBox combo = (ComboBox) component;
                        Vector items = combo.getItems(); // TODO should use methods in modal
                        for (int i=0;i<items.size();i++) {
                            Object item = items.elementAt(i);
                            if (item instanceof String) {
                                if (((String)item).equals(value)) {
                                    index = i;
                                    break;
                                }
                            }
                            else if (item instanceof Option) {
                                if (((Option)item).getKey().equals(value)) {
                                    index = i;
                                    break;
                                }
                            }
                            // should NOT have components inside here!
                        }
                        if (index > -1) {
                            combo.setSelectedIndex(index);
                        }
                    }
                }
                else if (component instanceof Button) {
                    ((Button) component).setSelected("true".equals(value));
                }
            }
        }
    }

    public Object readObject(KXmlParser parser,ActionListener listener) throws Exception {

        String name = parser.getName();

        if (name.equals("panel")) {

            Panel panel = new Panel();

            panel = readPanel(parser,panel,listener);

            return readUIObject(parser, panel,listener);
        }
        else if (name.equals("dialog")) {

            Frame frame = new Frame();
            frame.setClosable(false);
            frame.setMaximizable(false);

            GridBagLayout layout = readLayout(parser);
            frame.getContentPane().setLayout(layout);

            String title = null;
            boolean i18n = false;

            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {

                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("icon".equals(key)) {
                    frame.setIconImage( loadIcon(value) );
                }
                else if ("text".equals(key)) {
                    title = value;
                }
                else if ("i18n".equals(key)) {
                    i18n = ("true".equals(value));
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

            if (title != null) {
                frame.setTitle( getPropertyText(title,i18n) );
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

            int selectedIndex = -1;

            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("selected".equals(key)) {
                    selectedIndex = Integer.parseInt(value);
                }
            }

            GridBagConstraints constraints = readUIObject(parser, combobox,listener);

            if (combobox.getItemCount() > selectedIndex && selectedIndex != -1) {
                combobox.setSelectedIndex(selectedIndex);
            }

            return constraints;
        }
        else if (name.equals("list")) {
            final List list = new List();

            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("action".equals(key)) {
                    list.setActionCommand(value);
                    list.addActionListener(listener);
                }
                else if("property".equals(key)) {
                    Hashtable properties = getProperties(value);
                    String fixedCellHeight = (String)properties.get("fixedCellHeight");
                    if (fixedCellHeight!=null) {
                        list.setFixedCellHeight( Integer.parseInt(fixedCellHeight) );
                    }
                }
            }

            return readUIObject(parser, list,listener);
        }
        else if (name.equals("textfield")) {
            TextField textfield = new TextField();

            readTextComponent(parser,textfield,listener);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("passwordfield")) {
            TextField textfield = new TextField(TextField.PASSWORD);

            readTextComponent(parser,textfield,listener);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("numericfield")) {
            TextField textfield = new TextField(TextField.NUMERIC);

            readTextComponent(parser,textfield,listener);

            return readUIObject(parser, textfield,listener);
        }
        else if (name.equals("textarea")) {

            Class theclass = TextArea.class;

            final int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if("property".equals(key)) {
                    Hashtable properties = getProperties(value);
                    String plafname = (String)properties.get("plafname");
                    if ("TextPane".equals(plafname)) {
                        theclass = TextPane.class;
                    }
                }

            }

            Component textarea = (Component)theclass.newInstance();
               
            readTextComponent(parser,textarea,listener);

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
        else if (name.equals("table")) {
            Table table = new Table();
            return readUIObject(parser, table,listener);
        }
        else { // if (name.equals("slider")) {
            //#debug
            System.out.println("unknown object found: "+name);

            Label slider = new Label("unknown item: "+name);
            return readUIObject(parser, slider,listener);
        }

    }

    protected Panel readPanel(KXmlParser parser, Panel panel, ActionListener listener) {

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

            panel.setLayout(layout);

            panel = scrollable?new ScrollPane( panel ):panel;

            Border border2=null;
            if (border!=null) {
                border2 = new LineBorder();
            }
            if (text!=null) {
                border2 = new TitledBorder(border2, text, new Label().getFont());
            }
            panel.setBorder(border2);

            return panel;
    }

    protected void readButton(KXmlParser parser, Button button,ActionListener listener) {

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

    public void readLabel(KXmlParser parser, Label label) {
            String labelText = null;
            boolean i18n = false;

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    if (!(label instanceof ComboBox)) {
                        labelText = value;
                    }
                }
                else if ("i18n".equals(key)) {
                    i18n = ("true".equals(value));
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

            if (labelText != null) {
                label.setText( getPropertyText(labelText,i18n) );
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

    protected void readTextComponent(KXmlParser parser, Component text,ActionListener listener) {
            String textLabel = null;
            boolean i18n = false;

            int count = parser.getAttributeCount();
            for (int c=0;c<count;c++) {
                String key = parser.getAttributeName(c);
                String value = parser.getAttributeValue(c);
                if ("text".equals(key)) {
                    textLabel = value;
                }
                else if ("i18n".equals(key)) {
                    i18n = ("true".equals(value));
                }
                else if("property".equals(key)) {
                    Hashtable properties = getProperties(value);
                    String constraint = (String)properties.get("constraint");
                    if (constraint!=null) {
                        if (text instanceof TextComponent) {
                            ((TextComponent)text).setConstraints( Integer.parseInt(constraint) );
                        }
                    }
                    String maxsize = (String)properties.get("maxsize");
                    if (maxsize!=null) {
                        if (text instanceof TextComponent) {
                            ((TextComponent)text).setMaxSize( Integer.parseInt(maxsize) );
                        }
                    }
                }
                else if ("editable".equals(key)) {
                    text.setFocusable( "true".equals(value) );
                }
                else if ("wrap".equals(key)) {
                    if (text instanceof TextArea) {
                        ((TextArea)text).setLineWrap( "true".equals(value) );
                    }
                }
                else if ("columns".equals(key)) { // TODO could be done better
                    if (text instanceof TextComponent) {
                        ((TextComponent)text).setPreferredSize( ((TextComponent)text).getFont().getWidth('W') * Integer.parseInt(value) , ((TextComponent)text).getPreferredHeight());
                    }
                }
                else if ("rows".equals(key)) {
                    if (text instanceof TextArea) {
                        ((TextArea)text).setPreferredSize( ((TextArea)text).getPreferredWidth(), ((TextArea)text).getFont().getHeight() * Integer.parseInt(value) );
                    }
                }

            }

            if (text instanceof TextPane) {
                ((TextPane)text).setActionListener(listener);
            }

            if (textLabel != null) {
                if (text instanceof TextComponent) {
                    ((TextComponent)text).setText( getPropertyText(textLabel,i18n) );
                }
                else if (text instanceof TextPane) { // TODO temp for now
                    ((TextPane)text).setText( getPropertyText(textLabel,i18n) );
                }
            }
    }

    private void readOption(KXmlParser parser, Option op) {
        String label = null;
        boolean i18n = false;

        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("text".equals(key)) {
                label = value;
            }
            else if ("i18n".equals(key)) {
                i18n = ("true".equals(value));
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

        if (label != null) {
            op.setValue( getPropertyText(label,i18n) );
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
            else if ("visible".equals(key)) {
                comp.setVisible( "true".equals(value) );
            }
            else if("property".equals(key)) {
                Hashtable properties = getProperties(value);
                String plafname = (String)properties.get("plafname");
                if (plafname!=null) {
                    comp.setName( plafname );
                }
            }
        }

        if (uiobject.weightx > 0 && uiobject.weighty > 0 && comp instanceof Frame) {
            ((Frame)comp).setMaximum(true);
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

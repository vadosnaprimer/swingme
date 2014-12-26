package net.yura.mobile.gui.layout;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.TextPane;
import net.yura.mobile.gui.components.TextPane.TextStyle;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.io.kxml2.KXmlParser;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Option;
import net.yura.mobile.util.StringUtil;

/**
 * @author Yura Mamyrin
 */
public class XHTMLLoader {

    Panel root;
    TagHandler currentTag;
    Component currentComponent;
    boolean newBlock;
    ActionListener al;

    public static Component load(String text, ActionListener listener) {
        byte[] bytes;
        try {
            bytes = text.getBytes("UTF-8"); // if we do not use UTF-8 here, the £ symbol gets converted to a ? on BB
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.toString());
        }
        XHTMLLoader xhtmlLoader = new XHTMLLoader();
        xhtmlLoader.gotResult( new ByteArrayInputStream( bytes ) ,listener);
        return xhtmlLoader.getRoot();
    }

    public void gotResult(InputStream resultsStream,ActionListener actionL) {

        try {

            al = actionL;
            root = new Panel(new FlowLayout(Graphics.VCENTER,0));
            currentComponent = root;

            KXmlParser parser = new KXmlParser();
            parser.setInput(resultsStream, null);

            startInlineSection();

            read(parser);

            endInlineSection();
        }
        catch(Exception ex) {
            Logger.warn("cant load " + resultsStream + " " + actionL, ex);
        }
    }

    public void gotResult(TextPane pane,String string) {

        currentComponent = pane;
        try {
            KXmlParser parser = new KXmlParser();
            parser.setInput(new ByteArrayInputStream( string.getBytes("UTF-8") ), null); // as we decode by default as UTF-8, we need to encode as UTF-8 too
            read(parser);
        }
        catch(Exception ex) {
            Logger.warn("cant set " + pane + " " + string, ex);
        }
    }

    /**
     * This method should only return a Synth Style or a preset, e.g. &lt;b&gt;
     * it should NOT create new styles based on the tag!!!
     *
     * do not default
     * @return style for this tag if found, otherwise null
     */
    private TextStyle getStyleForTag(String name) {

/* TODO
        final int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("class".equals(key)) {

            }
            else if ("id".equals(key)) {

            }
            else if ("style".equals(key)) {

            }
        }
*/
        Style skinStyle = DesktopPane.getDesktopPane().getLookAndFeel().getStyle(name);

        if (skinStyle!=null) {
            TextStyle textStyle = new TextStyle();
            textStyle.setName(name);
            textStyle.putAll(skinStyle);
            return textStyle;
        }

        return (TextStyle)htmlTextStyles.get(name);
    }

    protected void read(KXmlParser parser) throws Exception {

        int eventType = parser.getEventType();
        do {
            if(eventType == KXmlParser.START_TAG) {

                String name = parser.getName().toLowerCase();

                TagHandler tag = new TagHandler(name);

                tag.setParant(currentTag);
                tag.style = getStyleForTag(name);

                currentTag = tag;
                currentTag.processStartElement(parser);

                if (currentComponent instanceof TextPane) {
                    TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                    currentTag.styleStart = inlineText.getText().length();
                }

                if (isBlock( name ) ) {
                    newBlock = true;
                }

            }
            else if(eventType == KXmlParser.END_TAG) {

                if (currentComponent instanceof TextPane) {
                    if (currentTag.style!=null) {
                        TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                        int styleEnd = inlineText.getText().length();
                        if (currentTag.style.getAlignment() == -1) {
                            inlineText.setCharacterAttributes(currentTag.styleStart, styleEnd-currentTag.styleStart, currentTag.style);
                        }
                        else {
                            inlineText.setParagraphAttributes(currentTag.styleStart, styleEnd-currentTag.styleStart, currentTag.style);
                        }
                    }
                }

                if (isBlock( currentTag.name ) ) {
                    newBlock = true;
                }

                currentTag.processEndElement(parser);
                currentTag = currentTag.getParent();

            }
            else if(eventType == KXmlParser.TEXT) {
                currentTag.processText(parser);
            }
            else if(eventType == KXmlParser.ENTITY_REF) {
                currentTag.processRef(parser);
            }
            //#mdebug debug
            else if(eventType == KXmlParser.START_DOCUMENT) {
                Logger.debug("Start document");
            }
            else if(eventType == KXmlParser.END_DOCUMENT) {
                Logger.debug("End document");
            }
            else {
                Logger.debug("unknown event: "+eventType);
            }
            //#enddebug
            eventType = parser.nextToken();
        } while (eventType != KXmlParser.END_DOCUMENT);

    }

    public Component getRoot() {
        return root;
    }

    private final static Hashtable htmlTextStyles = new Hashtable();

    static {

        // css defaults
        TextStyle bold = new TextStyle();
        TextStyle italic = new TextStyle();
        TextStyle underline = new TextStyle();
        TextStyle center = new TextStyle();
        TextStyle link = new TextStyle();
        TextStyle font = new TextStyle();

        bold.setBold(true);
        bold.setName("b");

        italic.setItalic(true);
        italic.setName("i");

        underline.setUnderline(true);
        underline.setName("u");

        center.setAlignment( TextStyle.ALIGN_CENTER );
        center.setName("center");

        link.setUnderline(true);
        link.setForeground(0xFF0000FF);
        link.addForeground(0xFFFF0000, Style.FOCUSED);
        link.setName("a");

        font.setName("font");

        addHtmlTextStyle(bold);
        addHtmlTextStyle(italic);
        addHtmlTextStyle(underline);
        addHtmlTextStyle(center);
        addHtmlTextStyle(link);
        addHtmlTextStyle(font);

        //TextStyle h1 = new TextStyle();
        //h1.addFont(new Font(
        //        javax.microedition.lcdui.Font.FACE_SYSTEM,
        //        javax.microedition.lcdui.Font.STYLE_PLAIN,
        //        javax.microedition.lcdui.Font.SIZE_LARGE), Style.ALL);
        //h1.setName("h1");
        //h1.setForeground(0xFFFF0000);
        //addHtmlTextStyle(h1);
    }

    public boolean isBlock(String tag) {
        return "center".equals(tag) || "p".equals(tag) || "h1".equals(tag) || "h2".equals(tag) || "h3".equals(tag);
    }

    public static void addHtmlTextStyle(TextStyle style) {
        htmlTextStyles.put(style.getName(), style);
    }

    private void startInlineSection() {
        TextPane it = new TextPane();
        it.setActionListener(al);
        //TextArea it = new TextArea(); it.setLineWrap(true);
        //#mdebug debug
        if (DesktopPane.debug) {
            it.setBorder( new LineBorder(0xFFFF0000) );
        }
        //#enddebug
        ((Panel)currentComponent).add(it);
        currentComponent = it;
        newBlock = false;
    }
    private void endInlineSection() {
        // clear all formatting
        TextPane text = ((TextPane)currentComponent);
        //TextArea text = ((TextArea)currentComponent);
        currentComponent = text.getParent();
        if ("".equals(text.getText().trim())) {
            ((Panel)currentComponent).remove(text);
        }
    }
    private void insertComponent(Component c) {
        // if we are able to insert a component into a TextPane
        // then we would not need to end the TextPane here
        // and we would not need to start it again after its finished
        endInlineSection(); // end current inline Text
        ((Panel)currentComponent).add(c);
        currentComponent = c;
    }
    private void endComponent() {
        currentComponent = currentComponent.getParent();
        startInlineSection();
    }
    private void insertPanel(Panel p,GridBagConstraints con) {
        con.weightx = 1;
        ((Panel)currentComponent).add(p,con);
        currentComponent = p;
        startInlineSection();
    }
    private void endPanel() {
        endInlineSection();
        currentComponent = ((Panel)currentComponent).getParent();
    }



    class TagHandler {

        TagHandler parent;

        int row=-1;
        Vector rows;

        TextStyle style;
        int styleStart;

        String name;

        TagHandler(String name) {
            this.name = name;
        }

        public void processStartElement(KXmlParser parser) throws Exception {
            final int count = parser.getAttributeCount();

//#debug debug
Logger.debug("START: "+name);

            if ("a".equals(name)) {

                String value = parser.getAttributeValue(null, "href");


                //for (int c=0;c<count;c++) {
                //    String key = parser.getAttributeName(c).toLowerCase();
                //    String value = parser.getAttributeValue(c);
                //    if ("href".equals(key)) {

                if (value!=null) {
                        TextStyle linkStyle = new TextStyle();

                        linkStyle.putAll(style);
                        linkStyle.setAction(value);

                        style = linkStyle;
                }

                //    }
                //}
            }
            if ("font".equals(name)) {

                String color = parser.getAttributeValue(null, "color");
                if (color!=null) {
                    TextStyle linkStyle = new TextStyle();

                    linkStyle.putAll(style);
                    linkStyle.setForeground( Graphics2D.parseColor(color, 16) );

                    style = linkStyle;
                }

            }
            else if ("br".equals(name)) {
                if (currentComponent instanceof TextPane) { // should be TextComponent
                    TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                    inlineText.append( "\n" );
                }
                //#mdebug info
                else {
                    Logger.info("strange place for br tag, br can not go here");
                }
                //#enddebug
            }
            else if ("select".equals(name)) {
                int size = 1;
                for (int c=0;c<count;c++) {
                    String key = parser.getAttributeName(c).toLowerCase();
                    String value = parser.getAttributeValue(c);
                    if ("size".equals(key)) {
                        size = Integer.parseInt(value);
                    }
                }
                final Component c;
                if (size == 1) {
                    c = new ComboBox();
                }
                else {
                    c = new List();
                }
                insertComponent(c);

            }
            else if ("option".equals(name)) {

                boolean selected=false;
                for (int c=0;c<count;c++) {
                    String key = parser.getAttributeName(c).toLowerCase();
                    //String value = parser.getAttributeValue(c);
                    if ("selected".equals(key)) {
                        selected = true;
                    }
                }

                if (currentComponent instanceof ComboBox) {
                    ComboBox inlineText = (ComboBox)XHTMLLoader.this.currentComponent;
                    inlineText.getItems().addElement( new Option() );
                    if (selected || inlineText.getItemCount()==1) {
                        // TODO this does not work
                        // can only do this at the END of option
                        inlineText.setSelectedIndex( inlineText.getItemCount()-1 );
                    }
                }
                else if (currentComponent instanceof List) {
                    List inlineText = (List)XHTMLLoader.this.currentComponent;
                    inlineText.addElement( new Option() );
                    if (selected) {
                        Vector selectedValues = inlineText.getSelectedValues();
                        selectedValues.addElement( inlineText.getElementAt( inlineText.getSize()-1 ) );
                        inlineText.setSelectedValues(selectedValues);
                    }
                }
                //#mdebug info
                else {
                    Logger.info("strange place for option, should be inside select");
                }
                //#enddebug
            }
            else if ("input".equals(name)) {
                Class theClass = null;
                String text = null;
                int constraints = TextComponent.ANY;
                for (int c=0;c<count;c++) {
                    String key = parser.getAttributeName(c).toLowerCase();
                    String value = parser.getAttributeValue(c);
                    if ("type".equals(key)) { // text submit password button checkbox radio
                        value = value.toLowerCase();
                        if ("button".equals(value)) {
                            theClass = Button.class;
                        }
                        else if ("text".equals(value)) {
                            theClass = TextField.class;
                        }
                        else if ("checkbox".equals(value)) {
                            theClass = CheckBox.class;
                        }
                        else if ("radio".equals(value)) {
                            theClass = RadioButton.class;
                        }
                        else if ("password".equals(value)) {
                            theClass = TextField.class;
                            constraints = TextComponent.PASSWORD;
                        }
                        else {
                            theClass = TextField.class;
                        }
                    }
                    else if ("name".equals(key)) {

                    }
                    else if ("value".equals(key)) {
                        text = value;
                    }
                }
                if (theClass!=null) {
                    Component comp = (Component)theClass.newInstance();
                    if (text!=null) {
                        if (comp instanceof Label) {
                            ((Label)comp).setText(text);
                        }
                        else if (comp instanceof TextComponent) {
                            ((TextComponent)comp).setText(text);
                            ((TextComponent)comp).setConstraints(constraints);
                        }
                    }
                    insertComponent(comp);
                }
            }
            else if ("button".equals(name)) {
                insertComponent( new Button() );
            }
            else if ("textarea".equals(name)) {
                insertComponent( new TextArea() );
            }
            else if ("ul".equals(name) || "ol".equals(name)) { // lists
                Panel p = new Panel(new GridBagLayout(2, 0, 0, 0, 0, 0));
                //#mdebug debug
                if (DesktopPane.debug) {
                    p.setBorder( new LineBorder(0xFF00FF00) );
                }
                //#enddebug
                insertComponent( p );
            }
            else if ("li".equals(name)) { // list items

                Label l = new Label("ol".equals(parent.name)? (((Panel)currentComponent).getComponentCount()/2+1) +".":"*");
                l.setVerticalAlignment(Graphics.TOP);
                // we did insertComponent with a panel, so now we know we have a panel here
                ((Panel)currentComponent).add(l, new GridBagConstraints());

                insertPanel(new Panel(new FlowLayout(Graphics.VCENTER,0)), new GridBagConstraints());
            }
            else if ("tr".equals(name)) { // row
                if (parent!=null && parent.rows!=null) {
                    parent.row++;
                }
                //#mdebug info
                else {
                    Logger.info("strange place for tr tag, tr can not go here");
                }
                //#enddebug
            }
            else if ("table".equals(name)) {
                rows = new Vector();
                insertComponent(new Panel(new GridBagLayout(000, 2, 2, 2, 2, 2)) );
            }
            else if ("th".equals(name) || "td".equals(name)) { // col
                String colspan = parser.getAttributeValue(null,"colspan");
                String rowspan = parser.getAttributeValue(null,"rowspan");
                int colspani = colspan==null?1:Integer.parseInt(colspan);
                int rowspani = rowspan==null?1:Integer.parseInt(rowspan);

                if (parent!=null && parent.parent != null && parent.parent.rows!=null) {
                    for (int a=0;a<rowspani;a++) {
                        parent.parent.addToRow(a,colspani);
                    }
                }
                //#mdebug info
                else {
                    Logger.info("strange place for th/td tag, th/td can not go here");
                }
                //#enddebug

                Panel p = new Panel(new FlowLayout(Graphics.VCENTER,0));
                //#mdebug debug
                if (DesktopPane.debug) {
                    p.setBorder( new LineBorder(0xFF0000FF) );
                }
                //#enddebug
                GridBagConstraints c = new GridBagConstraints();
                c.colSpan = colspani;
                c.rowSpan = rowspani;
                //c.weightx = 1;
                //c.weighty = 1;
                insertPanel(p, c);

            }
            //#mdebug info
            else if ("b".equals(name)) {

            }
            else if ("i".equals(name)) {

            }
            else if ("u".equals(name)) {

            }
            else if ("center".equals(name)) {

            }
            else if ("body".equals(name)) {

            }
            else if ("p".equals(name)) {
                // do nothing
            }
            else if ("title".equals(name)) {
                // do nothing
            }
            else if ("html".equals(name)) {
                // do nothing
            }
            else if ("head".equals(name)) {
                // do nothing
            }
            else {
                Logger.info("unknwon start: "+name);
            }
            //#enddebug

            // TODO read style atribute and do things
        }

        private void addTextToLastOption(Vector items,String text) {
            if (items.size() == 0) return; // ignore this text if we are not in a option
            Option option = (Option)items.lastElement();
            String current = option.getValue();
            option.setValue(current==null?text:current+text);
        }

        public void processEndElement(KXmlParser parser) {

            if ("select".equals(name)) {
                endComponent();
            }
            else if ("input".equals(name)) {
                endComponent();
            }
            else if ("button".equals(name)) {
                endComponent();
            }
            else if ("textarea".equals(name)) {
                endComponent();
            }
            else if ("tr".equals(name)) {
                // TODO set marker for end of row, we will need to insert a empty panl
                // at this marker if the row does not have enough elements to fill it up
            }
            else if ("th".equals(name) || "td".equals(name)) {
                endPanel();
            }
            else if ("table".equals(name)) {
                int biggest = 0;
                for (int a=0;a<rows.size();a++) {
                    Integer row = (Integer)rows.elementAt(a);
                    if (row.intValue() > biggest) {
                        biggest = row.intValue();
                    }
                }
                GridBagLayout layout = (GridBagLayout)((Panel)currentComponent).getLayout();
                layout.columns = biggest;
                //#debug debug
                Logger.debug("bigget "+rows+" "+biggest);
                endComponent();
            }
            else if ("li".equals(name)) {
                endPanel();
            }
            else if ("ul".equals(name) || "ol".equals(name)) {
                endComponent();
            }
            //#mdebug info
            else if ("b".equals(name)) {

            }
            else if ("i".equals(name)) {

            }
            else if ("u".equals(name)) {

            }
            else if ("center".equals(name)) {

            }
            else if ("a".equals(name)) {

            }
            else if ("body".equals(name)) {

            }
            else if ("p".equals(name)) {
                // do nothing
            }
            else if ("html".equals(name)) {
                // do nothing
            }
            else if ("head".equals(name)) {
                // do nothing
            }
            else if ("title".equals(name)) {
                // do nothing
            }
            else if ("option".equals(name)) {
                // do nothing
            }
            else if ("br".equals(name)) {
                // do nothing
            }
            else {
                Logger.info("unknown end: "+name);
            }
            //#enddebug
        }

        private void processText(KXmlParser parser) {
            String string = parser.getText();
            string = StringUtil.replaceAll(string, "\n", " ");
            string = StringUtil.replaceAll(string, "\t", " ");
            // as this is HTML we want to reduce all space to a single space
            while (string.indexOf("  ")>=0) {
                string = StringUtil.replaceAll(string, "  ", " ");
            }

            //#debug debug
            Logger.debug("    text: \""+string+"\"");
            if (currentComponent instanceof TextPane) { // should be TextComponent

                if (string.length()>0) {
                    TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;

                    if (newBlock && inlineText.getText().length()!=0) {
                        inlineText.append("\n");
                    }
                    newBlock=false; // we are now adding some text, and we should already know if we needed or didnt need to start it with a \n
                    
                    // if we are the start of a new block of text, get rid of any spaces at the start of the line
                    if (inlineText.getText().length()==0 || inlineText.getText().endsWith(" ") || inlineText.getText().endsWith("\n")) {
                        if (string.length()!=0 && string.charAt(0)==' ') {
                            string = string.substring(1);
                        }
                    }
                    inlineText.append( string );
                }
            }
            else if (currentComponent instanceof ComboBox) {
                ComboBox inlineText = (ComboBox)XHTMLLoader.this.currentComponent;
                addTextToLastOption( inlineText.getItems(),string );
            }
            else if (currentComponent instanceof List) {
                List inlineText = (List)XHTMLLoader.this.currentComponent;
                addTextToLastOption(inlineText.getItems(), string);
            }
            else if (currentComponent instanceof Button) {
                Button inlineText = (Button)XHTMLLoader.this.currentComponent;
                inlineText.setText( inlineText.getText()+string );
            }
            else if (currentComponent instanceof TextArea) {
                TextArea inlineText = (TextArea)XHTMLLoader.this.currentComponent;
                inlineText.append( string );
            }
            //#mdebug info
            else {
                Logger.info("strange place for text");
            }
            //#enddebug
        }

        private void processRef(KXmlParser parser) {
            //#debug debug
            Logger.debug("ref: "+parser.getName());
            if (currentComponent instanceof TextPane) { // should be TextComponent
                TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                inlineText.append( parser.getName() );
            }
            //#mdebug info
            else {
                Logger.info("strange place for ref");
            }
            //#enddebug
        }


        void addToRow(int a, int num) {
            int currentRow = row + a;
            while (rows.size()<=currentRow) {
                rows.addElement(new Integer(0));
            }
            int cols = ((Integer)rows.elementAt(currentRow)).intValue() + num;
            rows.setElementAt(new Integer(cols), currentRow);
        }

        private TagHandler getParent() {
            return parent;
        }

        private void setParant(TagHandler currentTag) {
            parent = currentTag;
        }
    }
}

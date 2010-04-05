package net.yura.mobile.gui.layout;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
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
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Option;
import net.yura.mobile.util.StringUtil;
import org.kxml2.io.KXmlParser;

/**
 * @author Yura Mamyrin
 */
public class XHTMLLoader {

    Panel root;
    TagHandler currentTag;
    Component currentComponent;

    public void gotResult(InputStream resultsStream) {

        try {

            root = new Panel(new FlowLayout(Graphics.VCENTER,0));
            currentComponent = root;

            KXmlParser parser = new KXmlParser();
            parser.setInput(resultsStream, null);

            startInlineSection();

            read(parser);

            endInlineSection();
        }
        catch(Exception ex) {
            Logger.warn(ex);
        }
    }

    public void gotResult(TextPane pane,String string) {

        currentComponent = pane;
        try {
            KXmlParser parser = new KXmlParser();
            parser.setInput(new ByteArrayInputStream( string.getBytes() ), null);

            read(parser);
        }
        catch(Exception ex) {
            Logger.warn(ex);
        }
    }

    /**
     * do not default
     * @return style for this tag if found, otherwise null
     */
    private TextStyle getStyleForTag(KXmlParser parser) {

        String name = parser.getName();

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

        // css defaults
        if ("b".equals(name)) {
            return bold;
        }
        else if ("i".equals(name)) {
            return italic;
        }
        else if ("u".equals(name)) {
            return underline;
        }
        else if ("center".equals(name)) {
            return center;
        }
        else if ("a".equals(name)) {
            return link;
        }

        return null;
    }

    protected void read(KXmlParser parser) throws Exception {

        int eventType = parser.getEventType();
        do {
            if(eventType == KXmlParser.START_TAG) {
                TagHandler tag = new TagHandler();

                tag.setParant(currentTag);
                tag.style = getStyleForTag(parser);

                currentTag = tag;
                currentTag.processStartElement(parser);

                if (currentComponent instanceof TextPane) {
                    TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                    currentTag.styleStart = inlineText.getText().length();
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

                currentTag.processEndElement(parser);
                currentTag = currentTag.getParent();
            }
            else if(eventType == KXmlParser.TEXT) {
                currentTag.processText(parser);
            }
            else if(eventType == KXmlParser.ENTITY_REF) {
                currentTag.processRef(parser);
            }
            //#mdebug
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

    final static TextStyle bold = new TextStyle();
    final static TextStyle italic = new TextStyle();
    final static TextStyle underline = new TextStyle();
    final static TextStyle center = new TextStyle();
    final static TextStyle link = new TextStyle();
    static {
        bold.setBold(true);
        bold.setName("b");
        italic.setItalic(true);
        italic.setName("i");
        underline.setUnderline(true);
        underline.setName("u");
        center.setAlignment( Graphics.HCENTER );
        center.setName("center");

        link.setUnderline(true);
        link.setForeground(0x0000FF);
        link.addForeground(0xFF0000, Style.FOCUSED);
        link.setName("a");
    }

    private void startInlineSection() {
        TextPane it = new TextPane();
        //TextArea it = new TextArea(); it.setLineWrap(true);
        //#mdebug
        if (DesktopPane.debug) {
            it.setBorder( new LineBorder(0x00FF0000) );
        }
        //#enddebug
        ((Panel)currentComponent).add(it);
        currentComponent = it;
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

        public void processStartElement(KXmlParser parser) throws Exception {
            final int count = parser.getAttributeCount();
            String startTag = parser.getName().toLowerCase();
//#debug debug
Logger.debug("START: "+startTag);

            if ("a".equals(startTag)) {
                for (int c=0;c<count;c++) {
                    String key = parser.getAttributeName(c).toLowerCase();
                    String value = parser.getAttributeValue(c);
                    if ("href".equals(key)) {

                        TextStyle linkStyle = new TextStyle();

                        linkStyle.putAll(style);
                        linkStyle.setAction(value);

                        style = linkStyle;
                    }
                }
            }
            else if ("br".equals(startTag)) {
                if (currentComponent instanceof TextPane) { // should be TextComponent
                    TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                    inlineText.setText( inlineText.getText()+"\n" );
                }
                //#mdebug warn
                else {
                    Logger.warn("strange place for br tag, br can not go here");
                }
                //#enddebug
            }
            else if ("select".equals(startTag)) {
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
            else if ("option".equals(startTag)) {

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
                //#mdebug warn
                else {
                    Logger.warn("strange place for option, should be inside select");
                }
                //#enddebug
            }
            else if ("input".equals(startTag)) {
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
            else if ("button".equals(startTag)) {
                insertComponent( new Button() );
            }
            else if ("textarea".equals(startTag)) {
                insertComponent( new TextArea() );
            }
            else if ("ul".equals(startTag) || "ol".equals(startTag)) { // lists
                Panel p = new Panel(new GridBagLayout(2, 0, 0, 0, 0, 0));
                //#mdebug
                if (DesktopPane.debug) {
                    p.setBorder( new LineBorder(0x0000FF00) );
                }
                //#enddebug
                insertComponent( p );
            }
            else if ("li".equals(startTag)) { // list items
                Label l = new Label("*");
                l.setVerticalAlignment(Graphics.TOP);
                // we did insertComponent with a panel, so now we know we have a panel here
                ((Panel)currentComponent).add(l, new GridBagConstraints());

                insertPanel(new Panel(new FlowLayout(Graphics.VCENTER,0)), new GridBagConstraints());
            }
            else if ("tr".equals(startTag)) { // row
                if (parent!=null && parent.rows!=null) {
                    parent.row++;
                }
                //#mdebug warn
                else {
                    Logger.warn("strange place for tr tag, tr can not go here");
                }
                //#enddebug
            }
            else if ("table".equals(startTag)) {
                rows = new Vector();
                insertComponent(new Panel(new GridBagLayout(000, 2, 2, 2, 2, 2)) );
            }
            else if ("th".equals(startTag) || "td".equals(startTag)) { // col
                String colspan = parser.getAttributeValue(null,"colspan");
                String rowspan = parser.getAttributeValue(null,"rowspan");
                int colspani = colspan==null?1:Integer.parseInt(colspan);
                int rowspani = rowspan==null?1:Integer.parseInt(rowspan);

                if (parent!=null && parent.parent != null && parent.parent.rows!=null) {
                    for (int a=0;a<rowspani;a++) {
                        parent.parent.addToRow(a,colspani);
                    }
                }
                //#mdebug warn
                else {
                    Logger.warn("strange place for th/td tag, th/td can not go here");
                }
                //#enddebug

                Panel p = new Panel(new FlowLayout(Graphics.VCENTER,0));
                //#mdebug
                if (DesktopPane.debug) {
                    p.setBorder( new LineBorder(0x000000FF) );
                }
                //#enddebug
                GridBagConstraints c = new GridBagConstraints();
                c.colSpan = colspani;
                c.rowSpan = rowspani;
                //c.weightx = 1;
                //c.weighty = 1;
                insertPanel(p, c);

            }
            //#mdebug warn
            else if ("b".equals(startTag)) {

            }
            else if ("i".equals(startTag)) {

            }
            else if ("u".equals(startTag)) {

            }
            else if ("center".equals(startTag)) {

            }
            else if ("body".equals(startTag)) {

            }
            else if ("p".equals(startTag)) {
                // do nothing
            }
            else if ("title".equals(startTag)) {
                // do nothing
            }
            else if ("html".equals(startTag)) {
                // do nothing
            }
            else if ("head".equals(startTag)) {
                // do nothing
            }
            else {
                Logger.warn("unknwon start: "+startTag);
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
            String endTag = parser.getName();

            if ("select".equals(endTag)) {
                endComponent();
            }
            else if ("input".equals(endTag)) {
                endComponent();
            }
            else if ("button".equals(endTag)) {
                endComponent();
            }
            else if ("textarea".equals(endTag)) {
                endComponent();
            }
            else if ("tr".equals(endTag)) {
                // TODO set marker for end of row, we will need to insert a empty panl
                // at this marker if the row does not have enough elements to fill it up
            }
            else if ("th".equals(endTag) || "td".equals(endTag)) {
                endPanel();
            }
            else if ("table".equals(endTag)) {
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
            else if ("li".equals(endTag)) {
                endPanel();
            }
            else if ("ul".equals(endTag) || "ol".equals(endTag)) {
                endComponent();
            }
            //#mdebug warn
            else if ("b".equals(endTag)) {

            }
            else if ("i".equals(endTag)) {

            }
            else if ("u".equals(endTag)) {

            }
            else if ("center".equals(endTag)) {

            }
            else if ("a".equals(endTag)) {

            }
            else if ("body".equals(endTag)) {

            }
            else if ("p".equals(endTag)) {
                // do nothing
            }
            else if ("html".equals(endTag)) {
                // do nothing
            }
            else if ("head".equals(endTag)) {
                // do nothing
            }
            else if ("title".equals(endTag)) {
                // do nothing
            }
            else if ("option".equals(endTag)) {
                // do nothing
            }
            else if ("br".equals(endTag)) {
                // do nothing
            }
            else {
                Logger.warn("unknown end: "+endTag);
            }
            //#enddebug
        }

        private void processText(KXmlParser parser) {
            String string = parser.getText();
            string = StringUtil.replaceAll(string, "\n", " ");
            //string = StringUtil.replaceAll(string, "\t", " ");
            //string = StringUtil.replaceAll(string, "  ", " ");
            //string = StringUtil.trimStart(string);
            //#debug debug
            Logger.debug("    text: \""+string+"\"");
            if (currentComponent instanceof TextPane) { // should be TextComponent
                TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                inlineText.append( string );
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
            //#mdebug warn
            else {
                Logger.warn("strange place for text");
            }
            //#enddebug
        }

        private void processRef(KXmlParser parser) {
            //#debug debug
            Logger.debug("ref: "+parser.getName());
            if (currentComponent instanceof TextPane) { // should be TextComponent
                TextPane inlineText = (TextPane)XHTMLLoader.this.currentComponent;
                inlineText.setText( inlineText.getText()+parser.getName() );
            }
            //#mdebug warn
            else {
                Logger.warn("strange place for ref");
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

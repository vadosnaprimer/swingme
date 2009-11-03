package net.yura.mobile.gui.layout;

import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.TextPane;
import net.yura.mobile.gui.components.TextPane.TextStyle;
import net.yura.mobile.util.Option;
import org.kxml2.io.KXmlParser;

/**
 * @author Yura Mamyrin
 */
public class XHTMLLoader {

    TagHandler rootTag;
    TagHandler currentTag;

    public void gotResult(InputStream resultsStream) {

        try {

                rootTag = new TagHandler();
                rootTag.panel = new Panel(new FlowLayout(Graphics.VCENTER));
                rootTag.inlineText = new TextPane();
                rootTag.panel.add(rootTag.inlineText);

                currentTag = rootTag;


            KXmlParser parser = new KXmlParser();
            parser.setInput(resultsStream, null);
            parser.nextTag(); // the START_DOCUMENT event

            read(parser);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void read(KXmlParser parser) throws Exception {

        int eventType = parser.getEventType();
        do {
//            if(eventType == KXmlParser.START_DOCUMENT) {
//                System.out.println("Start document");
//            }
//            else if(eventType == KXmlParser.END_DOCUMENT) {
//                System.out.println("End document");
//            }
            if(eventType == KXmlParser.START_TAG) {
                TagHandler tag = new TagHandler();
                tag.panel = currentTag.panel;
                tag.inlineText = currentTag.inlineText;
                tag.setParant(currentTag);
                currentTag = tag;
                currentTag.processStartElement(parser);
            }
            else if(eventType == KXmlParser.END_TAG) {
                currentTag.processEndElement(parser);
                currentTag = currentTag.getParent();
            }
            else if(eventType == KXmlParser.TEXT) {
                currentTag.processText(parser);
            }
            else if(eventType == KXmlParser.ENTITY_REF) {
                currentTag.processRef(parser);
            }
            eventType = parser.nextToken();
        } while (eventType != KXmlParser.END_DOCUMENT);

    }

    public Component getRoot() {
System.out.println( ((TextPane)rootTag.inlineText).getText() );
        return rootTag.panel;
    }

    final static TextStyle bold = new TextStyle();
    final static TextStyle italic = new TextStyle();
    final static TextStyle underline = new TextStyle();
    static {
        bold.setBold(true);
        italic.setItalic(true);
        underline.setUnderline(true);
    }

    class TagHandler {

        TagHandler parent;

        int row=-1;
        Vector rows;

        Panel panel;
        Component inlineText;

        TextStyle style;
        int styleStart;

        public void processStartElement(KXmlParser parser) throws Exception {
            final int count = parser.getAttributeCount();
            String startTag = parser.getName().toLowerCase();

System.out.println("START: "+startTag);

            if ("html".equals(startTag)) {

            }
            else if ("body".equals(startTag)) {

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
                if (size == 1) {
                    inlineText = new ComboBox();
                }
                else {
                    inlineText = new List();
                }
            }
            else if ("option".equals(startTag)) {
                if (inlineText instanceof ComboBox) {
                    ComboBox inlineText = (ComboBox)this.inlineText;
                    inlineText.getItems().addElement( new Option() );
                }
                else if (inlineText instanceof List) {
                    List inlineText = (List)this.inlineText;
                    inlineText.getItems().addElement( new Option() );
                }
                //#mdebug
                else {
                    System.out.println("strange place for option, should be inside select");
                }
                //#enddebug
            }
            else if ("input".equals(startTag)) {
                Class theClass = null;
                String text = null;
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
                        }
                    }
                }
            }
            else if ("b".equals(startTag)) {
                startFormat(bold);
            }
            else if ("i".equals(startTag)) {
                startFormat(italic);
            }
            else if ("u".equals(startTag)) {
                startFormat(underline);
            }
            else if ("br".equals(startTag)) {
                if (inlineText instanceof TextPane) { // should be TextComponent
                    TextPane inlineText = (TextPane)this.inlineText;
                    inlineText.setText( inlineText.getText()+"\n" );
                }
                //#mdebug
                else {
                    System.out.println("strange place for br tag, br can not go here");
                }
                //#enddebug
            }
            else if ("ul".equals(startTag) || "ol".equals(startTag)) { // lists

            }
            else if ("li".equals(startTag)) { // list items

            }
            else if ("table".equals(startTag)) {
                // new blocking tag
                panel = new Panel(new GridBagLayout(000, 2, 2, 2, 2, 2));
                rows = new Vector();
            }
            else if ("tr".equals(startTag)) { // row
                if (parent!=null && parent.rows!=null) {
                    parent.row++;
                }
                //#mdebug
                else {
                    System.out.println("strange place for tr tag, tr can not go here");
                }
                //#enddebug
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
                //#mdebug
                else {
                    System.out.println("strange place for th/td tag, th/td can not go here");
                }
                //#enddebug
            }
            //#mdebug
            else {
                System.out.println("unknwon start: "+startTag);
            }
            //#enddebug
        }


        public void processEndElement(KXmlParser parser) {
            String endTag = parser.getName();
            if (style!=null) {
                if (inlineText instanceof TextPane) {
                    TextPane inlineText = (TextPane)this.inlineText;
                    int boldend = inlineText.getText().length();
                    inlineText.setCharacterAttributes(styleStart, boldend-styleStart, bold);
                }
                return;
            }


            if ("table".equals(endTag)) {
                int biggest = 0;
                for (int a=0;a<rows.size();a++) {
                    Integer row = (Integer)rows.elementAt(a);
                    if (row.intValue() > biggest) {
                        biggest = row.intValue();
                    }
                }
                System.out.println("bigget "+rows+" "+biggest);
            }
            //#mdebug
            else {
                System.out.println("unknown end: "+endTag);
            }
            //#enddebug
        }

        private void processText(KXmlParser parser) {
            String string = parser.getText();
            //string = StringUtil.replaceAll(string, "\n", " ");
            //string = StringUtil.replaceAll(string, "\t", " ");
            //string = StringUtil.replaceAll(string, "  ", " ");
            //string = StringUtil.trimStart(string);
            System.out.println("    text: \""+string+"\"");
            if (inlineText instanceof TextPane) { // should be TextComponent
                TextPane inlineText = (TextPane)this.inlineText;
                inlineText.setText( inlineText.getText()+string );
            }
            //#mdebug
            else {
                System.out.println("strange place for text");
            }
            //#enddebug
        }
        
        private void processRef(KXmlParser parser) {
            System.out.println("ref: "+parser.getName());
            if (inlineText instanceof TextPane) { // should be TextComponent
                TextPane inlineText = (TextPane)this.inlineText;
                inlineText.setText( inlineText.getText()+parser.getName() );
            }
            //#mdebug
            else {
                System.out.println("strange place for ref");
            }
            //#enddebug
        }


        private void startFormat(TextStyle st) {
            if (inlineText instanceof TextPane) {
                TextPane inlineText = (TextPane)this.inlineText;
                styleStart = inlineText.getText().length();
                style = st;
            }
            //#mdebug
            else {
                System.out.println("strange place for format tag, formatting can not go here");
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
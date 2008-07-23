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

package net.yura.mobile.gui;

import java.io.InputStream;
import java.util.Hashtable;
import net.yura.mobile.gui.border.LineBorder;
import org.kxml2.io.KXmlParser;

public class Theme {

	public Theme() {
		
            styles = new Hashtable();
            
            Style buttonStyle = new Style();
            buttonStyle.addBackground(0x00FFFFFF, Style.ALL);
            buttonStyle.addForeground(0x00000000, Style.ALL);
            buttonStyle.addForeground(0x000000FF, Style.FOCUSED);
            buttonStyle.addForeground(0x00808080, Style.DISABLED);
            buttonStyle.addBorder(new LineBorder(0x00808080),Style.ALL);
            buttonStyle.addBorder(new LineBorder(0x00000000), Style.FOCUSED);
            
            setStyleFor(buttonStyle,"Button");
            setStyleFor(buttonStyle,"TextField");

            Style radioStyle = new Style();
            radioStyle.addForeground(0x00000000, Style.ALL);
            radioStyle.addForeground(0x000000FF, Style.FOCUSED);
            radioStyle.addForeground(0x00808080, Style.DISABLED);
            setStyleFor(radioStyle,"RadioButton");
            setStyleFor(radioStyle,"CheckBox");
            
            Style scrollStyle = new Style();
            scrollStyle.addProperty(new Integer(0x00FFFFFF),"scrollBarCol",Style.ALL );
            scrollStyle.addProperty(new Integer(0x00000000),"scrollTrackCol",Style.ALL );
            setStyleFor(scrollStyle,"ScrollPane");
	}
        
        private Hashtable styles;
        
        public void load(InputStream input) throws Exception {
            
            KXmlParser parser = new KXmlParser();
            parser.setInput(input, null);
            parser.nextTag();

            Hashtable styleList = new Hashtable();

            // read start tag
            while (parser.nextTag() != KXmlParser.END_TAG) {

                String name = parser.getName();
                
                if ("style".equals(name)){
                    
                    String id=parser.getAttributeValue(null, "id");
                    Style newStyle = readStyle(parser);
                    styleList.put(id, newStyle);

                }
                else {

                    if ("bind".equals(name)) {

                        String style=parser.getAttributeValue(null, "style");
                        String key=parser.getAttributeValue(null, "key");

                        Style newStyle = (Style)styleList.get(style);
                        Style oldStyle = (Style)styles.get(key);

                        Style theStyle;
                        if (oldStyle!=null) {
                            theStyle = new Style();
                            theStyle.putAll(oldStyle);
                            theStyle.putAll(newStyle);
                        }
                        else {
                            theStyle = newStyle;
                        }
                        setStyleFor(theStyle, key);

                    }
                    else {
                        System.out.println("unknown found: "+name);
                    }
                    
                    // read end tag
                    parser.skipSubTree();
                    
                }


            }

        }

        public Style getStyle(String name) {
            Style st = (Style)styles.get(name);
            return st;
        }
        
        
        private Style readStyle(KXmlParser parser) throws Exception {
            
            Style newStyle = new Style();

            // read start tag
            while (parser.nextTag() != KXmlParser.END_TAG) {
                    String name = parser.getName();

                    
                    if ("state".equals(name)) {
                        
                        String value = parser.getAttributeValue(null, "value");
                        int st = workOutState(value);
                        
                        // read start tag
                        //parser.getEventType();
                        while (parser.nextTag() != KXmlParser.END_TAG) {
                            String name2 = parser.getName();
                            
                            if ("imagePainter".equals(name2)) {
                                String path = parser.getAttributeValue(null, "path");
                                String sourceInsets = parser.getAttributeValue(null, "sourceInsets");
                                String paintCenter = parser.getAttributeValue(null, "paintCenter");
                                
                                
                            }
                            else if ("property".equals(name2)) {

                                
                            }
                            else if ("color".equals(name2)) {
                                
                                
                            }
                            else if ("font".equals(name2)) {
                                

                            }
                            else {
                                System.out.println("unknown found: "+name2);
                            }
                            
                            // read end tag
                            parser.skipSubTree();
                        }
                        
                    }
                    else {
                        if ("insets".equals(name)) {

                        }
                        else if ("imageIcon".equals(name)) {

                        }
                        else {
                            System.out.println("unknown found: "+name);
                        }
                        
                        // read end tag
                        parser.skipSubTree();
                    }

            }
            
            return newStyle;
            
        }

        public void setStyleFor(Style theStyle, String key) {
            Font f = theStyle.getFont(Style.ALL);
            if (f==null) {
                theStyle.addFont(new Font(), Style.ALL );
            }
            styles.put(key, theStyle);
        }

        private int workOutState(String value) {

            int result=Style.ALL;
            
            if (value==null) {
                return result;
            }
            if ("ENABLED".indexOf(value)!=-1) {
                result |= Style.ENABLED;
            }
            if ("DISABLED".equals(value)) {
                result |= Style.DISABLED;
            }
            if ("FOCUSED".equals(value)) {
                result |= Style.FOCUSED;
            }
            if ("SELECTED".equals(value)) {
                result |= Style.SELECTED;
            }
            
            return result;

        }

}


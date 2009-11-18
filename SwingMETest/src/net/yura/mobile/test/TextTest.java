/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.test;

import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.TextPane;
import net.yura.mobile.gui.components.TextPane.TextStyle;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.layout.GridLayout;
import net.yura.mobile.gui.layout.XHTMLLoader;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.test.MainPane.Section;

/**
 *
 * @author Administrator
 */
public class TextTest extends Section {

    Panel componentTest2;
    TextArea viewText;

    public void createTests() {
                                // text
                                addTest("TextField Test","componentTest2");
                                addTest("TextArea Test","textAreaTest1");
                                addTest("TextArea Dialog","textAreaTest2");
                                addTest("View Text","viewText");
                                addTest("Text Pane","textPane");
                                addTest("HTML Text","HTMLtest");
                                addTest("View XHTML","viewXHTML");
    }

    public void openTest(String actionCommand) {

                if ("componentTest2".equals(actionCommand)) {

			if (componentTest2==null) {
                            componentTest2 = new Panel( new BorderLayout() );

                            Panel component1 = new Panel(new GridLayout(0,1,0));
                            Panel component2 = new Panel(new GridLayout(0,1));

                            component1.add( new Label("any") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.ANY) );
                            component1.add( new Label("email") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.EMAILADDR) );
                            component1.add( new Label("numeric") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.NUMERIC) );
                            component1.add( new Label("phone") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.PHONENUMBER) );
                            component1.add( new Label("url") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.URL) );
                            component1.add( new Label("decimal") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.DECIMAL) );
                            component1.add( new Label("password") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.PASSWORD) );
                            component1.add( new Label("Word") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.INITIAL_CAPS_WORD) );
                            component1.add( new Label("Sentence") );
                            component2.add( new TextField(javax.microedition.lcdui.TextField.INITIAL_CAPS_SENTENCE) );

                            componentTest2.add(component1,Graphics.LEFT);
                            componentTest2.add(component2);

                        }
                        addToScrollPane(componentTest2,null);
                }
                else if ("textAreaTest1".equals(actionCommand)) {

                    Panel p1 = new Panel( new FlowLayout(Graphics.VCENTER) );

                    Panel p = new Panel( new GridLayout(0, 2) );

                    TextArea ta = new TextArea("edit me");
                    ta.setLineWrap(true);
                    p.add( ta );
                    p.add(new Button("bob"));

                    p1.add(new Button("top"));
                    p1.add(p);
                    p1.add(new Button("bottom"));

                    addToScrollPane(p1,null);
                }
                else if ("textAreaTest2".equals(actionCommand)) {

                    OptionPane.showMessageDialog(null, new Object[] {"short string",
                        "Hello bob the builder this is a really" +
                        " really long string, thats relly really long"
                            ,new Button("hi")}, "title", OptionPane.INFORMATION_MESSAGE);
                }
		else if ("viewText".equals(actionCommand)) {

			//if (viewText==null) {


StringBuffer buf = new StringBuffer();


//* wrap testing
for (int c=0;c<4;c++) {
	buf.append("sdfdsfsdf sdfjk hdsfjk s diw k s d f j k s dfjksdh skjdf sdjkf sdhfjkskd fskjdf hsdjkf hsdjkf sdjkf hskjd fhsdf\n");
}
//*/
				viewText = new TextArea();
                                viewText.setFocusable(false);
				viewText.setLineWrap(true);
				viewText.setText(buf.toString()); // this is the same as passing it into the constructor if wrap is false

				//viewText.setLineWrap(true); // this is the BAD order to do this
							    // as it needs to work out the size twice
			//}

/* THIS wont work, but it wont work in Swing either!!

			viewText.setBorder( new EmptyBorder(10,10,10,10) );
			Panel p = new Panel( new BorderLayout() );
			p.add(viewText);
			p.add(new Label("Label"),Graphics.TOP);
*/

			ScrollPane tmp = new ScrollPane( viewText );
			tmp.setBorder( new EmptyBorder(10,10,10,10) );
			Panel p = new Panel( new BorderLayout() );
			p.add(tmp);

                        CheckBox edit = new CheckBox("Edit");
                        edit.setActionCommand("open_text_edit");
                        edit.addActionListener(this);
                        edit.setSelected( viewText.isFocusable() );
                        edit.setMnemonic( KeyEvent.KEY_SOFTKEY1 );

                        CheckBox wrap = new CheckBox("Wrap");
                        wrap.setActionCommand("open_text_wrap");
                        wrap.addActionListener(this);
                        wrap.setSelected( viewText.getLineWrap() );

                        Panel bottom = new Panel(new FlowLayout());
                        bottom.add(edit);
                        bottom.add(wrap);
                        p.add(bottom, Graphics.BOTTOM);

			addToContentPane(p, null );

		}
                else if ("open_text_edit".equals(actionCommand)) {

                    viewText.setFocusable( !viewText.isFocusable() );
                    viewText.repaint();

                }
                else if ("open_text_wrap".equals(actionCommand)) {

                    viewText.setLineWrap( !viewText.getLineWrap() );
                    viewText.getWindow().revalidate();
                    viewText.getWindow().repaint();

                }
                else if ("HTMLtest".equals(actionCommand)) {


                    TextPane html = new TextPane();
                    html.setActionListener(this);
html.setBackground(0x00FFAAAA);
                    html.setText("<html><center>Bob <b>the</b> <i>builder</i>. <a href=\"link\">link</a></center></html>");

                    Panel p = new Panel(new BorderLayout());

                    TextArea ta = new TextArea("sdfgjkh sdf jlghsfdgh sfh gjdflkj ghdlf jghdjl kfhgdlf jghdkf lghdl fghdl kfgh dlfkgh dlfkghdflgk jhdlfkgh");
                    ta.setLineWrap(true);

                    p.add(html, Graphics.TOP);
                    p.add(ta);

                    addToContentPane( p ,null);

                }
		else if ("viewXHTML".equals(actionCommand)) {

			//if (viewText==null) {


XHTMLLoader loader = null;

InputStream inputStreamTxt=null;
try {
	inputStreamTxt = this.getClass().getResourceAsStream("/xhtmltest.xhtml"); // xhtmltest // "/test2.xhtml"
        loader = new XHTMLLoader();
        loader.gotResult(inputStreamTxt);
	//int c ;
	//while ((c = inputStreamTxt.read()) != -1)
	//{buf.append((char)c);}
}
catch(Exception ex) {
	ex.printStackTrace();
}
finally {
    if (inputStreamTxt!=null) {
	try {
		inputStreamTxt.close();
	}
	catch(Exception ex) { }
    }
}


				//viewText.setLineWrap(true); // this is the BAD order to do this
							    // as it needs to work out the size twice
			//}

/* THIS wont work, but it wont work in Swing either!!

			viewText.setBorder( new EmptyBorder(10,10,10,10) );
			Panel p = new Panel( new BorderLayout() );
			p.add(viewText);
			p.add(new Label("Label"),Graphics.TOP);
*/

                        Component xhtml = loader.getRoot();
                        System.out.println("obre "+xhtml);

			//addToContentPane(p, makeButton("Back","mainmenu") , edit );
                        addToScrollPane( xhtml ,null);

		}
                else if ("textPane".equals(actionCommand)) {

                    String text = "Lorem ipsum dolor sit amet, consectetur " +
                    "adipiscing elit. Nam nunc lacus, dapibus id tincidunt in, " +
                    "malesuada consequat diam. \nQuisque fermentum risus eu velit " +
                    "tincidunt viverra. Morbi nec dictum tellus. Morbi dui lectus, " +
                    "congue in cursus eget, dapibus sed sapien. \nPellentesque habitant " +
                    "morbi tristique senectus et netus et malesuada fames ac turpis " +
                    "egestas. Vestibulum consectetur sem quis tellus" +
                    "adipiscing elit. \nNam nunc lacus, dapibus id tincidunt in, " +
                    "malesuada consequat diam. \nQuisque \nfermentum risus eu velit " +
                    "tincidunt viverra. Morbi nec dictum tellus. Morbi dui lectus, " +
                    "congue in cursus eget, dapibus sed sapien. Pellentesque habitant " +
                    "morbi tristique senectus et netus et malesuada fames ac turpis " +
                    "egestas. Vestibulum consectetur           sem         quis tellus01234";
                    TextPane textPane = new TextPane();
                    textPane.setText(text);

                    TextStyle bold = new TextStyle();
                    bold.setBold(true);

                    TextStyle italic = new TextStyle();
                    italic.setItalic(true);
                    italic.setAlignment(TextStyle.ALIGN_RIGHT);

                    TextStyle underline = new TextStyle();
                    underline.setUnderline(true);
                    underline.setForeground(0x0000FF);

                    underline.addForeground(0xFF0000, Style.FOCUSED);
                    underline.addBackground(0xFFFF00, Style.FOCUSED);
                    underline.addBorder(new LineBorder(0x000000,3), Style.ALL);
                    underline.addBorder(new LineBorder(0x0000FF,3), Style.FOCUSED);
                    underline.setAction("SomeAction");

                    TextStyle blue = new TextStyle();
                    blue.setForeground(0x0000FF);

                    TextStyle alignRight = new TextStyle();
                    alignRight.setAlignment(TextStyle.ALIGN_RIGHT);
                    alignRight.setForeground(0xFF0000);

                    TextStyle alignCenter = new TextStyle();
                    alignCenter.setAlignment(TextStyle.ALIGN_CENTER);
                    alignCenter.setForeground(0x00FF00);

                    TextStyle iconStyle = null;
                    try {
                        iconStyle = new TextStyle();
                        iconStyle.setIcon(new Icon("/skin1.png"));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                    textPane.setCharacterAttributes(0, 5, bold);         //Bold
                    textPane.setCharacterAttributes(3, 7, italic);       //Italic
                    textPane.setCharacterAttributes(70, 10, italic); // Underline + Italic
                    textPane.setCharacterAttributes(50, 10, bold);   // Underline + Bold
                    textPane.setCharacterAttributes(15, 100, underline); // Underline

                    textPane.setCharacterAttributes(90, 10, italic);
                    textPane.setCharacterAttributes(90, 10, bold);  // Underline + Italic + Bold

                    textPane.setCharacterAttributes(130, 10, blue);  // blue

                    textPane.setParagraphAttributes(0, 0, alignRight);
                    textPane.setParagraphAttributes(125, 0, alignCenter);
                    textPane.setParagraphAttributes(text.length() - 1, 0, underline);

                    textPane.setCharacterAttributes(251, 2, iconStyle);  // Icon
                    textPane.setCharacterAttributes(text.length() - 3, 10, bold);  // Bold

                    Panel p = new Panel( new BorderLayout() );

                    Button bx = new Button("Button BOTTOM");
                    bx.addActionListener(this);
                    bx.setActionCommand("revalidate");

                    p.add(new Button("Button TOP"), Graphics.TOP);
                    p.add(textPane);
                    p.add(bx, Graphics.BOTTOM);

                    p.setBackground(0xFFFFFF);
                    textPane.setBackground(0xFFFFFF);

                    ScrollPane tmp = new ScrollPane( p );
                    tmp.setBorder( new EmptyBorder(10,10,10,10) );
                    p2 = new Panel( new BorderLayout() );
                    p2.add(tmp);

                    addToContentPane(p2,null);
                }
                else if ("revalidate".equals(actionCommand)) {

                    p2.revalidate();
                    p2.repaint();
                }
                else {

                    System.out.println("unknown command "+actionCommand);
                }

    }
    private Panel p2;

}

/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.test;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.Spinner;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.TabbedPane;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.BevelBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.celleditor.DefaultCellEditor;
import net.yura.mobile.gui.cellrenderer.DefaultTabRenderer;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.Table;
import net.yura.mobile.gui.layout.GridLayout;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.plaf.nimbus.NimbusLookAndFeel;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.FileChooser;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 */
public class MainPane extends DesktopPane implements ActionListener {

	private Panel mainmenu;
        private Panel componentTest;
        private Panel componentTest2;
        private Panel info;
        private Panel border;
        private Panel tabPanel;
        private Menu menu;
        private Menu mainMenu;
        private Panel tableTest;
        
        private Icon image;
        private TextArea infoLabel,viewText,loadPanel;
	private Vector images;
	private Frame mainWindow;
	
        private SynthLookAndFeel synth;
        private MetalLookAndFeel metal;
        
	public MainPane(MyMidlet a) {
		super(a,0,null);
	}

	public void initialize() {
		
                metal = new MetalLookAndFeel();
                //setLookAndFeel( metal );
                setLookAndFeel( new NimbusLookAndFeel() );

                mainWindow = new Frame();
                
		mainWindow.setMaximum(true);
                
		//mainWindow.setActionListener(this);

                try {
                    image = new Icon("/world_link.png");
                }catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                mainWindow.setMenuBar(new MenuBar());
                mainWindow.setUndecorated(true);
                mainWindow.setVisible(true);
		actionPerformed("mainmenu");
		
	}
	
        private void addMainMenuButton(String a,String b) {
            
            Button infoButton = new Button(a);
            infoButton.setActionCommand(b);
            infoButton.addActionListener(this);
            mainmenu.add(infoButton);
        }
        
	public void actionPerformed(String actionCommand) {

		if ("exit".equals(actionCommand)) {
			
			exit();
			
		}
		else if ("mainmenu".equals(actionCommand)) {
			
			if (mainmenu==null) {
			
				mainmenu = new Panel( new FlowLayout(Graphics.VCENTER) );
				
				Label helloWorld = new Label("Test App Menu");
				mainmenu.add(helloWorld);
				
                                addMainMenuButton("Info","info");
				addMainMenuButton("Load","loadpanel");
				addMainMenuButton("View Text","viewText");
				addMainMenuButton("Error","throwerror");
                                addMainMenuButton("Component Test","componentTest");
                                addMainMenuButton("TextField Test","componentTest2");
                                addMainMenuButton("Border Test","borderTest");
                                addMainMenuButton("Tab Test","tabTest");
                                addMainMenuButton("Window Test","windowTest1");
                                addMainMenuButton("Option Pane Test","optionPaneTest");
                                addMainMenuButton("Table Test","tableTest");
                                addMainMenuButton("XUL Test","xulTest");
                                addMainMenuButton("XUL Test 2","xulTest2");
                                addMainMenuButton("File Chooser","fileChooser");

                                
                                mainMenu = new Menu("Menu");
                                //mainMenu.addActionListener(this);
                                addMenuItem(mainMenu,"metalTheme", "Metal Theme");
                                addMenuItem(mainMenu,"aether1", "Nimbus Default Theme");
                                addMenuItem(mainMenu,"aetherGreen", "Nimbus Green Theme");
                                addMenuItem(mainMenu,"aetherCharcoal", "Nimbus Charcoal Theme");
                                addMenuItem(mainMenu,"aether2", "Nimbus .Net Theme");
                                addMenuItem(mainMenu,"synthTheme1", "Synth Theme 1");
                                addMenuItem(mainMenu,"synthTheme2", "Synth Theme 2");
                                addMenuItem(mainMenu,"synthTheme3", "Synth Theme 3");
                                addMenuItem(mainMenu,"synthTheme4", "Synth Theme 4");
                                addMenuItem(mainMenu,"synthTheme5", "Synth Theme 5");
			}

			addToScrollPane(mainmenu, mainMenu, makeButton("Exit","exit") );
			
		}
                else if ("aether1".equals(actionCommand)) {
                    setupNewLookAndFeel( new NimbusLookAndFeel() );
                }
                else if ("aether2".equals(actionCommand)) {
                    System.out.println("not setup yet....");
                    //setupNewLookAndFeel( new NimbusLookAndFeel("net") );
                }
                else if ("aetherGreen".equals(actionCommand)) {
                    Hashtable styles = new Hashtable();
                    styles.put("nimbusBase", new Integer(0x00358c33));
                    NimbusLookAndFeel green = new NimbusLookAndFeel(Font.SIZE_MEDIUM,styles);
                    setupNewLookAndFeel( green );
                }
                else if ("aetherCharcoal".equals(actionCommand)) {
                    Hashtable styles = new Hashtable();
                    styles.put("nimbusBase", new Integer(0x00666666));
                    styles.put("nimbusGreyBlue", new Integer(0x00999999));
                    styles.put("control", new Integer(0x00bbbbbb));
                    NimbusLookAndFeel red = new NimbusLookAndFeel(Font.SIZE_MEDIUM,styles);
                    setupNewLookAndFeel( red );
                }
                else if ("metalTheme".equals(actionCommand)) {
                    
                    if (metal==null) {
                        metal = new MetalLookAndFeel();
                    }
                    setupNewLookAndFeel(metal);

                    
                }
                else if ("synthTheme1".equals(actionCommand)) {
                    loadSynthSkin("/synthdemo/synthDemo.xml");
                }
                else if ("synthTheme2".equals(actionCommand)) {
                    loadSynthSkin("/iphone/synth.xml");
                }
                else if ("synthTheme3".equals(actionCommand)) {
                    loadSynthSkin("/visto/synthVisto.xml");
                }
                else if ("synthTheme4".equals(actionCommand)) {
                    loadSynthSkin("/telus/synthVisto.xml");
                }
                else if ("synthTheme5".equals(actionCommand)) {
                    loadSynthSkin("/lg/synthLG.xml");
                }
                else if ("windowTest1".equals(actionCommand)) {

                    Frame test1 = new Frame("Window Title");
                    test1.setIconImage(image);
                    //test1.add( new TitleBar("Window Title",image,true,true,true,true,true),Graphics.TOP);

                    //test1.add(new Label("LALAL TEST 1"));
                    //test1.setBackground(0x00FFFFFF);

                    try {
                        XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/calculator.xml"), this);
                        test1.add( loader.getRoot() );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                    
                    // Test that pack method works too
                    //test1.pack();

                    //MenuBar bar = new MenuBar();

                    Menu foo = new Menu("foo");
                    foo.setMnemonic(KeyEvent.KEY_SOFTKEY1);
                    foo.add( new Button("hehehehe :)") );
                    //bar.add(foo);

                    test1.add(foo,Graphics.RIGHT);

                    test1.setBounds(10, 10, getWidth()-20, getHeight()/2);
                    test1.setVisible(true);
                    
                }
                else if ("xulTest".equals(actionCommand)) {

                    Panel panel = null;

                    try {
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/demo.xml"), this);
                        //XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/tabbedpane.xml"), this);
                        XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/generate.xml"), this);
                        panel = (Panel)loader.getRoot();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    addToScrollPane(panel, null, makeButton("Back","mainmenu") );
                }
                else if ("xulTest2".equals(actionCommand)) {

                    final Frame window = new Frame();

                    try {
                        XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/generate.xml"), new ActionListener() {
                            public void actionPerformed(String arg0) {
                                if ("ok()".equals(arg0)) {
                                    OptionPane.showMessageDialog(this, "you clicked ok", "info", OptionPane.INFORMATION_MESSAGE);

                                }
                                else if ("close()".equals(arg0) || "ok".equals(arg0)) {
                                    window.setVisible(false);
                                }
                                else if ("buttonOutputDirClicked()".equals(arg0)) {
System.out.println("open file browser");
                                }
                            }
                        });
                        window.setContentPane( (Panel)loader.getRoot() );
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    window.setMaximum(true);
                    window.setVisible(true);

                }
                else if ("info".equals(actionCommand)) {
			
			if (info==null) {
			
                                infoLabel = new TextArea("...",Graphics.HCENTER);
                                //infoLabel.setSize(getWidth(),infoLabel.getHeight());
                                infoLabel.setFocusable(false);
                                
				info = new Panel( new BorderLayout() ) {
                                        { focusable=true; }
                                  	public void pointerEvent(int type, int x, int y,KeyEvent keys) {
                                            super.pointerEvent(type, x, y, keys);
                                            infoLabel.setText("pointerEvent: "+x+","+y+"\n");
                                            switch(type) {
                                                case DesktopPane.DRAGGED: infoLabel.append("DRAGGED"); break;
                                                case DesktopPane.PRESSED: infoLabel.append("PRESSED"); break;
                                                case DesktopPane.RELEASED: infoLabel.append("RELEASED"); break;
                                                default: infoLabel.append("Unknown"); break;
                                            }
                                            infoLabel.repaint();

                                        }  
                                    	public boolean keyEvent(KeyEvent keypad) {
                                            int code1 = keypad.getJustPressedKey();
                                            int code2 = keypad.getJustReleasedKey();
                                            int code3 = keypad.getIsDownKey();
                                            int code = (code3==0)?code2:code3;
                                            if (code!=0) {
                                                infoLabel.setText("keyEvent: "+code +"\nKeyText: "+keypad.getKeyText(code));
                                                if (code>0) {
                                                    infoLabel.append("\nchar: "+(char)code);
                                                    
                                                    char inputChar = keypad.getKeyChar(code,KeyEvent.getChars( (char)code, TextField.ANY ), false);
                                                    if (inputChar!=0) {
                                                        infoLabel.append("\ninput char: "+ inputChar );
                                                    }
                                                }
                                                int gcode = keypad.getKeyAction(code);
                                                if (gcode!=0) {
                                                    infoLabel.append("\ngame action: "+gcode+"\n");
                                                    switch(gcode) {
                                                        case 1: infoLabel.append("UP"); break;
                                                        case 2: infoLabel.append("LEFT"); break;
                                                        case 5: infoLabel.append("RIGHT"); break;
                                                        case 6: infoLabel.append("DOWN"); break;
                                                        case 8: infoLabel.append("FIRE"); break;
                                                        case 9: infoLabel.append("GAME_A"); break;
                                                        case 10: infoLabel.append("GAME_B"); break;
                                                        case 11: infoLabel.append("GAME_C"); break;
                                                        case 12: infoLabel.append("GAME_D"); break;
                                                        default: infoLabel.append("Unknown"); break;
                                                    }
                                                }
                                                if (code1!=0) { infoLabel.append("\nJustPressed"); }
                                                if (code2!=0) { infoLabel.append("\nJustReleased"); }
                                                if (code3!=0) { infoLabel.append("\nIsDown"); }
                                                if (code1==0 && code3!=0) { infoLabel.append("\nHeldDown"); }
                                                infoLabel.repaint();
                                            }
                                            return true;
                                        }
                                };
                                
                                info.add( new Label("Info for keys and pointer"),Graphics.TOP );

                                info.add(infoLabel);

			}
			
			addToScrollPane(info, null,  makeButton("Back","mainmenu") );
			
		}
                else if ("componentTest".equals(actionCommand)) {
			
			if (componentTest==null) {
			
				componentTest = new Panel( new FlowLayout(Graphics.VCENTER) );

                                Menu testMain = new Menu("Menu");

                                addMenuItem(testMain,"t1","test 1");
                                addMenuItem(testMain,"t2","test 2");
                                addMenuItem(testMain,"t3","test 3");

                                Menu testMain2 = new Menu("sub Menu");

                                addMenuItem(testMain2,"t1","test 4");
                                addMenuItem(testMain2,"t2","test 5");
                                addMenuItem(testMain2,"t3","test 6");

                                testMain.add(testMain2);

				componentTest.add( new Label("a Label") );
                                if (image!=null) { componentTest.add( new Label( image ) ); }
                                Button b = new Button("a Button");
                                b.setToolTipText("A ToolTip for a button");
				componentTest.add( b );

                                Button b2 = new Button("disabled");
                                b2.setFocusable(false);
				componentTest.add( b2 );

                                componentTest.add(testMain);

                                componentTest.add( new CheckBox("a CheckBox") );
                                componentTest.add( new RadioButton("a RadioButton") );
                                
                                Vector items = new Vector();
                                items.addElement("One");
                                items.addElement(new Option("2","Two",image));
                                items.addElement(new Option("3","Three option"));
                                items.addElement(new Option("4",null,image,"(no text)"));

                                ComboBox disabledCombo = new ComboBox(items);
                                disabledCombo.setFocusable(false);

                                componentTest.add( new ComboBox(items) );
                                componentTest.add( disabledCombo );
                                componentTest.add( new Spinner(items, false));
                                
                                TextArea longText = new TextArea("a MultilineLabel with a very long bit of text that will need to go onto more than 1 line");
                                longText.setFocusable(false);
				longText.setLineWrap(true);
                                componentTest.add( longText );
                                
                                Panel email = new Panel( new FlowLayout() );
                                TextField t1 = new TextField();
                                TextField t2 = new TextField();
                                t1.setPreferredWidth(0.35);
                                t2.setPreferredWidth(0.35);
                                
                                email.add(t1);
                                email.add(new Label("@"));
                                email.add(t2);
                                
                                componentTest.add(email);
                                componentTest.add( new List(items,new DefaultListCellRenderer(),false) );
                                
                                menu = new Menu("Menu");
                                // menu has NO action listoner, so it fires NO action and ONLY opens the menu!
                                menu.add(new Button("bob"));
                                
                                Menu menu2 = new Menu("Sub");
                                //menu2.addActionListener(this);
                                menu2.add(new Button("fred"));
                                addMenuItem(menu2,"action","item (will close menu)");
                                menu.add(menu2);
                                
			}
			
			addToScrollPane(componentTest, menu, makeButton("Back","mainmenu") );
			
		}
                else if ("componentTest2".equals(actionCommand)) {
			
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
                        addToScrollPane(componentTest2, makeButton("Back","mainmenu"), null );
                }
		else if ("loadpanel".equals(actionCommand)) {
			
			if (loadPanel==null) {
				
				loadPanel = new TextArea();
                                loadPanel.setFocusable(false);
				loadPanel.setLineWrap(true);
				images = new Vector();

			}
			
			addToScrollPane(loadPanel, makeButton("Load","load") , makeButton("Back","mainmenu") );
		}
		else if ("viewText".equals(actionCommand)) {
			
			if (viewText==null) {
				

StringBuffer buf = new StringBuffer();


/*
InputStream inputStreamTxt=null;
try {
	inputStreamTxt = this.getClass().getResourceAsStream("/SynthME.dtd");
	int c ;
	while ((c = inputStreamTxt.read()) != -1)
	{buf.append((char)c);}
}
catch(Exception ex) {
	ex.printStackTrace();
	buf.append(ex.toString());
}
finally {
    if (inputStreamTxt!=null) {
	try {
		inputStreamTxt.close();
	}
	catch(Exception ex) { }
    }
}
*/
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
			}

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

			addToContentPane(p, makeButton("Back","mainmenu") , edit );

		}
                else if ("open_text_edit".equals(actionCommand)) {
                    
                    viewText.setFocusable( !viewText.isFocusable() );
                    mainWindow.repaint();
                    // TODO mainWindow.setupFocusedComponent();
                    
                }
                else if ("load".equals(actionCommand)) {
			
			Image testImage;
			String message;
			try {
				testImage = Image.createImage(500, 500);
				images.addElement(testImage);
				message = "loaded: "+testImage;
			}
			catch (Throwable e) {
				message = "unable to load: "+e.toString();
				e.printStackTrace();
			}
			loadPanel.append(message+"\n");

                        mainWindow.repaint();
		}
                else if ("borderTest".equals(actionCommand)) {
			
			if (border==null) {
				
				border = new Panel( new FlowLayout(Graphics.VCENTER) );
                                Label test1 = new Label("CompoundBorder test");
                                test1.setBorder( new CompoundBorder(
                                        new BevelBorder(4,0x00FF0000,0x0000FFFF),
                                        new CompoundBorder(
                                            new LineBorder( 0x0000FF00, 0x00FFFFFF,4,true), 
                                            new LineBorder(0x000000FF, 3))) );
                                border.add(test1);


                                Label test2 = new Label("ImageBorder test");
                                try {
                                    test2.setBorder(MatteBorder.load("/skin1.skin"));
                                }
                                catch(Exception ex){
                                    ex.printStackTrace();
                                }
                                border.add(test2);
                                
                                Panel menuTest = new Panel(new FlowLayout(Graphics.VCENTER,0));
                                try {
                                    menuTest.setBorder(MatteBorder.load("/skin2.skin"));
                                }
                                catch(Exception ex){
                                    ex.printStackTrace();
                                }
                                menuTest.add(new Button("menu TEST item 1"));
                                menuTest.add(new Button("menu TEST item 2"));
                                menuTest.add(new Button("menu TEST item 3"));
                                menuTest.add(new Button("menu TEST item 4"));
                                border.add(menuTest);
                                
                                menuTest = new Panel(new FlowLayout(Graphics.VCENTER,0));
                                menuTest.setBorder(new MatteBorder(10,20,30,40,image));
                                menuTest.add(new Button("MatteBorderTest"));
                                border.add(menuTest);
			}
			
			addToScrollPane(border, null , makeButton("Back","mainmenu") );
		}
                else if ("tabTest".equals(actionCommand)) {
			
			if (tabPanel==null) {
				
                                final TabbedPane tabbedPane = new TabbedPane();
                                
                                Panel tab1 = new Panel( new FlowLayout(Graphics.VCENTER) );
                                tab1.setName("Tab 1");
                                tab1.setBackground(0x00FF0000);
                                tab1.add( new Label("This is tab ONE") );
                                
                                final RadioButton top = new RadioButton("Top",true);
                                final RadioButton bottom = new RadioButton("Bottom");
                                final RadioButton right = new RadioButton("Right");
                                final RadioButton left = new RadioButton("Left");
                                
                                ActionListener tabSwap = new ActionListener() {
                                    public void actionPerformed(String actionCommand) {
                                        if (top.getText().equals(actionCommand)) {
                                            tabbedPane.setTabPlacement(Graphics.TOP);
                                        }
                                        else if (bottom.getText().equals(actionCommand)) {
                                            tabbedPane.setTabPlacement(Graphics.BOTTOM);
                                        }
                                        else if (right.getText().equals(actionCommand)) {
                                            tabbedPane.setTabPlacement(Graphics.RIGHT);
                                        }
                                        else if (left.getText().equals(actionCommand)) {
                                            tabbedPane.setTabPlacement(Graphics.LEFT);
                                        }
                                        tabPanel.getParent().revalidate();
                                        tabPanel.getParent().repaint();
                                    }
                                };
                                
                                ButtonGroup group = new ButtonGroup();
                                group.add(top);
                                group.add(bottom);
                                group.add(right);
                                group.add(left);
                                
                                top.addActionListener(tabSwap);
                                bottom.addActionListener(tabSwap);
                                right.addActionListener(tabSwap);
                                left.addActionListener(tabSwap);
                                
                                tab1.add(top);
                                tab1.add(bottom);
                                tab1.add(right);
                                tab1.add(left);
                                
                                Panel tab2 = new Panel( new FlowLayout() );
                                tab2.setBackground(0x0000FF00);

                                TabbedPane level2 = new TabbedPane();
                                level2.addTab("one", new Button("too?") );
                                level2.addTab("two", new Button("This is tab TWO 2") );

                                tab2.add( level2 );

                                Panel tab3 = new Panel( new FlowLayout(Graphics.VCENTER) );
                                List l3 = new List( new DefaultListCellRenderer() );
                                tab3.setBackground(0x000000FF);
                                
                                Vector anotherlist = new Vector();
                                
                                for (int c=0;c<20;c++) {
                                    anotherlist.addElement("A REALLY LONG LIST ITEM, that will need things like side scrolling "+c);
                                }
                                l3.setListData(anotherlist);
                                l3.setFixedCellHeight(15);
                                tab3.add(new Label("a lable for the list"));
                                tab3.add(l3);
                                
                                Panel tab4 = new Panel( new BorderLayout() );
                                tab4.add(new Label("Tab 4 title"),Graphics.TOP);
                                tab4.add(new ScrollPane(new List(anotherlist,new DefaultListCellRenderer(),false)));

                                tabbedPane.add(tab1);
                                tabbedPane.addTab("TAB 2", image, tab2,"i am a tooltip");
                                tabbedPane.addTab("eee", new ScrollPane(tab3));
                                tabbedPane.addTab(null,image,tab4);
                                
                                tabPanel = tabbedPane;
                                
			}
			
			addToContentPane(tabPanel, null , makeButton("Back","mainmenu") );
		}
                else if ("tableTest".equals(actionCommand)) {
                    
                    if (tableTest==null) {
                        
                        Vector options = new Vector();
                        options.addElement(new Option("yes","Happy",null,":-)"));
                        options.addElement(new Option("no","Sad"));
                        options.addElement(new Option("ok","OK"));
                        
                        Vector rows = new Vector();
                            Vector row1 = new Vector();
                            row1.addElement("YURA");
                            row1.addElement(new Integer(24));
                            row1.addElement(new Boolean(false));
                            row1.addElement(options.elementAt(0));
                        rows.addElement(row1);
                            
                            Vector row2 = new Vector();
                            row2.addElement("bob");
                            row2.addElement(new Integer(25));
                            row2.addElement(new Boolean(true));
                            row2.addElement(options.elementAt(1));
                        rows.addElement(row2);
                        
                            Vector row3 = new Vector();
                            row3.addElement("fred");
                            row3.addElement(new Integer(30));
                            row3.addElement(new Boolean(false));
                            row3.addElement(options.elementAt(2));
                        rows.addElement(row3);
                        
                            Vector row4 = new Vector();
                            row4.addElement( "thingy");
                            row4.addElement(new Integer(30));
                            row4.addElement(new Boolean(true));
                            row4.addElement(new Option("ok","OK"));
                        rows.addElement(row4);
                        
                        // copy pasty from DefaultListCellRenderer
                        class MyCheckBox extends CheckBox implements ListCellRenderer {

                            //private boolean sel,foc;
                            //private int colorNormal,colorSelected,foregroundNormal,foregroundSelected;
                            //protected Border normal,selected,focusedAndSelected;
                            private int state;
                            
                            public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                                
                                setSelected( value instanceof Boolean?((Boolean)value).booleanValue() : false );

                                // a checkbox can be TICKED, but this is NOT the same as the cell being selected
                                // so this isSelected is in ref to the cell in the table and not if the box is ticked or not
//                                sel = isSelected;
//                                foc = cellHasFocus;
//
//                                //setBorder(cellHasFocus?focusedAndSelected:(isSelected?selected:normal));
//                                setBackground(isSelected?colorSelected:colorNormal);
//                                setForeground(isSelected?foregroundSelected:foregroundNormal);

                                state=Style.ALL;
                                if ( list!=null ) {
                                    if (list.isFocusable()) {
                                        //state |= Style.ENABLED;
                                    }
                                    else {
                                        state |= Style.DISABLED;
                                    }
                                }
                                if (cellHasFocus) {
                                    state |= Style.FOCUSED;
                                }
                                if (isSelected) {
                                    state |= Style.SELECTED;
                                }


                                return this;
                            }

                            public int getCurrentState() {
                                return state;
                            }
//                            protected void paintBorder(Graphics2D g) {
//
//                                if (foc && activeBorder!=null) {
//                                    activeBorder.paintBorder(this, g, width, height);
//                                }
//                                else if (sel && selectedBorder!=null) {
//                                    selectedBorder.paintBorder(this, g, width, height);
//                                }
//                                else if (border!=null) {
//                                    border.paintBorder(this, g, width, height);
//                                }
//
//                            }

//                            public String getName() {
//                                return "ListRenderer";
//                            }
                            
//                            public void updateUI() {
//                                    super.updateUI();
//                                    Style st = DesktopPane.getDefaultTheme(this);
//
//                                    border = st.getBorder( Style.ENABLED );
//                                    activeBorder = st.getBorder( Style.FOCUSED | Style.SELECTED);
//                                    selectedBorder = st.getBorder( Style.SELECTED );
//
//                                    colorNormal = st.getBackground( Style.ALL );
//                                    colorSelected = st.getBackground( Style.SELECTED );
//
//                                    foregroundNormal = st.getForeground( Style.ALL );
//                                    foregroundSelected = st.getForeground( Style.SELECTED );
//                            }

                        }

                        Vector numbers = new Vector();
                        for (int c=0;c<100;c++) {
                            numbers.addElement(new Integer(c));
                        }

                        
                        Table table = new Table(rows,null) {
                            public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return (rowIndex!=3);
                            }
                        };
                        
                        table.setDefaultRenderer(Integer.class, new DefaultTabRenderer(Graphics.TOP));
                        table.setDefaultRenderer(Boolean.class, new MyCheckBox() );
                        
                        table.setDefaultEditor(Integer.class, new DefaultCellEditor( new Spinner(numbers,false) ) );
                        table.setDefaultEditor(Boolean.class, new DefaultCellEditor(new CheckBox()) );
                        table.setDefaultEditor(Option.class, new DefaultCellEditor(new ComboBox(options)) );
                        
                        // testing clip
//                        table.setColumnWidth(0, 100);
//                        table.setColumnWidth(1, 100);
//                        table.setColumnWidth(2, 100);
//                        table.setColumnWidth(3, 100);
//                        table.setRowHeight(100);
                        
                        tableTest = new Panel( new BorderLayout() );
                        
                        Panel top = new Panel(new FlowLayout());
                        top.add(new Label("Table!"));
                        top.add(new Button("button"));
                        
                        tableTest.add(top,Graphics.TOP);
                        tableTest.add(new ScrollPane(table));
		    }
                    
                    addToScrollPane(tableTest, null , makeButton("Back","mainmenu") );
                    
                }
		else if ("throwerror".equals(actionCommand)) {
			throw new RuntimeException("some bad error happened!");
		}
                else if ("optionPaneTest".equals(actionCommand)) {
                    
                    OptionPane.showMessageDialog(null,new Object[] {
                        "Hello, whats your name?",
                        //new TextArea("bob the builder"),
                        new TextField(TextField.ANY)
                    },"Title",0);
                }
                else if ("fileChooser".equals(actionCommand)) {

                    FileChooser chooser = new FileChooser();
                    chooser.showDialog(this, "fileSelected", "Select File", "Select");
                }
		else {
			
			System.out.println("Unknown Command: "+actionCommand);
		}
		
	}

        private Button makeButton(String label,String action) {
            Button button = new Button(label);
            System.out.println(action+" -> "+this);
            button.setActionCommand(action);
            button.addActionListener(this);
            return button;
        }

        private void addMenuItem(Menu menu, String action, String label) {
            menu.add(makeButton(label, action));
        }


        private void addToContentPane(Component a,Button b,Button c) {

            Panel pane = mainWindow.getContentPane();
            pane.removeAll();
            pane.add(a);

            setCommandButtons(b,c);
        }

	private ScrollPane scroll;
	private void addToScrollPane(Component a,Button b,Button c) {
		
		if (scroll==null) {
			
			scroll = new ScrollPane();
                        //Label label = new Label("yura.net Mobile");
                        //label.setHorizontalAlignment(Graphics.HCENTER);
                        //mainWindow.add(label,Graphics.TOP);
                        //mainWindow.add(scroll);
                        //mainWindow.add(new Label(""),Graphics.BOTTOM);
                        
		}
               	scroll.removeAll();
		scroll.add(a);

                addToContentPane(scroll,b,c);

	}

        private void setCommandButtons(Button b,Button c) {

                MenuBar bar = mainWindow.getMenuBar();
                bar.removeAll();
                if (b!=null) {
                    b.setMnemonic(KeyEvent.KEY_SOFTKEY1);
                    bar.add(b);
                }
                if (c!=null) {
                    c.setMnemonic(KeyEvent.KEY_SOFTKEY2);
                    bar.add(c);
                }

                mainWindow.revalidate();

		//setActiveComponent(getContentPane());
		// TODO mainWindow.setupFocusedComponent();

		mainWindow.repaint();
        }


    private void loadSynthSkin(String string) {

                    synth = new SynthLookAndFeel();

                    try {
                        synth.load(  getClass().getResourceAsStream(string) );
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    setupNewLookAndFeel( synth );

    }
/**
 * This is VERY far from perfect
 * but swing does it something like this
 * but it does NOT update rendererd
 * there is a HACK to update menus
 * and OptionPane will surely not be updated
 * @param theme
 * @see Menu
 * @see OptionPane
 * @see List
 */
    private void setupNewLookAndFeel(LookAndFeel theme) {
        setLookAndFeel(theme);

        componentTest=null;
        componentTest2=null;
        info=null;
        border=null;
        tabPanel=null;
        menu=null;
        tableTest=null;
        infoLabel=null;
        viewText=null;
        loadPanel=null;
        
        // not these as we have these open now
        //mainMenu=null;
        //mainmenu=null;
        
        updateComponentTreeUI(mainWindow);
        mainWindow.revalidate();
        mainWindow.repaint();
    }

}

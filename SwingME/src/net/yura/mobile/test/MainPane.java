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

package net.yura.mobile.test;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
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
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.components.TabbedPane;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.cellrenderer.DefaultTabRenderer;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.Table;
import net.yura.mobile.gui.components.TitleBar;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;
import net.yura.mobile.util.ButtonGroup;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 */
public class MainPane extends DesktopPane implements ActionListener {

	private Panel mainmenu;
        private Panel componentTest;
        private Panel info;
        private Panel border;
        private Panel tabPanel;
        private Menu menu;
        private Table tableTest;
        
        private Image image;
        private TextArea infoLabel;
	private TextArea loadPanel;
	private Vector images;
	private Window mainWindow;
	
	public MainPane(MyMidlet a) {
		super(a,0,null);
	}

	public void initialize() {
		

		
//                SynthLookAndFeel th = new SynthLookAndFeel();
//                try {
//                    th.load(  getClass().getResourceAsStream("/synthDemo.xml") );
//                }
//                catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//		setLookAndFeel( th );

                setLookAndFeel( new MetalLookAndFeel() ); 

                mainWindow = new Window();
                
		mainWindow.setBoundsWithBorder(0, 0, getWidth(), getHeight());
                
		mainWindow.setActionListener(this);

                try {
                    image = Image.createImage("/world_link.png");
                }catch (IOException ex) {
                    ex.printStackTrace();
                }
                
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
				addMainMenuButton("Error","throwerror");
                                addMainMenuButton("Component Test","componentTest");
                                addMainMenuButton("Border Test","borderTest");
                                addMainMenuButton("Tab Test","tabTest");
                                addMainMenuButton("Window Test 1","windowTest1");
			}
			
			addToScrollPane(mainmenu, null, new CommandButton("Exit","exit") );
			
		}
                else if ("windowTest1".equals(actionCommand)) {

                    Window test1 = new Window();
                    test1.getContentPane().add( new TitleBar("Window Title",image,true,true,true,true,true),Graphics.TOP);
                    test1.getContentPane().add(new Label("LALAL TEST 1"));
                    test1.getContentPane().setBackground(0x00FFFFFF);
                    
                    test1.setBounds(10, 10, getWidth()-20, getHeight()/2);
                    
                    // Test that pack method works too
                    //test1.pack();
                    
                    add(test1);
                    
                }
                else if ("info".equals(actionCommand)) {
			
			if (info==null) {
			
                                infoLabel = new TextArea("...");
                                infoLabel.setSize(getWidth(),infoLabel.getHeight());
                                
				info = new Panel( new BorderLayout() ) {
                                        { selectable=true; }
                                  	public void pointerEvent(int type, int x, int y) {
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
                                                    
                                                    char inputChar = keypad.getKeyChar(0, false);
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
			
			addToScrollPane(info, null,  new CommandButton("Back","mainmenu") );
			
		}
                else if ("componentTest".equals(actionCommand)) {
			
			if (componentTest==null) {
			
				componentTest = new Panel( new FlowLayout(Graphics.VCENTER) );

				componentTest.add( new Label("a Label") );
                                if (image!=null) { componentTest.add( new Label( image ) ); }
				componentTest.add( new Button("a Button") );
                                componentTest.add( new CheckBox("a CheckBox") );
                                componentTest.add( new RadioButton("a RadioButton") );
                                
                                Vector items = new Vector();
                                items.addElement("One");
                                items.addElement(new Option("2","Two",image));
                                items.addElement(new Option("3","Three option"));
                                items.addElement(new Option("4",null,image));
                                
                                componentTest.add( new ComboBox(items) );
                                componentTest.add( new Spinner(items, false));
                                
                                componentTest.add( new TextField(javax.microedition.lcdui.TextField.NUMERIC) );
                                componentTest.add( new TextField(javax.microedition.lcdui.TextField.ANY) );
                                componentTest.add( new TextField(javax.microedition.lcdui.TextField.ANY | javax.microedition.lcdui.TextField.PASSWORD) );
                                
                                componentTest.add( new TextArea("a MultilineLabel with a very long bit of text that will need to go onto more then 1 line") );
                                
                                componentTest.add( new List(items,new DefaultListCellRenderer(),false) );
                                
                                menu = new Menu("Menu");
                                menu.add(new Button("bob"));
                                
                                Menu menu2 = new Menu("Sub");
                                menu2.addActionListener(this);
                                menu2.add(new Button("fred"));
                                menu2.addMenuItem("action","item (will close menu)", null);
                                menu.add(menu2);
                                
			}
			
			addToScrollPane(componentTest, new CommandButton(menu,"popmenu"),  new CommandButton("Back","mainmenu") );
			
		}
		else if ("loadpanel".equals(actionCommand)) {
			
			if (loadPanel==null) {
				
				loadPanel = new TextArea("");
				loadPanel.setAlignment(Graphics.LEFT);
				images = new Vector();
                                //loadPanel.setSize(getWidth()*2, loadPanel.getHeight());
			}
			
			addToScrollPane(loadPanel, new CommandButton("Load","load") , new CommandButton("Back","mainmenu") );
			//setActiveComponent(loadPanel);
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
			//loadPanel.setSize( getWidth()-ScrollPane.getBarThickness(getWidth(), getHeight()) , loadPanel.getHeight());
			loadPanel.append(message+"\n");
			//getContentPane().doLayout();
                        //mainWindow.getContentPane().revalidate();
			mainWindow.getContentPane().repaint();
		}
                else if ("borderTest".equals(actionCommand)) {
			
			if (border==null) {
				
				border = new Panel( new FlowLayout(Graphics.VCENTER) );
                                Label test1 = new Label("CompoundBorder test");
                                test1.setBorder( new CompoundBorder(
                                        new LineBorder(0x00FF0000, 3),
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
			
			addToScrollPane(border, null , new CommandButton("Back","mainmenu") );
		}
                else if ("tabTest".equals(actionCommand)) {
			
			if (tabPanel==null) {
				
                                final TabbedPane tabbedPane = new TabbedPane();
                            
				tabPanel = new Panel( new BorderLayout() );
                                tabPanel.add(new Label("Tab Test"),Graphics.TOP);
                                
                                Panel tab1 = new Panel( "Tab 1" );
                                tab1.setLayout(new FlowLayout(Graphics.VCENTER));
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
                                        tabPanel.revalidate();
                                        tabPanel.repaint();
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
                                tab2.add( new Button("This is tab TWO") );

                                Panel tab3 = new Panel( new FlowLayout(Graphics.VCENTER) );
                                List l3 = new List( new DefaultListCellRenderer() );
                                tab3.setBackground(0x000000FF);
                                
                                Vector anotherlist = new Vector();
                                
                                for (int c=0;c<20;c++) {
                                    anotherlist.addElement("A REALLY LONG LIST ITEM, that will need things like side scrolling "+c);
                                }
                                l3.setListData(anotherlist);
                                tab3.add(new Label("a lable for the list"));
                                tab3.add(l3);
                                
                                Panel tab4 = new Panel( new BorderLayout() );
                                tab4.add(new Label("Tab 4 title"),Graphics.TOP);
                                tab4.add(new ScrollPane(new List(anotherlist,new DefaultListCellRenderer(),false)));

                                tabbedPane.add(tab1);
                                tabbedPane.addTab("TAB 2", image, tab2);
                                tabbedPane.addTab("tab 3", new ScrollPane(tab3));
                                tabbedPane.addTab(null,image,tab4);
                                
                                tabPanel.add(tabbedPane);
                                
			}
			
			addToScrollPane(tabPanel, null , new CommandButton("Back","mainmenu") );
		}
                else if ("tableTest".equals(actionCommand)) {
                    
                    if (tableTest==null) {
                        
                        Vector rows = new Vector();
                            Vector row1 = new Vector();
                            row1.addElement("YURA");
                            row1.addElement(new Integer(24));
                            row1.addElement("175");
                            row1.addElement(new Option("yes","Happy"));
                        rows.addElement(row1);
                            
                            Vector row2 = new Vector();
                            row2.addElement("bob");
                            row2.addElement(new Integer(25));
                            row2.addElement("170");
                            row2.addElement(new Option("yes","Happy"));
                        rows.addElement(row2);
                        
                            Vector row3 = new Vector();
                            row3.addElement("fred");
                            row3.addElement(new Integer(30));
                            row3.addElement("173");
                            row3.addElement(new Option("yes","Happy"));
                        rows.addElement(row3);
                        
                        class MyCheckBox extends CheckBox {
                            
                            private Option myOption;
                            
                            public MyCheckBox() {
                                super("?");
                            }
                            
                            public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column) {
                                
                                myOption = (Option)value;
                                
                                setSelected( "yes".equalsIgnoreCase(myOption.getId()) );
                                return this;
                            }

                            public Object getCellEditorValue() {
                                
                                myOption.setId( isSelected()?"yes":"no" );
                                
                                return myOption;
                            }

                        }
                        
                        
                        class MyTextField extends TextField {
                            
                            public MyTextField() {
                                super(TextField.ANY);
                            }
                            
                            public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column) {
                                setText( value.toString() );
                                return this;
                            }

                            public Object getCellEditorValue() {
                                return getText();
                            }

                        }
                        

                        Vector numbers = new Vector();
                        for (int c=0;c<100;c++) {
                            numbers.addElement(new Integer(c));
                        }
                        Spinner spinner = new Spinner(numbers,false);
                        
                        Vector editors = new Vector();
                        editors.addElement(new MyTextField());
                        editors.addElement(spinner);
                        editors.addElement(new TextField(TextField.ANY));
                        editors.addElement(new MyCheckBox());
                        
                        Vector renderers = new Vector();
                        renderers.addElement(new Label());
                        renderers.addElement(spinner);
                        renderers.addElement(new DefaultListCellRenderer());
                        renderers.addElement(new DefaultTabRenderer(Graphics.TOP));
                        
                        tableTest = new Table(rows,null);
                        tableTest.setDefaultEditors(editors);
                        tableTest.setDefaultRenderer(renderers);
		    }
                    
                    addToScrollPane(tableTest, null , new CommandButton("Back","mainmenu") );
                    
                }
		else if ("throwerror".equals(actionCommand)) {
			throw new RuntimeException("some bad error happened!");
		}
		else {
			
			System.out.println("Unknown Command: "+actionCommand);
		}
		
	}
	
	private ScrollPane scroll;
	private void addToScrollPane(Component a,CommandButton b,CommandButton c) {
		

		
		if (scroll==null) {
			
			scroll = new ScrollPane();
                        Label label = new Label("yura.net Mobile");
                        label.setHorizontalAlignment(Graphics.HCENTER);
                        mainWindow.getContentPane().add(label,Graphics.TOP);
                        mainWindow.getContentPane().add(scroll);
                        mainWindow.getContentPane().add(new Label(""),Graphics.BOTTOM);
		}
		
               	scroll.removeAll();
		scroll.add(a);

		mainWindow.getContentPane().revalidate();
		
		mainWindow.setWindowCommand(0, b);
		mainWindow.setWindowCommand(1, c);
		
		//setActiveComponent(getContentPane());
		mainWindow.setupFocusedComponent();
		
		mainWindow.repaint();
	}

}

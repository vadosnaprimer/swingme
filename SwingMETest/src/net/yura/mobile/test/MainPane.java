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
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

//import javax.microedition.lcdui.Font;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.BevelBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.nimbus.NimbusLookAndFeel;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.TitledBorder;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.components.TextPane;


/**
 * @author Yura Mamyrin
 */
public class MainPane extends DesktopPane implements ActionListener {

	private Panel mainmenu;
        private Panel info;
        private Panel border;
        private Menu mainMenu;

        public Icon image;
        private TextPane infoLabel;
        private TextArea loadPanel;
	private Vector images;
	private Frame mainWindow;

        private SynthLookAndFeel synth;
        private MetalLookAndFeel metal;

        Frame xuldialog;

        private TextArea sysoutArea;











	public MainPane(MyMidlet a) {
		super(a,0,null);
	}

	public void initialize() {

                metal = new MetalLookAndFeel();
                //setLookAndFeel( metal );
//                Hashtable settings = new Hashtable();
//                settings.put("font", new Font("/font/test_0.png", "/font/test.fnt"));
//                settings.put("Button.foreground", new Integer(0x00ff0000));
                setLookAndFeel( new NimbusLookAndFeel(16) );

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

        private Section addSection(String a,Section section) {

            section.mainPane = this;

            mainmenu.add( section.makeButton(a, "openSection") );
            return section;

        }

        private void addMainMenuButton(String a,String b) {

            Button infoButton = new Button(a);
            infoButton.setActionCommand(b);
            infoButton.addActionListener(this);
            mainmenu.add(infoButton);
        }

        public abstract static class Section extends Panel implements ActionListener {

            protected MainPane mainPane;

            public Section() {
                super( new FlowLayout( Graphics.VCENTER ) );
                createTests();
            }

            public void actionPerformed(String arg0) {

                if ("openSection".equals(arg0)) {

                    mainPane.addToScrollPane(this, null , mainPane.makeButton("Back","mainmenu") );

                }
                else {
                    //System.out.println("unknow command "+arg0);
                    openTest(arg0);
                }

            }

            public void addTest(String name, String id) {

                Button infoButton = new Button(name);
                infoButton.setActionCommand(id);
                infoButton.addActionListener(this);
                add(infoButton);
            }

            public void addToScrollPane(Component p,Button b) {
                mainPane.addToScrollPane(p, b, makeButton("Back","openSection")) ;

            }
            public void addToContentPane(Component p,Button b) {
                mainPane.addToContentPane(p, b, makeButton("Back","openSection")) ;
            }

            public abstract void createTests();
            public abstract void openTest(String id);

            public Button makeButton(String label,String action) {
                Button button = new Button(label);
                button.setActionCommand(action);
                button.addActionListener(this);
                return button;
            }
        }

	public void actionPerformed(String actionCommand) {

		if ("exit".equals(actionCommand)) {

			Midlet.exit();

		}
		else if ("mainmenu".equals(actionCommand)) {

			if (mainmenu==null) {

				mainmenu = new Panel( new FlowLayout(Graphics.VCENTER) );

				Label helloWorld = new Label("Test App Menu");
				mainmenu.add(helloWorld);

                                // add test sections

                                addMainMenuButton("Info","info");

                                addSection("Component Test",new ComponentTest());
                                addSection("Text Test",new TextTest());
                                addSection("Layout Test",new LayoutTest());

                                addMainMenuButton("Border Test","borderTest");
                                addMainMenuButton("Font Test","fontTest");

                                addSection("ServiceLink Test",new ServiceLinkTest());

                                try {
                                    addSection("BlueTooth Test",new BlueToothTest());
                                }
                                catch(Throwable th) {
                                    mainmenu.add( new Label("no BlueTooth") );
                                    mainmenu.add( new Label( th.toString() ) );
                                }

				addMainMenuButton("Load Images","loadpanel");
				addSection("Graphics Test",new GraphicsTest());
				addMainMenuButton("Throw Error","throwerror");
                                addMainMenuButton("System.out", "sysout");
                                addMainMenuButton("Hide Show", "hideshow");

                                // add theme swap test

                                mainMenu = new Menu("Menu");
                                //mainMenu.addActionListener(this);
                                addMenuItem(mainMenu,"metalTheme", "Metal Theme");
                                addMenuItem(mainMenu,"aether1", "Nimbus Default Theme");
                                addMenuItem(mainMenu,"aetherGreen", "Nimbus Green Theme");
                                addMenuItem(mainMenu,"aether2", "Nimbus Red Theme");
                                addMenuItem(mainMenu,"aetherCharcoal", "Nimbus Charcoal Theme");
                                addMenuItem(mainMenu,"synthTheme1", "Synth J2SE Demo");
                                addMenuItem(mainMenu,"synthTheme2", "Synth iPhone");
                                addMenuItem(mainMenu,"synthTheme3", "Synth Visto");
                                addMenuItem(mainMenu,"synthTheme4", "Synth Telus");
                                addMenuItem(mainMenu,"synthTheme5", "Synth LG");
			}

			addToScrollPane(mainmenu, mainMenu, makeButton("Exit","exit") );

                        MenuBar bar = mainWindow.getMenuBar();
                        //bar.insert(new Label("a"), 0);
                        //bar.insert(new Label("b"), 2);
                        //bar.insert(new Label("c"), 4);
                        bar.addElement( new Label(image) );

		}
                else if ("hideshow".equals(actionCommand)) {

                    final Display display = Display.getDisplay(Midlet.getMidlet());
                    final DesktopPane dp = DesktopPane.getDesktopPane();

                    Midlet.hide();

                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            }
                            catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            //dp.callSerially(this);
                            display.setCurrent( dp );
                        }
                    }.start();
                }
                else if ("sysout".equals(actionCommand)) {

                    if (sysoutArea==null) {

                        sysoutArea = new TextArea();
                        sysoutArea.setLineWrap(true);
                        sysoutArea.setFocusable(false);

                        new Thread() {
                            public void run() {
                                try {
                                    InputConnection inputConnection = (InputConnection)Connector.open("redirect://", 1);
                                    InputStream inputStream = inputConnection.openInputStream();
                                    byte buffer[] = new byte[256];
                                    int len = -1;
                                    do {
                                        len = inputStream.read(buffer);
                                        if(len > 0) {
                                            String s = new String(buffer, 0, len);
                                            sysoutArea.append(s);
                                        }
                                    } while(true);
                                    //inputStream.close();
                                }
                                catch(Exception ee)
                                {
                                    ee.printStackTrace();
                                }
                            }

                        }.start();
                    }

                    addToScrollPane(sysoutArea, null,  makeButton("Back","mainmenu") );

                }
                else if ("aether1".equals(actionCommand)) {
                    setupNewLookAndFeel( new NimbusLookAndFeel() );
                }
                else if ("aether2".equals(actionCommand)) {
                    Hashtable styles = new Hashtable();
                    styles.put("nimbusBase", new Integer(0x008c3533));
                    NimbusLookAndFeel red = new NimbusLookAndFeel(javax.microedition.lcdui.Font.SIZE_MEDIUM,styles);
                    setupNewLookAndFeel( red );
                }
                else if ("aetherGreen".equals(actionCommand)) {
                    Hashtable styles = new Hashtable();
                    styles.put("nimbusBase", new Integer(0x00358c33));
                    NimbusLookAndFeel green = new NimbusLookAndFeel(javax.microedition.lcdui.Font.SIZE_MEDIUM,styles);
                    setupNewLookAndFeel( green );
                }
                else if ("aetherCharcoal".equals(actionCommand)) {
                    Hashtable styles = new Hashtable();
                    styles.put("nimbusBase", new Integer(0x00666666));
                    styles.put("nimbusGreyBlue", new Integer(0x00999999));
                    styles.put("control", new Integer(0x00bbbbbb));
                    NimbusLookAndFeel charcoal = new NimbusLookAndFeel(javax.microedition.lcdui.Font.SIZE_MEDIUM,styles);
                    setupNewLookAndFeel( charcoal );
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

                else if ("info".equals(actionCommand)) {

			if (info==null) {

                                infoLabel = new TextPane();
                                infoLabel.setText("...");
                                final TextPane.TextStyle center = new TextPane.TextStyle();
                                center.setAlignment(Graphics.HCENTER);
                                infoLabel.setParagraphAttributes(0, 0, center);
                                //infoLabel.setSize(getWidth(),infoLabel.getHeight());
                                infoLabel.setFocusable(false);

				info = new Panel( new BorderLayout() ) {
                                        { focusable=true; }
                                  	public void processMouseEvent(int type, int x, int y,KeyEvent keys) {
                                            super.processMouseEvent(type, x, y, keys);
                                            infoLabel.setText("pointerEvent: "+x+","+y+"\n");
                                            switch(type) {
                                                case DesktopPane.DRAGGED: infoLabel.append("DRAGGED"); break;
                                                case DesktopPane.PRESSED: infoLabel.append("PRESSED"); break;
                                                case DesktopPane.RELEASED: infoLabel.append("RELEASED"); break;
                                                default: infoLabel.append("Unknown"); break;
                                            }
                                            infoLabel.setParagraphAttributes(0, infoLabel.getText().length(), center);
                                            infoLabel.getParent().revalidate();
                                            infoLabel.getParent().repaint();

                                        }
                                    	public boolean processKeyEvent(KeyEvent keypad) {

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

                                                infoLabel.setParagraphAttributes(0, infoLabel.getText().length(), center);
                                                infoLabel.getParent().revalidate();
                                                infoLabel.getParent().repaint();
                                            }
                                            return true;
                                        }
                                };

                                info.add( new Label("Info for keys and pointer"),Graphics.TOP );

                                info.add(infoLabel);

			}

			addToScrollPane(info, null,  makeButton("Back","mainmenu") );

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
                                    test2.setBorder(new TitledBorder(MatteBorder.load("/skin1.skin"), "Title", test2.getFont()));
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

                else if ("fontTest".equals(actionCommand)) {
                    Panel panel = new Panel( new FlowLayout( Graphics.VCENTER ) );



		    String[] labels = {
			    "abcdefgh",
			    "ijklmnopqr",
			    "stuvwxyz",
			    "ABCDEFGH",
			    "IJKLMNOPQR",
			    "STUVWXYZ",
			    "!$#|@/\\\"':;!_-",

		    };

		    Font[] fonts = {
			Font.getFont("/font/test.fnt",new String[] { "/font/test.png" }, new int[] {0x00FFFFFF }),
			Font.getFont("/font/calibri.fnt", new String[] { "/font/calibri.png" },new int[] {0x00000000 } ),

			Font.getFont("/basicfont/font1-small.font"),
			Font.getFont("/basicfont/font1-med.font"),
			Font.getFont("/basicfont/font1-large.font"),

			Font.getFont("/basicfont/font2-small.font"),
			Font.getFont("/basicfont/font2-med.font"),
			Font.getFont("/basicfont/font2-large.font"),

			Font.getFont("/basicfont/treasure.font"),
		    };

		    int colors[] = {

			    0x00000000,
			    0x00FFFFFF,
			    0x00FF0000,
			    0x0000FF00,
			    0x000000FF,
                            0x00FF00FF,
                            0x00FFFF00

		    };

     		    for(int l = 0; l < labels.length; l++) {
			Label label = new Label(labels[l]);
			panel.add(label);

		    }

		    for(int f = 0; f < fonts.length; f++ ) {

			panel.add(new Label("Font " + f));

			int color = 0;

			for(int l = 0; l < labels.length; l++) {

				if(color == colors.length)
					color = 0;

				    Button button = new Button(labels[l]);
				    button.setFont(fonts[f]);
				    button.setForeground(colors[color]);
				    panel.add(button);


				color++;
			}
		    }

                    Font treasure = Font.getFont("/basicfont/treasure.font");

		    TextArea t = new TextArea("/|\\/|\\/|\\/|\\\n|||||||||\nWwWwWwWwW\n~~~~~~~~~\n_-_-_-_-_\n||||||||||");
		    t.setLineWrap(true);
		    t.setFont(treasure);
		    panel.add(t);
                    addToScrollPane(panel, null, makeButton("Back","mainmenu") );
                }
		else if ("throwerror".equals(actionCommand)) {
			throw new RuntimeException("some bad error happened!");
		}
		else {
                    System.out.println("Unknown Command: "+actionCommand);
		}

	}

        private Button makeButton(String label,String action) {
            Button button = new Button(label);
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
            pane.add(new Label("yura.net Mobile"), Graphics.TOP);

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

        mainmenu=null;
        mainMenu=null;
        actionPerformed("mainmenu");

        updateComponentTreeUI(mainWindow);
        mainWindow.revalidate();
        mainWindow.repaint();
    }

}

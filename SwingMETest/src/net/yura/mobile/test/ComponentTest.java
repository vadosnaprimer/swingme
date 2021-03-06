/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.BevelBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.border.TitledBorder;
import net.yura.mobile.gui.celleditor.DefaultCellEditor;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Camera;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.FileChooser;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.ImageView;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.PageView;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ProgressBar;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.Slider;
import net.yura.mobile.gui.components.Spinner;
import net.yura.mobile.gui.components.TabbedPane;
import net.yura.mobile.gui.components.Table;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.io.FileUtil;
import net.yura.mobile.io.HTTPClient;
import net.yura.mobile.test.MainPane.Section;
import net.yura.mobile.util.ImageUtil;
import net.yura.mobile.util.Option;

/**
 *
 * @author Administrator
 */
public class ComponentTest  extends Section{

    private Panel componentTest;
    private Camera cameraPanel;
    private Panel tableTest;
    private Menu menu;
    private Panel tabPanel;
    FileChooser chooser1;

    public void createTests() {
        addTest("Component Test","componentTest");
        addTest("Tab Test","tabTest");
        addTest("Window Test","windowTest1");
        addTest("Option Pane Test 1","optionPaneTest1");
        addTest("Option Pane Test 2","optionPaneTest");
        addTest("Table Test","tableTest");
        addTest("Scroll Test 1","scrollTest1");
        addTest("Scroll Test 2","scrollTest2");
        addTest("Scroll Test 3","scrollTest3");
        addTest("File Chooser","fileChooser");
        addTest("Multi File Chooser","fileChooser2");
        addTest("Test Camera","testCamera");
        addTest("Test Progress","testProgress");
        addTest("Page View", "pageView");
        addTest("Test Gallery w > h", "testImageView1");
        addTest("Test Gallery h > w", "testImageView2");

    }

    public void actionPerformed(String actionCommand) {
        if ("openSection".equals(actionCommand)) {
            if (cameraPanel != null) {
                cameraPanel.close();
                cameraPanel = null;
            }
        }

        super.actionPerformed(actionCommand);
    }

    private Vector makeEnabledComponents() {

        Vector components = new Vector();

        Menu testMain = new Menu("Menu");

        testMain.add(new Button("test 1"));
        testMain.add(new Button("test 2 lalalalalal"));
        testMain.add(new Button("test 3"));
        testMain.addSeparator();
        testMain.add(new RadioButton("test ra"));
        testMain.add(new CheckBox("test ch"));

        Button off = new Button("disabled");
        off.setFocusable(false);
        testMain.add(off);

        Menu testMain2 = new Menu("sub Menu");

        testMain2.add(new Button("test 4"));
        testMain2.add(new Button("test 5"));
        testMain2.add(new Button("test 6"));

        testMain.add(testMain2);


        Button b = new Button("a Button");
        b.setToolTipText("A ToolTip for a button");
        components.addElement( b );

        components.addElement(testMain);

        CheckBox cb = new CheckBox("a CheckBox");
        cb.setUseSelectButton(true);
        components.addElement( cb );

        RadioButton rb = new RadioButton("a RadioButton");
        rb.setUseSelectButton(true);
        components.addElement( rb );

        components.addElement( new ComboBox(getItems()) );
        components.addElement( new Spinner(getItems(), true));

        final TextField textField = new TextField(TextField.ANY);

        textField.addActionListener( new ActionListener() {
            public void actionPerformed(String actionCommand) {
                OptionPane.showMessageDialog(null, "yay", "message", OptionPane.INFORMATION_MESSAGE);
            }
        });

        components.addElement(textField);

        Slider slider = new Slider(0, 7, 4);
 	slider.setPaintTicks(true);
 	slider.setMajorTickSpacing(3);
 	slider.setMinorTickSpacing(1);
 	//slider.setPaintLabels( true );
 	slider.setSnapToTicks( true );

        slider.setPreferredSize(200, -1);
        slider.setBorder( new LineBorder(0xFFFF0000) );

        components.addElement(slider);

        slider.addChangeListener(new ChangeListener() {
            public void changeEvent(Component arg0, int arg1) {
                textField.setText( String.valueOf(arg1) );
            }
        });

        Slider slider2 = new Slider(0, 11, 6);
 	slider2.setPaintTicks(true);
 	slider2.setMajorTickSpacing(5);
 	slider2.setMinorTickSpacing(1);
 	//slider2.setPaintLabels( true );
 	slider2.setSnapToTicks( true );

        slider2.setHorizontal(false);
        slider2.setPreferredSize(-1,100);
        components.addElement(slider2);

        slider2.setExtent(3);

        components.addElement( new List( getItems() ,new DefaultListCellRenderer(),List.VERTICAL) );

        return components;
    }

    private Vector getItems() {

                Vector items = new Vector();
                items.addElement("One");
                items.addElement(new Option("2","Two",mainPane.image));
                items.addElement(new Option("3","Three option"));
                items.addElement(new Option("4",null,mainPane.image,"(no text)"));
                items.addElement("");
                items.addElement(null);
                return items;

    }


    public void openTest(String actionCommand) {

        if ("componentTest".equals(actionCommand)) {

            if (componentTest==null) {

                componentTest = new Panel( new FlowLayout(Graphics.VCENTER) );

                Panel panel1 = new Panel( new FlowLayout(Graphics.VCENTER) );
                Vector v1 = makeEnabledComponents();
                for (int c=0;c<v1.size();c++) {
                    panel1.add( (Component)v1.elementAt(c) );
                }

                Panel panel2 = new Panel( new FlowLayout(Graphics.VCENTER) );
                Vector v2 = makeEnabledComponents();
                for (int c=0;c<v2.size();c++) {
                    Component co = (Component)v2.elementAt(c);
                    co.setFocusable(false);
                    panel2.add( co );
                }


                componentTest.add(panel1);
                componentTest.add(panel2);

                Label l = new Label("a Label");
                l.setToolTipText("a label can have a tool tip too");
                componentTest.add( l );
                if (mainPane.image!=null) { componentTest.add( new Label( mainPane.image ) ); }

                TextArea longText = new TextArea("a MultilineLabel with a very long bit of text that will need to go onto more than 1 line");
                longText.setFocusable(false);
                longText.setLineWrap(true);
                componentTest.add( longText );

                Panel email = new Panel( new FlowLayout() );
                TextField t1 = new TextField();
                TextField t2 = new TextField();
                //t1.setPreferredWidth(0.35);
                //t2.setPreferredWidth(0.35);

                email.add(t1);
                email.add(new Label("@"));
                email.add(t2);

                email.setBorder( new BevelBorder(1, 0xFFFFFFFF, 0xFF000000) );

                componentTest.add(email);

                menu = new Menu("Menu");
                // menu has NO action listoner, so it fires NO action and ONLY opens the menu!
                menu.add(new Button("bob"));

                final Menu menu2 = new Menu("Sub");

                menu2.addActionListener( new ActionListener() {
                        // Override
                        public void actionPerformed(String actionCommand) {
                                System.out.println("adding items to submenu");
                                menu2.removeAll();
		                menu2.add(new Button("fred"));
		                menu2.add(new Button("item 2"));
                        }
                });

                menu.add(menu2);

                menu.add(new Button("bob test 1"));
                menu.add(new Button("bob test 2"));

            }

            addToScrollPane(componentTest, menu );

        }
        else if ("scrollTest1".equals(actionCommand)) {
            Panel p = new Panel( new FlowLayout(Graphics.VCENTER) );
            for (int c=0;c<30;c++) {
                Button b = new Button("hello "+c);
                b.setFocusable(false);
                p.add(b);
            }


            Button w = makeButton("W", "windowTest1");

            addToScrollPane(p,w);
        }
        else if ("windowTest1".equals(actionCommand)) {

            Frame test1 = new Frame("Window Title");
            test1.setIconImage(mainPane.image);
            //test1.add( new TitleBar("Window Title",image,true,true,true,true,true),Graphics.TOP);

            //test1.add(new Label("LALAL TEST 1"));
            //test1.setBackground(0xFFFFFFFF);

            try {
                XULLoader loader = XULLoader.load(getClass().getResourceAsStream("/calculator.xml"), this);
                test1.add( loader.getRoot() );
                loader.getRoot().setBackground(0xFF00FF00);
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

            Button close = new Button("Close");
            close.setActionCommand(Frame.CMD_CLOSE);
            // hack to avoid having to make a new action listener
            close.addActionListener(test1.getTitlePane());
            close.setMnemonic(KeyEvent.KEY_SOFTKEY2);

            Panel p = new Panel( new FlowLayout() );
            p.setBackground(0xFF0000FF);
            p.add(foo);
            p.add(close);

            test1.add(p,Graphics.BOTTOM);

            test1.setMaximizable(false);
            test1.setClosable(false);

            test1.setClosable(true);
            test1.setMaximizable(true);

            test1.setBounds(10, 10, getWidth()-20, getHeight()/2);
            test1.setVisible(true);

        }
        else if ("scrollTest2".equals(actionCommand)) {

            Panel scrollTest = new Panel(new FlowLayout(Graphics.VCENTER));

            String s  = "sdfg\n\n\n\n\n\n\n\n\n\na\n\nb\n\n\n\n\n\nc\n\n\n\na\n\nb\n\n\n\n\n\nc\n\n\n\na\n\nb\n\n\n\n\n\nc\n\n\n\na\n\nb\n\n\n\n\n\nc\n\n\n\n\n\n\nsgfsdf";

            TextArea[] areas = new TextArea[5];
            for (int c=0;c<areas.length;c++) {
                areas[c] = new TextArea();
                areas[c].setFocusable(false);
                areas[c].setLineWrap(true);
                areas[c].setText(s);
            }

            scrollTest.add(areas[0]);
            Button bobButton = new Button("BOB1");
            bobButton.setPreferredSize(750, -1);
            scrollTest.add(bobButton);
            scrollTest.add(areas[1]);
            scrollTest.add(new Button("BOB2") );
            scrollTest.add(areas[2]);

            Button center = new Button("center?");
            center.setMnemonic(KeyEvent.KEY_SOFTKEY3);
            scrollTest.add(center);

            addToScrollPane(scrollTest,null);
        }
        else if ("scrollTest3".equals(actionCommand)) {

            Panel p = new Panel(new FlowLayout( Graphics.VCENTER ));

            for (int c=0;c<10;c++) {
                Panel p2 = new Panel(new FlowLayout( Graphics.VCENTER ));
                for (int a=0;a<5;a++) {
                    p2.add(new Button("button "+c+" "+a));
                }
                List l = new List();
                for (int a=0;a<5;a++) {
                    l.addElement("item "+c+" "+a);
                }
                p2.add(l);
                ScrollPane scroll = new ScrollPane(p2);
                if (c==3) {
                    scroll.setMode(ScrollPane.MODE_SCROLLARROWS);
                    scroll.setBackground(0xFFFFAAAA);
                    p2.setBackground(0xFFAAFFAA);
                }
                scroll.setPreferredSize(-1, 100);
                p.add(scroll);
            }

            addToScrollPane(p,null);
        }
        else if ("tabTest".equals(actionCommand)) {

            if (tabPanel==null) {

                final TabbedPane tabbedPane = new TabbedPane();

                Panel tab1 = new Panel( new FlowLayout(Graphics.VCENTER) );
                tab1.setName("Tab 1");
                tab1.setBackground(0xFFFF0000);
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
                tab2.setBackground(0xFF00FF00);

                TabbedPane level2 = new TabbedPane();
                level2.addTab("one", new Button("too?") );
                level2.addTab("two", new Button("This is tab TWO 2") );

                tab2.add( level2 );

                Panel tab3 = new Panel( new FlowLayout(Graphics.VCENTER) );
                List l1 = new List( new DefaultListCellRenderer() ) {
                    public void paintComponent(Graphics2D g) {
                        System.out.println("paint "+getFirstVisibleIndex()+" "+getLastVisibleIndex());

                        // a error in paint!!?!?!?1!
                        //if (new Random().nextInt(3)==2) {
                        //    throw new StringIndexOutOfBoundsException();
                        //}

                        super.paintComponent(g);
                    }
                };
                //l1.setBorder( new MatteBorder(5, 5, 5, 5, 0xFFFF0000) );
                l1.setBorder( new MatteBorder(100, 100, 100, 100, 0xFFFF0000) );

                tab3.setBackground(0xFF0000FF);

                Vector anotherlist = new Vector();

                for (int c=0;c<20;c++) {
                    anotherlist.addElement(c+" A REALLY LONG LIST ITEM, that will need things like side scrolling "+c);
                }
                l1.setListData(anotherlist);
                //l1.setFixedCellHeight(15);
                l1.setPreferredSize(-1, 550);
                tab3.add(new Label("a lable for the list"));
                tab3.add(new Button("button") );
                tab3.add(l1);
                tab3.add(new Button("b 2"));

                Panel tab4 = new Panel( new BorderLayout() );
                tab4.add(new Label("Tab 4 title"),Graphics.TOP);

                List l2 = new List(anotherlist,new DefaultListCellRenderer(),List.VERTICAL) {
                    public void paintComponent(Graphics2D g) {
                        System.out.println("paint "+getFirstVisibleIndex()+" "+getLastVisibleIndex());
                        super.paintComponent(g);
                    }
                };
                l2.addListSelectionListener(new ChangeListener() {
                    public void changeEvent(Component arg0, int arg1) {
                        System.out.println("change "+arg1);
                    }
                });

                ActionListener listaction = new ActionListener() {
                    public void actionPerformed(String string) {
                        System.out.println("crazy funk "+string);
                    }
                };
                l1.addActionListener(listaction);
                l2.addActionListener(listaction);

                tab4.add(new ScrollPane(l2));

                tabbedPane.add(tab1);
                tabbedPane.addTab("TAB 2", mainPane.image, tab2,"i am a tooltip");
                tabbedPane.addTab("eee", new ScrollPane(tab3));
                tabbedPane.addTab(null,mainPane.image,tab4);

                tabPanel = tabbedPane;

            }

            Menu mainMenu = new Menu("Menu");
            for (int c=0;c<20;c++) {
                mainMenu.add( new Button("Hello") );
            }


            addToContentPane(tabPanel, mainMenu  );
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
                row1.addElement(Boolean.FALSE);
                row1.addElement(options.elementAt(0));
                rows.addElement(row1);

                Vector row2 = new Vector();
                row2.addElement("bob");
                row2.addElement(new Integer(25));
                row2.addElement(Boolean.TRUE);
                row2.addElement(options.elementAt(1));
                rows.addElement(row2);

                Vector row3 = new Vector();
                row3.addElement("fred");
                row3.addElement(new Integer(30));
                row3.addElement(Boolean.FALSE);
                row3.addElement(options.elementAt(2));
                rows.addElement(row3);

                Vector row4 = new Vector();
                row4.addElement( "thingy");
                row4.addElement(new Integer(30));
                row4.addElement(Boolean.TRUE);
                row4.addElement(new Option("ok","OK"));
                rows.addElement(row4);

                Vector numbers = new Vector();
                for (int c=0;c<100;c++) {
                    numbers.addElement(new Integer(c));
                }


                Table table = new Table(rows,null) {
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return (rowIndex!=3);
                    }
                };

                table.setDefaultRenderer(Integer.class, new DefaultCellEditor( new Spinner(numbers,false) ));
                table.setDefaultRenderer(Boolean.class, new DefaultCellEditor( new CheckBox() ) );

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

                //table.setIntercellSpacing(5);
            }

            addToScrollPane(tableTest, null );

        }
        else if ("optionPaneTest1".equals(actionCommand)) {

            //OptionPane.showMessageDialog(null, "Hello There", "Greeting", OptionPane.PLAIN_MESSAGE);

            Button[] b = new Button[] { new Button("Sure!")};
            OptionPane.showOptionDialog(null, "message1", "title", 0, OptionPane.INFORMATION_MESSAGE, null,b , null);


            Button[] b2 = new Button[] { new Button("No!")};
            OptionPane.showOptionDialog(null, "message2", "title", 0, OptionPane.INFORMATION_MESSAGE, null,b2 , null);

            Button[] b3 = new Button[] { new Button("Yes!")};
            OptionPane.showOptionDialog(null, "message3", "title", 0, OptionPane.INFORMATION_MESSAGE, null,b3 , null);

        }
        else if ("optionPaneTest".equals(actionCommand)) {
/*
                    FileChooser.GridList grid = new FileChooser.GridList(20);
                    grid.setPreferredSize(100, -1);
                    Vector stuff = new Vector();
                    for (int c='a';c<'h';c++) {
                        stuff.addElement(String.valueOf((char)c));
                    }
                    grid.setListData(stuff);
*/
            OptionPane.showMessageDialog(null,new Object[] {
                    "hi", //"Hello, whats your name?",
                    //new TextArea("bob the builder"),
                    //grid,
                    new TextField()
            },"Title",OptionPane.QUESTION_MESSAGE);
        }
        else if ("fileChooser".equals(actionCommand)) {

            String string = "file:///android_asset//";
            Vector en = FileUtil.listFiles(string, FileUtil.TYPE_ALL, false);
            System.out.println("file: "+en);

            if (chooser1==null) {
                chooser1 = new FileChooser("file:///android_asset/");
            }
            chooser1.showDialog(this, "fileSelected1", "Select File", "Select");
        }
        else if ("fileChooser2".equals(actionCommand)) {

            FileChooser chooser = new FileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.showDialog(this, "fileSelected2", "Select File", "Select");
        }
        else if ("fileSelected1".equals(actionCommand)) {

            String file = chooser1.getSelectedFile();
            Image img = ImageUtil.getImageFromFile(file);
            if (img!=null) {
                OptionPane.showMessageDialog(null, new Label(new Icon(img)), "image", OptionPane.PLAIN_MESSAGE);
            }
        }
        else if ("testCamera".equals(actionCommand)) {
            cameraPanel = new Camera();
            Button cameraCapture = makeButton("Capture","cameraCapture");
            cameraCapture.setMnemonic( KeyEvent.KEY_SOFTKEY3 ); // works on blackberry too
            addToContentPane(cameraPanel, cameraCapture );
            cameraPanel.setActionListener(this);
            cameraPanel.setActionCommand("cameraCaptureDone");
        }
        else if ("cameraCapture".equals(actionCommand)) {

            cameraPanel.capture();
        }
        else if ("cameraCaptureDone".equals(actionCommand)) {
            cameraPanel.setActionListener(null);
            cameraPanel.close();
            byte[] imgData = cameraPanel.getSnapshotData();

            Panel p = new Panel();
            p.setLayout(new BoxLayout(Graphics.VCENTER));

            if (imgData == null) {
                p.add( new Label("Camera Capture Failed!") );
            }
            else {
                Image img = Image.createImage(imgData, 0, imgData.length);
                imgData = null;
                System.gc();

                p.add( new Label("H:"+img.getHeight()+ ", W:" + img.getWidth()) );
                p.add( new Label(new Icon(img)) );
            }

            addToScrollPane(p,null);
        }
        else if ("testProgress".equals(actionCommand)) {

            final Window win = new Window();
            win.setName("Dialog");
            final ProgressBar bar = new ProgressBar();
            win.add(bar);
            win.pack();
            win.setLocationRelativeTo(null);

            new Thread() {
                public void run() {
                    win.setVisible(true);
                    bar.setIndeterminate(true);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    bar.setIndeterminate(false);
                    win.setVisible(false);
                }
            }.start();

        }
        else if ("testImageView1".equals(actionCommand) ||
                 "testImageView2".equals(actionCommand)) {
            try {
                Image img = Image.createImage("/swingme_logo.png");
                if ("testImageView2".equals(actionCommand)) {
                    img = Image.createImage(img, 0, 0, img.getWidth(), img.getHeight(), Sprite.TRANS_ROT90);
                }
                ImageView imgView = new ImageView();
                imgView.setBackgroundImage(new Icon(img));

                ScrollPane sp = new ScrollPane();
                sp.setBackground(0xff000000);
                sp.add(imgView);

                addToContentPane(sp, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if ("pageView".equals(actionCommand)) {
            System.out.println("pageView");

            Vector panels = new Vector();

            for (int c=0;c<8;c++) {
                Panel p = new Panel( new FlowLayout( Graphics.VCENTER ) );

                p.setBackground((c % 2 == 0) ? 0xFF0000FF : 0xFF00FFFF);
                p.setBorder(new LineBorder(0xFF000000));
                p.add( new Button("button1 "+c) );
                p.add( new Button("button2 "+c) );

                panels.addElement(p);

                if (c == 2) {
                    Label l = new Label("TEST");
                    l.setBackground(0xFFFFFF00);
                    panels.addElement(l);
                }

                if (c == 4) {
                    try {
                        Image img = Image.createImage("/swingme_logo.png");
                        ImageView imgView = new ImageView();
                        imgView.setBackgroundImage(new Icon(img));

                        imgView.setBackground(0xFF000000);
                        panels.addElement(imgView);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            PageView pg = new PageView(panels);
            pg.setBackground(0xFF000000);

            addToContentPane(pg,null);

        }
        else {
            System.out.println("Unknown action... " + actionCommand);
        }
    }
}

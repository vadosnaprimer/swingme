/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.test;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.border.BevelBorder;
import net.yura.mobile.gui.celleditor.DefaultCellEditor;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Camera;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.FileChooser;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ProgressBar;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.ScrollPane;
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
import net.yura.mobile.io.NativeUtil;
import net.yura.mobile.test.MainPane.Section;
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
            addTest("Option Pane Test","optionPaneTest");
            addTest("Table Test","tableTest");
            addTest("Scroll Test 1","scrollTest1");
            addTest("Scroll Test 2","scrollTest2");
            addTest("File Chooser","fileChooser");
            addTest("Multi File Chooser","fileChooser2");
            addTest("Test Camera","testCamera");
            addTest("Test Progress","testProgress");
    }

    public void openTest(String actionCommand) {

                if ("componentTest".equals(actionCommand)) {

			if (componentTest==null) {

				componentTest = new Panel( new FlowLayout(Graphics.VCENTER) );

                                Menu testMain = new Menu("Menu");

                                testMain.add(new Button("test 1"));
                                testMain.add(new Button("test 2 lalalalalal"));
                                testMain.add(new Button("test 3"));
                                testMain.addSeparator();
                                testMain.add(new RadioButton("test ra"));
                                testMain.add(new CheckBox("test ch"));

                                Menu testMain2 = new Menu("sub Menu");

                                testMain2.add(new Button("test 4"));
                                testMain2.add(new Button("test 5"));
                                testMain2.add(new Button("test 6"));

                                testMain.add(testMain2);

				componentTest.add( new Label("a Label") );
                                if (mainPane.image!=null) { componentTest.add( new Label( mainPane.image ) ); }
                                Button b = new Button("a Button");
                                b.setToolTipText("A ToolTip for a button");
				componentTest.add( b );

                                Button b2 = new Button("disabled");
                                b2.setFocusable(false);
				componentTest.add( b2 );

                                componentTest.add(testMain);

                                CheckBox cb = new CheckBox("a CheckBox");
                                cb.setUseSelectButton(true);
                                componentTest.add( cb );

                                RadioButton rb = new RadioButton("a RadioButton");
                                rb.setUseSelectButton(true);
                                componentTest.add( rb );

                                Vector items = new Vector();
                                items.addElement(null);
                                items.addElement("");
                                items.addElement("One");
                                items.addElement(new Option("2","Two",mainPane.image));
                                items.addElement(new Option("3","Three option"));
                                items.addElement(new Option("4",null,mainPane.image,"(no text)"));

                                ComboBox disabledCombo = new ComboBox(items);
                                disabledCombo.setFocusable(false);

                                Spinner disabledSpinner = new Spinner(items, false);
                                disabledSpinner.setFocusable(false);

                                componentTest.add( new ComboBox(items) );
                                componentTest.add( disabledCombo );
                                componentTest.add( new Spinner(items, false));
                                componentTest.add(disabledSpinner);

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
                                componentTest.add(new TextField(TextField.PASSWORD));
                                componentTest.add( new List(items,new DefaultListCellRenderer(),false) );

                                menu = new Menu("Menu");
                                // menu has NO action listoner, so it fires NO action and ONLY opens the menu!
                                menu.add(new Button("bob"));

                                Menu menu2 = new Menu("Sub");
                                //menu2.addActionListener(this);
                                menu2.add(new Button("fred"));
                                menu2.add(new Button("item 2"));
                                menu.add(menu2);

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
                    addToScrollPane(p,null);
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
                // hack to avoid having to make a new action listoner
                close.addActionListener(test1.getTitleBar());
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
                        scrollTest.add(new Button("BOB1") );
                        scrollTest.add(areas[1]);
                        scrollTest.add(new Button("BOB2") );
                        scrollTest.add(areas[2]);

                        Button center = new Button("center?");
                        center.setMnemonic(KeyEvent.KEY_SOFTKEY3);
                        scrollTest.add(center);

			addToScrollPane(scrollTest,null);
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
                                List l3 = new List( new DefaultListCellRenderer() );
                                tab3.setBackground(0xFF0000FF);

                                Vector anotherlist = new Vector();

                                for (int c=0;c<20;c++) {
                                    anotherlist.addElement("A REALLY LONG LIST ITEM, that will need things like side scrolling "+c);
                                }
                                l3.setListData(anotherlist);
                                l3.setFixedCellHeight(15);
                                tab3.add(new Label("a lable for the list"));
                                tab3.add(new Button("button") );
                                tab3.add(l3);
                                tab3.add(new Button("b 2"));

                                Panel tab4 = new Panel( new BorderLayout() );
                                tab4.add(new Label("Tab 4 title"),Graphics.TOP);

                                List l2 = new List(anotherlist,new DefaultListCellRenderer(),false);
                                l2.addChangeListener(new ChangeListener() {
                                    public void changeEvent(Component arg0, int arg1) {
                                        System.out.println("change "+arg1);
                                    }
                                });
                                tab4.add(new ScrollPane(l2));

                                tabbedPane.add(tab1);
                                tabbedPane.addTab("TAB 2", mainPane.image, tab2,"i am a tooltip");
                                tabbedPane.addTab("eee", new ScrollPane(tab3));
                                tabbedPane.addTab(null,mainPane.image,tab4);

                                tabPanel = tabbedPane;

			}

			addToContentPane(tabPanel, null  );
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

                    if (chooser1==null) {
                        chooser1 = new FileChooser();
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
                    Image img = NativeUtil.getImageFromFile(file);
                    if (img!=null) {
                        OptionPane.showMessageDialog(null, new Label(new Icon(img)), "image", OptionPane.PLAIN_MESSAGE);
                    }
                }
                else if ("testCamera".equals(actionCommand)) {
                    cameraPanel = new Camera();
                    addToContentPane(cameraPanel, makeButton("Capture","cameraCapture") );
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

                    Label l, d = null;
                    if (imgData == null) {
                        l = new Label("Camera Capture Failed!");
                    } else {
                        Image img = Image.createImage(imgData, 0, imgData.length);
                        imgData = null;
                        System.gc();
                        d = new Label("H:"+img.getHeight()+ ", W:" + img.getWidth());
                        l = new Label(new Icon(img));
                    }
                    Panel p = new Panel();
                    p.setLayout(new BoxLayout(Graphics.VCENTER));
                    p.add(d);
                    p.add(l);

                    addToContentPane(p,null);
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
                else {

                }
    }
}

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
import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.plaf.nimbus.NimbusLookAndFeel;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.MenuBar;


/**
 * @author Yura Mamyrin
 */
public class MainPane extends DesktopPane {

    public Icon image;
    private Frame mainWindow;
    private ScrollPane scroll;

    public MainPane(MyMidlet a) {
        super(a,0,null);
    }

    public void initialize() {

        // metal = new MetalLookAndFeel();
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
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        mainWindow.setMenuBar(new MenuBar());
        mainWindow.setUndecorated(true);
        mainWindow.setVisible(true);
    }

    public void setMainSection(Section section) {

        section.mainPane = this;

        section.createTests();
        section.openTest("mainmenu");
    }


    public abstract static class Section extends Panel implements ActionListener {

        protected MainPane mainPane;
        protected Section parentSection;

        public Section(MainPane mainPane) {
            super( new FlowLayout( Graphics.VCENTER ) );
            this.mainPane = mainPane;
            createTests();
        }

        public Section() {
            this(null);
        }

        public void actionPerformed(String arg0) {
             if ("openSection".equals(arg0)) {

                 if (parentSection == null) {
                     mainPane.addToScrollPane(this, null , null);
                 } else {
                     // Add a back button, if we have a parent test section...
                     Button bk = parentSection.makeButton("Back", "mainmenu");
                     mainPane.addToScrollPane(this, null, bk);
                 }
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

        public Section addSection(String a, Section section) {

            section.mainPane = this.mainPane;
            section.parentSection = this;

            add( section.makeButton(a, "openSection") );
            return section;
        }

        public void addToScrollPane(Component p,Button b1, Button b2) {
            mainPane.addToScrollPane(p, b1, b2) ;
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

        public Frame getMainWindow() {
            return mainPane.mainWindow;
        }
    }

    private void addToContentPane(Component a,Button b,Button c) {

        Panel pane = mainWindow.getContentPane();
        pane.removeAll();
        pane.add(a);
        pane.add(new Label("yura.net Mobile"), Graphics.TOP);

        setCommandButtons(b,c);
    }

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
}

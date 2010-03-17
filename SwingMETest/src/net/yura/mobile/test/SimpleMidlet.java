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

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;

/**
 * @author Yura Mamyrin
 */
public class SimpleMidlet extends Midlet implements ActionListener {

	private DesktopPane rootpane;

	protected DesktopPane makeNewRootPane() {
		return new DesktopPane(this,0,null);
	}

	protected void initialize(DesktopPane rp) {

		this.rootpane = rp;

		rootpane.setLookAndFeel( new MetalLookAndFeel() );

                Frame mainWindow = new Frame();

                Button exit = new Button("Exit");
                exit.addActionListener(this);
                exit.setActionCommand("exit");

                //mainWindow.setActionListener(this);
		//mainWindow.setWindowCommand(1, new CommandButton("Exit","exit") );

                MenuBar bar = new MenuBar();
                bar.add(exit);
                mainWindow.setMenuBar(bar);

		mainWindow.add( new Label("Hello World!") );
		mainWindow.setMaximum(true);
                mainWindow.setVisible(true);


                // this code is the same in normal java awt and SwingME
            ActionListener actionListener = new ActionListener() {
                //public void actionPerformed(ActionEvent e) { String action = e.getActionCommand();
                public void actionPerformed(String action) {
                    if ("exit".equals(action)) {
                        System.exit(0);
                    }
                    else {
                        System.out.println("unknown action "+action);
                    }

                }
            };
            Frame frame = new Frame("window");
            Panel panel = new Panel(new FlowLayout());
            Button button = new Button("exit");
            button.setActionCommand("exit");
            button.addActionListener(actionListener);
            panel.add(button);
            panel.add(new Label("label"));
            frame.add(panel);
            MenuBar menubar = new MenuBar();
            Menu menu = new Menu("menu");
//            MenuItem open = new MenuItem("Open");
//            MenuItem close = new MenuItem("Close");
            Button open = new Button("Open");
            Button close = new Button("Close");
            open.setActionCommand("open");
            close.setActionCommand("close");
            open.addActionListener(actionListener);
            close.addActionListener(actionListener);
            menu.add(open);
            menu.add(close);
            menubar.add(menu);
            frame.setMenuBar(menubar);
            frame.pack();
            frame.setVisible(true);

	}

	public void actionPerformed(String actionCommand) {

		if ("exit".equals(actionCommand)) {

			Midlet.exit();

		}

	}

}

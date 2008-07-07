package net.yura.mobile.test;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Theme;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.layout.FlowLayout;

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
		
		Window mainWindow = rootpane.getSelectedFrame();
		
                mainWindow.setActionListener(this);
                
		rootpane.setDefaultTheme( new Theme() );
		
		mainWindow.setWindowCommand(1, new CommandButton("Exit","exit") );
		
		mainWindow.getContentPane().setBackground( 0x00EEEEEE );
		mainWindow.getContentPane().setLayout( new FlowLayout() );
		mainWindow.getContentPane().add( new Label("Hello World!") );
		mainWindow.getContentPane().doLayout();
	}

	public void actionPerformed(String actionCommand) {
		
		if ("exit".equals(actionCommand)) {
			
			rootpane.exit();
			
		}
		
	}

}

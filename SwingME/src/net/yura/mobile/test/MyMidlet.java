package net.yura.mobile.test;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.DesktopPane;

/**
 * @author Yura Mamyrin
 */
public class MyMidlet extends Midlet {
	
	protected DesktopPane makeNewRootPane() {
		return new MainPane(this);
	}

	protected void initialize(DesktopPane rp) {
		((MainPane)rp).initialize();
	}

}
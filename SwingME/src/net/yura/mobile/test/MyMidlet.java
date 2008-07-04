package net.yura.mobile.test;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.RootPane;

/**
 * @author Yura Mamyrin
 */
public class MyMidlet extends Midlet {
	
	protected RootPane makeNewRootPane() {
		return new MainPane(this);
	}

	protected void initialize(RootPane rp) {
		((MainPane)rp).initialize();
	}

}
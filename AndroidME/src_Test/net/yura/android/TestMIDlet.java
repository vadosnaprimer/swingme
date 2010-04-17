package net.yura.android;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.test.MainPane;

/**
 * @author Yura Mamyrin
 */
public class TestMIDlet extends Midlet {

	protected DesktopPane makeNewRootPane() {
		return new MainPane(this);
	}

	protected void initialize(DesktopPane rp) {
	    MainPane mainPane = (MainPane) rp;
	    mainPane.initialize();
	    mainPane.setMainSection(new MainTest(mainPane));
	}
}
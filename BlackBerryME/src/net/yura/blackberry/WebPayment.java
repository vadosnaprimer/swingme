package net.yura.blackberry;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.script.ScriptEngine;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import org.w3c.dom.Document;

public final class WebPayment extends MainScreen {

	private BrowserField browserField;

	public WebPayment(String startURL, final String endURL, String title) {
		setTitle(title);
		BrowserFieldConfig myBrowserFieldConfig = new BrowserFieldConfig();
		myBrowserFieldConfig.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_POINTER);
		browserField = new BrowserField(myBrowserFieldConfig);
		add(browserField);
		browserField.requestContent(startURL);
		browserField.addListener(new BrowserFieldListener() {
			public void documentCreated(BrowserField browserField, ScriptEngine scriptEngine, Document document) {
				if (document.getBaseURI().equals(endURL)) {
					close();
				}
				System.out.println("Document created. URI: " + document.getBaseURI());
			}
		});
		UiApplication.getUiApplication().pushScreen(this);
	}

	protected boolean keyDown(int keyCode, int time) {
		if (Keypad.key(keyCode) == Keypad.KEY_ESCAPE) {
			return browserField.back();
		}
		return super.keyDown(keyCode, time);
	}

}

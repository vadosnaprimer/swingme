package net.yura.blackberry;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.script.ScriptEngine;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.yura.mobile.gui.Midlet;

import org.w3c.dom.Document;

public final class WebPayment extends MainScreen {

    private BrowserField browserField;

    public WebPayment(String startURL, final String successURL, final String errorURL, final int successConstant, final int errorConstant, String title) {
        setTitle(title);
        BrowserFieldConfig myBrowserFieldConfig = new BrowserFieldConfig();
        myBrowserFieldConfig.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_POINTER);
        browserField = new BrowserField(myBrowserFieldConfig);
        add(browserField);
        browserField.requestContent(startURL);
        browserField.addListener(new BrowserFieldListener() {
                public void documentCreated(BrowserField browserField, ScriptEngine scriptEngine, Document document) {
                        if (document.getBaseURI().toLowerCase().indexOf(successURL.toLowerCase())> -1) {
                            Midlet.getMidlet().onResult(-1, successConstant, null);
                            close();                                                                    
                        } 
                        else if (document.getBaseURI().toLowerCase().indexOf(errorURL.toLowerCase()) > - 1) {
                            Midlet.getMidlet().onResult(-1, errorConstant, null);
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
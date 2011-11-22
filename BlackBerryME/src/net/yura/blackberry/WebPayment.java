package net.yura.blackberry;

import javax.microedition.io.InputConnection;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.browser.field2.ProtocolController;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.yura.mobile.gui.Midlet;

public final class WebPayment extends MainScreen {

    private BrowserField browserField;
    private int cancelConstant;
    private boolean tryUseCarrierConnection;

    public WebPayment(String startURL, final String successURL, final String errorURL, final int successConstant, final int errorConstant, final int carrierConstant, final int cancelConstant, String title, final boolean tryUseCarrierConnection) {
        setTitle(title);
        BrowserFieldConfig myBrowserFieldConfig = new BrowserFieldConfig();
        this.cancelConstant = cancelConstant;
        this.tryUseCarrierConnection = tryUseCarrierConnection;
        if (tryUseCarrierConnection) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            int[] transportTypes = { TransportInfo.TRANSPORT_WAP2, TransportInfo.TRANSPORT_WAP, TransportInfo.TRANSPORT_TCP_CELLULAR, TransportInfo.TRANSPORT_BIS_B, TransportInfo.TRANSPORT_TCP_WIFI };
            connectionFactory.setPreferredTransportTypes(transportTypes);
            myBrowserFieldConfig.setProperty(BrowserFieldConfig.CONNECTION_FACTORY, connectionFactory);
        }

        myBrowserFieldConfig.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_POINTER);
        browserField = new BrowserField(myBrowserFieldConfig);
        myBrowserFieldConfig.setProperty(BrowserFieldConfig.CONTROLLER, new ProtocolController(browserField) {

            public InputConnection handleResourceRequest(BrowserFieldRequest request) throws Exception {                
                if (checkUrl(request)) {
                    return super.handleResourceRequest(request);
                }
                return null;
            }
            
            public void handleNavigationRequest(BrowserFieldRequest request) throws Exception {                
                if (checkUrl(request)) {
                    super.handleNavigationRequest(request);
                }
            }
            
            private boolean checkUrl(BrowserFieldRequest request) {                
                String url = request.getURL().toLowerCase();
                System.out.println("handleNavigationRequest: " + url);
                boolean success = url.indexOf(successURL.toLowerCase()) > -1;
                boolean fail = url.indexOf(errorURL.toLowerCase()) > -1;
                if (success || fail) {
                    if (tryUseCarrierConnection) {
                        close(carrierConstant, request.getURL());
                    }
                    else {
                        close(success ? successConstant : errorConstant, null);
                    }
                    return false;
                }
                return true;          
            }
            
        });

        add(browserField);
        browserField.requestContent(startURL);
        UiApplication.getUiApplication().pushScreen(this);
    }

    public void close() {
        close(-1, null);
    }

    private void close(int responseCode, Object res) {
        Midlet.getMidlet().onResult(-1, responseCode, res);
        // Close cannot be called from the UI thread, and we are calling it from
        // inside the Protocol Controller
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                WebPayment.super.close();
            }
        });
    }

    protected boolean keyDown(int keyCode, int time) {
        if (Keypad.key(keyCode) == Keypad.KEY_ESCAPE) {
            if (!browserField.back()) {
                close(tryUseCarrierConnection ? cancelConstant : -1, null);
            }
            return true;
        }
        return super.keyDown(keyCode, time);
    }

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.mobile.io;
/**
 *
 * @author AP
 */
public abstract class ClipboardManager implements ServiceLink.TaskHandler {
    /** Creates a new instance of LocationMonitor */
    public ClipboardManager() {
        ServiceLink link = ServiceLink.getInstance();
        link.registerForTask("GetClipboardTextError", this);
        link.registerForTask("PutClipboardText", this);
    }

    public void getClipboard() {
        ServiceLink link = ServiceLink.getInstance();
        if (link.isConnected())
            link.addToOutbox(new ServiceLink.Task("GetClipboardText", null));
    }

    public void putClipboard(String text) {
        ServiceLink link = ServiceLink.getInstance();
        if (link.isConnected())
            link.addToOutbox(new ServiceLink.Task("PutClipboardText", text));
    }

}

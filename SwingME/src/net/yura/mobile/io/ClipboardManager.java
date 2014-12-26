/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.mobile.io;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.io.ServiceLink.Task;
import net.yura.mobile.logging.Logger;

/**
 *
 * @author AP
 */
public class ClipboardManager implements ServiceLink.TaskHandler {
    /** Creates a new instance of LocationMonitor */
    private ClipboardManager() {
        if (Midlet.getPlatform()==Midlet.PLATFORM_NOKIA_S60) {
            ServiceLink link = ServiceLink.getInstance();
            link.registerForTask("GetClipboardTextError", this);
            link.registerForTask("PutClipboardText", this);
        }
    }

    public void handleTask(Task task) {
        String strMethod = task.getMethod();
        if ("GetClipboardTextError".equals(strMethod)) {
            // error
        }
        else if ("PutClipboardText".equals(strMethod)) {
            String txt = (String) task.getObject();
        }
    }

    private static ClipboardManager instance;

    static {
        instance = new ClipboardManager();
    }

    public static ClipboardManager getInstance() {
        return instance;
    }

    public String getText() {

        if (Midlet.getPlatform()==Midlet.PLATFORM_NOKIA_S60) {
            ServiceLink link = ServiceLink.getInstance();
            if (link.isConnected()) {
                link.sendTask(new ServiceLink.Task("GetClipboardText", null));

                // TODO now pause and then resume when u have got the object
            }
        }
        else { // for me4se and will be for android
            Midlet midlet = Midlet.getMidlet();

            midlet.platformRequest("clipboard://get",null);

            return System.getProperty("clipboard.text");
        }

        return null;
    }

    public void setText(String text) {

        if (Midlet.getPlatform()==Midlet.PLATFORM_NOKIA_S60) {
            ServiceLink link = ServiceLink.getInstance();
            if (link.isConnected()) {
                link.sendTask(new ServiceLink.Task("PutClipboardText", text));
            }
        }
        else {
            Midlet midlet = Midlet.getMidlet();
            //midlet.platformRequest("clipboard://put",text);
            try {
                midlet.platformRequest("clipboard://put/"+text);
            }
            catch(Exception ex) {
                Logger.warn("cant put " + text, ex);
            }
        }
    }

}

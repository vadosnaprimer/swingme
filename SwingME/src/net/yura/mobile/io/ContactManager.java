/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.io;

/**
 *
 * @author MarkH
 */
public abstract class ContactManager implements ServiceLink.TaskHandler {
    /** Creates a new instance of LocationMonitor */
    public ContactManager() {
        ServiceLink link = ServiceLink.getInstance();
        link.registerForTask("GetContactCountError", this);
        link.registerForTask("PutContactCount", this);
    }

    public void getContactCount() {
        ServiceLink link = ServiceLink.getInstance();
        if (link.isConnected()) {
            link.sendTask(new ServiceLink.Task("GetContactCount", null));
        }
    }

}

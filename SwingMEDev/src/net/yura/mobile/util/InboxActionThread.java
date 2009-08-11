package net.yura.mobile.util;

import java.util.Vector;
import net.yura.mobile.gui.DesktopPane;

/**
 * @author Kat
 */
public abstract class InboxActionThread extends Thread {

    private Vector inbox = new Vector();
    private boolean runnning;

    public InboxActionThread() {
    }

    public InboxActionThread(String name) {
        super(name);
    }

    public void kill() {
           synchronized(this) {
                runnning = false;
                notify();
            }
    }

    public void run() {

        runnning = true;

        runLoop: while (runnning) {

            try {
                    Object object;

                    synchronized(this) {
                        while(inbox.isEmpty()) {
                                if (!runnning) {
                                    break runLoop;
                                }
                                try {
                                    wait();
                                }
                                catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                        }
                        object = inbox.elementAt(0);
                        inbox.removeElementAt(0);
                    }


                    action(object);


            }
            catch (Exception ex) {
                ex.printStackTrace();
                DesktopPane.log(getName()+" "+ex.toString());
            }

        }

    }

    public void addToInbox(Object obj) {

        synchronized(this) {

            inbox.addElement(obj);
            notify();

        }

    }
    public boolean isRunning() {
        return runnning;
    }

    public abstract void action(Object object) throws Exception;

}

package net.yura.mobile.util;

import java.util.Vector;

/**
 * @author Kat
 */
public abstract class QueueProcessorThread extends Thread {

    private Vector inbox = new Vector();
    private boolean runnning;

    public QueueProcessorThread() {
    }

    public QueueProcessorThread(String name) {
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


                    process(object);


            }
            catch (Exception ex) {
                ex.printStackTrace();
                //DesktopPane.log(getName()+" "+ex.toString());
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

    public void clearInbox() {
        synchronized(this) {
            inbox.removeAllElements();
        }
    }

    public abstract void process(Object object) throws Exception;

}

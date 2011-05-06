package net.yura.mobile.util;

import java.util.Vector;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public abstract class QueueProcessorThread extends Thread {

    private Vector inbox = new Vector();
    private boolean runnning;

    /**
     * @deprecated
     */
    public QueueProcessorThread() {
        //#debug debug
        Logger.dumpStack();
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

        try {
            // Changing thread priority should only be done in platforms
            // were actually improve performance.
            boolean doThreadYield = (Midlet.getPlatform() != Midlet.PLATFORM_ANDROID);
            if (doThreadYield) {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            }

            runnning = true;

            runLoop: while (runnning) {

                Object object=null;

                try {

                    synchronized(this) {
                        while(inbox.isEmpty()) {
                            if (!runnning) {
                                break runLoop;
                            }
                            try {
                                wait();
                            }
                            catch (InterruptedException ex) {
                                Logger.info(ex);
                                // TODO do something!!!!
                            }
                        }
                        object = inbox.elementAt(0);
                        inbox.removeElementAt(0);
                    }

                    // try not to slow down the UI
                    if (doThreadYield) {
                        Thread.yield();
                        Thread.sleep(0);
                    }

                    process(object);

                    if (doThreadYield) {
                        Thread.yield();
                        Thread.sleep(0);
                    }
                }
                catch (Exception ex) {
                    //#mdebug warn
                    Logger.warn("[QueueProcessorThread-"+getName()+"] error processing "+object);
                    Logger.warn(ex);
                    //#enddebug
                }
            }
        }
        catch(Throwable t) {
            //#mdebug error
            Logger.error("[QueueProcessorThread-"+getName()+"] fatal error: "+t.toString());
            Logger.error(t);
            //#enddebug
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

    public Vector getInbox() {
        return inbox;
    }

    public abstract void process(Object object) throws Exception;

}

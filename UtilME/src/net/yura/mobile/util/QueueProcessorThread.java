package net.yura.mobile.util;

import java.util.Vector;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public abstract class QueueProcessorThread extends Thread {

    public static boolean DROP_PRIORITY=true;

    private Vector inbox = new Vector();
    private boolean runnning;

    /**
     * @deprecated should provide a name for the thread with the {@link #QueueProcessorThread(String)} constructor
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

            //#debug info
            Logger.info("[QueueProcessorThread-"+getName()+"] START DROP_PRIORITY=="+DROP_PRIORITY);

            if (DROP_PRIORITY) {
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
                    if (DROP_PRIORITY) {
                        Thread.yield();
                        Thread.sleep(0);
                    }

                    process(object);

                    if (DROP_PRIORITY) {
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

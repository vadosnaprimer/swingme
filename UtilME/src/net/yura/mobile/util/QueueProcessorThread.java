package net.yura.mobile.util;

import java.util.Vector;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public abstract class QueueProcessorThread implements Runnable {

    public static boolean CHANGE_PRIORITY=true;

    private Vector inbox = new Vector();
    private boolean runnning;
    private Vector threads = new Vector(1);

    public QueueProcessorThread(String name) {
        this(name,1);
    }

    public QueueProcessorThread(String name,int num) {
        //#mdebug debug
        if (num<1) {
            throw new IllegalArgumentException("min 1 thread: "+num);
        }
        //#enddebug
        for (int c=0;c<num;c++) {
            threads.addElement(new Thread(this,name+"-"+c));
        }
    }

    public void start() {
        for (int c=0;c<threads.size();c++) {
            ((Thread)threads.elementAt(c)).start();
        }
    }

    public void kill() {
        synchronized(this) {
            runnning = false;
            notifyAll();
        }
    }

    public void run() {
        try {
            //#debug info
            Logger.info("[QueueProcessorThread-"+Thread.currentThread().getName()+"] START DROP_PRIORITY=="+CHANGE_PRIORITY);

            if (CHANGE_PRIORITY) {
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
                    if (CHANGE_PRIORITY) {
                        Thread.yield();
                        Thread.sleep(0);
                    }

                    //Logger.info("[QueueProcessorThread-"+Thread.currentThread().getName()+"] process: "+object);
                    process(object);

                    if (CHANGE_PRIORITY) {
                        Thread.yield();
                        Thread.sleep(0);
                    }
                }
                catch (Exception ex) {
                    //#mdebug warn
                    Logger.warn("[QueueProcessorThread-"+Thread.currentThread().getName()+"] error processing "+object);
                    Logger.warn(ex);
                    //#enddebug
                }
            }
        }
        catch(Throwable t) {
            //#mdebug error
            Logger.error("[QueueProcessorThread-"+Thread.currentThread().getName()+"] fatal error: "+t.toString());
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

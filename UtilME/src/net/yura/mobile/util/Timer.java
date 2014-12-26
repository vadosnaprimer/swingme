package net.yura.mobile.util;

import net.yura.mobile.logging.Logger;

public class Timer extends Thread {

    private String id;
    private long delay;
    private Runnable task;
    private boolean cancelled = false;

    /**
     * @see java.util.Timer#schedule(java.util.TimerTask, long) Timer.schedule
     */
    public void schedule(String id, Runnable task, long delay){

        //#debug debug
        Logger.debug("Setting schedule for (" + id + ") with delay of: " + delay);

        this.id = id;
        this.delay = delay;
        this.task = task;
        this.setPriority(MIN_PRIORITY);
        this.start();
    }

    /**
     * @see java.util.Timer#schedule(java.util.TimerTask, long, long) Timer.schedule
     */
    public void schedule(String id, Runnable task, long delay, long period){
        throw new RuntimeException("not done yet :-)");
    }

    public final void run() {

        try {
            if (!cancelled) {
              try {
                  Thread.sleep(delay);
              }
              catch (InterruptedException e) {
                  //#debug info
                  Logger.info(null, e);
              }

              if (cancelled) return;

              //#debug debug
              Logger.debug("Running task (" + id + ") after a schedule of: " + delay);
              task.run();
            }
        }
        catch (Throwable t) {
          //#debug warn
          Logger.warn("error in (" + id + ") timer", t);
        }

    }


    /**
     * @see java.util.Timer#cancel() Timer.cancel
     */
    public void cancel(){

        //#debug debug
        Logger.debug("Cancelling task (" + id + ")");

        cancelled = true;
    }
}

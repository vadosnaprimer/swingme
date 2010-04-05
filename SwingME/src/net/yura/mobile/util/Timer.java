package net.yura.mobile.util;

import net.yura.mobile.logging.Logger;

/**
 *
 * @author BMA
 */
public class Timer extends Thread{

    private String id;
    private long delay;
    private Runnable task;
    private boolean cancelled = false;

    public void schedule(String id, Runnable task, long delay){

        //#debug
        Logger.debug("Setting schedule for {" + id + "} with delay of: " + delay);
        
        this.id = id;
        this.delay = delay;
        this.task = task;
        this.setPriority(MIN_PRIORITY);
        this.start();
    }

    public void run(){

        while(!cancelled){
            try {
              try {
                  this.sleep(delay);
              }
              catch (InterruptedException e) {
                Logger.info(e);
              }

              if (cancelled) break;

              //#debug debug
              Logger.debug("Running task {" + id + "} after a schedule of: " + delay);
              task.run();
            }
            catch (Throwable t) {
              //#mdebug
              Logger.error("error in {"+id+"} timer: "+t.toString());
              Logger.error(t);
              //#enddebug
            }
        }
    }


    public void cancel(){

        //#debug debug
        Logger.debug("Cancelling task {" + id + "}");

        cancelled = true;
    }
}

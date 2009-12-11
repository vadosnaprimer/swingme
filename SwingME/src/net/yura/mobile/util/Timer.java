package net.yura.mobile.util;

import net.yura.mobile.gui.DesktopPane;

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
        System.out.println("Setting schedule for {" + id + "} with delay of: " + delay);
        
        this.id = id;
        this.delay = delay;
        this.task = task;
        this.setPriority(MIN_PRIORITY);
        this.start();
    }

    public void run(){

        while(!cancelled){
            try {
                this.sleep(delay);
            } catch (InterruptedException e) {                
                e.printStackTrace();
            }

            if (cancelled) break;

            //#debug
            System.out.println("Running task {" + id + "} after a schedule of: " + delay);

            try {
                task.run();
            }
            catch (Throwable t) {
                //#mdebug
                DesktopPane.log("error in {"+id+"} timer: "+t.toString());
                t.printStackTrace();
                //#enddebug
            }
        }
    }


    public void cancel(){

        //#debug
        System.out.println("Cancelling task {" + id + "}");

        cancelled = true;
    }
}

package net.yura.mobile.util;

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

        //System.out.println("Setting schedule for {" + id + "} with delay of: " + delay);
        
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (cancelled) break;

            //System.out.println("Running task {" + id + "} after a schedule of: " + delay);

            try {
                task.run();
            } catch (Throwable t){
                // System.out.println(t.getMessage());
            }
        }
    }


    public void cancel(){
        //System.out.println("Cancelling task {" + id + "}");
        cancelled = true;
    }
}

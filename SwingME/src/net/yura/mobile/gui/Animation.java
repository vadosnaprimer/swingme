package net.yura.mobile.gui;

import java.util.Hashtable;
import java.util.Vector;

import net.yura.mobile.gui.components.Component;
import net.yura.mobile.logging.Logger;

/**
 * used by all infinitely animating components, such as the indeterminate progress bar
 */
public class Animation extends Thread {

    static Animation animation;
    public static int FPS = 10;
    final Vector components = new Vector();
    final Hashtable lastcall = new Hashtable();

    public Animation() {
        super("SwingMe-Animation-Two");
    }

    public void run() {
        try {
            long lastWait = System.currentTimeMillis();
            int sleep = 1000/FPS;

            mainloop: while (true) {


                synchronized (components) {
                    if (components.isEmpty()) {
                        components.wait();

                        lastWait = System.currentTimeMillis();
                    }
                }

                for (int c=components.size()-1;c>=0;c--) { // need to count down as we may be removing object from the vector

                    Component cmp = (Component)components.elementAt(c);

                    // we only want to animate something if its asked for in in the past second
                    long lastPaint = ((Long)lastcall.get(cmp)).longValue();
                    if (lastWait-lastPaint > 1000) {
                        deregisterAnimated(cmp);
                        continue;
                    }

                    if (DesktopPane.getDesktopPane()==null) { // DesktopPane is set to null when killflag==true
                        break mainloop;
                    }
                    
                    cmp.animate();
                }

                lastWait = workoutSleep(sleep,lastWait);
            }
        }
        catch (Throwable th) {
            //#debug info
            Logger.warn(th);
        }

        animation = null;
    }

    public static long workoutSleep(int sleep,long lastWait) throws InterruptedException {
                long time = System.currentTimeMillis();

                long wait = Math.min(sleep, Math.max(0, sleep - (time-lastWait) ));
                //System.out.println("wait "+wait);
                Thread.sleep( wait );
                
                return time + wait;
    }
    
    public static void registerAnimated(Component cmp) {

        if (animation==null) {
            animation = new Animation();
            animation.start();
        }
        Animation ani = animation;

        // put it firt in the table, so if its in the vector, its def in the table
        ani.lastcall.put(cmp, new Long(System.currentTimeMillis()));

        synchronized (ani.components) {
            if (!ani.components.contains(cmp)) {

                //#debug debug
                Logger.info("[Animation] registerAnimated "+cmp+"@"+System.identityHashCode(cmp));

                ani.components.addElement(cmp);
                ani.components.notify();
            }
        }
    }

    public static void deregisterAnimated(Component cmp) {

        //#debug debug
        Logger.info("[Animation] deregisterAnimated "+cmp+"@"+System.identityHashCode(cmp));

        Animation ani = animation;
        
        if (ani!=null) {
            ani.components.removeElement(cmp);
            ani.lastcall.remove(cmp);
        }
    }
}

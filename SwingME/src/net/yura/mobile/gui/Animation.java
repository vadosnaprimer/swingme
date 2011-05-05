package net.yura.mobile.gui;

import java.util.Hashtable;
import java.util.Vector;

import net.yura.mobile.gui.components.Component;
import net.yura.mobile.logging.Logger;

public class Animation extends Thread{

	static Animation animation;
	
	static final int fps = 25;
	
	static final Vector components = new Vector();
	
	static final Hashtable lastcall = new Hashtable();
	
	public void run() {

		try {
			while (true) {
				
				synchronized (components) {
					if (components.isEmpty()) {
						components.wait();
					}
				}
				
				long start = System.currentTimeMillis();
				
				for (int c=components.size()-1;c>=0;c--) { // need to count down as we may be removing object from the vector
					
					Component cmp = (Component)components.elementAt(c);
					
					// we only want to animate something if its asked for in in the past second
					long lastPaint = ((Long)lastcall.get(cmp)).longValue();
					if (System.currentTimeMillis()-lastPaint > 1000) {
						deregisterAnimated(cmp);
						continue;
					}
					
					cmp.animate();
				}
				
				long end = System.currentTimeMillis();
			
				Thread.sleep( Math.max(0, 1000/fps - (end-start) ) );
				
			}
		}
		catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	public static void registerAnimated(Component cmp) {

		if (animation==null) {
			animation = new Animation();
			animation.start();
		}

		// put it firt in the table, so if its in the vector, its def in the table
		lastcall.put(cmp, new Long(System.currentTimeMillis()));
		
		synchronized (components) {
			if (!components.contains(cmp)) {
				
				//#debug debug
				Logger.info("[Animation] registerAnimated "+cmp+"@"+System.identityHashCode(cmp));
				
				components.addElement(cmp);
				components.notify();
			}
		}

	}

	public static void deregisterAnimated(Component cmp) {
		
		//#debug debug
		Logger.info("[Animation] deregisterAnimated "+cmp+"@"+System.identityHashCode(cmp));
		
		components.removeElement(cmp);
		lastcall.remove(cmp);
	}


}

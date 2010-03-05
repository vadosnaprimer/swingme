/*
 * Created on 01.07.2005

 */
package org.me4se.psi.java1.media.video;

import java.util.Vector;

import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import javax.microedition.media.control.VideoControl;

import org.me4se.impl.BasicMMDataSource;
import org.me4se.impl.BasicMMPlayer;


/**
 * @author Stefan Haustein, Michael Kroll
 */
public class VideoCaptureImpl extends BasicMMPlayer implements Runnable {

	public VideoCaptureImpl() {
		super(true);
	}



	VideoControlImpl videoControl = new VideoControlImpl();
	
    public void closeImpl() {
         // thread is terminated automatically, depending on the state
    }
    
    public void deallocateImpl() {
    }

    
    public String getContentType() {
        if(state == UNREALIZED) throw new IllegalStateException();
       // TODO Wrong image/png? 
    	    return "capture://video";
    }



    public void prefetchImpl() throws MediaException {
    }

    public void realizeImpl() throws MediaException {
    }


    public void setLoopCount(int i) {
        if(state == STARTED || state == CLOSED){
        	throw new IllegalStateException();
        }
        if(i != 1 && i != -1) throw new IllegalArgumentException();
    }

    public long setMediaTime(long l) throws MediaException {
       throw new MediaException("Cannot set Media time for capture://");
    }

    public void startImpl() throws MediaException {
        new Thread(this).start();
    }

    public void stopImpl() throws MediaException {
    }

    public Control getControl(String s) {
    	if(state == UNREALIZED) throw new IllegalStateException();
        if (s.equals("VideoControl")) {
            return videoControl;
        }
        return null;
    }

    public Control[] getControls() {
    	if(state == UNREALIZED) throw new IllegalStateException();
        return new Control[]{videoControl};
    }
    
    
	public void run() {
		while(state == STARTED){
			try{
				Thread.sleep(200);
			}
			catch (Exception e) {
			}
			videoControl.tick();
		}
	}


}

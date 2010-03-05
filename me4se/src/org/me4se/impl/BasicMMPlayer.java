package org.me4se.impl;

import java.util.Vector;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.TimeBase;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.midlet.ApplicationManager;

public class BasicMMPlayer implements Player, VolumeControl {

	/** Send start, stop etc notifications automatically when start(), stop() etc. is called */
	boolean autoNotify;
	protected long desiredTime = -1;
    protected int loopCount = 1;
    protected long duration = TIME_UNKNOWN;
    protected BasicMMDataSource dataSource;
    
	public BasicMMPlayer(boolean autoNotify){
		this.autoNotify = autoNotify;
	}
	
	class PlayerNotifier implements Runnable {
		String event;
		Object param;

		PlayerNotifier(String event, Object param){
			this.event = event;
			this.param = param;
		}

		public void run(){
			notifyListeners(event, param);
		}
	}
	
    TimeBase timeBase = TimeBaseImpl.defaultInstance;
   
    
	private Vector listeners = new Vector();

    protected int state = UNREALIZED;
    
    public void setSource(BasicMMDataSource ds){
    	this.dataSource = ds;
    }

    public void addPlayerListener(PlayerListener playerlistener) {
        listeners.addElement(playerlistener);
    }

    
    public int getState(){
        return state;
    }
    
    /** Use this method in the player implementation to notify all listeners... */
    
    public void notifyListeners(String event, Object param) {
		for (int i = 0; i < listeners.size(); i++) {
			try {
				((PlayerListener) listeners.elementAt(i)).playerUpdate(BasicMMPlayer.this, event, param);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

    }
    
    
    /** Use this method in the player implementation to notify all listeners in a background thread... */
    
    public void startListenerNotification(String event, Object param) {
    	if(listeners.size() != 0){
    		new Thread(new PlayerNotifier(event, param)).start();
    	}
    }
    
    
    public TimeBase getTimeBase(){
        return timeBase;
    }
    
    public void setTimeBase(TimeBase master) throws MediaException {
        if(state == UNREALIZED || state == STARTED) {
                   throw new IllegalStateException();
        }
        this.timeBase = master;
    }

    public void removePlayerListener(PlayerListener playerlistener) {
    	listeners.removeElement(playerlistener);
    }
	
    
    public void close() {
       // System.out.println("MIDI close called");

        if(state == CLOSED) return;
        if(state == STARTED) {
        	deallocate();
        }
        closeImpl();
        state = CLOSED;
        if(autoNotify){
        	startListenerNotification(PlayerListener.CLOSED, null);
        }
    }
    
    
    public void deallocate() {
        if(state == REALIZED || state == UNREALIZED) return;
        if(state == CLOSED) throw new IllegalStateException();
        if(state == STARTED ){
            try{
                stop();
            }
            catch(MediaException e){
            }
        }
        deallocateImpl();
        state = REALIZED;
    }

    
    public void prefetch() throws MediaException {
        if(state == UNREALIZED){
            realize();
        }
        if(state == PREFETCHED || state == STARTED) return;
        if(state == CLOSED) throw new IllegalStateException();
        try{
        	prefetchImpl();
        }
        catch(Exception e){
        	e.printStackTrace();
        	throw new MediaException(e.toString());
        }
        state = PREFETCHED;
    }


    public void realize() throws MediaException {
        if(state == PREFETCHED || state == STARTED) return;
        if(state == CLOSED) throw new IllegalStateException();
        try{
        	realizeImpl();
        }
        catch(Exception e){
        	e.printStackTrace();
        	throw new MediaException(e.toString());
        }

        state = REALIZED;
    }




    public void start() throws MediaException {
    	
    	//System.out.println("START REQUEST: "+this);
    	
        if(state == CLOSED) {
            throw new IllegalStateException();
        }

        if(state == STARTED) return;
        
        if(state == UNREALIZED || state == REALIZED){
            prefetch();
        }
        try{
        	startImpl();
        	ApplicationManager.getInstance().activePlayers.addElement(this);
    	}
        catch(MediaException e){
        	throw e;
        }
    	catch(Exception e){
    		e.printStackTrace();
    		throw new MediaException(e.toString());
    	}

        state = STARTED;
        if(autoNotify){
        	startListenerNotification(PlayerListener.STARTED, null);
        }
    }


    
    public void stop() throws MediaException {
        //System.out.println("MMPlayer stop called");
        if(state == CLOSED) throw new IllegalStateException();
        if(state != STARTED) return;
        try{
        	stopImpl();
        	ApplicationManager.getInstance().activePlayers.removeElement(this);
        }
        catch(Exception e){
        	e.printStackTrace();
        	throw new MediaException(e.toString());
        }
        state = PREFETCHED;
        if(autoNotify){
        	startListenerNotification(PlayerListener.STOPPED, null);
        }
    }
    
    // The following methods should all be implmeneted in an actual player...
    
    
    public void closeImpl(){
     //   System.out.println("BasicMMPlayer.closeImpl()");
    }

    public void deallocateImpl(){
     //   System.out.println("BasicMMPlayer.deallocateImpl()");
    }

    public void prefetchImpl() throws Exception{
     //   System.out.println("BasicMMPlayer.prefetchImpl()");
    }

    public void realizeImpl() throws Exception{
   //     System.out.println("BasicMMPlayer.realizeImpl()");
    }
    
    public void stopImpl() throws Exception{
        System.out.println("BasicMMPlayer.stopImpl()");
    }
    
    public void startImpl() throws Exception{
        System.out.println("BasicMMPlayer.startImpl()");
    }

    public long setMediaTime(long l) throws MediaException {
        desiredTime = l;
        return l;
    }

    public long getMediaTime() {
        System.out.println("BasicMMPlayer.getMediaTime()");
        return TIME_UNKNOWN;
    }

    public long getDuration() {
        return duration;
    }

    public String getContentType() {
    	
    	if (state == UNREALIZED)
    		throw new IllegalStateException();
    		
    	return dataSource.getContentType();

    }

    public void setLoopCount(int i) {
       if (state == STARTED || state == CLOSED) {
           throw new IllegalStateException();
        }

        loopCount = i;
    }

    public Control[] getControls() {
        return new Control[]{this};
    }

    public Control getControl(String s) {
    	return s.endsWith("VolumeControl") ? this : null;
    }


    public void setMute(boolean mute) {
        System.out.println("VolumeControl.setMute("+mute+")");
    }

    public boolean isMuted() {
        System.out.println("VolumeControl.getMute()");
        return false;
    }

    public int setLevel(int level) {
        System.out.println("VolumeControl.setLevel("+level+")");
        return 100;
    }

    public int getLevel() {
        System.out.println("VolumeControl.getLevel()");
        return 100;
    }

}

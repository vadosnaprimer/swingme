package javax.microedition.media;

/**
 * @API MIDP-2.0
 * @API MMAPI-1.0
 */ 
public interface Player extends Controllable {

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final int UNREALIZED = 100;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final int REALIZED = 200;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final int PREFETCHED = 300;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 	
	public static final int STARTED = 400;
	
	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 	
	public static final int CLOSED = 0;
	
	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 	
	public static final long TIME_UNKNOWN = -1;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void realize() throws MediaException;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void prefetch() throws MediaException;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void start() throws MediaException;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void stop() throws MediaException;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void deallocate();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void close();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract long setMediaTime(long l) throws MediaException;

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract long getMediaTime();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract int getState();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract long getDuration();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract String getContentType();

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void setLoopCount(int i);

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void addPlayerListener(PlayerListener playerlistener);

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void removePlayerListener(PlayerListener playerlistener);
	
	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public void setTimeBase(TimeBase master) throws MediaException;
	
	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public TimeBase getTimeBase();	
}

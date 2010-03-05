package javax.microedition.media;

/**
 * @API MIDP-2.0
 * @API MMAPI-1.0
 */ 
public interface PlayerListener {

  /** 
   * @API JSR-135
   */
  public static final String BUFFERING_STARTED = "bufferingStarted";
  
  /** 
   * @API JSR-135
   */
  public static final String BUFFERING_STOPPED = "bufferingStopped";


  /**
   * @API MIDP-2.0
   * @API MMAPI-1.0
   */ 
  public static final String CLOSED = "closed";

    /**
     * @API MIDP-2.0
     * @API MMAPI-1.0
     */ 
    public static final String DEVICE_AVAILABLE = "deviceAvailable";
    
    /**
     * @API MIDP-2.0
     * @API MMAPI-1.0
     */ 
    public static final String DEVICE_UNAVAILABLE = "deviceUnavailable";

	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final String DURATION_UPDATED = "durationUpdated";

    /**
     * @API MIDP-2.0
     * @API MMAPI-1.0
     */ 
    public static final String END_OF_MEDIA = "endOfMedia";
    /**
     * @API MIDP-2.0
     * @API MMAPI-1.0
     */ 
    public static final String ERROR = "error";

    
    /** 
     * @API JSR-135
     */
    
      public static final String RECORD_ERROR = "recordError";

    /** 
     * @API JSR-135
     */
    
      public static final String RECORD_STARTED = "recordStarted";
    
      /** 
       * @API JSR-135
       */
      
      public static final String RECORD_STOPPED = "recordStopped";

    
    
    
    /** 
     * @API JSR-135 
     */
    public static final String SIZE_CHANGED = "sizeChanged";

    
    /**
     * @API MIDP-2.0
     * @API MMAPI-1.0
     */ 
    public static final String STARTED = "started";
    
    /**
     * @API MIDP-2.0
     * @API MMAPI-1.0
     */ 
    public static final String STOPPED = "stopped";

    /** 
     * @API JSR-135
     */
    public static final String STOPPED_AT_TIME = "stoppedAtTime";
      

    
    /**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public static final String VOLUME_CHANGED = "volumeChanged";

	
	/**
	 * @API MIDP-2.0
	 * @API MMAPI-1.0
	 */ 
	public abstract void playerUpdate(Player player, String event, Object eventData);

    

}

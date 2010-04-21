package javax.microedition.media;

public interface PlayerListener {
	static String	CLOSED  = "closed";
	static String	DEVICE_AVAILABLE = "deviceAvailable";
	static String	DEVICE_UNAVAILABLE  = "deviceUnavailable";
	static String	DURATION_UPDATED = "durationUpdated";
	static String	END_OF_MEDIA = "endOfMedia";
	static String	ERROR = "error";
	static String	STARTED = "started";
	static String	STOPPED = "stopped";
	static String	VOLUME_CHANGED = "volumeChanged";

	void playerUpdate(Player player, String event, Object eventData);
}

package javax.microedition.media;

public interface Player extends Controllable {
	static int	CLOSED = 0;
	static int  UNREALIZED = 100;
	static int	REALIZED = 200;
    static int  PREFETCHED = 300;
	static int	STARTED = 400;
	static long	TIME_UNKNOWN = -1l;

	void	addPlayerListener(PlayerListener playerListener);
	void	close() ;
	void	deallocate() ;
	String	getContentType() ;
	long	getDuration() ;
	long	getMediaTime();
	int	    getState() ;
	void	prefetch() throws MediaException;
	void	realize() throws MediaException;
	void	removePlayerListener(PlayerListener playerListener);
	void	setLoopCount(int count);
	long	setMediaTime(long now) throws MediaException;
	void	start() throws MediaException;
	void	stop() throws MediaException;
}

package javax.obex;

import javax.microedition.io.Connection;

public interface SessionNotifier extends Connection {
	
	public Connection acceptAndOpen(ServerRequestHandler handler);
	
	public Connection acceptAndOpen(ServerRequestHandler handler, Authenticator auth);
}
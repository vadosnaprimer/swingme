package javax.obex;

import javax.microedition.io.Connection;

public interface ClientSession extends Connection {

	public HeaderSet connect(HeaderSet headers);
	
	public HeaderSet createHeaderSet();

	public HeaderSet delete(HeaderSet headers);
	
	public HeaderSet disconnect(HeaderSet headers);

	public Operation get(HeaderSet headers);

	public long	getConnectionID();
	
	public Operation put(HeaderSet headers);
	
	public void	setAuthenticator(Authenticator auth);
	
	public void setConnectionID(long id);

	public HeaderSet setPath(HeaderSet headers, boolean backup, boolean create);
	
}
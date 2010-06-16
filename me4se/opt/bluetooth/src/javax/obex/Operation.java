package javax.obex;

import javax.microedition.io.ContentConnection;

public interface Operation extends ContentConnection {

	public void abort();
	
	public HeaderSet getReceivedHeaders();
	
	public int getResponseCode();
	
	public void sendHeaders(HeaderSet headers); 
}
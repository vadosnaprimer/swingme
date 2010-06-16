package javax.obex;

public class ServerRequestHandler {

	protected 	ServerRequestHandler() {
		
	}

	public HeaderSet createHeaderSet() {
		return null;
	}
	
	public long getConnectionID() {
		return Long.MIN_VALUE;
	}
	
	public void onAuthenticationFailure(byte[] userName) {
		
	}
	
	public int onConnect(HeaderSet request, HeaderSet reply) {
		return Integer.MIN_VALUE;
	}
	
	public int onDelete(HeaderSet request, HeaderSet reply) {
		return Integer.MIN_VALUE;
	}
	
	public void onDisconnect(HeaderSet request, HeaderSet reply) {
		
	}
	
	public int onGet(Operation op) {
		return Integer.MIN_VALUE;
	}
	
	public int onPut(Operation op) {
		return Integer.MIN_VALUE;
	}
	
	public int onSetPath(HeaderSet request, HeaderSet reply, boolean backup, boolean create) {
		return Integer.MIN_VALUE;
	}
	
	public void setConnectionID(long id) {
		
	}	
}
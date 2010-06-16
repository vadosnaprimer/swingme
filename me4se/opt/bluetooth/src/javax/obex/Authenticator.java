package javax.obex;

public interface Authenticator {

	public PasswordAuthentication onAuthenticationChallenge(String description, boolean isUserIdRequired, boolean isFullAccess);
	public byte[] onAuthenticationResponse(byte[] userName); 
}
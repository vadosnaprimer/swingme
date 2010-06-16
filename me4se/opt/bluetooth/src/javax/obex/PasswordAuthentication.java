package javax.obex;

public class PasswordAuthentication {

	private byte[] userName;
	private byte[] password;
	
	public PasswordAuthentication(byte[] userName, byte[] password) {
		this.userName = userName;
		this.password = password;
	}
 
    public byte[] getPassword() {
		return password;
	}

	public byte[] getUserName() {
		return userName;
	}
}
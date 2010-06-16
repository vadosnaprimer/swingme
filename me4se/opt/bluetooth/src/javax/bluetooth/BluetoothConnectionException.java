package javax.bluetooth;

import java.io.IOException;

public class BluetoothConnectionException extends IOException {

	public static int FAILED_NOINFO = 0x0004;
	public static int NO_RESOURCES = 0x0003;
	public static int SECURITY_BLOCK = 0x0002;
	public static int TIMEOUT = 0x0005;
	public static int UNACCEPTABLE_PARAMS = 0x0006;
	public static int UNKNOWN_PSM = 0x0001;

	public BluetoothConnectionException(int error) {
		
	}
	
	public BluetoothConnectionException(int error, String msg) {
		
	}
	
	public int getStatus() {
		return Integer.MIN_VALUE;
	}	
}

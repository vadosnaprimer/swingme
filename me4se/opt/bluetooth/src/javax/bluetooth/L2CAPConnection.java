package javax.bluetooth;

import javax.microedition.io.Connection;

public interface L2CAPConnection extends Connection {

	public static int DEFAULT_MTU = 0x02A0;
	public static int MINIMUM_MTU = 0x30;

	public int getReceiveMTU();
	
	public int getTransmitMTU();
	
	public boolean ready();
	
	public int receive(byte[] inBuf);

	public void send(byte[] data);
}
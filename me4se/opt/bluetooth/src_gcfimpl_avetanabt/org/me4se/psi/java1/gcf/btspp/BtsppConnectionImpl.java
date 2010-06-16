package org.me4se.psi.java1.gcf.btspp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.me4se.impl.ConnectionImpl;

import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.obex.OBEXConnection;
import de.avetana.bluetooth.rfcomm.RFCommConnectionImpl;
import de.avetana.bluetooth.stack.BluetoothStack;

public class BtsppConnectionImpl extends ConnectionImpl implements
		StreamConnection {

	private static BluetoothStack stack = null;

	private Connection conn = null;

	private String url = null;

	public DataOutputStream openDataOutputStream() throws IOException {
		System.out.println("BtsppConnectionImpl.openDataOutputStream() called NYI !");
		return null;
	}

	public OutputStream openOutputStream() throws IOException {
		try {
			JSR82URL jurl = new JSR82URL(url);
			if (jurl.getProtocol() != JSR82URL.PROTOCOL_RFCOMM)	throw new IOException("Only RFComm connection provide an OutputStream");
			if (conn instanceof StreamConnection)
				return ((StreamConnection) conn).openOutputStream();
			else
				throw new IOException("Could not get Stream from connection");
		} catch (Exception ex) {
			throw new IOException(ex.getMessage());
		}
	}

	public DataInputStream openDataInputStream() throws IOException {
		System.out.println("BtsppConnectionImpl.openDataInputStream() called NYI !");
		return null;
	}

	public InputStream openInputStream() throws IOException {
		try {
			JSR82URL jurl = new JSR82URL(url);
			if (jurl.getProtocol() != JSR82URL.PROTOCOL_RFCOMM)	throw new IOException("Only RFComm connection provide an InputStream");
			if (conn instanceof StreamConnection)
				return ((StreamConnection) conn).openInputStream();
			else if (conn instanceof StreamConnectionNotifier)
				return ((StreamConnectionNotifier) conn).acceptAndOpen().openInputStream();
			else
				throw new IOException("Could not get Stream from connection");
		} catch (Exception ex) {
			throw new IOException(ex.getMessage());
		}
	}

	public void close() throws IOException {
		conn.close();
	}

	public void initialise(Properties properties) {
		System.out.println("init btspp connection...");
		if (stack == null) {
			try {
				stack = BluetoothStack.getBluetoothStack();
				System.out.println("bt stack initialized!");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());

			}
		}
		System.out.println("init btspp connection done!");
	}

	public void open(String url, int mode, boolean timeouts) throws IOException {
		this.url = url;
		initialise(null);
		try {
			JSR82URL myURL = new JSR82URL(url);

			if (myURL.getProtocol() == JSR82URL.PROTOCOL_RFCOMM)
				conn = stack.openRFCommConnection(myURL, 1000);
			else if (myURL.getProtocol() == JSR82URL.PROTOCOL_L2CAP)
				conn = stack.openL2CAPConnection(myURL, 1000);
			if (myURL.getProtocol() == JSR82URL.PROTOCOL_OBEX)
				conn = new OBEXConnection((RFCommConnectionImpl) stack.openRFCommConnection(myURL, 1000));

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}
}

package org.me4se.psi.java1.gcf.serversocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.me4se.impl.ConnectionImpl;
import org.me4se.psi.java1.gcf.socket.*;

public class ServersocketConnectionImpl extends ConnectionImpl implements StreamConnectionNotifier {

	ServerSocket serverSocket;

	public void initialise(Properties properties) {
	}

	public void open(String url, int mode, boolean timeouts) throws IOException {

		serverSocket = new ServerSocket(Integer.parseInt(url.substring(url.lastIndexOf(':') + 1)));
	}

	public StreamConnection acceptAndOpen() throws IOException {

		SocketConnectionImpl c = new SocketConnectionImpl();
		c.socket = serverSocket.accept();

		return c;
	}

	public void close() throws IOException {
		serverSocket.close();
	}

}
package org.me4se.psi.java1.gcf.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.ConnectionImpl;

public class SocketConnectionImpl extends ConnectionImpl implements StreamConnection, SocketConnection {


	public Socket socket;
	DataOutputStream outputStream;
	
	public void open(String url, int mode, boolean timeouts) throws IOException {
		// socket://
		
		String proxy = ApplicationManager.getInstance().getProperty("me4se.socketproxy");
        
        // System.out.println("Connecting to: "+url);
        
		int cut = url.lastIndexOf(':');

		String host;
		int port;

		if (cut >= 9) {
			host = url.substring(9, cut);
			port = Integer.parseInt(url.substring(cut + 1));
		} else {
			host = url.substring(9);
			port = 80;
		}

		if (proxy == null || proxy.trim().length() == 0) {
			socket = new Socket(host, port);
		} else {
			if(proxy.startsWith("socket://")){
				proxy = proxy.substring(9);
			}
			cut = proxy.indexOf(':');
			socket = new Socket(
					proxy.substring(0, cut), 
					Integer.parseInt(proxy.substring(cut+1)));

			outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.writeUTF(host);
			outputStream.writeInt(port);
			outputStream.flush();
		}
	}

	public InputStream openInputStream() throws IOException {
		return socket.getInputStream();
	}

	public DataInputStream openDataInputStream() throws IOException {
	
		return new DataInputStream(openInputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		return openDataOutputStream();
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		if(outputStream == null){
			outputStream = new DataOutputStream(socket.getOutputStream());
		}
		
		return outputStream;
	}

	public void close() throws IOException {
		socket.close();
	}

    public void setSocketOption(byte option, int value) throws IllegalArgumentException, IOException {
        switch(option){
        case SocketConnection.DELAY:
            socket.setTcpNoDelay(option == 0);
            break;
        case SocketConnection.KEEPALIVE:
            socket.setKeepAlive(option != 0);
            break;
        case SocketConnection.LINGER:
            socket.setSoLinger(value != 0, value);
            break;
        case SocketConnection.RCVBUF:
            socket.setReceiveBufferSize(value);
            break;
        case SocketConnection.SNDBUF:
            socket.setSendBufferSize(value);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public int getSocketOption(byte option) throws IllegalArgumentException, IOException {
        switch(option){
        case SocketConnection.DELAY:
            return socket.getTcpNoDelay() ? 0 : 1;
        case SocketConnection.KEEPALIVE:
            return socket.getKeepAlive() ? 1 : 0;  
        case SocketConnection.LINGER:
            return socket.getSoLinger();
        case SocketConnection.RCVBUF:
            return socket.getReceiveBufferSize();
        case SocketConnection.SNDBUF:
            return socket.getSendBufferSize();
        default:
            throw new IllegalArgumentException();
        }
    }

    public String getLocalAddress() throws IOException {
        return socket.getLocalAddress().toString();
    }

    public int getLocalPort() throws IOException {
        return socket.getLocalPort();
    }

    public String getAddress() throws IOException {
        return socket.getRemoteSocketAddress().toString();
    }

    public int getPort() throws IOException {
        return socket.getPort();
    }

}
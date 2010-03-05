/**
 * Re-implemented 2004-11-01 SH/MK
 * 
 * @author Mario A. Negro Ponzi
 */

package org.me4se.psi.java1.gcf.datagram;

import javax.microedition.io.*;

import org.me4se.impl.ConnectionImpl;

import java.io.*;
import java.net.*;
import java.util.*;


public class DatagramConnectionImpl extends ConnectionImpl implements UDPDatagramConnection {
	DatagramSocket datagramsocket;
	String host;
	int port;
	
    public void open (String url, int mode, boolean timeouts) throws IOException {
		int cut = url.lastIndexOf(':');
		if (cut >= 11) {
			host = url.substring(11, cut);
			port = Integer.parseInt(url.substring(cut + 1));
		} else {
			// if port is not specified let the system decide
			host = url.substring(11);
		}

		// if host is not specified, we are in server mode. bind to local port
		// otherwise let system pick a free port.
		if (host.length() == 0)
			datagramsocket=new DatagramSocket(port);
		else
			datagramsocket=new DatagramSocket();
	}

    public void close () throws IOException {
    	datagramsocket.close();
    }

	public void initialise( Properties properties ) {
	}
	
	public int getMaximumLength() throws IOException {
		return 1024;
	}
	
	public int getNominalLength() throws IOException {
		return 1024;
	}
	
	public void send(Datagram dgram) throws IOException {
        
        datagramsocket.send(((DatagramImpl) dgram).getPacket());
	}

	public void receive(Datagram dgram) throws IOException {

        DatagramPacket dp = ((DatagramImpl) dgram).getPacket();
        datagramsocket.receive(dp);

        dgram.setData(dp.getData(), dp.getOffset(), dp.getLength());
        dgram.setLength(dp.getLength());
	}

	public Datagram newDatagram(int size) throws IOException {
		return newDatagram(new byte[size], size, "datagram://"+host+":"+Integer.toString(port));
	}

	public Datagram newDatagram(int size, String addr) throws IOException {
		return newDatagram(new byte[size], size, addr);
	}

	public Datagram newDatagram(byte[] buf, int size) throws IOException{
		return newDatagram(buf, size, "datagram://"+host+":"+Integer.toString(port));
		}

	public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
		return new DatagramImpl(buf, size, addr);
	}

	public String getLocalAddress() throws IOException
	{
		return datagramsocket.getLocalAddress().getHostAddress();
	}

	public int getLocalPort() throws IOException
	{
		return datagramsocket.getLocalPort();
	}
}
	
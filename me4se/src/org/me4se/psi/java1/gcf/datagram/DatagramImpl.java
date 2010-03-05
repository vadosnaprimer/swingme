/**
 *  Re-implemented 2004-11-01 SH/MK
 * 
 * @author Mario A. Negro Ponzi
 */

package org.me4se.psi.java1.gcf.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.microedition.io.Datagram;


public class DatagramImpl implements Datagram {
	
	//public DatagramPacket dp;
	int rwPointer = 0;
	byte[] data;
    int length;
    int offset;
    String address;
    
	public DatagramImpl(byte[] buf, int size, String addr) throws IOException {
		//dp = new DatagramPacket(buf, size);
        data = buf;
        this.length = size;
        this.address = addr;
	}

	public String getAddress() {
		try {
			return address;
		}
		catch (Exception e) {
			return (null);
		}
	}

	public byte[] getData() {
		return data;
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	public void setAddress(String addr) throws IOException {
        this.address = addr;
        /*
		if (addr == null)
			throw new IOException();
		else {
			int cut = addr.lastIndexOf(':');
			String host;
			int port = -1;

			if (cut >= 11) {
				host = addr.substring(11, cut);
				port = Integer.parseInt(addr.substring(cut + 1));
			}
			else {
				host = addr.substring(11);
				port = -1;
			}
			dp.setAddress(InetAddress.getByName(host));
			if (port != -1)
				dp.setPort(port);
		}*/
	}

	public void setAddress(Datagram reference) {
		try {
			setAddress(reference.getAddress());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLength(int len) {
        length = len;
//		dp.setLength(len);
	}

	public void setData(byte[] buffer, int offset, int len) {
        System.arraycopy(buffer, offset, data, 0, len);
	}

    
    DatagramPacket getPacket() throws UnknownHostException{
        
        int cut = address.lastIndexOf(':');
        String host;
        int port = -1;

        if (cut >= 11) {
            host = address.substring(11, cut);
            port = Integer.parseInt(address.substring(cut + 1));
        }
        else {
            host = address.substring(11);
            port = -1;
        }
        
        DatagramPacket dp = new DatagramPacket(data, offset, length);
        dp.setAddress(InetAddress.getByName(host));
        if (port != -1)
            dp.setPort(port);
        
        return dp;

    }

    InetAddress getInetAddress() throws UnknownHostException{
        int cut = address.lastIndexOf(':');
    	return InetAddress.getByName(address.substring(12, cut));
    }
    

	public void reset() {
		rwPointer = 0;
        offset = 0;
        length = 0;
	}

	public boolean readBoolean() throws IOException {
		return readByte() != 0;
	}
	
	public byte readByte() throws IOException {
		return data[rwPointer++];
	}
	
	public char readChar() throws IOException {
		return (char) ((readByte() << 8) | (readByte() & 0xff));
	}
	
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}
	
	public void readFully(byte[] b, int off, int len) throws IOException {
		if (b == null)
			throw new NullPointerException();
		if (off < 0)
			throw new IndexOutOfBoundsException();
		if (len < 0)
			throw new IndexOutOfBoundsException();

		System.arraycopy(data, 0, b, off, len);
		rwPointer += len;
	}
	
	public int readInt() throws IOException {
		return (((readByte() & 0xff) << 24) | ((readByte() & 0xff) << 16) | ((readByte() & 0xff) << 8) | (readByte() & 0xff));

	}
	
	public long readLong() throws IOException {
		return (
			((long) (readByte() & 0xff) << 56)
				| ((long) (readByte() & 0xff) << 48)
				| ((long) (readByte() & 0xff) << 40)
				| ((long) (readByte() & 0xff) << 32)
				| ((long) (readByte() & 0xff) << 24)
				| ((long) (readByte() & 0xff) << 16)
				| ((long) (readByte() & 0xff) << 8)
				| ((long) (readByte() & 0xff)));

	}
	
	public short readShort() throws IOException {
		return (short) ((readByte() << 8) | (readByte() & 0xff));
	}
	
	public int readUnsignedByte() throws IOException {
		return (int) data[rwPointer++];
	}
	
	public int readUnsignedShort() throws IOException {
		return (((readByte() & 0xff) << 8) | (readByte() & 0xff));
	}
	
	public String readUTF() throws IOException{
		int len = readShort();
        byte[] buf = new byte[len];
        readFully(buf);
        return new String(buf, "utf8");
	}
	
	public int skipBytes(int n) {
		rwPointer += n;
        return n;
	}

	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int off, int len) {
		System.arraycopy(b, off, data, rwPointer, len);
        rwPointer += len;
	}
	
	public void write(int b) throws IOException {
		writeByte((byte) (0xff & (b >> 24)));
		writeByte((byte) (0xff & (b >> 16)));
		writeByte((byte) (0xff & (b >> 8)));
		writeByte((byte) (0xff & b));
	}
	
	public void writeBoolean(boolean v) throws IOException {
		if (v)
			writeByte((byte) (1));
		else
			writeByte((byte) (0));
	}
	
	public void writeByte(int v) throws IOException {
		writeByte((byte) v);
	}
	
	public void writeChar(int v) throws IOException {
		writeByte((byte) (0xff & (v >> 8)));
		writeByte((byte) (0xff & v));
	}
	
	public void writeChars(String s) throws IOException {
		
		for(int i = 0; i < s.length(); i++){
			writeChar(s.charAt(i));
        }
	}

	public void writeInt(int v) throws IOException {
		writeByte((byte) (0xff & (v >> 24)));
		writeByte((byte) (0xff & (v >> 16)));
		writeByte((byte) (0xff & (v >> 8)));
		writeByte((byte) (0xff & v));
	}
	
	public void writeLong(long v) throws IOException {
		writeByte((byte) (0xff & (v >> 48)));
		writeByte((byte) (0xff & (v >> 40)));
		writeByte((byte) (0xff & (v >> 32)));
		writeByte((byte) (0xff & (v >> 24)));
		writeByte((byte) (0xff & (v >> 16)));
		writeByte((byte) (0xff & (v >> 8)));
		writeByte((byte) (0xff & v));
	}
	
	public void writeShort(int v) throws IOException {
		writeByte((byte) (0xff & (v >> 8)));
		writeByte((byte) (0xff & v));
	}
	
	public void writeUTF(String str) throws IOException {
        byte[] buf = str.getBytes("utf8");
		writeShort(buf.length);
        write(buf);
	}

	public float readFloat() {
		throw new RuntimeException("Datagram.readFloat() not yet implemented.");
	}
	
	public double readDouble() {
		throw new RuntimeException("Datagram.readDouble() not yet implemented.");
	}
	
	public String readLine() {
		throw new RuntimeException("Datagram.readLine() not yet implemented.");
	}
	
	public void writeFloat(float f) {
		throw new RuntimeException("Datagram.writeFloat() not yet implemented.");	
	}
	
	public void writeDouble(double d) {
		throw new RuntimeException("Datagram.writeDouble() not yet implemented.");	
	}
	
	public void writeBytes(String s) {
		throw new RuntimeException("Datagram.writeBytes() not yet implemented.");
	}
}
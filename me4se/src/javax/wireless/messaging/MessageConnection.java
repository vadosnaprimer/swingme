package javax.wireless.messaging;

import javax.microedition.io.*;
import java.io.*;

public interface MessageConnection extends Connection {

	public static String BINARY_MESSAGE = "binary";
	public static String TEXT_MESSAGE   = "text";

	public Message newMessage(java.lang.String type);
	public Message newMessage(java.lang.String type, java.lang.String address);
	public int numberOfSegments(Message msg);
	public Message receive() throws IOException,InterruptedIOException;
	public void send(Message msg) throws IOException, InterruptedIOException;
	public void setMessageListener(MessageListener l) throws IOException;
}

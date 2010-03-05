package org.me4se.psi.java1.gcf.sms;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

import org.me4se.impl.ConnectionImpl;

public class SmsConnectionImpl extends ConnectionImpl implements MessageConnection {

  private String url;
  
  public Message newMessage(String type, String address) {
    System.out.println("ME4SE: SmsConnectionImpl.newMessage(String type='" + type + "', String address='" + address + "')");
    return null;
  }

  public Message newMessage(String type) {
    System.out.println("ME4SE: SmsConnectionImpl.newMessage(String type='" + type + "')");    
    if (type == MessageConnection.TEXT_MESSAGE) {
      TextMessageImpl msg = new TextMessageImpl();
      // URL Handling must be improved here !
      msg.setAddress(url.substring(url.indexOf("//")+2));
      return msg;
    }
    else {
      throw new RuntimeException("*** ME4SE: Binary Messages are NYI!");
    }
  }

  public int numberOfSegments(Message msg) {
    System.out.println("ME4SE: SmsConnectionImpl.numberOfSegments(Message msg='" + msg + "')");    
    return 0;
  }

  public Message receive() throws IOException, InterruptedIOException {
    System.out.println("ME4SE: SmsConnectionImpl.receive()");
    return null;
  }

  public void send(Message msg) throws IOException, InterruptedIOException {
    System.out.println("ME4SE: SmsConnectionImpl.send(Message msg='" + msg + "')");
  }

  public void setMessageListener(MessageListener l) throws IOException {
    System.out.println("ME4SE: SmsConnectionImpl.setMessageListener(MessageListener l='" + l + "')");
  }

  public void close() throws IOException {
    System.out.println("ME4SE: SmsConnectionImpl.close()");
  }

  public void open(String url, int mode, boolean timeouts) throws IOException {
    this.url = url;
    System.out.println("ME4SE: SmsConnectionImpl.open(String url='" + url + "', int mode='" + mode + "', boolean timeouts='" + timeouts + "')");    
  }
}

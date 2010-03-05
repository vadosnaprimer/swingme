package org.me4se.psi.java1.gcf.sms;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.wireless.messaging.Message;
import javax.wireless.messaging.TextMessage;

public class TextMessageImpl implements TextMessage {

  private String address;
  private Date timestamp;
  private String payload;
  
  
  public TextMessageImpl() {
    timestamp = new Date(System.currentTimeMillis());
  }
  
  public String getAddress() {
    return address;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setAddress(String addr) {
    this.address = addr;
  }

  public String getPayloadText() {
    return payload;
  }

  public void setPayloadText(String data) {
    this.payload = data;
  }
  
  public String toString() {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return "TextMessageImpl: address='" + address +"', timestamp='" + dateFormatter.format(timestamp)+ "', payload='" + payload + "'";
  }
}

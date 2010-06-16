package org.me4se.psi.java1.nfc;

import javax.microedition.contactless.ndef.NDEFRecordListener;
import javax.microedition.contactless.ndef.NDEFRecordType;

public class NDEFRecordListenerContainer {

  private NDEFRecordListener listener;
  private NDEFRecordType recordType;
  
  public NDEFRecordListenerContainer(NDEFRecordListener listener, NDEFRecordType recordType) {
    this.listener = listener;
    this.recordType = recordType;
  }
  
  public NDEFRecordListener getListener() {
    return listener;
  }
  
  public NDEFRecordType getRecordType() {
    return recordType;
  }
}
package org.me4se.psi.java1.nfc;

import javax.microedition.contactless.*;

public class SimpleTargetProperties implements TargetProperties {

  /*
   * The UID of the target
   */
  private String uid;
  
  public SimpleTargetProperties(String uid) {
    this.uid = uid;
  }
  
  public Class[] getConnectionNames() {
    System.out.println("ME4SE-NFC: SimpleTargetProperties.getConnectionNames() called.");
    
    Class[] result = new Class[2];
    try {
      result[0] = Class.forName("javax.microedition.contactless.rf.PlainTagConnection");
      result[1] = Class.forName("javax.microedition.contactless.rf.PlainTagConnection");      
    }
    catch (Exception ex) {
      System.out.println("Error in getConnectionNames(): " + ex.getMessage());
    }
    return result;
  }

  public String getMapping() {
    System.out.println("ME4SE-NFC: SimpleTargetProperties.getMapping() called.");
    return "ME4SE:Mapping";
  }

  public String getProperty(String name) {
    System.out.println("ME4SE-NFC: SimpleTargetProperties.getProperty(String name='" + name + "') called.");
    return null;
  }

  public TargetType[] getTargetTypes() {
    System.out.println("ME4SE-NFC: SimpleTargetProperties.getTargetTypes() called.");
    return new TargetType[]{TargetType.RFID_TAG};
  }

  public String getUid() {
    System.out.println("ME4SE-NFC: SimpleTargetProperties.getUid() called.");
    return uid;
  }

  public String getUrl() {
    System.out.println("ME4SE-NFC: SimpleTargetProperties.getUrl() called.");
    return "ME4SE:RFID";
  }

  public String getUrl(Class connectionName) {
    System.out.println("ME4SE-NFC: SimpleTargetProperties.getUrl(Class connectionName='" + connectionName + "') called.");
    return "ME4SE:RFID:" + connectionName;
  }

  public boolean hasTargetType(TargetType type) {    
    System.out.println("ME4SE-NFC: SimpleTargetProperties.hasTargetType(TargetType type='" + type + "') called.");
    return false;
  }
}
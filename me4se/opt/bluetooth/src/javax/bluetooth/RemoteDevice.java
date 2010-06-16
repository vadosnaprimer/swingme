package javax.bluetooth;

import java.io.IOException;

import javax.microedition.io.Connection;

public class RemoteDevice {

  protected RemoteDevice(String address) {
  }

  public boolean authenticate() {
    return false;
  }

  public boolean authorize(Connection conn) {
    return false;
  }

  public boolean encrypt(Connection conn, boolean on) throws IOException {
    return false;
  }

  public boolean equals(Object obj) {
    return false;
  }

  public String getBluetoothAddress() {
    return null;
  }

  public String getFriendlyName(boolean alwaysAsk) throws IOException {
    return null;
  }

  public static RemoteDevice getRemoteDevice(Connection conn) throws IOException {
    return null;
  }

  public int hashCode() {
    return Integer.MIN_VALUE;
  }

  public boolean isAuthenticated() {
    return false;
  }

  public boolean isAuthorized(Connection conn) throws IOException {
    return false;
  }

  public boolean isEncrypted() {
    return false;
  }

  public boolean isTrustedDevice() {
    return false;
  }
}
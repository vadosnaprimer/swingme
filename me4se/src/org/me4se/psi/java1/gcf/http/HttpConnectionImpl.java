// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS: API Complete
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

// Andre, this is the old version; I have shifted your version
// to src_applet in order to be able to build a "simple" version
// for execution on PJava devices. 

package org.me4se.psi.java1.gcf.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.ConnectionImpl;
import org.me4se.impl.Log;

public class HttpConnectionImpl extends ConnectionImpl implements HttpConnection {

  static final String[] ESCAPE_HEADER = { "cookie", "set-cookie", "set-cookie2", "cookie2" };

  URL url;
  protected HttpURLConnection con;
  String proxy;
  int responseCode = -1;

  static final int OPEN_OUTPUT_STREAM = 1;
  static final int GET_RESPONSE_CODE = 2;
  static final int OPEN_INPUT_STREAM = 3;

  class Privileged implements PrivilegedExceptionAction {

    int action;

    Privileged(int action) {
      this.action = action;
    }

    public Object run() throws IOException {
      switch (action) {
      case OPEN_OUTPUT_STREAM:
        return con.getOutputStream();
      case OPEN_INPUT_STREAM:
        if (con.getResponseCode() >= 300)
          return con.getErrorStream();
        // try {
        return con.getInputStream();

      case GET_RESPONSE_CODE:
        return new Integer(con.getResponseCode());
      }
      throw new RuntimeException("unknown privileged action code: " + action);
    }
  }

  Object doPrivileged(int action) throws IOException {
    Privileged pa = new Privileged(action);
    try {
      return ApplicationManager.getInstance().getBooleanProperty("me4se.signed", false) ? AccessController
          .doPrivileged(pa) : pa.run();
    } catch (PrivilegedActionException e) {
      throw new SecurityException(e);
    }
  }

  String escapeKey(String key) {
    if (proxy != null && key != null) {
      for (int i = 0; i < ESCAPE_HEADER.length; i++) {
        if (ESCAPE_HEADER[i].equalsIgnoreCase(key)) {
          return "x-me4se-" + key;
        }
      }
    }

    return key;
  }

  public void open(String url, int mode, boolean timeouts) throws IOException {

    Log.log(Log.IO, "entering http open '" + url + "' mode: " + mode);

    this.url = new URL(url);
    proxy = url.startsWith("https") ? null : ApplicationManager.getInstance().getProperty("me4se.httpproxy");

    Log.log(Log.IO, "using proxy: " + proxy);

    con = proxy == null ? (HttpURLConnection) this.url.openConnection() : (HttpURLConnection) new URL(proxy)
        .openConnection();

    con.setUseCaches(false);
    boolean doOutput = (mode & Connector.WRITE) != 0;
    con.setDoOutput(doOutput);
    con.setDoInput(true);
    // con.setRequestProperty("connection", "close");
    con.setRequestProperty("User-Agent", ApplicationManager.getInstance().getProperty("me4se.useragent",
        ApplicationManager.getInstance().getProperty("microedition.platform", "MobileRunner-J2ME")));
    if (proxy != null) {
      con.setRequestProperty("me4se-target-url", url);
    }

    Log.log(Log.IO, "leaving: http open");

  }

  public long getDate() throws IOException {
    return con.getDate();
  }

  public String getEncoding() {
    return con.getContentEncoding();
  }

  public long getExpiration() throws IOException {
    return con.getExpiration();
  }

  public String getFile() {
    return url.getFile();
  }

  public String getHeaderField(String key) throws IOException {
    getResponseCode();

    return con.getHeaderField(escapeKey(key));
  }

  public String getHeaderField(int index) throws IOException {
    getResponseCode();
    return con.getHeaderField(index);
  }

  public int getHeaderFieldInt(String key, int def) throws IOException {
    getResponseCode();
    return con.getHeaderFieldInt(escapeKey(key), def);
  }

  public String getHeaderFieldKey(int index) throws IOException {
    getResponseCode();
    String key = con.getHeaderFieldKey(index);

    if (index == 0 && key == null && con.getHeaderField(0) != null) {
      return "x-status-line";
    }

    return key != null && key.toLowerCase().startsWith("x-me4se-") ? key.substring(8) : key;
  }

  public long getHeaderFieldDate(String key, long def) throws IOException {
    getResponseCode();
    return con.getHeaderFieldDate(escapeKey(key), def);
  }

  public String getHost() {
    return url.getHost();
  }

  public long getLength() {
    return con.getContentLength();
  }

  public long getLastModified() throws IOException {
    return con.getLastModified();
  }

  public int getPort() {
    return url.getPort();
  }

  public String getProtocol() {
    return url.getProtocol();
  }

  public String getQuery() {
    String s = url.toString();
    int cut = s.indexOf('?');
    return cut == -1 ? null : s.substring(cut + 1);
  }

  public String getRef() {
    return url.getRef();
  }

  public int getResponseCode() throws IOException {
    if (responseCode == -1) {

      Log.log(Log.IO, "entering http getResponseCode");

      try {
        responseCode = ((Integer) doPrivileged(GET_RESPONSE_CODE)).intValue();

      } catch (FileNotFoundException fnfex) {
        responseCode = HttpConnection.HTTP_NOT_FOUND;
      } catch (IOException ioex) {
        if (ioex.getMessage().startsWith("file not found")) {
          responseCode = HttpConnection.HTTP_NOT_FOUND;
        } else {
          System.out.println("IOException: " + ioex);
          throw ioex;
        }
      }

      Log.log(Log.IO, "leaving http getResponseCode: " + responseCode);
    }

    return responseCode;
  }

  public String getRequestProperty(String name) {
    return con.getRequestProperty(escapeKey(name));
  }

  public String getType() {
    return con.getContentType();
  }

  public String getURL() {
    return url.toString();
  }

  public String getRequestMethod() {
    return con.getRequestMethod();
  }

  public String getResponseMessage() throws IOException {
    getResponseCode();
    return con.getResponseMessage();
  }

  public void setRequestMethod(String method) throws IOException {
    con.setRequestMethod(method);
  }

  public void setRequestProperty(String key, String value) throws IOException {
    key = escapeKey(key);
    String oldVal = con.getRequestProperty(key);
    // System.out.println("setRequestProperty("+key+", "+value+"); oldval:
    // "+oldVal);
    con.setRequestProperty(key, value);
  }

  public InputStream openInputStream() throws IOException {
    return (InputStream) doPrivileged(OPEN_INPUT_STREAM);
  }

  public DataInputStream openDataInputStream() throws IOException {
    return new DataInputStream(openInputStream());
  }

  public OutputStream openOutputStream() throws IOException {
    return (OutputStream) doPrivileged(OPEN_OUTPUT_STREAM);
  }

  public DataOutputStream openDataOutputStream() throws IOException {
    return new DataOutputStream(openOutputStream());
  }

  public void close() {
    con.disconnect();
  }
}
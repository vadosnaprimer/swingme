package net.yura.mobile.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public abstract class HTTPClient extends QueueProcessorThread {

    public void makeRequest(Request request) {
        addToInbox(request);
    }

    public static class Request {
        public String url;
        public Hashtable params;
        public boolean post;
        public int type;
    }

    protected abstract void onError(Request request, Exception ex);

    protected abstract void onResult(Request request, InputStream is,long length) throws IOException;


    public static byte[] getData(InputStream iStrm,int length) throws IOException {

          ByteArrayOutputStream bStrm = null;

          // ContentConnection includes a length method
          byte imageData[];

          if (length != -1) {
            imageData = new byte[length];
            // Read the png into an array
    //        iStrm.read(imageData);
            DataInputStream din = new DataInputStream(iStrm);
            din.readFully(imageData);
          }
          else { // Length not available...
            bStrm = new ByteArrayOutputStream();
            int ch;
            while ((ch = iStrm.read()) != -1) {
              bStrm.write(ch);
            }
            imageData = bStrm.toByteArray();
            bStrm.close();
          }

          return imageData;
    }

    public void process(Object arg0) throws Exception {
        Request request = (Request)arg0;

        String url = request.url;
        String getpost = null;
        if (!request.post && request.params !=null) {
            StringBuffer getpostb = new StringBuffer();
            Enumeration enu = request.params.keys();
            while(enu.hasMoreElements()) {
                Object key = enu.nextElement();
                getpostb.append( encode( String.valueOf( key ) ) );
                getpostb.append("=");
                getpostb.append(encode( String.valueOf( request.params.get(key) ) ));
                getpostb.append("&");
            }
            if (getpostb.length() >0) {
                getpostb.deleteCharAt( getpostb.length()-1 );
                getpost = getpostb.toString();
            }
        }

        HttpConnection httpConn = null;
        InputStream is = null;
        OutputStream os = null;

        try {

            if (!request.post && getpost!=null) {
                if (url.indexOf('?') >=0) {
                    url = url +"&"+getpost;
                }
                else {
                    url = url +"?"+getpost;
                }
            }

            httpConn = (HttpConnection)Connector.open(url);

            // Setup HTTP Request
            httpConn.setRequestMethod(request.post?HttpConnection.POST:HttpConnection.GET);

            //httpConn.setRequestProperty("User-Agent",
            //  "Profile/MIDP-1.0 Confirguration/CLDC-1.0");

            /** Initiate connection and check for the response code. If the
            response code is HTTP_OK then get the content from the target
            **/
            int respCode = httpConn.getResponseCode();
            if (respCode == HttpConnection.HTTP_OK) {
                //StringBuffer sb = new StringBuffer();
                os = httpConn.openOutputStream();
                is = httpConn.openInputStream();

                if (request.post) {
                    os.write(getpost.getBytes());
                    os.flush();
                    // TODO do we close here ???
                }

                onResult(request,is,httpConn.getLength());
            }
            else {
                throw new IOException("HTTP ERROR: " + respCode);
            }
        }
        catch(Exception ex) {
            onError(request,ex);
        }
        finally {
            NativeUtil.close(httpConn);
            NativeUtil.close(is);
            NativeUtil.close(os);
        }
    }

    public static String encode(String s) {
        byte[] bytes = s.getBytes();
        StringBuffer ret = new StringBuffer(s.length());
        for (int a=0;a<bytes.length;a++) {
          byte c = bytes[a];
          if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
          || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '*' || c == '_')
            ret.append((char)c);
          else if (c == ' ')
            ret.append('+');
          else {
            appendHex(c, ret);
            if (c >=128) {
              appendHex(bytes[++a], ret);
            }
            if (c >= 224) {
              appendHex(bytes[++a], ret);
            }
          }
        }
        return ret.toString();
    }

  private static void appendHex(int arg0, StringBuffer buff){
    buff.append('%');
    if (arg0 < 16) {
      buff.append('0');
    }
    buff.append(Integer.toHexString(arg0));
  }

}

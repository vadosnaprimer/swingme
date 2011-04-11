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

import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public abstract class HTTPClient extends QueueProcessorThread {

    public HTTPClient() {
        super("HTTPClient");
    }

    public void makeRequest(Request request) {
        addToInbox(request);
    }

    public static class Request {
        public String url;
        public Hashtable headers;
        public Hashtable params;
        public boolean post;
        public Object id;
        public int redirects = 5;
        public byte[] postData;
    }

    protected abstract void onError(Request request, int responseCode, Hashtable headers, Exception ex);

    protected abstract void onResult(Request request, int responseCode, Hashtable headers, InputStream is, long length) throws IOException;


    public void process(Object arg0) throws Exception {
        Request request = (Request)arg0;

        String url = request.url;
        String getpost = null;
        if ((!request.post || request.postData!=null) && request.params !=null) {
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
        int respCode = 0;
        Hashtable headers = new Hashtable();

        try {

            if ((!request.post || request.postData!=null) && getpost!=null) {
                if (url.indexOf('?') >=0) {
                    url = url +"&"+getpost;
                }
                else {
                    url = url +"?"+getpost;
                }
            }

            httpConn = (HttpConnection)Connector.open(url);
            // Setup HTTP Request
            httpConn.setRequestMethod(request.post || request.postData!=null ? HttpConnection.POST : HttpConnection.GET);

            if(request.headers!=null) {
            	Enumeration e = request.headers.keys();
            	while(e.hasMoreElements()) {
	                String key = e.nextElement().toString();
	                httpConn.setRequestProperty(key, request.headers.get(key).toString());
	            }
            }
            //httpConn.setRequestProperty("User-Agent",
            //  "Profile/MIDP-1.0 Confirguration/CLDC-1.0");
            if(request.post) {
            	// opening an output stream will automatically set the request method to POST
            	// BE Careful!!!!!
	            os = httpConn.openOutputStream();
	            if (request.postData!=null ) {
	                os.write(request.postData);
	            }
	            else if(request.post && getpost!=null) {
	                os.write(getpost.getBytes());
	            }
	            os.flush();
            }
            /** Initiate connection and check for the response code. If the
            response code is HTTP_OK then get the content from the target
             **/
            respCode = httpConn.getResponseCode();
            for (int i = 0;; i++) {
                String key = httpConn.getHeaderFieldKey(i);
                if (key == null) {
                    // NOTE: For some implementation, zero is special, and can
                    // return null. We should try again.
                    if (i > 0) {
                        break;
                    }
                }
                else {
                    headers.put(key, httpConn.getHeaderField(i));
                }
            }

            switch(respCode)
            {
                case HttpConnection.HTTP_OK:
                    is = httpConn.openInputStream();
                    onResult(request, respCode, headers, is, httpConn.getLength());
                    break;
                    //case HttpConnection.HTTP_MOVED_PERM:
                case HttpConnection.HTTP_SEE_OTHER:
                    request.post = false;
                    request.postData = null;
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                    request.url = httpConn.getHeaderField("Location");
                    if(request.url != null && request.redirects>0)
                    {
                        addToInbox(request);
                        request.redirects--;
                        break;
                    }
                default:
                    is = httpConn.openInputStream();
                    onResult(request, respCode, headers, is, httpConn.getLength());
                    break;
            }
        }
        catch(Exception ex) {
            Logger.info(ex);
            onError(request, respCode, headers, ex);
        }
        finally {
            FileUtil.close(httpConn);
            FileUtil.close(is);
            FileUtil.close(os);
        }
    }

    /**
     * @see java.net.URLEncoder#encode(java.lang.String, java.lang.String) URLEncoder.encode
     */
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

    private static void appendHex(byte b, StringBuffer buff){
        int n = b & 0xFF;
        buff.append('%');
        if (n < 16) {
            buff.append('0');
        }
        buff.append(Integer.toHexString(n));
    }


    /**
     * @see java.net.URLDecoder#decode(java.lang.String, java.lang.String) URLDecoder.decode
     */
    public static String decode(String s) {

	boolean needToChange = false;
	int numChars = s.length();
	StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
	int i = 0;

	char c;
	byte[] bytes = null;
	while (i < numChars) {
            c = s.charAt(i);
            switch (c) {
	    case '+':
		sb.append(' ');
		i++;
		needToChange = true;
		break;
	    case '%':
		/*
		 * Starting with this instance of %, process all
		 * consecutive substrings of the form %xy. Each
		 * substring %xy will yield a byte. Convert all
		 * consecutive  bytes obtained this way to whatever
		 * character(s) they represent in the provided
		 * encoding.
		 */

		try {

		    // (numChars-i)/3 is an upper bound for the number
		    // of remaining bytes
		    if (bytes == null)
			bytes = new byte[(numChars-i)/3];
		    int pos = 0;

		    while ( ((i+2) < numChars) &&
			    (c=='%')) {
			bytes[pos++] =
			    (byte)Integer.parseInt(s.substring(i+1,i+3),16);
			i+= 3;
			if (i < numChars)
			    c = s.charAt(i);
		    }

		    // A trailing, incomplete byte encoding such as
		    // "%x" will cause an exception to be thrown

		    if ((i < numChars) && (c=='%'))
			throw new IllegalArgumentException(
		         "URLDecoder: Incomplete trailing escape (%) pattern");

		    sb.append(new String(bytes, 0, pos));
		} catch (NumberFormatException e) {
		    throw new IllegalArgumentException(
                    "URLDecoder: Illegal hex characters in escape (%) pattern - "
		    + e.getMessage());
		}
		needToChange = true;
		break;
	    default:
		sb.append(c);
		i++;
		break;
            }
        }

        return (needToChange? sb.toString() : s);
    }


}

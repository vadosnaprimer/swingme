package net.yura.mobile.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
        boolean getpost = false;
        if (request.params !=null && request.params.size()>0) {
            getpost = true;
        }

        HttpConnection httpConn = null;
        InputStream is = null;
        OutputStream os = null;
        int respCode = 0;
        Hashtable headers = new Hashtable();

        try {

            // if this is a GET, or we have other data to POST
            if ((!request.post || request.postData!=null) && getpost) {

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                Writer w = new OutputStreamWriter(b);
                getPostString(request.params,w);
                w.flush();
                String getpostString = b.toString(); // this should be safe, as URL Encoded String will not have any UTF

/*              // this does the same but creates a new class "java.io.StringWriter" that we can use
                final StringBuffer buff = new StringBuffer();
                Writer writer = new Writer() { // in JavaSE we have a class
                    public void write(char[] cbuf, int off, int len) {
                        buff.append(cbuf, off, len);
                    }
                    public void flush() { }
                    public void close() { }
                };
                getPostString(request.params,writer);
                String getpostString = buff.toString();
*/

                if (url.indexOf('?') >=0) {
                    url = url +"&"+getpostString;
                }
                else {
                    url = url +"?"+getpostString;
                }
            }

            if (SocketClient.connectAppend!=null) {
                url = url + SocketClient.connectAppend;
            }

            httpConn = (HttpConnection)Connector.open(url);
            
            boolean post = request.post || request.postData!=null;
            
            // Setup HTTP Request
            httpConn.setRequestMethod(post ? HttpConnection.POST : HttpConnection.GET);

            if(request.headers!=null) {
            	Enumeration e = request.headers.keys();
            	while(e.hasMoreElements()) {
	                String key = e.nextElement().toString();
	                httpConn.setRequestProperty(key, request.headers.get(key).toString());
	            }
            }
            //httpConn.setRequestProperty("User-Agent",
            //  "Profile/MIDP-1.0 Confirguration/CLDC-1.0");
            if(post) {
            	// opening an output stream will automatically set the request method to POST
            	// BE Careful!!!!!
                try {
	            os = httpConn.openOutputStream();
	            if (request.postData!=null ) {
	                os.write(request.postData);
	            }
	            else if(request.post && getpost) {

                        Writer w = new OutputStreamWriter(os);
                        getPostString(request.params,w);
                        w.flush();

	                //os.write(getpost.getBytes());
	            }
	            os.flush();
                }
                finally {
                    FileUtil.close(os);
                }
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

            switch(respCode) {
            	// ================= OK =================
                case HttpConnection.HTTP_OK:
                    is = httpConn.openInputStream();
                    onResult(request, respCode, headers, is, httpConn.getLength());
                    break;

                // ================= MOVE =================
                //case HttpConnection.HTTP_MOVED_PERM:
                case HttpConnection.HTTP_SEE_OTHER:
                    request.post = false;
                    request.postData = null;
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                    request.url = httpConn.getHeaderField("Location");
                    if(request.url != null && request.redirects>0) {
                        addToInbox(request);
                        request.redirects--;
                        break;
                    }
                    // we can not retry any more, fall though to error

                // ================= ERORRS =================
                default:
                	onError(request, respCode, headers, null);
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
        }
    }



    public void getPostString(Hashtable params,Writer getpostb) throws IOException {

            //StringBuffer getpostb = new StringBuffer();
            Enumeration enu = params.keys();

            boolean first=true;

            while(enu.hasMoreElements()) {
                Object key = enu.nextElement();

                if (first) {
                    first=false;
                }
                else {
                    getpostb.write('&');
                }

                encode( String.valueOf( key ), getpostb );
                getpostb.write('=');
                encode( String.valueOf( params.get(key)), getpostb );
            }

            //return getpostb.toString();

    }

    /**
     * @see java.net.URLEncoder#encode(java.lang.String) URLEncoder.encode
     */
    public static String encode(String s) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream(s.length());
            Writer w = new OutputStreamWriter(b);
            encode(s,w);
            w.flush();
            return b.toString();
        }
        catch(IOException ex) {
            //#debug debug
            ex.printStackTrace();
            throw new RuntimeException( ex.toString() );
        }
    }

    /**
     * @see java.net.URLEncoder#encode(java.lang.String, java.lang.String) URLEncoder.encode
     */
    public static void encode(String s,Writer ret) throws IOException {
        for (int a=0;a<s.length();a++) {
            char c = s.charAt(a);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '*' || c == '_') {
                ret.write(c);
            }
            else if (c == ' ') {
                ret.write('+');
            }
            else {
                // we do not convert the whole string to UTF-8, but only the current char, this is better for memory
                byte[] ba = String.valueOf(c).getBytes("UTF-8"); // YURA this can fail if the system default is not UTF-8
                for (int j = 0; j < ba.length; j++) {

                    int n = ba[j] & 0xFF;
                    ret.write('%');
                    if (n < 16) {
                        ret.write('0');
                    }
                    ret.write(Integer.toHexString(n));

                    // THIS IS THE SAME AS ABOVE, ALSO WORKS
                    // the only difference is that it uses A-F and not a-f
                    //ret.write('%');
                    //int d1 = (ba[j] >> 4) & 0xF;
                    //int d2 = ba[j] & 0xF;
                    //char ch1 = (char) ((d1<10)?('0' + d1):('A' - 10 + d1));
                    //char ch2 = (char) ((d2<10)?('0' + d2):('A' - 10 + d2));
		    //ret.write( ch1 );
		    //ret.write( ch2 );
                }
            }
        }
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

                    try {
                        sb.append(new String(bytes, 0, pos,"UTF-8")); // YURA this can fail if the system default is not UTF-8
                    }
                    catch(Exception ex) {
                        throw new RuntimeException();
                    }
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

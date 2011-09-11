package net.yura.mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import net.yura.mobile.logging.Logger;

public class Url {
    // url > protocol://host[:port][/file][?query] http://g.com/home/h?a=1
    private String protocol;
    private String host;
    private int port;
    private String path;
    private String query;

    public Url(String spec) {
        protocol = searchPart(spec, 0, ":");

        int startIdx = protocol.length() + 3;
        String hostPort = searchPart(spec, startIdx, "/?");

        host = searchPart(hostPort, 0, ":");
        String portStr = searchPart(hostPort, host.length() + 1, "");
        if (portStr.length() > 0) {
            port = Integer.parseInt(portStr);
        }

        startIdx += hostPort.length() + 1;
        if (startIdx < spec.length() && spec.charAt(startIdx - 1) == '?') {
            path = "";
            query = spec.substring(startIdx);
        }
        else {
            path = searchPart(spec, startIdx, "?");

            startIdx += path.length() + 1;
            query = searchPart(spec, startIdx, "");
        }
    }

    public Url(String protocol, String host, int port, String file, String query) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = file;
        this.query = query;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String file) {
        this.path = file;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void addQueryParameter(String key, String value) {
        if (query.length() > 0) {
            query += '&';
        }
        query += key + '=' + encode(value);
    }

    public String getQueryParameter(String key) {

        int startIdx = 0;
        while (startIdx < query.length()) {
            String k = searchPart(query, startIdx, "=");
            startIdx += k.length() + 1;
            String v = searchPart(query, startIdx, "&");
            startIdx += v.length() + 1;

            if (key.equals(k)) {
                return decode(v);
            }
        }

        return "";
    }

    public String getPathSegment(int idx) {
        String res = "";
        int startIdx = 0;
        for (int i = 0; i <= idx; i++) {
            res = searchPart(path, startIdx, "/");
            startIdx += res.length() + 1;
        }

        return decode(res);
    }

    public void addPathSegment(String newSeg) {

        if (path.length() > 0) {
            path += '/';
        }
        path += encode(newSeg);
    }

    // Override
    public String toString() {
        return protocol + "://" + host +
        (port > 0 ? ":" + port : "") +
        (path.length() > 0 ? "/" + path : "") +
        (query.length() > 0 ? "?" + query : "");
    }

    // --- Internal helper methods ---

    private String searchPart(String spec, int startIdx, String delim) {

        if (startIdx >= spec.length()) {
            return "";
        }

        for (int i = 0; i < delim.length(); i++) {
            int idx = spec.indexOf(delim.charAt(i), startIdx);
            if (idx >= 0) {
                return spec.substring(startIdx, idx);
            }
        }

        return spec.substring(startIdx);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static String toQueryString(Hashtable map) {
    
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(b);
            writeQueryString(map,w);
            w.flush();
            return b.toString(); // this should be safe, as URL Encoded String will not have any UTF
            
/*          // this does the same but creates a new class "java.io.StringWriter" that we can use
            final StringBuffer buff = new StringBuffer();
            Writer writer = new Writer() { // in JavaSE we have a class
                public void write(char[] cbuf, int off, int len) {
                    buff.append(cbuf, off, len);
                }
                public void flush() { }
                public void close() { }
            };
            getPostString(map,writer);
            return buff.toString();
*/
            
        }
        catch (IOException ex) {
            //#debug debug
            Logger.warn(ex);
            throw new RuntimeException( ex.toString() );
        }
    }
    
    

    public static void writeQueryString(Hashtable params,Writer getpostb) throws IOException {

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
            Logger.warn(ex);
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

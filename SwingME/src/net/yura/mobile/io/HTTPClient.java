package net.yura.mobile.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.QueueProcessorThread;
import net.yura.mobile.util.Url;

/**
 * @author Yura Mamyrin
 */
public abstract class HTTPClient extends QueueProcessorThread {

    public HTTPClient() {
        this(2);
    }

    public HTTPClient(int num) {
        super("HTTPClient",num);
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
        public boolean aborted = false;
        private Connection activeConnection = null;

        //#mdebug debug
        public String toString() {
            return url+" "+headers+" "+params+" "+post+" "+id+" "+redirects+" "+postData;
        }
        //#enddebug

        public void abort() {
        	aborted = true;
        	//#debug debug
			Logger.debug("Aborting request " + url);
        	FileUtil.close(activeConnection);
        }
    }

    protected abstract void onError(Request request, int responseCode, Hashtable headers, Exception ex);

    protected abstract void onResult(Request request, int responseCode, Hashtable headers, InputStream is, long length) throws Exception;


    public void process(Object arg0) {
        Request request = (Request)arg0;

        if (request.aborted) {
        	return;
        }

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

                String getpostString = Url.toQueryString(request.params);

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
            request.activeConnection = httpConn;
            if (request.aborted) {
            	FileUtil.close(httpConn);
            	return;
            }

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
                        Url.writeQueryString(request.params,w);
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
            //Logger.info(ex); // we pass this exception onto other parts of the app, and they can print if if they need to
        	if (!request.aborted) {
        		onError(request, respCode, headers, ex);
        	}
        }
        finally {
        	request.activeConnection = null;
            FileUtil.close(httpConn);
            FileUtil.close(is);
        }
    }



}

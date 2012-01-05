package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.yura.mobile.io.HTTPClient.Request;

/**
 * @author yura mamyrin
 */
public abstract class MultiThreadedHttpClient {
    
    Vector clients = new Vector();
    
    public MultiThreadedHttpClient(int a) {
        
        for (int c=0;c<a;c++) {
            
            HTTPClient httpclient = new HTTPClient() {

                protected void onError(Request request, int responseCode, Hashtable headers, Exception ex) {
                    MultiThreadedHttpClient.this.onError(request, responseCode, headers, ex);
                }

                protected void onResult(Request request, int responseCode, Hashtable headers, InputStream is, long length) throws IOException {
                    MultiThreadedHttpClient.this.onResult(request, responseCode, headers, is, length);
                }
            };
            httpclient.start();
            
            clients.addElement( httpclient );
        }
        
    }
    
    public void makeRequest(Request request) {
        
        int size = Integer.MAX_VALUE;
        HTTPClient choosen=null;
        
        for (int c=0;c<clients.size();c++) {
            HTTPClient client = (HTTPClient)clients.elementAt(c);
            int s = client.getInbox().size();
            if (s < size) {
                s = size;
                choosen = client;
            }
        }
        choosen.makeRequest(request);
    }
    
    public void kill() {
        for (int c=0;c<clients.size();c++) {
            HTTPClient client = (HTTPClient)clients.elementAt(c);
            client.kill();
        }
    }
    
    protected abstract void onError(Request request, int responseCode, Hashtable headers, Exception ex);

    protected abstract void onResult(Request request, int responseCode, Hashtable headers, InputStream is, long length) throws IOException;

    
}

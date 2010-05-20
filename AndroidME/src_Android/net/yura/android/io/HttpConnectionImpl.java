package net.yura.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.microedition.io.HttpConnection;

public class HttpConnectionImpl implements HttpConnection {

    private static final int STATE_SETUP = 0;
    private static final int STATE_CONNECTED = 1;

    private int state = STATE_SETUP;

    private String urlString;
    private String requestMethod = GET;

    private URL url;
    private HttpURLConnection connection;
    private InputStream input;
    private OutputStream output;

    public HttpConnectionImpl(String url) {
        if (url == null) {
            throw new NullPointerException();
        }
        checkIsValidUrl(url);
        this.urlString = url;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid URL");
        }
    }

    public HttpConnectionImpl(String url, int mode) {
        this(url);
    }

    protected void checkIsValidUrl(String checkUrl) {
        if (checkUrl.indexOf("http:") != 0
                || (checkUrl.length() <= "http:".length())) {
            throw new IllegalArgumentException("invalid URL " + checkUrl);
        }
    }

    public void close() throws IOException {
        if (input != null) {
            input.close();
        }
        if (output != null) {
            output.close();
        }
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openOutputStream());
    }

    public OutputStream openOutputStream() throws IOException {
        if (output != null) {
            throw new IOException("already opened");
        }
        connect();
        connection.setDoOutput(true);
        output = this.connection.getOutputStream();
        return output;
    }

    // only in setup state
    public void setRequestMethod(String requestMethod) throws IOException {
        if (state == STATE_SETUP) {
            if (requestMethod.equals(GET)) {
                this.requestMethod = requestMethod;
            } else if (requestMethod.equals(POST)) {
                this.requestMethod = requestMethod;
            } else {
                throw new IllegalArgumentException("illegal request method "
                        + requestMethod);
            }
        } else {
            throw new IOException("already connected");
        }
    }

    public void setRequestProperty(String key, String value) throws IOException {
        connect();
        connection.setRequestProperty(key, value);
    }

    // invoke at any time
    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestProperty(String key) {
        try {
            connect();
        } catch (IOException e) {
            return "";
        }
        return connection.getRequestProperty(key);
    }

    public String getURL() {
        return urlString;
    }

    public String getQuery() {
        return url.getQuery();
    }

    public int getPort() {
        return url.getPort();
    }

    public String getHost() {
        return url.getHost();
    }

    public String getProtocol() {
        return url.getProtocol();
    }

    public String getFile() {
        return url.getFile();
    }

    public String getRef() {
        return url.getRef();
    }

    // these calls force transition to connected state
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    public InputStream openInputStream() throws IOException {
        connect();
        input = connection.getInputStream();
        return input;
    }

    public long getLength() {
        try {
            connect();
        } catch (IOException ex) {
            return 0;
        }
        return connection.getContentLength();
    }

    public String getType() {
        try {
            connect();
        } catch (IOException ex) {
            return "";
        }
        return connection.getContentType();
    }

    public String getEncoding() {
        try {
            connect();
        } catch (IOException ex) {
            return "";
        }
        return connection.getContentEncoding();
    }

    public String getHeaderField(String name) throws IOException {
        connect();
        return connection.getHeaderField(name);
    }

    public String getHeaderField(int n) throws IOException {
        connect();
        return connection.getHeaderField(n);
    }

    public int getResponseCode() throws IOException {
        connect();
        return connection.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        connect();
        return connection.getResponseMessage();
    }

    public int getHeaderFieldInt(String name, int def) throws IOException {
        connect();
        return connection.getHeaderFieldInt(name, def);
    }

    public long getHeaderFieldDate(String name, long def) throws IOException {
        connect();
        return connection.getHeaderFieldDate(name, def);
    }

    public String getHeaderFieldKey(int n) throws IOException {
        connect();
        return connection.getHeaderFieldKey(n);
    }

    public long getDate() throws IOException {
        connect();
        return connection.getDate();
    }

    public long getExpiration() throws IOException {
        connect();
        return connection.getExpiration();
    }

    public long getLastModified() throws IOException {
        connect();
        return connection.getLastModified();
    }

    protected synchronized void connect() throws IOException {
        if (state == STATE_CONNECTED) {
            if (connection == null) {
                throw new IOException(
                        "Invalid State. No connection in state STATE_CONNECTED");
            }
            return;
        } else {
            state = STATE_CONNECTED;
        }
        connection = (HttpURLConnection) this.url.openConnection();
        connection.setRequestMethod(this.requestMethod);
    }
}

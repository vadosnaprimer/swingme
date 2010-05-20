package net.yura.android.io;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;

public class HttpsConnectionImpl extends HttpConnectionImpl implements
        HttpsConnection {

    public HttpsConnectionImpl(String url) {
        this(url, Connector.READ_WRITE);
    }

    public HttpsConnectionImpl(String url, int mode) {
        super(url, mode);
    }

    protected void checkIsValidUrl(String checkUrl) {
        if (checkUrl.indexOf("https:") != 0
                || (checkUrl.length() <= "https:".length())) {
            throw new IllegalArgumentException("invalid URL " + checkUrl);
        }
    }

    public SecurityInfo getSecurityInfo() throws IOException {
        connect();
        // TODO implement security info
        return null;
    }

    public int getPort() {
        return super.getPort();
    }
}

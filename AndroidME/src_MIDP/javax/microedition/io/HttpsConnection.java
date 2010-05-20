package javax.microedition.io;

import java.io.*;

public interface HttpsConnection extends HttpConnection {

    public SecurityInfo getSecurityInfo() throws IOException;

    public int getPort();
}

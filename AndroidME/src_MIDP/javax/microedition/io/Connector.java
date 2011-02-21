package javax.microedition.io;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.yura.android.io.AndroidAssetConnection;
import net.yura.android.io.AndroidURLConnection;
import net.yura.android.io.AndroidFileConnection;
import net.yura.android.io.HttpConnectionImpl;
import net.yura.android.io.HttpsConnectionImpl;
import net.yura.android.io.ServerSocketConnection;
import net.yura.android.io.SocketConnection;
import net.yura.android.messaging.MessageConnectionImpl;

public class Connector {
    public static final int READ = 0x01;
    public static final int WRITE = 0x02;
    public static final int READ_WRITE = READ | WRITE;

    private static final String PROTOCOL_ASSET = "file:///android_asset/";
    private static final String PROTOCOL_FILE = "file:";
    private static final String PROTOCOL_SOCKET = "socket:";
    private static final String PROTOCOL_HTTP = "http:";
    private static final String PROTOCOL_HTTPS = "https:";
    private static final String PROTOCOL_SMS = "sms:";

    public static final Connection open(String name) throws IOException {
        return open(name, READ_WRITE);
    }

    public static final Connection open(String name, int mode)
            throws IOException {
        Connection connection;
        if (name.startsWith(PROTOCOL_ASSET)) {
            connection = new AndroidAssetConnection(name.substring( PROTOCOL_ASSET.length() ));
        }
        else if (name.startsWith(PROTOCOL_FILE)) {
            connection = new AndroidFileConnection(name);
        }
        else if (name.startsWith(PROTOCOL_SOCKET)) {
            connection = getSocketConnection(name);
        }
        else if (name.startsWith(PROTOCOL_SMS)) {
            connection =  new MessageConnectionImpl(name);
        }
        else if (name.startsWith(PROTOCOL_HTTP)) {
            connection =  new HttpConnectionImpl(name, mode);
        }
        else if (name.startsWith(PROTOCOL_HTTPS)) {
            connection =  new HttpsConnectionImpl(name, mode);
        }
        else {
            connection = new AndroidURLConnection(name);
        }
        return connection;
    }

    public static final DataInputStream openDataInputStream(String name)
            throws IOException {
        return new DataInputStream(openInputStream(name));
    }

    public static final DataOutputStream openDataOutputStream(String name)
            throws IOException {
        return new DataOutputStream(openOutputStream(name));
    }

    public static final InputStream openInputStream(String name)
            throws IOException {
        Connection connection = open(name, READ);
        return ((InputConnection) connection).openInputStream();
    }

    public static final OutputStream openOutputStream(String name)
            throws IOException {
        Connection connection = open(name, WRITE);
        return ((OutputConnection) connection).openOutputStream();
    }


    private static Connection getSocketConnection(String name) throws IOException {
        int portSepIndex = name.lastIndexOf(':');
        int port = Integer.parseInt(name.substring(portSepIndex + 1));
        String host = name.substring("socket://".length(), portSepIndex);

        if (host.length() > 0) {
            return new SocketConnection(host, port);
        } else {
            return new ServerSocketConnection(port);
        }
    }
}

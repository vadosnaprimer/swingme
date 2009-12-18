package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public abstract class SocketClient implements Runnable {
    public final static int DISCONNECTED = 1;
    public final static int CONNECTED = 2;
    public final static int CONNECTING = 3;
    public final static int COMMUNICATING = 4;

    private QueueProcessorThread writeThread;
    private Thread readThread;

    private Vector offlineBox = new Vector();

    protected StreamConnection conn;
    protected OutputStream out;
    protected InputStream in;

    private final String server;
    private int wait;

    public SocketClient(String server) {
        this.server = server;
    }
    protected StreamConnection openConnection() throws IOException {
        return (StreamConnection)Connector.open("socket://" + server);
    }

    public void addToOutbox(Object obj) {

        if (writeThread==null) {
            writeThread = new QueueProcessorThread("[SocketClient] writeThread") {
                public void process(Object object) {

                    if (conn==null) {

                        // if we had a disconnect in the read and have shutdown the socket
                        // but are still getting objects comming in, and we have not yet
                        // told the app we have lost the connection
                        if (out!=null || in!=null) {
                            addToOfflineBox(object);
                            return;
                        }

                        wait = 1000;
                        // MAKE THE CONNECTION!!
                        while (out==null || in==null) {

                            if(!isRunning()) return;

                            updateState(CONNECTING);
                            //#debug
                            System.out.println("[SocketClient] Trying to connect to: "+server);
                            try {
                                conn = openConnection();
                                out = conn.openOutputStream();
                                in = conn.openInputStream();
                            }
                            catch (SecurityException s) {
                                updateState(DISCONNECTED);
                                //#debug
                                s.printStackTrace();

                                securityException();
                                return;
                            }
                            catch (Exception x) {
                                updateState(DISCONNECTED);
                                //#debug
                                x.printStackTrace();

                                try {
                                    Thread.sleep(wait);
                                }
                                catch (InterruptedException ex) {
                                    //#debug
                                    ex.printStackTrace();
                                }

                                wait = wait * 2;
                                if (wait > 300000) {
                                    wait = 300000;
                                }

                            }
                        }

                        // need to make a new 1, so any bits of xml still left in the old 1 are cleared
                        connected(in,out);

                        // START THE READ THREAD
                        readThread = new Thread(SocketClient.this,"[SocketClient] readThread");
                        readThread.start();

                    }

                    try {
//#debug
System.out.println("[SocketClient] sending object: "+object);
                        updateState(COMMUNICATING);

                        write(out, object);

                        out.flush();

                        updateState(CONNECTED);

                    }
                    catch(Exception ex) {
                        // THIS WILL ONLY HAPPEN IF YOU GET A DISCONNECT DURING A SEND
                        //#mdebug
                        System.out.println("[SocketClient] Exception during a write to socket");
                        ex.printStackTrace();
                        //#enddebug

                        // move this and any queued objects to offline inbox
                        addToOfflineBox( object );

                        NativeUtil.close(conn);
                        conn = null;
                    }
                }
            };
            writeThread.start();
        }

        writeThread.addToInbox( obj );
    }

    public void addToOfflineBox(Object t) {
        if (offlineBox.contains(t)) {
            offlineBox.addElement(t);
        }
    }

    public Vector getOfflineBox() {
        return offlineBox;
    }

    public final void run() {
//#mdebug
String name = readThread.getName();
System.out.println("[SocketClient] STARTING "+name);
//#enddebug
        Thread.currentThread().setPriority( Thread.MIN_PRIORITY );
        Object task;
        while (true) {
            try {
                task = read(in);
            }
            catch(Exception ex) {
                // THIS HAPPENS ON A DISCONNECT

                updateState(DISCONNECTED);

                //#mdebug
                if (writeThread!=null) {
                    System.out.println("[SocketClient] Exception during read from socket");
                    ex.printStackTrace();
                }
                //#enddebug

                NativeUtil.close(conn);
                conn=null;
                break;
            }

            updateState(COMMUNICATING);

//#debug
System.out.println("[SocketClient] got object: "+task);
            try {

                Thread.yield();
                Thread.sleep(0);

                handleObject( task );

                Thread.yield();
                Thread.sleep(0);

            }
            catch (Exception x) {
                //#mdebug
                DesktopPane.log("[SocketClient] CAN NOT HANDLE! Task: "+task+" "+x.toString() );
                x.printStackTrace();
                //#enddebug
            }
//#debug
System.out.println("[SocketClient] finished handling object, waiting for new object from server");
            updateState(CONNECTED);
        }

        Object a1=in;
        Object a2=out;

        in = null;
        out = null;

        // we have not had a disconnect requested, so we have to get ready to reconnect
        if (writeThread!=null) {
            // move anything in the inbox to the offline inbox
            Vector inbox = writeThread.getInbox();
            for (int c=0;c<inbox.size();c++) {
                addToOfflineBox(inbox.elementAt(c));
            }
            writeThread.clearInbox();
            disconnected();
        }

        NativeUtil.close(a1);
        NativeUtil.close(a2); // close on input can block???

//#debug
System.out.println("[SocketClient] ENDING "+name);
    }

    public void disconnect() {
        writeThread.kill();
        writeThread = null;
        NativeUtil.close(conn);
    }
  
    protected void sendOfflineInboxMessages() {
        //#debug
        System.out.println("[SocketClient] sending offline messages: "+offlineBox);
        while (!offlineBox.isEmpty()) {
                Object task = offlineBox.elementAt(0);
                offlineBox.removeElementAt(0);
                addToOutbox( task );
        }
    }

    protected void securityException() {
        //#debug
        System.out.println("[SocketClient] Socket connections are not allowed.");
    }

    protected abstract void handleObject(Object task);
    protected abstract void updateState(int c);

    protected abstract void write(OutputStream out, Object object) throws IOException;
    protected abstract Object read(InputStream in) throws IOException;

    protected abstract void connected(InputStream in,OutputStream out);

    // this should send a new message if you want to make a connection again
    // the message you send will be something like 'hello' or 'login'
    protected abstract void disconnected();

}

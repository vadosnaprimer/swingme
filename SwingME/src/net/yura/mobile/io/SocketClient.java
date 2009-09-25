package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
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

    private Vector offlineBox = new Vector();

    private boolean running;
    private OutputStream out;
    private SocketConnection sc=null;
    private InputStream in=null;

    private String server;

    public SocketClient(String server) {
        this.server = server;
    }

    public void addToOutbox(Object obj) {

        if (writeThread==null) {
            writeThread = new QueueProcessorThread("writeThread") {
                public void process(Object object) {

                    if (out==null) {

                        // MAKE THE CONNECTION!!
                        while (out==null || in==null) {

                            updateState(CONNECTING);

                            try {
                                sc = (SocketConnection) Connector.open("socket://"+server);
                                out = sc.openOutputStream();
                                in = sc.openInputStream();
                            }
                            catch (IOException x) {
                                updateState(DISCONNECTED);
                                x.printStackTrace();
                                try {
                                    Thread.sleep(5000);
                                }
                                catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        // need to make a new 1, so any bits of xml still left in the old 1 are cleared
                        connected(in,out);

                        // START THE READ THREAD
                        Thread readThread = new Thread(SocketClient.this);
                        readThread.start();

                    }

                    //Task task = (Task)object;
                    try {
//#debug
System.out.println("sending object: "+object);
                        updateState(COMMUNICATING);

                        write(out, object);

                        out.flush();

                        updateState(CONNECTED);

                    }
                    catch(IOException ex) {
                        // THIS WILL ONLY HAPPEN IF YOU GET A DISCONNECT DURING A SEND
                        ex.printStackTrace();
                        addToOfflineBox( object );
                    }

                }
            };
            writeThread.start();
        }

        writeThread.addToInbox( obj );
    }

    public void addToOfflineBox(Object t) {
        for (int c=0;c<offlineBox.size();c++) {
            Object t2 = offlineBox.elementAt(c);
            // if we already have this task in the list of tasks to send
            if (t2.equals(t)) {
                return;
            }
        }
        offlineBox.addElement(t);
    }

    public final void run() {

        running = true;

        Object task;

        while (running) {
            try {
                task = read(in);
            }
            catch(Exception ex) {
                // THIS HAPPENS ON A DISCONNECT

                updateState(DISCONNECTED);

                if (running) {
                    ex.printStackTrace();
                }
                break;
            }

            updateState(COMMUNICATING);
//#debug
System.out.println("got object: "+task);
            try {
                handleObject( task );
            }
            catch (Exception x) {
                DesktopPane.log("CAN NOT HANDLE! "+ task );
                x.printStackTrace();
            }

            updateState(CONNECTED);
        }



        NativeUtil.close(out);
        NativeUtil.close(in);
        NativeUtil.close(sc);
        out = null;
        in= null;
        sc=null;

        if(running) {
            disconnected();
        }
    }

    public void exit() throws Exception {

        // TODO make sure everything is saved!!!!!

        running = false;
        writeThread.kill();
    }

    protected void sendOfflineInboxMessages() {

        while (!offlineBox.isEmpty()) {
                Object task = offlineBox.elementAt(0);
                offlineBox.removeElementAt(0);
                addToOutbox( task );
        }
    }

    protected abstract void handleObject(Object task);
    protected abstract void updateState(int c);

    protected abstract void write(OutputStream out, Object object) throws IOException;
    protected abstract Object read(InputStream in) throws Exception;

    protected abstract void connected(InputStream in,OutputStream out);
    protected abstract void disconnected();

}

package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public abstract class SocketClient implements Runnable {
    public final static int DISCONNECTED = 1;
    public final static int CONNECTED = 2;
    public final static int CONNECTING = 3;
    public final static int COMMUNICATING = 4;
    public final static int DISCONNECTED_AND_PAUSED = 5;

    protected QueueProcessorThread writeThread;
    private Thread readThread;

    /**
     * not sure how useful this is, as sometimes write does NOT throw any exception
     * when trying to send something, even though it was not able to send, and the
     * connection is shut down right after, that message would have been lost, and
     * will not end up in the offline inbox
     */
    protected Vector offlineBox = new Vector();

    protected StreamConnection conn;
    protected OutputStream out;
    protected InputStream in;

    //#mdebug debug
    private boolean disconnected = false;
    public void setDisconnected(boolean b){
        this.disconnected = b;
    }
    //#enddebug

    // configurable parameters
    protected int maxRetries = 3;
    protected int retryWaitMultiplier = 2;
    protected int initialWaitValue = 1000;
    protected int maxWaitTimeMillis = 30000;
    boolean pauseReconnectOnFailure;

    private int retryCount;
    protected String protocol = "socket://";
    private final String server;

    /**
     * you can pass null into this method, but then you need to Override {@link #getNextServer()}
     * @param server
     */
    public SocketClient(String server) {
        this.server = server;
    }

    public SocketClient(String server, int maxRetries, int retryWaitMultiplier, int initialWaitValue, int maxWaitTimeMillis, boolean pauseReconnectOnFailure){
        this.server = server;
        this.maxRetries = maxRetries;
        this.retryWaitMultiplier = retryWaitMultiplier;
        this.initialWaitValue = initialWaitValue;
        this.maxWaitTimeMillis = maxWaitTimeMillis;
        this.pauseReconnectOnFailure = pauseReconnectOnFailure;
    }

    public void setRetryCount(int retryCount){
        this.retryCount = retryCount;
    }
    protected int getRetryCount(){
        return retryCount;
    }
    protected int getMaxRetries(){
        return maxRetries;
    }

    public static String connectAppend;

    protected StreamConnection openConnection(String serv) throws IOException {

        // TODO NOT DONE this needs to be turned ON for HTTP and turned OFF when used for BT
        if (connectAppend!=null &&
                serv.indexOf(";") < 0) {
            // TODO: Hack for black berry direct tcp/ip connection
            // TODO: http://www.localytics.com/blog/post/how-to-reliably-establish-a-network-connection-on-any-blackberry-device/
            //serv += ";deviceside=true";
        	serv += connectAppend;
        }

        return (StreamConnection)Connector.open(protocol + serv);
    }

    public void addToOutbox(Object obj) {

        if (writeThread==null) {
            writeThread = new QueueProcessorThread("SocketClient-WriteThread") {
                public void process(Object object) {

                    if (conn==null) {

                        int wait = initialWaitValue;
                        // MAKE THE CONNECTION!!
                        while (out==null || in==null) {

                            if(!isRunning()) return;

                            String serv = null;
                            try {
                                serv = getNextServer();
                                retryCount++;

                                //#debug info
                                Logger.info("[SocketClient] Trying to connect to: "+serv+" to send: "+object);

                                //#debug debug
                                if (disconnected) throw new IOException();

                                conn = openConnection(serv);
                                out = conn.openOutputStream();
                                in = conn.openInputStream();


                                // success therefore reset connecting state
                                updateState(CONNECTING);

                                // reset the retryCount and initial wait time
                                retryCount=0;
                                wait = initialWaitValue;
                            }
                            catch (SecurityException s) {

                                close(conn,in,out);

                                updateState(DISCONNECTED);
                                //#debug info
                                Logger.info("cant connect " + serv, s);

                                securityException();
                                return;
                            }
                            catch (Exception x) {

                                close(conn,in,out);

                                //#mdebug info
                                if (x instanceof IOException) {
                                    Logger.info(x.toString()); // we do not want to print the full stack trace as this is not a serious error
                                }
                                else {
                                    Logger.info("cant connect " + serv, x);
                                }
                                //#enddebug

                                if (pauseReconnectOnFailure && retryCount > maxRetries) {
                                    updateState(DISCONNECTED_AND_PAUSED);
                                    // number of reconnects exhausted so wait thread
                                    synchronized(this) {
                                        try {
                                            wait();
                                        }
                                        catch (Exception e){
                                            //#debug info
                                            Logger.info(null, e);
                                        }
                                    }

                                    // update listener with current state
                                    updateState(DISCONNECTED);

                                    // reset the retry count
                                    retryCount = 0;
                                }
                                else {
                                    updateState(DISCONNECTED);

                                    try {
                                        // pause a while before next reconnect attempt
                                        Thread.sleep(wait);

                                        // increment the wait time between subsequent reconnect attempts
                                        wait = wait * retryWaitMultiplier;
                                        if (wait > maxWaitTimeMillis) {
                                            wait = maxWaitTimeMillis;
                                        }
                                    }
                                    catch (InterruptedException ex) {
                                        //#debug info
                                        Logger.info(null, ex);
                                    }
                                }
                            }
                        }

                        // need to make a new 1, so any bits of xml still left in the old 1 are cleared
                        connected(in,out);

                        // START THE READ THREAD
                        readThread = new Thread(SocketClient.this,"SocketClient-ReadThread");
                        readThread.start();

                    }

                    try {
                        //#debug info
                        Logger.info("[SocketClient] sending object: "+object);
                        updateState(COMMUNICATING);

                        //#mdebug debug
                        if (disconnected) throw new IOException();
                        // this is NOT good enough as a test, as right at this point the IO Exception can happen in the READ thead and not here!
                        // maybe have something like
                        // if (readExceptionDuringWrite) NativeUtil.close(in);
                        // if (writeException) NativeUtil.close(out);
                        // there are THREE things that can go wrong,
                        // 1. exception in read
                        // 2. exception in write
                        // 3. exception in read during a write that then triggers a exception in the write
                        //#enddebug

                        write(out, object);

                        out.flush();

                        updateState(CONNECTED);

                    }
                    catch(Exception ex) {
                        // THIS WILL ONLY HAPPEN IF YOU GET A DISCONNECT DURING A SEND
                        //#debug info
                        Logger.info("[SocketClient] Exception during a write to socket", ex);

                        // move this and any queued objects to offline inbox
                        addToOfflineBox(object, true);
                        // TODO note that other objects may have already been moved into the offlineInbox
                        // by this time by the shutdownConnection() being called from the read thread

                        shutdownConnection();

                    }
                }
            };

            // We are going to create a thread/start it, etc and this will
            // take time... We need to signal that we are "connecting" strait
            // away, otherwise if someone ask isItDisconnected() we would reply
            // true, and further connections would be tried.
            updateState(CONNECTING);

            writeThread.start();
        }

        writeThread.addToInbox( obj );

    }

    /**
     * if we are in the DISCONNECTED_AND_PAUSED state, wake us up
     */
    public void addToOfflineBox(Object t, boolean isFront) {
        if (!offlineBox.contains(t)) {
            if (isFront) {
                offlineBox.insertElementAt(t, 0);
            }
            else {
                offlineBox.addElement(t);
            }
        }

        wake();
    }

    /**
     * if we are in the DISCONNECTED_AND_PAUSED state, wake us up
     */
    public void wake() {
        QueueProcessorThread obj = writeThread;
        if (obj!=null) {
            synchronized(obj){
                obj.notify();
            }
        }
    }

    public Vector getOfflineBox() {
        return offlineBox;
    }

    protected void sendOfflineInboxMessages() {
        //#debug info
        Logger.info("[SocketClient] sending offline messages: "+offlineBox);
        while (!offlineBox.isEmpty()) {
            Object task = offlineBox.elementAt(0);
            offlineBox.removeElementAt(0);
            addToOutbox( task );
        }
    }

    public void disconnect() {
        QueueProcessorThread old = writeThread;
        writeThread = null;
            shutdownConnection();
        if(old != null) {
            old.kill();
        }
    }

    private synchronized void shutdownConnection() {

        // we have already shut down the connection
        // and if we do it again, we risk moving the "hello" message
        // out of the current queue of things to be sent
        if (conn==null && in==null && out==null) return;

        updateState(DISCONNECTED);

        close(conn,in,out);
        in = null;
        out = null;
        conn = null;

        // move anything in the inbox to the offline inbox
        if (writeThread!=null){
            Vector inbox = writeThread.getInbox();
            for (int c=0;c<inbox.size();c++) {
                addToOfflineBox(inbox.elementAt(c), false);
            }
            writeThread.clearInbox();

            // we have not had a disconnect requested, so we have to get ready to reconnect
            disconnected();
        }
    }

    private void close(final StreamConnection connection,final InputStream inputStream,final OutputStream outputStream) {
        // we HAVE to close these here, as if we do not close them, and ONLY
        // close the Connection, the readThread stays in the blocked state

        // we HAVE to have another thread here, as we may be closing this from ANY thread and it can get stuck
        new Thread() {
            public void run() {
                FileUtil.close(inputStream); // any close on input can block??? on SE JP7 ????
                FileUtil.close(outputStream);
                FileUtil.close(connection);
            }
        }.start();
    }

    /**
     * This is the run() method of the Socket read thread
     * this thread can sometimes block and stick around even after a socket is closed and a new one reopened
     */
    public final void run() {
        try {
            //#mdebug info
            String name = readThread.getName();
            int id = System.identityHashCode(readThread);
            Logger.info("[SocketClient] STARTING "+ name +" "+id);
            //#enddebug

            // Changing thread priority should only be done in platforms
            // were actually improve performance.
            if (net.yura.mobile.util.QueueProcessorThread.CHANGE_PRIORITY) {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            }

            InputStream myin=in;

            try {

                while (true) {

                    //#debug debug
                    if (disconnected) throw new IOException();

                    Object task = read(myin);

                    updateState(COMMUNICATING);

                    //#debug info
                    Logger.info("[SocketClient] got object: "+task+" "+id);
                    try {

                        Thread.yield();
                        Thread.sleep(0);

                        handleObject( task );

                        Thread.yield();
                        Thread.sleep(0);

                    }
                    catch (Exception x) {
                        //#debug warn
                        Logger.warn("[SocketClient] CAN NOT HANDLE! Task: "+task+" "+x.toString(), x);
                    }

                    updateState(CONNECTED);
                }

            }
            catch(Exception ex) {
                // THIS HAPPENS ON A DISCONNECT

                //#debug info
                Logger.info("[SocketClient] Disconnect (Exception) during read from socket "+ex.toString()+" "+id);

                // this is a normal shutdown when a exception is thrown in the read thread and 'in' is the same as the one we used
                boolean normal = myin == in;

                //#mdebug info
                if (!(ex instanceof IOException) || (!normal && writeThread!=null)) {
                    // this is NOT a normal disconnect
                    Logger.warn("[SocketClient] strange disconnect in=" + in + " myin=" + myin, ex);
                }
                //#enddebug

                // only call shut down if the current connection is the same as my connection
                if (normal) {
                    shutdownConnection();
                }

            }

            //#debug info
            Logger.info("[SocketClient] ENDING "+ name +" "+id);
        }
        catch (Throwable t){
            //#debug error
            Logger.error(null, t);
        }
    }

    // --------------------------- abstract methods ---------------------------

    protected String getNextServer() {
        return server;
    }

    protected void securityException() {
        //#debug warn
        Logger.warn("[SocketClient] Socket connections are not allowed.");
    }

    /**
     * this is called when a object is recieved from the server
     */
    protected abstract void handleObject(Object task);

    /**
     * this is used to update a connection indicator
     */
    protected abstract void updateState(int c);

    protected abstract void write(OutputStream out, Object object) throws IOException;
    protected abstract Object read(InputStream in) throws IOException;

    /**
     * this method is called when a connection is established
     */
    protected abstract void connected(InputStream in,OutputStream out);

    /**
     * This method is called when a connection is lost
     *
     * this should send a new message if you want to make a connection again
     * the message you send will be something like 'hello' or 'login'
     */
    protected abstract void disconnected();

}
package javax.microedition.media.player;

import java.util.Vector;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import net.yura.android.AndroidMeActivity;
import net.yura.android.AndroidMeApp;

public abstract class BasicPlayer implements Player {

    // Player's state
    int state = UNREALIZED;

    // Player Listener List
    Vector<PlayerListener> listenerList = new Vector<PlayerListener>();

    // Player worker methods
    abstract protected void doRealize() throws MediaException;
    abstract protected void doPrefetch() throws MediaException;
    abstract protected void doStart() throws MediaException;
    abstract protected void doStop();
    abstract protected void doDeallocate();
    abstract protected void doClose();

    abstract protected long doSetMediaTime(long now) throws MediaException;
    abstract protected long doGetMediaTime();
    abstract protected long doGetDuration();
    abstract protected Control[] doGetControls();
    abstract protected Control doGetControl(String type);



    /**
     * Check to see if the Player is closed.  If the
     * unrealized boolean flag is true, check also to
     * see if the Player is UNREALIZED.
     *
     * @param unrealized the flag whether to check the unrealized state.
     */
    protected void chkClosed(boolean unrealized) {
        if (state == CLOSED || (unrealized && state == UNREALIZED)) {
            throw new IllegalStateException("The Player is " +
                (state == CLOSED ? "closed" : "unrealized"));
        }
    }

    public void setLoopCount(int count) throws IllegalArgumentException, IllegalStateException {
        chkClosed(false);
        if (state == STARTED) {
            throw new IllegalStateException("setLoopCount");
        }
        if (count == 0 || count < -1) {
            throw new IllegalArgumentException("setLoopCount");
        }

//JP        loopCountSet = count;
//        loopCount = count;
    }

    public synchronized void realize() throws MediaException {
        chkClosed(false);

        if (state < REALIZED) {
            sendPlayerEvent("doRealize", null);
            state = REALIZED;
        }
    }


    public synchronized void prefetch() throws MediaException {
        chkClosed(false);

        if (state < PREFETCHED) {

            realize();
            sendPlayerEvent("doPrefetch", null);

            state = PREFETCHED;
        }
    }


    public synchronized void start() throws MediaException {
        chkClosed(false);

        if (state < STARTED) {
            realize();
            prefetch();

        // If it's at the EOM, it will automatically
        // loop back to the beginning.
// JP        if (EOM)
//            setMediaTime(0);

            sendPlayerEvent("doStart", null);

            state = STARTED;
            sendListenerEvent(PlayerListener.STARTED, new Long(getMediaTime()));
        }
    }

    public synchronized void stop() {
        chkClosed(false);

//JP        loopAfterEOM = false;

        if (state >= STARTED) {
            try {
                sendPlayerEvent("doStop", null);
            }
            catch (Exception e) {
            }

            state = PREFETCHED;
            sendListenerEvent(PlayerListener.STOPPED, new Long(getMediaTime()));
        }
    }

    public synchronized void deallocate() {
        chkClosed(false);

//JP        loopAfterEOM = false;

        if (state >= PREFETCHED) {
            stop();
            try {
                sendPlayerEvent("doDeallocate", null);
            }
            catch (MediaException e) {
            }
            state = REALIZED;
        }
    }

    public synchronized void close() {
        if (state > CLOSED) {
            deallocate();
            try {
                sendPlayerEvent("doClose", null);
            }
            catch (MediaException e) {
            }

            state = CLOSED;

//JP            try {
//                if (stream != null)
//                stream.close();
//            } catch (IOException e) { }

            sendListenerEvent(PlayerListener.CLOSED, null);
        }
    }

    public synchronized long setMediaTime(long now) throws MediaException {
        chkClosed(true);

        if (now < 0) {
            now = 0;
        }

        long theDur = getDuration();
        if ((theDur != TIME_UNKNOWN) && (now > theDur)) {
            now = theDur;
        }

        long rtn = doSetMediaTime(now);
//JP        EOM = false;

        return rtn;
    }

    public long getMediaTime() {
        chkClosed(false);
        return doGetMediaTime();
    }

    public int getState() {
        // A race condition can occur between the return of this method and the
        // execution of a state changing method.
        return state;
    }

    public long getDuration() {
        chkClosed(false);
        return doGetDuration();
    }

    // Override
    public void addPlayerListener(PlayerListener playerListener) {
        chkClosed(false);
        if (playerListener != null && !listenerList.contains(playerListener)) {
            listenerList.add(playerListener);
        }
    }

    public void removePlayerListener(PlayerListener playerListener) {
        chkClosed(false);
        listenerList.remove(playerListener);
    }

    void sendListenerEvent(final String evt, final Object evtData) {

        //  There's always one listener for EOM -- itself.
        if (listenerList.size() > 0 || evt == PlayerListener.END_OF_MEDIA) {

            AndroidMeApp.getIntance().invokeLater(
                new Runnable() {

                    public void run() {
                        doListenerEvent(evt, evtData);
                    }
                });
        }
    }

    void doListenerEvent(final String evt, final Object evtData) {
//JP
//        if (evt == PlayerListener.END_OF_MEDIA) {
//
//
//            synchronized (BasicPlayer.this) {
//                EOM = true;
//                loopAfterEOM = false;
//
//                if (state > Player.PREFETCHED) {
//                    state = Player.PREFETCHED;
//
//                    if (loopCount > 1 || loopCount == -1) {
//                        loopAfterEOM = true;
//                    }
//                }
//            }
//        }

        synchronized (listenerList) {
            for (int i = 0; i < listenerList.size(); i++) {
                try {
                    PlayerListener l = listenerList.elementAt(i);
                    l.playerUpdate(BasicPlayer.this, evt, evtData);
                }
                catch (Exception e) {
                    System.err.println("Error in playerUpdate: " + e);
                }
            }
        }

//JP      if (loopAfterEOM) {
//            doLoop();
//        }

    }

    void doPlayerEvent(final String evt, final Object evtData) throws MediaException {
        if (evt == "doRealize") {
            doRealize();
        }
        else if (evt == "doPrefetch") {
            doPrefetch();
        }
        else if (evt == "doStart") {
            doStart();
        }
        else if (evt == "doStop") {
            doStop();
        }
        else if (evt == "doDeallocate") {
            doDeallocate();
        }
        else if (evt == "doClose") {
            doClose();
        }
    }

    Throwable playerException;
    void sendPlayerEvent(final String evt, final Object evtData) throws MediaException {
        playerException = null;

        AndroidMeApp.getIntance().invokeAndWait(new Runnable() {
            public void run() {
                try {
                    doPlayerEvent(evt, evtData);
                } catch (Throwable e) {
                    playerException = e;
                }
            }
        });

        if (playerException != null) {
            if (playerException instanceof MediaException) {
                throw (MediaException) playerException;
            }
            playerException.printStackTrace();
        }
    }


//JP    /**
//     * the worker method to deliver EOM event
//     */
//    synchronized void doLoop() {
//
//    // If a loop count is set, we'll loop back to the beginning.
//    if ((loopCount > 1) || (loopCount == -1)) {
//        try {
//        if (setMediaTime(0) == 0) {
//            if (loopCount > 1)
//            loopCount--;
//            start();
//        } else
//            loopCount = 1;
//        } catch (MediaException ex) {
//        loopCount = 1;
//        }
//    } else if (loopCountSet > 1)
//        loopCount = loopCountSet;
//
//    loopAfterEOM = false;
//    }

    public Control[] getControls() {
        chkClosed(true);
        return doGetControls();
    }

    public Control getControl(String type) {
        chkClosed(true);

        // Prepend the package name if the type given does not
        // have the package prefix.
        if (type.indexOf('.') < 0) {
            type = "javax.microedition.media.control." + type;
        }

        return doGetControl(type);
    }
}

package net.yura.mobile.logging;

import java.util.Vector;

/**
 * 
 * @author Orens
 * 
 */
public final class Logger {
//#mdebug error
    private static final int MAX_NUMBER_OF_ENTRIES = 100;
    private static boolean STORE_LOG = true;

    private final static int OFF = 99;
    private final static int TRACE = 0;
    private final static int DEBUG = 1;
    private final static int INFO = 2;
    private final static int WARNING = 3;
    private final static int ERROR = 4;

    private static String NEW_SESSION_STR = "New Session";
    private static final String[] LEVEL_NAMES = {"TRACE", "DEBUG", "INFO", "WARNING", "ERROR"};
    private static long _startTime = System.currentTimeMillis();
    private static Logger _logger;
    private Vector _messageContainer;
    private static int printLevel = TRACE;


    // If like to see the class name in log printing, switch to true:
    private final static boolean PRINT_CLASS_NAME = true;

    /**
     * Instantiates a new logger.
     */
    private Logger() {
        //TODO: load previous data if exists.
        _messageContainer = new Vector();
    }

    /**
     * Inits the.
     */
    public static void init() {
        _logger = new Logger();
        addMessageToContainer("*********** " + NEW_SESSION_STR + "***************");
    }

    public synchronized static void trace(String message) {
        log(message, TRACE);
    }

    public synchronized static void info(String message) {
        log(message, INFO);
    }

    public synchronized static void debug(String message) {
        log(message, DEBUG);
    }

    public synchronized static void warning(String message) {
        log(message, WARNING);
    }

    public synchronized static void error(String message) {
        log(message, ERROR);
    }

    /**
     * Clear log.
     */
    public void clearLog() {
        _logger._messageContainer = new Vector();
        addMessageToContainer("***** " + NEW_SESSION_STR + "**************");
        addMessageToContainer(formatMessage("***** Log Cleared", INFO));
        storeLog();
    }

    /**
     * Store log.
     */
    public static void storeLog() {
        // to implement
    }

    /**
     * Gets the log.
     *
     * @return the log
     */
    public static Vector getLog() {
        return _logger._messageContainer;
    }

    // ////////////////
    // Private methods
    // ///////////////

     /**
     * Log.
     *
     * @param message the message
     * @param level the level
     * @param e the e
     */
    private static void log(String message, int level) {
        if ( PRINT_CLASS_NAME ) {
            message = "[" + classNameWithoutPackage( Thread.currentThread().getClass().getName() ) + "]" + message;
        }
        String formattedMessage = formatMessage(message, level);
        // always print screen
        if (level >= printLevel) {
            System.out.println(formattedMessage);
        	// add to logger
        	addMessageToContainer(formattedMessage);
        }

        if (level == ERROR && STORE_LOG) {
            storeLog();
        }
    }
    /**
     * Adds the message to container.
     *
     * @param message the message
     */
    private static void addMessageToContainer(String message) {
        if (STORE_LOG) {
        if (_logger._messageContainer.size() >= MAX_NUMBER_OF_ENTRIES) {
            _logger._messageContainer.removeElementAt(0);
        }
        _logger._messageContainer.addElement(message);
    }
    }

    /**
     * Format message.
     *
     * @param message the message
     * @param level the level
     *
     * @return the string
     */
    private static String formatMessage(String message, int level) {
        StringBuffer formattedMessage = new StringBuffer();
        formattedMessage.append(System.currentTimeMillis() - _startTime);
        formattedMessage.append(" [");
        if (level < LEVEL_NAMES.length) {
            formattedMessage.append(LEVEL_NAMES[level]);
        }
        formattedMessage.append("]");
        formattedMessage.append(message);
        return formattedMessage.toString();
    }

    private static String classNameWithoutPackage( String longClassName ) {
        int i = 0;
        String str = longClassName.substring( i );
        while ( i != -1 ) {
            i = str.indexOf(".");
            str = str.substring( i + 1 );
        }
        return str;
    }
//#enddebug
}



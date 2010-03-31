package net.yura.mobile.logging;

/**
 *
 * @author Orens
 *
 */
public class Logger {
    public final static int DEBUG = 0; //debug
    public final static int INFO = 1; //info
    public final static int WARN = 2; //warn
    public final static int ERROR = 3; //error
    public final static int FATAL = 4; //fatal
    private static final String[] LEVEL_NAMES = {"debug", "info", "warn", "error"};
    private static Logger logger = new Logger();
    private static int level;

    protected  Logger() {
      //#debug fatal
      level = FATAL;
      //#debug error
      level = ERROR;
      //#debug warn
      level = WARN;
      //#debug info
      level = INFO;
      //#debug debug
      level = DEBUG;
    }

    public static void setLogger(Logger logger)
    {
      Logger.logger = logger;
    }

    public static void setLevel(int level)
    {
      Logger.level = level;
    }

    /**
     * Debug logging is the most verbose and can be used for any type of logging.
     * Developers will only need to see this info when debugging a problem and should work at the info level most of the time.
     */
    public static void debug(String message) {
      //#debug debug
      if(level<=DEBUG) logger.log(message, "DEBUG");
    }

    /**
     * Debug logging is the most verbose and can be used for any type of logging.
     * Developers will only need to see this info when debugging a problem and should work at the info level most of the time.
     */
    public static void debug(Throwable throwable) {
      //#debug debug
      if(level<=DEBUG) logger.log(throwable, "DEBUG");
    }

    /**
     * Info logging should be used for information that may prove useful for any developer.
     * Logging related to debugging a specific issue should use the debug level.
     */
    public static void info(String message) {
      //#debug info
      if(level<=INFO) logger.log(message, "INFO");
    }

    /**
     * Info logging should be used for information that may prove useful for any developer.
     * Logging related to debugging a specific issue should use the debug level.
     */
    public static void info(Throwable throwable) {
      //#debug info
      if(level<=INFO) logger.log(throwable, "INFO");
    }

    /**
     * Warn logging should be used for errors which should be reported in the testing phase.
     * These logs could be, for example, written out to the filesystem during testing.
     */
    public static void warn(String message) {
      //#debug warn
      if(level<=WARN) logger.log(message, "WARN");
    }

    /**
     * Warn logging should be used for errors which should be reported in the testing phase.
     * These logs could be, for example, written out to the filesystem during testing.
     */
    public static void warn(Throwable throwable) {
      //#debug warn
      if(level<=WARN) logger.log(throwable, "WARN");
    }

    /**
     * Error logging should be used for errors which should be reported even on a release build.
     * These logs could be, for example, send over the network and therefore should only contain serious errors.
     */
    public static void error(String message) {
      //#debug error
      if(level<=ERROR) logger.log(message, "ERROR");
     }

    /**
     * Error logging should be used for errors which should be reported even on a release build.
     * These logs could be, for example, send over the network and therefore should only contain serious errors.
     */
    public static void error(Throwable throwable) {
      //#debug error
      if(level<=ERROR) logger.log(throwable, "ERROR");
     }

    protected synchronized void log(String message, String level)
    {
      System.err.print("[" + level + "] " + message);
    }

    protected synchronized void log(Throwable throwable, String level)
    {
      System.err.print("[" + level + "] ");
      throwable.printStackTrace();
    }
}



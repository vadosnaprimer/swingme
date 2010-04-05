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
    private final static String LEVEL_NAMES[] = new String[]{"DEBUG", "INFO", "WARN", "ERROR", "FATAL"};
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
     * Developers will only need to see this info when debugging a problem and
     * should work at the info level most of the time to avoid all the output.
     * Expected exceptions can be reported on the debug level
     */
    public static void debug(String message) {
      //#debug debug
      if(level<=DEBUG) logger.log(message, DEBUG);
    }

    /**
     * Debug logging is the most verbose and can be used for any type of logging.
     * Developers will only need to see this info when debugging a problem and
     * should work at the info level most of the time to avoid all the output.
     * Expected exceptions can be reported on the debug level
     */
    public static void debug(Exception exception) {
      //#debug debug
      if(level<=DEBUG) logger.log(exception, DEBUG);
    }

    /**
     * Info logging should be used for information that may prove useful for any
     * developer. Logging related to debugging a specific issue or with lots of
     * output should use the debug level. Unexpected exceptions without
     * implications can be reported on the info level.
     */
    public static void info(String message) {
      //#debug info
      if(level<=INFO) logger.log(message, INFO);
    }

    /**
     * Info logging should be used for information that may prove useful for any
     * developer. Logging related to debugging a specific issue or with lots of
     * output should use the debug level. Unexpected exceptions without
     * implications can be reported on the info level.
     */
    public static void info(Exception exception) {
      //#debug info
      if(level<=INFO) logger.log(exception, INFO);
    }

    /**
     * Warn logging should be used for any errors or bugs. Unexpected exceptions
     * with implications and errors can be reported on the warn level.
     */
    public static void warn(String message) {
      //#debug warn
      if(level<=WARN) logger.log(message, WARN);
    }

    /**
     * Warn logging should be used for any errors or bugs. Unexpected exceptions
     * with implications and errors can be reported on the warn level. Warn
     * logging can also and should be used for assertions.
     */
    public static void warn(Exception exception) {
      //#debug warn
      if(level<=WARN) logger.log(exception, WARN);
    }

    /**
     * Warn logging should be used for any errors or bugs. Unexpected exceptions
     * with implications and errors can be reported on the warn level. Warn
     * logging can also and should be used for assertions.
     */
    public static void warn(Error error) {
      //#debug warn
      if(level<=WARN) logger.log(error, WARN);
    }

    /**
     * Warn logging should be used for any errors or bugs. Unexpected exceptions
     * with implications and errors can be reported on the warn level. Warn
     * logging can also and should be used for assertions.
     */
    public static void warn(boolean assertion) {
      //#debug warn
      if(level<=WARN && assertion) throw new IllegalArgumentException("Assertion failed");
    }

    /**
     * Error logging should be used for serious errors which should be reported
     * even on a release build. Throwable try catch blocks surrounding code entry
     * points should report on the error level.
     */
    public static void error(String message) {
      //#debug error
      if(level<=ERROR) logger.log(message, ERROR);
     }

    /**
     * Error logging should be used for serious errors which should be reported
     * even on a release build. Throwable try catch blocks surrounding code entry
     * points should report on the error level.
     */
    public static void error(Throwable throwable) {
      //#debug error
      if(level<=ERROR) logger.log(throwable, ERROR);
     }

    public static void printStackTrace(String message, int level)
    {
      //#mdebug error
      if(Logger.level<=level) try
      {
        throw new Exception(message);
      }
      catch(Exception e)
      {
        logger.log(e, level);
      }
      //#enddebug
    }

    protected String toString(int level)
    {
      return "[" + LEVEL_NAMES[level] + "] ";
    }

    protected synchronized void log(String message, int level)
    {
      System.err.println(toString(level) + message);
    }

    protected synchronized void log(Throwable throwable, int level)
    {
      System.err.print(toString(level));
      throwable.printStackTrace();
    }
}



package net.yura.mobile.logging;

import java.io.PrintStream;

/**
 * @author Orens
 */
public class Logger {

    /**
     * @see java.util.logging.Level#FINE Level.FINE
     */
    public final static int DEBUG = 0;

    /**
     * @see java.util.logging.Level#INFO Level.INFO
     */
    public final static int INFO = 1;

    /**
     * @see java.util.logging.Level#WARNING Level.WARNING
     */
    public final static int WARN = 2;

    /**
     * @see java.util.logging.Level#SEVERE Level.SEVERE
     */
    public final static int ERROR = 3;

    /**
     * can not log at this level, is used to turn off logging
     * @see java.util.logging.Level#OFF  Level.OFF 
     */
    public final static int FATAL = 4;

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
    //#mdebug debug
    public static void debug(String message) {
      if (level <= DEBUG) logger.log(message, DEBUG);
    }
    //#enddebug

    /**
     * Debug logging is the most verbose and can be used for any type of logging.
     * Developers will only need to see this info when debugging a problem and
     * should work at the info level most of the time to avoid all the output.
     * Expected exceptions can be reported on the debug level
     */
    public static void debug(String error, Throwable exception) {
      //#debug debug
      if (level <= DEBUG) logger.log(error, exception, DEBUG);
    }

    /**
     * Info logging should be used for information that may prove useful for any
     * developer. Logging related to debugging a specific issue or with lots of
     * output should use the debug level. Unexpected exceptions without
     * implications can be reported on the info level.
     */
    //#mdebug info
    public static void info(String message) {
      if (level <= INFO) logger.log(message, INFO);
    }
    //#enddebug

    /**
     * Info logging should be used for information that may prove useful for any
     * developer. Logging related to debugging a specific issue or with lots of
     * output should use the debug level. Unexpected exceptions without
     * implications can be reported on the info level.
     */
    public static void info(String error, Throwable exception) {
      //#debug info
      if (level <= INFO) logger.log(error, exception, INFO);
    }

    /**
     * Warn logging should be used for any errors or bugs. Unexpected exceptions
     * with implications and errors can be reported on the warn level.
     */
    //#mdebug warn
    public static void warn(String message) {
      if (level <= WARN) logger.log(message, WARN);
    }
    //#enddebug

    /**
     * Warn logging should be used for any errors or bugs. Unexpected exceptions
     * with implications and errors can be reported on the warn level. Warn
     * logging can also and should be used for assertions.
     */
    public static void warn(String error, Throwable exception) {
      //#debug warn
      if (level <= WARN) logger.log(error, exception, WARN);
    }

    /**
     * Warn logging should be used for any errors or bugs. Unexpected exceptions
     * with implications and errors can be reported on the warn level. Warn
     * logging can also and should be used for assertions.
     */
    //#mdebug warn
    public static void warn(boolean assertion) {
      if (level <= WARN && assertion) throw new IllegalArgumentException("Assertion failed");
    }
    //#enddebug

    /**
     * Error logging should be used for serious errors which should be reported
     * even on a release build. Throwable try catch blocks surrounding code entry
     * points should report on the error level.
     */
    //#mdebug error
    public static void error(String message) {
      //#debug error
      if (level <= ERROR) logger.log(message, ERROR);
     }
    //#enddebug

    /**
     * Error logging should be used for serious errors which should be reported
     * even on a release build. Throwable try catch blocks surrounding code entry
     * points should report on the error level.
     */
    public static void error(String error, Throwable throwable) {
      //#debug error
      if (level <= ERROR) logger.log(error, throwable, ERROR);
     }

    /**
     * same as the Java SE method with the same name
     * @see java.lang.Thread#dumpStack() Thread.dumpStack()
     */
    public static void dumpStack() {
      //#mdebug debug
      try {
        throw new Exception("Stack trace");
      }
      catch(Exception e) {
        e.printStackTrace();
      }
      //#enddebug
    }

    protected String toString(int level)
    {
      return "[" + LEVEL_NAMES[level] + "] ";
    }

    protected synchronized void log(String message, int level)
    {
      PrintStream out = level<WARN ? System.out : System.err;
      out.println(toString(level) + message);
    }

    protected synchronized void log(String error, Throwable throwable, int level)
    {
      System.err.println(toString(level) + (error == null ? "" : " " + error) + " " + throwable);
      throwable.printStackTrace();
    }
}



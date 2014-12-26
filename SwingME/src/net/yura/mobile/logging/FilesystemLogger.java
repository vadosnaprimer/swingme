package net.yura.mobile.logging;

import java.io.IOException;
import java.io.PrintStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author Orens
 *
 */
public class FilesystemLogger extends Logger {

  private Logger logger;
  private static FileConnection file;
  private static PrintStream log;

  public FilesystemLogger(Logger logger)
  {
    this.logger = logger;
  }

  public FilesystemLogger()
  {
    this(new Logger());
  }

  private static void open() throws IOException
  {
    file = (FileConnection) Connector.open(System.getProperty("fileconn.dir.photos") + System.currentTimeMillis() + ".log", Connector.WRITE);
    file.create();
    log = new PrintStream(file.openOutputStream());
  }

  protected synchronized void log(String message, int level) {
    logger.log(message, level);
    try {
      if (log == null) {
        open();
      }
      log.println(toString(level) + message);
    } catch (IOException e) {
      logger.log(null, e, WARN);
    }
  }

  protected synchronized void log(String error, Throwable throwable, int level) {
    logger.log(error, throwable, level);
    try {
      if (log == null) {
        open();
      }
      String stacktrace = CallStack.getStacktrace();
      log.print(toString(level) + (error == null ? "" : error + " ") + (stacktrace.length() == 0 ? throwable.toString() + "\n" : stacktrace));
    } catch (IOException e) {
      logger.log(null, e, WARN);
    }
  }

  public static void close() {
    try {
      file.close();
    }
    catch(IOException e) {
      Logger.warn(null, e);
    }
  }
}



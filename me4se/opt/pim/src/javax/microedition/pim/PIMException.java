package javax.microedition.pim;

/**
 * @author Stefan Haustein
 */
public class PIMException extends Exception {

  public static final int FEATURE_NOT_SUPPORTED = 0;
  public static final int GENERAL_ERROR = 1;
  public static final int LIST_CLOSED = 2;
  public static final int LIST_NOT_ACCESSIBLE = 3;
  public static final int MAX_CATEGORIES_EXCEEDED = 4;
  public static final int UNSUPPORTED_VERSION = 5;
  public static final int UPDATE_ERROR = 6;

  int reason;

  // Constructs a new instance of this class with its stack trace filled in.
  public PIMException() {
    reason = 1;
  }

  // Constructs a new instance of this class with its stacktrace and message
  // filled in.
  public PIMException(java.lang.String detailMessage) {
    this(detailMessage, 1);
  }

  // Constructs a new instance of this class with its stacktrace, message, and
  // reason filled in.
  PIMException(java.lang.String detailMessage, int reason) {
    super(detailMessage);
    this.reason = reason;
  }

  public int getReason() {
    return reason;
  }
}

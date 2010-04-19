package javax.microedition.pim;

/**
 * Represents an exception thrown when a field is referenced that is not supported
 * in the particular PIM list that an element belongs to.
 */
public class UnsupportedFieldException extends java.lang.RuntimeException
{
	private int field = -1;

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace filled in. A
	 * default value of -1 is assigned to the Field.
	 * 
	 */
	public UnsupportedFieldException()
	{
		super();
	}

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace and message filled
	 * in. A default value of -1 is assigned to the Field.
	 * 
	 * @param detailMessage - String The detail message for the exception.
	 */
	public UnsupportedFieldException(java.lang.String detailMessage)
	{
		super( detailMessage );
	}

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace, message, and
	 * offending field filled in.
	 * 
	 * @param detailMessage - String The detail message for the exception.
	 * @param field - int the offending field for the exception.
	 */
	public UnsupportedFieldException(java.lang.String detailMessage, int field)
	{
		super( detailMessage );
		this.field = field;
	}

	/**
	 * 
	 * Method to access the field for which this exception is thrown. -1 indicates no
	 * field value has been assigned to this exception.
	 * 
	 * 
	 * @return int representing the offending field.
	 */
	public int getField()
	{
		return this.field;
	}
}

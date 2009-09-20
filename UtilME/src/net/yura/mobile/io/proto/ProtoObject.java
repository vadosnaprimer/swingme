package net.yura.mobile.io.proto;

public class ProtoObject
{
	private int    index           = 0;
	private int    type            = 0;
	private long   length          = 0L;
    private long   value           = 0L;
	private byte[] payload         = null;

	public ProtoObject( int index , int type , long length , long value , byte[] payload )
	{
	    this.index   = index;
	    this.type    = type;
	    this.length  = length;
	    this.value   = value;
	    this.payload = payload;
	}
/*
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "ProtoObject {Index" );
        sb.append( index );
        sb.append( " Type " );
        sb.append( type );
        sb.append( " Length " );
        sb.append( length );
        sb.append( " Value " );
        sb.append( value );
        sb.append( "}" );
        return( sb.toString() );
    }
*/
	public int getIndex()
	{
		return this.index;
	}
	
	public int getType()
	{
	    return this.type;
	}
	
	public long getLength()
	{
	    return this.length;
	}
	
	public long getValue()
	{
	    return this.value;
	}
	
	public byte[] getPayload()
	{
	    return this.payload;
	}
	
	public java.lang.Boolean getBoolean()
	{
	    // Payload is a variable integer, expected as 1 bit signed integer
	    return new java.lang.Boolean( (value != 0L) );
	}

    public java.lang.Byte getByte()
    {
	    // Payload is a variable integer, expected as 8 bit signed integer
	    return new java.lang.Byte( (byte)value );
    }
    
    public java.lang.Character getCharacter()
    {
	    // Payload is a variable integer, expected as 16 bit signed integer
	    return new java.lang.Character( (char)value );
    }	
    
    public java.lang.Double getDouble()
    {
        // payload is a fixed integer, in LSB-MSB format, 64 bits in size
        return new java.lang.Double( Double.longBitsToDouble(value) );
    }
    
    public java.lang.Float getFloat()
    {
        // payload is a fixed integer, in LSB-MSB format, 32 bits in size
        return new java.lang.Float( Float.intBitsToFloat((int)value) );
    }
    
    public java.lang.Integer getInteger()
    {
        return new java.lang.Integer( (int)value );
	    // Payload is a variable integer, expected as 32 bit signed integer, in zigzag encoding
    }
    
    public java.lang.Long getLong()
    {
	    // Payload is a variable integer, expected as 64 bit signed integer, in zigzag encoding
        return new java.lang.Long( value );
    }

    public java.lang.Short getShort()
    {
	    // Payload is a variable integer, expected as 16 bit signed integer
	    return new java.lang.Short( (short)value );
    }
    
    public java.lang.String getString()
    {
        if ( payload == null )
            return null;
            
        // payload is a UTF-8 encoded byte string
        return new java.lang.String( payload );
    }
    
    public byte[] getByteArray()
    {
        // payload is a byte array
        return this.payload;
    }
}
package net.yura.mobile.io.proto;

import java.io.*;
import java.util.*;

public class ProtoInputStream extends InputStream
{
    // WIRE FORMAT TYPES
    public static final int WIRE_FORMAT_VARINT            = 0;
    public static final int WIRE_FORMAT_SIXTY_FOUR_BIT    = 1;
    public static final int WIRE_FORMAT_LENGTH_DELIMITED  = 2;
    public static final int WIRE_FORMAT_START_GROUP       = 3; // DEPRECATED AND NOT SUPPORTED
    public static final int WIRE_FORMAT_END_GROUP         = 4; // DEPRECATED AND NOT SUPPORTED
    public static final int WIRE_FORMAT_THIRTY_TWO_BIT    = 5;

    // JAVA TYPES (USED INTERNALLY WHEN MAPPING FIELD TYPES - NOT THE SAME AS OBJECT TYPES)
    public static final int JAVA_BOOLEAN                  = 0;
    public static final int JAVA_BYTE                     = 1;
    public static final int JAVA_CHARACTER                = 2;
    public static final int JAVA_DOUBLE                   = 3;
    public static final int JAVA_FLOAT                    = 4;
    public static final int JAVA_INTEGER                  = 5;
    public static final int JAVA_LONG                     = 6;
    public static final int JAVA_SHORT                    = 7;
    public static final int JAVA_BYTE_ARRAY               = 8;
    public static final int JAVA_STRING                   = 9;

    // MAXIMUM FIELD INDEX POSSIBLE
    public static final long MAXIMUM_FIELD_INDEX          = 536870911L;
    
    // RESERVED FIELDS
    public static final long FIRST_RESERVED_FIELD_INDEX   = 19000L;
    public static final long LAST_RESERVED_FIELD_INDEX    = 19999L;

    InputStream in;
    private static String NULL_STREAM = "Stream is null";

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // INPUTSTREAM METHODS
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public ProtoInputStream( InputStream in )
    {
        this.in = in;
    }
    
    public int available() throws IOException
    {
        if ( this.in == null )
            throw new IOException(NULL_STREAM);

        return this.in.available();
    }
    
    public void close() throws IOException
    {
        if ( this.in == null )
            throw new IOException(NULL_STREAM);

        this.in.close();
    }
    
    public void mark( int readlimit )
    {
        if ( this.in != null )
            this.in.mark( readlimit );
    }
    
    public boolean markSupported()
    {
        if ( this.in == null )
            return false;

        return this.in.markSupported();
    }
    
    public int read() throws IOException
    {
        if ( this.in == null )
            throw new IOException(NULL_STREAM);

        return this.in.read();
    }
    
    public int read(byte[] b) throws IOException
    {
        if ( this.in == null )
            throw new IOException(NULL_STREAM);

        return this.in.read(b);
    }
    
    public int read(byte[] b, int off, int len) throws IOException
    {
        if ( this.in == null )
            throw new IOException(NULL_STREAM);

        return this.in.read(b,off,len);
    }
    
    public void reset() throws IOException
    {
        if ( this.in == null )
            throw new IOException(NULL_STREAM);

        this.in.reset();
    }
    
    public long skip( long n ) throws IOException
    {
        if ( this.in == null )
            throw new IOException(NULL_STREAM);

        return this.in.skip(n);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // PROTOCOL BUFFER METHODS
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public ProtoObject readProto() throws IOException
    {
        ProtoObject protoObject = null;
        
        long length     = 0L;
        long prefix     = readVariableInteger();
        byte [] payload = null;
        int type        = (int)(prefix & 7);
        int index       = (int)(prefix >> 3);
        
        long value      = 0L;
        
        switch( type )
        {
            case ProtoInputStream.WIRE_FORMAT_VARINT:
                value = readVariableInteger();          
                break;
                
            case ProtoInputStream.WIRE_FORMAT_SIXTY_FOUR_BIT:    
                length  = 8L;
                payload = new byte[ (int)length ];
                read( payload );
                if ( read( payload ) < 8 )
                    throw new IOException( "Unexpected end of stream for 64-bit value" );
                value = (((long)payload[7]) << 56) |
                        (((long)payload[6]) << 48) |
                        (((long)payload[5]) << 40) |
                        (((long)payload[4]) << 32) |
                        (((long)payload[3]) << 24) |
                        (((long)payload[2]) << 16) |
                        (((long)payload[1]) <<  8) |
                        (((long)payload[0]));
                        
                break;
                
            case ProtoInputStream.WIRE_FORMAT_LENGTH_DELIMITED: 
                length = readVariableInteger();
                
                payload = new byte[ (int)length ];
                if ( length > 0 )
                {
                    if ( read( payload ) < length )
                        throw new IOException( "Unexpected end of stream for length delimited object" );
                }
                
                break;
                
            case ProtoInputStream.WIRE_FORMAT_THIRTY_TWO_BIT:    
                length  = 4L;
                payload = new byte[ (int)length ];
                if ( read( payload ) < 4 )
                    throw new IOException( "Unexpected end of stream for 32-bit value" );
                value = (((long)payload[3]) << 24) |
                        (((long)payload[2]) << 16) |
                        (((long)payload[1]) <<  8) |
                        (((long)payload[0]));
                break;

            case ProtoInputStream.WIRE_FORMAT_START_GROUP:       
            case ProtoInputStream.WIRE_FORMAT_END_GROUP:    
                throw new IOException( "Deprecated Wire Type (Group)" );
                 
            default:
                throw new IOException( "Unknown Wire Type" );
        }

        return new ProtoObject( index , type , length , value , payload );
    }

    public long readVariableInteger() throws IOException
    {
        long value = 0L;
        int i      = 0;
        int shift  = 0;
        do
        {
            byte b   =  (byte)read();
            
            long tmp =  ((long)b) & 0xffL;
            tmp      &= 0x7FL;
            tmp     <<= shift;
            value    |= tmp;            
            shift   += 7;

            if ( (b & (byte)0x80) != (byte)0x80 )
                break;

        } while ( true );

        return fromZigZag(value);
    }  
    
    public long fromZigZag( long n )
    {
        return n;
        //return (n >>> 1) ^ -(n & 1);
    }
}
package net.yura.mobile.io.proto;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class ProtoOutputStream extends OutputStream
{
    private OutputStream out = null;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // OUTPUTSTREAM METHODS
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public ProtoOutputStream( OutputStream out )
    {
        this.out = out;
    }

    public void close() throws IOException
    {
        if ( out != null )
            out.close();
    }

    public void flush() throws IOException
    {
        if ( out != null )
            out.flush();
    }

    public void write( byte[] b ) throws IOException
    {
        if ( out != null && b != null )
            out.write( b );
    }
    
    public void write( byte[] b, int off, int len ) throws IOException 
    {
        if ( out != null && b != null )
            out.write( b , off , len );
    }

    public void write( int b ) throws IOException
    {
        byte[] x = new byte[4];
        x[0] = (byte)( ( b >> 24 ) & 0xff );
        x[1] = (byte)( ( b >> 16 ) & 0xff );
        x[2] = (byte)( ( b >>  8 ) & 0xff );
        x[3] = (byte)( ( b       ) & 0xff );
        write( x );
    }    

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // PROTOCOL BUFFER METHODS
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void write( int fieldIndex , boolean value ) throws IOException { write( fieldIndex , (value ? 1L : 0L) ); }
    public void write( int fieldIndex , byte    value ) throws IOException { write( fieldIndex , (long)value ); }
    public void write( int fieldIndex , char    value ) throws IOException { write( fieldIndex , (long)value ); }
    public void write( int fieldIndex , int     value ) throws IOException { write( fieldIndex , (long)value ); }
    public void write( int fieldIndex , short   value ) throws IOException { write( fieldIndex , (long)value ); }

    public void write( int fieldIndex , long    value ) throws IOException 
    {
        byte[] prefix  = encodeBytes( longToByteArray( encodeFieldIndexAndWireFormatType( fieldIndex , ProtoInputStream.WIRE_FORMAT_VARINT ) ) );
        byte[] payload = encodeVariableInteger( value );
        write( concatenate( prefix , payload ) ); 
    }

    public void write( int fieldIndex , double  value ) throws IOException 
    {
        byte[] prefix  = encodeBytes( longToByteArray( encodeFieldIndexAndWireFormatType( fieldIndex , ProtoInputStream.WIRE_FORMAT_SIXTY_FOUR_BIT ) ) );
        byte[] payload = encodeDouble( value );
        write( concatenate( prefix , payload ) ); 
    }

    public void write( int fieldIndex , float   value ) throws IOException 
    { 
        byte[] prefix  = encodeBytes( longToByteArray( encodeFieldIndexAndWireFormatType( fieldIndex , ProtoInputStream.WIRE_FORMAT_THIRTY_TWO_BIT ) ) );
        byte[] payload = encodeSingle( value );
        write( concatenate( prefix , payload ) ); 
    }

    public void write( int fieldIndex , java.lang.Object o ) throws IOException
    {
        if ( fieldIndex > -1 && o != null )
            write( encodeObject( fieldIndex , o ) );
    }

    public byte[] encodeObject( int fieldIndex , java.lang.Object value ) throws IOException
    {
        if ( fieldIndex < 0 )
            return null;
        if ( value == null )
            return null;

        int type      = javaTypeToWireFormatType( value );
        byte[] prefix = encodeVariableInteger( encodeFieldIndexAndWireFormatType( fieldIndex , type ) );
        byte[] result = encodeObject( value );
        byte[] length = encodeVariableInteger( (long)result.length );
        
        return concatenate( prefix , concatenate( length , result ) );
    }        

    public int javaTypeToWireFormatType( java.lang.Object value ) throws IOException
    {
        if ( value instanceof byte[] )
            return ProtoInputStream.WIRE_FORMAT_LENGTH_DELIMITED;
    
        if ( value instanceof java.lang.String )
            return ProtoInputStream.WIRE_FORMAT_LENGTH_DELIMITED;        

        if ( value instanceof java.lang.Boolean )
            return ProtoInputStream.WIRE_FORMAT_VARINT;

        if ( value instanceof java.lang.Byte )
            return ProtoInputStream.WIRE_FORMAT_VARINT;
    
        if ( value instanceof java.lang.Character )
            return ProtoInputStream.WIRE_FORMAT_VARINT;
    
        if ( value instanceof java.lang.Double )
            return ProtoInputStream.WIRE_FORMAT_SIXTY_FOUR_BIT;

        if ( value instanceof java.lang.Float )
            return ProtoInputStream.WIRE_FORMAT_THIRTY_TWO_BIT;

        if ( value instanceof java.lang.Integer )
            return ProtoInputStream.WIRE_FORMAT_VARINT;

        if ( value instanceof java.lang.Long )
            return ProtoInputStream.WIRE_FORMAT_VARINT;

        if ( value instanceof java.lang.Short )
            return ProtoInputStream.WIRE_FORMAT_VARINT;

        throw new IOException("Unsupported Java Type");
    }

    public long encodeFieldIndexAndWireFormatType( int fieldIndex , int wireFormatType )
    {
        long result = ((long)fieldIndex) << 3;
        result      |= (long)(wireFormatType & 0x7);
        return result;
    }
        
    public byte[] encodeObject( java.lang.Object value )
    {
        if ( value instanceof byte[] )
        {
            byte[] bytes  = (byte [])value;
            byte[] length = encodeVariableInteger( (long)bytes.length );
            return concatenate( length , bytes );        
        }

        if ( value instanceof java.lang.String )
        {
            byte[] bytes = null;
            try
            {
                bytes = ((java.lang.String)value).getBytes("UTF8");
            }
            catch( UnsupportedEncodingException uee )
            {
                throw new RuntimeException();
            }
            
            return bytes;        
        }

        if ( value instanceof java.lang.Boolean )
        {
            java.lang.Boolean b = (java.lang.Boolean)value;
            return encodeVariableInteger( b.booleanValue() ? 1L : 0L );
        }

        if ( value instanceof java.lang.Byte )
            return encodeVariableInteger( ((java.lang.Byte)value).byteValue() );
    
        if ( value instanceof java.lang.Character )
            return encodeVariableInteger( ((java.lang.Character)value).charValue() );
    
        if ( value instanceof java.lang.Double )
            return encodeDouble( ((java.lang.Double)value).doubleValue() );

        if ( value instanceof java.lang.Float )
            return encodeSingle( ((java.lang.Float)value).floatValue() );

        if ( value instanceof java.lang.Integer )
            return encodeVariableInteger( ((java.lang.Integer)value).intValue() );

        if ( value instanceof java.lang.Long )
            return encodeVariableInteger( ((java.lang.Long)value).longValue() );

        if ( value instanceof java.lang.Short )
            return encodeVariableInteger( ((java.lang.Short)value).shortValue() );

        return new byte[0];
    }

    public byte[] encodeVariableInteger( long value )
    {
        return encodeBytes( longToByteArray( toZigZag( value ) ) );
    }

    public byte[] encodeSingle( float value )
    {
        int floatBits   = Float.floatToIntBits( value );
        byte[] data     = intToByteArray( floatBits );
        return data;        
    }
    
    public byte[] encodeDouble( double value )
    {
        long doubleBits = Double.doubleToLongBits( value );
        byte[] data     = longToByteArray( doubleBits );
        return data;        
    }

    public byte[] encodeBytes( byte[] inputBuffer )
    {
        if ( inputBuffer == null )
            return null;
            
        if ( inputBuffer.length == 0 )
            return inputBuffer;
    
        // Create an output buffer.
        byte[] outputBuffer = new byte[ 1 ];
        
        int bits        = 0;
        int outputIndex = 0;

        // Preprocess the input buffer to remove unnecessary 0 bytes
        while( inputBuffer[ inputBuffer.length - 1 ] == (byte)0 && inputBuffer.length > 1 )
        {
            byte[] tmp = new byte[ inputBuffer.length - 1 ];
            System.arraycopy( inputBuffer , 0 , tmp , 0 , inputBuffer.length - 1 );
            inputBuffer = tmp;        
        }        
        
        // Now convert to var int format
        for( int inputIndex = 0 ; inputIndex < inputBuffer.length ; inputIndex++ )
        {
            // Fetch the current byte
            byte b = inputBuffer[ inputIndex ];

            for ( int shift = 7 ; shift >= 0 ; shift-- )
            {
                // Get the least significant bit from the current byte in the input buffer.
                int lsb = (int)( b & 1);

                // Set the most significant bit in the output buffer if necessary
                if ( lsb == 1 )
                    outputBuffer[ outputIndex ] = (byte)( (int)outputBuffer[ outputIndex ] | 0x80 );
                
                // Shift to the right, clearing top bit for next time around (unsigned shift right)
                outputBuffer[ outputIndex ] >>>= 1;
                outputBuffer[ outputIndex ] &= 0x7f;
                
                // Update the number of bits written to the outputBuffer
                bits++;

                // If we have written 7 bits to the output byte, then update the output position           
                if ( bits == 7 )
                {
                    //System.out.println( "outputIndex advancing" );
                    bits = 0;
                    outputBuffer[ outputIndex ] |= (byte)0x80;
                    
                    // Don't add a new byte to the array if there is nothing left to process, i.e. if we're on the last bit of the last input byte
                    
                    byte[] tmp = new byte[ outputBuffer.length + 1 ];
                    System.arraycopy( outputBuffer , 0 , tmp , 0 , outputBuffer.length );
                    outputBuffer = null; // ensure old array released for gc explicitly
                    outputBuffer = tmp;

                    outputIndex++;
                }
                
                b >>= 1;   
            }                    
        }

        // Make sure that the last byte of the output buffer does not have the high bit set and remove spurious bytes
        if ( outputBuffer.length > 0 )
        {
            outputBuffer[ outputBuffer.length - 1 ] &= ( byte )0x7f;
                
            if ( outputBuffer[ outputBuffer.length - 1 ] == 0 )
            {
                byte[] tmp = new byte[ outputBuffer.length - 1 ];
                System.arraycopy( outputBuffer , 0 , tmp , 0 , outputBuffer.length - 1 );
                outputBuffer = null; // ensure old array released for gc explicitly
                outputBuffer = tmp;
            }

            if ( outputBuffer.length > 0 )
                outputBuffer[ outputBuffer.length - 1 ] &= ( byte )0x7f;
        }

        return outputBuffer;        
    }
    
    public byte[] concatenate( byte[] prefix , byte[] suffix )
    {
        if ( prefix == null && suffix == null )
            return null;
        if ( prefix == null )
            return suffix;
        if ( suffix == null )
            return prefix;
        
        byte [] result = new byte[ prefix.length + suffix.length ];

        System.arraycopy( prefix , 0 , result , 0             , prefix.length );
        System.arraycopy( suffix , 0 , result , prefix.length , suffix.length );
        
        return result; 
    }
    
    public byte[] resize( byte[] buffer , int newSize )
    {
        if ( buffer == null ) 
            return buffer;
            
        if ( newSize < 0 )
            return buffer;

        byte[] result = new byte[ newSize ];
        System.arraycopy( buffer , 0 , result , 0 , ( newSize > buffer.length ? buffer.length : newSize ) );
        
        return result;
    }

    public byte[] intToByteArray( int value )
    {
        byte[] x = new byte[4];

        x[3] = (byte)( ( value >> 24 ) & 0xffL );
        x[2] = (byte)( ( value >> 16 ) & 0xffL );
        x[1] = (byte)( ( value >>  8 ) & 0xffL );
        x[0] = (byte)( ( value       ) & 0xffL );

        return x;
    }

    public byte[] longToByteArray( long value )
    {
        byte[] x = new byte[8];

        x[7] = (byte)( ( value >> 56 ) & 0xffL );
        x[6] = (byte)( ( value >> 48 ) & 0xffL );
        x[5] = (byte)( ( value >> 40 ) & 0xffL );
        x[4] = (byte)( ( value >> 32 ) & 0xffL );
        x[3] = (byte)( ( value >> 24 ) & 0xffL );
        x[2] = (byte)( ( value >> 16 ) & 0xffL );
        x[1] = (byte)( ( value >>  8 ) & 0xffL );
        x[0] = (byte)( ( value       ) & 0xffL );

        return x;
    }

    public long toZigZag( long n )
    {
        return n;
        //return (n << 1) ^ (n >> 63);
    }
}
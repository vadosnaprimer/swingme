package net.yura.mobile.io.proto;

import java.io.*;
import java.util.*;

public class ProtoUtil
{
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // OBJECT IDENTITIES
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public static final int UNKNOWN                            =    0;
    public static final int END                                =    1;
    public static final int NULL                               =    2;
    
    public static final int JAVA_LANG_BOOLEAN                  = 1001;
    public static final int JAVA_LANG_BYTE                     = 1002;
    public static final int JAVA_LANG_CHARACTER                = 1003;
    public static final int JAVA_LANG_DOUBLE                   = 1004;
    public static final int JAVA_LANG_FLOAT                    = 1005;
    public static final int JAVA_LANG_INTEGER                  = 1006;
    public static final int JAVA_LANG_LONG                     = 1007;
    public static final int JAVA_LANG_SHORT                    = 1008;
    public static final int JAVA_LANG_STRING                   = 1009;
    public static final int JAVA_UTIL_HASHTABLE                = 1010;
    public static final int JAVA_UTIL_VECTOR                   = 1011;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // FIELD IDENTITIES
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public final static int END_OF_OBJECT          =    1; // Special field used to define the end of variable length objects.
    public final static int PREFIX_OBJECTTYPE      =    2;

    protected int prefixMessage = UNKNOWN;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // CONSTRUCTOR
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public ProtoUtil()
    {
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // STREAMS
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    protected ProtoInputStream  protoInputStream  = null;
    protected ProtoOutputStream protoOutputStream = null;

    public void setInputStream( InputStream in )
    {
        this.protoInputStream = new ProtoInputStream( in );
    }

    public void setOutputStream( OutputStream out )
    {
        this.protoOutputStream = new ProtoOutputStream( out );
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // "PREFIXED" MESSAGE METHODS
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public Object readObject( ProtoInputStream in ) throws IOException
    {
        Object           result = null;
        ProtoInputStream _in    = ( in == null ? this.protoInputStream : in );

        // ALWAYS READ A PREFIX
        this.prefixMessage = readPrefix( _in );
        
        ProtoObject proto = null;
        
        if ( this.prefixMessage != UNKNOWN )
        {
            switch( this.prefixMessage )
            {
                case END:     // End of collection
                    this.prefixMessage = UNKNOWN;
                    return null;

            	case NULL:    // Returns NULL
                    this.prefixMessage = UNKNOWN;
                    return null;
            	                      
            	case JAVA_LANG_BOOLEAN:      
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getBoolean();
            	    break;
            	
            	case JAVA_LANG_BYTE:         
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getByte();
            	    break;

            	case JAVA_LANG_CHARACTER:         
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getCharacter();
            	    break;

            	case JAVA_LANG_DOUBLE:       
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getDouble();
            	    break;

            	case JAVA_LANG_FLOAT:        
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getFloat();
            	    break;

            	case JAVA_LANG_INTEGER:
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getInteger();
            	    break;

            	case JAVA_LANG_LONG:         
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getLong();
            	    break;

            	case JAVA_LANG_SHORT:        
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getShort();
            	    break;

            	case JAVA_LANG_STRING:
            	    proto  = _in.readProto();
            	    if ( proto != null )
                	    result = proto.getString();
            	    break;

            	case JAVA_UTIL_HASHTABLE:  
            	    Hashtable h = new Hashtable();
            	    Object key = null;
            	    while( ( key = readObject( _in ) ) != null )
            	    {
            	        Object value = readObject( _in );
            	        h.put( key , value );
            	    } 
            	    result = h;
            	    break;

            	case JAVA_UTIL_VECTOR:
            	    Vector v = new Vector();
            	    Object value = null;
            	    while( ( value = readObject( _in ) ) != null )
                        v.addElement(value);
                    result = (Object)v;
                    break;

                case UNKNOWN: // Returns NULL
                default:
                    return null;
            }
        }
        
        return result;    
    }
    
    public void writeObject( Object object , ProtoOutputStream out ) throws IOException
    {
        ProtoOutputStream _out = ( out == null ? this.protoOutputStream : out );
    
    	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	// HANDLE NULL OBJECTS
    	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    	if ( object == null )
    	{
    		writePrefix( NULL , _out );
    		return;
    	}
    
    	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	// BASIC JAVA OBJECTS AND COLLECTIONS START
    	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    	if ( object instanceof java.util.Hashtable )
    	{
    		writePrefix( JAVA_UTIL_HASHTABLE , _out );
    		
    		Hashtable   h = (Hashtable)object;
    		Enumeration e = h.keys();
    		
    		for( ; e.hasMoreElements() ; )
    		{
    		    Object key   = e.nextElement();
    		    Object value = h.get( key );

    		    writeObject( key   , _out );
    		    writeObject( value , _out ); 
    		}

            // Write end of collection prefix type
    		writePrefix( END , _out );
    	}
    	else if ( object instanceof java.util.Vector )
    	{
    		writePrefix( JAVA_UTIL_VECTOR , _out );
    
    		Vector      v = (Vector)object;
    		Enumeration e = v.elements();
    		
    		for( ; e.hasMoreElements() ; )
    		{
    		    Object value = e.nextElement();
    		    writeObject( value , _out ); 
    		}

            // Write end of collection prefix type
    		writePrefix( END , _out );
    	}
    	else 
    	{
    	    int p = UNKNOWN;
    	    
    		p = ( object instanceof java.lang.Boolean   ? JAVA_LANG_BOOLEAN   : p );
    		p = ( object instanceof java.lang.Byte      ? JAVA_LANG_BYTE      : p );
    		p = ( object instanceof java.lang.Character ? JAVA_LANG_CHARACTER : p );
    		p = ( object instanceof java.lang.Double    ? JAVA_LANG_DOUBLE    : p );
    		p = ( object instanceof java.lang.Float     ? JAVA_LANG_FLOAT     : p );
    		p = ( object instanceof java.lang.Integer   ? JAVA_LANG_INTEGER   : p );
    		p = ( object instanceof java.lang.Long      ? JAVA_LANG_LONG      : p );
    		p = ( object instanceof java.lang.Short     ? JAVA_LANG_SHORT     : p );
    		p = ( object instanceof java.lang.String    ? JAVA_LANG_STRING    : p );
    
            if ( p != UNKNOWN )
            {
        		writePrefix( p , _out );
        		_out.write( 0 , object );
        	}
    	}
    
    	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	// BASIC JAVA OBJECTS AND COLLECTIONS END
    	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
    
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // MESSAGE:PREFIX
    // This message type is defined by us uniquely for show the type of following object
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public int readPrefix( ProtoInputStream in ) throws IOException
    {
        int      objPrefix      = UNKNOWN;
        ProtoObject protoObject = null;
        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );
        
        boolean inObject = true;

        while ( inObject )
        {
            protoObject = _in.readProto();
        
            switch( protoObject.getIndex() )
            {
                case PREFIX_OBJECTTYPE:
                    objPrefix = (protoObject.getInteger()).intValue();
                    break;     

                case END_OF_OBJECT:
                default:
                    inObject = false;
                    break;                   
            }
        }
        protoObject = null;

        return objPrefix;
    }
    
    public void writePrefix( int prefix , ProtoOutputStream out ) throws IOException
    {
        ProtoOutputStream _out = ( out == null ? this.protoOutputStream : out );
        if ( prefix != null )
        {   
            if (  prefix == UNKNOWN )
                throw new IOException("Attempt to create UNKNOWN object type prefix");
            _out.write( PREFIX_OBJECTTYPE ,  prefix );
        }            
        _out.write( END_OF_OBJECT , (byte)0 );
        _out.flush();
    }
}
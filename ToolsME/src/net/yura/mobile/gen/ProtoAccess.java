package com.exeus.proto;

import java.io.*;
import java.util.*;

import net.yura.mobile.io.proto.*;

public class ProtoAccess extends ProtoUtil
{
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // OBJECT IDENTITIES
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public final static int COM_EXEUS_PROTO_MESSAGE_TEST       = 10000;
    public final static int COM_EXEUS_PROTO_MESSAGE_NUMBER     = 10001;
    public final static int COM_EXEUS_PROTO_MESSAGE_TESTOBJECT = 10002;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // FIELD IDENTITIES
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public final static int TEST_ID                = 1001;
    
    public final static int NUMBER_NUMBER          = 1101;
    public final static int NUMBER_TYPE            = 1102;
    
    public final static int TEST_OBJECT_TEST       = 1201;
    public final static int TEST_OBJECT_NAME       = 1202;
    public final static int TEST_OBJECT_AGE        = 1203;
    public final static int TEST_OBJECT_NUMBERS    = 1204;
    public final static int TEST_OBJECT_LEGS       = 1205;

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // CONSTRUCTOR
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public ProtoAccess()
    {
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // "PREFIXED" MESSAGE METHODS
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public Object readObject( ProtoInputStream in ) throws IOException, ProtoException
    {
        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );

        Object result = super.readObject( _in );
        
        if ( result != null )
            return result;
        
        // END, or NULL
        if ( result == null && this.prefixMessage == ProtoInputStream.UNKNOWN )
        {
            return null;
        }
        
        // ONLY READ A PREFIX IF THE SUPERCLASS DIDN'T             
        if ( this.prefixMessage == ProtoInputStream.UNKNOWN )
        {
            this.prefixMessage = readPrefix( _in );
        }
        
        ProtoObject proto = null;
        
        switch( this.prefixMessage )
        {
        	case COM_EXEUS_PROTO_MESSAGE_TEST:       
        	    result = readMessageTest( _in );
        	    break; 
        	case COM_EXEUS_PROTO_MESSAGE_NUMBER:     
        	    result = readMessageNumber( _in );
        	    break; 
        	case COM_EXEUS_PROTO_MESSAGE_TESTOBJECT:
        	    result = readMessageTestObject( _in );
        	    break; 
            // This commented out region is an example of what we do for a message which does not have a corresponding 
            // Java object.
            // We don't have a corresponding object for Login, so result will be a hashtable
        	// case COM_EXEUS_PROTO_MESSAGE_LOGIN:
        	//    result = readMessageLogin( _in );    
            default:
                throw new ProtoException( "ProtoAccess - Unknown Object Type - " + this.prefixMessage);
        }
        
        return result;    
    }
    
    public void writeObject( Object object , ProtoOutputStream out ) throws IOException, ProtoException
    {
        ProtoOutputStream _out = ( out == null ? this.protoOutputStream : out );
    
    	if ( object instanceof com.exeus.proto.Test )
    	{
    		writePrefix( COM_EXEUS_PROTO_MESSAGE_TEST , _out );
    		writeMessageTest( (com.exeus.proto.Test)object , _out );	
    		return;
    	}
    	else if ( object instanceof com.exeus.proto.Number )
    	{
    		writePrefix( COM_EXEUS_PROTO_MESSAGE_NUMBER , _out );
    		writeMessageNumber( (com.exeus.proto.Number)object , _out );	
    		return;
    	}
    	else if ( object instanceof com.exeus.proto.TestObject )
    	{
    		writePrefix( COM_EXEUS_PROTO_MESSAGE_TESTOBJECT , _out );
    		writeMessageTestObject( (com.exeus.proto.TestObject)object , _out );	
    		return;
    	}
    	else if ( object instanceof java.util.Hashtable )
    	{
    	    // This is just a sample of how we handle a message type without a corresponding Java object.
    	    // See if the key fields in the Hashtable correspond to a known object. 
    	    
    	    //Hashtable h = (Hashtable)object;
    	    
    	    //if ( ( h.get( "username" ) != null ) &&
    	    //     ( h.get( "password" ) != null )  )
    	    //{
    	        // p.set_objectType( COM_EXEUS_PROTO_MESSAGE_LOGIN );
                // _out.write( LOGIN_USERNAME , (String)h.get("username") );
                // _out.write( LOGIN_PASSWORD , (String)h.get("password") );
                // _out.write( END_OF_OBJECT , (byte)0 ); // Special marker for variable length objects
                // _out.flush();
                // return;
    	    //}
    	}

        super.writeObject( object , out );
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // MESSAGE:TEST
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public Test readMessageTest( ProtoInputStream in ) throws java.io.IOException, ProtoException
    {
        Test        objTest     = new Test();
        ProtoObject protoObject = null;
        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );
        
        boolean inObject = true;
        while ( inObject )
        {
            protoObject = _in.readProto();

            switch( protoObject.getIndex() )
            {
                case TEST_ID:
                    objTest.set_id( (protoObject.getInteger()).intValue() );
                    break;     

                case END_OF_OBJECT:
                default:
                    inObject = false;
                    break;                   
            }
        }
        protoObject = null;

        return objTest;
    }

    public void writeMessageTest( Test test , ProtoOutputStream out  ) throws java.io.IOException, ProtoException
    {
        ProtoOutputStream _out = ( out == null ? protoOutputStream : out );

        if ( test != null )
            _out.write( TEST_ID , test.get_id() );
        _out.write( END_OF_OBJECT , (byte)0 );
        _out.flush();
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // MESSAGE:NUMBER
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public com.exeus.proto.Number readMessageNumber( ProtoInputStream in ) throws java.io.IOException, ProtoException
    {
        com.exeus.proto.Number objNumber = new com.exeus.proto.Number();
        ProtoObject protoObject = null;
        ProtoInputStream _in = ( in == null ? protoInputStream : in );

        boolean inObject = true;
        while ( inObject )
        {
            protoObject = _in.readProto();

            switch( protoObject.getIndex() )
            {
                case NUMBER_NUMBER:
                    objNumber.set_number( protoObject.getString() );
                    break;     

                case NUMBER_TYPE:
                    // The commented out code below shows how to translate an enumeration
                    //int i = (protoObject.getInteger()).intValue()
                    //switch( i )
                    //{
                    //    case 1:  objNumber.set_type( "TYPE_ONE" ); break;
                    //    case 2:  objNumber.set_type( "TYPE_TWO" ); break;
                    //    case 3:  objNumber.set_type( "TYPE_THREE" ); break;
                    //    default: throw new ProtoException("Unknown Enumerated Value");
                    //}
                    //break;

                    objNumber.set_type( (protoObject.getInteger()).intValue() );
                    break;
                
                case END_OF_OBJECT:
                default:
                    inObject = false;
                    break;                   
            }
        }

        protoObject = null;

        return objNumber;        
    }
    
    public void writeMessageNumber( com.exeus.proto.Number number , ProtoOutputStream out  ) throws java.io.IOException, ProtoException
    {
        ProtoOutputStream _out = ( out == null ? protoOutputStream : out );

        if ( number != null )
        {
            _out.write( NUMBER_NUMBER , number.get_number() );
            
            // The commented out code below shows how to write an enumeration
            
            //String s = number.get_type();
            //if ( s.equals( "TYPE_ONE"   ) ) _out.write( NUMBER_TYPE , 1 );
            //if ( s.equals( "TYPE_TWO"   ) ) _out.write( NUMBER_TYPE , 2 );
            //if ( s.equals( "TYPE_THREE" ) ) _out.write( NUMBER_TYPE , 3 );

            _out.write( NUMBER_TYPE   , number.get_type()   );
        }
        _out.write( END_OF_OBJECT , (byte)0 ); // Special marker for variable length objects
        _out.flush();
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // MESSAGE:TESTOBJECT
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public TestObject readMessageTestObject( ProtoInputStream in ) throws java.io.IOException, ProtoException
    {
        ByteArrayInputStream bais          = null;
        TestObject           objTestObject = new TestObject();
        ProtoObject          protoObject   = null;
        
        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );
        
        boolean inObject = true;
        while ( inObject )
        {
            protoObject = _in.readProto();

            switch( protoObject.getIndex() )
            {
                case TEST_OBJECT_TEST:
                    bais = new ByteArrayInputStream( protoObject.getPayload() );
                    objTestObject.set_test( readMessageTest( new ProtoInputStream(bais) ) );
                    bais.close();
                    bais = null;
                    break;
                    
                case TEST_OBJECT_NAME:
                    objTestObject.set_name( protoObject.getString() );
                    break;
                
                case TEST_OBJECT_AGE:
                    objTestObject.set_age( (protoObject.getByte()).byteValue() );
                    break;
                
                case TEST_OBJECT_NUMBERS:
                    bais = new ByteArrayInputStream( protoObject.getPayload() );
                    (objTestObject.get_numbers()).addElement( readMessageNumber(new ProtoInputStream(bais)) );
                    bais.close();
                    bais = null;
                    break;
                
                case TEST_OBJECT_LEGS:
                    (objTestObject.get_legs()).addElement( protoObject.getString() );
                    break;
                    
                case END_OF_OBJECT:
                    inObject = false;    
                
                default:
                    inObject = false;
            }
        }        

        protoObject = null;
        
        return objTestObject;        
    }

    public void writeMessageTestObject( TestObject testObject , ProtoOutputStream out ) throws java.io.IOException, ProtoException
    {
        ByteArrayOutputStream baos   = null;
        byte []               buffer = null;
        byte []               length = null;
        byte []               id     = null;
        Vector                v      = null;
    
        ProtoOutputStream _out = ( out == null ? protoOutputStream : out );

        if ( testObject != null )
        {
            baos = new ByteArrayOutputStream();
            writeMessageTest( testObject.get_test() , new ProtoOutputStream( baos ) );
            buffer = baos.toByteArray();
            baos.close();
            baos   = null;
    
            length = _out.encodeVariableInteger((long)buffer.length);
            id     = _out.encodeVariableInteger( _out.encodeFieldIndexAndWireFormatType( TEST_OBJECT_TEST , ProtoBase.WIRE_FORMAT_LENGTH_DELIMITED ) );
            _out.write( id );
            _out.write( length );
            _out.write( buffer );              
            id     = null;
            length = null;
            buffer = null;
    
            protoOutputStream.write( TEST_OBJECT_NAME    , testObject.get_name() );
    
            protoOutputStream.write( TEST_OBJECT_AGE     , testObject.get_age() );
    
            v = testObject.get_numbers();
            if ( v != null )
            {
                Enumeration e = v.elements();
                for( ; e.hasMoreElements() ; )
                {
                    baos = new ByteArrayOutputStream();
                    writeMessageNumber( (com.exeus.proto.Number)e.nextElement() , new ProtoOutputStream( baos ) );
                    buffer = baos.toByteArray();
                    baos.close();
                    baos   = null;
                    length = _out.encodeVariableInteger((long)buffer.length);
                    id     = _out.encodeVariableInteger( _out.encodeFieldIndexAndWireFormatType( TEST_OBJECT_NUMBERS , ProtoBase.WIRE_FORMAT_LENGTH_DELIMITED ) );
                    _out.write( id );
                    _out.write( length );
                    _out.write( buffer );              
                    id     = null;
                    length = null;
                    buffer = null;
                }
            }
    
            v = testObject.get_legs();
            if ( v != null )
            {
                Enumeration e = v.elements();
                for( ; e.hasMoreElements() ; )
                {
                    String s = (String)e.nextElement();
                    buffer   = s.getBytes();
                    length   = _out.encodeVariableInteger((long)buffer.length);
                    id       = _out.encodeVariableInteger( _out.encodeFieldIndexAndWireFormatType( TEST_OBJECT_LEGS , ProtoBase.WIRE_FORMAT_LENGTH_DELIMITED ) );
                    _out.write( id );
                    _out.write( length );
                    _out.write( buffer );              
                    id     = null;
                    length = null;
                    buffer = null;
                }
            } 
        }

        _out.write( END_OF_OBJECT , (byte)0 ); // Special marker for variable length objects
        _out.flush();
    }
    
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // MESSAGE:LOGIN
    // This commented out section is an example of a message that does not have
    // a corresponding Java object. We read it as a hashtable in this case.
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 
    //public java.util.Hashtable readMessageLogin( ProtoInputStream in ) throws java.io.IOException, ProtoException
    //{
    //    java.util.Hashtable h = new java.util.Hashtable();
    //    ProtoObject protoObject = null;
    //    ProtoInputStream _in = ( in == null ? protoInputStream : in );
    //
    //    boolean inObject = true;
    //    while ( inObject )
    //    {
    //        protoObject = _in.readProto();
    //
    //        switch( protoObject.getIndex() )
    //        {
    //            case LOGIN_PASSWORD:
    //                h.put( protoObject.getString() );
    //                break;     
    //
    //            case LOGIN_USERNAME:
    //                h.put( protoObject.getString() );
    //                break;
    //            
    //            case END_OF_OBJECT:
    //            default:
    //                inObject = false;
    //                break;                   
    //        }
    //    }
    //
    //    protoObject = null;
    //
    //    return h;        
    //}
}
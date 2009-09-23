package net.yura.mobile.gen;

import java.io.*;
import java.util.*;
import net.yura.tools.mobilegen.model.*;
import net.yura.mobile.io.proto.*;

public class ProtoAccess extends ProtoUtil
{
    public ProtoAccess()
    {
    }

    // ==================================================================================
    // OBJECT IDENTITIES
    // ==================================================================================

    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TEST = 10000;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_NUMBER = 10001;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT = 10002;

    // ==================================================================================
    // FIELD IDENTITIES
    // ==================================================================================

    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TEST_ID = 1201;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_NUMBER_NUMBER = 1202;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_NUMBER_TYPE = 1203;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_ID = 1204;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_NAME = 1205;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_AGE = 1206;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_NUMBERS = 1207;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_BODY = 1208;
    public final static int NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_LEGS = 1209;

    // ==================================================================================
    // COLLECTION METHODS
    // ==================================================================================

    public Object readObject( ProtoInputStream in ) throws IOException
    {
        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );
        Object result = super.readObject( _in );
        if ( result != null )
            return result;
        // END, or NULL
        if ( result == null && this.prefixMessage == ProtoUtil.UNKNOWN )
        {
            return null;
        }
        ProtoObject proto = null;
        switch( this.prefixMessage )
        {
            case NET_YURA_TOOLS_MOBILEGEN_MODEL_TEST: 
                result = readTest( _in );
                break; 

            case NET_YURA_TOOLS_MOBILEGEN_MODEL_NUMBER: 
                result = readNumber( _in );
                break; 

            case NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT: 
                result = readTestObject( _in );
                break; 

            default:
                throw new IOException( "ProtoAccess - Unknown Object Type - " + this.prefixMessage);
        }
        return result; 
    }


    public void writeObject( Object obj , ProtoOutputStream out ) throws IOException
    {
        ProtoOutputStream _out = ( out == null ? this.protoOutputStream : out );
        if ( obj instanceof net.yura.tools.mobilegen.model.Test )
        {
            writePrefix( NET_YURA_TOOLS_MOBILEGEN_MODEL_TEST , _out );
            writeTest((net.yura.tools.mobilegen.model.Test)obj , _out );
            return;
        }

        if ( obj instanceof net.yura.tools.mobilegen.model.Number )
        {
            writePrefix( NET_YURA_TOOLS_MOBILEGEN_MODEL_NUMBER , _out );
            writeNumber((net.yura.tools.mobilegen.model.Number)obj , _out );
            return;
        }

        if ( obj instanceof net.yura.tools.mobilegen.model.TestObject )
        {
            writePrefix( NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT , _out );
            writeTestObject((net.yura.tools.mobilegen.model.TestObject)obj , _out );
            return;
        }

        if ( obj instanceof java.util.Hashtable )
        {
            Hashtable h = (Hashtable)obj;
        }
        super.writeObject( obj , out );
    }

    // ==================================================================================
    // OBJECT METHODS
    // ==================================================================================

    public net.yura.tools.mobilegen.model.Test readTest( ProtoInputStream in ) throws IOException
    {
        Hashtable hashtableOfObjectArraysAsVectors = new Hashtable();
        Hashtable hashtableOfObjectArrayTypes = new Hashtable();
        ByteArrayInputStream bais          = null;
        net.yura.tools.mobilegen.model.Test           objTest = new net.yura.tools.mobilegen.model.Test();
        ProtoObject          protoObject   = null;

        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );

        boolean inObject = true;
        while ( inObject )
        {
            protoObject = _in.readProto();

            switch( protoObject.getIndex() )
            {
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_TEST_ID:
                  // SINGLE INSTANCE OF PRIMITIVE FIELD
                  objTest.setId(protoObject.getInteger().intValue());
                  break;
                case END_OF_OBJECT:
                  inObject = false; 
                  break;
                default:
                  inObject = false; 
                  break;
            }

        }

        protoObject = null;

        for( Enumeration keys = hashtableOfObjectArraysAsVectors.keys() ; keys.hasMoreElements() ; )
        {
            String fieldName = (String)keys.nextElement();
            String className = (String)hashtableOfObjectArrayTypes.get( fieldName ); 
            Vector temporaryVector = (Vector)hashtableOfObjectArraysAsVectors.get( fieldName );
            if ( temporaryVector != null )
            {
            }
        }
        return objTest;

    }

    public void writeTest( net.yura.tools.mobilegen.model.Test obj , ProtoOutputStream out ) throws IOException
    {
         ByteArrayOutputStream baos   = null;
         byte []               buffer = null;
         byte []               length = null;
         byte []               id     = null;
         Vector                v      = null;
         ProtoOutputStream _out = ( out == null ? protoOutputStream : out );
         if ( obj != null )
         {
        }

        _out.write( END_OF_OBJECT , (byte)0 );
        _out.flush();
    }

    public net.yura.tools.mobilegen.model.Number readNumber( ProtoInputStream in ) throws IOException
    {
        Hashtable hashtableOfObjectArraysAsVectors = new Hashtable();
        Hashtable hashtableOfObjectArrayTypes = new Hashtable();
        ByteArrayInputStream bais          = null;
        net.yura.tools.mobilegen.model.Number           objNumber = new net.yura.tools.mobilegen.model.Number();
        ProtoObject          protoObject   = null;

        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );

        boolean inObject = true;
        while ( inObject )
        {
            protoObject = _in.readProto();

            switch( protoObject.getIndex() )
            {
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_NUMBER_NUMBER:
                  // SINGLE INSTANCE OF PRIMITIVE FIELD
                  objNumber.setNumber(protoObject.getString());
                  break;
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_NUMBER_TYPE:
                  // SINGLE INSTANCE OF PRIMITIVE FIELD
                  objNumber.setType(protoObject.getInteger().intValue());
                  break;
                case END_OF_OBJECT:
                  inObject = false; 
                  break;
                default:
                  inObject = false; 
                  break;
            }

        }

        protoObject = null;

        for( Enumeration keys = hashtableOfObjectArraysAsVectors.keys() ; keys.hasMoreElements() ; )
        {
            String fieldName = (String)keys.nextElement();
            String className = (String)hashtableOfObjectArrayTypes.get( fieldName ); 
            Vector temporaryVector = (Vector)hashtableOfObjectArraysAsVectors.get( fieldName );
            if ( temporaryVector != null )
            {
            }
        }
        return objNumber;

    }

    public void writeNumber( net.yura.tools.mobilegen.model.Number obj , ProtoOutputStream out ) throws IOException
    {
         ByteArrayOutputStream baos   = null;
         byte []               buffer = null;
         byte []               length = null;
         byte []               id     = null;
         Vector                v      = null;
         ProtoOutputStream _out = ( out == null ? protoOutputStream : out );
         if ( obj != null )
         {
        }

        _out.write( END_OF_OBJECT , (byte)0 );
        _out.flush();
    }

    public net.yura.tools.mobilegen.model.TestObject readTestObject( ProtoInputStream in ) throws IOException
    {
        Hashtable hashtableOfObjectArraysAsVectors = new Hashtable();
        Hashtable hashtableOfObjectArrayTypes = new Hashtable();
        ByteArrayInputStream bais          = null;
        net.yura.tools.mobilegen.model.TestObject           objTestObject = new net.yura.tools.mobilegen.model.TestObject();
        ProtoObject          protoObject   = null;

        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );

        boolean inObject = true;
        while ( inObject )
        {
            protoObject = _in.readProto();

            switch( protoObject.getIndex() )
            {
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_ID:
                  // SINGLE INSTANCE OF PRIMITIVE FIELD
                  objTestObject.setId(protoObject.getInteger().intValue());
                  break;
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_NAME:
                  // SINGLE INSTANCE OF PRIMITIVE FIELD
                  objTestObject.setName(protoObject.getString());
                  break;
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_AGE:
                  // SINGLE INSTANCE OF PRIMITIVE FIELD
                  objTestObject.setAge(protoObject.getByte().byteValue());
                  break;
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_NUMBERS:
                  // REPEATED INSTANCE OF MESSAGE OBJECT --> VECTOR
                  bais = new ByteArrayInputStream( protoObject.getPayload() );
                  (objTestObject.getNumbers()).addElement( readNumber(new ProtoInputStream(bais)) );
                  bais.close();
                  bais = null;
                  break;
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_BODY:
                  // OBJECT
                  bais = new ByteArrayInputStream( protoObject.getPayload() );
                  objTestObject.setBody( readObject(new ProtoInputStream(bais)) );
                  bais.close();
                  bais = null;
                  break;
                case NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_LEGS:
                  // OBJECT ARRAY
                  hashtableOfObjectArrayTypes.put( "legs" , "java.lang.String[]" );
                  Vector temporaryVector = (Vector)hashtableOfObjectArraysAsVectors.get( "legs" );
                  if ( temporaryVector == null )
                  {
                      temporaryVector = new Vector();
                      hashtableOfObjectArraysAsVectors.put( "legs" , temporaryVector );
                  }
                  bais = new ByteArrayInputStream( protoObject.getPayload() );
                  Object temporaryObject = readObject(new ProtoInputStream(bais) );
                  temporaryVector.addElement( temporaryObject );
                  bais.close();
                  bais = null;
                  break;
                case END_OF_OBJECT:
                  inObject = false; 
                  break;
                default:
                  inObject = false; 
                  break;
            }

        }

        protoObject = null;

        for( Enumeration keys = hashtableOfObjectArraysAsVectors.keys() ; keys.hasMoreElements() ; )
        {
            String fieldName = (String)keys.nextElement();
            String className = (String)hashtableOfObjectArrayTypes.get( fieldName ); 
            Vector temporaryVector = (Vector)hashtableOfObjectArraysAsVectors.get( fieldName );
            if ( temporaryVector != null )
            {
                if ( fieldName.equals( "legs" ) )
                {
                    java.lang.String  [] temporaryObjectArray = new java.lang.String  [ temporaryVector.size() ];
                    temporaryVector.copyInto( temporaryObjectArray );
                    objTestObject.setLegs( temporaryArray );
                }
            }
        }
        return objTestObject;

    }

    public void writeTestObject( net.yura.tools.mobilegen.model.TestObject obj , ProtoOutputStream out ) throws IOException
    {
         ByteArrayOutputStream baos   = null;
         byte []               buffer = null;
         byte []               length = null;
         byte []               id     = null;
         Vector                v      = null;
         ProtoOutputStream _out = ( out == null ? protoOutputStream : out );
         if ( obj != null )
         {
             // FIELD : legs
             // OBJECT ARRAY
             v = new Vector();
             Object [] tmpArray = (Object [])obj.getLegs();
             for( int arrayIndex = 0 ; arrayIndex < tmpArray.length ; arrayIndex++ ) v.add( tmpArray[arrayIndex] );
             v.addAll( (Collection));
             Enumeration e = v.elements();
             for( ; e.hasMoreElements() ; )
             {
                 protoOutputStream.write( NET_YURA_TOOLS_MOBILEGEN_MODEL_TESTOBJECT_LEGS , e.nextElement() );
             } 

        }

        _out.write( END_OF_OBJECT , (byte)0 );
        _out.flush();
    }


}

package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.yura.mobile.io.proto.CodedInputStream;
import net.yura.mobile.io.proto.CodedOutputStream;
import net.yura.mobile.io.proto.WireFormat;

/**
 * @author Yura Mamyrin
 */
public class ProtoUtil {

    protected static final String[] EMPTY = new String[0];
    protected static final String unknown = "unknown ";

    protected static final int VECTOR_ELEMENT = 1; // from proto file

    protected static final int KEY_VALUE = 1; // from proto file

    protected static final int HASHTABLE_KEY = 1; // from proto file
    protected static final int HASHTABLE_VALUE = 2; // from proto file

    public int save(OutputStream out, Object obj) throws IOException {

        int size = computeAnonymousObjectSize(obj);

        //byte[] message = new byte[size];
        CodedOutputStream out2 = CodedOutputStream.newInstance(out);
        encodeAnonymousObject(out2,obj);

        out2.flush();
        //out.write(message);
//Logger.debug("save size "+size);
        return size;
    }

    public Object load(InputStream in,int size) throws IOException {
        CodedInputStream in2 = CodedInputStream.newInstance(in);
        in2.setSizeLimit(size);

        int lim = in2.pushLimit(size);
        Object obj = decodeAnonymousObject(in2);
        in2.popLimit(lim);

        return obj;
    }

    protected Object decodeAnonymousObject(CodedInputStream in2) throws IOException {
        Object obj=null;
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int type = WireFormat.getTagFieldNumber(tag);
//            int wireType = WireFormat.getTagWireType(tag);
    //Logger.debug("read field "+fieldNo );
    //Logger.debug("wire type "+wireType );
            if (obj != null) {
                throw new IOException("more then one field inside Anonymous Object, newFieldNo="+type+" currentObject="+obj );
            }
            switch (type) {
                //case BinUtil.TYPE_NULL: obj = null; break;
                case BinUtil.TYPE_INTEGER: obj = new Integer( in2.readInt32() ); break;
                case BinUtil.TYPE_DOUBLE: obj = new Double(in2.readDouble()); break;
                case BinUtil.TYPE_STRING: obj = in2.readString(); break;
                case BinUtil.TYPE_BOOLEAN: obj = in2.readBool()?Boolean.TRUE:Boolean.FALSE; break;
                case BinUtil.TYPE_BYTE: obj = new Byte((byte)in2.readInt32()); break;
                case BinUtil.TYPE_CHARACTER: obj = new Character( (char)in2.readInt32()); break;
                case BinUtil.TYPE_SHORT: obj = new Short( (short)in2.readInt32()); break;
                case BinUtil.TYPE_LONG: obj = new Long(in2.readInt64()); break;
                case BinUtil.TYPE_FLOAT: obj = new Float(in2.readFloat()); break;
                case BinUtil.TYPE_BYTE_ARRAY: obj = in2.readBytes(); break;
                default:
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    //Logger.debug("object size "+size);
                    obj = decodeObject(in2, type );
                    //Logger.debug("object "+obj);
                    in2.popLimit(lim);
                    break;
            }
        }
        return obj;
    }

    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {
        switch (type) {
            case BinUtil.TYPE_VECTOR: return decodeVector(in2);
            case BinUtil.TYPE_ARRAY:
                Vector v = decodeVector(in2);
                Object[] array = new Object[v.size()];
                v.copyInto(array);
                return array;
            case BinUtil.TYPE_HASHTABLE: return decodeHashtable(in2);
            default: throw new IOException("unknown type "+type);
        }
    }

    protected Vector decodeVector(CodedInputStream in2) throws IOException {
        Vector vector = new Vector();

        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
//            int wireType = WireFormat.getTagWireType(tag);
    //Logger.debug("read field "+fieldNo );
    //Logger.debug("wire type "+wireType );

            if (fieldNo == VECTOR_ELEMENT) {
                int size = in2.readBytesSize();
                int lim = in2.pushLimit(size);
                //Logger.debug("object size "+size);
                Object obj = decodeAnonymousObject(in2);
                vector.addElement(obj);
                in2.popLimit(lim);
            }
            else {
                in2.skipField(tag);
            }

        }

        return vector;
    }

    protected Hashtable decodeHashtable(CodedInputStream in2) throws IOException {
        Hashtable hashtable = new Hashtable();

        while (!in2.isAtEnd()) {
            final int tag = in2.readTag();
            final int fieldNo = WireFormat.getTagFieldNumber(tag);
//            final int wireType = WireFormat.getTagWireType(tag);
    //Logger.debug("read field "+fieldNo );
    //Logger.debug("wire type "+wireType );

            if (fieldNo == KEY_VALUE) {

                final int size = in2.readBytesSize();
                final int lim = in2.pushLimit(size);

                Object key = null;
                Object value = null;
                
                while (!in2.isAtEnd()) {
                    final int tag2 = in2.readTag();
                    final int fieldNo2 = WireFormat.getTagFieldNumber(tag2);
//                    final int wireType2 = WireFormat.getTagWireType(tag2);
            //Logger.debug("read field "+fieldNo2 );
            //Logger.debug("wire type "+wireType2 );

                    switch(fieldNo2) {
                        case HASHTABLE_KEY: {
                            int size2 = in2.readBytesSize();
                            int lim2 = in2.pushLimit(size2);
                            //Logger.debug("object size "+size2);
                            key = decodeAnonymousObject(in2);
                            in2.popLimit(lim2);
                            break;
                        }
                        case HASHTABLE_VALUE: {
                            int size2 = in2.readBytesSize();
                            int lim2 = in2.pushLimit(size2);
                            //Logger.debug("object size "+size2);
                            value = decodeAnonymousObject(in2);
                            in2.popLimit(lim2);
                            break;
                        }
                        default: {
                            in2.skipField(tag2);
                            break;
                        }
                    }

                }

                hashtable.put(key, value);

                in2.popLimit(lim);

            }
            else {
                in2.skipField(tag);
            }
        }

        return hashtable;
    }

    /////////////////////////////////////////////////////////////////////////////

    protected int computeObjectSize(Object obj,int type) {
        switch(type) {
            case BinUtil.TYPE_VECTOR: return computeVectorSize( obj );
            case BinUtil.TYPE_ARRAY: return computeArraySize( (Object[])obj );
            case BinUtil.TYPE_HASHTABLE: return computeHashtableSize( obj );
            default: throw new RuntimeException();
        }
    }

    protected void encodeObject(CodedOutputStream out, Object obj,int type) throws IOException {
        switch(type) {
            case BinUtil.TYPE_VECTOR: encodeVector( out, obj ); break;
            case BinUtil.TYPE_ARRAY: encodeArray( out, (Object[])obj ); break;
            case BinUtil.TYPE_HASHTABLE: encodeHashtable( out, obj ); break;
            default: throw new IOException();
        }
    }

    /**
     * Needed for ProtoFileUtil
     */
    protected int computeByteArraySize(Object obj) {
        return ((byte[])obj).length;
    }
    /**
     * Needed for ProtoFileUtil
     */
    protected void encodeByteArray(CodedOutputStream out,Object obj) throws IOException {
        out.writeRawBytes((byte[])obj);
    }

    public int computeAnonymousObjectSize(Object obj) {
        int type = getObjectTypeEnum(obj);
        //#mdebug debug
        //if (type==BinUtil.TYPE_HASHTABLE) {
        //    System.out.println("[ProtoUtil] Sending object as Hashtable "+obj);
        //}
        //#enddebug
        switch(type) {
            case BinUtil.TYPE_NULL: return 0; // nothing
            case BinUtil.TYPE_INTEGER: return CodedOutputStream.computeInt32Size(type,((Integer)obj).intValue());
            case BinUtil.TYPE_DOUBLE: return CodedOutputStream.computeDoubleSize(type, ((Double)obj).doubleValue() );
            case BinUtil.TYPE_FLOAT: return CodedOutputStream.computeFloatSize(type, ((Float)obj).floatValue());
            case BinUtil.TYPE_STRING: return CodedOutputStream.computeStringSize(type, (String)obj );
            case BinUtil.TYPE_BOOLEAN: return CodedOutputStream.computeBoolSize(type, ((Boolean)obj).booleanValue() );
            case BinUtil.TYPE_BYTE: return CodedOutputStream.computeInt32Size(type,((Byte)obj).byteValue());
            case BinUtil.TYPE_CHARACTER: return CodedOutputStream.computeInt32Size(type,((Character)obj).charValue());
            case BinUtil.TYPE_SHORT: return CodedOutputStream.computeInt32Size(type,((Short)obj).shortValue());
            case BinUtil.TYPE_LONG: return CodedOutputStream.computeInt64Size(type,((Long)obj).longValue());
            case BinUtil.TYPE_BYTE_ARRAY: return CodedOutputStream.computeBytesSize(type, computeByteArraySize(obj) );
            default: return CodedOutputStream.computeBytesSize( type, computeObjectSize(obj,type) );
        }
    }
    protected void encodeAnonymousObject(CodedOutputStream out, Object obj) throws IOException {
        int type = getObjectTypeEnum(obj);
        switch(type) {
            case BinUtil.TYPE_NULL: break; // nothing
            case BinUtil.TYPE_INTEGER: out.writeInt32(type,((Integer)obj).intValue()); break;
            case BinUtil.TYPE_DOUBLE: out.writeDouble(type,((Double)obj).doubleValue()); break;
            case BinUtil.TYPE_FLOAT: out.writeFloat(type, ((Float)obj).floatValue() ); break;
            case BinUtil.TYPE_STRING: out.writeString(type, (String)obj ); break;
            case BinUtil.TYPE_BOOLEAN: out.writeBool(type, ((Boolean)obj).booleanValue() ); break;
            case BinUtil.TYPE_BYTE: out.writeInt32(type, ((Byte)obj).byteValue()); break;
            case BinUtil.TYPE_CHARACTER: out.writeInt32(type,((Character)obj).charValue()); break;
            case BinUtil.TYPE_SHORT: out.writeInt32(type,((Short)obj).shortValue()); break;
            case BinUtil.TYPE_LONG: out.writeInt64(type,((Long)obj).longValue()); break;
            case BinUtil.TYPE_BYTE_ARRAY:
                out.writeBytes(type, computeByteArraySize(obj) );
                encodeByteArray(out, obj);
                break;
            default:
                out.writeBytes(type, computeObjectSize(obj, type) );
                encodeObject(out,obj,type);
                break;
        }
    }

    protected int computeVectorSize(Object list) {
        Vector vector = (Vector)list;
        int size=0;
        for (int c=0;c<vector.size();c++) {
            int s = computeAnonymousObjectSize( vector.elementAt(c) );
            size = size + CodedOutputStream.computeBytesSize(VECTOR_ELEMENT, s);
        }
        return size;
    }

    private int computeArraySize(Object[] vector) {
        int size=0;
        for (int c=0;c<vector.length;c++) {
            int s = computeAnonymousObjectSize( vector[c] );
            size = size + CodedOutputStream.computeBytesSize(VECTOR_ELEMENT, s);
        }
        return size;
    }


    protected int computeHashtableSize(Object map) {
        Hashtable hashtable = (Hashtable)map;

        int totalSize = 0;

        Enumeration enu = hashtable.keys();
        while (enu.hasMoreElements()) {
            Object key = enu.nextElement();
            Object value = hashtable.get(key);

            int keySize = computeAnonymousObjectSize(key);
            int valueSize = computeAnonymousObjectSize(value);
            int size1 = CodedOutputStream.computeBytesSize(HASHTABLE_KEY, keySize);
            int size2 = CodedOutputStream.computeBytesSize(HASHTABLE_VALUE, valueSize);

            int s = CodedOutputStream.computeBytesSize(KEY_VALUE, size1+size2);

            totalSize = totalSize + s;
        }

        return totalSize;

    }

    protected void encodeVector(CodedOutputStream out, Object list) throws IOException {
        Vector vector = (Vector)list;
        for (int c=0;c<vector.size();c++) {
            Object obj = vector.elementAt(c);
            out.writeBytes(VECTOR_ELEMENT, computeAnonymousObjectSize(obj));
            encodeAnonymousObject( out, obj );
        }
    }
    private void encodeArray(CodedOutputStream out, Object[] vector) throws IOException {
        for (int c=0;c<vector.length;c++) {
            Object obj = vector[c];
            out.writeBytes(VECTOR_ELEMENT, computeAnonymousObjectSize(obj));
            encodeAnonymousObject( out, obj );
        }
    }

    protected void encodeHashtable(CodedOutputStream out, Object map) throws IOException {
        Hashtable hashtable = (Hashtable)map;
        
        Enumeration enu = hashtable.keys();
        while (enu.hasMoreElements()) {
            Object key = enu.nextElement();
            Object value = hashtable.get(key);

            int keySize = computeAnonymousObjectSize(key);
            int valueSize = computeAnonymousObjectSize(value);
            int size1 = CodedOutputStream.computeBytesSize(HASHTABLE_KEY, keySize);
            int size2 = CodedOutputStream.computeBytesSize(HASHTABLE_VALUE, valueSize);

            out.writeBytes(KEY_VALUE,size1+size2 );

            out.writeBytes(HASHTABLE_KEY, keySize);
            encodeAnonymousObject( out, key );

            out.writeBytes(HASHTABLE_VALUE, valueSize);
            encodeAnonymousObject( out, value );
        }
    }

    protected int getObjectTypeEnum(Object obj) {

        if (obj instanceof Double) {
            return BinUtil.TYPE_DOUBLE;
        }
        else if (obj instanceof Integer) {
            return BinUtil.TYPE_INTEGER;
        }
        else if (obj instanceof Vector) {
            return BinUtil.TYPE_VECTOR;
        }
        else if (obj instanceof String) {
            return BinUtil.TYPE_STRING;
        }
        else if (obj instanceof Float) {
            return BinUtil.TYPE_FLOAT;
        }
        else if (obj instanceof Short) {
            return BinUtil.TYPE_SHORT;
        }
        else if (obj instanceof Long) {
            return BinUtil.TYPE_LONG;
        }
        else if (obj instanceof Boolean) {
            return BinUtil.TYPE_BOOLEAN;
        }
        else if (obj instanceof Byte) {
            return BinUtil.TYPE_BYTE;
        }
        else if (obj instanceof Object[]) {
            return BinUtil.TYPE_ARRAY;
        }
        else if (obj instanceof Hashtable) {
            return BinUtil.TYPE_HASHTABLE;
        }
        else if (obj instanceof byte[]) {
            return BinUtil.TYPE_BYTE_ARRAY;
        }
        else if (obj instanceof Character) {
            return BinUtil.TYPE_CHARACTER;
        }
        else if (obj == null) {
            return BinUtil.TYPE_NULL;
        }
        else {
            throw new RuntimeException();
        }

    }

    public static boolean hashtableIsMessage(Hashtable hash,String[] required,String[] optional) {
        if (hash.size() < required.length || hash.size() > (required.length + optional.length)) {
            return false;
        }
        int found = 0;
        Enumeration enu = hash.keys();
        while (enu.hasMoreElements()) {
            Object key = enu.nextElement();
            if (canBeFoundIn(key,required)) {
                found ++;
            }
            else if (!canBeFoundIn(key,optional)) {
                return false;
            }
        }
        return (required.length == found);
    }

    private static boolean canBeFoundIn(Object key,Object[] array) {
        for (int c=0;c<array.length;c++) {
            if (array[c].equals(key)) {
                return true;
            }
        }
        return false;
    }

    protected Object getObjetById(Object id,Class clas) {
        throw new RuntimeException();
    }
    protected Object getObjectId(Object object) {
        throw new RuntimeException();
    }
}

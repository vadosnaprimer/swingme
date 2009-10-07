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

    private static final int OBJECT_TYPE = 1; // from proto file
    private static final int OBJECT_VALUE = 2; // from proto file
    
    private static final int DEFAULT_FIELD = 1;

    private static final int VECTOR_ELEMENT = 1;

    private static final int KEY_VALUE = 1;
    private static final int HASHTABLE_KEY = 1;
    private static final int HASHTABLE_VALUE = 2;

    public int save(OutputStream out, Object obj) throws IOException {

        int size = computeAnonymousObjectSize(obj);

        //byte[] message = new byte[size];
        CodedOutputStream out2 = CodedOutputStream.newInstance(out);
        encodeAnonymousObject(out2,obj);

        out2.flush();
        //out.write(message);
System.out.println("save size "+size);
        return size;
    }

    public Object load(InputStream in,int size) throws IOException {
        CodedInputStream in2 = CodedInputStream.newInstance(in);

        int lim = in2.pushLimit(size);
        Object obj = decodeAnonymousObject(in2);
        in2.popLimit(lim);
        return obj;
    }

    private Object decodeAnonymousObject(CodedInputStream in2) throws IOException {

        int type=-1;
        Object obj=null;

        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
    System.out.println("read field "+fieldNo );
    System.out.println("wire type "+wireType );

            if (fieldNo == OBJECT_TYPE) {
                type = in2.readInt32();
                System.out.println("object type "+type);
            }
            else if (fieldNo == OBJECT_VALUE) {
                if (type==-1) {
                    throw new IOException("fuck, fields in wrong order to be able to decode");
                }

                int size = in2.readBytesSize();
                int lim = in2.pushLimit(size);
                System.out.println("object size "+size);
                obj = decodeObject(in2,type);
                System.out.println("object "+obj);
                in2.popLimit(lim);

            }

        }
        return obj;
    }

    private Object decodeObject(CodedInputStream in2,int type) throws IOException {

        switch (type) {
            case BinUtil.TYPE_VECTOR: return decodeVector(in2);
            case BinUtil.TYPE_ARRAY: {
                Vector v = decodeVector(in2);
                Object[] array = new Object[v.size()];
                v.copyInto(array);
                return array;
            }
            case BinUtil.TYPE_HASHTABLE: return decodeHashtable(in2);
        }

        Object simple=null;

        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
    System.out.println("read field "+fieldNo );
    System.out.println("wire type "+wireType );

            if (fieldNo == DEFAULT_FIELD) {
                simple = readSimple(in2,type);
            }
        }
        return simple;

    }

    private Object readSimple(CodedInputStream in2,int type) throws IOException {

        switch (type) {
                case BinUtil.TYPE_INTEGER: return new Integer( in2.readInt32() );
                case BinUtil.TYPE_DOUBLE: return new Double(in2.readDouble());
                case BinUtil.TYPE_STRING: return in2.readString();
                case BinUtil.TYPE_BOOLEAN: return new Boolean(in2.readBool());
                case BinUtil.TYPE_BYTE: return new Byte((byte)in2.readInt32());
                case BinUtil.TYPE_CHARACTER: return new Character( (char)in2.readInt32());
                case BinUtil.TYPE_SHORT: return new Short( (short)in2.readInt32());
                case BinUtil.TYPE_LONG: return new Long(in2.readInt64());
                case BinUtil.TYPE_FLOAT: return new Float(in2.readFloat());
                case BinUtil.TYPE_BYTE_ARRAY: return in2.readBytes();
                case BinUtil.TYPE_NULL: return null; // TODO ???
                default: throw new IOException();
        }
    }

    private Vector decodeVector(CodedInputStream in2) throws IOException {
        Vector vector = new Vector();

        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
    System.out.println("read field "+fieldNo );
    System.out.println("wire type "+wireType );

            if (fieldNo == VECTOR_ELEMENT) {

                int size = in2.readBytesSize();
                int lim = in2.pushLimit(size);
                System.out.println("object size "+size);
                Object obj = decodeAnonymousObject(in2);
                vector.addElement(obj);
                in2.popLimit(lim);

            }

        }

        return vector;
    }

    private Hashtable decodeHashtable(CodedInputStream in2) throws IOException {
        Hashtable hashtable = new Hashtable();

        while (!in2.isAtEnd()) {
            final int tag = in2.readTag();
            final int fieldNo = WireFormat.getTagFieldNumber(tag);
            final int wireType = WireFormat.getTagWireType(tag);
    System.out.println("read field "+fieldNo );
    System.out.println("wire type "+wireType );

            if (fieldNo == KEY_VALUE) {

                final int size = in2.readBytesSize();
                final int lim = in2.pushLimit(size);

                Object key = null;
                Object value = null;
                
                while (!in2.isAtEnd()) {
                    final int tag2 = in2.readTag();
                    final int fieldNo2 = WireFormat.getTagFieldNumber(tag2);
                    final int wireType2 = WireFormat.getTagWireType(tag2);
            System.out.println("read field "+fieldNo2 );
            System.out.println("wire type "+wireType2 );

                    if (fieldNo2 == HASHTABLE_KEY) {

                        int size2 = in2.readBytesSize();
                        int lim2 = in2.pushLimit(size2);
                        System.out.println("object size "+size2);
                        key = decodeAnonymousObject(in2);
                        in2.popLimit(lim2);

                    }
                    else if (fieldNo2 == HASHTABLE_VALUE) {

                        int size2 = in2.readBytesSize();
                        int lim2 = in2.pushLimit(size2);
                        System.out.println("object size "+size2);
                        value = decodeAnonymousObject(in2);
                        in2.popLimit(lim2);
                    }
                }

                hashtable.put(key, value);

                in2.popLimit(lim);

            }

        }

        return hashtable;
    }

    // #########################################################################

    private int computeObjectSize(Object obj) {

        if (obj instanceof Double) {
            return CodedOutputStream.computeDoubleSize(DEFAULT_FIELD, ((Double)obj).doubleValue() );
        }
        else if (obj instanceof Integer) {
            return CodedOutputStream.computeInt32Size(DEFAULT_FIELD,((Integer)obj).intValue());
        }
        else if (obj instanceof String) {
            return CodedOutputStream.computeStringSize(DEFAULT_FIELD, (String)obj );
        }
        else if (obj instanceof Float) {
            return CodedOutputStream.computeFloatSize(DEFAULT_FIELD, ((Float)obj).floatValue());
        }
        else if (obj instanceof Short) {
            return CodedOutputStream.computeInt32Size(DEFAULT_FIELD,((Short)obj).shortValue());
        }
        else if (obj instanceof Long) {
            return CodedOutputStream.computeInt64Size(DEFAULT_FIELD,((Long)obj).longValue());
        }
        else if (obj instanceof Boolean) {
            return CodedOutputStream.computeBoolSize(DEFAULT_FIELD, ((Boolean)obj).booleanValue() );
        }
        else if (obj instanceof Byte) {
            return CodedOutputStream.computeInt32Size(DEFAULT_FIELD,((Byte)obj).byteValue());
        }
        else if (obj instanceof Object[]) {
            return computeArraySize( (Object[])obj );
        }
        else if (obj instanceof Hashtable) {
            return computeHashtableSize( (Hashtable)obj );
        }
        else if (obj instanceof byte[]) {
            return CodedOutputStream.computeBytesSize(DEFAULT_FIELD, ((byte[])obj).length );
        }
        else if (obj instanceof Character) {
            return CodedOutputStream.computeInt32Size(DEFAULT_FIELD,((Character)obj).charValue());
        }
        else if (obj instanceof Vector) {
            return computeVectorSize( (Vector)obj );
        }
        else if (obj == null) {
            return 0; // TODO is this right??
        }
        else {
            throw new RuntimeException();
        }
    }

    private void encodeObject(CodedOutputStream out, Object obj) throws IOException {

        if (obj instanceof Double) {
            out.writeDouble(DEFAULT_FIELD,((Double)obj).doubleValue());
        }
        else if (obj instanceof Integer) {
            out.writeInt32(DEFAULT_FIELD,((Integer)obj).intValue());
        }
        else if (obj instanceof Vector) {
            encodeVector( out, (Vector)obj );
        }
        else if (obj instanceof String) {
            out.writeString(DEFAULT_FIELD, (String)obj );
        }
        else if (obj instanceof Float) {
            out.writeFloat(DEFAULT_FIELD, ((Float)obj).floatValue() );
        }
        else if (obj instanceof Short) {
            out.writeInt32(DEFAULT_FIELD,((Short)obj).shortValue());
        }
        else if (obj instanceof Long) {
            out.writeInt64(DEFAULT_FIELD,((Long)obj).longValue());
        }
        else if (obj instanceof Boolean) {
            out.writeBool(DEFAULT_FIELD, ((Boolean)obj).booleanValue() );
        }
        else if (obj instanceof Byte) {
            out.writeInt32(DEFAULT_FIELD, ((Byte)obj).byteValue());
        }
        else if (obj instanceof Object[]) {
            encodeArray( out, (Object[])obj );
        }
        else if (obj instanceof Hashtable) {
            encodeHashtable( out, (Hashtable)obj );
        }
        else if (obj instanceof byte[]) {
            out.writeBytes(DEFAULT_FIELD, (byte[])obj);
        }
        else if (obj instanceof Character) {
            out.writeInt32(DEFAULT_FIELD,((Character)obj).charValue());
        }
        else if (obj == null) {
            // nothing??
        }
        else {
            throw new IOException();
        }

    }



    private int computeAnonymousObjectSize(Object obj) {

        int size1 = CodedOutputStream.computeInt32Size(OBJECT_TYPE, getObjectTypeEnum(obj) );
        int size2 = CodedOutputStream.computeBytesSize(OBJECT_VALUE, computeObjectSize(obj) );

        return size1+size2;

    }
    private void encodeAnonymousObject(CodedOutputStream out, Object object) throws IOException {

        out.writeInt32(OBJECT_TYPE,  getObjectTypeEnum(object) );
        out.writeBytes(OBJECT_VALUE, computeObjectSize(object) );
        encodeObject(out,object);

    }





    private int computeVectorSize(Vector vector) {
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


    private int computeHashtableSize(Hashtable hashtable) {

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

    private void encodeVector(CodedOutputStream out, Vector vector) throws IOException {
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

    private void encodeHashtable(CodedOutputStream out, Hashtable vector) throws IOException {
        Enumeration enu = vector.keys();
        while (enu.hasMoreElements()) {
            Object key = enu.nextElement();
            Object value = vector.get(key);

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

    public int getObjectTypeEnum(Object obj) {

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

}

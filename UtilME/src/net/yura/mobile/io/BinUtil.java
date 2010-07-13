package net.yura.mobile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.yura.mobile.logging.Logger;

/**
 * @author Lenin
 */
public class BinUtil {

    public static final int TYPE_BYTE_ARRAY = 13;
    public static final int TYPE_HASHTABLE = 12;
    public static final int TYPE_VECTOR = 10;
    public static final int TYPE_ARRAY = 11;
    public static final int TYPE_STRING = 5;
    public static final int TYPE_INTEGER = 1;
    public static final int TYPE_DOUBLE = 2;
    public static final int TYPE_FLOAT = 3;
    public static final int TYPE_BOOLEAN = 4;
    public static final int TYPE_SHORT = 6;
    public static final int TYPE_LONG = 7;
    public static final int TYPE_CHARACTER = 8;
    public static final int TYPE_BYTE = 9;
    public static final int TYPE_NULL = 0;

    public void save(OutputStream out, Object obj) throws IOException {
        DataOutputStream out2 = new DataOutputStream(out);
        writeObject(out2,obj);
        out2.flush();
    }

    public Object load(InputStream in) throws IOException {
        DataInputStream int2 = new DataInputStream(in);
        return readObject(int2);
    }

    protected void writeObject(DataOutputStream out, Object obj) throws IOException {

        if (obj instanceof Double) {
            out.writeInt(TYPE_DOUBLE);
            out.writeDouble(((Double)obj).doubleValue());
        }
        else if (obj instanceof Integer) {
            out.writeInt(TYPE_INTEGER);
            out.writeInt(((Integer)obj).intValue());
        }
        else if (obj instanceof Vector) {
            out.writeInt(TYPE_VECTOR);
            writeVector( out, (Vector)obj );
        }
        else if (obj instanceof String) {
            out.writeInt(TYPE_STRING);
            out.writeUTF( (String)obj );
        }
        else if (obj instanceof Float) {
            out.writeInt(TYPE_FLOAT);
            out.writeFloat(((Float)obj).floatValue());
        }
        else if (obj instanceof Short) {
            out.writeInt(TYPE_SHORT);
            out.writeShort(((Short)obj).shortValue());
        }
        else if (obj instanceof Long) {
            out.writeInt(TYPE_LONG);
            out.writeLong(((Long)obj).longValue());
        }
        else if (obj instanceof Boolean) {
            out.writeInt(TYPE_BOOLEAN);
            out.writeBoolean(((Boolean)obj).booleanValue());
        }
        else if (obj instanceof Byte) {
            out.writeInt(TYPE_BYTE);
            out.writeByte(((Byte)obj).byteValue());
        }
        else if (obj instanceof Object[]) {
            out.writeInt(TYPE_ARRAY);
            writeArray( out, (Object[])obj );
        }
        else if (obj instanceof Hashtable) {
            out.writeInt(TYPE_HASHTABLE);
            writeHashtable( out, (Hashtable)obj );
        }
        else if (obj instanceof byte[]) {
            out.writeInt(TYPE_BYTE_ARRAY);
            writeBytes(out, (byte[])obj);
        }
        else if (obj instanceof Character) {
            out.writeInt(TYPE_CHARACTER);
            out.writeChar(((Character)obj).charValue());
        }
        else if (obj == null) {
            out.writeInt(TYPE_NULL);
        }
        else {
            out.writeInt(-1);
            out.writeInt(0);
            throw new IOException();
        }
    }

    protected void writeBytes(DataOutputStream out, byte[] bytes) throws IOException {

            out.writeInt( bytes.length );
            out.write( bytes );

    }

    protected void writeVector(DataOutputStream out, Vector vector) throws IOException {
        int size = vector.size();
        out.writeInt(size);
        for (int c=0;c<size;c++) {
            writeObject( out, vector.elementAt(c) );
        }
    }

    protected void writeArray(DataOutputStream out, Object[] object) throws IOException {
        out.writeInt(object.length);
        for (int c=0;c<object.length;c++) {
            writeObject( out, object[c] );
        }
    }

    protected void writeHashtable(DataOutputStream out, Hashtable hashtable) throws IOException {
        int size = hashtable.size();
        out.writeInt(size*2);

        Enumeration enu = hashtable.keys();
        while (enu.hasMoreElements()) {
            Object key = enu.nextElement();
            Object obj = hashtable.get(key);
            writeObject( out, key );
            writeObject( out, obj );
        }

    }

    protected Object readObject(DataInputStream in2) throws IOException {

        int type = in2.readInt();

        switch (type) {
            case TYPE_INTEGER: return new Integer(in2.readInt());
            case TYPE_DOUBLE: return new Double(in2.readDouble());
            case TYPE_VECTOR: return readVector(in2);
            case TYPE_STRING: return in2.readUTF();
            case TYPE_BOOLEAN: return in2.readBoolean()?Boolean.TRUE:Boolean.FALSE;
            case TYPE_BYTE: return new Byte(in2.readByte());
            case TYPE_CHARACTER: return new Character(in2.readChar());
            case TYPE_SHORT: return new Short(in2.readShort());
            case TYPE_LONG: return new Long(in2.readLong());
            case TYPE_ARRAY: return readArray(in2);
            case TYPE_HASHTABLE: return readHashtable(in2);
            case TYPE_FLOAT: return new Float(in2.readFloat());
            case TYPE_BYTE_ARRAY: return readBytes(in2);
            case TYPE_NULL: return null;
        }

        int size = in2.readInt();

        return readObject(in2,type,size);

    }

    protected Object readObject(DataInputStream in2,int type,int size) throws IOException {

        //#debug warn
        Logger.warn("unknown object, type: "+type+" length: "+size);
        for (int c=0;c<size;c++) {
            Object obj = readObject(in2);
            //#debug warn
            Logger.warn("unknown object, content: "+obj);
        }
        //return null;
        throw new IOException();
    }

    protected void skipUnknownObjects(DataInputStream in,int num) throws IOException {

        for (int c=0;c<num;c++) {
            Object obj = readObject(in);
            //#debug warn
            Logger.warn("unknown object found: "+obj);
        }

    }

    protected static void checkType(int got,int want) throws IOException {
        if (want != got) {
            throw new IOException("wrong type, expected: "+want+" got: "+got);
        }
    }

    protected Vector readVector(DataInputStream in2) throws IOException {
        int size = in2.readInt();
        Vector vector = new Vector(size);
        for (int c=0;c<size;c++) {
            Object obj = readObject(in2);
            vector.addElement(obj);
        }
        return vector;
    }

    protected Object[] readArray(DataInputStream in2) throws IOException {
        int size = in2.readInt();
        Object[] vector = new Object[size];
        for (int c=0;c<size;c++) {
            Object obj = readObject(in2);
            vector[c] = obj;
        }
        return vector;
    }

    protected byte[] readBytes(DataInputStream in2) throws IOException {

        int size = in2.readInt();
        byte[] bytes = new byte[size];
        in2.readFully(bytes);
        return bytes;
        
    }

    protected Hashtable readHashtable(DataInputStream in2) throws IOException {
        int size = in2.readInt()/2;
        Hashtable vector = new Hashtable(size);
        for (int c=0;c<size;c++) {
            Object key = readObject(in2);
            Object value = readObject(in2);
            vector.put(key, value);
        }
        return vector;
    }


}

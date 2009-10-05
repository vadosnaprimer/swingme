package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.TestObject;
import net.yura.tools.mobilegen.model.Test;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.BinUtil;
import java.io.DataOutputStream;
import java.io.DataInputStream;
/**
 * THIS FILE IS GENERATED, DO NOT EDIT
 */
public class BinAccess extends BinUtil {
    public static final int TYPE_TESTOBJECT=20;
    public static final int TYPE_TEST=21;
    public BinAccess() {
    }
    protected void writeObject(DataOutputStream out, Object object) throws IOException {
        if (object instanceof TestObject) {
            out.writeInt(TYPE_TESTOBJECT);
            saveTestObject(out,(TestObject)object);
        }
        else if (object instanceof Test) {
            out.writeInt(TYPE_TEST);
            saveTest(out,(Test)object);
        }
        else {
            super.writeObject(out, object);
        }
    }
    protected void saveTestObject(DataOutputStream out,TestObject object) throws IOException {
        out.writeInt(8);
        out.writeInt( TYPE_BYTE);
        out.writeByte( object.getAge() );
        writeObject(out, object.getBody() );
        out.writeInt( TYPE_INTEGER);
        out.writeInt( object.getId() );
        writeObject(out, object.getImage() );
        writeObject(out, object.getLegs() );
        writeObject(out, object.getName() );
        writeObject(out, object.getNumbers() );
        writeObject(out, object.getObjects() );
    }
    protected void saveTest(DataOutputStream out,Test object) throws IOException {
        out.writeInt(1);
        out.writeInt( TYPE_INTEGER);
        out.writeInt( object.getId() );
    }
    protected Object readObject(DataInputStream in,int type,int size) throws IOException {
        switch (type) {
            case TYPE_TESTOBJECT: return readTestObject(in,size);
            case TYPE_TEST: return readTest(in,size);
            default: return super.readObject(in,type,size);
        }
    }
    protected TestObject readTestObject(DataInputStream in,int size) throws IOException {
        TestObject object = new TestObject();
        if (size>0) {
            checkType(in.readInt() , TYPE_BYTE);
            object.setAge( in.readByte() );
        }
        if (size>1) {
            object.setBody( (Object)readObject(in) );
        }
        if (size>2) {
            checkType(in.readInt() , TYPE_INTEGER);
            object.setId( in.readInt() );
        }
        if (size>3) {
            object.setImage( (byte[])readObject(in) );
        }
        if (size>4) {
            Object[] objects = (Object[])readObject(in);
            String[] array=null;
            if (objects!=null) {
                array = new String[objects.length];
                System.arraycopy(objects,0,array,0,objects.length);
            }
            object.setLegs(array);
        }
        if (size>5) {
            object.setName( (String)readObject(in) );
        }
        if (size>6) {
            object.setNumbers( (Vector)readObject(in) );
        }
        if (size>7) {
            Object[] objects = (Object[])readObject(in);
            Object[] array=null;
            if (objects!=null) {
                array = new Object[objects.length];
                System.arraycopy(objects,0,array,0,objects.length);
            }
            object.setObjects(array);
        }
        if (size>8) {
            skipUnknownObjects(in,size - 8);
        }
        return object;
    }
    protected Test readTest(DataInputStream in,int size) throws IOException {
        Test object = new Test();
        if (size>0) {
            checkType(in.readInt() , TYPE_INTEGER);
            object.setId( in.readInt() );
        }
        if (size>1) {
            skipUnknownObjects(in,size - 1);
        }
        return object;
    }
}

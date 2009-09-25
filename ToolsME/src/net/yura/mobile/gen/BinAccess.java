package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.Test;
import net.yura.tools.mobilegen.model.TestObject;
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
    public static final int TYPE_TEST=20;
    public static final int TYPE_TESTOBJECT=21;
    public BinAccess() {
    }
    protected void writeObject(DataOutputStream out, Object object) throws IOException {
        if (object instanceof Test) {
            out.writeInt(TYPE_TEST);
            saveTest(out,(Test)object);
        }
        else if (object instanceof TestObject) {
            out.writeInt(TYPE_TESTOBJECT);
            saveTestObject(out,(TestObject)object);
        }
        else {
            super.writeObject(out, object);
        }
    }
    protected void saveTest(DataOutputStream out,Test object) throws IOException {
        out.writeInt(1);
        out.writeInt( TYPE_INTEGER);
        out.writeInt( object.getId() );
    }
    protected void saveTestObject(DataOutputStream out,TestObject object) throws IOException {
        out.writeInt(6);
        out.writeInt( TYPE_BYTE);
        out.writeByte( object.getAge() );
        writeObject(out, object.getBody() );
        out.writeInt( TYPE_INTEGER);
        out.writeInt( object.getId() );
        writeObject(out, object.getLegs() );
        writeObject(out, object.getName() );
        writeObject(out, object.getNumbers() );
    }
    protected Object readObject(DataInputStream in,int type,int size) throws IOException {
        switch (type) {
            case TYPE_TEST: return readTest(in,size);
            case TYPE_TESTOBJECT: return readTestObject(in,size);
            default: return super.readObject(in,type,size);
        }
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
            Object[] objects = (Object[])readObject(in);
            String[] array=null;
            if (objects!=null) {
                array = new String[objects.length];
                System.arraycopy(objects,0,array,0,objects.length);
            }
            object.setLegs(array);
        }
        if (size>4) {
            object.setName( (String)readObject(in) );
        }
        if (size>5) {
            object.setNumbers( (Vector)readObject(in) );
        }
        if (size>6) {
            skipUnknownObjects(in,size - 6);
        }
        return object;
    }
}

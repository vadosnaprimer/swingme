package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.*;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.ProtoFileUtil;
import net.yura.mobile.io.proto.CodedOutputStream;
import net.yura.mobile.io.proto.CodedInputStream;
import net.yura.mobile.io.proto.WireFormat;
/**
* THIS FILE IS GENERATED, DO NOT EDIT
*/
public class ProtoAccess2 extends ProtoAccess2Decode {
    public static final int TYPE_LOGIN=22;
    public static final int TYPE_TEST=10001;
    public static final int TYPE_TEST_OBJECT=10003;
    protected int getObjectTypeEnum(Object obj) {
        if (obj instanceof Hashtable) {
            Hashtable table = (Hashtable)obj;
            if (hashtableIsMessage(table,new String[] {"username","password","intx","intz"},new String[] {"type","tests","image","inty"})) {
                return TYPE_LOGIN;
            }
        }
        if (obj instanceof TestObject) {
            return TYPE_TEST_OBJECT;
        }
        if (obj instanceof Test) {
            return TYPE_TEST;
        }
        return super.getObjectTypeEnum(obj);
    }
    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {
        switch (type) {
            case TYPE_LOGIN: return d22(in2);
            case TYPE_TEST: return d10001(in2);
            case TYPE_TEST_OBJECT: return d10003(in2);
            default: return super.decodeObject(in2, type);
        }
    }
    protected int computeObjectSize(Object obj,int type) {
        switch (type) {
            case TYPE_LOGIN: return c22( (Hashtable)obj );
            case TYPE_TEST: return c10001( (Test)obj );
            case TYPE_TEST_OBJECT: return c10003( (TestObject)obj );
            default: return super.computeObjectSize(obj,type);
        }
    }
    protected void encodeObject(CodedOutputStream out, Object obj,int type) throws IOException {
        switch (type) {
            case TYPE_LOGIN: e22( out, (Hashtable)obj ); break;
            case TYPE_TEST: e10001( out, (Test)obj ); break;
            case TYPE_TEST_OBJECT: e10003( out, (TestObject)obj ); break;
            default: super.encodeObject(out,obj,type); break;
        }
    }
    private int c22(Hashtable object) {
        int size=0;
        String usernameValue = (String)object.get("username");
        size = size + CodedOutputStream.computeStringSize(1, usernameValue );
        String passwordValue = (String)object.get("password");
        size = size + CodedOutputStream.computeStringSize(2, passwordValue );
        Vector typeVector = (Vector)object.get("type");
        if (typeVector!=null) {
            for (int c=0;c<typeVector.size();c++) {
                String typeValue = (String)typeVector.elementAt(c);
                size = size + CodedOutputStream.computeInt32Size(3, getTypeEnum(typeValue) );
            }
        }
        Vector testsVector = (Vector)object.get("tests");
        if (testsVector!=null) {
            for (int c=0;c<testsVector.size();c++) {
                TestObject testsValue = (TestObject)testsVector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(4, c10003( testsValue ));
            }
        }
        Object imageValue = (Object)object.get("image");
        if (imageValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(5, computeByteArraySize(imageValue) );
        }
        Vector intyVector = (Vector)object.get("inty");
        if (intyVector!=null) {
            for (int c=0;c<intyVector.size();c++) {
                Integer intyValue = (Integer)intyVector.elementAt(c);
                size = size + CodedOutputStream.computeInt32Size(6, intyValue.intValue() );
            }
        }
        Integer intxValue = (Integer)object.get("intx");
        size = size + CodedOutputStream.computeInt32Size(7, intxValue.intValue() );
        Boolean intzValue = (Boolean)object.get("intz");
        size = size + CodedOutputStream.computeBoolSize(8, intzValue.booleanValue() );
        return size;
    }
    private int c10003(TestObject object) {
        int size=0;
        int idValue = object.getId();
        size = size + CodedOutputStream.computeInt32Size(1204, idValue );
        String nameValue = object.getName();
        if (nameValue!=null) {
            size = size + CodedOutputStream.computeStringSize(1205, nameValue );
        }
        byte ageValue = object.getAge();
        size = size + CodedOutputStream.computeInt32Size(1206, ageValue );
        String myTypeValue = object.getMyType();
        if (myTypeValue!=null) {
            size = size + CodedOutputStream.computeInt32Size(1207, getTypeEnum(myTypeValue) );
        }
        Object bodyValue = object.getBody();
        if (bodyValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(1208, computeAnonymousObjectSize( bodyValue ));
        }
        String[] legsArray = object.getLegs();
        if (legsArray!=null) {
            for (int c=0;c<legsArray.length;c++) {
                String legsValue = legsArray[c];
                size = size + CodedOutputStream.computeStringSize(1209, legsValue );
            }
        }
        byte[] imageValue = object.getImage();
        if (imageValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(1, computeByteArraySize(imageValue) );
        }
        Hashtable organsValue = object.getOrgans();
        if (organsValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(3, computeHashtableSize( organsValue ));
        }
        boolean isAliveValue = object.getIsAlive();
        size = size + CodedOutputStream.computeBoolSize(4, isAliveValue );
        int headsValue = object.getHeads();
        size = size + CodedOutputStream.computeInt32Size(5, headsValue );
        long last_updatedValue = object.getLastUpdated();
        size = size + CodedOutputStream.computeInt64Size(6, last_updatedValue );
        int thingsValue = object.getThings();
        size = size + CodedOutputStream.computeInt32Size(7, thingsValue );
        Test and_one_insideValue = object.getAndOneInside();
        if (and_one_insideValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(8, c10001( and_one_insideValue ));
        }
        Vector numbersVector = object.getNumbers();
        if (numbersVector!=null) {
            for (int c=0;c<numbersVector.size();c++) {
                Integer numbersValue = (Integer)numbersVector.elementAt(c);
                size = size + CodedOutputStream.computeInt32Size(9, numbersValue );
            }
        }
        Object[] objectsArray = object.getObjects();
        if (objectsArray!=null) {
            for (int c=0;c<objectsArray.length;c++) {
                Object objectsValue = objectsArray[c];
                size = size + CodedOutputStream.computeBytesSize(20, computeAnonymousObjectSize( objectsValue ));
            }
        }
        Test test_by_idValue = object.getTestById();
        if (test_by_idValue!=null) {
            int test_by_idValueId = ((Integer)getObjectId(test_by_idValue)).intValue();
            size = size + CodedOutputStream.computeInt32Size(30, test_by_idValueId );
        }
        Hashtable login_by_idValue = object.getLoginById();
        if (login_by_idValue!=null) {
            int login_by_idValueId = ((Integer)getObjectId(login_by_idValue)).intValue();
            size = size + CodedOutputStream.computeInt32Size(31, login_by_idValueId );
        }
        Object object_by_idValue = object.getObjectById();
        if (object_by_idValue!=null) {
            String object_by_idValueId = (String)getObjectId(object_by_idValue);
            size = size + CodedOutputStream.computeStringSize(32, object_by_idValueId );
        }
        return size;
    }
    private int c10001(Test object) {
        int size=0;
        int idValue = object.getId();
        size = size + CodedOutputStream.computeInt32Size(1201, idValue );
        return size;
    }
    private void e22(CodedOutputStream out, Hashtable object) throws IOException {
        String usernameValue = (String)object.get("username");
        out.writeString(1, usernameValue );
        String passwordValue = (String)object.get("password");
        out.writeString(2, passwordValue );
        Vector typeVector = (Vector)object.get("type");
        if (typeVector!=null) {
            for (int c=0;c<typeVector.size();c++) {
                String typeValue = (String)typeVector.elementAt(c);
                out.writeInt32(3, getTypeEnum(typeValue) );
            }
        }
        Vector testsVector = (Vector)object.get("tests");
        if (testsVector!=null) {
            for (int c=0;c<testsVector.size();c++) {
                TestObject testsValue = (TestObject)testsVector.elementAt(c);
                out.writeBytes(4,c10003( testsValue ));
                e10003( out, testsValue );
            }
        }
        Object imageValue = (Object)object.get("image");
        if (imageValue!=null) {
            out.writeBytes(5, computeByteArraySize(imageValue) );
            encodeByteArray(out,imageValue);
        }
        Vector intyVector = (Vector)object.get("inty");
        if (intyVector!=null) {
            for (int c=0;c<intyVector.size();c++) {
                Integer intyValue = (Integer)intyVector.elementAt(c);
                out.writeInt32(6, intyValue.intValue() );
            }
        }
        Integer intxValue = (Integer)object.get("intx");
        out.writeInt32(7, intxValue.intValue() );
        Boolean intzValue = (Boolean)object.get("intz");
        out.writeBool(8, intzValue.booleanValue() );
    }
    private void e10003(CodedOutputStream out, TestObject object) throws IOException {
        int idValue = object.getId();
        out.writeInt32(1204, idValue );
        String nameValue = object.getName();
        if (nameValue!=null) {
            out.writeString(1205, nameValue );
        }
        byte ageValue = object.getAge();
        out.writeInt32(1206, ageValue );
        String myTypeValue = object.getMyType();
        if (myTypeValue!=null) {
            out.writeInt32(1207, getTypeEnum(myTypeValue) );
        }
        Object bodyValue = object.getBody();
        if (bodyValue!=null) {
            out.writeBytes(1208,computeAnonymousObjectSize( bodyValue ));
            encodeAnonymousObject( out, bodyValue );
        }
        String[] legsArray = object.getLegs();
        if (legsArray!=null) {
            for (int c=0;c<legsArray.length;c++) {
                String legsValue = legsArray[c];
                out.writeString(1209, legsValue );
            }
        }
        byte[] imageValue = object.getImage();
        if (imageValue!=null) {
            out.writeBytes(1, computeByteArraySize(imageValue) );
            encodeByteArray(out,imageValue);
        }
        Hashtable organsValue = object.getOrgans();
        if (organsValue!=null) {
            out.writeBytes(3,computeHashtableSize( organsValue ));
            encodeHashtable( out, organsValue );
        }
        boolean isAliveValue = object.getIsAlive();
        out.writeBool(4, isAliveValue );
        int headsValue = object.getHeads();
        out.writeInt32(5, headsValue );
        long last_updatedValue = object.getLastUpdated();
        out.writeInt64(6, last_updatedValue );
        int thingsValue = object.getThings();
        out.writeInt32(7, thingsValue );
        Test and_one_insideValue = object.getAndOneInside();
        if (and_one_insideValue!=null) {
            out.writeBytes(8,c10001( and_one_insideValue ));
            e10001( out, and_one_insideValue );
        }
        Vector numbersVector = object.getNumbers();
        if (numbersVector!=null) {
            for (int c=0;c<numbersVector.size();c++) {
                Integer numbersValue = (Integer)numbersVector.elementAt(c);
                out.writeInt32(9, numbersValue );
            }
        }
        Object[] objectsArray = object.getObjects();
        if (objectsArray!=null) {
            for (int c=0;c<objectsArray.length;c++) {
                Object objectsValue = objectsArray[c];
                out.writeBytes(20,computeAnonymousObjectSize( objectsValue ));
                encodeAnonymousObject( out, objectsValue );
            }
        }
        Test test_by_idValue = object.getTestById();
        if (test_by_idValue!=null) {
            int test_by_idValueId = ((Integer)getObjectId(test_by_idValue)).intValue();
            out.writeInt32(30, test_by_idValueId );
        }
        Hashtable login_by_idValue = object.getLoginById();
        if (login_by_idValue!=null) {
            int login_by_idValueId = ((Integer)getObjectId(login_by_idValue)).intValue();
            out.writeInt32(31, login_by_idValueId );
        }
        Object object_by_idValue = object.getObjectById();
        if (object_by_idValue!=null) {
            String object_by_idValueId = (String)getObjectId(object_by_idValue);
            out.writeString(32, object_by_idValueId );
        }
    }
    private void e10001(CodedOutputStream out, Test object) throws IOException {
        int idValue = object.getId();
        out.writeInt32(1201, idValue );
    }
}

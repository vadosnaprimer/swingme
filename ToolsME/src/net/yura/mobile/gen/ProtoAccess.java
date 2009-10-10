package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.*;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.ProtoUtil;
import net.yura.mobile.io.proto.CodedOutputStream;
import net.yura.mobile.io.proto.CodedInputStream;
import net.yura.mobile.io.proto.WireFormat;
/**
* THIS FILE IS GENERATED, DO NOT EDIT
*/
public class ProtoAccess extends ProtoUtil {
    public static final int TYPE_BOB=10000;
    public static final int TYPE_TEST=10001;
    public static final int TYPE_LOGIN=22;
    public static final int TYPE_MESSAGE=20;
    public static final int TYPE_TEST_OBJECT=10003;
    public ProtoAccess() { }
    private int getTypeEnum(String enu) {
        if ("BOBA".equals(enu)) return 1;
        if ("FRED".equals(enu)) return 2;
        if ("LALA".equals(enu)) return 3;
        return -1;
    }
    private String getTypeString(int i) {
        switch (i) {
            case 1: return "BOBA";
            case 2: return "FRED";
            case 3: return "LALA";
            default: return "unknown "+i;
        }
    }
    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {
        switch (type) {
            case TYPE_BOB: return decodeBob(in2);
            case TYPE_TEST: return decodeTest(in2);
            case TYPE_LOGIN: return decodeLogin(in2);
            case TYPE_MESSAGE: return decodeMessage(in2);
            case TYPE_TEST_OBJECT: return decodeTestObject(in2);
            default: return super.decodeObject(in2, type);
        }
    }
    protected int computeObjectSize(Object obj,int type) {
        switch (type) {
            case TYPE_BOB: return computeBobSize( (Hashtable)obj );
            case TYPE_TEST: return computeTestSize( (Test)obj );
            case TYPE_LOGIN: return computeLoginSize( (Hashtable)obj );
            case TYPE_MESSAGE: return computeMessageSize( (Hashtable)obj );
            case TYPE_TEST_OBJECT: return computeTestObjectSize( (TestObject)obj );
            default: return super.computeObjectSize(obj,type);
        }
    }
    protected void encodeObject(CodedOutputStream out, Object obj,int type) throws IOException {
        switch (type) {
            case TYPE_BOB: encodeBob( out, (Hashtable)obj ); break;
            case TYPE_TEST: encodeTest( out, (Test)obj ); break;
            case TYPE_LOGIN: encodeLogin( out, (Hashtable)obj ); break;
            case TYPE_MESSAGE: encodeMessage( out, (Hashtable)obj ); break;
            case TYPE_TEST_OBJECT: encodeTestObject( out, (TestObject)obj ); break;
            default: super.encodeObject(out,obj,type); break;
        }
    }
    protected int getObjectTypeEnum(Object obj) {
        if (obj instanceof Hashtable) {
            Hashtable table = (Hashtable)obj;
            if (table.size() == 3 && table.get("vec1")!=null && table.get("vec2")!=null && table.get("vec3")!=null) {
                return TYPE_BOB;
            }
            if (table.size() == 5 && table.get("username")!=null && table.get("password")!=null && table.get("type")!=null && table.get("tests")!=null && table.get("image")!=null) {
                return TYPE_LOGIN;
            }
            if (table.size() == 1 && table.get("body")!=null) {
                return TYPE_MESSAGE;
            }
        }
        if (obj instanceof Test) {
            return TYPE_TEST;
        }
        if (obj instanceof TestObject) {
            return TYPE_TEST_OBJECT;
        }
        return super.getObjectTypeEnum(obj);
    }
    private int computeMessageSize(Hashtable object) {
        int size=0;
        Object bodyValue = (Object)object.get("body");
        if (bodyValue!=null) {
            int s = computeAnonymousObjectSize( bodyValue );
            size = size + CodedOutputStream.computeBytesSize(1, s);
        }
        return size;
    }
    private int computeLoginSize(Hashtable object) {
        int size=0;
        String usernameValue = (String)object.get("username");
        size = size + CodedOutputStream.computeStringSize(1, usernameValue );
        String passwordValue = (String)object.get("password");
        size = size + CodedOutputStream.computeStringSize(2, passwordValue );
        String typeValue = (String)object.get("type");
        if (typeValue!=null) {
            size = size + CodedOutputStream.computeInt32Size(3, getTypeEnum(typeValue) );
        }
        Vector testsVector = (Vector)object.get("tests");
        if (testsVector!=null) {
            for (int c=0;c<testsVector.size();c++) {
                TestObject testsValue = (TestObject)testsVector.elementAt(c);
                int s = computeTestObjectSize( testsValue );
                size = size + CodedOutputStream.computeBytesSize(4, s);
            }
        }
        byte[] imageValue = (byte[])object.get("image");
        if (imageValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(5, imageValue );
        }
        return size;
    }
    private int computeBobSize(Hashtable object) {
        int size=0;
        Vector vec1Vector = (Vector)object.get("vec1");
        if (vec1Vector!=null) {
            for (int c=0;c<vec1Vector.size();c++) {
                Vector vec1Value = (Vector)vec1Vector.elementAt(c);
                int s = computeVectorSize( vec1Value );
                size = size + CodedOutputStream.computeBytesSize(1, s);
            }
        }
        Vector vec2Vector = (Vector)object.get("vec2");
        if (vec2Vector!=null) {
            for (int c=0;c<vec2Vector.size();c++) {
                Vector vec2Value = (Vector)vec2Vector.elementAt(c);
                int s = computeVectorSize( vec2Value );
                size = size + CodedOutputStream.computeBytesSize(2, s);
            }
        }
        Vector vec3Vector = (Vector)object.get("vec3");
        if (vec3Vector!=null) {
            for (int c=0;c<vec3Vector.size();c++) {
                Vector vec3Value = (Vector)vec3Vector.elementAt(c);
                int s = computeVectorSize( vec3Value );
                size = size + CodedOutputStream.computeBytesSize(3, s);
            }
        }
        return size;
    }
    private int computeTestSize(Test object) {
        int size=0;
        int idValue = object.getId();
        size = size + CodedOutputStream.computeInt32Size(1201, idValue );
        return size;
    }
    private int computeTestObjectSize(TestObject object) {
        int size=0;
        int idValue = object.getId();
        size = size + CodedOutputStream.computeInt32Size(1204, idValue );
        String nameValue = object.getName();
        size = size + CodedOutputStream.computeStringSize(1205, nameValue );
        byte ageValue = object.getAge();
        size = size + CodedOutputStream.computeInt32Size(1206, ageValue );
        String myTypeValue = object.getMyType();
        size = size + CodedOutputStream.computeInt32Size(1207, getTypeEnum(myTypeValue) );
        Object bodyValue = object.getBody();
        if (bodyValue!=null) {
            int s = computeAnonymousObjectSize( bodyValue );
            size = size + CodedOutputStream.computeBytesSize(1208, s);
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
            size = size + CodedOutputStream.computeBytesSize(1, imageValue );
        }
        return size;
    }
    private void encodeMessage(CodedOutputStream out, Hashtable object) throws IOException {
        Object bodyValue = (Object)object.get("body");
        if (bodyValue!=null) {
            int s = computeAnonymousObjectSize( bodyValue );
            out.writeBytes(1,s);
            encodeAnonymousObject( out, bodyValue );
        }
    }
    private void encodeLogin(CodedOutputStream out, Hashtable object) throws IOException {
        String usernameValue = (String)object.get("username");
        out.writeString(1, usernameValue );
        String passwordValue = (String)object.get("password");
        out.writeString(2, passwordValue );
        String typeValue = (String)object.get("type");
        if (typeValue!=null) {
            out.writeInt32(3, getTypeEnum(typeValue) );
        }
        Vector testsVector = (Vector)object.get("tests");
        if (testsVector!=null) {
            for (int c=0;c<testsVector.size();c++) {
                TestObject testsValue = (TestObject)testsVector.elementAt(c);
                int s = computeTestObjectSize( testsValue );
                out.writeBytes(4,s);
                encodeTestObject( out, testsValue );
            }
        }
        byte[] imageValue = (byte[])object.get("image");
        if (imageValue!=null) {
            out.writeBytes(5, imageValue );
        }
    }
    private void encodeBob(CodedOutputStream out, Hashtable object) throws IOException {
        Vector vec1Vector = (Vector)object.get("vec1");
        if (vec1Vector!=null) {
            for (int c=0;c<vec1Vector.size();c++) {
                Vector vec1Value = (Vector)vec1Vector.elementAt(c);
                int s = computeVectorSize( vec1Value );
                out.writeBytes(1,s);
                encodeVector( out, vec1Value );
            }
        }
        Vector vec2Vector = (Vector)object.get("vec2");
        if (vec2Vector!=null) {
            for (int c=0;c<vec2Vector.size();c++) {
                Vector vec2Value = (Vector)vec2Vector.elementAt(c);
                int s = computeVectorSize( vec2Value );
                out.writeBytes(2,s);
                encodeVector( out, vec2Value );
            }
        }
        Vector vec3Vector = (Vector)object.get("vec3");
        if (vec3Vector!=null) {
            for (int c=0;c<vec3Vector.size();c++) {
                Vector vec3Value = (Vector)vec3Vector.elementAt(c);
                int s = computeVectorSize( vec3Value );
                out.writeBytes(3,s);
                encodeVector( out, vec3Value );
            }
        }
    }
    private void encodeTest(CodedOutputStream out, Test object) throws IOException {
        int idValue = object.getId();
        out.writeInt32(1201, idValue );
    }
    private void encodeTestObject(CodedOutputStream out, TestObject object) throws IOException {
        int idValue = object.getId();
        out.writeInt32(1204, idValue );
        String nameValue = object.getName();
        out.writeString(1205, nameValue );
        byte ageValue = object.getAge();
        out.writeInt32(1206, ageValue );
        String myTypeValue = object.getMyType();
        out.writeInt32(1207, getTypeEnum(myTypeValue) );
        Object bodyValue = object.getBody();
        if (bodyValue!=null) {
            int s = computeAnonymousObjectSize( bodyValue );
            out.writeBytes(1208,s);
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
            out.writeBytes(1, imageValue );
        }
    }
    private Hashtable decodeMessage(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setBody( decodeObject(in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                default: // TODO skip unknown fields
            }
        }
        return object;
    }
    private Hashtable decodeLogin(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1: {
                    object.put("username", in2.readString() );
                    break;
                }
                case 2: {
                    object.put("password", in2.readString() );
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setType( decodeType(in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                case 4: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setTests( decodeTestObject(in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                case 5: {
                    object.put("image", in2.readBytes() );
                    break;
                }
                default: // TODO skip unknown fields
            }
        }
        return object;
    }
    private Hashtable decodeBob(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setVec1( decodeVector(in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                case 2: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setVec2( decodeVector(in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setVec3( decodeVector(in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                default: // TODO skip unknown fields
            }
        }
        return object;
    }
    private Test decodeTest(CodedInputStream in2) throws IOException {
        Test object = new Test();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1201: {
                    object.setId( (int)in2.readInt32() );
                    break;
                }
                default: // TODO skip unknown fields
            }
        }
        return object;
    }
    private TestObject decodeTestObject(CodedInputStream in2) throws IOException {
        TestObject object = new TestObject();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1204: {
                    object.setId( (int)in2.readInt32() );
                    break;
                }
                case 1205: {
                    object.setName( (String)in2.readString() );
                    break;
                }
                case 1206: {
                    object.setAge( (byte)in2.readInt32() );
                    break;
                }
                case 1207: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setMyType( decodeType(in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                case 1208: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setBody( decodeAnonymousObject(in2);
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
                case 1209: {
                    object.setLegs( (String[])in2.readString() );
                    break;
                }
                case 1: {
                    object.setImage( (byte[])in2.readBytes() );
                    break;
                }
                default: // TODO skip unknown fields
            }
        }
        return object;
    }
}

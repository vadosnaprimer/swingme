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
            if (table.size() == 3 && table.get("vec1")!=null && table.get("vec2")!=null && table.get("vec3")!=null            ) {
                return TYPE_BOB;
            }
            if (table.size() == 4 && table.get("username")!=null && table.get("password")!=null && table.get("type")!=null && table.get("tests")!=null            ) {
                return TYPE_LOGIN;
            }
            if (table.size() == 1 && table.get("body")!=null            ) {
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
        return size;
    }
    private int computeLoginSize(Hashtable object) {
        int size=0;
        return size;
    }
    private int computeBobSize(Hashtable object) {
        int size=0;
        return size;
    }
    private int computeTestSize(Test object) {
        int size=0;
        size = size + CodedOutputStream.computeInt32Size(1201, object.getId() );
        return size;
    }
    private int computeTestObjectSize(TestObject object) {
        int size=0;
        size = size + CodedOutputStream.computeInt32Size(1204, object.getId() );
        size = size + CodedOutputStream.computeStringSize(1205, object.getName() );
        size = size + CodedOutputStream.computeInt32Size(1206, object.getAge() );
        if (object.getBody()!=null) {
            int s = computeObjectSize(object.getBody() );
            size = size + CodedOutputStream.computeBytesSize(1208, s);
        }
        String[] array = object.getLegs();
        for (int c=0;c<array.length;c++) {
            String obj = array[c];
            int s = computeString[]Size(object.getLegs() );
            size = size + CodedOutputStream.computeBytesSize(1209, s);
        }
        return size;
    }
    private void encodeMessage(CodedOutputStream out, Hashtable object) throws IOException {
    }
    private void encodeLogin(CodedOutputStream out, Hashtable object) throws IOException {
    }
    private void encodeBob(CodedOutputStream out, Hashtable object) throws IOException {
    }
    private void encodeTest(CodedOutputStream out, Test object) throws IOException {
        out.writeInt32(1201, object.getId() );
    }
    private void encodeTestObject(CodedOutputStream out, TestObject object) throws IOException {
        out.writeInt32(1204, object.getId() );
        out.writeString(1205, object.getName() );
        out.writeInt32(1206, object.getAge() );
        if (object.getBody()!=null) {
            writeObject( out, 1208, object.getBody() );
        }
        String[] array = object.getLegs();
        for (int c=0;c<array.length;c++) {
            String obj = array[c];
            writeString[]( out, 1209, object.getLegs() );
        }
    }
    private Hashtable decodeMessage(CodedInputStream in2) {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1: {
                    break;
                }
            }
        }
        return object;
    }
    private Hashtable decodeLogin(CodedInputStream in2) {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1: {
                    break;
                }
                case 2: {
                    break;
                }
                case 3: {
                    break;
                }
                case 4: {
                    break;
                }
            }
        }
        return object;
    }
    private Hashtable decodeBob(CodedInputStream in2) {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1: {
                    break;
                }
                case 2: {
                    break;
                }
                case 3: {
                    break;
                }
            }
        }
        return object;
    }
    private Test decodeTest(CodedInputStream in2) {
        Test object = new Test();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1201: {
                    object.setId( in2.readInt32() );
                    break;
                }
            }
        }
        return object;
    }
    private TestObject decodeTestObject(CodedInputStream in2) {
        TestObject object = new TestObject();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            int wireType = WireFormat.getTagWireType(tag);
            switch(fieldNo) {
                case 1204: {
                    object.setId( in2.readInt32() );
                    break;
                }
                case 1205: {
                    object.setName( in2.readString() );
                    break;
                }
                case 1206: {
                    object.setAge( in2.readInt32() );
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
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    object.setLegs( decodeString[](in2) );
                    vector.addElement(obj);
                    in2.popLimit(lim);
                    break;
                }
            }
        }
        return object;
    }
}

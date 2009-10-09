package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.*;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.ProtoUtil;
import net.yura.mobile.io.proto.CodedOutputStream;
import net.yura.mobile.io.proto.CodedInputStream;
/**
 * THIS FILE IS GENERATED, DO NOT EDIT
 */
public class ProtoAccess extends ProtoUtil {
    public static final int TYPE_BOB=10000;
    public static final int TYPE_TEST=10001;
    public static final int TYPE_LOGIN=22;
    public static final int TYPE_MESSAGE=20;
    public static final int TYPE_TEST_OBJECT=10003;
    public ProtoAccess() {
    }
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
            if (table.size() == 4 && table.get("username")!=null && table.get("password")!=null && table.get("type")!=null && table.get("tests")!=null) {
                return TYPE_LOGIN;
            }
            if (table.size() == 2 && table.get("type")!=null && table.get("body")!=null) {
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
}

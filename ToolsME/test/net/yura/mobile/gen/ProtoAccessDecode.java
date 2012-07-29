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
public class ProtoAccessDecode extends ProtoAccessEnum {
    protected Hashtable decodeLogin(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        Vector typeVector = new Vector();
        Vector testsVector = new Vector();
        Vector intyVector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    String value = in2.readString();
                    object.put("username",value);
                    break;
                }
                case 2: {
                    String value = in2.readString();
                    object.put("password",value);
                    break;
                }
                case 3: {
                    String value = getTypeString( in2.readInt32() );
                    typeVector.addElement( value );
                    break;
                }
                case 4: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    TestObject value = decodeTestObject(in2);
                    in2.popLimit(lim);
                    testsVector.addElement( value );
                    break;
                }
                case 5: {
                    byte[] value = in2.readBytes();
                    object.put("image",value);
                    break;
                }
                case 6: {
                    Integer value = new Integer(in2.readInt32() );
                    intyVector.addElement( value );
                    break;
                }
                case 7: {
                    Integer value = new Integer(in2.readInt32() );
                    object.put("intx",value);
                    break;
                }
                case 8: {
                    Boolean value = in2.readBool()?Boolean.TRUE:Boolean.FALSE;
                    object.put("intz",value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        object.put("type",typeVector);
        object.put("tests",testsVector);
        object.put("inty",intyVector);
        return object;
    }
    protected TestObject decodeTestObject(CodedInputStream in2) throws IOException {
        TestObject object = new TestObject();
        Vector legsVector = new Vector();
        Vector numbersVector = new Vector();
        Vector objectsVector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1204: {
                    int value = in2.readInt32();
                    object.setId(value);
                    break;
                }
                case 1205: {
                    String value = in2.readString();
                    object.setName(value);
                    break;
                }
                case 1206: {
                    byte value = (byte)in2.readInt32();
                    object.setAge(value);
                    break;
                }
                case 1207: {
                    String value = getTypeString( in2.readInt32() );
                    object.setMyType(value);
                    break;
                }
                case 1208: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Object value = decodeAnonymousObject(in2);
                    in2.popLimit(lim);
                    object.setBody(value);
                    break;
                }
                case 1209: {
                    String value = in2.readString();
                    legsVector.addElement( value );
                    break;
                }
                case 1: {
                    byte[] value = in2.readBytes();
                    object.setImage(value);
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeHashtable(in2);
                    in2.popLimit(lim);
                    object.setOrgans(value);
                    break;
                }
                case 4: {
                    boolean value = in2.readBool();
                    object.setIsAlive(value);
                    break;
                }
                case 5: {
                    int value = in2.readInt32();
                    object.setHeads(value);
                    break;
                }
                case 6: {
                    long value = in2.readInt64();
                    object.setLastUpdated(value);
                    break;
                }
                case 7: {
                    int value = in2.readInt32();
                    object.setThings(value);
                    break;
                }
                case 8: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Test value = decodeTest(in2);
                    in2.popLimit(lim);
                    object.setAndOneInside(value);
                    break;
                }
                case 9: {
                    Integer value = new Integer(in2.readInt32() );
                    numbersVector.addElement( value );
                    break;
                }
                case 20: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Object value = decodeAnonymousObject(in2);
                    in2.popLimit(lim);
                    objectsVector.addElement( value );
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        String[] legsArray = new String[legsVector.size()];
        legsVector.copyInto(legsArray);
        object.setLegs(legsArray);
        object.setNumbers(numbersVector);
        Object[] objectsArray = new Object[objectsVector.size()];
        objectsVector.copyInto(objectsArray);
        object.setObjects(objectsArray);
        return object;
    }
    protected Test decodeTest(CodedInputStream in2) throws IOException {
        Test object = new Test();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1201: {
                    int value = in2.readInt32();
                    object.setId(value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        return object;
    }
}

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
    public static final int TYPE_CLIENT_LOGIN_SUCCESS=23;
    public static final int TYPE_MESSAGE=20;
    public static final int TYPE_TEST_OBJECT=10003;
    public ProtoAccess() { }
    public static int getTypeEnum(String enu) {
        if ("BOBA".equals(enu)) return 1;
        if ("FRED".equals(enu)) return 2;
        if ("LALA".equals(enu)) return 3;
        return -1;
    }
    public static String getTypeString(int i) {
        switch (i) {
            case 1: return "BOBA";
            case 2: return "FRED";
            case 3: return "LALA";
            default: return "unknown "+i;
        }
    }
    public static int getOtherThingsTypeEnum(String enu) {
        if ("thing".equals(enu)) return 1;
        if ("stuff".equals(enu)) return 0;
        return -1;
    }
    public static String getOtherThingsTypeString(int i) {
        switch (i) {
            case 1: return "thing";
            case 0: return "stuff";
            default: return "unknown "+i;
        }
    }
    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {
        switch (type) {
            case TYPE_BOB: return decodeBob(in2);
            case TYPE_TEST: return decodeTest(in2);
            case TYPE_LOGIN: return decodeLogin(in2);
            case TYPE_CLIENT_LOGIN_SUCCESS: return decodeClientLoginSuccess(in2);
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
            case TYPE_CLIENT_LOGIN_SUCCESS: return computeClientLoginSuccessSize( (Hashtable)obj );
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
            case TYPE_CLIENT_LOGIN_SUCCESS: encodeClientLoginSuccess( out, (Hashtable)obj ); break;
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
            if (table.size() >= 7 && table.size() <= 8 && table.get("username")!=null && table.get("password")!=null && table.get("type")!=null && table.get("tests")!=null && table.get("inty")!=null && table.get("intx")!=null && table.get("intz")!=null) {
                return TYPE_LOGIN;
            }
            if (table.size() == 6 && table.get("sessionId")!=null && table.get("albums")!=null && table.get("login")!=null && table.get("login2")!=null && table.get("newPeople")!=null && table.get("newMessages")!=null) {
                return TYPE_CLIENT_LOGIN_SUCCESS;
            }
            if (table.size() == 1 && table.get("body")!=null) {
                return TYPE_MESSAGE;
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
    private int computeClientLoginSuccessSize(Hashtable object) {
        int size=0;
        String sessionIdValue = (String)object.get("sessionId");
        size = size + CodedOutputStream.computeStringSize(1, sessionIdValue );
        Vector albumsVector = (Vector)object.get("albums");
        if (albumsVector!=null) {
            for (int c=0;c<albumsVector.size();c++) {
                Hashtable albumsValue = (Hashtable)albumsVector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(3, computeAlbumSize( albumsValue ));
            }
        }
        Hashtable loginValue = (Hashtable)object.get("login");
        size = size + CodedOutputStream.computeBytesSize(6, computeLoginSize( loginValue ));
        Hashtable login2Value = (Hashtable)object.get("login2");
        size = size + CodedOutputStream.computeBytesSize(7, computeLoginSize( login2Value ));
        Integer newPeopleValue = (Integer)object.get("newPeople");
        size = size + CodedOutputStream.computeInt32Size(4, newPeopleValue.intValue() );
        Integer newMessagesValue = (Integer)object.get("newMessages");
        size = size + CodedOutputStream.computeInt32Size(5, newMessagesValue.intValue() );
        return size;
    }
    private int computeAlbumSize(Hashtable object) {
        int size=0;
        String someStringValue = (String)object.get("someString");
        size = size + CodedOutputStream.computeStringSize(1, someStringValue );
        return size;
    }
    private int computeMessageSize(Hashtable object) {
        int size=0;
        Object bodyValue = (Object)object.get("body");
        size = size + CodedOutputStream.computeBytesSize(1, computeAnonymousObjectSize( bodyValue ));
        return size;
    }
    private int computeLoginSize(Hashtable object) {
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
                size = size + CodedOutputStream.computeBytesSize(4, computeTestObjectSize( testsValue ));
            }
        }
        byte[] imageValue = (byte[])object.get("image");
        if (imageValue!=null) {
            size = size + CodedOutputStream.computeBytesSize(5, imageValue );
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
    private int computeBobSize(Hashtable object) {
        int size=0;
        Vector vec1Vector = (Vector)object.get("vec1");
        if (vec1Vector!=null) {
            for (int c=0;c<vec1Vector.size();c++) {
                Vector vec1Value = (Vector)vec1Vector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(1, computeVectorSize( vec1Value ));
            }
        }
        Vector vec2Vector = (Vector)object.get("vec2");
        if (vec2Vector!=null) {
            for (int c=0;c<vec2Vector.size();c++) {
                Vector vec2Value = (Vector)vec2Vector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(2, computeVectorSize( vec2Value ));
            }
        }
        Vector vec3Vector = (Vector)object.get("vec3");
        if (vec3Vector!=null) {
            for (int c=0;c<vec3Vector.size();c++) {
                Vector vec3Value = (Vector)vec3Vector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(3, computeVectorSize( vec3Value ));
            }
        }
        return size;
    }
    private int computeTestObjectSize(TestObject object) {
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
            size = size + CodedOutputStream.computeBytesSize(1, imageValue );
        }
        Vector armsVector = object.getArms();
        if (armsVector!=null) {
            for (int c=0;c<armsVector.size();c++) {
                Hashtable armsValue = (Hashtable)armsVector.elementAt(c);
                size = size + CodedOutputStream.computeBytesSize(2, computeClientLoginSuccessSize( armsValue ));
            }
        }
        Hashtable organsValue = object.getOrgans();
        size = size + CodedOutputStream.computeBytesSize(3, computeHashtableSize( organsValue ));
        boolean isAliveValue = object.getIsAlive();
        size = size + CodedOutputStream.computeBoolSize(4, isAliveValue );
        int headsValue = object.getHeads();
        size = size + CodedOutputStream.computeInt32Size(5, headsValue );
        long lastUpdatedValue = object.getLastUpdated();
        size = size + CodedOutputStream.computeInt64Size(6, lastUpdatedValue );
        int thingsValue = object.getThings();
        size = size + CodedOutputStream.computeInt32Size(7, thingsValue );
        return size;
    }
    private int computeTestSize(Test object) {
        int size=0;
        int idValue = object.getId();
        size = size + CodedOutputStream.computeInt32Size(1201, idValue );
        return size;
    }
    private void encodeClientLoginSuccess(CodedOutputStream out, Hashtable object) throws IOException {
        String sessionIdValue = (String)object.get("sessionId");
        out.writeString(1, sessionIdValue );
        Vector albumsVector = (Vector)object.get("albums");
        if (albumsVector!=null) {
            for (int c=0;c<albumsVector.size();c++) {
                Hashtable albumsValue = (Hashtable)albumsVector.elementAt(c);
                out.writeBytes(3,computeAlbumSize( albumsValue ));
                encodeAlbum( out, albumsValue );
            }
        }
        Hashtable loginValue = (Hashtable)object.get("login");
        out.writeBytes(6,computeLoginSize( loginValue ));
        encodeLogin( out, loginValue );
        Hashtable login2Value = (Hashtable)object.get("login2");
        out.writeBytes(7,computeLoginSize( login2Value ));
        encodeLogin( out, login2Value );
        Integer newPeopleValue = (Integer)object.get("newPeople");
        out.writeInt32(4, newPeopleValue.intValue() );
        Integer newMessagesValue = (Integer)object.get("newMessages");
        out.writeInt32(5, newMessagesValue.intValue() );
    }
    private void encodeAlbum(CodedOutputStream out, Hashtable object) throws IOException {
        String someStringValue = (String)object.get("someString");
        out.writeString(1, someStringValue );
    }
    private void encodeMessage(CodedOutputStream out, Hashtable object) throws IOException {
        Object bodyValue = (Object)object.get("body");
        out.writeBytes(1,computeAnonymousObjectSize( bodyValue ));
        encodeAnonymousObject( out, bodyValue );
    }
    private void encodeLogin(CodedOutputStream out, Hashtable object) throws IOException {
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
                out.writeBytes(4,computeTestObjectSize( testsValue ));
                encodeTestObject( out, testsValue );
            }
        }
        byte[] imageValue = (byte[])object.get("image");
        if (imageValue!=null) {
            out.writeBytes(5, imageValue );
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
    private void encodeBob(CodedOutputStream out, Hashtable object) throws IOException {
        Vector vec1Vector = (Vector)object.get("vec1");
        if (vec1Vector!=null) {
            for (int c=0;c<vec1Vector.size();c++) {
                Vector vec1Value = (Vector)vec1Vector.elementAt(c);
                out.writeBytes(1,computeVectorSize( vec1Value ));
                encodeVector( out, vec1Value );
            }
        }
        Vector vec2Vector = (Vector)object.get("vec2");
        if (vec2Vector!=null) {
            for (int c=0;c<vec2Vector.size();c++) {
                Vector vec2Value = (Vector)vec2Vector.elementAt(c);
                out.writeBytes(2,computeVectorSize( vec2Value ));
                encodeVector( out, vec2Value );
            }
        }
        Vector vec3Vector = (Vector)object.get("vec3");
        if (vec3Vector!=null) {
            for (int c=0;c<vec3Vector.size();c++) {
                Vector vec3Value = (Vector)vec3Vector.elementAt(c);
                out.writeBytes(3,computeVectorSize( vec3Value ));
                encodeVector( out, vec3Value );
            }
        }
    }
    private void encodeTestObject(CodedOutputStream out, TestObject object) throws IOException {
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
            out.writeBytes(1, imageValue );
        }
        Vector armsVector = object.getArms();
        if (armsVector!=null) {
            for (int c=0;c<armsVector.size();c++) {
                Hashtable armsValue = (Hashtable)armsVector.elementAt(c);
                out.writeBytes(2,computeClientLoginSuccessSize( armsValue ));
                encodeClientLoginSuccess( out, armsValue );
            }
        }
        Hashtable organsValue = object.getOrgans();
        out.writeBytes(3,computeHashtableSize( organsValue ));
        encodeHashtable( out, organsValue );
        boolean isAliveValue = object.getIsAlive();
        out.writeBool(4, isAliveValue );
        int headsValue = object.getHeads();
        out.writeInt32(5, headsValue );
        long lastUpdatedValue = object.getLastUpdated();
        out.writeInt64(6, lastUpdatedValue );
        int thingsValue = object.getThings();
        out.writeInt32(7, thingsValue );
    }
    private void encodeTest(CodedOutputStream out, Test object) throws IOException {
        int idValue = object.getId();
        out.writeInt32(1201, idValue );
    }
    private Hashtable decodeClientLoginSuccess(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        Vector albumsVector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    String value = in2.readString();
                    object.put("sessionId",value);
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeAlbum(in2);
                    in2.popLimit(lim);
                    albumsVector.addElement( value );
                    break;
                }
                case 6: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeLogin(in2);
                    in2.popLimit(lim);
                    object.put("login",value);
                    break;
                }
                case 7: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeLogin(in2);
                    in2.popLimit(lim);
                    object.put("login2",value);
                    break;
                }
                case 4: {
                    Integer value = new Integer(in2.readInt32() );
                    object.put("newPeople",value);
                    break;
                }
                case 5: {
                    Integer value = new Integer(in2.readInt32() );
                    object.put("newMessages",value);
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        object.put("albums",albumsVector);
        return object;
    }
    private Hashtable decodeAlbum(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    String value = in2.readString();
                    object.put("someString",value);
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
    private Hashtable decodeMessage(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Object value = decodeAnonymousObject(in2);
                    in2.popLimit(lim);
                    object.put("body",value);
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
    private Hashtable decodeLogin(CodedInputStream in2) throws IOException {
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
                    Boolean value = new Boolean(in2.readBool() );
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
    private Hashtable decodeBob(CodedInputStream in2) throws IOException {
        Hashtable object = new Hashtable();
        Vector vec1Vector = new Vector();
        Vector vec2Vector = new Vector();
        Vector vec3Vector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Vector value = decodeVector(in2);
                    in2.popLimit(lim);
                    vec1Vector.addElement( value );
                    break;
                }
                case 2: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Vector value = decodeVector(in2);
                    in2.popLimit(lim);
                    vec2Vector.addElement( value );
                    break;
                }
                case 3: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Vector value = decodeVector(in2);
                    in2.popLimit(lim);
                    vec3Vector.addElement( value );
                    break;
                }
                default: {
                    in2.skipField(tag);
                    break;
                }
            }
        }
        object.put("vec1",vec1Vector);
        object.put("vec2",vec2Vector);
        object.put("vec3",vec3Vector);
        return object;
    }
    private TestObject decodeTestObject(CodedInputStream in2) throws IOException {
        TestObject object = new TestObject();
        Vector legsVector = new Vector();
        Vector armsVector = new Vector();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1204: {
                    int value = (int)in2.readInt32();
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
                case 2: {
                    int size = in2.readBytesSize();
                    int lim = in2.pushLimit(size);
                    Hashtable value = decodeClientLoginSuccess(in2);
                    in2.popLimit(lim);
                    armsVector.addElement( value );
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
                    boolean value = (boolean)in2.readBool();
                    object.setIsAlive(value);
                    break;
                }
                case 5: {
                    int value = (int)in2.readInt32();
                    object.setHeads(value);
                    break;
                }
                case 6: {
                    long value = (long)in2.readInt64();
                    object.setLastUpdated(value);
                    break;
                }
                case 7: {
                    int value = (int)in2.readInt32();
                    object.setThings(value);
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
        object.setArms(armsVector);
        return object;
    }
    private Test decodeTest(CodedInputStream in2) throws IOException {
        Test object = new Test();
        while (!in2.isAtEnd()) {
            int tag = in2.readTag();
            int fieldNo = WireFormat.getTagFieldNumber(tag);
            switch(fieldNo) {
                case 1201: {
                    int value = (int)in2.readInt32();
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

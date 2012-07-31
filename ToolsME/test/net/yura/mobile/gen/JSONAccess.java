package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.TestObject;
import net.yura.tools.mobilegen.model.Test;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.JSONUtil;
import net.yura.mobile.io.json.JSONWriter;
import net.yura.mobile.io.json.JSONTokener;
/**
 * THIS FILE IS GENERATED, DO NOT EDIT
 */
public class JSONAccess extends JSONUtil {
    public static final String TYPE_TESTOBJECT="TestObject";
    public static final String TYPE_TEST="Test";
    public JSONAccess() {
    }
    protected void saveObject(JSONWriter serializer, Object object) throws IOException {
        if (object instanceof TestObject) {
            serializer.object();
            serializer.key(TYPE_TESTOBJECT);
            serializer.object();
            saveTestObject(serializer,(TestObject)object);
            serializer.endObject();
            serializer.endObject();
        }
        else if (object instanceof Test) {
            serializer.object();
            serializer.key(TYPE_TEST);
            serializer.object();
            saveTest(serializer,(Test)object);
            serializer.endObject();
            serializer.endObject();
        }
        else {
            super.saveObject(serializer, object);
        }
    }
    protected void saveTestObject(JSONWriter serializer,TestObject object) throws IOException {
        saveTest(serializer,object);
        serializer.key("age");
        serializer.value( object.getAge() );
        serializer.key("body");
        {
            Object obj = object.getBody();
            if (obj!=null) {
                saveObject(serializer, obj );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("image");
        {
            byte[] bytes = object.getImage();
            if (bytes!=null) {
                serializer.value( new String(org.bouncycastle.util.encoders.Base64.encode(bytes)) );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("legs");
        {
            Object[] array = object.getLegs();
            if (array!=null) {
                saveArray( serializer, array );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("loginById");
        {
            Hashtable hashtable = object.getLoginById();
            if (hashtable!=null) {
                saveHashtable( serializer, hashtable );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("myType");
        {
            String string = object.getMyType();
            if (string!=null) {
                serializer.value( string );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("name");
        {
            String string = object.getName();
            if (string!=null) {
                serializer.value( string );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("numbers");
        {
            Vector vector = object.getNumbers();
            if (vector!=null) {
                saveVector(serializer, vector );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("objectById");
        {
            Object obj = object.getObjectById();
            if (obj!=null) {
                saveObject(serializer, obj );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("objects");
        {
            Object[] array = object.getObjects();
            if (array!=null) {
                saveArray( serializer, array );
            }
            else {
                serializer.nullValue();
            }
        }
        serializer.key("testById");
        {
            Test obj = object.getTestById();
            if (obj!=null) {
                serializer.object();
                saveTest( serializer, obj );
                serializer.endObject();
            }
            else {
                serializer.nullValue();
            }
        }
        
    }
    protected void saveTest(JSONWriter serializer,Test object) throws IOException {
        serializer.key("id");
        serializer.value( object.getId() );
    }
    protected Object readObject(JSONTokener tokener, String name) throws IOException {
        if ("TestObject".equals(name)) {
            return readTestObject(tokener);
        }
        else if ("Test".equals(name)) {
            return readTest(tokener);
        }
        else {
            return super.readObject(tokener,name);
        }
    }
    protected TestObject readTestObject(JSONTokener tokener) throws IOException {
        TestObject object = new TestObject();
        tokener.startObject();
        for (boolean end=false;!end;end = tokener.endObject()) {
            String key = tokener.nextKey();
            if ("age".equals(key)) {
                object.setAge( Byte.parseByte(tokener.nextSimple()) );
            }
            else if ("body".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setBody( readObject(tokener) );
                }
            }
            else if ("id".equals(key)) {
                object.setId( Integer.parseInt(tokener.nextSimple()) );
            }
            else if ("image".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setImage( org.bouncycastle.util.encoders.Base64.decode( tokener.nextString() ) );
                }
            }
            else if ("legs".equals(key)) {
                if (!tokener.nextNull()) {
                    Vector objects = readVector(tokener);
                    String[] array=null;
                    array = new String[objects.size()];
                    objects.copyInto(array);
                    object.setLegs(array);
                }
            }
            else if ("loginById".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setLoginById( readHashtable(tokener) );
                }
            }
            else if ("myType".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setMyType( tokener.nextString() );
                }
            }
            else if ("name".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setName( tokener.nextString() );
                }
            }
            else if ("numbers".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setNumbers( readVector(tokener) );
                }
            }
            else if ("objectById".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setObjectById( readObject(tokener) );
                }
            }
            else if ("objects".equals(key)) {
                if (!tokener.nextNull()) {
                    Vector objects = readVector(tokener);
                    Object[] array=null;
                    array = new Object[objects.size()];
                    objects.copyInto(array);
                    object.setObjects(array);
                }
            }
            else if ("testById".equals(key)) {
                if (!tokener.nextNull()) {
                    object.setTestById( readTest(tokener) );
                }
            }
            else {
                throw new IOException("unknown field: "+key); // TODO skip unknown fields
            }
        }
        return object;
    }
    protected Test readTest(JSONTokener tokener) throws IOException {
        Test object = new Test();
        tokener.startObject();
        for (boolean end=false;!end;end = tokener.endObject()) {
            String key = tokener.nextKey();
            if ("id".equals(key)) {
                object.setId( Integer.parseInt(tokener.nextSimple()) );
            }
            else {
                throw new IOException("unknown field: "+key); // TODO skip unknown fields
            }
        }
        return object;
    }
}

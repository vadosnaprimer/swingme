package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.TestObject;
import net.yura.tools.mobilegen.model.Test;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.JSONUtil;
import net.yura.mobile.io.json.JSONWriter;
import net.yura.mobile.util.SystemUtil;
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
            serializer.key("class");
            serializer.value(TYPE_TESTOBJECT);
            saveTestObject(serializer,(TestObject)object);
            serializer.endObject();
        }
        else if (object instanceof Test) {
            serializer.object();
            serializer.key("class");
            serializer.value(TYPE_TEST);
            saveTest(serializer,(Test)object);
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
        Object bodyValue = object.getBody();
        if (bodyValue!=null) {
            saveObject(serializer, bodyValue );
        }
        else {
            serializer.nullValue();
        }
        serializer.key("image");
        byte[] imageValue = object.getImage();
        if (imageValue!=null) {
            serializer.value( new String(org.bouncycastle.util.encoders.Base64.encode(imageValue)) );
        }
        else {
            serializer.nullValue();
        }
        serializer.key("legs");
        Object[] legsValue = object.getLegs();
        if (legsValue!=null) {
            saveArray(serializer, legsValue);
        }
        else {
            serializer.nullValue();
        }
        serializer.key("loginById");
        Hashtable loginByIdValue = object.getLoginById();
        if (loginByIdValue!=null) {
            saveHashtable(serializer, loginByIdValue);
        }
        else {
            serializer.nullValue();
        }
        serializer.key("myType");
        String myTypeValue = object.getMyType();
        if (myTypeValue!=null) {
            serializer.value(myTypeValue);
        }
        else {
            serializer.nullValue();
        }
        serializer.key("name");
        String nameValue = object.getName();
        if (nameValue!=null) {
            serializer.value(nameValue);
        }
        else {
            serializer.nullValue();
        }
        serializer.key("numbers");
        Vector numbersValue = object.getNumbers();
        if (numbersValue!=null) {
            saveVector(serializer, numbersValue);
        }
        else {
            serializer.nullValue();
        }
        serializer.key("objectById");
        Object objectByIdValue = object.getObjectById();
        if (objectByIdValue!=null) {
            saveObject(serializer, objectByIdValue );
        }
        else {
            serializer.nullValue();
        }
        serializer.key("objects");
        Object[] objectsValue = object.getObjects();
        if (objectsValue!=null) {
            saveArray(serializer, objectsValue);
        }
        else {
            serializer.nullValue();
        }
        serializer.key("testById");
        Test testByIdValue = object.getTestById();
        if (testByIdValue!=null) {
            serializer.object();
            saveTest(serializer, testByIdValue);
            serializer.endObject();
        }
        else {
            serializer.nullValue();
        }
    }
    protected void saveTest(JSONWriter serializer,Test object) throws IOException {
        serializer.key("id");
        serializer.value( object.getId() );
    }
    protected Object readObject(String name, Hashtable map) {
        if ("TestObject".equals(name)) {
            return readTestObject(map, new TestObject());
        }
        if ("Test".equals(name)) {
            return readTest(map, new Test());
        }
        return super.readObject(name, map);
    }
    protected TestObject readTestObject(Hashtable map, TestObject object) {
        readTest(map, object);
        object.setAge( ((Long) map.get("age")).byteValue() );
        object.setBody( map.get("body") );
        String imageValue = (String) map.get("image");
        if (imageValue != null) {
            object.setImage( org.bouncycastle.util.encoders.Base64.decode( (String) map.get("image") ) );
        }
        Object[] legsValue = (Object[]) map.get("legs");
        if (legsValue != null) {
            String[] legsArray = new String[legsValue.length];
            System.arraycopy(legsValue, 0, legsArray, 0, legsValue.length);
            object.setLegs(legsArray);
        }
        object.setLoginById( (Hashtable) map.get("loginById") );
        object.setMyType( (String) map.get("myType") );
        object.setName( (String) map.get("name") );
        Object[] numbersValue = (Object[]) map.get("numbers");
        if (numbersValue != null) {
            object.setNumbers(SystemUtil.asList(numbersValue));
        }
        object.setObjectById( map.get("objectById") );
        object.setObjects((Object[]) map.get("objects"));
        object.setTestById( (Test) map.get("testById") );
        return object;
    }
    protected Test readTest(Hashtable map, Test object) {
        object.setId( ((Long) map.get("id")).intValue() );
        return object;
    }
}

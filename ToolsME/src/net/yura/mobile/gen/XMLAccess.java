package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.TestObject;
import net.yura.tools.mobilegen.model.Test;
import java.util.Hashtable;
import java.util.Vector;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import net.yura.mobile.io.XMLUtil;
/**
 * THIS FILE IS GENERATED, DO NOT EDIT
 */
public class XMLAccess extends XMLUtil {
    public XMLAccess() {
    }
    protected void saveObject(XmlSerializer serializer,Object object) throws IOException {
        if (object instanceof TestObject) {
            serializer.startTag(null,"TestObject");
            saveTestObject(serializer,(TestObject)object);
            serializer.endTag(null,"TestObject");
        }
        else if (object instanceof Test) {
            serializer.startTag(null,"Test");
            saveTest(serializer,(Test)object);
            serializer.endTag(null,"Test");
        }
        else {
            super.saveObject(serializer, object);
        }
    }
    protected void saveTestObject(XmlSerializer serializer,TestObject object) throws IOException {
        if (object.getName()!=null) {
            serializer.attribute(null,"name", object.getName() );
        }
        if (object.getImage()!=null) {
            //serializer.attribute(null,"image", new String( org.bouncycastle.util.encoders.Base64.encode( object.getImage() ) ) );
        }
        serializer.attribute(null,"lastUpdated", String.valueOf( object.getLastUpdated() ) );
        serializer.attribute(null,"things", String.valueOf( object.getThings() ) );
        if (object.getMyType()!=null) {
            serializer.attribute(null,"myType", object.getMyType() );
        }
        serializer.attribute(null,"age", String.valueOf( object.getAge() ) );
        serializer.attribute(null,"heads", String.valueOf( object.getHeads() ) );
        serializer.attribute(null,"isAlive", String.valueOf( object.getIsAlive() ) );
        saveTest(serializer, object);
        serializer.startTag(null,"objects");
        saveObject(serializer, object.getObjects() );
        serializer.endTag(null,"objects");
        serializer.startTag(null,"body");
        saveObject(serializer, object.getBody() );
        serializer.endTag(null,"body");
        serializer.startTag(null,"andOneInside");
        saveObject(serializer, object.getAndOneInside() );
        serializer.endTag(null,"andOneInside");
        serializer.startTag(null,"legs");
        saveObject(serializer, object.getLegs() );
        serializer.endTag(null,"legs");
        serializer.startTag(null,"numbers");
        saveObject(serializer, object.getNumbers() );
        serializer.endTag(null,"numbers");
        serializer.startTag(null,"arms");
        saveObject(serializer, object.getArms() );
        serializer.endTag(null,"arms");
        serializer.startTag(null,"organs");
        saveObject(serializer, object.getOrgans() );
        serializer.endTag(null,"organs");
    }
    protected void saveTest(XmlSerializer serializer,Test object) throws IOException {
        serializer.attribute(null,"id", String.valueOf( object.getId() ) );
    }
    protected Object readObject(KXmlParser parser) throws Exception {
        String name = parser.getName();
        if ("TestObject".equals(name)) {
            return readTestObject(parser);
        }
        else if ("Test".equals(name)) {
            return readTest(parser);
        }
        else {
            return super.readObject(parser);
        }
    }
    protected TestObject readTestObject(KXmlParser parser) throws Exception {
        TestObject object = new TestObject();
        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("name".equals(key)) {
                object.setName(value);
            }
            else if ("image".equals(key)) {
                //object.setImage( org.bouncycastle.util.encoders.Base64.decode(value) );
            }
            else if ("lastUpdated".equals(key)) {
                object.setLastUpdated( Long.parseLong( value ) );
            }
            else if ("things".equals(key)) {
                object.setThings( Integer.parseInt(value) );
            }
            else if ("myType".equals(key)) {
                object.setMyType(value);
            }
            else if ("age".equals(key)) {
                object.setAge( Byte.parseByte(value) );
            }
            else if ("heads".equals(key)) {
                object.setHeads( Integer.parseInt(value) );
            }
            else if ("isAlive".equals(key)) {
                object.setIsAlive( "true".equals( value ) );
            }
            else if ("id".equals(key)) {
                object.setId( Integer.parseInt(value) );
            }
            else {
                System.out.println("unknown item found "+key);
            }
        }
        while (parser.nextTag() != KXmlParser.END_TAG) {
            String name = parser.getName();
            if ("body".equals(name)) {
                Object obj = null;
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    if (obj!=null) { throw new IOException(); }
                    obj = readObject(parser);
                }
                object.setBody( (Object)obj );
            }
            else if ("andOneInside".equals(name)) {
                Object obj = null;
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    if (obj!=null) { throw new IOException(); }
                    obj = readObject(parser);
                }
                object.setAndOneInside( (Test)obj );
            }
            else if ("objects".equals(name)) {
                Object obj = null;
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    if (obj!=null) { throw new IOException(); }
                    obj = readObject(parser);
                }
                Object[] array = null;
                if (obj!=null) {
                    Object[] objects = (Object[])obj;
                    array = new Object[objects.length];
                    System.arraycopy(objects,0,array,0,objects.length);
                }
                object.setObjects(array);
            }
            else if ("legs".equals(name)) {
                Object obj = null;
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    if (obj!=null) { throw new IOException(); }
                    obj = readObject(parser);
                }
                String[] array = null;
                if (obj!=null) {
                    Object[] objects = (Object[])obj;
                    array = new String[objects.length];
                    System.arraycopy(objects,0,array,0,objects.length);
                }
                object.setLegs(array);
            }
            else if ("numbers".equals(name)) {
                Object obj = null;
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    if (obj!=null) { throw new IOException(); }
                    obj = readObject(parser);
                }
                object.setNumbers( (Vector)obj );
            }
            else if ("arms".equals(name)) {
                Object obj = null;
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    if (obj!=null) { throw new IOException(); }
                    obj = readObject(parser);
                }
                object.setArms( (Vector)obj );
            }
            else if ("organs".equals(name)) {
                Object obj = null;
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    if (obj!=null) { throw new IOException(); }
                    obj = readObject(parser);
                }
                object.setOrgans( (Hashtable)obj );
            }
            else {
                System.out.println("unknown section: "+name);
                parser.skipSubTree();
            }
        }
        return object;
    }
    protected Test readTest(KXmlParser parser) throws Exception {
        Test object = new Test();
        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("id".equals(key)) {
                object.setId( Integer.parseInt(value) );
            }
            else {
                System.out.println("unknown item found "+key);
            }
        }
        parser.skipSubTree();
        return object;
    }
}

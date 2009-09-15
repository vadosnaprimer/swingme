package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.Test;
import net.yura.tools.mobilegen.model.TestObject;
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
    public void saveObject(XmlSerializer serializer,Object object) throws IOException {
        if (object instanceof Test) {
            serializer.startTag(null,"Test");
            saveTest(serializer,(Test)object);
            serializer.endTag(null,"Test");
        }
        else if (object instanceof TestObject) {
            serializer.startTag(null,"TestObject");
            saveTestObject(serializer,(TestObject)object);
            serializer.endTag(null,"TestObject");
        }
        else {
            super.saveObject(serializer, object);
        }
    }
    public void saveTest(XmlSerializer serializer,Test object) throws IOException {
        serializer.attribute(null,"id", String.valueOf( object.getId() ) );
    }
    public void saveTestObject(XmlSerializer serializer,TestObject object) throws IOException {
        serializer.attribute(null,"name", String.valueOf( object.getName() ) );
        serializer.attribute(null,"age", String.valueOf( object.getAge() ) );
        saveTest(serializer, object);
        serializer.startTag(null,"body");
        saveObject(serializer, object.getBody() );
        serializer.endTag(null,"body");
        serializer.startTag(null,"legs");
        saveObject(serializer, object.getLegs() );
        serializer.endTag(null,"legs");
        serializer.startTag(null,"numbers");
        saveObject(serializer, object.getNumbers() );
        serializer.endTag(null,"numbers");
    }
    public Object readObject(KXmlParser parser) throws Exception {
        String name = parser.getName();
        if ("Test".equals(name)) {
            return readTest(parser);
        }
        else if ("TestObject".equals(name)) {
            return readTestObject(parser);
        }
        else {
            return super.readObject(parser);
        }
    }
    private Test readTest(KXmlParser parser) throws Exception {
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
    private TestObject readTestObject(KXmlParser parser) throws Exception {
        TestObject object = new TestObject();
        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            if ("name".equals(key)) {
                object.setName(value);
            }
            else if ("age".equals(key)) {
                object.setAge( Byte.parseByte(value) );
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
            else {
                System.out.println("unknown section: "+name);
                parser.skipSubTree();
            }
        }
        return object;
    }
}

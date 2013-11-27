package net.yura.mobile.io;

import java.io.Reader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.yura.mobile.io.kxml2.KXmlParser;
import net.yura.mobile.io.kxml2.KXmlSerializer;
import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public class XMLUtil {

    public static final String TAG_HASHTABLE = "Hashtable";
    public static final String TAG_VECTOR = "Vector";
    public static final String TAG_ARRAY = "ArrayList";
    public static final String TAG_STRING = "String";
    public static final String TAG_INTEGER = "Integer";
    public static final String TAG_DOUBLE = "Double";
    public static final String TAG_FLOAT = "Float";
    public static final String TAG_BOOLEAN = "Boolean";
    public static final String TAG_SHORT = "Short";
    public static final String TAG_LONG = "Long";
    public static final String TAG_CHARACTER = "Character";
    public static final String TAG_BYTE = "Byte";
    public static final String TAG_NULL = "nulltype";

    private KXmlParser parser;

    public Object load(Reader reader) throws Exception {

            if (parser==null) {
                parser = new KXmlParser();
            }
            parser.setInput(reader);
            parser.nextTag();
            return readObject(parser);

    }

    public void save(OutputStream output,Object object) throws IOException {

            XmlSerializer serializer = new KXmlSerializer();
            serializer.setOutput(output,"UTF-8");
            serializer.startDocument("UTF-8", null);

            saveObject(serializer,object);

            //serializer.startTag(null,"task");
            //serializer.attribute(null,"method",method);
            //if (object!=null) { saveObject(serializer,object); }
            //serializer.endTag(null,"task");
            serializer.endDocument();
            serializer.flush();

    }

    protected void saveVector(XmlSerializer serializer,Vector object) throws IOException {
        for (int c=0;c<object.size();c++) {
            saveObject( serializer, object.elementAt(c) );
        }
    }
    protected void saveArray(XmlSerializer serializer,Object[] object) throws IOException {
        for (int c=0;c<object.length;c++) {
            saveObject( serializer, object[c] );
        }
    }

    protected void saveHashtable(XmlSerializer serializer,Hashtable object) throws IOException {

        Enumeration enu = object.keys();
        while (enu.hasMoreElements()) {
            Object key =enu.nextElement();
            Object obj = object.get(key);
            if (obj instanceof String && key instanceof String) {
                serializer.attribute(null, (String)key, (String)obj );
            }
        }
        
        enu = object.keys();
        while (enu.hasMoreElements()) {
            Object key = enu.nextElement();
            Object obj = object.get(key);
            if (!(obj instanceof String) || !(key instanceof String)) {
                serializer.startTag(null,"entry");
                saveObject(serializer,key);
                saveObject(serializer,obj);
                serializer.endTag(null,"entry");
            }
        }
    }

    protected static boolean isSimpleObject(Object object) {
        return object == null ||
                object instanceof String ||
                object instanceof Integer ||
                object instanceof Double ||
                object instanceof Long ||
                object instanceof Float ||
                object instanceof Short ||
                object instanceof Byte ||
                object instanceof Character ||
                object instanceof Boolean;
    }

    protected void saveObject(XmlSerializer serializer,Object object) throws IOException {
        String tagName = getObjectType(object);
        serializer.startTag(null,tagName);
        if (object instanceof Hashtable) {
            saveHashtable(serializer, (Hashtable)object);
        }
        else if (object instanceof Vector) {
            saveVector(serializer, (Vector)object);
        }
        else if (object instanceof Object[]) {
            saveArray(serializer, (Object[])object);
        }
        else {
            serializer.attribute(null,"value", String.valueOf(object) );
        }
        serializer.endTag(null,tagName);
    }

    protected static String getObjectType(Object object) throws IOException {

            String tagName;

            if (object == null) {
                tagName = TAG_NULL;
            }
            else if (object instanceof String) {
                tagName = TAG_STRING;
            }
            else if (object instanceof Integer) {
                tagName = TAG_INTEGER;
            }
            else if (object instanceof Character) {
                tagName = TAG_CHARACTER;
            }
            else if (object instanceof Double) {
                tagName = TAG_DOUBLE;
            }
            else if (object instanceof Float) {
                tagName = TAG_FLOAT;
            }
            else if (object instanceof Boolean) {
                tagName = TAG_BOOLEAN;
            }
            else if (object instanceof Byte) {
                tagName = TAG_BYTE;
            }
            else if (object instanceof Short) {
                tagName = TAG_SHORT;
            }
            else if (object instanceof Long) {
                tagName = TAG_LONG;
            }
            else if (object instanceof Hashtable) {
                tagName = TAG_HASHTABLE;
            }
            else if (object instanceof Vector) {
                tagName = TAG_VECTOR;
            }
            else if (object instanceof Object[]) {
                tagName = TAG_ARRAY;
            }
            else {
                // TODO somehow encode it or do something else??
                throw new IOException( "unknown class: "+object.getClass()+" for object: "+object );
            }

            return tagName;
    }

    protected Vector readVector(KXmlParser parser) throws Exception {

        Vector vector = new Vector();

        // read start tag
        while (parser.nextTag() != KXmlParser.END_TAG) {

                Object object =  readObject(parser);
                vector.addElement(object);

        }
        return vector;

    }

    protected Hashtable readHashtable(KXmlParser parser) throws Exception {

        Hashtable map = new Hashtable();

        int count = parser.getAttributeCount();
        for (int c=0;c<count;c++) {
            String key = parser.getAttributeName(c);
            String value = parser.getAttributeValue(c);
            map.put(key, value);

        }

        // read start tag
        while (parser.nextTag() != KXmlParser.END_TAG) {

            //String name = parser.getName(); // entry

            Object key=null;
            Object value=null;
            int a=0;

            while (parser.nextTag() != KXmlParser.END_TAG) {
                Object obj = readObject(parser);
                if (a==0) { key = obj; }
                else if (a==1) { value = obj; }
                else { throw new RuntimeException(); }
                a++;
            }

            map.put(key, value);
        }
        return map;

    }

    protected Object readObject(KXmlParser parser) throws Exception {

        String name = parser.getName();

        if (TAG_VECTOR.equals(name) ) {

            return readVector(parser);
        }
        else if (TAG_ARRAY.equals(name)) {
            Vector vector = readVector(parser);
            Object[] array = new Object[vector.size()];
            vector.copyInto(array);
            return array;
        }
        else if ( TAG_HASHTABLE.equals(name) || "HashMap".equals(name)) {

             return readHashtable(parser);
        }
        else {

            String value = parser.getAttributeValue(null, "value");
            // read end tag
            parser.skipSubTree();

            if (TAG_STRING.equals(name)) {
                return value;
            }
            else if (TAG_INTEGER.equals(name)) {
                return Integer.valueOf( value );
            }
            else if (TAG_DOUBLE.equals(name)) {
                return Double.valueOf( value );
            }
            else if (TAG_FLOAT.equals(name)) {
                return Float.valueOf( value );
            }
            else if (TAG_BOOLEAN.equals(name)) {
                return "true".equals( value )?Boolean.TRUE:Boolean.FALSE;
            }
            else if (TAG_SHORT.equals(name)) {
                return new Short( Short.parseShort( value ) );
            }
            else if (TAG_LONG.equals(name)) {
                return new Long( Long.parseLong( value ) );
            }
            else if (TAG_CHARACTER.equals(name)) {
                return new Character( value.charAt(0) );
            }
            else if (TAG_BYTE.equals(name)) {
                return new Byte( Byte.parseByte( value ) );
            }
            else if (TAG_NULL.equals(name)) {
                return null;
            }
            else {
                // TODO load class or skip it?
                throw new IOException( "unknown tag: "+name );
            }

        }

    }


}

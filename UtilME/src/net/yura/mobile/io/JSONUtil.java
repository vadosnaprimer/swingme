package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.yura.mobile.io.json.JSONTokener;
import net.yura.mobile.io.json.JSONWriter;

/**
 * @author Yura Mamyrin
 */
public class JSONUtil {

    public void save(OutputStream out, Object obj) throws IOException {

        OutputStreamWriter w = new OutputStreamWriter(out);

        JSONWriter writer = new JSONWriter(w);

        saveObject(writer,obj);

        w.flush();

    }

    public Object load(InputStream in) throws IOException {

        JSONTokener tokener = new JSONTokener(new UTF8InputStreamReader(in));

        return readObject(tokener);
    }

    private void saveObject(JSONWriter serializer, Object object) throws IOException {

        serializer.object();

        if (object instanceof Hashtable) {
            serializer.key(XMLUtil.TAG_HASHTABLE);
            saveHashtable(serializer, (Hashtable)object);
        }
        else if (object instanceof Vector) {
            serializer.key(XMLUtil.TAG_VECTOR);
            saveVector(serializer, (Vector)object);
        }
        else if (object instanceof Object[]) {
            serializer.key(XMLUtil.TAG_ARRAY);
            saveArray(serializer, (Object[])object);
        }
        else {
            serializer.key( XMLUtil.getObjectType(object) );
            
            if (object instanceof Boolean) {
                serializer.value( ((Boolean)object).booleanValue() );
            }
            else if (object == null) {
                serializer.nullValue();
            }
            else if (object instanceof String) {
                serializer.value( (String)object );
            }
            else if (object instanceof Character) {
                serializer.value( ((Character)object).charValue() );
            }
            else if (object instanceof Integer) {
                serializer.value( ((Integer)object).longValue() );
            }
            else if (object instanceof Double) {
                serializer.value( ((Double)object).doubleValue() );
            }
            else if (object instanceof Float) {
                serializer.value( ((Float)object).doubleValue() );
            }
            else if (object instanceof Boolean) {
                serializer.value( ((Boolean)object).booleanValue() );
            }
            else if (object instanceof Byte) {
                serializer.value( ((Byte)object).byteValue() );
            }
            else if (object instanceof Short) {
                serializer.value( ((Short)object).shortValue() );
            }
            else if (object instanceof Long) {
                serializer.value( ((Long)object).longValue() );
            }
            else {
                throw new IOException();
            }
        }
        serializer.endObject();
    }

    private Object readObject(JSONTokener x) throws IOException {

        x.startObject();

        String key = x.nextKey();

        Object object = readObject(x, key);

        if (!x.endObject()) {
            throw new IOException();
        }

        return object;

    }

    protected Object readObject(JSONTokener tokener, String name) throws IOException {

        if (XMLUtil.TAG_VECTOR.equals(name) ) {
            return readVector(tokener);
        }
        else if (XMLUtil.TAG_ARRAY.equals(name)) {
            Vector vector = readVector(tokener);
            Object[] array = new Object[vector.size()];
            vector.copyInto(array);
            return array;
        }
        else if ( XMLUtil.TAG_HASHTABLE.equals(name) || "HashMap".equals(name)) {
             return readHashtable(tokener);
        }
        else if (XMLUtil.TAG_STRING.equals(name)) {
             return tokener.nextString();
        }
        else {

            String value = tokener.nextSimple();

            if (XMLUtil.TAG_INTEGER.equals(name)) {
                return Integer.valueOf( value );
            }
            else if (XMLUtil.TAG_DOUBLE.equals(name)) {
                return Double.valueOf( value );
            }
            else if (XMLUtil.TAG_FLOAT.equals(name)) {
                return Float.valueOf( value );
            }
            else if (XMLUtil.TAG_BOOLEAN.equals(name)) {
                return new Boolean( "true".equals( value ) );
            }
            else if (XMLUtil.TAG_SHORT.equals(name)) {
                return new Short( Short.parseShort( value ) );
            }
            else if (XMLUtil.TAG_LONG.equals(name)) {
                return new Long( Long.parseLong( value ) );
            }
            else if (XMLUtil.TAG_CHARACTER.equals(name)) {
                return new Character( value.charAt(0) );
            }
            else if (XMLUtil.TAG_BYTE.equals(name)) {
                return new Byte( Byte.parseByte( value ) );
            }
            else if (XMLUtil.TAG_NULL.equals(name)) {
                return null;
            }
            else {
                // TODO load class or something???
                System.out.println("unknown object: "+name);
                //return value;
                throw new IOException();
            }

        }

    }

    private void saveHashtable(JSONWriter serializer, Hashtable hashtable) throws IOException {

        boolean keyObject = false;

        Enumeration enu = hashtable.keys();
        while (enu.hasMoreElements()) {
            Object key =enu.nextElement();
            if (!(key instanceof String)) {
                keyObject = true;
                break;
            }
        }

        if (keyObject) { // TODO ???

            serializer.array();

            enu = hashtable.keys();
            while (enu.hasMoreElements()) {
                Object key = enu.nextElement();
                Object obj = hashtable.get(key);
                saveObject(serializer, key);
                saveObject(serializer, obj);
            }

            serializer.endArray();
        }
        else {
            serializer.object();

            enu = hashtable.keys();
            while (enu.hasMoreElements()) {
                String key = (String)enu.nextElement();
                Object obj = hashtable.get(key);
                serializer.key(key);
                saveObject(serializer, obj);

            }

            serializer.endObject();
        }

    }

    protected void saveVector(JSONWriter serializer,Vector object) throws IOException {
        serializer.array();
        for (int c=0;c<object.size();c++) {
            saveObject( serializer, object.elementAt(c) );
        }
        serializer.endArray();
    }
    protected void saveArray(JSONWriter serializer,Object[] object) throws IOException {
        serializer.array();
        for (int c=0;c<object.length;c++) {
            saveObject( serializer, object[c] );
        }
        serializer.endArray();
    }

    private Vector readVector(JSONTokener x) throws IOException {
        Vector vector = new Vector();

        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() == ']') {
            return vector;
        }
        x.back();
        for (;;) {
            if (x.nextClean() == ',') {
                x.back();
                vector.addElement(null);
            } else {
                x.back();
                vector.addElement( readObject(x) );
            }
            switch (x.nextClean()) {
                case ';':
                case ',':
                    if (x.nextClean() == ']') {
                        return vector;
                    }
                    x.back();
                    break;
                case ']':
                    return vector;
                default:
                    throw x.syntaxError("Expected a ',' or ']'");
            }
        }

    }

    private Hashtable readHashtable(JSONTokener x) throws IOException {
        Hashtable hashtable = new Hashtable();
        if (x.nextClean() == '[') {
            // we will use array of key,value

        }
        else {
            x.back(); // as we read the first char to check for type we need to go back
            x.startObject();

            for (boolean end=false;!end;end = x.endObject()) {
                String key = x.nextKey();
                Object obj = readObject(x);
                hashtable.put(key, obj);
            }

        }
        return hashtable;
    }

}

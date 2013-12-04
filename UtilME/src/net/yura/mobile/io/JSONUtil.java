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
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.SystemUtil;

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

    protected void saveObject(JSONWriter serializer, Object object) throws IOException {

        if (object == null) {
            serializer.nullValue();
        }
        else if (object instanceof String) {
            serializer.value( (String)object );
        }
        else if (object instanceof Boolean) {
            serializer.value( ((Boolean)object).booleanValue() );
        }
        else if (object instanceof Hashtable) {
            saveHashtable(serializer, (Hashtable)object);
        }
        else if (object instanceof Object[]) {
            saveArray(serializer, (Object[])object);
        }
        else if (object instanceof Double) {
            serializer.value( ((Double)object).doubleValue() );
        }
        else if (object instanceof Long) {
            serializer.value( ((Long)object).longValue() );
        }
        else if (object instanceof Character) {
            serializer.value( ((Character)object).charValue() );
        }
        else {
            serializer.object();
            serializer.key("class");
            serializer.value( XMLUtil.getObjectType(object) );
            serializer.key("value");
            if (object instanceof Integer) {
                serializer.value( ((Integer)object).longValue() );
            }
            else if (object instanceof Float) {
                serializer.value( ((Float)object).doubleValue() );
            }
            else if (object instanceof Byte) {
                serializer.value( (long) ((Byte) object).byteValue() );
            }
            else if (object instanceof Short) {
                serializer.value( (long) ((Short) object).shortValue() );
            }
            else if (object instanceof Vector) {
                saveVector(serializer, (Vector)object);
            }
            else {
                throw new IOException("unknown object "+object);
            }
            serializer.endObject();
        }
    }

    /**
     * this method can detect string, null, true, false, or any object
     */
    protected Object readObject(JSONTokener x) throws IOException {

        char c = x.nextClean();
        x.back();
        Object result;

        switch (c) {
            case '"':
                result = x.nextString();
                break;
            case '\'':
                String string = x.nextString();
                if (string.length() == 1) {
                    result = new Character(string.charAt(0));
                }
                else {
                    result = string;
                }
                break;
            case '{':
        	Hashtable map = readHashtable(x);
        	Object objClass = map.get("class");
        	if (objClass instanceof String) {
        	    map.remove("class");
        	    result = readObject((String) objClass, map);
        	}
        	else {
        	    result = map;
        	}
                break;
            case '[':
        	Vector vector = readVector(x);
        	Object[] array = new Object[vector.size()];
        	vector.copyInto(array);
                result = array;
                break;
            case '(':
                // TODO not currently supported as does not recognise close ')'
                result = readVector(x);
                break;
            default:
                String s = x.nextSimple();
                if (s.equalsIgnoreCase("true")) {
                    result = Boolean.TRUE;
                }
                else if (s.equalsIgnoreCase("false")) {
                    result =  Boolean.FALSE;
                }
                else if (s.equalsIgnoreCase("null")) {
                    result = null;
                }
                else if (s.indexOf('.') >= 0) {
                    result = Double.valueOf(s);
                }
                else {
                    result = new Long( Long.parseLong(s) );
                }
                break;
        }
        return result;
    }

    protected Object readObject(String name, Hashtable map) {
        Object value = map.get("value");
        if (XMLUtil.TAG_HASHTABLE.equals(name)) {
            // we can only end up here if we failed to encode the map because of non-string keys.
            Object[] array = (Object[])value;
            Hashtable object = new Hashtable();
            for (int c = 0; c < array.length; c++) {
        	Object key = array[c++];
        	object.put(key, array[c]);
            }
            return object;
        }
        else if (XMLUtil.TAG_VECTOR.equals(name)) {
            return SystemUtil.asList((Object[])value);
        }
        else if (XMLUtil.TAG_INTEGER.equals(name)) {
            return new Integer((int) ((Long) value).longValue());
        }
        else if (XMLUtil.TAG_FLOAT.equals(name)) {
            return new Float(((Double)value).floatValue());
        }
        else if (XMLUtil.TAG_SHORT.equals(name)) {
            return new Short((short) ((Long) value).longValue());
        }
        else if (XMLUtil.TAG_CHARACTER.equals(name)) {
            return new Character( ((String)value).charAt(0) );
        }
        else if (XMLUtil.TAG_BYTE.equals(name)) {
            return new Byte((byte) ((Long)value).longValue());
        }
        // No reason to double encode as these are supported by json.
        //else if (XMLUtil.TAG_DOUBLE.equals(name)) {
        //    return Double.valueOf( value );
        //}
        //else if (XMLUtil.TAG_LONG.equals(name)) {
        //    return new Long( Long.parseLong( value ) );
        //}
        else {
            // Failed to find a class, so will return Map.
            System.err.println("dont know how to decode "+name+" "+map);
            map.put("class", name);
            return map;
        }
    }

    protected void saveHashtable(JSONWriter serializer, Hashtable hashtable) throws IOException {

        boolean keyObject = false;

        Enumeration enu = hashtable.keys();
        while (enu.hasMoreElements()) {
            Object key =enu.nextElement();
            if (!(key instanceof String)) {
                keyObject = true;
                break;
            }
        }

        if (keyObject) {
            serializer.object();
            serializer.key("class");
            serializer.value(XMLUtil.TAG_HASHTABLE);
            serializer.key("value");
            serializer.array();
            enu = hashtable.keys();
            while (enu.hasMoreElements()) {
                Object key = enu.nextElement();
                Object obj = hashtable.get(key);
                saveObject(serializer, key);
                saveObject(serializer, obj);
            }
            serializer.endArray();
            serializer.endObject();
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

    protected Vector readVector(JSONTokener x) throws IOException {
        Vector vector = new Vector();
        x.startArray();

        // CHECK FOR EMPTY VECTOR
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
            if (x.endArray()) {
                return vector;
            }
        }
    }

    protected Hashtable readHashtable(JSONTokener x) throws IOException {
        Hashtable hashtable = new Hashtable();
        x.startObject();
        // CHECK FOR EMPTY HASHTABLE
        if (x.nextClean() == '}') {
            return hashtable;
        }
        x.back(); // as we read the first char to check for empty we need to go back
        for (boolean end=false;!end;end = x.endObject()) {
            String key = x.nextKey();
            Object obj = readObject(x);
            if (obj != null) {
                hashtable.put(key, obj);
            }
        }
        return hashtable;
    }
}

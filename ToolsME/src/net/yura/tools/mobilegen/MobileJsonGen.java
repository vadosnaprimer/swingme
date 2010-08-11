package net.yura.tools.mobilegen;

import net.yura.tools.mobilegen.model.TestObject;
import net.yura.tools.mobilegen.model.Test;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Lenin
 */
public class MobileJsonGen extends BaseGen {

    @Override
    public void doGen() throws Exception {

   
/*
        Class theclass = TestObject.class;

        Class superclass = theclass.getSuperclass();
System.out.println(superclass);
        Method[] methods = theclass.getMethods();
System.out.println(Arrays.asList(methods));
        Class[] classes = theclass.getClasses();
System.out.println(Arrays.asList(classes));
        Method[] mymethods = theclass.getDeclaredMethods();
System.out.println(Arrays.asList(mymethods));

String className = theclass.getSimpleName();

*/
//ArrayList<Class> classes = new ArrayList<Class>();
//classes.add(Test.class);
//classes.add(TestObject.class);

ArrayList<Class> classes = (ArrayList<Class>) loadClassesFromFile(getClassNamesFile());
File f = getGeneratedFile();
System.out.println("saving to file: "+f);
PrintStream ps = new PrintStream( f); //new File("src/net/yura/mobile/gen/BinAccess.java"));

int n = 0;



ps.println("package "+getOutputPackage()+";");

for (Class c:classes) {
ps.println("import "+c.getName().replaceAll("\\$", "\\.")+";");
}

ps.println("import java.util.Hashtable;");
ps.println("import java.util.Vector;");
ps.println("import java.io.IOException;");
ps.println("import net.yura.mobile.io.JSONUtil;");
ps.println("import net.yura.mobile.io.json.JSONWriter;");
ps.println("import net.yura.mobile.io.json.JSONTokener;");

ps.println("/**");
ps.println(" * THIS FILE IS GENERATED, DO NOT EDIT");
ps.println(" */");
ps.println("public class "+getOutputClass()+" extends JSONUtil {");

n = 0;
for (Class c:classes) {
ps.println("    public static final String TYPE_"+c.getSimpleName().toUpperCase()+"=\""+c.getSimpleName()+"\";");
n++;
}

ps.println("    public "+getOutputClass()+"() {");
ps.println("    }");

ps.println("    protected void saveObject(JSONWriter serializer, Object object) throws IOException {");

n=0;
for (Class c:classes) {

String className = c.getSimpleName();

ps.println("        "+ ((n==0)?"":"else ") +"if (object instanceof "+className+") {");
ps.println("            serializer.object();");
ps.println("            serializer.key(TYPE_"+className.toUpperCase()+");");
ps.println("            serializer.object();");
ps.println("            save"+className+"(serializer,("+className+")object);");
ps.println("            serializer.endObject();");
ps.println("            serializer.endObject();");
ps.println("        }");

n++;
}


ps.println("        else {");
ps.println("            super.saveObject(serializer, object);");
ps.println("        }");

ps.println("    }");

for (Class c:classes) {
printSaveMethod(ps,c);
}

ps.println("    protected Object readObject(JSONTokener tokener, String name) throws IOException {");

n=0;
for (Class c:classes) {

String className = c.getSimpleName();

ps.println("        "+ ((n==0)?"":"else ") +"if (\""+className+"\".equals(name)) {");
ps.println("            return read"+className+"(tokener);");
ps.println("        }");
n++;
}

ps.println("        else {");
ps.println("            return super.readObject(tokener,name);");
ps.println("        }");

ps.println("    }");

for (Class c:classes) {
printLoadMethod(ps,c);
}

ps.println("}");
//        saveToFile(new File("output.java"), stringbuilder.toString());

    }


    public static void printSaveMethod(PrintStream ps,Class theclass) {

String className = theclass.getSimpleName();

ps.println("    protected void save"+className+"(JSONWriter serializer,"+className+" object) throws IOException {");

if (theclass.getSuperclass() != Object.class) {
ps.println("        save"+theclass.getSuperclass().getSimpleName()+"(serializer,object);");
}

ArrayList<Method> simpleMethods = getMethods(theclass,false,false);


for (Method m: simpleMethods) {

ps.println("        serializer.key(\""+MobileXmlGen.paramName(m)+"\");");

Class param = m.getReturnType();

if (param == String.class) {
ps.println("        {");
ps.println("            String string = object."+m.getName()+"();");
ps.println("            if (string!=null) {");
ps.println("                serializer.value( string );");
ps.println("            }");
ps.println("            else {");
ps.println("                serializer.nullValue();");
ps.println("            }");
ps.println("        }");
}
else if (param == int.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == double.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == float.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == boolean.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == short.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == long.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == char.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == byte.class) {
ps.println("        serializer.value( object."+m.getName()+"() );");
}
else if (param == Vector.class) {
ps.println("        {");
ps.println("            Vector vector = object."+m.getName()+"();");
ps.println("            if (vector!=null) {");
ps.println("                saveVector(serializer, vector );");
ps.println("            }");
ps.println("            else {");
ps.println("                serializer.nullValue();");
ps.println("            }");
ps.println("        }");
}
else if (param == Hashtable.class) {
ps.println("        {");
ps.println("            Hashtable hashtable = object."+m.getName()+"();");
ps.println("            if (hashtable!=null) {");
ps.println("                saveHashtable( serializer, hashtable );");
ps.println("            }");
ps.println("            else {");
ps.println("                serializer.nullValue();");
ps.println("            }");
ps.println("        }");
}
else if (param == byte[].class) {
ps.println("        {");
ps.println("            byte[] bytes = object."+m.getName()+"();");
ps.println("            if (bytes!=null) {");
ps.println("                serializer.value( new String(org.bouncycastle.util.encoders.Base64.encode(bytes)) );");
ps.println("            }");
ps.println("            else {");
ps.println("                serializer.nullValue();");
ps.println("            }");
ps.println("        }");
}
else if (param.isArray()) {
ps.println("        {");
ps.println("            Object[] array = object."+m.getName()+"();");
ps.println("            if (array!=null) {");
ps.println("                saveArray( serializer, array );");
ps.println("            }");
ps.println("            else {");
ps.println("                serializer.nullValue();");
ps.println("            }");
ps.println("        }");
}
else if (param == Object.class) {
ps.println("        {");
ps.println("            Object obj = object."+m.getName()+"();");
ps.println("            if (obj!=null) {");
ps.println("                serializer.object();");
ps.println("                saveObject(serializer, obj );");
ps.println("                serializer.endObject();");
ps.println("            }");
ps.println("            else {");
ps.println("                serializer.nullValue();");
ps.println("            }");
ps.println("        }");
}
else {
ps.println("        {");
ps.println("            "+param.getSimpleName()+" obj = object."+m.getName()+"();");
ps.println("            if (obj!=null) {");
ps.println("                serializer.object();");
ps.println("                save"+param.getSimpleName()+"( serializer, obj );");
ps.println("                serializer.endObject();");
ps.println("            }");
ps.println("            else {");
ps.println("                serializer.nullValue();");
ps.println("            }");
ps.println("        }");
ps.println("        ");
}

}

ps.println("    }");

    }

    public static void printLoadMethod(PrintStream ps,Class theclass) {

String className = theclass.getSimpleName();

ps.println("    protected "+className+" read"+className+"(JSONTokener tokener) throws IOException {");

ps.println("        "+className+" object = new "+className+"();");

ps.println("        tokener.startObject();");

ps.println("        for (boolean end=false;!end;end = tokener.endObject()) {");
ps.println("            String key = tokener.nextKey();");

ArrayList<Method> methods = getMethods(theclass,true,true);
int n = 0;
for (Method m: methods) {
Class param = m.getParameterTypes()[0];

ps.println("            "+(n==0?"":"else ")+"if (\""+MobileXmlGen.paramName(m)+"\".equals(key)) {");

if (param == String.class) {
ps.println("                if (!tokener.nextNull()) {");
ps.println("                    object."+m.getName()+"( tokener.nextString() );");
ps.println("                }");
}
else if (param == int.class) {
ps.println("                object."+m.getName()+"( Integer.parseInt(tokener.nextSimple()) );");
}
else if (param == double.class) {
ps.println("                object."+m.getName()+"( Double.parseDouble(tokener.nextSimple()) );");
}
else if (param == float.class) {
ps.println("                object."+m.getName()+"( Float.parseFloat(tokener.nextSimple()) );");
}
else if (param == boolean.class) {
ps.println("                object."+m.getName()+"( \"true\".equals(tokener.nextSimple()) );");
}
else if (param == short.class) {
ps.println("                object."+m.getName()+"( Short.parseShort(tokener.nextSimple()) );");
}
else if (param == long.class) {
ps.println("                object."+m.getName()+"( Long.parseLong(tokener.nextSimple()) );");
}
else if (param == char.class) {
ps.println("                object."+m.getName()+"( tokener.nextSimple().charAt(0) );");
}
else if (param == byte.class) {
ps.println("                object."+m.getName()+"( Byte.parseByte(tokener.nextSimple()) );");
}
else if (param == Vector.class) {
ps.println("                if (!tokener.nextNull()) {");
ps.println("                    object."+m.getName()+"( readVector(tokener) );");
ps.println("                }");
}
else if (param == Hashtable.class) {
ps.println("                if (!tokener.nextNull()) {");
ps.println("                    object."+m.getName()+"( readHashtable(tokener) );");
ps.println("                }");
}
else if (param == byte[].class) {
ps.println("                if (!tokener.nextNull()) {");
ps.println("                    object."+m.getName()+"( org.bouncycastle.util.encoders.Base64.decode( tokener.nextString() ) );");
ps.println("                }");
}
else if (param.isArray()) {
ps.println("                if (!tokener.nextNull()) {");
ps.println("                    Vector objects = readVector(tokener);");
ps.println("                    "+param.getComponentType().getSimpleName()+"[] array=null;");
ps.println("                    array = new "+param.getComponentType().getSimpleName()+"[objects.size()];");
ps.println("                    objects.copyInto(array);");
ps.println("                    object."+m.getName()+"(array);");
ps.println("                }");
}
else if (param == Object.class) {
ps.println("                if (!tokener.nextNull()) {");
ps.println("                    object."+m.getName()+"( readObject(tokener) );");
ps.println("                }");
}
else {
ps.println("                if (!tokener.nextNull()) {");
ps.println("                    object."+m.getName()+"( read"+param.getSimpleName()+"(tokener) );");
ps.println("                }");
}
ps.println("            }");
n++;
}
ps.println("            else {");
ps.println("                throw new IOException(\"unknown field: \"+key); // TODO skip unknown fields");
ps.println("            }");

ps.println("        }");

ps.println("        return object;");
ps.println("    }");


    }


	public static void saveToFile(File file,String string) {

            try {

                Writer outputWriter = null;

                try {

                    outputWriter = new BufferedWriter( new FileWriter(file) );

                    outputWriter.write(string);

                    outputWriter.flush();

                }
                finally {
                    if (outputWriter != null) outputWriter.close();
                }

            }
            catch(Exception ex) {

                    ex.printStackTrace();
            }



	}

    private static ArrayList<Method> getMethods(Class theclass, boolean set,boolean insuper) {
        Method[] mymethods = theclass.getDeclaredMethods();
        ArrayList<Method> result = new ArrayList<Method>();
        for (Method method:mymethods) {
            if (set && method.getName().startsWith("set") && hasBeanProperty(mymethods,method.getName().substring(3)) ) {
                result.add(method);
            }
            else if (!set && method.getName().startsWith("get") && hasBeanProperty(mymethods,method.getName().substring(3)) ) {
                result.add(method);
            }
        }

        if ( insuper && theclass.getSuperclass() != Object.class) {
            ArrayList<Method> result2 = getMethods(theclass.getSuperclass(), set,insuper);
            result.addAll(result2);
        }

        Collections.sort(result, new Comparator<Method>() {
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return result;
    }
}


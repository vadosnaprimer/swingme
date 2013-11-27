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

    static ArrayList<Class> classes;
    
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

classes = (ArrayList<Class>) sort(loadClassesFromFileRaw(getClassNamesFile()));
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
ps.println("import net.yura.mobile.util.SystemUtil;");

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
ps.println("            serializer.key(\"class\");");
ps.println("            serializer.value(TYPE_"+className.toUpperCase()+");");
ps.println("            save"+className+"(serializer,("+className+")object);");
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

ps.println("    protected Object readObject(String name, Hashtable map) {");

n=0;
for (Class c:classes) {

String className = c.getSimpleName();

ps.println("        if (\""+className+"\".equals(name)) {");
ps.println("            return read"+className+"(map, new "+className+"());");
ps.println("        }");
n++;
}

ps.println("        return super.readObject(name, map);");

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

Class superClass = theclass.getSuperclass();
ArrayList<Method> simpleMethods;
if (classes.contains(superClass)) {
    if (superClass != Object.class) {
    ps.println("        save"+superClass.getSimpleName()+"(serializer,object);");
    }
    simpleMethods = getMethods(theclass,false,false);
}
else {
    simpleMethods = getMethods(theclass,false,true);
}

for (Method m: simpleMethods) {

String name = MobileXmlGen.paramName(m);

ps.println("        serializer.key(\""+name+"\");");

Class param = m.getReturnType();

if (param == int.class) {
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
else if (param == String.class) {
ps.println("        String "+name+"Value = object."+m.getName()+"();");
ps.println("        if ("+name+"Value!=null) {");
ps.println("            serializer.value("+name+"Value);");
ps.println("        }");
ps.println("        else {");
ps.println("            serializer.nullValue();");
ps.println("        }");
}
else if (param == Vector.class) {
ps.println("        Vector "+name+"Value = object."+m.getName()+"();");
ps.println("        if ("+name+"Value!=null) {");
ps.println("            saveVector(serializer, "+name+"Value);");
ps.println("        }");
ps.println("        else {");
ps.println("            serializer.nullValue();");
ps.println("        }");
}
else if (param == Hashtable.class) {
ps.println("        Hashtable "+name+"Value = object."+m.getName()+"();");
ps.println("        if ("+name+"Value!=null) {");
ps.println("            saveHashtable(serializer, "+name+"Value);");
ps.println("        }");
ps.println("        else {");
ps.println("            serializer.nullValue();");
ps.println("        }");
}
else if (param == byte[].class) {
ps.println("        byte[] "+name+"Value = object."+m.getName()+"();");
ps.println("        if ("+name+"Value!=null) {");
ps.println("            serializer.value( new String(org.bouncycastle.util.encoders.Base64.encode("+name+"Value)) );");
ps.println("        }");
ps.println("        else {");
ps.println("            serializer.nullValue();");
ps.println("        }");
}
else if (param.isArray()) {
ps.println("        Object[] "+name+"Value = object."+m.getName()+"();");
ps.println("        if ("+name+"Value!=null) {");
ps.println("            saveArray(serializer, "+name+"Value);");
ps.println("        }");
ps.println("        else {");
ps.println("            serializer.nullValue();");
ps.println("        }");
}
else if (param == Object.class) {
ps.println("        Object "+name+"Value = object."+m.getName()+"();");
ps.println("        if ("+name+"Value!=null) {");
ps.println("            saveObject(serializer, "+name+"Value );");
ps.println("        }");
ps.println("        else {");
ps.println("            serializer.nullValue();");
ps.println("        }");
}
else {
ps.println("        "+param.getSimpleName()+" "+name+"Value = object."+m.getName()+"();");
ps.println("        if ("+name+"Value!=null) {");
ps.println("            serializer.object();");
ps.println("            save"+param.getSimpleName()+"(serializer, "+name+"Value);");
ps.println("            serializer.endObject();");
ps.println("        }");
ps.println("        else {");
ps.println("            serializer.nullValue();");
ps.println("        }");
}

}

ps.println("    }");

    }

    public static void printLoadMethod(PrintStream ps,Class theclass) {

String className = theclass.getSimpleName();

ps.println("    protected "+className+" read"+className+"(Hashtable map, "+className+" object) {");


Class superClass = theclass.getSuperclass();
ArrayList<Method> methods;
if (classes.contains(superClass)) {
    if (superClass != Object.class) {
    ps.println("        read"+superClass.getSimpleName()+"(map, object);");
    }
    methods = getMethods(theclass,true,false);
}
else {
    methods = getMethods(theclass,true,true);
}



int n = 0;
for (Method m: methods) {
Class param = m.getParameterTypes()[0];

String name = MobileXmlGen.paramName(m);

if (param == String.class) {
ps.println("        object."+m.getName()+"( (String) map.get(\""+name+"\") );");
}
else if (param == int.class) {
ps.println("        object."+m.getName()+"( ((Long) map.get(\""+name+"\")).intValue() );");
}
else if (param == double.class) {
ps.println("        object."+m.getName()+"( ((Double) map.get(\""+name+"\")).doubleValue() );");
}
else if (param == float.class) {
ps.println("        object."+m.getName()+"( ((Double) map.get(\""+name+"\")).floatValue() );");
}
else if (param == short.class) {
ps.println("        object."+m.getName()+"( ((Long) map.get(\""+name+"\")).shortValue() );");
}
else if (param == long.class) {
ps.println("        object."+m.getName()+"( ((Long) map.get(\""+name+"\")).longValue() );");
}
else if (param == char.class) {
ps.println("        object."+m.getName()+"( ((Character) map.get(\""+name+"\")).charValue() );");
}
else if (param == byte.class) {
ps.println("        object."+m.getName()+"( ((Long) map.get(\""+name+"\")).byteValue() );");
}
else if (param == boolean.class) {
ps.println("        object."+m.getName()+"( ((Boolean) map.get(\""+name+"\")).booleanValue() );");
}
else if (param == Hashtable.class) {
ps.println("        object."+m.getName()+"( (Hashtable) map.get(\""+name+"\") );");
}
else if (param == byte[].class) {
ps.println("        String "+name+"Value = (String) map.get(\""+name+"\");");
ps.println("        if ("+name+"Value != null) {");
ps.println("            object."+m.getName()+"( org.bouncycastle.util.encoders.Base64.decode( (String) map.get(\""+name+"\") ) );");
ps.println("        }");
}
else if (param.isArray()) {
    if (param.getComponentType() == Object.class){
ps.println("        object."+m.getName()+"((Object[]) map.get(\""+name+"\"));");
    }
    else {
ps.println("        Object[] "+name+"Value = (Object[]) map.get(\""+name+"\");");
ps.println("        if ("+name+"Value != null) {");
ps.println("            "+param.getComponentType().getSimpleName()+"[] "+name+"Array = new "+param.getComponentType().getSimpleName()+"["+name+"Value.length];");
ps.println("            System.arraycopy("+name+"Value, 0, "+name+"Array, 0, "+name+"Value.length);");
ps.println("            object."+m.getName()+"("+name+"Array);");
ps.println("        }");
    }
}
else if (param == Vector.class) {
ps.println("        Object[] "+name+"Value = (Object[]) map.get(\""+name+"\");");
ps.println("        if ("+name+"Value != null) {");
ps.println("            object."+m.getName()+"(SystemUtil.asList("+name+"Value));");
ps.println("        }");
}
else if (param == Object.class) {
ps.println("        object."+m.getName()+"( map.get(\""+name+"\") );");
}
else {
ps.println("        object."+m.getName()+"( ("+param.getSimpleName()+") map.get(\""+name+"\") );");
}
n++;
}

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
            if (set && method.getName().startsWith("set") && hasBeanProperty(theclass,mymethods,method.getName().substring(3)) ) {
                result.add(method);
            }
            else if (!set && method.getName().startsWith("get") && hasBeanProperty(theclass,mymethods,method.getName().substring(3)) ) {
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


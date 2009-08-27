package net.yura.tools.mobilegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * @author Lenin
 */
public class MobileGen {

    public static void main(String ... args) throws Exception {
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
ArrayList<Class> classes = new ArrayList<Class>();
classes.add(Test.class);
classes.add(TestObject.class);



PrintStream ps = new PrintStream(new File("src/net/yura/mobile/gen/XMLAccess.java"));

int n = 0;



ps.println("package net.yura.mobile.gen;");

for (Class c:classes) {
ps.println("import "+c.getName()+";");
}

ps.println("import java.util.Hashtable;");
ps.println("import java.util.Vector;");
ps.println("import org.kxml2.io.KXmlParser;");
ps.println("import org.xmlpull.v1.XmlSerializer;");
ps.println("import java.io.IOException;");
ps.println("import net.yura.mobile.io.XMLUtil;");

ps.println("/**");
ps.println(" * THIS FILE IS GENERATED, DO NOT EDIT");
ps.println(" */");
ps.println("public class XMLAccess extends XMLUtil {");

ps.println("    public XMLAccess() {");
ps.println("    }");

ps.println("    public void saveObject(XmlSerializer serializer,Object object) throws IOException {");

n=0;
for (Class c:classes) {

String className = c.getSimpleName();

ps.println("        "+ ((n==0)?"":"else ") +"if (object instanceof "+className+") {");
ps.println("            serializer.startTag(null,\""+className+"\");");
ps.println("            save"+className+"(serializer,("+className+")object);");
ps.println("            serializer.endTag(null,\""+className+"\");");
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

ps.println("    public Object readObject(KXmlParser parser) throws Exception {");

ps.println("        String name = parser.getName();");

n=0;
for (Class c:classes) {

String className = c.getSimpleName();

ps.println("        "+ ((n==0)?"":"else ") +"if (\""+className+"\".equals(name)) {");
ps.println("            return read"+className+"(parser);");
ps.println("        }");

n++;
}

ps.println("        else {");
ps.println("            return super.readObject(parser);");
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

ps.println("    public void save"+className+"(XmlSerializer serializer,"+className+" object) throws IOException {");

ArrayList<Method> simpleMethods = getMethods(theclass,"get",true,false);
for (Method m: simpleMethods) {
ps.println("        serializer.attribute(null,\""+paramName(m)+"\", String.valueOf( object."+m.getName()+"() ) );");
}

if (theclass.getSuperclass()!=Object.class) {
ps.println("        save"+theclass.getSuperclass().getSimpleName()+"(serializer, object);");
}

ArrayList<Method> complexMethods = getMethods(theclass,"get",false,false);
for (Method m: complexMethods) {

ps.println("        serializer.startTag(null,\""+paramName(m)+"\");");

Class returntype = m.getReturnType();

if (returntype.isArray()) {
ps.println("        saveArray(serializer, object."+m.getName()+"() );");
}
else {
ps.println("        save"+returntype.getSimpleName()+"(serializer, object."+m.getName()+"() );");
}

ps.println("        serializer.endTag(null,\""+paramName(m)+"\");");

}

ps.println("    }");

    }

    public static void printLoadMethod(PrintStream ps,Class theclass) {

String className = theclass.getSimpleName();

ps.println("    private "+className+" read"+className+"(KXmlParser parser) throws Exception {");

ps.println("        "+className+" object = new "+className+"();");

//ArrayList<Method> simpleMethods = getMethods(theclass,"set",true);
//for (Method m: simpleMethods) {
//String value = paramName(m);
//ps.println("        String "+value+" = parser.getAttributeValue(null, \""+paramName(m)+"\");");
//Class param = m.getParameterTypes()[0];
//    if (param == String.class) {
//    ps.println("        object."+m.getName()+"("+value+");");
//    }
//    else if (param == int.class) {
//    ps.println("        object."+m.getName()+"( Integer.parseInt("+value+") );");
//    }
//    else if (param == double.class) {
//    ps.println("        object."+m.getName()+"( Double.parseDouble("+value+") );");
//    }
//    else if (param == float.class) {
//    ps.println("        object."+m.getName()+"( Float.parseFloat("+value+") );");
//    }
//    else if (param == boolean.class) {
//    ps.println("        object."+m.getName()+"( \"true\".equals( "+value+" ) );");
//    }
//    else if (param == short.class) {
//    ps.println("        object."+m.getName()+"( Short.parseShort( "+value+" ) );");
//    }
//    else if (param == long.class) {
//    ps.println("        object."+m.getName()+"( Long.parseLong( "+value+" ) );");
//    }
//    else if (param == char.class) {
//    ps.println("        object."+m.getName()+"( "+value+".charAt(0) );");
//    }
//    else if (param == byte.class) {
//    ps.println("        object."+m.getName()+"( Byte.parseByte("+value+") );");
//    }
//    else {
//    throw new RuntimeException();
//    }
//}


ps.println("        int count = parser.getAttributeCount();");
ps.println("        for (int c=0;c<count;c++) {");

ps.println("            String key = parser.getAttributeName(c);");
ps.println("            String value = parser.getAttributeValue(c);");

ArrayList<Method> simpleMethods = getMethods(theclass,"set",true,true);
int n=0;
for (Method m: simpleMethods) {
Class param = m.getParameterTypes()[0];
ps.println("            "+(n==0?"":"else ")+"if (\""+paramName(m)+"\".equals(key)) {");
    if (param == String.class) {
    ps.println("                object."+m.getName()+"(value);");
    }
    else if (param == int.class) {
    ps.println("                object."+m.getName()+"( Integer.parseInt(value) );");
    }
    else if (param == double.class) {
    ps.println("                object."+m.getName()+"( Double.parseDouble(value) );");
    }
    else if (param == float.class) {
    ps.println("                object."+m.getName()+"( Float.parseFloat(value) );");
    }
    else if (param == boolean.class) {
    ps.println("                object."+m.getName()+"( \"true\".equals( value ) );");
    }
    else if (param == short.class) {
    ps.println("                object."+m.getName()+"( Short.parseShort( value ) );");
    }
    else if (param == long.class) {
    ps.println("                object."+m.getName()+"( Long.parseLong( value ) );");
    }
    else if (param == char.class) {
    ps.println("                object."+m.getName()+"( value.charAt(0) );");
    }
    else if (param == byte.class) {
    ps.println("                object."+m.getName()+"( Byte.parseByte(value) );");
    }
    else {
    throw new RuntimeException();
    }
ps.println("            }");
n++;
if (n==simpleMethods.size()) {
ps.println("            else {");
ps.println("                System.out.println(\"unknown item found \"+key);");
ps.println("            }");
}
}

ps.println("        }");


ArrayList<Method> complexMethods = getMethods(theclass,"set",false,true);

if (complexMethods.size() > 0) {
ps.println("        while (parser.nextTag() != KXmlParser.END_TAG) {");
ps.println("            String name = parser.getName();");
n=0;
for (Method m: complexMethods) {
Class param = m.getParameterTypes()[0];
ps.println("            "+(n==0?"":"else ")+"if (\""+paramName(m)+"\".equals(name)) {");
if (param.isArray()) {
ps.println("                Vector numbers = readVector(parser);");
ps.println("                String[] array = new String[numbers.size()];");
ps.println("                numbers.copyInto(array);");
ps.println("                object.setNumbers(array);");
}
else  {
ps.println("                object."+m.getName()+"( read"+param.getSimpleName()+"(parser) );");
}
ps.println("            }");
n++;
if (n==complexMethods.size()) {
ps.println("            else {");
ps.println("                System.out.println(\"unknown section: \"+name);");
ps.println("                parser.skipSubTree();");
ps.println("            }");
}
}


ps.println("        }");
}
else {
ps.println("        parser.skipSubTree();");
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

    private static ArrayList<Method> getMethods(Class theclass, String string, boolean simple,boolean incsuper) {
        Method[] mymethods = theclass.getDeclaredMethods();
        ArrayList<Method> result = new ArrayList<Method>();
        for (Method method:mymethods) {
            if ("get".equals(string) && method.getName().startsWith("get") && simple == isSimpleType(method.getReturnType()) ) {
                result.add(method);
            }
            else if ("set".equals(string) && method.getName().startsWith("set") && simple == isSimpleType(method.getParameterTypes()[0]) ) {
                result.add(method);
            }
        }

        if (incsuper && theclass.getSuperclass() != Object.class) {
            ArrayList<Method> result2 = getMethods(theclass.getSuperclass(), string, simple, incsuper);
            result.addAll(result2);
        }

        return result;
    }
    private static String paramName(Method m) {
        String n = m.getName();
        return Character.toLowerCase( n.charAt(3) ) + n.substring(4);
    }

    private static boolean isSimpleType(Class c) {
        return  c.isPrimitive() || c == String.class;
    }
}
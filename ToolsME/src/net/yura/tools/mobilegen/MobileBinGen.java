package net.yura.tools.mobilegen;

import net.yura.tools.mobilegen.model.TestObject;
import net.yura.tools.mobilegen.model.Test;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;
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
public class MobileBinGen extends BaseGen {

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
PrintStream ps = new PrintStream( new File(getGeneratedFile())); //new File("src/net/yura/mobile/gen/BinAccess.java"));

int n = 0;



ps.println("package net.yura.mobile.gen;");

for (Class c:classes) {
ps.println("import "+c.getName()+";");
}

ps.println("import java.util.Hashtable;");
ps.println("import java.util.Vector;");
ps.println("import java.io.IOException;");
ps.println("import net.yura.mobile.io.BinUtil;");
ps.println("import java.io.DataOutputStream;");
ps.println("import java.io.DataInputStream;");

ps.println("/**");
ps.println(" * THIS FILE IS GENERATED, DO NOT EDIT");
ps.println(" */");
ps.println("public class BinAccess extends BinUtil {");

n = 0;
for (Class c:classes) {
ps.println("    public static final int TYPE_"+c.getSimpleName().toUpperCase()+"="+(20+n)+";");
n++;
}

ps.println("    public BinAccess() {");
ps.println("    }");

ps.println("    public void writeObject(DataOutputStream out, Object object) throws IOException {");

n=0;
for (Class c:classes) {

String className = c.getSimpleName();

ps.println("        "+ ((n==0)?"":"else ") +"if (object instanceof "+className+") {");
ps.println("            out.writeInt(TYPE_"+className.toUpperCase()+");");
ps.println("            save"+className+"(out,("+className+")object);");
ps.println("        }");

n++;
}


ps.println("        else {");
ps.println("            super.writeObject(out, object);");
ps.println("        }");

ps.println("    }");

for (Class c:classes) {
printSaveMethod(ps,c);
}

ps.println("    public Object readObject(DataInputStream in,int type,int size) throws IOException {");

ps.println("        switch (type) {");


n=0;
for (Class c:classes) {

String className = c.getSimpleName();

ps.println("            case TYPE_"+className.toUpperCase()+": return read"+className+"(in,size);");

n++;
}

ps.println("            default: return super.readObject(in,type,size);");
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

ps.println("    public void save"+className+"(DataOutputStream out,"+className+" object) throws IOException {");

ArrayList<Method> simpleMethods = getMethods(theclass,"get",true);

ps.println("        out.writeInt("+simpleMethods.size()+");");

for (Method m: simpleMethods) {


Class param = m.getReturnType();

if (param == String.class) {
ps.println("        {");
ps.println("            String string = object."+m.getName()+"();");
ps.println("            if (string!=null) {");
ps.println("                out.writeInt( TYPE_STRING);");
ps.println("                out.writeUTF( string );");
ps.println("            }");
ps.println("            else {");
ps.println("                out.writeInt( TYPE_NULL);");
ps.println("            }");
ps.println("        }");
}
else if (param == int.class) {
ps.println("        out.writeInt( TYPE_INTEGER);");
ps.println("        out.writeInt( object."+m.getName()+"() );");
}
else if (param == double.class) {
ps.println("        out.writeInt( TYPE_DOUBLE);");
ps.println("        out.writeDouble( object."+m.getName()+"() );");
}
else if (param == float.class) {
ps.println("        out.writeInt( TYPE_FLOAT);");
ps.println("        out.writeFloat( object."+m.getName()+"() );");
}
else if (param == boolean.class) {
ps.println("        out.writeInt( TYPE_BOOLEAN);");
ps.println("        out.writeBoolean( object."+m.getName()+"() );");
}
else if (param == short.class) {
ps.println("        out.writeInt( TYPE_SHORT);");
ps.println("        out.writeShort( object."+m.getName()+"() );");
}
else if (param == long.class) {
ps.println("        out.writeInt( TYPE_LONG);");
ps.println("        out.writelong( object."+m.getName()+"() );");
}
else if (param == char.class) {
ps.println("        out.writeInt( TYPE_CHAR);");
ps.println("        out.writeChar( object."+m.getName()+"() );");
}
else if (param == byte.class) {
ps.println("        out.writeInt( TYPE_BYTE);");
ps.println("        out.writeByte( object."+m.getName()+"() );");
}

else if (param == Vector.class) {
ps.println("        {");
ps.println("            Vector vector = object."+m.getName()+"();");
ps.println("            if (vector!=null) {");
ps.println("                out.writeInt( TYPE_VECTOR);");
ps.println("                writeVector( out, vector );");
ps.println("            }");
ps.println("            else {");
ps.println("                out.writeInt( TYPE_NULL);");
ps.println("            }");
ps.println("        }");
}
else if (param == Hashtable.class) {
ps.println("        {");
ps.println("            Hashtable hashtable = object."+m.getName()+"();");
ps.println("            if (hashtable!=null) {");
ps.println("                out.writeInt( TYPE_HASHTABLE);");
ps.println("                writeHashtable( out, hashtable );");
ps.println("            }");
ps.println("            else {");
ps.println("                out.writeInt( TYPE_NULL);");
ps.println("            }");
ps.println("        }");
}
else if (param == byte[].class) {
ps.println("        {");
ps.println("            byte[] bytes = object."+m.getName()+"();");
ps.println("            if (bytes!=null) {");
ps.println("                out.writeInt( TYPE_BYTE_ARRAY);");
ps.println("                writeBytes( out, bytes );");
ps.println("            }");
ps.println("            else {");
ps.println("                out.writeInt( TYPE_NULL);");
ps.println("            }");
ps.println("        }");
}
else if (param.isArray()) {
ps.println("        {");
ps.println("            Object[] array = object."+m.getName()+"();");
ps.println("            if (array!=null) {");
ps.println("                out.writeInt( TYPE_ARRAY);");
ps.println("                writeArray( out, array );");
ps.println("            }");
ps.println("            else {");
ps.println("                out.writeInt( TYPE_NULL);");
ps.println("            }");
ps.println("        }");
}
else {
ps.println("        writeObject(out, object."+m.getName()+"() );");
}

}

ps.println("    }");

    }

    public static void printLoadMethod(PrintStream ps,Class theclass) {

String className = theclass.getSimpleName();

ps.println("    private "+className+" read"+className+"(DataInputStream in,int size) throws IOException {");

ps.println("        "+className+" object = new "+className+"();");

ArrayList<Method> methods = getMethods(theclass,"set",true);

for (Method m: methods) {
Class param = m.getParameterTypes()[0];

if (param == String.class) {
ps.println("        if (checkType(in.readInt() , TYPE_STRING)) {");
ps.println("            object."+m.getName()+"( in.readUTF() );");
ps.println("        }");
}
else if (param == int.class) {
ps.println("        checkType(in.readInt() , TYPE_INTEGER);");
ps.println("        object."+m.getName()+"( in.readInt() );");
}
else if (param == double.class) {
ps.println("        checkType(in.readInt() , TYPE_DOUBLE);");
ps.println("        object."+m.getName()+"( in.readDouble() );");
}
else if (param == float.class) {
ps.println("        checkType(in.readInt() , TYPE_FLOAT);");
ps.println("        object."+m.getName()+"( in.readFloat() );");
}
else if (param == boolean.class) {
ps.println("        checkType(in.readInt() , TYPE_BOOLEAN);");
ps.println("        object."+m.getName()+"( in.readBoolean() );");
}
else if (param == short.class) {
ps.println("        checkType(in.readInt() , TYPE_SHORT);");
ps.println("        object."+m.getName()+"( in.readShort() );");
}
else if (param == long.class) {
ps.println("        checkType(in.readInt() , TYPE_LONG);");
ps.println("        object."+m.getName()+"( in.readLong() );");
}
else if (param == char.class) {
ps.println("        checkType(in.readInt() , TYPE_CHAR);");
ps.println("        object."+m.getName()+"( in.readChar() );");
}
else if (param == byte.class) {
ps.println("        checkType(in.readInt() , TYPE_BYTE);");
ps.println("        object."+m.getName()+"( in.readByte() );");
}

else if (param == Vector.class) {
ps.println("        if (checkType(in.readInt() , TYPE_VECTOR)) {");
ps.println("            object."+m.getName()+"( readVector(in) );");
ps.println("        }");
}
else if (param == Hashtable.class) {
ps.println("        if (checkType(in.readInt() , TYPE_HASHTABLE)) {");
ps.println("            object."+m.getName()+"( readHashtable(in) );");
ps.println("        }");
}
else if (param == byte[].class) {
ps.println("        if (checkType(in.readInt() , TYPE_BYTE_ARRAY)) {");
ps.println("            object."+m.getName()+"( readBytes(in) );");
ps.println("        }");
}
else if (param.isArray()) {
ps.println("        if (checkType(in.readInt() , TYPE_ARRAY)) {");
ps.println("            Object[] objects = readArray(in);");
ps.println("            "+param.getComponentType().getSimpleName()+"[] array = new "+param.getComponentType().getSimpleName()+"[objects.length];");
ps.println("            System.arraycopy(objects,0,array,0,objects.length);");
ps.println("            object."+m.getName()+"(array);");
ps.println("        }");
}
else {
ps.println("        object."+m.getName()+"( readObject(in) );");
}

}


ps.println("        skipUnknownObjects(in,size - "+methods.size()+");");


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

    private static ArrayList<Method> getMethods(Class theclass, String string,boolean incsuper) {
        Method[] mymethods = theclass.getDeclaredMethods();
        ArrayList<Method> result = new ArrayList<Method>();
        for (Method method:mymethods) {
            if ("get".equals(string) && method.getName().startsWith("get") ) {
                result.add(method);
            }
            else if ("set".equals(string) && method.getName().startsWith("set") ) {
                result.add(method);
            }
        }

        if (incsuper && theclass.getSuperclass() != Object.class) {
            ArrayList<Method> result2 = getMethods(theclass.getSuperclass(), string, incsuper);
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


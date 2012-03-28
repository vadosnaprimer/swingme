package net.yura.tools.mobilegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Lenin
 */
public class MobileBinGen extends BaseGen {

    private boolean idClashs(int id,ArrayList<MyClass> classes) {
        for (MyClass c:classes) {
            if (c.id == id) {
                return true;
            }
        }
        return false;
    }
    
    
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

ArrayList<MyClass> myclasses = (ArrayList<MyClass>) loadClassesFromFileRaw(getClassNamesFile());
File f = getGeneratedFile();
System.out.println("saving to file: "+f);
PrintStream ps = new PrintStream( f); //new File("src/net/yura/mobile/gen/BinAccess.java"));





ps.println("package "+getOutputPackage()+";");

for (MyClass c:myclasses) {
ps.println("import "+c.theClass.getName().replaceAll("\\$", "\\.")+";");
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
ps.println("public class "+getOutputClass()+" extends BinUtil {");

int genId = 20;
for (MyClass c:myclasses) {
    
    int id = c.id;
    if (id < 20) {
        do {
            id = genId;
            genId++;
        } while ( idClashs(id,myclasses) );
    }
    
ps.println("    public static final int TYPE_"+c.theClass.getSimpleName().toUpperCase()+"="+id+";");

}

// we do the sort here instead of when we load the files so that the IDs are in the order that the Classes are in the file
// but the instanceof checking is done in the order defined by the ClassComparator
ArrayList<Class> classes = (ArrayList<Class>)sort(myclasses);

ps.println("    public "+getOutputClass()+"() {");
ps.println("    }");

ps.println("    protected void writeObject(DataOutputStream out, Object object) throws IOException {");

int n=0;
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

ps.println("    protected Object readObject(DataInputStream in,int type,int size) throws IOException {");

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

ps.println("    protected void save"+className+"(DataOutputStream out,"+className+" object) throws IOException {");

ArrayList<Method> simpleMethods = getMethods(theclass,false);

ps.println("        out.writeInt("+simpleMethods.size()+");");

for (Method m: simpleMethods) {


Class param = m.getReturnType();

//if (param == String.class) {
//ps.println("        {");
//ps.println("            String string = object."+m.getName()+"();");
//ps.println("            if (string!=null) {");
//ps.println("                out.writeInt( TYPE_STRING);");
//ps.println("                out.writeUTF( string );");
//ps.println("            }");
//ps.println("            else {");
//ps.println("                out.writeInt( TYPE_NULL);");
//ps.println("            }");
//ps.println("        }");
//}
if (param == int.class) {
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
ps.println("        out.writeLong( object."+m.getName()+"() );");
}
else if (param == char.class) {
ps.println("        out.writeInt( TYPE_CHAR);");
ps.println("        out.writeChar( object."+m.getName()+"() );");
}
else if (param == byte.class) {
ps.println("        out.writeInt( TYPE_BYTE);");
ps.println("        out.writeByte( object."+m.getName()+"() );");
}
//
//else if (param == Vector.class) {
//ps.println("        {");
//ps.println("            Vector vector = object."+m.getName()+"();");
//ps.println("            if (vector!=null) {");
//ps.println("                out.writeInt( TYPE_VECTOR);");
//ps.println("                writeVector( out, vector );");
//ps.println("            }");
//ps.println("            else {");
//ps.println("                out.writeInt( TYPE_NULL);");
//ps.println("            }");
//ps.println("        }");
//}
//else if (param == Hashtable.class) {
//ps.println("        {");
//ps.println("            Hashtable hashtable = object."+m.getName()+"();");
//ps.println("            if (hashtable!=null) {");
//ps.println("                out.writeInt( TYPE_HASHTABLE);");
//ps.println("                writeHashtable( out, hashtable );");
//ps.println("            }");
//ps.println("            else {");
//ps.println("                out.writeInt( TYPE_NULL);");
//ps.println("            }");
//ps.println("        }");
//}
//else if (param == byte[].class) {
//ps.println("        {");
//ps.println("            byte[] bytes = object."+m.getName()+"();");
//ps.println("            if (bytes!=null) {");
//ps.println("                out.writeInt( TYPE_BYTE_ARRAY);");
//ps.println("                writeBytes( out, bytes );");
//ps.println("            }");
//ps.println("            else {");
//ps.println("                out.writeInt( TYPE_NULL);");
//ps.println("            }");
//ps.println("        }");
//}
//else if (param.isArray()) {
//ps.println("        {");
//ps.println("            Object[] array = object."+m.getName()+"();");
//ps.println("            if (array!=null) {");
//ps.println("                out.writeInt( TYPE_ARRAY);");
//ps.println("                writeArray( out, array );");
//ps.println("            }");
//ps.println("            else {");
//ps.println("                out.writeInt( TYPE_NULL);");
//ps.println("            }");
//ps.println("        }");
//}
else {
ps.println("        writeObject(out, object."+m.getName()+"() );");
}

}

ps.println("    }");

    }

    public static void printLoadMethod(PrintStream ps,Class theclass) {

String className = theclass.getSimpleName();

ps.println("    protected "+className+" read"+className+"(DataInputStream in,int size) throws IOException {");

ps.println("        "+className+" object = new "+className+"();");

ArrayList<Method> methods = getMethods(theclass,true);
int n = 0;
for (Method m: methods) {
Class param = m.getParameterTypes()[0];

ps.println("        if (size>"+n+") {");

// we can NOT do a checkType as String is a Object and CAN be of type String OR NULL!!!
//if (param == String.class) {
//ps.println("            checkType(in.readInt() , TYPE_STRING);");
//ps.println("            object."+m.getName()+"( in.readUTF() );");
//}
if (param == int.class) {
ps.println("            checkType(in.readInt() , TYPE_INTEGER);");
ps.println("            object."+m.getName()+"( in.readInt() );");
}
else if (param == double.class) {
ps.println("            checkType(in.readInt() , TYPE_DOUBLE);");
ps.println("            object."+m.getName()+"( in.readDouble() );");
}
else if (param == float.class) {
ps.println("            checkType(in.readInt() , TYPE_FLOAT);");
ps.println("            object."+m.getName()+"( in.readFloat() );");
}
else if (param == boolean.class) {
ps.println("            checkType(in.readInt() , TYPE_BOOLEAN);");
ps.println("            object."+m.getName()+"( in.readBoolean() );");
}
else if (param == short.class) {
ps.println("            checkType(in.readInt() , TYPE_SHORT);");
ps.println("            object."+m.getName()+"( in.readShort() );");
}
else if (param == long.class) {
ps.println("            checkType(in.readInt() , TYPE_LONG);");
ps.println("            object."+m.getName()+"( in.readLong() );");
}
else if (param == char.class) {
ps.println("            checkType(in.readInt() , TYPE_CHAR);");
ps.println("            object."+m.getName()+"( in.readChar() );");
}
else if (param == byte.class) {
ps.println("            checkType(in.readInt() , TYPE_BYTE);");
ps.println("            object."+m.getName()+"( in.readByte() );");
}

//else if (param == Vector.class) {
//ps.println("        if (checkType(in.readInt() , TYPE_VECTOR)) {");
//ps.println("            object."+m.getName()+"( readVector(in) );");
//ps.println("        }");
//}
//else if (param == Hashtable.class) {
//ps.println("        if (checkType(in.readInt() , TYPE_HASHTABLE)) {");
//ps.println("            object."+m.getName()+"( readHashtable(in) );");
//ps.println("        }");
//}
//else if (param == byte[].class) {
//ps.println("        if (checkType(in.readInt() , TYPE_BYTE_ARRAY)) {");
//ps.println("            object."+m.getName()+"( readBytes(in) );");
//ps.println("        }");
//}
else if (param != byte[].class && param.isArray()) {
ps.println("            Object[] objects = (Object[])readObject(in);");
ps.println("            "+param.getComponentType().getSimpleName()+"[] array=null;");
ps.println("            if (objects!=null) {");
ps.println("                array = new "+param.getComponentType().getSimpleName()+"[objects.length];");
ps.println("                System.arraycopy(objects,0,array,0,objects.length);");
ps.println("            }");
ps.println("            object."+m.getName()+"(array);");
}
else {
ps.println("            object."+m.getName()+"( ("+param.getSimpleName()+")readObject(in) );");
}
ps.println("        }");
n++;
}

ps.println("        if (size>"+n+") {");
ps.println("            skipUnknownObjects(in,size - "+methods.size()+");");
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

        /**
         * find all methods
         */
    private static ArrayList<Method> getMethods(Class theclass, boolean set) {
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

        if ( theclass.getSuperclass() != Object.class) {
            ArrayList<Method> result2 = getMethods(theclass.getSuperclass(), set);
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


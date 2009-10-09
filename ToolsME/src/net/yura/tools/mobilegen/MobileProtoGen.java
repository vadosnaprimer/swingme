package net.yura.tools.mobilegen;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import net.yura.tools.mobilegen.ProtoLoader.EnumDefinition;
import net.yura.tools.mobilegen.ProtoLoader.MessageDefinition;

/**
 * @author Yura Mamyrin
 */
public class MobileProtoGen extends BaseGen {

    String protoSource    = null;
    String objectPackage  = null;
    String outputPackage  = null;
    String outputClass    = null;
    String sourceRoot     = null;

    public void setProtoSource( String argument ) {
	    this.protoSource   = argument;
    }
    public void setObjectPackage( String argument ) {
	    this.objectPackage = argument;
    }
    public void setOutputPackage( String argument ) {
	    this.outputPackage = argument;
    }
    public void setOutputClass( String argument ) {
	    this.outputClass   = argument;
    }
    public void setSourceRoot( String argument ) {
	    this.sourceRoot    = argument;
    }

    @Override
    public void doGen() throws Exception {




ProtoLoader loader = new ProtoLoader();
loader.process(protoSource, objectPackage);

enumDefs = loader.getEnumDefs();
messageDefs = loader.getMessageDefs();


StringBuffer tmp = new StringBuffer();
tmp.append( sourceRoot );
tmp.append( File.separator );
tmp.append( this.outputPackage.replace( "." , File.separator ) );
tmp.append( File.separator );
tmp.append( this.outputClass );
tmp.append( ".java" );
PrintStream ps = new PrintStream( new File( tmp.toString() ) );


ps.println("package net.yura.mobile.gen;");

//for (Class c:classes) {
ps.println("import "+this.objectPackage+".*;");
//}

ps.println("import java.util.Hashtable;");
ps.println("import java.util.Vector;");
ps.println("import java.io.IOException;");
ps.println("import net.yura.mobile.io.ProtoUtil;");
ps.println("import net.yura.mobile.io.proto.CodedOutputStream;");
ps.println("import net.yura.mobile.io.proto.CodedInputStream;");

ps.println("/**");
ps.println(" * THIS FILE IS GENERATED, DO NOT EDIT");
ps.println(" */");
ps.println("public class ProtoAccess extends ProtoUtil {");

printBody(ps);

ps.println("}");

    }

Hashtable<String,EnumDefinition> enumDefs;
Hashtable<String,MessageDefinition> messageDefs;


    public void printBody(PrintStream ps) {


EnumDefinition edef = enumDefs.get("ObjectType");

Set<Map.Entry<String,Integer>> set = edef.getValues().entrySet();
for (Map.Entry<String,Integer> enu:set) {
    int num = enu.getValue();
    if (num >= 20) {
ps.println("    public static final int "+enu.getKey()+"="+num+";");
    }
}

ps.println("    public ProtoAccess() {");
ps.println("    }");


printEnummethod(ps);

/*
ps.println("    protected void writeObject(DataOutputStream out, Object object) throws IOException {");

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


//        saveToFile(new File("output.java"), stringbuilder.toString());
*/
    }

    private MessageDefinition getMessageFromEnum(String key) {
        String name = key.substring(5).replaceAll("\\_", ""); // remove the "TYPE_"
        MessageDefinition md = messageDefs.get(name);
        if (md==null) throw new RuntimeException("no message found for type "+key);
        return md;
    }

    private void printEnummethod(PrintStream ps) {

        Set<String> keys = enumDefs.keySet();

        for (String name:keys) {
            EnumDefinition edef = enumDefs.get(name);
            Set<Map.Entry<String,Integer>> set = edef.getValues().entrySet();

            if ("ObjectType".equals(name) ) {

//ps.println("    @Override");
ps.println("    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {");
ps.println("        switch (type) {");



for (Map.Entry<String,Integer> enu:set) {
    int num = enu.getValue();
    if (num >= 20) {
ps.println("            case "+enu.getKey()+": return decode"+getMessageFromEnum(enu.getKey()).getName()+"(in2);");
    }
}


ps.println("            default: return super.decodeObject(in2, type);");
ps.println("        }");
ps.println("    }");
//ps.println("    @Override");
ps.println("    protected int computeObjectSize(Object obj,int type) {");
ps.println("        switch (type) {");



for (Map.Entry<String,Integer> enu:set) {
    int num = enu.getValue();
    if (num >= 20) {
ps.println("            case "+enu.getKey()+": return compute"+getMessageFromEnum(enu.getKey()).getName()+"Size( ("+getMessageFromEnum(enu.getKey()).getImplementation().getSimpleName()+")obj );");
    }
}

ps.println("            default: return super.computeObjectSize(obj,type);");
ps.println("        }");
ps.println("    }");
//ps.println("    @Override");
ps.println("    protected void encodeObject(CodedOutputStream out, Object obj,int type) throws IOException {");
ps.println("        switch (type) {");


for (Map.Entry<String,Integer> enu:set) {
    int num = enu.getValue();
    if (num >= 20) {
ps.println("            case "+enu.getKey()+": encode"+getMessageFromEnum(enu.getKey()).getName()+"( out, ("+getMessageFromEnum(enu.getKey()).getImplementation().getSimpleName()+")obj ); break;");
    }
}

ps.println("            default: super.encodeObject(out,obj,type); break;");
ps.println("        }");
ps.println("    }");
//ps.println("    @Override");
ps.println("    protected int getObjectTypeEnum(Object obj) {");

ps.println("        if (obj instanceof Hashtable) {");
ps.println("            Hashtable table = (Hashtable)obj;");


for (Map.Entry<String,Integer> enu:set) {
    int num = enu.getValue();
    if (num >= 20 && getMessageFromEnum(enu.getKey()).getImplementation() == Hashtable.class) {
ps.print("            if (table.size() == "+getMessageFromEnum(enu.getKey()).getFields().size());

Vector<ProtoLoader.FieldDefinition> fields = getMessageFromEnum(enu.getKey()).fields;
for (ProtoLoader.FieldDefinition field:fields) {
ps.print(" && table.get(\""+field.getName()+"\")!=null");
}

ps.println(") {");
ps.println("                return "+enu.getKey()+";");
ps.println("            }");
    }
}


ps.println("        }");

for (Map.Entry<String,Integer> enu:set) {
    int num = enu.getValue();
    if (num >= 20 && getMessageFromEnum(enu.getKey()).getImplementation() != Hashtable.class) {
ps.println("        if (obj instanceof "+getMessageFromEnum(enu.getKey()).getImplementation().getSimpleName()+") {");
ps.println("            return "+enu.getKey()+";");
ps.println("        }");
    }
}

ps.println("        return super.getObjectTypeEnum(obj);");

ps.println("    }");

            }
            else {

ps.println("    private int get"+name+"Enum(String enu) {");

for (Map.Entry<String,Integer> enu:set) {
ps.println("        if (\""+enu.getKey()+"\".equals(enu)) return "+enu.getValue()+";");
}

ps.println("        return -1;");
ps.println("    }");
ps.println("    private String get"+name+"String(int i) {");
ps.println("        switch (i) {");

for (Map.Entry<String,Integer> enu:set) {
ps.println("            case "+enu.getValue()+": return \""+enu.getKey()+"\";");
}

ps.println("            default: return \"unknown \"+i;");
ps.println("        }");
ps.println("    }");

            }

        }
    }

/*
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

//if (param == String.class) {
//ps.println("        if (checkType(in.readInt() , TYPE_STRING)) {");
//ps.println("            object."+m.getName()+"( in.readUTF() );");
//ps.println("        }");
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
*/
}

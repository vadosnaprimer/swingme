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
import net.yura.tools.mobilegen.ProtoLoader.FieldDefinition;
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
PrintStream ps = new PrintStream( new File( tmp.toString() ) ) {
    private int indent=0;
    @Override
    public void println(String string) {
        int open = string.indexOf('{');
        int close = string.indexOf('}');
        if (close >=0 && (open<0 || close<open)) {
            indent--;
        }
        super.println( "                                ".substring(0, indent*4) + string.trim() );
        if (open >=0 &&(close<0 || open>close)) {
            indent++;
        }
    }
};


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
ps.println("import net.yura.mobile.io.proto.WireFormat;");

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

ps.println("    public ProtoAccess() { }");


printEnummethod(ps);

Collection<MessageDefinition> messages = messageDefs.values();


// #############################################################################
// ############################## compute ######################################
// #############################################################################

for (MessageDefinition message:messages) {

ps.println("    private int compute"+message.getName()+"Size("+message.getImplementation().getSimpleName()+" object) {");
ps.println("        int size=0;");

printSaveComputeMethod(ps,message,true);

ps.println("        return size;");
ps.println("    }");

}

// #############################################################################
// ############################### encode ######################################
// #############################################################################

for (MessageDefinition message:messages) {

ps.println("    private void encode"+message.getName()+"(CodedOutputStream out, "+message.getImplementation().getSimpleName()+" object) throws IOException {");

printSaveComputeMethod(ps,message,false);

ps.println("    }");

}

// #############################################################################

printLoadMethods(ps);

    }



    public void printSaveComputeMethod(PrintStream ps,MessageDefinition message,boolean calc) {

Vector<ProtoLoader.FieldDefinition> fields = message.getFields();
for (ProtoLoader.FieldDefinition field:fields) {
    int fieldId = field.getID();

    if (message.getImplementation() != Hashtable.class) {

        if (field.repeated) {
            if (field.getImplementation() == Vector.class) {
ps.println("        Vector vector = object.get"+field.getName()+"();");
ps.println("        for (int c=0;c<vector.size();c++) {");
ps.println("            Object obj = vector.elementAt(c);");
            }
            else { // must be a array
ps.println("        "+field.getImplementation().getComponentType().getSimpleName()+"[] array = object.get"+firstUp(field.getName())+"();");
ps.println("        for (int c=0;c<array.length;c++) {");
ps.println("            "+field.getImplementation().getComponentType().getSimpleName()+" obj = array[c];");
            }
            if (field.packed) {
printSaveCalcField(ps,field,calc);
            }
            else {
printSaveCalcField(ps,field,calc);
            }
ps.println("        }");
        }
        else {
            if (field.required) {
printSaveCalcField(ps,field,calc);
            }
            else { // options
ps.println("        if (object.get"+firstUp(field.getName())+"()!=null) {");
printSaveCalcField(ps,field,calc);
ps.println("        }");
            }
        }
    }
    else { // hashtable


    }
}


    }

    private void printSaveCalcField(PrintStream ps, FieldDefinition field,boolean calc) {

        if (calc) {

Class param = field.getImplementation();
if (param.isPrimitive() || param == String.class) {
ps.println("        size = size + CodedOutputStream.compute"+firstUp(field.getType())+"Size("+field.getID()+", object.get"+firstUp(field.getName())+"() );");
}
else {
ps.println("         int s = compute"+param.getSimpleName()+"Size(object.get"+firstUp(field.getName())+"() );");
ps.println("         size = size + CodedOutputStream.computeBytesSize("+field.getID()+", s);");
}
        }
        else {

Class param = field.getImplementation();
if (param.isPrimitive() || param == String.class) {
ps.println("        out.write"+firstUp(field.getType())+"("+field.getID()+", object.get"+firstUp(field.getName())+"() );");
}
else {
ps.println("        write"+param.getSimpleName()+"( out, "+field.getID()+", object.get"+firstUp(field.getName())+"() );");
}
        }
    }















    public void printLoadMethods(PrintStream ps) {

        Collection<MessageDefinition> messages = messageDefs.values();

for (MessageDefinition message:messages) {

ps.println("    private "+message.getImplementation().getSimpleName()+" decode"+message.getName()+"(CodedInputStream in2) {");


ps.println("        "+message.getImplementation().getSimpleName()+" object = new "+message.getImplementation().getSimpleName()+"();");

ps.println("        while (!in2.isAtEnd()) {");
ps.println("            int tag = in2.readTag();");
ps.println("            int fieldNo = WireFormat.getTagFieldNumber(tag);");
ps.println("            int wireType = WireFormat.getTagWireType(tag);");
    //System.out.println("read field "+fieldNo );
    //System.out.println("wire type "+wireType );

Vector<ProtoLoader.FieldDefinition> fields = message.getFields();

ps.println("            switch(fieldNo) {");

for (ProtoLoader.FieldDefinition field:fields) {

ps.println("                case "+field.getID()+": {");

if (message.getImplementation() != Hashtable.class) {
System.out.println(message+" "+field);
    if (field.getImplementation().isPrimitive() || field.getImplementation() == String.class) {
    ps.println("                object.set"+firstUp(field.getName())+"( in2.read"+firstUp(field.getType())+"() );");
    }
    else {
    ps.println("                int size = in2.readBytesSize();");
    ps.println("                int lim = in2.pushLimit(size);");
                    //System.out.println("object size "+size);
    if (field.getImplementation() == Object.class) {
    ps.println("                object.set"+firstUp(field.getName())+"( decodeAnonymousObject(in2);");
    }
    else {
    ps.println("                object.set"+firstUp(field.getName())+"( decode"+field.getImplementation().getSimpleName()+"(in2) );");
    }
    ps.println("                vector.addElement(obj);");
    ps.println("                in2.popLimit(lim);");
    }
}
else {

    // todo readding into hashtable
}
ps.println("                    break;");
ps.println("                }");
}
ps.println("            }");

ps.println("        }");

ps.println("        return object;");
ps.println("    }");

}

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



    private String firstUp(String type) {
        return Character.toUpperCase( type.charAt(0) ) + type.substring(1);
    }

    private MessageDefinition getMessageFromEnum(String key) {
        String name = key.substring(5).replaceAll("\\_", ""); // remove the "TYPE_"
        MessageDefinition md = messageDefs.get(name);
        if (md==null) throw new RuntimeException("no message found for type "+key);
        return md;
    }

}

package net.yura.tools.mobilegen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProtoLoader {

        private String[] objectPackage;

        private Hashtable<String,EnumDefinition> enumDefs = new Hashtable<String,EnumDefinition>();
        private Hashtable<String,MessageDefinition> messageDefs = new Hashtable<String,MessageDefinition>();

        public Hashtable<String, EnumDefinition> getEnumDefs() {
            return enumDefs;
        }

        public Hashtable<String, MessageDefinition> getMessageDefs() {
            return messageDefs;
        }

    	public void process(String name,String[] objectPackage) throws IOException {

            this.objectPackage = objectPackage;

	    Vector<Struct> raw = parseProto(name);
            parseRaw(raw);
	}

        private String[] ignoreNames = new String[] {"Object","Vector","Array","Hashtable","KeyValue","String","Integer","Double","Float","Boolean","Short","Long","Character","Byte","ByteArray"};

        private boolean ignoreMessage(String name) {

            for (int c=0;c<ignoreNames.length;c++) {
                if (ignoreNames[c].equals(name)) {
                    return true;
                }
            }

            return false;
        }

        static class Struct {
            public String comment;
            public String value;

            @Override
            public String toString() {
                return value+" - COMMENT: "+comment;
            }
            
        }
        
	private Vector<Struct> parseProto(String protoSource) throws IOException {

	        BufferedReader r = new BufferedReader( new FileReader( protoSource ) );
	        String line      = null,myComment=null;
	        StringBuffer sb  = null,comment=null;
	        Vector<Struct> raw = new Vector<Struct>();
                
	        while( ( line = r.readLine() ) != null ) {

	            line = line.replaceAll( "\\s+"        , " " );
	            line = line.trim();

                    if ( line.startsWith( "//" ) ) {
                        if (comment==null) {
                            comment = new StringBuffer(line);
                        }
                        else {
                            comment.append(" ");
                            comment.append(line);
                        }
                    }
                    else {
                        line = line.replaceAll( "[\\/]{2}.*$" , ""  );

                        if ( line.startsWith( "package" ) || line.startsWith( "option " )) { // be careful not to dump "optional"
                            // Ignored
                            System.out.println("IGNORE LINE "+line);
                        }
                        else if ( line.startsWith( "enum" ) || line.startsWith( "message" )) {
                            if ( sb != null )
                                newStruct(raw, sb.toString(), myComment);

                            // start new Object
                            myComment = comment!=null?comment.toString():null;
                            sb = new StringBuffer(line);

                            // clear the next Objects comment
                            comment = null;
                        }
                        else if ( line.length() != 0 ) {
                            if ( sb != null ) {
                                sb.append( line );
                                sb.append( " " );
                                if (line.contains("}")) {
                                    comment = null; // if we have some comment from inside the message, del it as it does not belong to anything
                                }
                            }
                            else {
                                throw new RuntimeException("unexpected line "+line);
                            }
                        }
                    }
	        }
	        r.close();

                if ( sb != null )
                    newStruct(raw, sb.toString(), myComment);

                return raw;
	}
        
    static void newStruct(Vector<Struct> v,String value,String comment) {
        
        Struct struct = new Struct();
        
        value = value.replace( "\\{" , " {" ); // no idea why this is here?!
        
        struct.value = value;
        struct.comment = comment;

        v.add(struct);
    }

    private void parseRaw(Vector<Struct> raw) throws IOException {

        for( Enumeration<Struct> e = raw.elements() ; e.hasMoreElements() ; )
        {
            Struct s = e.nextElement();
                if ( s.value.startsWith( "enum" ) )
                    parseEnum(s.value,s.comment);
        }
        for( Enumeration<Struct> e = raw.elements() ; e.hasMoreElements() ; )
        {
            Struct s = e.nextElement();
                if ( s.value.startsWith( "message" ) )
                    parseMessage(s.value,s.comment);
        }
    }


    private void parseMessage( String msg, String comment ) throws IOException
    {
        // Find message name and fields using regex
        StringBuffer regex = new StringBuffer();

        regex.append( "message" ); // Keyword "message"
        regex.append( "\\s+?" );   // One or more whitespace characters
        regex.append( "(\\w+?)" ); // Message name parameter
        regex.append( "\\s+?" );   // One or more whitespace characters
        regex.append( "\\{" );     // Opening definition brace
        regex.append( "(.*)" );    // Field definitions
        regex.append( "\\}" );     // Closing definition brace

        Pattern parsingPattern = Pattern.compile( regex.toString() , Pattern.DOTALL );
        Matcher parsingMatcher = parsingPattern.matcher(msg);

        if ( !parsingMatcher.find() )
            throw new IOException( "ERROR : Unable to parse message." );

        String name   = parsingMatcher.group(1);
        String fields = parsingMatcher.group(2);
        name   = ( name   == null ? "" : name.trim()   );
        fields = ( fields == null ? "" : fields.trim() );


        // If the message name is "Prefix" or "Suffix" , ignore it
        // Prefix is used to define object types   (and is not represented by a message type in the engine)
        // Suffix is used to define end of objects (and is not represented by a message type in the engine)

        if ( ignoreMessage(name) ) {
            return;
        }

        //if ( !name.toLowerCase().equals("prefix") && !name.toLowerCase().equals("suffix") )
        //{
            MessageDefinition md = new MessageDefinition( name );
            md.comment = comment;

            // Attempt to get a class definition
            boolean found=false;
            for (String op:this.objectPackage) {
                System.out.println( "Looking for class " + op + "." + name);
                try {
                    Class c = Class.forName( op + "." + name );
                    md.setImplementation(c);
                    System.out.println( " - Found class " + op + "." + name);
                    found = true;
                    break;
                }
                catch( ClassNotFoundException cnfe ) {
                    //cnfe.printStackTrace();
                }
            }
            if (!found) {
                    System.out.println( " - Unable to find class for " + name);
                    md.setImplementation(Hashtable.class);
            }

            // Split fields
            //System.out.println( "Processing : Message " + name + ", Fields " + fields );
            String [] fieldArray = fields.split( "\\;" );

            for( int i = 0 ; i < fieldArray.length ; i++ )
            {
                parseField( md , fieldArray[i] );
            }

            //this.messages.addElement( md );
            this.messageDefs.put( MobileProtoGen.unCamel(name) , md );
        //}
    }

    private void parseField( MessageDefinition md , String fld ) throws IOException
    {
        String tmp = fld;

        //System.out.println( "Processing : Message " + md.getName() + ", Field " + fld );

        boolean required = false;
        boolean repeated = false;
        //String  javaType = "";

        // Simply regex by removing some optional field elements

        if ( tmp.indexOf( "optional" ) != -1 ) {
            required = false;
            //tmp = tmp.replace( "optional" , "" );
        }
        else if ( tmp.indexOf( "required" ) != -1 ) {
            required = true;
            //tmp = tmp.replace( "required" , "" );
        }
        else if ( tmp.indexOf( "repeated" ) != -1 ) {
            repeated = true;
            //tmp = tmp.replace( "repeated" , "" );
        }

//        int map = tmp.indexOf( "@" );
//        if ( map != -1 )
//        {
//             javaType = tmp.substring( map + 1 ).trim();
//             tmp = tmp.substring( 0 , map );
//        }

        StringBuffer regex = new StringBuffer();

        regex.append( "(\\w+?)" );                    // Field Type
        regex.append( "\\s+?" );
        regex.append( "(\\w+?)" );                    // Field Name
        regex.append( "\\s*?" );
        regex.append( "\\=" );
        regex.append( "\\s*?" );
        regex.append( "(\\d+)" );                    // Field Index

        // Find message name and fields using regex
        Pattern parsingPattern = Pattern.compile( regex.toString() );

        Matcher parsingMatcher = parsingPattern.matcher(fld);
        if ( !parsingMatcher.find() )
            throw new IOException( "ERROR : Unable to parse field in message " + md.getName() + "." );


        String type     = parsingMatcher.group(1);
        String name     = parsingMatcher.group(2);
        String tag      = parsingMatcher.group(3);

        type     = ( type     == null ? "" : type.trim()     );
        name     = ( name     == null ? "" : name.trim()     );
        tag      = ( tag      == null ? "0" : tag.trim()     );

        FieldDefinition fd = new FieldDefinition( name );

        fd.setRequired(required);
        fd.setRepeated(repeated);
        fd.setType(type);

        EnumDefinition enu = this.enumDefs.get( type );
        if (enu!=null) {
            fd.setEnumeratedType(enu);
        }

        Class messageClass = md.getImplementation();
        Class returnType   = null;
        if ( messageClass != Hashtable.class ) {
            try {

                String methodName = "get" + MobileProtoGen.firstUp(name);

                java.lang.reflect.Method method = null;
                try {
                    method = messageClass.getMethod( methodName , (Class [])null );
                }
                catch(NoSuchMethodException ex) {
                    if (required) {
                        throw ex;
                    }
                    System.out.println("missing object method: "+methodName+" in "+messageClass);
                }

                if ( method != null ) {
                    returnType = method.getReturnType();

                    // if we use a primative to represent this in our model
                    // then we dont need to know its a enum!
                    if (enu!=null) {
                        if (returnType.isArray()) {
                            returnType = returnType.getComponentType();
                        }
                        if (returnType.isPrimitive()) {
                            fd.setType("int32");
                            fd.setEnumeratedType(null);
                        }
                    }
                }

                //if ( returnType != null )
                //{
                    //System.out.println( "Field " + fd.getName() + ", " + returnType.getCanonicalName());
                //}

                fd.setImplementation(returnType);
            }
            catch( NoSuchMethodException nsme ) {
                nsme.printStackTrace();
                // We have no idea what the return type is. so we will be setting it to null
            }
            catch( SecurityException se ) {
                se.printStackTrace();
            }
        }

        //fd.setMap( javaType );
        fd.setID( Integer.parseInt(tag) );

        md.addField(fd);

    }


    private void parseEnum( String enm, String comment ) throws IOException
    {
        StringBuffer regex = new StringBuffer();

        regex.append( "enum" );    // Keyword "enum"
        regex.append( "\\s+?" );   // One or more whitespace characters
        regex.append( "(\\w+?)" ); // Enum name parameter
        regex.append( "\\s+?" );   // One or more whitespace characters
        regex.append( "\\{" );     // Opening definition brace
        regex.append( "(.*)" );    // Field definitions
        regex.append( "\\}" );     // Closing definition brace

        Pattern parsingPattern = Pattern.compile( regex.toString() , Pattern.DOTALL );
        Matcher parsingMatcher = parsingPattern.matcher(enm);

        if ( !parsingMatcher.find() )
            throw new IOException( "ERROR : Unable to parse message." );

        String name   = parsingMatcher.group(1);
        String fields = parsingMatcher.group(2);
        name   = ( name   == null ? "" : name.trim()   );
        fields = ( fields == null ? "" : fields.trim() );

        EnumDefinition ed = new EnumDefinition( name );
        ed.comment = comment;

        String [] enumArray = fields.split( "\\;" );

        for( int i = 0 ; i < enumArray.length ; i++ )
        {
            parseEnumeratedValue( ed , enumArray[i] );
        }

        this.enumDefs.put( name , ed );
    }



    private void parseEnumeratedValue( EnumDefinition ed , String assignment ) throws IOException
    {
        StringBuffer regex = new StringBuffer();

        regex.append( "(\\w+?)" ); // Enum name parameter
        regex.append( "\\s+?" );   // One or more whitespace characters
        regex.append( "\\=" );     // Equals
        regex.append( "\\s+?" );   // One or more whitespace characters
        regex.append( "(-?" );      // allow negative values
        regex.append( "\\d+)" );  // Value

        Pattern parsingPattern = Pattern.compile( regex.toString() , Pattern.DOTALL );
        Matcher parsingMatcher = parsingPattern.matcher(assignment);

        if ( !parsingMatcher.find() )
            throw new IOException( "ERROR : Unable to parse enumerated value :" + assignment );

        String name   = parsingMatcher.group(1);
        String value  = parsingMatcher.group(2);

        name  = ( name   == null ? "" : name.trim()   );
        value = ( value  == null ? "" : value.trim() );

        int v = 0;
        try
        {
            v = Integer.parseInt( value );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            throw new IOException( "ERROR : Unable to parse enumerated value, invalid numeric :" + assignment );
        }

        System.out.println( "Adding enumerated value \"" + name + "\", value " + value );

        ed.addValue( name , v );
    }




    class MessageDefinition {

        public String comment;
        private String name;
        protected Vector<FieldDefinition> fields;
        protected Class messageClass;

        public String getName() {
            return name;
        }

        public MessageDefinition( String name )
        {
            this.name = name;
            this.fields = new Vector<FieldDefinition>();
        }

        public void setFields( Vector fields )
        {
            this.fields = fields;
        }
        public Vector<FieldDefinition> getFields()
        {
            return this.fields;
        }

        public void addField( FieldDefinition fd )
        {
            this.fields.addElement( fd );
        }

        public Class getImplementation()
        {
            return this.messageClass;
        }

        public void setImplementation( Class messageClass )
        {
            this.messageClass = messageClass;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isDeprecated() {
            return comment != null && comment.indexOf("deprecated")>=0;
        }

    }

    class EnumDefinition {
        
        public String comment;
        protected Hashtable<String,Integer> hashtable;

        public EnumDefinition( String name )
        {
            this.hashtable = new Hashtable<String,Integer>();
        }

        public void setValues( Hashtable<String,Integer> hashtable )
        {
            this.hashtable = hashtable;
        }
        public Hashtable<String,Integer> getValues()
        {
            return this.hashtable;
        }

        public void addValue( String name , int value )
        {
            this.hashtable.put( name , value );
        }

        public boolean isDeprecated() {
            return comment != null && comment.indexOf("deprecated")>=0;
        }
    }

    class FieldDefinition {
        
        public String comment;
        
        protected String type;
        protected EnumDefinition enumeratedType;
        protected Class fieldClass;

        protected int     id;
        protected String  name;
        protected boolean required;
        protected boolean packed;
        protected boolean repeated;

        public FieldDefinition() {
            this.id       = 0;
            this.name     = "";
            this.required = false;
            this.repeated = false;
            this.packed   = false;
        }

        public FieldDefinition( String name ) {
            this.name = name;
        }

        public void setRequired( boolean required )
        {
            this.required = required;
        }
        public boolean getRequired()
        {
            return this.required;
        }

        public void setRepeated( boolean repeated )
        {
            this.repeated = repeated;
        }
        public boolean getRepeated()
        {
            return this.repeated;
        }

        public void setPacked( boolean packed )
        {
            this.packed = packed;
        }
        public boolean getPacked()
        {
            return this.packed;
        }

        public void setName( String name )
        {
            this.name = name;
        }
        public String getName()
        {
            return this.name;
        }

        public int getID()
        {
            return this.id;
        }

        public void setID( int id )
        {
            this.id = id;
        }

        public String getType()
        {
            return this.type;
        }

        public void setType( String type )
        {
            this.type = type;
        }

        public EnumDefinition getEnumeratedType()
        {
            return this.enumeratedType;
        }

        public void setEnumeratedType( EnumDefinition enumeratedType )
        {
            this.enumeratedType = enumeratedType;
        }

        public Class getImplementation()
        {
            return this.fieldClass;
        }

        public void setImplementation( Class fieldClass )
        {
            this.fieldClass = fieldClass;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isDeprecated() {
            return comment != null && comment.indexOf("deprecated")>=0;
        }
    }



}

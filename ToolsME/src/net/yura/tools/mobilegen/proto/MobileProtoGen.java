package net.yura.tools.mobilegen.proto;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.tools.ant.*;

public class MobileProtoGen // extends Task
{
    String protoSource    = null;
    String objectPackage  = null;
    String outputPackage  = null;
    String outputClass    = null;
    String sourceRoot     = null; 

    String outputFileName = null;
    Vector raw            = null;
    Vector messages       = null;
    Hashtable messageDefs = null;
    Hashtable enumDefs    = null;
    
    Hashtable mapping     = null;

	public static void main( String [] args )
	{
	    int exitCode = 1;
	    
	    MobileProtoGen mpg = new MobileProtoGen( "test.proto" , 
	                                             "com.exeus.proto.generated",
	                                             "com.exeus.proto" ,
	                                             "ProtoAccess" ,
	                                             "C:\\My Work\\Current\\Badoo\\Source" );
	    
	    if ( mpg.process() )
	    {
    	    if ( !mpg.emit() )
    	        System.out.println( "Could not generate output file" );
    	    else
    	    {
    	        exitCode = 0;
    	        System.out.println( "Done" );
    	    }
    	}
    	else
    	    System.out.println( "Could not parse input file" );
    	
	}

    // BEGIN: ANT TASK 
    
    public MobileProtoGen()
    {
	    this.messages    = new Vector();
	    this.messageDefs = new Hashtable();
	    this.enumDefs    = new Hashtable();
    }

    public void setProtoSource( String argument )
    {
	    this.protoSource   = argument;
    }
    
    public void setObjectPackage( String argument )
    {
	    this.objectPackage = argument;
    }
    
    public void setOutputPackage( String argument )
    {
	    this.outputPackage = argument;
    }
    
    public void setOutputClass( String argument )
    {
	    this.outputClass   = argument;
    }
    
    public void setSourceRoot( String argument )
    {
	    this.sourceRoot    = argument;
	    StringBuffer tmp = new StringBuffer( sourceRoot );
    }

	public void execute() throws BuildException 
	{
	    StringBuffer tmp = new StringBuffer();

            tmp.append( sourceRoot );
	    tmp.append( File.separator );
	    tmp.append( this.outputPackage.replace( "." , File.separator ) );
	    tmp.append( File.separator );
	    tmp.append( this.outputClass );
	    tmp.append( ".java" );
	    this.outputFileName = tmp.toString();
	    
	    if ( process() )
	    {
    	    if ( !emit() )
    	        throw new BuildException( "Could not generate output file" );
    	}
    	else
    	    throw new BuildException( "Could not parse input file" );
    }
    
    // END: ANT TASK

	public MobileProtoGen( String protoSource , String objectPackage , String outputPackage , String outputClass , String sourceRoot  )
	{
	    this.protoSource   = protoSource;
	    this.objectPackage = objectPackage;
	    this.outputPackage = outputPackage;
	    this.outputClass   = outputClass;
	    this.sourceRoot    = sourceRoot;
	    
	    StringBuffer tmp = new StringBuffer( sourceRoot );
	    tmp.append( File.separator );
	    tmp.append( this.outputPackage.replace( "." , File.separator ) );
	    tmp.append( File.separator );
	    tmp.append( this.outputClass );
	    tmp.append( ".java" );
	    this.outputFileName = tmp.toString();
	    
	    System.out.println("Source: " + protoSource);
        System.out.println("Target: " + this.outputFileName);	    
	     
	    this.messages    = new Vector();
	    this.messageDefs = new Hashtable();
	    this.enumDefs    = new Hashtable();
	}

	public boolean process()
	{
	    boolean result = parseProto();

	    if ( result )
	        result = parseRaw();
	        
	    return result;
	}
	
	private boolean parseProto()
	{
	    boolean result = false;
	    try
	    {
	        BufferedReader r = new BufferedReader( new FileReader( this.protoSource ) );
	        String line      = null;
	        StringBuffer sb  = null;
	        this.raw         = new Vector();
	        while( ( line = r.readLine() ) != null )
	        {
	            int map = line.indexOf( "// @map " );
	            
	            if ( map > -1 )
	            {
	                // This line contains a field type mapping directive/hint. It will be of the format // @map type and always follow a field definition.
	                // We can parse this later - for now, just alter the line to save it so that it will get parsed correctly later
	                line = line.replaceAll( "\\;" , "" );
	                line = line + ";";
	                line = line.replaceAll( "\\/\\/" , "" );
	                line = line.replaceAll( "\\@map " , " @" );
	                //System.out.println(line);
	            }
	        
	            line = line.replaceAll( "\\s+"        , " " );
	            line = line.replaceAll( "[\\/]{2}.*$" , ""  );
	            line = line.trim();
	            
	            if ( line.length() == 0 )
	                line = "";
	            
	            if ( line.startsWith( "package" ) )
	            {
	                // Ignored
	            }
	            else if ( line.startsWith( "option " ) ) // be careful not to dump "optional"
	            {
	                // Ignored
	            }
	            else if ( line.startsWith( "enum" ) )
	            {
                    if ( sb != null )
                        this.raw.addElement(sb.toString());
                    
                    sb = new StringBuffer();
                    sb.append( line );
	            }
                else if ( line.startsWith( "message" ) )
                {
                    if ( sb != null )
                        this.raw.addElement(sb.toString());
                    
                    sb = new StringBuffer();
                    sb.append( line );
                }
                else if ( line.length() != 0 )
                {
                    if ( sb == null )
                        sb = new StringBuffer();
                        
                    sb.append( line );
                    sb.append( " " );
                }
	        }
	        r.close();
	        
            if ( sb != null )
                this.raw.addElement(sb.toString());
	        
	        result = true;
	    }
	    catch( Exception e )
	    {
	        System.out.println(e);
	        result = false;
	    }

        return result;	    
	}

    private boolean parseRaw()
    {
        boolean result = true;
        for( Enumeration e = raw.elements() ; e.hasMoreElements() ; )
        {
            String s = (String)e.nextElement();
            s = s.replace( "\\{" , " {" );

            try
            {
                if ( s.startsWith( "message" ) )
                    parseMessage(s);
                
                if ( s.startsWith( "enum" ) )
                    parseEnum(s);
            }
            catch( ParsingException p )
            {
                System.out.println( p );
                result = false;
            }
        }
        return result;
    }	

    // message NAME { field; field; field; field; field; }
    public void parseMessage( String msg ) throws PatternSyntaxException , ParsingException
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
            throw new ParsingException( "ERROR : Unable to parse message." );
            
        String name   = parsingMatcher.group(1);
        String fields = parsingMatcher.group(2);
        name   = ( name   == null ? "" : name.trim()   );
        fields = ( fields == null ? "" : fields.trim() ); 


        // If the message name is "Prefix" or "Suffix" , ignore it
        // Prefix is used to define object types   (and is not represented by a message type in the engine)
        // Suffix is used to define end of objects (and is not represented by a message type in the engine)

        if ( !name.toLowerCase().equals("prefix") && !name.toLowerCase().equals("suffix") )
        {
            MessageDefinition md = new MessageDefinition( name );

            // Attempt to get a class definition

            try
            {
                System.out.println( "Looking for class " + this.objectPackage + "." + name);
                Class c = Class.forName( this.objectPackage + "." + name );
                md.setImplementation(c);
            }
            catch( ClassNotFoundException cnfe )
            {
                System.out.println( "Warning - Unable to find class " + this.objectPackage + "." + name);
            }

            // Split fields
            //System.out.println( "Processing : Message " + name + ", Fields " + fields );
            String [] fieldArray = fields.split( "\\;" );    
        
            for( int i = 0 ; i < fieldArray.length ; i++ )
            {
                parseField( md , fieldArray[i] );
            }

            this.messages.addElement( md );  
            this.messageDefs.put( name , md );   
        }  
    }

    // [required|optional] [repeated] fieldtype fieldname = fieldtag [packed=true|false];
    private void parseField( MessageDefinition md , String fld ) throws PatternSyntaxException , ParsingException
    {
        String tmp = fld;
        
        //System.out.println( "Processing : Message " + md.getName() + ", Field " + fld );
        
        boolean required = false;
        boolean repeated = false;
        String  javaType = "";

        // Simply regex by removing some optional field elements

        if ( tmp.indexOf( "optional" ) != -1 )
        {
            required = false;
            tmp = tmp.replace( "optional" , "" );
        }

        if ( tmp.indexOf( "required" ) != -1 )
        {
            required = true;
            tmp = tmp.replace( "required" , "" );
        }

        if ( tmp.indexOf( "repeated" ) != -1 )
        {
            repeated = true;
            tmp = tmp.replace( "repeated" , "" );
        }
        
        int map = tmp.indexOf( "@" );
        if ( map != -1 )
        {
             javaType = tmp.substring( map + 1 ).trim();
             tmp = tmp.substring( 0 , map );
        }
                
        StringBuffer regex = new StringBuffer();
        
        regex.append( "(\\w+?)" );                    // Field Type
        regex.append( "\\s+?" ); 
        regex.append( "(\\w+?)" );                    // Field Name
        regex.append( "\\s+?" ); 
        regex.append( "\\=" ); 
        regex.append( "\\s+?" ); 
        regex.append( "(\\d+)" );                    // Field Index

        // Find message name and fields using regex
        Pattern parsingPattern = Pattern.compile( regex.toString() );

        Matcher parsingMatcher = parsingPattern.matcher(fld);
        if ( !parsingMatcher.find() )
            throw new ParsingException( "ERROR : Unable to parse field in message " + md.getName() + "." );

        try
        {            
            String type     = parsingMatcher.group(1);
            String name     = parsingMatcher.group(2);
            String tag      = parsingMatcher.group(3);

            type     = ( type     == null ? "" : type.trim()     );
            name     = ( name     == null ? "" : name.trim()     );
            tag      = ( tag      == null ? "0" : tag.trim()     );

            FieldDefinition fd = new FieldDefinition( name );

            Class messageClass = md.getImplementation();
            Class returnType   = null;
            if ( messageClass != null )
            {
                try
                {
                    java.lang.reflect.Method method = messageClass.getMethod( makeName( "get" , name) , (Class [])null );

                    if ( method != null )
                        returnType = method.getReturnType();

                    //if ( returnType != null )
                    //{
                        //System.out.println( "Field " + fd.getName() + ", " + returnType.getCanonicalName());
                    //}

                    fd.setImplementation(returnType);
                }
                catch( NoSuchMethodException nsme )
                {
                    // We have no idea what the return type is. so we will be setting it to null
                }
                catch( SecurityException se )
                {
                }
            }

            fd.setRequired(required);
            fd.setRepeated(repeated);
            fd.setType(type);
            fd.setMap( javaType );
            fd.setID( Integer.parseInt(tag) ); 
        
            md.addField(fd);
        }
        catch( Exception e )
        {
            throw new ParsingException( e.toString() );
        }      
    }
    
    private void parseEnum( String enm ) throws ParsingException
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
            throw new ParsingException( "ERROR : Unable to parse message." );
            
        String name   = parsingMatcher.group(1);
        String fields = parsingMatcher.group(2);
        name   = ( name   == null ? "" : name.trim()   );
        fields = ( fields == null ? "" : fields.trim() ); 
        
        EnumDefinition ed = new EnumDefinition( name );

        String [] enumArray = fields.split( "\\," );    
    
        for( int i = 0 ; i < enumArray.length ; i++ )
        {
            parseEnumeratedValue( ed , enumArray[i] );
        }

        this.enumDefs.put( name , ed );   
    }    
    
    private void parseEnumeratedValue( EnumDefinition ed , String assignment ) throws ParsingException
    {
        StringBuffer regex = new StringBuffer();
        
        regex.append( "(\\w+?)" ); // Enum name parameter
        regex.append( "\\s+?" );   // One or more whitespace characters        
        regex.append( "\\=" );     // Equals
        regex.append( "\\s+?" );   // One or more whitespace characters        
        regex.append( "(\\d+)" );  // Value
        
        Pattern parsingPattern = Pattern.compile( regex.toString() , Pattern.DOTALL );
        Matcher parsingMatcher = parsingPattern.matcher(assignment);
         
        if ( !parsingMatcher.find() )
            throw new ParsingException( "ERROR : Unable to parse enumerated value :" + assignment );
            
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
            throw new ParsingException( "ERROR : Unable to parse enumerated value, invalid numeric :" + assignment ); 
        }
        
        System.out.println( "Adding enumerated value \"" + name + "\", value " + value );
        
        ed.addValue( name , v );        
    }    

	private boolean emit()
	{
        boolean result = true;
	    StringBuffer output = new StringBuffer();

        try
        {
    	    output.append( emitHeader() );
    	    
    	    output.append( "    // ==================================================================================\n" );
    	    output.append( "    // OBJECT IDENTITIES\n" );
    	    output.append( "    // ==================================================================================\n\n" );
    	    output.append( emitObjectIdentities() );
    
    	    output.append( "    // ==================================================================================\n" );
    	    output.append( "    // FIELD IDENTITIES\n" );
    	    output.append( "    // ==================================================================================\n\n" );
    	    output.append( emitFieldIdentities() );
    
    	    output.append( "    // ==================================================================================\n" );
    	    output.append( "    // COLLECTION METHODS\n" );
    	    output.append( "    // ==================================================================================\n\n" );
    	    output.append( emitCollectionMethods() );
    
    	    output.append( "    // ==================================================================================\n" );
    	    output.append( "    // OBJECT METHODS\n" );
    	    output.append( "    // ==================================================================================\n\n" );
    	    output.append( emitObjectMethods() );
    	    
    	    output.append( emitTrailer() );
            try
            {
    	        BufferedWriter w = new BufferedWriter( new FileWriter( this.outputFileName ) );
                w.write( output.toString() );
                w.flush();
                w.close();
            }
            catch( Exception inner )
            {
                System.out.println( inner );
                result = false;
            }
        }
        catch( Exception outer )
        {
            System.out.println( outer );
            result = false;
        }
        
        return result;
	}
	
	private String emitHeader()
	{
	    StringBuffer headerSection = new StringBuffer();

        headerSection.append("package net.yura.mobile.gen;\n\n");
        headerSection.append("import java.io.*;\n");
        headerSection.append("import java.util.*;\n");
        headerSection.append("import ");
        headerSection.append(this.objectPackage);
        headerSection.append(".*;\n");
        headerSection.append("import net.yura.mobile.io.proto.*;\n\n");
        headerSection.append("public class ProtoAccess extends ProtoUtil\n");
        headerSection.append("{\n");
        headerSection.append("    public ProtoAccess()\n" );
        headerSection.append("    {\n" );
        headerSection.append("    }\n\n" );

        return headerSection.toString();
    }
    
    private String getMessageConstant( String messageName )
    {
	    StringBuffer prefix = new StringBuffer( this.objectPackage != null ? this.objectPackage.replace( "." , "_" ).toUpperCase() : "" );
        prefix.append( ( messageName == null ? "" : "_" ) );
	    prefix.append( ( messageName == null ? "" : messageName.toUpperCase() ) );
	    return prefix.toString();
    }

    private String getFieldConstant( String messageName , String fieldName )
    {
        StringBuffer prefix = new StringBuffer( getMessageConstant( messageName ) );
        prefix.append( ( fieldName == null ? "" : "_" ) );
        prefix.append( ( fieldName == null ? "" : fieldName.toUpperCase() ) );
        return prefix.toString();
    }
    
    private String cast( String messageName )
    {
	    StringBuffer c = new StringBuffer( this.objectPackage == null ? "" : ( this.objectPackage.length() == 0 ? "" : this.objectPackage + "." ) );
	    c.append( ( messageName == null ? "" : messageName ) );
	    return c.toString();
    }
    
    private String makeName( String prefix , String partialCamelCase )
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( prefix == null ? "" : prefix );
        
        String s = ( partialCamelCase == null ? "" : partialCamelCase );
        s = ( partialCamelCase.equals("") ? "unknownEntity" : partialCamelCase );
        
        sb.append( s.length() > 0 ? s.substring(0,1).toUpperCase() : "" );
        sb.append( s.length() > 1 ? s.substring(1) : "" );
        
        return sb.toString();
    }

	private String emitObjectIdentities()
	{
	    int id = 10000;
	
	    StringBuffer section = new StringBuffer();
	    for( Enumeration e = messages.elements() ; e.hasMoreElements() ; )
	    {
	        MessageDefinition md = (MessageDefinition)e.nextElement();
	    
            section.append( "    public final static int " );
            section.append( getMessageConstant( md.getName() ) );
            section.append( " = " );
            section.append( id++ );
            section.append( ";\n" );
        }  
        
        section.append( "\n" );
        return section.toString();          	        
	}

	private String emitFieldIdentities()
	{
	    StringBuffer section = new StringBuffer();
	    for( Enumeration messageEnum = this.messages.elements() ; messageEnum.hasMoreElements() ; )
	    {
	        MessageDefinition md = (MessageDefinition)messageEnum.nextElement();
	        
	        if ( md != null )
	        {
    	        Vector fields = md.getFields();
    	        if ( fields != null )
    	        {
    	            for( Enumeration fieldEnum = fields.elements() ; fieldEnum.hasMoreElements() ; )
    	            {
            	        FieldDefinition fd = (FieldDefinition)fieldEnum.nextElement();
    	    
                        section.append( "    public final static int " );
                        section.append( getFieldConstant( md.getName() , fd.getName() ) );
                        section.append( " = " );
                        section.append( fd.getID() );
                        section.append( ";\n" );
                    }
                }
            }
        }  
        
        section.append( "\n" );
        return section.toString();          	        
	}
	
	private String emitCollectionMethods()
    {
	    StringBuffer section = new StringBuffer();

        section.append( emitReadObjectMethod() );
        section.append( "\n" );
        section.append( emitWriteObjectMethod() ); 
        section.append( "\n" );
	    
        return section.toString();
    }
    
    private String emitReadObjectMethod()
    {
	    StringBuffer section = new StringBuffer();
	    
        section.append( "    public Object readObject( ProtoInputStream in ) throws IOException\n" );	    
        section.append( "    {\n" );	

        section.append( "        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );\n" );

        section.append( "        Object result = super.readObject( _in );\n" );
        
        section.append( "        if ( result != null )\n" );
        section.append( "            return result;\n" );
        
        section.append( "        // END, or NULL\n" );
        section.append( "        if ( result == null && this.prefixMessage == ProtoUtil.UNKNOWN )\n" );
        section.append( "        {\n" );
        section.append( "            return null;\n" );
        section.append( "        }\n" );
        
        section.append( "        ProtoObject proto = null;\n" );
        
        section.append( "        switch( this.prefixMessage )\n" );
        section.append( "        {\n" );
	    for( Enumeration e = messages.elements() ; e.hasMoreElements() ; )
	    {
	        MessageDefinition md = (MessageDefinition)e.nextElement();
            section.append( "            case " + getMessageConstant(md.getName()) + ": \n" );      
            section.append( "                result = " + makeName( "read" , md.getName() ) + "( _in );\n" );
            section.append( "                break; \n\n" );
        }
        section.append( "            default:\n" );
        section.append( "                throw new IOException( \"ProtoAccess - Unknown Object Type - \" + this.prefixMessage);\n" );
        section.append( "        }\n" );
        section.append( "        return result; \n" );   
        section.append( "    }\n\n" );	    
	    
        return section.toString();
    }

    private String emitWriteObjectMethod()
    {
	    StringBuffer section = new StringBuffer();

        section.append( "    public void writeObject( Object obj , ProtoOutputStream out ) throws IOException\n" );
        section.append( "    {\n" );	
        
        section.append( "        ProtoOutputStream _out = ( out == null ? this.protoOutputStream : out );\n" );
    
        // FOR EACH DEFINED OBJECT WRITE OUT THE FOLLOWING BLOCK
        
	    for( Enumeration e = messages.elements() ; e.hasMoreElements() ; )
	    {
	        MessageDefinition md = (MessageDefinition)e.nextElement();
	        
    	    if ( objectOnClassPath( md.getName() ) )
    	    {
                section.append( "        if ( obj instanceof " + cast(md.getName()) + " )\n" );
                section.append( "        {\n" );
                section.append( "            writePrefix( " + getMessageConstant(md.getName()) + " , _out );\n" );
                section.append( "            " + makeName( "write" , md.getName() ) + "((" + cast(md.getName()) + ")obj , _out );\n" );
                section.append( "            return;\n" );
                section.append( "        }\n\n" );
            }
        }
            	
        section.append( "        if ( obj instanceof java.util.Hashtable )\n" );
        section.append( "        {\n" );
        section.append( "            Hashtable h = (Hashtable)obj;\n" );

        // FOR EACH UNDEFINED OBJECT WRITE OUT THE FOLLOWING BLOCK

	    for( Enumeration e = messages.elements() ; e.hasMoreElements() ; )
	    {
	        MessageDefinition md = (MessageDefinition)e.nextElement();
	        
    	    if ( !objectOnClassPath( md.getName() ) )
    	    {
    	        StringBuffer ifStatement = new StringBuffer();
                String delimiter         = "";
                for( Enumeration en = md.getFields().elements() ; en.hasMoreElements() ; )
                {
                    FieldDefinition f = (FieldDefinition)en.nextElement();
                    
                    ifStatement.append( "                " );                
                    ifStatement.append( delimiter );
                    ifStatement.append( " ( h.get( \"" + f.getName() + "\" ) != null ) \n" );
                    delimiter = " && ";
                }
                
                section.append( "            if (\n" );
                section.append( ifStatement );
                section.append( "               )\n" );                
                section.append( "            {\n" );
                section.append( "                " + makeName("write",md.getName()) +  "( h , _out );\n" );                
                section.append( "                return;\n" );                
                section.append( "            }\n\n" );
            }
        }
    	    
        section.append( "        }\n" );

        section.append( "        super.writeObject( obj , out );\n" );
        section.append( "    }\n" );	    

        return section.toString();
    }
    
	private String emitObjectMethods() throws ParsingException
    {
	    StringBuffer section = new StringBuffer();
	    
	    for( Enumeration e = messages.elements() ; e.hasMoreElements() ; )
	    {
	        MessageDefinition md = (MessageDefinition)e.nextElement();
	        
    	    if ( objectOnClassPath( md.getName() ) )
    	    {
    	        section.append( emitReadMessageMethod(md) );
    	        section.append( "\n" );
    	        section.append( emitWriteMessageMethod(md) ); 
    	        section.append( "\n" );
    	    }
    	    else
    	    {
    	        section.append( emitReadHashtableMethod( md ) );
    	        section.append( "\n" );
    	        section.append( emitWriteHashtableMethod( md ) ); 
    	        section.append( "\n" );
    	    }
        }
        return section.toString();
    }
    
    private boolean objectOnClassPath( String objectName )
    {
        // The full directory will be sourceRoot + objectPackage + objectName

        StringBuffer fileName = new StringBuffer( "" );
        
        if ( this.sourceRoot != null )
        {
            if ( this.sourceRoot.length() > 0 )
            {
                fileName.append( this.sourceRoot );
                fileName.append( File.separator );
            }
        }
        
        if ( this.objectPackage != null )
        {
            if ( this.objectPackage.length() > 0 )
            {
                String tmp = this.objectPackage.replace( '.' , File.separatorChar );
                fileName.append( tmp ); 
                fileName.append( File.separator );
            }
        }
        
        fileName.append( objectName );
        fileName.append( ".java" );
        
        //System.out.println( "Checking for existence of " + fileName.toString() );
        
        try
        {
            File file = new File( fileName.toString() ); 
            if ( file.exists() )
            {
                //System.out.println( " - Found!" );
                return true;
            }
        }
        catch( SecurityException se )
        {
        }
        
        //System.out.println( " - Not Found!" );
        return false;
    }
    
    private String emitReadMessageMethod( MessageDefinition md ) throws ParsingException
    {
	    StringBuffer section = new StringBuffer();
	    
	    String objectName = md.getName();
	    String methodName = makeName( "read" , objectName );
	    
        section.append( "    public " );
        section.append( cast(objectName) );
        section.append( " " );
        section.append( methodName );
        section.append( "( ProtoInputStream in ) throws IOException\n" );	    
        section.append( "    {\n" );	

        section.append( "        Hashtable hashtableOfObjectArraysAsVectors = new Hashtable();\n" );
        section.append( "        Hashtable hashtableOfObjectArrayTypes = new Hashtable();\n" );

        section.append( "        ByteArrayInputStream bais          = null;\n" );
        section.append( "        " + cast(objectName) + "           obj" + objectName + " = new " + cast(objectName) + "();\n" );
        section.append( "        ProtoObject          protoObject   = null;\n\n" );
        section.append( "        ProtoInputStream _in = ( in == null ? this.protoInputStream : in );\n\n" );
        section.append( "        boolean inObject = true;\n" );
        section.append( "        while ( inObject )\n" );
        section.append( "        {\n" );
        section.append( "            protoObject = _in.readProto();\n\n" );
        section.append( "            switch( protoObject.getIndex() )\n" );
        section.append( "            {\n" );

        // Now we process each field in turn and provide mapping for it
        
        for( Enumeration e = md.getFields().elements() ; e.hasMoreElements() ; )
        {
            FieldDefinition f = (FieldDefinition)e.nextElement();
            
            section.append( "                case " + getFieldConstant( objectName , f.getName() ) + ":\n" );

            Class fieldClass = f.getImplementation();
            if ( fieldClass != null )
            {
                if ( fieldClass.isArray() && f.getRepeated() )
                {
                    // We'll be building a vector here, then converting to the appropriate array of objects
                    section.append( "                  // OBJECT ARRAY\n" );
                    section.append( "                  hashtableOfObjectArrayTypes.put( \"" + f.getName() + "\" , \"" + fieldClass.getCanonicalName() + "\" );\n" );
                    section.append( "                  Vector temporaryVector = (Vector)hashtableOfObjectArraysAsVectors.get( \"" + f.getName() + "\" );\n" );
                    section.append( "                  if ( temporaryVector == null )\n" );
                    section.append( "                  {\n" );
                    section.append( "                      temporaryVector = new Vector();\n" );
                    section.append( "                      hashtableOfObjectArraysAsVectors.put( \"" + f.getName() + "\" , temporaryVector );\n" );
                    section.append( "                  }\n" );
                    section.append( "                  bais = new ByteArrayInputStream( protoObject.getPayload() );\n" );
                    section.append( "                  Object temporaryObject = readObject(new ProtoInputStream(bais) );\n" );
                    section.append( "                  temporaryVector.addElement( temporaryObject );\n" );
                    section.append( "                  bais.close();\n" );
                    section.append( "                  bais = null;\n" );
                    section.append( "                  break;\n" );
                    continue;
                }
            }

            if ( f.getMap().trim().equals("Object") && f.getType().trim().equals("bytes") )
            {
                section.append( "                  // OBJECT\n" );
                section.append( "                  bais = new ByteArrayInputStream( protoObject.getPayload() );\n" );
                section.append( "                  obj" + objectName + "." + makeName("set",f.getName()) + "( readObject(new ProtoInputStream(bais)) );\n" );
                section.append( "                  bais.close();\n" );
                section.append( "                  bais = null;\n" );
                section.append( "                  break;\n" );
                continue;
            }

            // If the field is an enumeration
            if ( isEnum( f.getType() ) )
            {
                EnumDefinition ed = getEnumDef( f.getType() );
            
                section.append( "                  // ENUMERATED INTEGER\n" );

                // For debug purposes, we treat this as per a string
                
                section.append( "                  {\n" );
                section.append( "                      int i = (protoObject.getInteger()).intValue();\n" );
                section.append( "                      switch( i )\n" );
                section.append( "                      {\n");
                
                Hashtable h = ed.getValues();
                Enumeration keys = h.keys();
                for( ; keys.hasMoreElements() ; )
                {
                    String enumerationKey   = (String)keys.nextElement();
                    int    enumerationValue = ((Integer)h.get(enumerationKey)).intValue();                 
                    section.append( "                          case " + enumerationKey + ":  obj" + objectName + "." + makeName("set",f.getName()) + "( \"" + enumerationKey + "\" ); break; // " + enumerationValue + "\n" );
                }            
                
                section.append( "                          default: throw new IOException(\"Unknown Enumerated Value\");\n" );
                section.append( "                      }\n" );
                section.append( "                  }\n" );
                
                section.append( "                  break;\n" );
                continue;
            }

            // If the field is just a simple single primitive
            if ( isPrimitive( f.getType() ) && !f.getRepeated() )
            {
                section.append( "                  // SINGLE INSTANCE OF PRIMITIVE FIELD\n" );
                section.append( "                  " );
                section.append( getProtoObjectAssignment( "obj" + objectName , f ) );
                section.append( "\n                  break;\n" );
                continue;
            }

            // If the field is just a simple repeated primitive, it's a vector
            if ( isPrimitive( f.getType() ) && f.getRepeated() )
            {
                section.append( "                  // REPEATED INSTANCE OF PRIMITIVE FIELD --> VECTOR\n" );
                String s = getProtoObjectMethod( f );
                section.append( "                  bais = new ByteArrayInputStream( protoObject.getPayload() );\n" );
                section.append( "                  (obj" + objectName + "." + makeName("get",f.getName()) + "()).addElement( " + s + " );\n" );
                section.append( "                  bais.close();\n" );
                section.append( "                  bais = null;\n" );
                section.append( "                  break;\n" );
                continue;
            }
            
            // If the field is just a single message, it's an object (in theory, could be a hashtable)
            if ( !isPrimitive( f.getType() ) && !f.getRepeated() )
            {
                if ( !isDefined( f.getType() ) )
                    throw new ParsingException( "ERROR : Message \"" + objectName + "\" has undefined type \"" + f.getType() + "\" for field \"" + f.getName() + "\". You may be missing a message or enumeration definition." );

                section.append( "                  // SINGLE INSTANCE OF MESSAGE OBJECT --> OBJECT\n" );
                section.append( "                  bais = new ByteArrayInputStream( protoObject.getPayload() );\n" );
                section.append( "                  (obj" + objectName + "." + makeName("set",f.getName()) + "(" + makeName("read",f.getType()) + "(new ProtoInputStream(bais)) );\n" );
                section.append( "                  bais.close();\n" );
                section.append( "                  bais = null;\n" );
                section.append( "                  break;\n" );
                continue;
            }
            
            // If the field is just a repeated message, it's a Vector of objects
            if ( !isPrimitive( f.getType() ) && f.getRepeated() )
            {
                if ( !isDefined( f.getType() ) )
                    throw new ParsingException( "ERROR : Message \"" + objectName + "\" has undefined type \"" + f.getType() + "\" for field \"" + f.getName() + "\". You may be missing a message or enumeration definition." );
            
                section.append( "                  // REPEATED INSTANCE OF MESSAGE OBJECT --> VECTOR\n" );
                section.append( "                  bais = new ByteArrayInputStream( protoObject.getPayload() );\n" );
                section.append( "                  (obj" + objectName + "." + makeName("get",f.getName()) + "()).addElement( " + makeName("read",f.getType()) + "(new ProtoInputStream(bais)) );\n" );
                section.append( "                  bais.close();\n" );
                section.append( "                  bais = null;\n" );
                section.append( "                  break;\n" );
                continue;
            }
        } 

        section.append( "                case END_OF_OBJECT:\n" );
        section.append( "                  inObject = false; \n" );
        section.append( "                  break;\n" );
        section.append( "                default:\n" );
        section.append( "                  inObject = false; \n" );
        section.append( "                  break;\n" );
        section.append( "            }\n\n" );
        section.append( "        }\n\n" );
        section.append( "        protoObject = null;\n\n" );

        section.append( "        for( Enumeration keys = hashtableOfObjectArraysAsVectors.keys() ; keys.hasMoreElements() ; )\n" );
        section.append( "        {\n" );
        section.append( "            String fieldName = (String)keys.nextElement();\n" );
        section.append( "            String className = (String)hashtableOfObjectArrayTypes.get( fieldName ); \n" );
        section.append( "            Vector temporaryVector = (Vector)hashtableOfObjectArraysAsVectors.get( fieldName );\n" );
        section.append( "            if ( temporaryVector != null )\n" );
        section.append( "            {\n" );


        for( Enumeration e = md.getFields().elements() ; e.hasMoreElements() ; )
        {
            FieldDefinition f = (FieldDefinition)e.nextElement();

            Class fieldClass = f.getImplementation();
            if ( fieldClass != null )
            {
                if ( fieldClass.isArray() && f.getRepeated() )
                {
                    section.append( "                if ( fieldName.equals( \"" + f.getName() + "\" ) )\n" );
                    section.append( "                {\n" );
                    String baseName = fieldClass.getCanonicalName().replace('[',' ').replace(']',' ');
                    section.append( "                    " + baseName + "[] temporaryObjectArray = new " + baseName + "[ temporaryVector.size() ];\n" );
                    section.append( "                    temporaryVector.copyInto( temporaryObjectArray );\n" );
                    section.append( "                    obj" + objectName +  "." + makeName("set",f.getName()) + "( temporaryObjectArray );\n" );
                    section.append( "                }\n" );
                }
            }
        }

        section.append( "            }\n" );
        section.append( "        }\n" );

        section.append( "        return obj" + objectName + ";\n\n" );
        section.append( "    }\n" );
	    
        return section.toString();
    }
    
    private String emitWriteMessageMethod( MessageDefinition md ) throws ParsingException
    {
	    StringBuffer section = new StringBuffer();

	    String objectName = md.getName();
	    String methodName = makeName( "write" , objectName );
	    
        section.append( "    public void " );
        section.append( methodName );
        section.append( "( " );
        section.append( cast(objectName) );
        section.append( " obj , ProtoOutputStream out ) throws IOException\n" );
        section.append( "    {\n" );	
        
        section.append( "         ByteArrayOutputStream baos   = null;\n" );
        section.append( "         byte []               buffer = null;\n" );
        section.append( "         byte []               length = null;\n" );
        section.append( "         byte []               id     = null;\n" );
        section.append( "         Vector                v      = null;\n" );
    
        section.append( "         ProtoOutputStream _out = ( out == null ? protoOutputStream : out );\n" );

        section.append( "         if ( obj != null )\n" );
        section.append( "         {\n" );

        for( Enumeration e = md.getFields().elements() ; e.hasMoreElements() ; )
        {
            FieldDefinition f = (FieldDefinition)e.nextElement();
            // If the return type is an object array
            Class fieldClass = f.getImplementation();

            boolean emitted = false;


            // If the field is an enumeration
            if ( !emitted && isEnum( f.getType() ) )
            {
                EnumDefinition ed = getEnumDef( f.getType() );

                section.append( "             // FIELD : " + f.getName() + "\n" );
                section.append( "             // ENUMERATED INTEGER\n" );
                section.append( "             String s = obj." + makeName("get",f.getName()) + "();\n");

                Hashtable h = ed.getValues();
                Enumeration keys = h.keys();
                for( ; keys.hasMoreElements() ; )
                {
                    String enumerationKey   = (String)keys.nextElement();
                    int    enumerationValue = ((Integer)h.get(enumerationKey)).intValue();                 
                    section.append( "             if ( s.equals( \"" + enumerationKey + "\"   ) ) _out.write( " + getFieldConstant( objectName , f.getName() ) + " , " + enumerationValue + " );\n");
                }
                emitted = true;
            }

            if ( !emitted && fieldClass != null )
            {
                if ( fieldClass.isArray() && f.getRepeated() )
                {
                    section.append( "             // FIELD : " + f.getName() + "\n" );
                    section.append( "             // OBJECT ARRAY\n" );
                    //section.append( "             v = new Vector();\n" );
                    section.append( "             Object [] tmpArray = (Object [])obj." + makeName( "get" , f.getName() ) + "();\n" );
                    section.append( "             for( int arrayIndex = 0 ; arrayIndex < tmpArray.length ; arrayIndex++ )\n " ); //v.add( tmpArray[arrayIndex] );\n" );

                    //section.append( "             Enumeration e = v.elements();\n" );
                    //section.append( "             for( ; e.hasMoreElements() ; )\n" );
                    section.append( "             {\n" );
                    section.append( "                 protoOutputStream.write( " );
                    section.append( getFieldConstant( objectName , f.getName() ) );
                    //section.append( " , e.nextElement() );\n" );
                    section.append( " , tmpArray[ arrayIndex ] );\n" );
                    section.append( "             } \n\n" );
                    emitted = true;
                }
            }

            if ( !emitted && f.getMap().trim().equals("Object") && f.getType().trim().equals("bytes") )
            {
                section.append( "             // FIELD : " + f.getName() + "\n" );
                section.append( "             // OBJECT\n" );
                section.append( "             baos = new ByteArrayOutputStream();\n" );
                section.append( "             writeObject( obj." + makeName("get",f.getName()) + "() , new ProtoOutputStream( baos ) );\n" );
                section.append( "             buffer = baos.toByteArray();\n" );
                section.append( "             baos.close();\n" );
                section.append( "             baos   = null;\n" );
                section.append( "             _out.write( buffer );      \n\n" );
                emitted = true;
            }


            // If the field is just a simple single primitive
            if ( !emitted && isPrimitive( f.getType() ) && !f.getRepeated() )
            {
                section.append( "             // FIELD : " + f.getName() + "\n" );
                section.append( "             // SINGLE INSTANCE OF PRIMITIVE FIELD.\n" );
                section.append( "             protoOutputStream.write( " );
                section.append( getFieldConstant( objectName , f.getName() ) );
                section.append( " , obj." );
                section.append( makeName( "get" , f.getName() ) );
                section.append( "() );\n\n" );
                emitted = true;
            }                
                
            // If the field is just a simple repeated primitive, it's a vector
            if ( !emitted && isPrimitive( f.getType() ) && f.getRepeated() )
            {
                section.append( "             // FIELD : " + f.getName() + "\n" );
                section.append( "             // REPEATED INSTANCE OF PRIMITIVE FIELD --> VECTOR\n" );
                section.append( "             v = obj." + makeName( "get" , f.getName() ) + "();\n" );
                section.append( "             if ( v != null )\n" );
                section.append( "             {\n" );
                section.append( "                 Enumeration e = v.elements();\n" );
                section.append( "                 for( ; e.hasMoreElements() ; )\n" );
                section.append( "                 {\n" );
                section.append( "                     protoOutputStream.write( " );
                section.append( getFieldConstant( objectName , f.getName() ) );
                section.append( " , e.nextElement() );\n" );
                section.append( "                 }\n" );
                section.append( "             } \n\n" );
                emitted = true;
            }
            
            // If the field is just a single message, it's an object (in theory, could be a hashtable)
            if ( !emitted && !isPrimitive( f.getType() ) && !isEnum( f.getType() ) && !f.getRepeated() )
            {
                if ( !isDefined( f.getType() ) )
                    throw new ParsingException( "ERROR : Message \"" + objectName + "\" has undefined type \"" + f.getType() + "\" for field \"" + f.getName() + "\". You may be missing a message or enumeration definition." );
    
                section.append( "             // FIELD : " + f.getName() + "\n" );
                section.append( "             // SINGLE INSTANCE OF MESSAGE OBJECT --> OBJECT\n" );
                section.append( "             baos = new ByteArrayOutputStream();\n" );
                section.append( "             " + makeName( "write" , md.getName() ) + "( obj." + makeName( "get" , f.getName() ) + "() , new ProtoOutputStream( baos ) );\n" );
                section.append( "             buffer = baos.toByteArray();\n" );
                section.append( "             baos.close();\n" );
                section.append( "             baos   = null;\n" );
                section.append( "             length = _out.encodeVariableInteger((long)buffer.length);\n" );
                section.append( "             id     = _out.encodeVariableInteger( _out.encodeFieldIndexAndWireFormatType( " + getFieldConstant( objectName , f.getName() ) + " , ProtoInputStream.WIRE_FORMAT_LENGTH_DELIMITED ) );\n" );
                section.append( "             _out.write( id );\n" );
                section.append( "             _out.write( length );\n" );
                section.append( "             _out.write( buffer );      \n" );        
                section.append( "             id     = null;\n" );
                section.append( "             length = null;\n\n" );
                emitted = true;
            }
    
            // If the field is just a repeated message, it's a Vector of objects
            if ( !emitted && !isPrimitive( f.getType() ) && !isEnum( f.getType() ) && f.getRepeated() )
            {
                if ( !isDefined( f.getType() ) )
                    throw new ParsingException( "ERROR : Message \"" + objectName + "\" has undefined type \"" + f.getType() + "\" for field \"" + f.getName() + "\". You may be missing a message or enumeration definition." );
    
                section.append( "             // FIELD : " + f.getName() + "\n" );
                section.append( "             // REPEATED INSTANCE OF MESSAGE OBJECT --> VECTOR\n" );
                section.append( "             v = obj." + makeName( "get" , f.getName() ) + "();\n" );
                section.append( "             if ( v != null )\n" );
                section.append( "             {\n" );
                section.append( "                 Enumeration e = v.elements();\n" );
                section.append( "                 for( ; e.hasMoreElements() ; )\n" );
                section.append( "                 {\n" );
                section.append( "                     baos = new ByteArrayOutputStream();\n" );
                section.append( "                     " + makeName( "write" , f.getType() ) + "( (" + cast(f.getType()) + ")e.nextElement() , new ProtoOutputStream( baos ) );\n" );
                section.append( "                     buffer = baos.toByteArray();\n" );
                section.append( "                     baos.close();\n" );
                section.append( "                     baos   = null;\n" );
                section.append( "                     length = _out.encodeVariableInteger((long)buffer.length);\n" );
                section.append( "                     id     = _out.encodeVariableInteger( _out.encodeFieldIndexAndWireFormatType( " + getFieldConstant( objectName , f.getName() ) + " , ProtoInputStream.WIRE_FORMAT_LENGTH_DELIMITED ) );\n" );
                section.append( "                     _out.write( id );\n" );
                section.append( "                     _out.write( length );\n" );
                section.append( "                     _out.write( buffer );      \n" );        
                section.append( "                     id     = null;\n" );
                section.append( "                     length = null;\n" );
                section.append( "                     buffer = null;\n" );
                section.append( "                 }\n" );
                section.append( "             }\n\n" );
                emitted = true;
            }                    
        }

        section.append( "        }\n\n" );	    
        section.append( "        _out.write( END_OF_OBJECT , (byte)0 );\n" );
        section.append( "        _out.flush();\n" );
        section.append( "    }\n" );	    

        return section.toString();
    }
    
    private String emitReadHashtableMethod( MessageDefinition md ) throws ParsingException
    {
	    StringBuffer section = new StringBuffer();
	    
	    String objectName = md.getName();
	    String methodName = makeName( "read" , objectName );
	    
        section.append( "    public Hashtable " );
        section.append( methodName );
        section.append( "( ProtoInputStream in ) throws IOException\n" );	    
        section.append( "    {\n" );	   
        
        section.append( "        java.util.Hashtable h = new java.util.Hashtable();\n" );
        section.append( "        ProtoObject protoObject = null;\n" );
        section.append( "        ProtoInputStream _in = ( in == null ? protoInputStream : in );\n" );
    
        section.append( "        boolean inObject = true;\n" );
        section.append( "        while ( inObject )\n" );
        section.append( "        {\n" );
        section.append( "            protoObject = _in.readProto();\n" );
    
        section.append( "            switch( protoObject.getIndex() )\n" );
        section.append( "            {\n" );
        
        for( Enumeration e = md.getFields().elements() ; e.hasMoreElements() ; )
        {
            FieldDefinition fd = (FieldDefinition)e.nextElement();

            // OBJECT ARRAY


            // ENUMERATED TYPE


            // SINGLE PRIMITIVE          
            if ( isPrimitive( fd.getType() ) && !isEnum( fd.getType() ) && !fd.getRepeated() ) 
            {
                section.append( "                // SINGLE PRIMITIVE, NOT REPEATED\n" );
                section.append( "                case " + getFieldConstant( md.getName() , fd.getName() ) + ":\n" );
                section.append( "                    h.put( \"" + fd.getName() + "\" , " + getProtoObjectMethod( fd ) + " );\n" );
                section.append( "                    break;\n\n" );
                continue;
            }

            // REPEATED PRIMITIVE            


            // SINGLE MESSAGE


            // REPEATED MESSAGE
        }
            
        section.append( "                case END_OF_OBJECT:\n" );
        section.append( "                default:\n" );
        section.append( "                    inObject = false;\n" );
        section.append( "                    break; \n" );                  
        section.append( "            }\n" );
        section.append( "        }\n" );
    
        section.append( "        protoObject = null;\n" );
    
        section.append( "        return h; \n" );       
         
        section.append( "    }\n" );	    
	    
        return section.toString();
    }
    
    private String emitWriteHashtableMethod( MessageDefinition md )
    {
	    StringBuffer section = new StringBuffer();

	    String objectName = md.getName();
	    String methodName = makeName( "write" , objectName );
	    
        section.append( "    public void " );
        section.append( methodName );
        section.append( "( Hashtable " );
        section.append( " obj , ProtoOutputStream out ) throws IOException\n" );	    
        section.append( "    {\n" );	    

        section.append( "        ProtoOutputStream _out = ( out == null ? protoOutputStream : out );\n" );
        for( Enumeration e = md.getFields().elements() ; e.hasMoreElements() ; )
        {
            FieldDefinition f = (FieldDefinition)e.nextElement();
            section.append( "        _out.write( " + getFieldConstant( objectName , f.getName() ) + " , obj.get( \"" + f.getName() + "\" ) );\n" );
        }

        section.append( "    }\n" );	    

        return section.toString();
    }
    	
	private String emitTrailer()
	{
	    return "\n}\n";
	}

	private String getProtoObjectMethod( FieldDefinition f ) throws ParsingException
	{
        String type = f.getType();
        String map  = f.getMap();

        Class c = f.getImplementation();
        if ( c != null )
        {
            System.out.println("Class:" + c.getName());
            if ( c.getName().toLowerCase().contains("byte"))      map = "byte";
            if ( c.getName().toLowerCase().contains("char"))      map = "char";
            if ( c.getName().toLowerCase().contains("short"))     map = "short";
            if ( c.getName().toLowerCase().contains("integer"))   map = "int";
            if ( c.getName().toLowerCase().contains("long"))      map = "long";
        }

        if ( type.equals( "double"     ) ) { return "protoObject.getDouble().doubleValue()"; }
        if ( type.equals( "float"      ) ) { return "protoObject.getFloat().floatValue()"; }
        if ( type.equals( "bool"       ) ) { return "protoObject.getBoolean().booleanValue()"; }
        if ( type.equals( "string"     ) ) { return "protoObject.getString()"; }
        if ( type.equals( "bytes"      ) ) { return "protoObject.getByteArray()"; }

        if ( map.equals( "byte" )  && type.equals( "int32" ) ) { return "protoObject.getByte().byteValue()"; }
        if ( map.equals( "char" )  && type.equals( "int32" ) ) { return "protoObject.getCharacter().charValue()"; }
        if ( map.equals( "short" ) && type.equals( "int32" ) ) { return "protoObject.getShort().shortValue()"; }
        if ( map.equals( "int" )   && type.equals( "int32" ) ) { return "protoObject.getInteger().intValue()"; }
        if ( map.equals( "long" )  && type.equals( "int32" ) ) { return "protoObject.getLong().longValue()"; }

        if ( map.equals( "byte" )  && type.equals( "int64" ) ) { return "protoObject.getByte().byteValue()"; }
        if ( map.equals( "char" )  && type.equals( "int64" ) ) { return "protoObject.getCharacter().charValue()"; }
        if ( map.equals( "short" ) && type.equals( "int64" ) ) { return "protoObject.getShort().shortValue()"; }
        if ( map.equals( "int" )   && type.equals( "int64" ) ) { return "protoObject.getInteger().intValue()"; }
        if ( map.equals( "long" )  && type.equals( "int64" ) ) { return "protoObject.getLong().longValue()"; }

        if ( type.equals( "int32"      ) ) { return "protoObject.getInteger().intValue()"; }
        if ( type.equals( "int64"      ) ) { return "protoObject.getLong().longValue()"; }

        throw new ParsingException("ERROR : Encountered unsupported Field Type : " +
            type +
            ". Please convert to a supported type i.e. one of double, float, bool, string, bytes, int32, int64." );
	}
	
	private String getProtoObjectAssignment( String objectName , FieldDefinition f ) throws ParsingException
	{
	    return objectName + "." + makeName("set",f.getName()) + "(" + getProtoObjectMethod(f) + ");";
	}
	
	private boolean isPrimitive( String type )
	{
	    if ( type == null ) return false;

	    return( 
	        type.equals( "double"   ) ||
	        type.equals( "float"    ) ||
	        type.equals( "int32"    ) ||
	        type.equals( "int64"    ) ||
	        type.equals( "uint32"   ) ||
	        type.equals( "uint64"   ) ||
	        type.equals( "sint32"   ) ||
	        type.equals( "sint64"   ) ||
	        type.equals( "fixed32"  ) ||
	        type.equals( "fixed64"  ) ||
	        type.equals( "sfixed32" ) ||
	        type.equals( "sfixed64" ) ||
	        type.equals( "bool"     ) ||
	        type.equals( "string"   ) ||
	        type.equals( "bytes"    )
	        );
	}
	
	private boolean isDefined( String messageName )
	{
	    return ( this.messageDefs.get( messageName ) != null );
	}
	
	private boolean isEnum( String enumName )
	{
	    return ( this.enumDefs.get( enumName ) != null );
	}

	private MessageDefinition getMessageDef( String messageName )
	{
	    return ( (MessageDefinition)this.messageDefs.get( messageName ) );
	}

	private EnumDefinition getEnumDef( String enumName )
	{
	    return ( (EnumDefinition)this.enumDefs.get( enumName ) );
	}
}

class ParsingException extends Exception
{
    public ParsingException()
    {
        super();
    }
    
    public ParsingException( String s )
    {
        super(s);
    }
}

class Definition
{
    protected int     id;
	protected String  name;
	protected boolean required;
	protected boolean packed;
	protected boolean repeated;
	
	public Definition()
	{
	    this.id       = 0;
	    this.name     = "";
	    this.required = false;
	    this.repeated = false;
	    this.packed   = false;
	}
	
	public Definition( String name )
	{
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
}	

class MessageDefinition extends Definition
{
    protected Vector fields;
    protected Class messageClass;

    public MessageDefinition( String name )
    {
        super(name);
        this.fields = new Vector();
    }
	
    public void setFields( Vector fields )
    {
        this.fields = fields;
    }	
    public Vector getFields()
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
}

class EnumDefinition extends Definition
{
    protected Hashtable hashtable;
    
    public EnumDefinition( String name )
    {
        super(name);
        this.hashtable = new Hashtable();
    }
    
    public void setValues( Hashtable hashtable )
    {
        this.hashtable = hashtable;
    }	
    public Hashtable getValues()
    {
        return this.hashtable;
    }
    
	public void addValue( String name , int value )
	{
	    this.hashtable.put( name , value );
	}
	public String getValue( String name )
	{
	    return (String)this.hashtable.get( name );
	}
}

class FieldDefinition extends Definition
{
	protected String type;
	protected String map;
	protected boolean enumeratedType = false;
        protected Class fieldClass;
	
	public FieldDefinition( String name )
	{
	    super(name);
	}
	
	public String getType()
	{
	    return this.type;
	}
	
	public void setType( String type )
	{
	    this.type = type;
	}
	
	public String getMap()
	{
	    return this.map;
	}
	
	public void setMap( String map )
	{
	    this.map = map;
	}
	
	
	public boolean isEnumeratedType()
	{
	    return this.enumeratedType;
	}
	
	public void setEnumeratedType( boolean enumeratedType )
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

}    
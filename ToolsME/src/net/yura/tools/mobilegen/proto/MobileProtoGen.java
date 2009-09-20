package net.yura.tools.mobilegen;

import java.io.*;
import java.util.*;

public class MobileProtoGen
{
    String protoFileName = null;
    String root          = null;
    
    Vector messages      = null;

	public static void Main( String [] args )
	{
	    int exitCode = 1;
	    
	    MobileProtoGen c = new MobileProtoGen( args[0] , args[1] );
	    
	    if ( c.process() )
	    {
    	    if ( !c.emit() )
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

	public MobileProtoGen( String protoFileName , String root )
	{
	    this.protoFileName = protoFileName;
	    this.root          = root;
	    
	    this.messages = new Vector();
	}

	public boolean process()
	{
	    int locus = 0;
	    MessageDefinition md = new MessageDefiniton();
	    FieldDefinition   fd = new FieldDefinition();

	    boolean result = false;
	    try
	    {
	        // Open the file, process message by message
	        
	        BufferedReader r = new BufferedReader( new FileReader( this.protoFileName ) );
	        String line = null;
	        StringBuffer buffer = new StringBuffer();
	        while( ( line = r.readLine() ) != null )
	        {
	            // Strip comments, tidy whitespace, etc
	            
	            line = m.replaceAll( "\s+" , " " );
	            line = line.replaceAll( "[\/]{2}.*$" , "" );
	            line = line.trim();
	            
                buffer.append(line);	            
                buffer.append( " " );
	        }
	        r.close();
	        
	        System.out.println( buffer );
	    }
	    catch( Exception e )
	    {
	        result = false;
	    }
	    
	    return result;
	}

	public boolean emit()
	{
	    return false;
	}
}

class Definition
{
	private String name;
	private bool   required;
	private bool   optional;
	private bool   packed;
	
	public Definition()
	{
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
	
	public void setOptional( boolean optional )
	{
	    this.optional = optional;
	}
	public boolean getOptional()
	{
	    return this.optional;
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
}	

class Proto
{
	private java.util.Vector definitions;
}

class MessageDefinition extends Definition
{
	private String javaPackage;
	private String javaClass;
	private java.util.Vector fields;
	
	public MessageDefinition( String name )
	{
	    super(name);
	}
	
	public void setJavaPackage( String javaPackage )
	{
	    this.javaPackage = javaPackage;    
	}
	public String getJavaPackage()
	{
	}
	
	public void setJavaClass()
	{
	}
	public String getJavaClass()
	{
	    this.javaClass = javaClass;    
	}
	
	public String getCanonicalName()
	{
	}
	
	public void addField( FieldDefinition fd )
	{
	}
    public void getFields()
    {
    }
}

class FieldDefinition extends Definition
{
	private String type;
	
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
}


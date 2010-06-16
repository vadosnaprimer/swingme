package javax.microedition.contactless;

public interface TargetProperties {

	public Class[] getConnectionNames();

	public String getMapping();
	
	public String getProperty(String name);
	
	public TargetType[] getTargetTypes();
	
	public String getUid();
	
	public String getUrl();
	
	public String getUrl(Class connectionName);
	
	public boolean hasTargetType(TargetType type); 
}

package javax.microedition.contactless.visual;

import javax.microedition.io.Connection;

public interface VisualTagConnection extends Connection {

	public Object generateVisualTag(byte[] data, Class imageClass, ImageProperties properties);
	
	public byte[] readVisualTag(Object tagImage, Class imageClass, String symbologyName);
	
}

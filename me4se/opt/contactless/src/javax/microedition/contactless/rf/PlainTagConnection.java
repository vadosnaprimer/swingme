package javax.microedition.contactless.rf;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.contactless.ContactlessException;
import javax.microedition.contactless.TagConnection;

public interface PlainTagConnection extends TagConnection {
	
	public Vector transceive(Vector input) throws ContactlessException, IOException;

}

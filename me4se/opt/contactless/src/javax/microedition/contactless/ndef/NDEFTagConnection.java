package javax.microedition.contactless.ndef;

import java.io.IOException;

import javax.microedition.contactless.ContactlessException;
import javax.microedition.contactless.TagConnection;

public interface NDEFTagConnection extends TagConnection {

	public NDEFMessage readNDEF() throws ContactlessException, IOException;
	
	public void writeNDEF(NDEFMessage message) throws ContactlessException, IOException;
	
}
package javax.microedition.contactless.sc;

import java.io.IOException;

import javax.microedition.contactless.ContactlessException;
import javax.microedition.contactless.TagConnection;

public interface ISO14443Connection extends TagConnection {

	public byte[] exchangeData(byte[] data) throws IOException, ContactlessException;
	
}
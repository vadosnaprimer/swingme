package org.me4se.psi.java1.gcf.resource;

import javax.microedition.io.*;

import org.me4se.impl.*;

import java.io.*;

public class ResourceConnectionImpl extends ConnectionImpl implements StreamConnection {

	String name;

	public void initialise(java.util.Properties p) {
	}

	public void open(String url, int mode, boolean timeouts) throws IOException {

		int cut = url.indexOf(":");
		name = url.substring(cut + 1).replace('\\', '/');
		if (!name.startsWith("/"))
			name = "/" + name;
	}

	public InputStream openInputStream() throws IOException {
		InputStream is = getClass().getResourceAsStream(name);
		if (is == null)
			throw new IOException("Resource '" + name + "' not found!");

		return is;
	}

	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		throw new RuntimeException("return socket.getOutputStream ();");
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	public void close() throws IOException {
	}

}

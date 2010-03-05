package org.me4se.impl;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;


public abstract class ConnectionImpl implements Connection {

    public abstract void open (String url, int mode, 
			       boolean timeouts) throws IOException;

    public abstract void close () throws IOException;

	//public abstract void initialise( Properties properties );     
}



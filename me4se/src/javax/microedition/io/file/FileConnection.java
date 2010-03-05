/*
 * $Id: FileConnection.java,v 1.2 2007/07/31 08:49:10 omry_y Exp $
 */

/**
 * @author Michael Kroll, michael.kroll@trantor.de
 */

package javax.microedition.io.file;

import java.io.*;
import java.util.Enumeration;

import javax.microedition.io.StreamConnection;

/**
 * @API PDAP-1.0
 */
public interface FileConnection extends StreamConnection {

	/**
	 * @API PDAP-1.0
	 */

	public long availableSize();
	/**
	 * @API PDAP-1.0
	 */

	public boolean canRead();

	/**
	 * @API PDAP-1.0
	 */
	public boolean canWrite();

	/**
	 * @API PDAP-1.0
	 */
	public void create() throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public void delete() throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public long directorySize(boolean includeSubDirs) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public boolean exists();

	/**
	 * @API PDAP-1.0
	 */
	public long fileSize() throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public String getName();

	/**
	 * @API PDAP-1.0
	 */
	public String getPath();

	/**
	 * @API PDAP-1.0
	 */
	public String getURL();

	/**
	 * @API PDAP-1.0
	 */
	public boolean isDirectory();

	/**
	 * @API PDAP-1.0
	 */
	public boolean isHidden();

	/**
	 * @API PDAP-1.0
	 */
	public long lastModified();

	/**
	 * @API PDAP-1.0
	 */
	public Enumeration list() throws IOException;
	
	/**
	 * @API PDAP-1.0
	 */
	public Enumeration list(String filter, boolean includeHidden) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public void mkdir()throws java.io.IOException;

	/**
	 * @API PDAP-1.0
	 */
	public boolean rename(java.lang.String newName) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public void setFileConnection(String fileName) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public void setHidden(boolean hidden) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public void setReadable(boolean readable) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public void setWriteable(boolean writable) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public long totalSize();

	/**
	 * @API PDAP-1.0
	 */
	public void truncate(long byteOffset) throws IOException;

	/**
	 * @API PDAP-1.0
	 */
	public long usedSize();
	
	/**
	 * @API  FileConnection Optional Package 1.0
	 * @param byteOffset
	 * @return
	 */
	public OutputStream openOutputStream(long byteOffset) throws IOException;

        // YURA FIX
	public void setWritable(boolean writable) throws IOException;

}


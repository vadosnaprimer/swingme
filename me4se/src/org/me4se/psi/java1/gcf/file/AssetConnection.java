/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me4se.psi.java1.gcf.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.ApplicationManager;

/**
 *
 * @author Administrator
 */
public class AssetConnection implements FileConnection {

    String name;

    public AssetConnection(String name) {
        this.name = name;
    }

    public long availableSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canRead() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canWrite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void create() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long directorySize(boolean includeSubDirs) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean exists() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long fileSize() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDirectory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isHidden() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long lastModified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Enumeration list() throws IOException {

        Vector result = new Vector();
        String dir = "assets/"+name;

        try {

            CodeSource src = getMidletClass().getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();

                ZipInputStream zin = new ZipInputStream(jar.openStream());
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {

                    String filename = ze.getName();

                    if (filename.startsWith(dir) && filename.lastIndexOf('/')==dir.length()-1 && filename.length() > dir.length()) {
                        result.addElement( filename.substring( dir.length() ) );
                    }

                    zin.closeEntry();

                }
                zin.close();
            }

        }
        catch(Exception ex) { ex.printStackTrace(); }

        return result.elements();
        
    }

    public Enumeration list(String filter, boolean includeHidden) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mkdir() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean rename(String newName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFileConnection(String fileName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setHidden(boolean hidden) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setReadable(boolean readable) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setWriteable(boolean writable) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long totalSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void truncate(long byteOffset) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long usedSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputStream openOutputStream(long byteOffset) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setWritable(boolean writable) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    public InputStream openInputStream() throws IOException {
        return getMidletClass().getResourceAsStream("/assets/"+name);
    }

    public void close() throws IOException {
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Class getMidletClass() {
        return ApplicationManager.getInstance().active.getClass();
    }

}

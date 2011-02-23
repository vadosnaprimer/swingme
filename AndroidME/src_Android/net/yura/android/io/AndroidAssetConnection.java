package net.yura.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.file.FileConnection;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import net.yura.android.AndroidMeApp;

public class AndroidAssetConnection implements FileConnection {

    // we store the name of dirs like this "dirName/" even though the real name is "dirName"
    // and we have to remove the trailing "/" if we want to actually list the files in that dir
    private String name;

    public AndroidAssetConnection(String n) {
        this.name = n;
    }

    @Override
    public void close() {

    }

    @Override
    public long availableSize() {
        return 0;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public void create() throws IOException {
        throw new RuntimeException();
    }

    @Override
    public void delete() throws IOException {
        throw new RuntimeException();
    }

    @Override
    public long directorySize(boolean includeSubDirs) throws IOException {
        return 0;
    }

    @Override
    public boolean exists() {
        return true; // as we dont know how to check
    }

    @Override
    public long fileSize() throws IOException {
        AssetManager manager = getAssetManager();
        AssetFileDescriptor fd = manager.openFd(name);
        return fd.getLength();
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    /**
     * @see #name
     */
    @Override
    public boolean isDirectory() {
        return "".equals(name) || name.endsWith("/");
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public Enumeration<String> list() throws IOException {
        return list("", false);
    }

    /**
     * @see #name
     */
    @Override
    public Enumeration<String> list(String filter, boolean includeHidden) throws IOException {

        String name = this.name;

        // for some reason when you pass "/" to the list() method, you get a list of the files in the root of the apk
        // but there is no way to list anything else using the "/" at the start or end

        if (name.length()>1 && name.endsWith("/")) {
            name = name.substring(0, name.length()-1);
        }

        AssetManager manager = getAssetManager();

        final String[] files = manager.list(name);
        // TODO Auto-generated method stub
        return new Enumeration<String>() {
            int c=0;
            @Override
            public boolean hasMoreElements() {
                return c<files.length;
            }
            @Override
            public String nextElement() {
                String file = files[c++];
                // if we have a dot in the name, we assume if is not a dir, otherwise we add a "/" to the end
                return file+( file.indexOf('.')<=0 ?"/":"");
            }
        };
    }

    @Override
    public void mkdir() throws IOException {
        throw new RuntimeException();
    }

    @Override
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    @Override
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(openDataOutputStream());
    }

    @Override
    public InputStream openInputStream() throws IOException {
        AssetManager manager = getAssetManager();
        return manager.open(name);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return openOutputStream(0);
    }

    @Override
    public OutputStream openOutputStream(long byteOffset) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public void rename(String newName) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public void setFileConnection(String fileName) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public void setHidden(boolean hidden) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public void setReadable(boolean readable) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public void setWritable(boolean writable) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public long totalSize() {
        return 0;
    }

    @Override
    public void truncate(long byteOffset) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public long usedSize() {
        return 0;
    }

    private static AssetManager getAssetManager() {
        return AndroidMeApp.getIntance().getResources().getAssets();
    }

}

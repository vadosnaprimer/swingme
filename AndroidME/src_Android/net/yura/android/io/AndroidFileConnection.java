package net.yura.android.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.microedition.io.file.FileConnection;

public class AndroidFileConnection implements FileConnection {

    private static final String SPECIAL_CHARACTERS = "*.^?[]\\";

    public static final File getParentDirectory(File file) {
        String path = file.getAbsolutePath();
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - File.separator.length());
        }
        int index = path.lastIndexOf(File.separator);
        File result;
        if (index >= 0) {
            path = path.substring(0, index);
            result = new File(path);
        } else {
            result = null;
        }
        return result;
    }

    private static final String escape(String s) {
        StringBuffer result = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (SPECIAL_CHARACTERS.indexOf(c) >= 0) {
                result.append("\\");
            }
            result.append(c);
        }
        return result.toString();
    }

    private static final File getFile(String url) throws IOException {
        final String URL_FILE_ROOT = "file:///";
        if (url == null || !url.startsWith(URL_FILE_ROOT)) {
            throw new IOException("Invalid file url");
        }

        String filePath = url.substring(URL_FILE_ROOT.length());
        return new File(filePath);
    }

    private File file;
    private boolean open;

    public AndroidFileConnection(String url) throws IOException {
        this.file = getFile(url);
        this.open = true;
    }

    // Override
    public long availableSize() {
        // this isn't available
        return -1;
    }

    // Override
    public boolean canRead() {
        return this.file.canRead();
    }

    // Override
    public boolean canWrite() {
        return this.file.canWrite();
    }

    // Override
    public void create() throws IOException {
        if (!this.file.createNewFile()) {
            throw new IOException("file creation failed");
        }
    }

    // Override
    public void delete() throws IOException {
        if (!this.file.delete()) {
            throw new IOException("file deletion failed");
        }
    }

    // Override
    public long directorySize(boolean includeSubDirs) throws IOException {
        // why is this in the interface?
        return getDirectorySize(this.file, includeSubDirs);
    }

    private long getDirectorySize(File file, boolean recursive) {
        long size = 0;
        File[] children = file.listFiles();
        for (File child : children) {
            if (child.isDirectory()) {
                if (recursive) {
                    size += getDirectorySize(child, recursive);
                }
            } else {
                size += child.length();
            }
        }
        return size;
    }

    // Override
    public boolean exists() {
        return this.file.exists();
    }

    // Override
    public long fileSize() throws IOException {
        return this.file.length();
    }

    // Override
    public String getName() {
        return this.file.getName();
    }

    // Override
    public String getPath() {
        return this.file.getPath();
    }

    // Override
    public String getURL() {
        return this.file.toURI().toString();
    }

    // Override
    public boolean isDirectory() {
        return this.file.isDirectory();
    }

    // Override
    public boolean isHidden() {
        return this.file.isHidden();
    }

    // Override
    public boolean isOpen() {
        return this.open;
    }

    // Override
    public long lastModified() {
        return this.file.lastModified();
    }

    // Override
    public Enumeration list() throws IOException {
        return list(null, false);
    }

    // Override
    public Enumeration list(String filter, boolean includeHidden)
            throws IOException {
        Pattern pattern;
        if (filter == null) {
            pattern = Pattern.compile(".*");
        } else {
            String[] literalParts = filter.split("\\*");
            StringBuffer sb = new StringBuffer(filter.length());
            for (int i = 0; i < literalParts.length; i++) {
                String part = literalParts[i];
                String literalPart = escape(part);
                if (i > 0) {
                    sb.append("*");
                }
                sb.append(literalPart);
            }
            pattern = Pattern.compile(sb.toString());
        }

        final Pattern filterPattern = pattern;
        File[] a = this.file.listFiles(new FilenameFilter() {

            // Override
            public boolean accept(File dir, String name) {
                return filterPattern.matcher(name).matches();
            }

        });

        Vector<String> v = new Vector<String>();
        for (File file : a) {
            String fileName = file.getName();
            if (file.isDirectory() && !fileName.endsWith(File.separator)) {
                fileName += File.separator;
            }
            v.add(fileName);
        }

        return v.elements();
    }

    // Override
    public void mkdir() throws IOException {
        if (!this.file.mkdir()) {
            throw new IOException("unable to create directory");
        }

    }

    // Override
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(this.openInputStream());
    }

    // Override
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(this.openOutputStream());
    }

    // Override
    public InputStream openInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    // Override
    public OutputStream openOutputStream() throws IOException {
        return openOutputStream(0);
    }

    // Override
    public OutputStream openOutputStream(long byteOffset) throws IOException {
        boolean append;
        if (byteOffset == 0) {
            append = false;
        } else if (byteOffset < this.file.length()) {
            append = true;
        } else {
            throw new IOException("offsets not supported");
        }
        FileOutputStream fos = new FileOutputStream(this.file, append);
        return fos;
    }

    // Override
    public void rename(String newName) throws IOException {
        File directory = getParentDirectory(this.file);
        File targetFile = new File(directory, newName);
        this.file.renameTo(targetFile);
    }

    // Override
    public void setFileConnection(String fileName) throws IOException {
        if (fileName.equals("..")) {
            File directory = getParentDirectory(this.file);
            if (directory == null) {
                throw new IOException("no parent dir");
            }
            this.file = directory;
        } else {
            this.file = new File(this.file, fileName);
        }
    }

    // Override
    public void setHidden(boolean hidden) throws IOException {
        throw new IOException("unsupported");
    }

    // Override
    public void setReadable(boolean readable) throws IOException {
        file.setReadable(readable);
    }

    // Override
    public void setWritable(boolean writable) throws IOException {
        file.setWritable(writable);
    }

    // Override
    public long totalSize() {
        return this.file.length();
    }

    // Override
    public void truncate(long byteOffset) throws IOException {
        throw new IOException("unsupported");
    }

    // Override
    public long usedSize() {
        return this.file.length();
    }

    // Override
    public void close() throws IOException {
        this.open = false;
    }
}

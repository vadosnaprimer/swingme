package javax.microedition.io.file;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import net.yura.mobile.logging.Logger;

public class FileSystemRegistry {

    public static boolean addFileSystemListener(FileSystemListener listener) {
        return false;
    }

    public static boolean removeFileSystemListener(FileSystemListener listener) {
        return false;
    }

    public static Enumeration listRoots() {
        File[] roots = File.listRoots();
        Vector<String> rootsArray = new Vector<String>(roots.length);
        for (File root : roots) {
            try {
                String path = root.getCanonicalPath();
                rootsArray.add(path);
            } catch (Exception e) {
                Logger.warn(e);
            }
        }
        return rootsArray.elements();
    }
}

package net.yura.mobile.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import net.yura.mobile.logging.Logger;

public class FileUtil {

    // TODO why would we care about files made in the last 30 seconds???
    public final static long RECENT_PICTURE_TIME_INTERVAL = 30000; // 30 Seconds
    public static final String ROOT_PREX = "file:///";

    public static final int TYPE_ALL = 0;
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_PICTURE = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_OTHER = 5;

    private static final int[] TYPES_KNOWN = {TYPE_PICTURE,TYPE_AUDIO,TYPE_VIDEO};
    public static final String[][] EXTS_FILENAME = {
        { "JPG", "JPEG","JPE","PNG","GIF","BMP","PNG.REM" /* .png.rem used on blackberry */ },
        {"AMR","MP3","MP4","AAC","WMA","WAV","MID","MIDI","M4A"},
        {"3GP"},};


    public static void close(Object fc) {
        if( fc != null ) {
            try {
                if (fc instanceof Connection) {
                    ((Connection)fc).close();
                }
                else if (fc instanceof InputStream) {
                    ((InputStream)fc).close();
                }
                else if (fc instanceof OutputStream) {
                    ((OutputStream)fc).close();
                }
                else {
                    throw new RuntimeException();
                }
            }
            catch(IOException ioe) {
            	//#debug info
                Logger.info("cant close " + fc, ioe);
            }
        }
    }

    public static Vector listFiles(String dir,int filter,boolean recent) {

        Vector files = new Vector();

        try {
            if (ROOT_PREX.equals(dir)) {
                // add root folder
                for (Enumeration e = FileSystemRegistry.listRoots(); e.hasMoreElements();) {
                    files.addElement( e.nextElement()  );
                }
            }
            else {
                files.addElement( "../" );

                FileConnection fc = (FileConnection)Connector.open(dir, Connector.READ);

                if (fc.exists() && fc.isDirectory()) {

                    long earliestAcceptableTime = System.currentTimeMillis()-RECENT_PICTURE_TIME_INTERVAL;
                    int dircount = 1;

                    for (Enumeration enu = fc.list(
                            //#debug debug
                            "*",true
                            ); enu.hasMoreElements();) {
                        String fname = (String) enu.nextElement();
                        //filter item from file type
                        if ( isFileType(fname, TYPE_FOLDER) ) {
                            files.insertElementAt(fname,dircount);
                            dircount++;
                        }
                        else if ( isFileType(fname,filter)) {
                            if(recent) {
                                if (!(FileUtil.getLastModified(dir+fname) >= earliestAcceptableTime)) {
                                    continue;
                                }
                            }
                            files.addElement(fname);
                        }
                    }
                }
            }

        }
        catch(Exception ex) {
          //#debug debug
          Logger.warn("error moving dir", ex);
        }

        return files;

    }

    public static int getFileType(String fname) {
        String name = fname.toUpperCase();
        if (name.endsWith("/"))
            return TYPE_FOLDER;

        for (int i = 0; i < EXTS_FILENAME.length; i++)
            for( int j = 0; j < EXTS_FILENAME[i].length ; j++ )
                if (name.endsWith(EXTS_FILENAME[i][j]))
                    return TYPES_KNOWN[i];
        return TYPE_OTHER;
    }

    public static boolean isFileType(String fname, int filetype) {

        String name = fname.toUpperCase();

        if( filetype == TYPE_ALL ) {
            return true;
        }
        else if( filetype == TYPE_FOLDER ) {
/*
            // this is overkill, and only works for full paths, not filenames
            try {
                FileConnection fc =(FileConnection)(Connector.open(fname,Connector.READ));
                return fc.isDirectory();
            }
            catch (IOException e) {
                Logger.warn(e);
            }
*/
            return name.endsWith("/");
        }
        else if( filetype == TYPE_PICTURE || filetype == TYPE_AUDIO  || filetype == TYPE_VIDEO ) {
            int j = filetype - 2;
            for( int i = 0; i < EXTS_FILENAME[j].length ; i++ ) {
                if (name.endsWith(EXTS_FILENAME[j][i])) {
                    return true;
                }
            }
        }
        else if( filetype == TYPE_OTHER ) {
            return getFileType(fname) == TYPE_OTHER;
        }
        return false;
    }

    /**
     * get path from full path
     * ex  c:/a/b/c.exe => c:/a/b/
     * @param fullName String
     * @return String
     * @see java.io.File#getPath() File.getPath
     */
    public static String extractPath(String fullName) {
        return fullName.substring(0,fullName.lastIndexOf('/')+1);
    }
    /**
     * get file name from full path
     * ex: c:/a/b/c.exe =>c.exe
     * @param fullName String
     * @return String
     * @see java.io.File#getName() File.getName
     */
    public static String extractName(String fullName) {
        return fullName.substring(fullName.lastIndexOf('/')+1);
    }

    //,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,
    //==== File IO =============================================================
    //°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°º¤ø,¸¸,ø¤º°``°

    public static InputStream getInputStreamFromFileConnector(final String fileName) throws IOException {
        return getReadFileConnection(fileName).openInputStream();
    }

    public static FileConnection getWriteFileConnection(String fullName) throws IOException {
                FileConnection fc = (FileConnection)Connector.open(fullName,Connector.READ_WRITE); // we need read and write here, as exists needs read access
                if (fc != null) {
                    //create new file when there is not a existed file
                    if (fc.exists()) {
                        fc.delete();
                    }

                    fc.create();
                    fc.setWritable(true);
                    return fc;
                }
                throw new IOException();

    }

    public static FileConnection getReadFileConnection(String fullName) throws IOException {
                FileConnection fc = (FileConnection)Connector.open(fullName,Connector.READ);
                if (fc != null) {
                    return fc;
                }
                throw new IOException();
    }

    /**
     * open file and get file size
     * @param fileName String
     * @return int ,file size
     * @see java.io.File#length() File.length
     */
    public static int getFileSize(String fileName) {
        FileConnection fc=null;
        int fileSize=-1;
        try {
            fc=(FileConnection)(Connector.open(fileName, Connector.READ));
            if(fc!=null&&fc.exists()) {
                fileSize=(int)fc.fileSize();
                fc.close();
            }
        }
        catch(Exception e) {
          Logger.warn("cant get size " + fileName, e);
        }
        return fileSize;
    }

    /**
     * Returns the date/time of which a file is last modified.
     *
     * @param fileName File name icluding full path to the file
     * @return Last modified time of the file in long format
     * @see java.io.File#lastModified() File.lastModified
     */
    public static long getLastModified(String fileName) {
        FileConnection fc=null;
        long lastModified=-1;
        try {
            fc=(FileConnection)(Connector.open(fileName, Connector.READ));
            if(fc!=null&&fc.exists()) {
                lastModified=fc.lastModified();
            }
        }
        catch(Exception e) {
          Logger.warn("error " + fileName, e);
        }

        //Close the file connection
        close(fc);

        return lastModified;
    }

    /**
     * Check if the file specified in the path exists on the local filesystem.
     * @param filePath full file name
     * @return return boolean, true if file exists, false if it does not
     * @see java.io.File#exists() File.exists
     */
    public static boolean localFileExists(String filePath) {
        if (filePath==null) return false;

        FileConnection fc = null;
        try {
            fc = (FileConnection)(Connector.open(filePath, Connector.READ));
            if (fc != null) {
                return fc.exists();
            }

            return false;
        }
        catch (Exception e) {
          Logger.info("error " + filePath, e);
          return false;
        }
    }

    /**
     * mkdir in native
     * @param dirName dir name
     * @return if success return true
     * @see java.io.File#mkdir() File.mkdir
     */
    public static boolean mkDir(String dirName) {
        if(!dirName.trim().endsWith("/")) {
            return false;    //dir name should be endswith "/" on Nokia device
        }
        FileConnection fc=null;
        try {
            fc=(FileConnection)(Connector.open(dirName,Connector.READ_WRITE ));
            if( fc != null ) {
                if( !fc.exists() )
                    fc.mkdir() ;
                fc.close();
                fc = null;
                return true;
            }
        }
        catch(Exception e) {
          Logger.warn("cant make " + dirName, e);
        }
        return false;
    }


    /**
     * Returns the list of all files and directories available in a directory.
     *
     * @param dir The path to the directory we will look into.
     * @return A String Enumeration of all the available files and directories inside dir.
     * @see java.io.File#list() File.list
     */
    public static Enumeration getDirectoryFiles(String dir) {
        Enumeration en = null;
        if(dir==null || !dir.trim().endsWith("/")) {
            return null;
        }
        FileConnection fc=null;
        try {
            fc=(FileConnection)(Connector.open(dir,Connector.READ));
            en = fc.list();
        }
        catch (Exception ex) {
          Logger.info("error " + dir, ex);
        }

        return en;
    }

    /**
     * delete file or folder
     * @param path full path string
     * @see java.io.File#delete() File.delete
     */
    public static void deleteFile(String path) {
        try {
            FileConnection fc=(FileConnection)(Connector.open(path,Connector.READ_WRITE));
            if( fc != null && fc.exists() ) {
                if(fc.isDirectory()) {
                    for(Enumeration enu = fc.list(); enu.hasMoreElements(); ) {
                        deleteFile(path + (String)enu.nextElement());
                    }
                }
                fc.delete();
                fc.close();
            }
        }
        catch(Exception e) {
          Logger.warn("cant del " + path, e);
        }
    }

    /**
     * read file from native
     * @param fileName file name
     * @param startPos start position
     * @param length read length, if -1, read all
     * @return byte data[]
     */
    public static byte[] readFile(String fileName,long startPos,int length)
    {
        byte[] buffer = null;
        FileConnection fc = null;
        InputStream is = null;
        try {
            fc=getReadFileConnection(fileName);
            if( fc != null && fc.exists() && fc.canRead() ) {
                if( length == -1 ) {
                    length = (int)fc.fileSize();
                }
                else if( length >= fc.fileSize()-startPos ) {
                    length = (int)(fc.fileSize()-startPos);
                }
                if( length > 0 ) {
                    buffer = new byte[length];
                    is = fc.openInputStream();
                    is.skip(startPos);
                    is.read(buffer,0,length);
                    is.close();
                    is = null;
                }
                fc.close();
                return buffer;
            }
        }
        catch(Exception e) {
          Logger.warn("cant read " + fileName + " " + startPos + " " + length, e);
        }
        return null;
    }

    /**
     * Write the data specified in a array out to the native filesystem.
     * @param fullname full file name
     * @param append indicates whether to append or create a new file
     * @param fileStartPos start position of file
     * @param data byte[]
     * @param dataStartPos start position of byte[]
     * @param dataLength write length
     * @return return file name new created or elder, if null, means write failed
     */
    public static String writeFile(String fullname, boolean append, int fileStartPos, byte[] data, int dataStartPos,int dataLength) {
        return writeFile(fullname, append, false, fileStartPos, data, dataStartPos, dataLength);
    }

    /**
     * Write the data specified in a array out to the native filesystem.
     * @param fullname full file name
     * @param append indicates whether to append or create a new file
     * @param deleteExisting indicates whether to delete the existing file if there is one
     * @param fileStartPos start position of file
     * @param data byte[]
     * @param dataStartPos start position of byte[]
     * @param dataLength write length
     * @return return file name new created or elder, if null, means write failed
     */
    public static String writeFile(String fullname, boolean append, boolean deleteExisting, int fileStartPos, byte[] data, int dataStartPos,int dataLength) {
        FileConnection fc = null;
        OutputStream os = null;
        String dstname = fullname;
        try {
            fc = getWriteFileConnection(fullname);
            if( fc != null ) {
                //create new file when there is not a existed file
                if( !fc.exists() ) {
                    fc.create();
                    fc.setWritable(true);
                }
                else {
                    if (deleteExisting) {
                        deleteFile(fullname);
                        fc.create();
                        fc.setWritable(true);
                    }
                    else {
                        //change the file name when current existed & we don't want append
                        if( !append )
                        {
                            fc.close();
                            fc = null;
                            fc = findAvailableFile(fullname);
                            dstname = fc.getURL();
                        }
                    }
                }
                os = fc.openOutputStream(fileStartPos);
                os.write(data,dataStartPos,dataLength);
                os.flush();
            }
        }
        catch(Exception e) {
            dstname = null;

          Logger.warn("error " + fullname + " " + append + " " + deleteExisting + " " + fileStartPos + " " + dataStartPos + " " + dataLength, e);
        }
        finally {
            close(os);
            close(fc);
        }
        return dstname;
    }


    //search an file which name has not been used
    private static FileConnection findAvailableFile(String fileName) throws Exception {
        FileConnection fc = null;

        String bef = fileName.substring(0,fileName.lastIndexOf('.'));
        String end = fileName.substring(fileName.lastIndexOf('.'));
        String fname = null;
        for(int j=0; ; j++)
        {
            fname = bef + "_" + j + end;
            fc = (FileConnection)Connector.open( fname , Connector.READ_WRITE );
            if( !fc.exists() )
            {
                fc.create();
                fc.setWritable(true);
                return fc;
            }
            else
            {
                fc.close();
            }
        }
    }

    /**
     * copy file to other dir
     * sometimes Attachment.fileName did not equal local file name
     * @param srcfileName original filename
     * @param srcFullName src path
     * @param desFullPath target dir name
     * @return if success return true;
     */
    public static boolean copyFile(String srcfileName,String srcFullName,String desFullPath) {
        int COPY_BLOCK_SIZE=10*1024;

        FileConnection dst = null;
        FileConnection src = null;
        InputStream dis = null;
        OutputStream dos = null;
        boolean writesucc = false;
        try {
            src = (FileConnection) Connector.open(srcFullName, Connector.READ_WRITE);
            if( src != null && src.exists() ) {
                dst = (FileConnection) Connector.open(desFullPath + srcfileName, Connector.READ_WRITE);
                if( dst.exists() ) {
                    dst = findAvailableFile(desFullPath + srcfileName);
                }
                else {
                    dst.create();
                    dst.setWritable(true);
                }
                dis = src.openInputStream();
                dos = dst.openOutputStream();
                byte[] data = new byte[COPY_BLOCK_SIZE];
                int i = 0;
                while( ( i = dis.read(data,0,COPY_BLOCK_SIZE ) ) != -1  ) {
                    dos.write(data,0,i);
                    dos.flush();
                }
                writesucc = true;
            }
        }
        catch (Exception e) {
            writesucc = false;

          Logger.warn("copy error " + srcfileName + " " + srcFullName + " " + desFullPath, e);
        }
        finally {
            close(dis);
            close(dos);
            close(src);
            close(dst);
        }
        return writesucc;
    }

    /** Copy a file through jsr-75
     * @param source the path to the source file
     * @param destination the path to the destination file
     */
    public static void copyFile(String source, String destination) {
        try {
            byte[] sourceData = FileUtil.readFile(source, 0, -1);
            FileUtil.writeFile(destination, false, true, 0, sourceData, 0, sourceData.length);
        }
        catch(Exception e) {
          Logger.warn("copy error " + source + " " + destination, e);
        }
    }

}

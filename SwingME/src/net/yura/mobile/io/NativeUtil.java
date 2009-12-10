package net.yura.mobile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.DesktopPane;

public class NativeUtil {

    /**
     * open file and get file size
     * @param fileName String
     * @return int ,file size
     */
    public static int getFileSize(String fileName)
    {
        FileConnection fc=null;
        int fileSize=-1;
        try
        {
            fc=(FileConnection)(Connector.open(fileName, Connector.READ));
            if(fc!=null&&fc.exists())
            {
                fileSize=(int)fc.fileSize();
                fc.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return fileSize;
    }

    /**
     * Returns the date/time of which a file is last modified.
     *
     * @param fileName File name icluding full path to the file
     * @return Last modified time of the file in long format
     */
    public static long getLastModified(String fileName)
    {
        FileConnection fc=null;
        long lastModified=-1;
        try
        {
            fc=(FileConnection)(Connector.open(fileName, Connector.READ));
            if(fc!=null&&fc.exists())
            {
                lastModified=fc.lastModified();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //Close the file connection
        try
        {
            if (fc!=null)
            {
                fc.close();
            }
        }
        catch (Exception ex)
        {
        }

        return lastModified;
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
        DataInputStream is = null;
        try
        {
            fc=(FileConnection)(Connector.open(fileName,Connector.READ));
            if( fc != null && fc.exists() && fc.canRead() )
            {
                if( length == -1 )
                {
                    length = (int)fc.fileSize();
                }
                else if( length >= fc.fileSize()-startPos )
                {
                    length = (int)(fc.fileSize()-startPos);
                }
                if( length > 0 )
                {
                    buffer = new byte[length];
                    is = fc.openDataInputStream();
                    is.skip(startPos);
                    is.read(buffer,0,length);
                    is.close();
                    is = null;
                }
                fc.close();
                return buffer;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Write the data specified in a array out to the native filesystem.
     * @param fileName full file name
     * @param append indicates whether to append or create a new file
     * @param fileStartPos start position of file
     * @param data byte[]
     * @param dataStartPos start position of byte[]
     * @param dataLength write length
     * @return return file name new created or elder, if null, means write failed
     */
    public static String writeFile(String fullname, boolean append, int fileStartPos, byte[] data, int dataStartPos,int dataLength)
    {
        return writeFile(fullname, append, false, fileStartPos, data, dataStartPos, dataLength);
    }



    /**
     * Write the data specified in a array out to the native filesystem.
     * @param fileName full file name
     * @param append indicates whether to append or create a new file
     * @param deleteExisting indicates whether to delete the existing file if there is one
     * @param fileStartPos start position of file
     * @param data byte[]
     * @param dataStartPos start position of byte[]
     * @param dataLength write length
     * @return return file name new created or elder, if null, means write failed
     */
    public static String writeFile(String fullname, boolean append, boolean deleteExisting, int fileStartPos, byte[] data, int dataStartPos,int dataLength)
    {
        FileConnection fc = null;
        OutputStream os = null;
        String dstname = fullname;
        try
        {
            fc = (FileConnection)(Connector.open(fullname,Connector.READ_WRITE));
            if( fc != null )
            {
                //create new file when there is not a existed file
                if( !fc.exists() )
                {
                    fc.create();
                    fc.setWritable(true);
                }
                else
                {
                    if (deleteExisting)
                    {
                        deleteFile(fullname);
                        fc.create();
                        fc.setWritable(true);
                    }
                    else
                    {
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
        catch(Exception e)
        {
            dstname = null;

            e.printStackTrace();
        }
        finally
        {
            if( os != null )
            {
                try
                {
                    os.close();
                }
                catch(IOException ioe)
                {}
            }
            if( fc != null )
            {
                try
                {
                    fc.close();
                }
                catch(IOException ioe)
                {}
            }
        }
        return dstname;
    }

    /**
     * Check if the file specified in the path exists on the local filesystem.
     * @param filePath full file name
     * @return return boolean, true if file exists, false if it does not
     */
    public static boolean localFileExists(String filePath)
    {
        if (filePath==null) return false;

        FileConnection fc = null;
        try
        {
            fc = (FileConnection)(Connector.open(filePath, Connector.READ_WRITE));
            if (fc != null)
            {
                return fc.exists();
            }

            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    //search an file which name has not been used
    private static FileConnection findAvailableFile(String fileName) throws Exception
    {
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
     * Returns the list of all files and directories available in a directory.
     *
     * @param dir The path to the directory we will look into.
     * @return A String Enumeration of all the available files and directories inside dir.
     */
    public static Enumeration getDirectoryFiles(String dir)
    {
        Enumeration en = null;
        if(dir==null || !dir.trim().endsWith("/"))
        {
            return null;
        }
        FileConnection fc=null;
        try
        {
            fc=(FileConnection)(Connector.open(dir,Connector.READ));
            en = fc.list();
        }
        catch (Exception ex)
        {

        }

        return en;
    }

    /**
     * mkdir in native
     * @param dirName dir name
     * @return if success return true
     */
    public static boolean mkDir(String dirName)
    {
        if(!dirName.trim().endsWith("/"))
        {
            return false;    //dir name should be endswith "/" on Nokia device
        }
        FileConnection fc=null;
        try
        {
            fc=(FileConnection)(Connector.open(dirName,Connector.READ_WRITE ));
            if( fc != null )
            {
                if( !fc.exists() )
                    fc.mkdir() ;
                fc.close();
                fc = null;
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * copy file to other dir
     * sometimes Attachment.fileName did not equal local file name
     * @param srcfileName original filename
     * @param srcFullPath src path
     * @param desFullPath target dir name
     * @return if success return true;
     */
    public static boolean copyFile(String attachmentName,String srcFullName,String desFullPath)
    {
        int COPY_BLOCK_SIZE=10*1024;

        FileConnection dst = null;
        FileConnection src = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        boolean writesucc = false;
        try
        {
            src=(FileConnection)(Connector.open(srcFullName,Connector.READ_WRITE));
            if( src != null && src.exists() )
            {
                dst=(FileConnection)(Connector.open(desFullPath + attachmentName,Connector.READ_WRITE));
                if( dst.exists() )
                {
                    dst = findAvailableFile(desFullPath + attachmentName);
                }
                else
                {
                    dst.create();
                    dst.setWritable(true);
                }
                dis = src.openDataInputStream();
                dos = dst.openDataOutputStream();
                byte[] data = new byte[COPY_BLOCK_SIZE];
                int i = 0;
                while( ( i = dis.read(data,0,COPY_BLOCK_SIZE ) ) != -1  )
                {
                    dos.write(data,0,i);
                    dos.flush();
                }
                writesucc = true;
            }
        }
        catch (Exception e)
        {
            writesucc = false;

            e.printStackTrace();
        }
        finally
        {
            if( dis != null)
            {
                try
                {
                    dis.close();
                }
                catch(IOException ioe)
                {}
            }
            if( dos != null)
            {
                try
                {
                    dos.close();
                }
                catch(IOException ioe)
                {}
            }
            if( src != null )
            {
                try
                {
                    src.close();
                }
                catch(Exception e)
                {}
            }
            if( dst != null )
            {
                try
                {
                    dst.close();
                }
                catch(Exception e)
                {}
            }
        }
        return writesucc;
    }

    /**
     * delete file or folder
     * @param path full path string
     */
    public static void deleteFile(String path)
    {
        try
        {
            FileConnection fc=(FileConnection)(Connector.open(path,Connector.READ_WRITE));
            if( fc != null && fc.exists() )
            {
                if(fc.isDirectory())
                {
                    for(Enumeration enu = fc.list(); enu.hasMoreElements(); )
                    {
                        deleteFile(path + (String)enu.nextElement());
                    }
                }
                fc.delete();
                fc.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Copy a file through jsr-75
     * @param source the path to the source file
     * @param destination the path to the destination file
     */
    public static void copyFile(String source, String destination)
    {
        try
        {
            byte[] sourceData = NativeUtil.readFile(source, 0, -1);
            NativeUtil.writeFile(destination, false, true, 0, sourceData, 0, sourceData.length);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * get path from full path
     * ex  c:/a/b/c.exe => c:/a/b/
     * @param fullName String
     * @return String
     */
    public static String extractPath(String fullName)
    {
        return fullName.substring(0,fullName.lastIndexOf('/')+1);
    }
    /**
     * get file name from full path
     * ex: c:/a/b/c.exe =>c.exe
     * @param fullName String
     * @return String
     */
    public static String extractName(String fullName)
    {
        return fullName.substring(fullName.lastIndexOf('/')+1);
    }

    public static FileConnection getWriteFileConnection(String fullName) throws Exception {
                FileConnection fc = (FileConnection)Connector.open(fullName,Connector.READ_WRITE);
                if (fc != null) {
                    //create new file when there is not a existed file
                    if (fc.exists()) {
                        fc.delete();
                    }

                    fc.create();
                    fc.setWritable(true);
                    return fc;
                }
                throw new Exception();

    }
    public static FileConnection getReadFileConnection(String fullName) throws Exception {
                FileConnection fc = (FileConnection)Connector.open(fullName,Connector.READ);
                if (fc != null) {
                    return fc;
                }
                throw new Exception();
    }

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
            catch(IOException ioe) {}
        }
    }

    public final static long RECENT_PICTURE_TIME_INTERVAL = 30000;
    public static final String ROOT_PREX = "file:///";

    public static Vector listFiles(String dir,int filter,boolean recent) {

        Vector files = new Vector();

        try {
            if (ROOT_PREX.equals(dir)) {
                // add root folder
                for (Enumeration e = FileSystemRegistry.listRoots(); e.hasMoreElements();) {
                    files.addElement( (String)e.nextElement()  );
                }
            }
            else {
                files.addElement( "../" );

                FileConnection fc = (FileConnection)Connector.open(dir, Connector.READ);

                if (fc.exists() && fc.isDirectory()) {

                    long earliestAcceptableTime = System.currentTimeMillis()-RECENT_PICTURE_TIME_INTERVAL;
                    int dircount = 1;

                    for (Enumeration enu = fc.list(); enu.hasMoreElements();) {
                        String fname = (String) enu.nextElement();
                        //filter item from file type
                        if ( isFileType(fname, TYPE_FOLDER) ) {
                            files.insertElementAt(fname,dircount);
                            dircount++;
                        }
                        else if ( isFileType(fname,filter)) {
                            if(recent) {
                                if (!(NativeUtil.getLastModified(dir+fname) >= earliestAcceptableTime)) {
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
            //#mdebug
            ex.printStackTrace();
            DesktopPane.log("\error moving dir: "+ex.toString());
            //#enddebug
        }

        return files;

    }

    public static final int TYPE_ALL = 0;
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_PICTURE = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_OTHER = 5;

    private static final int[] TYPES_KNOWN = {TYPE_PICTURE,TYPE_AUDIO,TYPE_VIDEO};
    public static final String[][] EXTS_FILENAME = {
        { "JPG", "JPEG","JPE","PNG","GIF","BMP" },
        {"AMR","MP3","MP4","AAC","WMA","WAV","MID","MIDI","M4A"},
        {"3GP"},};

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


    public static InputStream getInputStreamFromFileConnector(final String fileName){
        InputStream is = null;
        try {
            is = ((FileConnection)Connector.open(fileName,Connector.READ)).openDataInputStream();
        } catch (Exception ex) {
            //#debug
            ex.printStackTrace();
            return null;
        }
        return is;
    }

    /**
     * will return null if no thumb found
     */
    public static Image getThumbnailFromFile(final String fileName) {
        DataInputStream dis = null;
        try {
            dis = ((FileConnection)Connector.open(fileName, Connector.READ)).openDataInputStream();
            byte[] thumb = parseJPEG(dis);
            if (thumb != null && thumb.length > 0) {
                return Image.createImage(thumb,0,thumb.length);
            }
            return null;
        }
        catch (Throwable err) {
            err.printStackTrace();
            return null;
        }
        finally {
            close(dis);
        }
    }

    /**
     * return null if not enough mem to load image
     */
    public static Image getImageFromFile(String filename) {
        InputStream is=null;
        try {
            is = NativeUtil.getInputStreamFromFileConnector(filename);
            return Image.createImage(is);
        }
        catch (Throwable err) {
            err.printStackTrace();
            return null;
        }
        finally {
            NativeUtil.close(is);
        }
    }

    public static byte[] parseJPEG( DataInputStream dis ) throws IOException {

        boolean motorolaOrder;
        byte[] thumbData = null;

        dis.read();            //SOI marker:ff
        dis.read();            //SOI marker:d8

        while (dis.read() == 0xFF)  //APP1 marker: FF
        {
            int marker = dis.read();    //APP1 marker: E1
            int ssss1 = dis.read();  //marker length
            int ssss2 = dis.read();  //marker length
            switch( marker )
            {
                case 0xE1:              //APP1 marker for EXIF format jpeg
                    dis.skip( 6 );      //EXIF header: E x i f 00 00

                    dis.mark( Integer.MAX_VALUE );  //TIFF start

                    int byteAlign1 = dis.read();   //byte order: 4D4D->Motorola 4949->Intel
                    int byteAlign2 = dis.read();
                    if( byteAlign1 == 0x49 && byteAlign2 == 0x49 )            //4949
                        motorolaOrder = false;
                    else if( byteAlign1 == 0x4D && byteAlign2 == 0x4D)        //4D4D
                        motorolaOrder = true;
                    else
                        throw new IOException("unsupported byte order format in TIFF header");

                    dis.skip( 2 );                 // Moto->00 2A Inter->2A 00

                    int IDF0offset = readInt( dis,motorolaOrder );//position of the first IFD, normally 8 this is relative to TIFF start

                    thumbData = readIFD( dis,0,0,motorolaOrder );

                    break;

                default:
                    dis.skip(ssss1 - 2);//skip the rest of the chunk
                    break;
            }
        }
        dis.close();
        return thumbData;
    }


    /**
     * parse IFD directory and copy out thumb data, and ignore other attributes
     * IFD format can be refered from {@link http://www.media.mit.edu/pia/Research/deepview/exif.html }
     */
    public static byte[] readIFD( DataInputStream dis,int thumbOffset,int thumbSize,boolean motorolaOrder ) throws IOException
    {
        int numDirectoryEntries = readShort( dis,motorolaOrder );

        for( int i = 0; i < numDirectoryEntries; i++ )
        {
            int entryTag = readShort( dis,motorolaOrder );
            int dataFormat = readShort( dis,motorolaOrder );
            int components = readInt( dis,motorolaOrder );
            int data = readInt( dis,motorolaOrder );
            switch( entryTag )
            {
                case 0x0201:
                    thumbOffset = data;
                    break;
                case 0x0202:
                    thumbSize = data;
                    break;
            }
        }
        int nextIFDOffset = readInt( dis,motorolaOrder );

        if( nextIFDOffset != 0 )
        {
            dis.reset();
            long skipped = dis.skip( nextIFDOffset );
            return readIFD( dis,thumbOffset,thumbSize,motorolaOrder );
        }
        else
        {
            //this is the IFD1 block so its the thumbnail
            dis.reset();
            dis.skip( thumbOffset );
            byte[] thumbData = new byte[ thumbSize ];
            dis.readFully( thumbData );
            return thumbData;
        }
    }



    /**
     * convert 4 byte to int using the byte order read from TIFF header
     */
    public static int readInt( DataInputStream dis,boolean motorolaOrder ) throws IOException {
        int temp = dis.readInt();
        return motorolaOrder ? temp : ( (temp&0xFF)<<24) | ( ((temp>>8)&0xFF)<<16)| (((temp>>16)&0xFF)<<8)| ( ((temp>>24)&0xFF));
    }

    /**
     * convert 2 byte to int using the byte order read from TIFF header
     */
    public static int readShort( DataInputStream dis,boolean motorolaOrder ) throws IOException {
        int temp = dis.readShort();
        return motorolaOrder ? temp : ( ((temp)&0xFF)<<8)| ( ((temp>>8)&0xFF));
    }

}

package net.yura.blackberry;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Image;
import net.yura.mobile.util.ImageUtil;

public class BlackBerryThumbLoader implements ImageUtil.ThumbnailLoader {

    

    public Image getThumbnailFromFile(String fileName) {


            byte[] img=null;

            try {

                // try and load from the BBThumbs.dat file
                img = getThumb(fileName); // this is the good method
                if (img!=null) {
                    return Image.createImage(img, 0, img.length);
                }
                
                /*
                 THIS IS A BAD METHOD 
                  
                // OLD BlackBerry
                String bbThumbs = fileName.substring(0, fileName.lastIndexOf('/')) +"/BBThumbs.dat";
                if (FileUtil.localFileExists(bbThumbs)) {
                    InputStream in = FileUtil.getInputStreamFromFileConnector(bbThumbs);
                    String file = fileName.substring(fileName.lastIndexOf('/')+1);
                    byte[] data = FileUtil.getData(in, -1);
                    img = readThumbs(data, file);
                    return Image.createImage(img, 0, img.length);
                }
                */

                /*
                 
                  THIS DOES NOT WORK!!
                 
                // NEW BlackBerry                                               can be "pictures" or "camera" after the "user"
                String bbThumbsNew;
                if (fileName.startsWith("file:///store/")) { // home/user/
                    bbThumbsNew="file:///store/appdata/rim/media/";
                }
                else if (fileName.startsWith("file:///system/")) {
                    bbThumbsNew="file:///system/appdata/rim/media/";
                }
                else {
                    bbThumbsNew="file:///SDCard/BlackBerry/system/media/";
                }

                String[] names = {"thumbs116x116.dat","thumbs480x360.dat","thumbs86x86.dat","thumbs480x480.dat"};

                for (int c=0;c<names.length;c++) {
                    String thumbs = bbThumbsNew+names[c];
                    if (FileUtil.localFileExists(thumbs)) {
                        InputStream in = FileUtil.getInputStreamFromFileConnector(thumbs);
                        byte[] data = FileUtil.getData(in, -1);
                        img = readThumbsAlternative(data, fileName);
                        return Image.createImage(img, 0, img.length);
                    }
                }
*/
            }
            catch (Exception ex) {
                //#mdebug debug
                System.err.println("error "+ex+" "+img+" "+(img!=null?img.length:-1));
                ex.printStackTrace();
                //#enddebug
            }
        
        
            return null;
    }

  

    private static Hashtable thumbsMap;
    private static String currentDB;
    /**
     * YURA: this method is for loading the old style BBThumbs.dat, and it works, it also does not use much memory so is good!
     */
    public static byte[] getThumb(String fileName){
        String DB = fileName.substring(0,fileName.lastIndexOf('/'))+"/BBThumbs.dat";
        fileName = fileName.substring(fileName.lastIndexOf('/')+1,fileName.length());
        boolean first=false;
        if (thumbsMap == null || !DB.equals(currentDB)){
            first=true;
            thumbsMap = new Hashtable();
            currentDB = DB;
        }
        byte b[] = null;
        FileConnection fc=null;
        InputStream in = null;
        try {
            fc = (FileConnection)Connector.open(DB,Connector.READ);
            if (!fc.exists()){
                return null;
            }
            in = fc.openInputStream();
            b = getThumbNail(first, thumbsMap, fileName, in);
        }
        catch (Exception e) {
        }
        finally {
            if (fc != null) {
                try {
                    fc.close();
                }
                catch (Exception e) {
                }
            }
            if (in != null){
                try {
                    in.close();
                }
                catch (Exception e) {
                }
            }
        }
        return b;
    }

    private static byte[] getThumbNail(boolean firstTime,Hashtable map, String fileName,InputStream thumbDBStream) throws Exception {
        if (!firstTime){
            int coors[] = (int[])map.get(fileName);
            if (coors == null){
                return null;
            }
            int fileStart = coors[0];
            int fileLength= coors[1];
            while ( (fileStart = fileStart - (int)thumbDBStream.skip(fileStart)) > 0);
            byte out[] = new byte[fileLength];
            int p = out.length;
            while ( (p = p - thumbDBStream.read( out,out.length - p , p)) > 0 );
//                      int c = 0;
//                      int b_ = in.read();
//                      while (b_ != -1){
//                              if (c &gt;= fileStart ){
//                                      out[c - fileStart ] = (byte)b_;
//                              }
//                              c++;
//                              if (c == fileStart + fileLength){
//                                      in.close();
//                                      return out;
//                              }
//                              b_ = in.read();
//                      }
            return out;
        }
        byte png_header[] = {
            (byte)0x89,0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        int png_header_count=0;
        int b_=thumbDBStream.read();
        int count =0;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte out[] = null;
        boolean firstThumb = true;
        int fileCopyPosition = 0;
        boolean fileFound = false;
        boolean searchHeader=true;
        int lastThumbLength = 0;
        int lastThumbPosition = 0;
        while (b_!= -1){
            if (searchHeader){
                bo.write(b_);
                if ((byte)b_ == png_header[png_header_count]){
                    png_header_count++;
                    if (png_header_count == png_header.length){
                        png_header_count=0;
                        byte bo_[]=bo.toByteArray();
                        int nameLengt=0;
                        int pos = 0;
                        if (firstThumb){
                            nameLengt = (0xff00&(bo_[6]<<8))|(bo_[7]&0xff);
                            pos = 8;
                        }
                        else {
                            nameLengt = (0xff00&(bo_[51]<<8))|(bo_[52]&0xff);
                            pos = 53;
                        }
                        String name = new String(bo_,pos,nameLengt);
                        //printbytes(bo_);
                        pos += nameLengt;
                        pos += 18;
                        lastThumbLength = ((bo_[pos]<<16)&0xff0000)| ((bo_[pos+1]<<8)&0xff00)|(bo_[pos+2]&0xff);
                        lastThumbPosition = count - png_header.length+1;
                        map.put(name, new int[]{lastThumbPosition,lastThumbLength});
                        bo = new ByteArrayOutputStream();
                        firstThumb = false;
                        if (name.equals(fileName)){
                            fileFound = true;
                            out = new byte[lastThumbLength];
                            System.arraycopy(png_header, 0, out, 0, png_header.length);
                            fileCopyPosition = png_header.length;
                            fileFound = true;
                        }
                        else {
                            fileFound = false;
                        }
                        searchHeader = false;
                        lastThumbPosition = count-png_header.length;
                    }
                }
                else {
                    png_header_count=0;
                }
            }
            else {
                if (fileFound) {
                    out[fileCopyPosition] = (byte)b_;
                    fileCopyPosition++;
                }
            }
            if (lastThumbLength + lastThumbPosition == count){
                searchHeader = true;
            }
            count++;
            b_=thumbDBStream.read();
        }
        return out;
    }



    
    
    
    

  
/*
  
      This is for loading the new files but does NOT work
  
    public static class TagGroup
    {
        public int Reserved1;
        public int Reserved2;
        public Vector Tags;

        public TagGroup(InputStream st, long length) {
            
            try {
            
                DataInputStream br = new DataInputStream(st);
                if (br.readShort() != 8710)
                {
                    //Not valid dataBase
                    return;
                }
                Reserved1 = br.readInt();
                Reserved2 = br.readInt();
                Tags = new Vector();
                long pos = 0;
                while (length > pos)
                {
                    st.mark(Integer.MAX_VALUE); //Again, not the best but if supported it gets the job done
                    Tag g = new Tag(br);
                    pos += g.getSize();
                    if (g.Reserved1 == 0)
                    {
                        st.reset();
                        break;
                    }
                    Tags.addElement(g);
                    if(g.Reserved1 == 8720)
                    {
                        //Appears to be a new tag database
                        break;
                    }
                }
                //Not sure what other tags are
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
        public static char[] readChars(DataInputStream br, int count) throws IOException
        {
            char[] chars = new char[count];
            for(int i = 0; i < count; i++)
            {
                    chars[i] = (char)br.read();
            }
            return chars;
        }


    public static class Tag
    {
        public short Reserved1;
        public int PathLength;
        public int NameLength;
        public int DataLength;
        public int Reserved2;
        public int Reserved3;
        public int Reserved4;
        public String Path;
        public String Name;
        public byte[] Image;
        public short Reserved5;

        public Tag(DataInputStream br) throws IOException
        {
            Reserved1 = br.readShort();
            PathLength = br.readInt();
            NameLength = br.readInt();
            DataLength = br.readInt();
            Reserved2 = br.readInt();
            Reserved3 = br.readInt();
            Reserved4 = br.readInt();
            int b = br.read(); //Gap (0)
            Path = new String(readChars(br, PathLength));
            Name = new String(readChars(br, NameLength));
            if (b != 0 && Reserved1 != 0)
            {
                System.out.println("Invalid gap for " + Path + Name);
            }
            Image = new byte[DataLength];
            br.readFully(Image);
            b = br.read(); //Gap (17)
            if (b != 17 && Reserved1 != 0 && Reserved1 != 8720)
            {
                    System.out.println("Invalid 2nd gap for " + Path + Name);
            }
            Reserved5 = br.readShort();
        }

        public int getSize()
        {
            return (2 * 2) + (6 * 4) + PathLength + NameLength + DataLength;
        }
    }
*/
    
    
    
    
    
    
    
    


    /**
     * YURA: this works but is very very bad on memory
     * 
     * @param byte[] search refers to BBThumbs.dat file already opened previously and
     * indexed for faster

     * lookups

     * @param String szSearch refers to the image we want the thumbnail of, so if
     * picture01.jpg is in the //folder with the current BBThumbs.dat then that
     * will be my search String

     * Also now with BB 5.0 we have random file access which will make this
     * method of lookup even

     * faster for single file thumbnail creation
     * /
    public static byte[] readThumbs(byte[] search, String szSearch) {

            // convert the search string to bytes for easier comparison

            byte[] searchtmp = szSearch.getBytes();
            for (int x = 0; x < search.length; x++) {
                    boolean found = false;
                    int lastbyte = 0;

                    // For the length of searchtmp trying to find a match in the byte
                    // file

                    // we could also have converted search to String [new
                    // String(search)]

                    // and have done an index of however I prefer direct byte access as

                    // lookups tend to be faster

                    for (int y = 0; y < searchtmp.length; y++) {
                            if (search[x + y] == searchtmp[y]) {
                                    lastbyte = x + y + 1;
                                    found = true;
                            } else {
                                    found = false;
                                    break;
                            }
                    }

                    if (found) {

                            // we found our search string so next we want to see how long in

                            // bytes the files is so we only read untill end of this PNG

                            // without needing to search for the AE 42 50 82 Hex String,

                            // also there might be a chance that AE 42 50 82 repeats itself

                            // as such it is highly recommended to get the size

                            byte[] tmpB = new byte[4];
                            tmpB[0] = search[lastbyte + 20];
                            tmpB[1] = search[lastbyte + 19];
                            tmpB[2] = search[lastbyte + 18];
                            tmpB[3] = search[lastbyte + 17];
                            long readsize = 0;
                            int t = 0;

                            // convert and retrieve the size

                            for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
                                    readsize |= (long) (tmpB[t++] & 0xff) << shiftBy;
                            }
                            tmpB = null;
                            tmpB = new byte[(int) readsize];
                            t = 0;

                            // now we read from the start of the image untill the end of

                            // the image

                            for (int y = lastbyte + 21; y < (int) readsize + lastbyte + 21; y++) {
                                    tmpB[t++] = search[y];
                            }

                            // and like that we have our bitmap

                            // now all that is left to do is to convert it using

                            // Bitmap.createBitmapFromBytes(tmpB, x, y, z);

                            return tmpB;

                    }
            }
            return null;
    }
    */


    /**
     * YURA: this if for loading the new style thumbs, and does NOT work at all in my tests
     * /
    public static byte[] readThumbsAlternative(byte[] search, String szSearch) {
        // file:///SDCard/BlackBerry/pictures/image.jpg
        szSearch = szSearch.substring(szSearch.lastIndexOf('/') + 1, szSearch.length());
        // convert the search string to bytes for easier comparison
        byte[] searchtmp = szSearch.getBytes();
        int lastbyte = 0;
        int endIndex = 0;
        for (int x = 0; x < search.length; x++) {
            boolean found = false;
            // For the length of searchtmp trying to find a match in the byte file
            // we could also have converted search to String [new String(search)]
            // and have done an index of however I prefer direct byte access as lookups tend to be faster
            for (int y = 0; y < searchtmp.length; y++) {
                if (search[x + y] == searchtmp[y]) {
                    lastbyte = x + y + 1;
                    found = true;
                }
                else {
                    found = false;
                    break;
                }
            }
            if (found) {
                for (int y = lastbyte; y < search.length; y++) {
                    byte first = search[y];
                    byte second = (y < search.length - 1) ? search[y + 1] : search[y];
                    String firstS = Integer.toString((first & 0xff) + 0x100, 16).substring(1);
                    String secondS = Integer.toString((second & 0xff) + 0x100, 16).substring(1);
                    if (firstS.equals("ff") && secondS.equals("d9")) {
                        endIndex = y + 1;
                        break;
                    }
                }
                if (endIndex > 0) {
                    break;
                }
            }
        }
        if (lastbyte > 0 && endIndex > 0) {
            byte[] b = new byte[lastbyte + endIndex];
            int counter = 0;
            for (int i = lastbyte; i <= endIndex; i++) {
                b[counter] = search[i];
                counter++;
            }
            return b;
    
        }
        return null;
    }
*/
    
}

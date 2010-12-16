/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public class ImageUtil {

    public static Image makeImage(int w,int h,int color) {

                int[] rgbBuff = new int[w*h];

                for (int i = 0; i < rgbBuff.length; i++) {

                    rgbBuff[i] = color;

                }

                return Image.createRGBImage(rgbBuff, w, h, true);

    }

    public static final void imageColor(int pixels[], int color) {
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = (color & 0xff) >> 0;
        for(int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] & 0xff000000) >> 24;
            int blue = (pixels[i] & 0xff) >> 0;
            pixels[i] = alpha << 24 | (blue * r) / 255 << 16 | (blue * g) / 255 << 8 | (blue * b) / 255;
        }

    }

    /**
     * replaces all values of the blue channel with a color
     */
    public static final Image imageColor(Image image, int i) {

        int ai[] = new int[image.getWidth() * image.getHeight()];
        image.getRGB(ai, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        imageColor(ai, i);
        return Image.createRGBImage(ai, image.getWidth(), image.getHeight(), true);
    }

    public static Image colorize(Image original, int newColor) {
        int[] rgba = new int[original.getWidth()*original.getHeight()];
        original.getRGB(rgba, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());

        for (int i=0; i< rgba.length;i++) {
            int alpha = ((rgba[i] >> 24) & 0xFF);
            rgba[i] = ( (newColor & 0xFFFFFF) | (alpha << 24));
        }

        return Image.createRGBImage(rgba, original.getWidth(), original.getHeight(), true);
    }

    public static Image scaleImage(Image img, int newW, int newH) {
        try {
            // Ensure we have 3D API, otherwise throws exception
            Class.forName("javax.microedition.m3g.Background");

            // Create a mutable image with the requested size
            Image resImg = Image.createImage(newW, newH);
            new Graphics2D(resImg.getGraphics()).drawScaledImage(img, 0, 0, newW, newH);

            return resImg;
        }
        catch (Throwable e) {
            // Do nothing. Converting with 3D API failed. Use sampling.
        }

        return getScaledImage(img, newW, newH);
    }



  public static int LARGESTBYTESTREAM = 170000;   // Current maximum byte stream allowed
  public static int THUMB_MAX_SIZE = 30000; // 16284;  // Max Thumbnail size


  /**
   * Scale and resize image to fit our screen
   * @param dispImage, the image
   * @param width , max width
   * @param heigth, max height
   * @return Image - a scaled(resized) image
   */
  public static Image  getScaledImage (Image dispImage, int width,int height){
    // Max image width = 3/4 of the screen width.
    if (dispImage != null){

      int imWidth = dispImage.getWidth ();
      int imHeight = dispImage.getHeight ();

      int widthScale = (imWidth << 9) * 4 / (width * 4);
      int heightScale = (imHeight << 9) * 1 / height;

      int chosenScale = Math.max ( widthScale, heightScale );

      dispImage = resize ( dispImage,0, chosenScale, 9 );
    }

    return dispImage;
  }

  /**
   * Get a thumbnail image from a byte array
   * @param fileBytes
   * @return Image
   */
  public static Image getJPEGthumb (byte[] fileBytes ) {
    Image image = null;

    try {
      // If file is JPEG encoded.
      if ( isJPEG (fileBytes[0], fileBytes[1]) ) {
        // Try to find thumbnail. Will throw ArrayOutOfBoundsException
        // if a thumbnail is not found.
        int startIndex = 2;
        do {
          // -1 == 0xFF and all JPEG markers begin with 0xFF
          while ( fileBytes[startIndex] != -1 ) {
            startIndex++;
          }
          startIndex++;
          // Try to find another Start Of Image marker (FFD8)
        } while ( (fileBytes[startIndex] & 0xFF) != 0xD8 );

        int endIndex = startIndex + 1;
        do {
          // -1 == 0xFF and all JPEG markers begin with 0xFF
          while ( fileBytes[endIndex] != -1 ) {
            endIndex++;
          }
          endIndex++;
          // Now try to find the End Of Image marker (FFD9).
        } while ( (fileBytes[endIndex] & 0xFF) != 0xD9 );
        // Create image out of thumbnail.
        image = Image.createImage ( fileBytes, startIndex - 1, endIndex - startIndex + 2 );
      }
    }
    catch (Throwable e) {
      Logger.error(e);
    }

    return image;
  }

  /**
   * Reads an InputStream into a array of bytes
   * @param inStr image stream
   * @param no_of_filebytes the size of the stream
   * @return byte[] array of bytes containing the image
   */
  private static byte[] readStream2 (InputStream inStr,int no_of_filebytes) throws IOException{
    DataInputStream newStr = new DataInputStream (inStr);
    byte[] byteArray = new byte[no_of_filebytes];
    newStr.readFully (byteArray);  // read entire file in one sweep...
    newStr.close ();
    return byteArray;
  }

  /**
   * Checks if two consectutive bytes can be interpreted as a jpeg
   * @param b1 first byte
   * @param b2 second byte
   * @return true if b1 and b2 are jpeg markers
   */
  private static boolean isJPEG (byte b1,byte b2){
    return ( (b1 & 0xFF) == 0xFF && (b2 & 0xFF) == 0xD8 );
  }

  /**
   * Traverse an inputStream and return a thumbnail image if any. We build the thumbnail directly
   * from the inputstream, thus avoiding to run out of memory on very large picture files.
   * @param str the stream
   * @returns Image - created from thumbnail iside jpeg file.
   */
  public static Image getThumbFromFile (InputStream str) throws IOException {

      byte[] tempByteArray = new byte[THUMB_MAX_SIZE]; // how big can a thumb get.
      byte[] bytefileReader = {0}; // lazy byte reader
      byte firstByte, secondByte = 0;
      int currentIndex = 0;
      str.read(bytefileReader);
      firstByte = bytefileReader[0];
      str.read(bytefileReader);
      secondByte = bytefileReader[0];
      int a;
      if (isJPEG(firstByte, secondByte)) {
          byte rByte = 0;
          do {
              while (rByte != -1) {
                  a = str.read(bytefileReader);
                  if (a == -1) {
                      return null;
                  }
                  rByte = bytefileReader[0];
              }
              a = str.read(bytefileReader);
              if (a == -1) {
                  return null;
              }
              rByte = bytefileReader[0];

          } while ((rByte & 0xFF) != 0xD8); // thumb starts
          tempByteArray[currentIndex++] = -1;
          tempByteArray[currentIndex++] = rByte;
          rByte = 0;
          do {
              while (rByte != -1) {
                  a = str.read(bytefileReader);
                  if (a == -1) {
                      return null;
                  }
                  rByte = bytefileReader[0];
                  tempByteArray[currentIndex++] = rByte;
              }
              a = str.read(bytefileReader);
              if (a == -1) {
                  return null;
              }
              rByte = bytefileReader[0];
              tempByteArray[currentIndex++] = rByte;
          } while ((rByte & 0xFF) != 0xD9); // thumb ends

// byte[] thumbBytes = new byte[currentIndex-1];
          tempByteArray[currentIndex++] = -1;
          Image im = Image.createImage(tempByteArray, 0, currentIndex - 1);
          tempByteArray = null;
          return im;

      }
      str.close();

      return null;
  }

  /**
   * Created a sized Image from a file
   * @param filename name of the image file
   * @param cW device width
   * @param cH device height
   * @return Image returns scaled image that should fit our screen dimensions
   */
  public static Image createImage (String filename,int cW,int cH) throws IOException, IOException{
    Image rImage = null;

      FileConnection fconn = (FileConnection) Connector.open (filename);
      InputStream inStream = fconn.openInputStream ();
      if (fconn.fileSize () > LARGESTBYTESTREAM){
        rImage = getThumbFromFile (inStream);
      } else{
        byte[] fileBytes = readStream2 (inStream,(int)fconn.fileSize ());

        inStream.close (); // Close streams and connections...we are done IO for now...
        fconn.close ();

        rImage = createImage (fileBytes);
        fileBytes = null;  // eligable for gc

      }
      Image scaledImage = getScaledImage (rImage, cW, cH);
      return scaledImage;

  }

  /**
   * Get a scaled image directly from inputstream
   * @param is resource input stream
   * @param cW screen width
   * @param cH screen height
   * @return Image if we can create/scale from is otherwise null
   */
  public static Image createImage(InputStream is,int cW,int cH){
    try{
       return getScaledImage(Image.createImage (is),cW,cH);
    }
    catch(Exception ex){
      Logger.warn(ex);
    }
    return null;
  }

  /**
   * Creates an image direct from array of bytes if size permits us.
   * @param encodedImage the image in an array of bytes
   * @return Image
   */
  private static Image createImage ( byte[] encodedImage){
    try {
      Image dispImage = null;
      if ( encodedImage.length > LARGESTBYTESTREAM ) {
        return  getJPEGthumb ( encodedImage );
      }
      if ( dispImage == null ) {
        return Image.createImage ( encodedImage, 0, encodedImage.length );
      }
    } catch( Exception e ) {
      System.gc ();
      try {
        return getJPEGthumb ( encodedImage );
      }
      catch( Exception e2 ) {
        Logger.warn(e2);
      }
    }
    return null;
  }

  /**
   * Fit the image on our screen size (width, height)
   * @param inImage the image to be resized
   * @param scaleFactor the factor with which we want to scale
   * @param scalePwr shift width/height scalePwr bits
   * @return Image - the resized image
   */
  public static Image resize ( Image inImage, int rotation, int scaleFactor, int scalePwr ){
    if (inImage==null) {
      return null;
    }

    int inImWidth = inImage.getWidth ();
    int inImHeight = inImage.getHeight ();

    int outWidth;
    int outHeight;
    int rgbRowIn[];
    int inImStartW;
    int inImStartH;

    // Adjust inImStartW with inImStepW for every step in height direction of outImage.
    int inImStepW;
    // Adjust inImStartH with inImStepH for every step in height direction of outImage.
    int inImStepH;
    // Start index in rgbRow.
    int rgbRowStartIndex;
    // Increase currW (rgbRow index) with stepRgbRow for every step in width direction of outImage.
    int stepRgbRow;
    int getRGBwidth;
    int getRGBheight;


    rgbRowIn = new int[inImWidth];
    outHeight = (inImHeight << scalePwr) / scaleFactor;
    outWidth = (inImWidth << scalePwr) / scaleFactor;
    inImStartW = 0;
    inImStepW = 0;

    inImStartH = 0;
    inImStepH = scaleFactor;
    rgbRowStartIndex = 0;
    stepRgbRow = scaleFactor;

    getRGBwidth = inImWidth;
    getRGBheight = 1;
    int rgbRowOut[] = new int[outWidth];
    Image outImage = Image.createImage ( outWidth, outHeight );
    if (outImage==null) return null;

    Graphics imGraphic = outImage.getGraphics ();
    if ( imGraphic==null || rgbRowIn==null || rgbRowOut==null ) {
      outImage = null;
      return null;
    }
    for ( int h = 0; h < outHeight; h++ ) {
      int currW = rgbRowStartIndex;
      int x = Math.max ( 0, Math.min ( inImWidth - 1, inImStartW >> scalePwr ) );
      int y = Math.max ( 0, Math.min ( inImHeight - 1, inImStartH >> scalePwr ) );

      inImage.getRGB ( rgbRowIn, 0, getRGBwidth, x, y, getRGBwidth, getRGBheight );
      for ( int w=0; w<outWidth; w++ ) {
        rgbRowOut[w] = rgbRowIn[ Math.max ( 0, Math.min ( rgbRowIn.length, currW >> scalePwr ) ) ];
        currW += stepRgbRow;
      }
      imGraphic.drawRGB ( rgbRowOut, 0, outWidth, 0, h, outWidth, 1, false );
      inImStartW += inImStepW;
      inImStartH += inImStepH;
    }

    return outImage;
  }







}

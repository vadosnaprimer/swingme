package javax.microedition.amms;

import java.io.OutputStream;

import javax.microedition.amms.control.ImageFormatControl;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Control;
import java.awt.image.BufferedImage;

public class AwtJpegEncoder implements MediaProcessor, ImageFormatControl {

    String format;
    Image image;
    OutputStream outputStream;

    public void complete() throws javax.microedition.media.MediaException {

        String imageIOformat = format.substring( "image/".length() );
        
        try {
             if (!javax.imageio.ImageIO.write(image._image, imageIOformat, outputStream) ) {
                 throw new Exception("ImageIO.write return false");
             }
        }
        catch(Throwable th) {
            
            if ("jpeg".equals(imageIOformat) || "jpg".equals(imageIOformat)) {
                try {
                    System.out.println("failed to save with ImageIO, falling back to com.sun.image.codec.jpeg");
                    BufferedImage img = image._image;
                    com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(outputStream);
                    com.sun.image.codec.jpeg.JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
                    param.setQuality( 0.5f, false);
                    encoder.setJPEGEncodeParam(param);
                    encoder.encode(img);
                    outputStream.close();
                    return; // yay, everything worked!
                }
                catch (Throwable ex) { } // anything really can go wrong here, but we do not really care
            }

            javax.microedition.media.MediaException mex = new javax.microedition.media.MediaException();
            mex.initCause(th);
            throw mex;
        }
    }

    public void setInput(Object inputStream) {
        this.image = (Image)inputStream;
    }

    public void setOutput(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Control getControl(String controlType) {
        return this;
    }

    public Control[] getControls() {
        throw new RuntimeException();
    }

    public void setFormat(String string) {
        format = string;
    }

}

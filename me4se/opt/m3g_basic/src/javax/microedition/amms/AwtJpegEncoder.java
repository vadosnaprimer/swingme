package javax.microedition.amms;

import java.io.OutputStream;

import javax.microedition.amms.control.ImageFormatControl;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Control;
import java.awt.image.BufferedImage;

public class AwtJpegEncoder implements MediaProcessor, ImageFormatControl {

    Image image;
    OutputStream outputStream;

    public void complete() throws javax.microedition.media.MediaException {

        try {
             if (javax.imageio.ImageIO.write(image._image, "jpg", outputStream) ) {
                 return;
             }
        }
        catch(Throwable th) {
            th.printStackTrace();
        }

        System.out.print("failed to save with ImageIO, falling back to com.sun.image.codec.jpeg");

        try {
        
            BufferedImage img = image._image;
            com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(outputStream);
            com.sun.image.codec.jpeg.JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
            param.setQuality( 0.5f, false);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(img);
            outputStream.close();
        }
        catch (Exception ex) {
            javax.microedition.media.MediaException mex = new javax.microedition.media.MediaException();
            mex.initCause(ex);
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
        // only jpeg is supported here
    }

}

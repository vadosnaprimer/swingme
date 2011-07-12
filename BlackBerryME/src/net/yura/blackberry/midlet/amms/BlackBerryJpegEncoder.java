package net.yura.blackberry.midlet.amms;

import java.io.OutputStream;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.JPEGEncodedImage;

public class BlackBerryJpegEncoder implements MediaProcessor, ImageFormatControl {

    Image image;
    OutputStream outputStream;
    
    public void complete() throws javax.microedition.media.MediaException {
        
        try {
            int[] data = new int[image.getWidth()*image.getHeight()];
            image.getRGB(data, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            
            Bitmap bitmap = new Bitmap(image.getWidth(),image.getHeight());
            bitmap.setARGB(data, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            
            JPEGEncodedImage encode = JPEGEncodedImage.encode(bitmap, 80);
            
            byte[] bytes = encode.getData();
            
            outputStream.write(bytes);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new MediaException(ex.toString());
        }
    }

    public void setInput(Image inputStream) {
        this.image = inputStream;
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

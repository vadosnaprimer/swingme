package net.yura.blackberry.rim.amms;

import java.io.OutputStream;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import net.rim.device.api.system.JPEGEncodedImage;
import net.yura.blackberry.rim.Image;

public class BlackBerryJpegEncoder implements MediaProcessor, ImageFormatControl {

    Image image;
    OutputStream outputStream;
    
    public void complete() throws javax.microedition.media.MediaException {
        
        try {
            JPEGEncodedImage encode = JPEGEncodedImage.encode(image.getBitmap(), 80);
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

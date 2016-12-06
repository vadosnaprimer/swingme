package javax.microedition.amms;

import java.io.OutputStream;

import javax.microedition.amms.control.ImageFormatControl;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Control;
import android.graphics.Bitmap.CompressFormat;

public class AndroidJpegEncoder implements MediaProcessor, ImageFormatControl {

    Image image;
    OutputStream outputStream;

    public void complete() throws javax.microedition.media.MediaException {

        boolean result = image.getBitmap().compress(CompressFormat.JPEG, 95, outputStream);

        if (!result) {
            throw new javax.microedition.media.MediaException("saving failed, compress returned false");
        }
    }

    public void setInput(Object image) {
        this.image = (Image)image;
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

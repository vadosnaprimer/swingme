package javax.microedition.amms;

import java.io.OutputStream;

public interface MediaProcessor extends javax.microedition.media.Controllable {

    /**
     * The API for this method says it HAS to take an {@link Object}, or we could get a NoSuchMethodError
     * But the only Object that is allowed is {@link javax.microedition.lcdui.Image}
     * @param image an instance of {@link javax.microedition.lcdui.Image}
     */
    void setInput(Object image);

    void setOutput(OutputStream outputStream);

    void complete() throws javax.microedition.media.MediaException;
}

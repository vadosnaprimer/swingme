package net.yura.blackberry.rim.amms;

import java.io.OutputStream;
import net.yura.blackberry.rim.Image;

public interface MediaProcessor extends javax.microedition.media.Controllable {

    public void setInput(Image inputStream);

    public void setOutput(OutputStream outputStream);

    public void complete() throws javax.microedition.media.MediaException;

}

package javax.microedition.amms;

import java.io.OutputStream;

public interface MediaProcessor extends javax.microedition.media.Controllable {

    public void setInput(Object inputStream);

    public void setOutput(OutputStream outputStream);

    public void complete() throws javax.microedition.media.MediaException;

}

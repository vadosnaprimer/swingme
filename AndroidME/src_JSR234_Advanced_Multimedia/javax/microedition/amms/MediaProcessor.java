package javax.microedition.amms;

import java.io.OutputStream;
import javax.microedition.lcdui.Image;

public interface MediaProcessor extends javax.microedition.media.Controllable {

    public void setInput(Image inputStream);

    public void setOutput(OutputStream outputStream);

    public void complete() throws javax.microedition.media.MediaException;

}

package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.file.FileConnection;
import net.yura.mobile.io.proto.CodedOutputStream;

/**
 * @author Yura Mamyrin
 */
public class ProtoFileUtil extends ProtoUtil {

    protected int getObjectTypeEnum(Object obj) {
        if (obj instanceof FileConnection) {
            return BinUtil.TYPE_BYTE_ARRAY;
        }
        return super.getObjectTypeEnum(obj);
    }

    protected int computeByteArraySize(Object obj) {
        if (obj instanceof FileConnection) {
            try {
                FileConnection file = (FileConnection)obj;
                return (int)file.fileSize();
            }
            catch(Exception ex) {
                //#debug
                ex.printStackTrace();
                return 0;
            }
        }
        else {
            return super.computeByteArraySize(obj);
        }
    }

    protected void encodeByteArray(CodedOutputStream out,Object obj) throws IOException {
        if (obj instanceof FileConnection) {
            int COPY_BLOCK_SIZE=1024;
            FileConnection file = (FileConnection)obj;
            InputStream is = file.openInputStream();
            byte[] data = new byte[COPY_BLOCK_SIZE];
            int i = 0;
            while( ( i = is.read(data,0,COPY_BLOCK_SIZE ) ) != -1  ) {
                out.writeRawBytes(data,0,i);
            }
            NativeUtil.close(is);
        }
        else {
            super.encodeByteArray(out, obj);
        }
    }

}

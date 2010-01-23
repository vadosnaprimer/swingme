package net.yura.server.gen;


import java.io.IOException;
import java.util.Hashtable;
import net.yura.mobile.io.ProtoUtil;
import net.yura.mobile.io.proto.CodedInputStream;
import net.yura.mobile.io.proto.CodedOutputStream;

public class ProtoAccess extends ProtoUtil {

    public static final int TYPE_MESSAGE = 20;
    public static final int TYPE_LOGIN = 21;

    @Override
    protected Object decodeObject(CodedInputStream in2,int type) throws IOException {
        switch (type) {
            case TYPE_MESSAGE: return decodeMessage(in2);
            case TYPE_LOGIN: return decodeLogin(in2);
            default: return super.decodeObject(in2, type);
        }
    }


    @Override
    protected int computeObjectSize(Object obj,int type) {
        switch (type) {
            case TYPE_MESSAGE: return computeMessageSize( (Message)obj );
            case TYPE_LOGIN: return computeLoginSize( (Hashtable)obj );
            default: return super.computeObjectSize(obj,type);
        }
    }


    @Override
    protected void encodeObject(CodedOutputStream out, Object obj,int type) throws IOException {
        switch (type) {
            case TYPE_MESSAGE: encodeMessage( out,(Message)obj ); break;
            case TYPE_LOGIN: encodeLogin( (Hashtable)obj ); break;
            default: super.encodeObject(out,obj,type); break;
        }
    }


    @Override
    protected int getObjectTypeEnum(Object obj) {
        if (obj instanceof Hashtable) {
            Hashtable table = (Hashtable)obj;

            if (table.size() == 2 && table.get("username")!=null && table.get("password")!=null) {
                return TYPE_LOGIN;
            }

        }

        if (obj instanceof Message) {
            return TYPE_MESSAGE;
        }
        else {
            return super.getObjectTypeEnum(obj);
        }

    }


    private int computeLoginSize(Hashtable table) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private int computeMessageSize(Message message) {

        int method = getMessageTypeEnum(message.method);

        throw new UnsupportedOperationException("Not yet implemented");
    }
    private Hashtable decodeLogin(CodedInputStream in2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private Message decodeMessage(CodedInputStream in2) {

        Message message = new Message();
        message.method = getMessageTypeString(5);

        throw new UnsupportedOperationException("Not yet implemented");
    }
    private void encodeLogin(Hashtable table) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private void encodeMessage(CodedOutputStream out, Message message) {

        int method = getMessageTypeEnum(message.method);

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int getMessageTypeEnum(String method) {
        if ("hello".equals(method)) return 5;
        if ("goodbuy".equals(method)) return 4;
        return -1;
    }
    private String getMessageTypeString(int i) {
        switch (i) {
            case 5: return "hello";
            case 4: return "goodbuy";
            default: return "unknown "+i;
        }
    }

    class Message {
        String method;
    }

}

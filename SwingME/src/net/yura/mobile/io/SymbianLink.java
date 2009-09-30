package net.yura.mobile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Yura Mamyrin
 */
public class SymbianLink extends SocketClient {

    private SymbianAccess access;

    // TODO do not forget that all messages that failed to send go into the offlinebox
    public SymbianLink() {
        super("127.0.0.1:4444");
    }

    protected void handleObject(Object obj) {
        Task task = (Task)obj;

        //#debug
        System.out.println("dont know what to do with task "+task);
    }

    protected void updateState(int c) {
        // TODO
    }

    protected void write(OutputStream out, Object object) throws IOException {
        access.save(out, object);
    }

    protected Object read(InputStream in) throws IOException {
        return access.load(in);
    }

    protected void connected(InputStream in, OutputStream out) {
        access = new SymbianAccess();
    }

    protected void disconnected() {
        access = null;
    }

    public static class Task {
        private String method;
        private Object object;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
        public String toString() {
            return method +" "+object;
        }
    }

    // THIS IS GENERATED CODE! DO NOT EDIT
    public static class SymbianAccess extends BinUtil {
        public static final int TYPE_TASK=20;
        protected void writeObject(DataOutputStream out, Object object) throws IOException {
            if (object instanceof Task) {
                out.writeInt(TYPE_TASK);
                saveTask(out,(Task)object);
            }
            else {
                super.writeObject(out, object);
            }
        }
        protected void saveTask(DataOutputStream out,Task object) throws IOException {
            out.writeInt(2);
            writeObject(out, object.getMethod() );
            writeObject(out, object.getObject() );
        }
        protected Object readObject(DataInputStream in,int type,int size) throws IOException {
            switch (type) {
                case TYPE_TASK: return readTask(in,size);
                default: return super.readObject(in,type,size);
            }
        }
        protected Task readTask(DataInputStream in,int size) throws IOException {
            Task object = new Task();
            if (size>0) {
                object.setMethod( (String)readObject(in) );
            }
            if (size>1) {
                object.setObject( (Object)readObject(in) );
            }
            if (size>2) {
                skipUnknownObjects(in,size - 2);
            }
            return object;
        }
    }
}

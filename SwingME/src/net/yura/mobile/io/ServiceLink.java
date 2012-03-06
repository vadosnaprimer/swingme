package net.yura.mobile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.SystemUtil;

/**
 * @author Yura Mamyrin
 */
public class ServiceLink extends SocketClient {
    protected static int majorVersion = 1;
    protected static int minorVersion = 0;
    protected static int earliestSupportedMajorVersion = 1;
    protected static int earliestSupportedMinorVersion = 0;
    protected Hashtable register;
    private int disconnectionCounter;

    public void setDetailedLogging(Boolean b) {
        if (isConnected()) {
        	sendTask(new ServiceLink.Task("PutOptionDetailedLog", b));
        }
    }

    public interface TaskHandler {
        void handleTask(Task task);
    }

    private ServiceAccess access;

    // TODO do not forget that all messages that failed to send go into the offlinebox
    protected ServiceLink() {
        super("127.0.0.1:4444");
        register = new Hashtable();
    }
    
    private static ServiceLink serviceLink;

    /**
     * NOTHING SHOULD BE DONE LIKE THIS EXCEPT THIS!!!!
     */
    public static ServiceLink getInstance() {
        if (serviceLink == null) {
            serviceLink = new ServiceLink();
            Hashtable info = new Hashtable();
            info.put("major", new Integer(majorVersion));
            info.put("minor", new Integer(minorVersion));
            serviceLink.sendTask(new Task("GetVersion", (Object) info));
        }
        return serviceLink;
    }
    
    public void sendTask(Task task) {
    	//if (Midlet.getPlatform()==Midlet.PLATFORM_NOKIA_S60) {
    		addToOutbox(task);
    	//}
    }

    public void registerForTask(String method, Object handler) {
        register.put(method, handler);
    }


    public void handleObject(Object obj) {
        Task task = (Task)obj;

        String method = task.getMethod();

        if ("PutVersion".equals(method)) {
            Hashtable info = (Hashtable) task.getObject();
            Integer major = (Integer) info.get("major");
            Integer minor = (Integer) info.get("minor");
            if ((major.intValue() < earliestSupportedMajorVersion) ||
                ((major.intValue() == earliestSupportedMajorVersion) &&
                 (minor.intValue() < earliestSupportedMinorVersion)))
                 disconnect();
        }
        else {
            TaskHandler handler = (TaskHandler) register.get(method);
            if (handler != null)
                handler.handleTask(task);
            //#debug warn
            else Logger.warn("dont know what to do with task " + task);
        }
    }

    protected void updateState(int aState) {
        if (aState == DISCONNECTED) {
            disconnectionCounter++;
            if (disconnectionCounter >= 1) {
                // it aint gonna happen...
                disconnect();
            }
        }
    }

    protected void write(OutputStream out, Object object) throws IOException {
        access.save(out, object);
    }

    protected Object read(InputStream in) throws IOException {
        return access.load(in);
    }

    protected void connected(InputStream in, OutputStream out) {
        access = new ServiceAccess();
    }

    protected void disconnected() {
        access = null;
    }

    public boolean isConnected() {
        return access != null;
    }

    public static class Task {
        private String method;
        private Object object;
        
        public Task(String aMethod, Object aObject) {
            method = aMethod;
            object = aObject;
        }
        public Task(String aMethod) {
            method = aMethod;
        }
        public Task() {
        }

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

        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + (this.method != null ? this.method.hashCode() : 0);
            hash = 37 * hash + (this.object != null ? this.object.hashCode() : 0);
            return hash;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Task) {
                Task t2 = (Task)obj;
                return SystemUtil.equals(getMethod(),t2.getMethod()) &&
                       SystemUtil.equals(getObject(),t2.getObject());
            }
            return false;
        }

        //#mdebug debug
        public String toString() {
            return method +" "+object;
        }
        //#enddebug
    }

    // THIS IS GENERATED CODE! DO NOT EDIT
    public static class ServiceAccess extends BinUtil {
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

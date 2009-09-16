package net.yura.tools.mobilegen.model;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Lenin
 */
public class Test {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void main(String[] args) {

        Hashtable table1 = new Hashtable();
        table1.put(new Test(), new TestObject());
        Vector vector1 = new Vector();
        TestObject to1 = new TestObject();
        to1.setAge( (byte)22 );
        to1.setName("lala");
        vector1.addElement("bob");
        vector1.addElement(to1);
        table1.put("fred", vector1);
        Hashtable login = new Hashtable();
        // the keys in this hashtable correspond to a known
        // messagetype, so in the server end this should come out as a Login
        // object as defined in the proto file
        login.put("username", "yura");
        login.put("password", "pa55word");
        table1.put("login", login);

    }

}

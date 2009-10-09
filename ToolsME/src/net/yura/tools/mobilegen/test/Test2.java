/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.tools.mobilegen.test;

import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.yura.server.gen.TestProtos.Bob;
import net.yura.server.gen.TestProtos.ObjectType;
import net.yura.server.gen.TestProtos.Test;
import net.yura.server.gen.TestProtos.TestObject;
import net.yura.server.gen.TestProtos.Vector;

/**
 *
 * @author Administrator
 */
public class Test2 {

    public static void main(String... args) throws Exception {


        Test test1 = Test.newBuilder().setId(55).build();
        TestObject test2 = TestObject.newBuilder().setId(5335).setAge(-5).setName("Gina").build();
        Test test3 = Test.newBuilder().setId(53344335).build();

        Vector.Builder addressBook = Vector.newBuilder();

        addressBook.addElements( getObject(test1) );
        addressBook.addElements( getObject(test2) );
        addressBook.addElements( getObject(test3) );

        addressBook.addElements( getTest1() );


        Vector v = addressBook.build();
//System.out.println("v=" + v);
//        v.writeTo(out);

        Bob.Builder bob = Bob.newBuilder();

        List<Vector> vec1 = new ArrayList<Vector>();
        vec1.add(v);
        vec1.add(v);
        vec1.add(v);

        bob.addAllVec1(vec1);
        bob.addAllVec2(vec1);
        bob.addAllVec3(vec1);
        
        Bob b = bob.build();
System.out.println("b1="+b);
        byte[] bbytes = getObject(b).toByteArray();

        com.google.protobuf.GeneratedMessage obj = getObject( net.yura.server.gen.TestProtos.Object.parseFrom( new ByteArrayInputStream(bbytes) ) );

System.out.println("b2="+obj);
        Bob outbob = (Bob)obj;
        Vector vout = outbob.getVec1(0); // 3 of them, also 3 vec2 and 3 vec3
        net.yura.server.gen.TestProtos.String sout = (net.yura.server.gen.TestProtos.String)getObject(vout.getElements(3));

System.out.println("equals="+b.equals(obj)+" getting string: "+sout.getValue() );
    }

    public static net.yura.server.gen.TestProtos.Object getTest1() throws Exception {
        net.yura.server.gen.TestProtos.String.Builder sb = net.yura.server.gen.TestProtos.String.newBuilder();
        sb.setValue("bob the builder");
        return getObject(sb.build());
    }

    public static net.yura.server.gen.TestProtos.Object getObject(com.google.protobuf.GeneratedMessage message) throws Exception {

        //ByteArrayOutputStream out = new ByteArrayOutputStream();

        net.yura.server.gen.TestProtos.Object.Builder prefix = net.yura.server.gen.TestProtos.Object.newBuilder();

        if (message instanceof Vector) {
            prefix.setObjectType(ObjectType.TYPE_VECTOR);
        }
        else if (message instanceof Bob) {
            prefix.setObjectType(ObjectType.BOB);
        }
        else if (message instanceof Test) {
            prefix.setObjectType(ObjectType.TEST);
        }
        else if (message instanceof TestObject) {
            prefix.setObjectType(ObjectType.TEST_OBJECT);
        }
        else if (message instanceof net.yura.server.gen.TestProtos.String) {
            prefix.setObjectType(ObjectType.TYPE_STRING);
        }
        else {
            throw new RuntimeException();
        }

        prefix.setValue( message.toByteString() );

        return prefix.build();
    }

    public static com.google.protobuf.GeneratedMessage getObject(net.yura.server.gen.TestProtos.Object object) throws Exception {

        ObjectType type = object.getObjectType();
        ByteString bytes = object.getValue();

        com.google.protobuf.GeneratedMessage message = null;

        if (type == ObjectType.TYPE_VECTOR) {
            message = Vector.parseFrom(bytes);
        }
        else if (type == ObjectType.BOB) {
            message = Bob.parseFrom(bytes);
        }
        else if (type == ObjectType.TEST) {
            message = Test.parseFrom(bytes);
        }
        else if (type == ObjectType.TEST_OBJECT) {
            message = TestObject.parseFrom(bytes);
        }
        else if (type == ObjectType.TYPE_STRING) {
            message = net.yura.server.gen.TestProtos.String.parseFrom(bytes);
        }
        else {
            throw new RuntimeException();
        }

        return message;

    }

}

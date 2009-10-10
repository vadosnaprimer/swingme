package net.yura.tools.mobilegen.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import net.yura.mobile.gen.BinAccess;

import net.yura.mobile.gen.XMLAccess;
import net.yura.mobile.io.ProtoUtil;
import net.yura.tools.mobilegen.model.Test;
import net.yura.tools.mobilegen.model.TestObject;
import org.kxml2.io.KXmlParser;

/**
 * @author Yura Mamyrin
 */
public class Test1 {

    static abstract class ReadWrite {

        InputStream is;
        OutputStream os;
        {
            try {

//                os = new ByteArrayOutputStream();

                is = new PipedInputStream();
                os = new PipedOutputStream( (PipedInputStream)is );

//                PipedInputStream pipe = new PipedInputStream();
//                is = new BufferedInputStream(pipe);
//                os = new BufferedOutputStream( new PipedOutputStream( pipe ) );
            }
            catch(Exception ex) {
                throw new RuntimeException();
            }
        }

        abstract void save(Object o) throws Exception;
        abstract Object read() throws Exception;

        private void writeDone() throws IOException {
            os.flush();
//            is = new ByteArrayInputStream( ((ByteArrayOutputStream)os).toByteArray() );
        }
    }


    public static void main(String... args) throws Exception {

/*
        ReadWrite kxml = new ReadWrite() {
            XMLAccess xml = new XMLAccess();
            @Override
            void save(Object o) throws Exception {
                xml.save(os, o);
            }
            @Override
            Object read() throws Exception {
                // this works as the kxml will always read everything out of each new reader
                // even adding a buffers will still always work fine, as the buffers will
                // never store something and not return it to the kxml reader on a read request
                return xml.load( new BufferedReader( new InputStreamReader( new BufferedInputStream(is)) ) );
            }
        };

        doTest(kxml);

        ReadWrite ybin = new ReadWrite() {
            BinAccess bin = new BinAccess();
            @Override
            void save(Object o) throws Exception {
                bin.save(os,  o);
            }
            @Override
            Object read() throws Exception {
                return bin.load(is);
            }
        };

        doTest(ybin);


        ReadWrite kxml2 = new ReadWrite() {
            XMLAccess xml = new XMLAccess() {
                // THIS WILL FAIL
                // as it makes a new KXmlParser each time the stream is read
                // this is bad as the previous KXmlParser would have overread into the next message
                // and this will make it ignore that part
                @Override
                public Object load(Reader reader) throws IOException {
                    try {
                        KXmlParser parser = new KXmlParser();
                        parser.setInput(reader);
                        parser.nextTag();
                        return readObject(parser);
                    }
                    catch(IOException ex) {
                        throw ex;
                    }
                    catch(Exception ex) { // other exceptions here are just IOException really
                        ex.printStackTrace();
                        throw new IOException(ex.toString());
                    }
                }
            };
            @Override
            void save(Object o) throws Exception {
                xml.save(os,  o);
            }
            @Override
            Object read() throws Exception {
                return xml.load( new InputStreamReader(is) );
            }
        };
        doTest(kxml2);


        ReadWrite proto = new ReadWrite() {
            ProtoAccess bin = new ProtoAccess();
            @Override
            void save(Object o) throws Exception {
                bin.writeObject(o, new ProtoOutputStream( os ) );
            }
            @Override
            Object read() throws Exception {
                return bin.readObject( new ProtoInputStream(is) );
            }
        };
        doTest(proto);


        ReadWrite proto2 = new ReadWrite() {
            ProtoUtil bin = new ProtoUtil();
            @Override
            void save(Object o) throws Exception {
                bin.save(os, o);
            }
            @Override
            Object read() throws Exception {
                return bin.load(is);
            }
        };
*/




        ReadWrite proto2 = new ReadWrite() {
            ProtoUtil bin = new ProtoUtil();

            int size;

            @Override
            void save(Object o) throws Exception {

//                ByteArrayOutputStream out = new ByteArrayOutputStream();
                size = bin.save(os, o);
//                byte[] bytes = out.toByteArray();
//                System.out.println("bytes array size "+bytes.length);
//                for (int c=0;c<bytes.length;c++) {
//                    System.out.println("reading "+c+" "+bytes[c]);
//                }
//                os.write( bytes );
                //os.write( ((net.yura.server.gen.TestProtos.Object)o).toByteArray() );
            }
            @Override
            Object read() throws Exception {
                return bin.load(is, size);
//                int c=0;
//                while (is.available()>0) {
//                    c++;
//                    System.out.println("reading "+c+" "+is.read());
//                }
//                return null; // net.yura.server.gen.TestProtos.Object.parseFrom( bytes );
            }
        };


        doTest(proto2);
 

    }



    public static void doTest(final ReadWrite util) throws Exception {

        final Vector objects = new Vector();
        objects.add(getTestObject2());
        //objects.add( Test2.getTest1() );
        /*
        objects.add(getTestObject4());
        objects.add(getTestObject3());
        objects.add(getTestObject4());
        objects.add(getTestObject3());
        objects.add(getTestObject4());
        objects.add(getTestObject3());
*/
        Thread a = new Thread() {
            @Override
            public void run() {
                try {
                    for (Object obj:objects) {
                        util.save(obj);
                    }
                    util.writeDone();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        a.start();

        Thread.sleep(3000);

        System.out.println();

        XMLAccess kxml = new XMLAccess();

        for (Object obj:objects) {
            Object o1 = util.read();
            System.out.print("equals=" +obj.equals(o1) +" "+o1);
            kxml.save(System.out, o1);
            System.out.println();
        }

    }

    public static Object getTestObject1() {

        return "bob the builder";

    }


    public static Object getTestObject2() {

        Vector bob = new Vector();

        bob.add("hello1");

        Vector bob2 = new Vector();
        bob2.add( new Integer(-666) );
        bob2.add(null);
        bob2.add( new Float(666) );

        bob.add(bob2);

        //bob.add(new byte[] {1,2,3,4,5,6,7,8,9,0} ); // kills the kml print out
        bob.add("lala");
        bob.add(null);
        //bob.add( new Object[] {"bob","the","builder","no",new Short( (short)5 ) } );

        Hashtable table = new Hashtable();
        table.put(new Double(123), new Double(456));
        table.put("KeyExample","ValueExample");

        bob.add(table);
        return bob;
    }

    public static Object getTestObject3() {

        Hashtable table1 = new Hashtable();
        table1.put(new Test(), new TestObject());
        Vector vector1 = new Vector();
        TestObject to1 = new TestObject();

        to1.setImage( new byte[] {1,2,3,4,5,6,7,8,9,0,} );

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

        return table1;

    }

    public static Object getTestObject4() {

        TestObject product2 = new TestObject();
        product2.setName("yura1");

        Vector bob = new Vector();
        bob.add(5);
        bob.add(5.5);
        bob.add(5.6);
        bob.add(5.7);
        bob.add(product2);
        bob.add(null);

        Map map = new Hashtable();
        map.put("username","bob");
        map.put("password","bob123");

        //map.put("MyElement",new Object[] {} ); // can not check this as equals in Hashtable does not use Arrays.equals
        //bob.add( new Object[] {} ); // can not do this either
        
        map.put("MyElement2",bob);

        map.put("p1",product2);

        TestObject product = new TestObject();
        product.setName("yura");

        map.put("p2",product);

        final Vector vector = new Vector();
        vector.add(map);
        vector.add(product);

        TestObject product1 = new TestObject();
        product1.setName("Gina");

        product1.addNumber("1234567890DD");
        product1.addNumber("6969696969DD");

        Test client1 = new Test();
        client1.setId(666);

        TestObject booking1 = new TestObject();
        booking1.setObjects( new Object[] {client1,client1} );

        vector.add(booking1);
        vector.add(product1);

        return vector;
    }


}

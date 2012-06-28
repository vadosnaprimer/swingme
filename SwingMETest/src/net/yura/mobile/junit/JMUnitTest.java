package net.yura.mobile.junit;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import jmunit.framework.cldc11.*;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.io.RMSUtil;
import net.yura.mobile.util.SystemUtil;

/**
 * @author Yura Mamyrin
 */
public class JMUnitTest extends TestCase {
    
    public JMUnitTest() {
        //The first parameter of inherited constructor is the number of test cases
        super(3, "JMUnitTest");
    }    
    
    public void test(int testNumber) throws Throwable {
        
        switch(testNumber) {
            case 0: testXULLoaderAdjustSizeToDensity(); break;
            case 1: testRMSUtil(); break;
            case 2: testSocketGet(); break;
        }
        
    }

    private void testXULLoaderAdjustSizeToDensity() {

        //    36x36 for low-density
        //    48x48 for medium-density
        //    72x72 for high-density
        //    96x96 for extra high-density

        int source = 36;

        XULLoader.setDPI("ldpi");
        assertEquals(36, XULLoader.adjustSizeToDensity(source) );
        
        XULLoader.setDPI("mdpi");
        assertEquals(48, XULLoader.adjustSizeToDensity(source) );
        
        XULLoader.setDPI("hdpi");
        assertEquals(72, XULLoader.adjustSizeToDensity(source) );
        
        XULLoader.setDPI("xhdpi");
        assertEquals(96, XULLoader.adjustSizeToDensity(source) );
        
        XULLoader.setDPI("tvdpi");
        assertEquals(64, XULLoader.adjustSizeToDensity(source) );
    }
    
    public void testRMSUtil() throws Exception {
        
            Hashtable obj = new Hashtable();
            obj.put("tests", new Vector());
            obj.put(new Integer(5), new Double(5.5));
            obj.put(new Object[] {"String",new Integer(666)}, new Hashtable());


            RMSUtil.save("Test1", obj);

            Object obj2 = RMSUtil.load("Test1");

            assertTrue( SystemUtil.equals(obj, obj2) );

    }
    
    
    void testSocketGet() throws Exception {
        
/*
        StreamConnection socket = (StreamConnection)Connector.open("socket://google.com:80");
        //Socket socket = SSLSocketFactory.getDefault().createSocket("google.com", 443 );
        //Socket socket = SocketFactory.getDefault().createSocket("google.com", 80 );

        String post = "GET / HTTP/1.0\r\n" + // 1.1 does not close the InputStream
        "Host: google.com\r\n" +
        "User-Agent: Mozilla/5.0 (Windows NT 5.2; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2\r\n" +
        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*€/*;q=0.8\r\n" +
        // "Accept-Language: en-us,en;q=0.5\r\n" +
        // "Accept-Encoding: gzip, deflate\r\n" +
        // "Connection: keep-alive\r\n" +
        // "Referer: http://yura.net/\r\n" +
        "\r\n";

        OutputStream out = socket.openOutputStream();
        InputStream in = socket.openInputStream();

        out.write( post.getBytes("ISO-8859-1") );
        out.flush();

        byte[] bytes = SystemUtil.getData(in, -1);

        String result = new String(bytes,"ISO-8859-1");
        System.out.println( result );
*/
        
    }
}

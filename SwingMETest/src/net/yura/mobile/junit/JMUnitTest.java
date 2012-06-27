package net.yura.mobile.junit;

import java.util.Hashtable;
import java.util.Vector;
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
        super(2, "JMUnitTest");
    }    
    
    public void test(int testNumber) throws Throwable {
        
        switch(testNumber) {
            case 0: testXULLoaderAdjustSizeToDensity(); break;
            case 1: testRMSUtil(); break;
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
    
}

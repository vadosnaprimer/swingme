package net.yura.tools.mobilegen;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Yura Mamyrin
 */
public class MobileProtoGenTest {

    @Test
    public void testFirstUp() {
        System.out.println("firstUp");

        assertEquals("HelloWorld",MobileProtoGen.firstUp("HelloWorld") );
        assertEquals("HELLOWORLD",MobileProtoGen.firstUp("HELLO_WORLD") );
        assertEquals("HelloWorld",MobileProtoGen.firstUp("helloWorld") );
        assertEquals("HelloWorld",MobileProtoGen.firstUp("hello_world") );

    }
}

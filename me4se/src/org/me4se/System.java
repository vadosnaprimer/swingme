package org.me4se;

import java.io.PrintStream;
import java.util.Properties;

import javax.microedition.midlet.ApplicationManager;

/** 
 * Wrapper for the system class (Access to the System class will yield a security exception
 * in the Applet context.
 */

public class System {
   public  static PrintStream  err = java.lang.System.err;
   public static PrintStream  out = java.lang.System.out;
    
   public static Properties properties = new Properties();
   
   public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
       java.lang.System.arraycopy(src, srcPos, dest, destPos, length);
   }
       
   public static long currentTimeMillis() {
       return java.lang.System.currentTimeMillis();
   }

   public static void exit(int status) {
       if(ApplicationManager.getInstance().applet == null){
           java.lang.System.exit(status);
       }
   }
   
   public static void gc() {
       java.lang.System.gc();
    }

    public static String   getProperty(String key) {
//        System.out.println("org.me4se.System.getProperty(): trying to access property: "+key);
        
        String result = ApplicationManager.getInstance().applet == null 
            ? java.lang.System.getProperty(key) 
                : (String) properties.get(key);
        
//        System.out.println("org.me4se.System.getProperty(" + key + "): returning property: "+result);
            
        return result;
    }
        
    static int  identityHashCode(Object x) {
        return java.lang.System.identityHashCode(x);
    }
}

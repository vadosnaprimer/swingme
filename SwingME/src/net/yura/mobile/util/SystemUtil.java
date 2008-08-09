/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.util;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Yura Mamyrin
 */
public class SystemUtil {

    /**
     * @param src
     * @param dest
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    public static void hashtablePutAll(Hashtable src,Hashtable dest) {
            Enumeration en = src.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                dest.put(key, src.get(key));
            }
    }
}

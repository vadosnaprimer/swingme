/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.yura.mobile.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

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
    
    /**
     * @param a array
     * @return a Vector
     * @see java.util.Arrays#asList(java.lang.Object[]) Arrays.asList
     */
    public static Vector asList(Object[] a) {
        Vector vec = new Vector (a.length);
        for (int c=0;c<a.length;c++) {
            vec.addElement(a[c]);
        }
        return vec;
    }
    
    /**
     * @param des
     * @param src
     * @see java.util.Vector#addAll(java.util.Collection) Vector.addAll
     */
    public static void addAll(Vector des,Vector src) {

        for (int c=0;c<src.size();c++) {
            des.addElement(src.elementAt(c));
        }
        
    }
}

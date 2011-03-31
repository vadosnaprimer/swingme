/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.util;

import java.io.IOException;
import java.io.InputStream;
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

    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 ==null && obj2 == null) return true;
        if (obj1==null || obj2 == null) return false;

        if (obj1 instanceof Hashtable && obj2 instanceof Hashtable) {
            Hashtable hash1 = (Hashtable) obj1;
            Hashtable hash2 = (Hashtable) obj2;
            if (hash1.size() != hash2.size())
                return false;
            Enumeration enum1 = hash1.keys();
            while (enum1.hasMoreElements())
            {
                   Object objKey1 = enum1.nextElement();
                   Object objValue1 = hash1.get(objKey1);
                   if (!hash2.containsKey(objKey1))
                       return false;
                   Object objValue2 = hash2.get(objKey1);
                   if (!equals(objValue1,objValue2))
                       return false;
            }
        }
        else if (obj1 instanceof Vector && obj2 instanceof Vector) {
            Vector vector1 = (Vector) obj1;
            Vector vector2 = (Vector) obj2;
            if (vector1.size() != vector2.size())
                return false;
            for (int i = 0; i < vector1.size(); i++) {
                if (!equals(vector1.elementAt(i),vector2.elementAt(i)))
                    return false;
            }
        }
        else if (obj1 instanceof Object[] && obj2 instanceof Object[]) {
            Object[] objArray1 = (Object[])obj1;
            Object[] objArray2 = (Object[])obj2;
            if (objArray1.length != objArray2.length) return false;
            for (int i = 0; i < objArray1.length; i++) {
                if (!equals(objArray1[i], objArray2[i]))
                    return false;
            }
        }
        else {
            return obj1.equals(obj2);
        }
        return true;
    }


    /**
     * @see java.util.Arrays#sort(java.lang.Object[]) Arrays.sort
     * @see java.util.Collections#sort(java.util.List) Collections.sort
     */
    public static void sort(Vector vec) {
        quickSort(vec, 0, vec.size() - 1,null);
    }

    /**
     * @see java.util.Arrays#sort(java.lang.Object[], java.util.Comparator) Arrays.sort
     * @see java.util.Collections#sort(java.util.List, java.util.Comparator) Collections.sort
     */
    public static void sort(Vector vec, Comparator comp) {
        quickSort(vec, 0, vec.size() - 1,comp);
    }

    /**
     * This sorting is based on quick-sort algorithm.
     * This will sort the vector in ascending order.
     * @param vec The vector to be sorted
     * @param lo0 Starting index which will be 0 for vectors and arrays
     * @param hi0 Last index which will be size-1 for vectors and arrays
     */
    private static void quickSort(Vector vec, int lo0, int hi0,Comparator comp) {
        int lo = lo0;
        int hi = hi0;

        if (hi0 > lo0) {
            Object mid = vec.elementAt((lo0 + hi0) / 2);
            while (lo <= hi) {
                // Find the first element that is greater than or equal to the
                // partition element starting from the left Index.
                while ((lo < hi0) && (comp==null?String.valueOf(vec.elementAt(lo)).compareTo(String.valueOf(mid)):comp.compare(vec.elementAt(lo), mid)) < 0) {
                    lo++;
                }

                // find an element that is smaller than or equal to the
                // partition element starting from the right Index.
                while ((hi > lo0) && (comp==null?String.valueOf(vec.elementAt(hi)).compareTo(String.valueOf(mid)):comp.compare(vec.elementAt(hi), mid)) > 0) {
                    hi--;
                }

                // if the indexes have not crossed, swap
                if (lo <= hi) {
                    Object t1 = vec.elementAt(hi);
                    vec.setElementAt(vec.elementAt(lo), hi);
                    vec.setElementAt(t1, lo);
                    lo++;
                    hi--;
                }
            }

            // If the right index has not reached the left side of array must
            // now sort the left partition.
            if (lo0 < hi) {
                quickSort(vec, lo0, hi,comp);
            }

            // If the left index has not reached the right side of array must
            // now sort the right partition.
            if (lo < hi0) {
                quickSort(vec, lo, hi0,comp);
            }
        }
    }

    /**
     * @see java.util.Collections#binarySearch(java.util.List, java.lang.Object, java.util.Comparator) Collections.binarySearch
     */
    public static int binarySearch(Vector l, Object key, Comparator c) {
	int low = 0;
	int high = l.size()-1;

	while (low <= high) {
	    int mid = (low + high) >>> 1;
	    Object midVal = l.elementAt(mid);
	    int cmp = (c!=null)?c.compare(midVal, key):String.valueOf(midVal).compareTo(String.valueOf(key));

	    if (cmp < 0)
		low = mid + 1;
	    else if (cmp > 0)
		high = mid - 1;
	    else
		return mid; // key found
	}
	return -(low + 1);  // key not found
    }

    public static String getFileSafeName(String name) {
        String invalid = "/?<>\\:*|\" ";
        for (int c=0;c<invalid.length();c++) {
            name = StringUtil.replaceAll(name,String.valueOf(invalid.charAt(c)),"");
        }
        return name;
    }
    
    public static byte[] arrayCopy(byte[] b, int offset, int length){
    	byte[] n = new byte[length];
    	
    	for (int i = offset, j=0; i < offset + length; i++,j++){
    		n[j] = b[i];
    	}
    	
    	return n;
    }

}

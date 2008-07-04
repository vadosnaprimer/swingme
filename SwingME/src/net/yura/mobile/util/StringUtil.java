package net.yura.mobile.util;

import java.util.Vector;

/**
 * @author Yura Mamyrin
 */
public class StringUtil {

	public static String[] split(String value, String separator) {
		int len = value.length();
		if (len == 0)
			return new String[0];
		int start = 0;
		final Vector parts = new Vector();
		int end = value.indexOf(separator);
		while (start < end) {
			parts.addElement(value.substring(start, end));
			start = end + 1;
			end = value.indexOf(separator, start);
		}
		parts.addElement(value.substring(start, len));

              String[] array = new String[parts.size()];
              parts.copyInto(array);
              return array;
	}
        
           public static String[] split(String value, char delimiter) {
               
		int len = value.length();
		if (len == 0)
			return new String[0];
		int start = 0;
		final Vector parts = new Vector();
		int end = value.indexOf(delimiter);
		while (start < end) {
			parts.addElement(value.substring(start, end));
			start = end + 1;
			end = value.indexOf(delimiter, start);
		}
		parts.addElement(value.substring(start, len));

              String[] array = new String[parts.size()];
              parts.copyInto(array);
              return array;

        }

}

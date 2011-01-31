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

import java.util.Vector;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 */
public class StringUtil {

    
    private static final char TOKEN_PREFIX = '{';
    private static final char TOKEN_SUFFIX = '}';

    /**
     * @see java.lang.String#split(java.lang.String) String.split
     */
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

    /**
     * @see java.lang.String#split(java.lang.String) String.split
     */
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

    /**
     * @see java.lang.String#replaceAll(java.lang.String, java.lang.String) String.replaceAll
     */
        public static String replaceAll(String text, String searchString, String replacementString) {

            StringBuffer sBuffer = new StringBuffer();
            int pos = 0;
            while((pos = text.indexOf(searchString)) != -1){
                sBuffer.append(text.substring(0, pos) + replacementString);
                text = text.substring(pos + searchString.length());
            }
            sBuffer.append(text);
            return sBuffer.toString();

        }
    
        public static String replace(String str, String replace, String replacement) {
            StringBuffer buffer = new StringBuffer(str);

            int i = str.indexOf(replace);
            if (i>=0) {
                buffer.delete(i, i + replace.length());
                buffer.insert(i, replacement);
            }
            //#mdebug info
            else {
                Logger.info("can not replace "+replace+" with "+replacement+" in string "+str);
            }
            //#enddebug

            return buffer.toString();
        }

        /**
         * Replace a token in the format {I} where I is a positive integer.
         * Only one replacement will take place.
         * 
         * @return str with a replacement
         */
        public static String replaceToken(String str, int tokenIndex, String replacement) {
            char[] chars = str.toCharArray();
            char tokenIndexChar = (char) (tokenIndex + '0');

            for (int i=0; i<chars.length - 2; i++) {
                if (chars[i] == TOKEN_PREFIX
                        && chars[i+1] == tokenIndexChar
                        && chars[i+2] == TOKEN_SUFFIX) {
                    String before = str.substring(0,   i);
                    String after  = str.substring(i+3, str.length());
                    return before + replacement + after;
                }
            }

            /* No replacement took place. */
            return str;
        }


}

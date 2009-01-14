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

/**
 * @author Yura Mamyrin
 */
public class StringUtil {

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
     * @see java.io.String#replaceAll(java.lang.String, java.lang.String) String.replaceAll
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

}

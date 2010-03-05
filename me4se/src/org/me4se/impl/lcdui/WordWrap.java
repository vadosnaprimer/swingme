// kObjects / WordWrap
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// License: LGPL
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA

package org.me4se.impl.lcdui;

//import java.awt.*;

public class WordWrap {

    PhysicalFont fm;
    int width;
    String txt;
    int pos;

    public WordWrap(PhysicalFont fm, String txt, int width) {

        this.fm = fm;
        this.txt = txt;
        this.width = width;
    }

    /**
     *  returns -1 if no text is left.  supports hard breaks.
     */

    public int next() {

        //System.out.println ("next !");

        int i = pos;
        int len = txt.length();

        if (pos >= len)
            return -1;

        int start = pos;

        while (true) {
            while (i < len && txt.charAt(i) > ' ')
                i++;

            //System.out.println ("found: "+txt.substring (start, i));

            int w = fm.stringWidth(txt.substring(start, i));
            if (pos == start) {

                //System.out.println ("firstword!");

                if (w > width) {
                    //System.out.println ("cut!");
                    while (--i > start && fm.stringWidth(txt.substring(start, i)) > width) {
                    }
                    pos = i;
                    break;
                }
            }

            if (w <= width)
                pos = i;

            if (w > width || i >= len || txt.charAt(i) == '\n' || txt.charAt(i) == '\r')
                break;
            i++; // jump over space 
        }

        return pos >= len ? len : ++pos;
    }

}
package javax.microedition.lcdui;

// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS: API complete
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

/** @API MIDP-1.0 */


public class Ticker {

    String str;
//	Vector owners = new Vector();

	/** @API MIDP-1.0 */

    public Ticker(String str) {
        this.str = str;
    }

	/** @API MIDP-1.0 */

    public void setString(String str) {
        this.str = str;
  /*      for(int i = 0; i < owners.size(); i++) {
        	((Screen)owners.elementAt(i)).tickerComponet.setText(str);
        }*/
    }

	/** @API MIDP-1.0 */

    public String getString() {
        return str;
    }

}

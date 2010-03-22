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

package javax.microedition.lcdui;

/**
 * @API MIDP-1.0
 */
public class AlertType {

	/**
	 * @API MIDP-1.0
	 */
	public static final AlertType INFO = new AlertType();

	/**
	 * @API MIDP-1.0
	 */
	public static final AlertType WARNING = new AlertType();

	/**
	 * @API MIDP-1.0
	 */
	public static final AlertType ERROR = new AlertType();

	/**
	 * @API MIDP-1.0
 	 */
	public static final AlertType ALARM = new AlertType();

	/**
	 * @API MIDP-1.0
	 */
	public static final AlertType CONFIRMATION = new AlertType();

	/**
	 * @API MIDP-1.0
	 */
	protected AlertType() {
	}

	/**
	 * @API MIDP-1.0
	 */
	public boolean playSound(Display display) {
		// TODO
		return true;
	}

}

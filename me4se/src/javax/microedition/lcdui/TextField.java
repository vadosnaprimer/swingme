// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//    thiago.leao.moreira@terra.com.br: mask password field, 
//         maxSize of TextField and TextField.getCaretPosition().
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
 * @API MIDP-2.0 
 */
public class TextField extends Item {

	/**
	 * @API MIDP-1.0
	 */
	public static final int ANY = 0;

	/**
	 * @API MIDP-1.0
	 */
	public static final int CONSTRAINT_MASK = 0x0ffff;
	
	/**
	 * @API MIDP-1.0
	 */
	public static final int EMAILADDR = 1;

	/**
	 * @API MIDP-1.0
	 */
	public static final int NUMERIC = 2;
	
	/**
	 * @API MIDP-1.0
	 */
	public static final int PASSWORD = 0x010000;

	/**
	 * @API MIDP-1.0
	 */
	public static final int PHONENUMBER = 3;

	/**
	 * @API MIDP-1.0
	 */
	public static final int URL = 4;

	/**
	 * @API MIDP-2.0
	 */
	public static final int DECIMAL = 5;
	
	/**
	 * @API MIDP-2.0
	 */
	public static final int UNEDITABLE = 0x20000;
	
	/**
	 * @API MIDP-2.0
	 */
	public static final int SENSITIVE = 0x40000;

	/**
	 * @API MIDP-2.0
	 */
	public static final int NON_PREDICTIVE = 0x80000;
	
	/**
	 * @API MIDP-2.0
	 */	
	public static final int INITIAL_CAPS_WORD = 0x100000;
	
	/**
	 * @API MIDP-2.0
	 */		
	public static final int INITIAL_CAPS_SENTENCE = 0x200000;

	ScmTextComponent field;

    /**
     * @API MIDP-1.0
     */
	public TextField(String label, String text, int maxSize, int constraints) {
		super(label);
		if (text == null) {
			text = "";
		}
		if (maxSize <= 0) {
			throw new RuntimeException("The maxSize on the TextField should be more than 0 actual value: " + maxSize);
		}
		if (constraints < 0) {
			constraints= TextField.ANY;
		}
		if (text.length() > maxSize) {
			text= text.substring(0, maxSize);
		}

		field = new ScmTextComponent(this, "textField", true);
		field.setText(text);
		field.setMaxSize(maxSize);
		field.setConstraints(constraints);
		lines.addElement(field);
	}

    /**
     * @API MIDP-1.0
     */
	public void delete(int offset, int length) {
		String s = getString();
		setString(s.substring(0, offset) + s.substring(offset + length));
	}

    /**
     * @API MIDP-1.0
     */
	public int getCaretPosition() {
		return field.getCaretPosition();
	}

    /**
     * @API MIDP-1.0
     */
	public int getChars(char[] data) {
		String s = getString();
		int l = s.length();
		s.getChars(0, l, data, 0);
		return l;
	}

    /**
     * @API MIDP-1.0
     */
	public int getConstraints() {
		return field.getConstraints();
	}

    /**
     * @API MIDP-1.0
     */
	public int getMaxSize() {
		return field.getMaxSize();
	}

    /**
     * @API MIDP-1.0
     */
	public String getString() {
		return field.getText();
	}

    /**
     * @API MIDP-1.0
     */
	public void insert(char[] data, int offset, int length, int position) {
		insert(new String(data, offset, length), position);
	}

    /**
     * @API MIDP-1.0
     */
	public void insert(String src, int position) {
		String s = getString();
		setString(s.substring(0, position) + src + s.substring(position));
	}

    /**
     * @API MIDP-1.0
     */
	public void setChars(char[] data, int offset, int length) {
		setString(new String(data, offset, length));
	}

    /**
     * @API MIDP-1.0
     */
	public void setConstraints(int constraints) {
		field.setConstraints(constraints);
	}

    /**
     * @API MIDP-1.0
     */
	public int setMaxSize(int maxSize) {
		return field.setMaxSize(maxSize);
	}

    /**
     * @API MIDP-1.0
     */
	public void setString(String text) {
		field.setText(text);
	}

    /**
     * @API MIDP-1.0
     */
	public int size() {
		return getString().length();
	}
	
	/**
	 * @API MIDP-2.0
	 * @ME4SE UNSUPPORTED
	 */	
	public void setInitialInputMode(String characterSubset) {
	}
}

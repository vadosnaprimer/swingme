// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors: API Complete
//
// STATUS:
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
public class TextBox extends Screen {

	ScmTextComponent box = new ScmTextComponent(null, "textBox", true);

	/**
	 * @API MIDP-1.0 
	 */
	public TextBox(String title, String text, int maxSize, int constraints) {
		super(title);
		box.setText(text);
		box.setConstraints(constraints);
		container.setMain(box, false);
		setMaxSize(maxSize);
	}

	/**
	 * @API MIDP-1.0 
	 */
	public void setString(String text) {
		box.setText(text);
	}

	/**
	 * @API MIDP-1.0 
	 */
	public int getMaxSize() {
		return box.getMaxSize();
	}

	/**
	 * @API MIDP-1.0 
	 */
	public int setMaxSize(int maxSize) {
		return box.setMaxSize(maxSize);
	}

	/**
	 * @API MIDP-1.0 
	 */
	public String getString() {
		return box.getText();
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
		return 0;
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
		return box.getConstraints();
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
		box.setConstraints(constraints);
	}

	/**
	 * @API MIDP-1.0 
	 */
	public int size() {
		return getString().length();
	}

	/**
	 * @ME4SE INTERNAL 
	 */
	protected void _showNotify() {
		box.requestFocus();
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNSUPPORTED
	 */	
	public void setInitialInputMode(String characterSubset) {
	}
	
	/* 
	 * Commented out: Not needed here, the Functionality is provided by a super class!
	 * If the method is needed here for some reason, please simply 
	 * call super.setTitle(..)
	 * 
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	public void setTitle(String s) {
		System.out.println("TextBox.setTitle() called with no effect!");
	}
	 */		
	
	/* 
	 * Commented out: Not needed here, the Functionality is provided by a super class!
	 * If the method is needed here for some reason, please simply 
	 * call super.setTicker(..)
	 * 
	 * @API MIDP-2.0
	 * @ME4SE UNIMPLEMENTED 
	public void setTicker(Ticker ticker) {
		System.out.println("TextBox.setTicker() called with no effect!");
	}

	 *	 */		
}

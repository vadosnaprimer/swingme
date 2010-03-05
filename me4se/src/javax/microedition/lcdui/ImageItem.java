// ME4SE - A MicroEdition Emulation for J2SE
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS: API Complete
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
public class ImageItem extends Item {

	/**
	 * @API MIDP-1.0
	 */
	public static final int LAYOUT_DEFAULT = 0;

	/**
	 * @API MIDP-1.0
	 */
	public static final int LAYOUT_LEFT = 1;

	/**
	 * @API MIDP-1.0
	 */
	public static final int LAYOUT_RIGHT = 2;

	/**
	 * @API MIDP-1.0
	 */
	public static final int LAYOUT_CENTER = 3;

	/**
	 * @API MIDP-1.0
	 */
	public static final int LAYOUT_NEWLINE_BEFORE = 0x0100;

	/**
	 * @API MIDP-1.0
	 */
	public static final int LAYOUT_NEWLINE_AFTER = 0x0200;

	String alt;
	ScmImage wrapper;
    
	/**
	 * @API MIDP-1.0
	 */
	public ImageItem(String label, Image img, int layout, String alt) {
	    this(label, img, layout, alt, PLAIN);
	}

	/**
	 * @API MIDP-2.0
	 */
	public ImageItem(String label, Image image, int layout, String altText, int appearanceMode) {
        super(label, appearanceMode);
        wrapper = new ScmImage(this, image, layout);
        this.alt = altText;
        lines.addElement(wrapper);	
    }

	public int getAppearanceMode() {
		return appearanceMode;
	}

	/**
	 * @API MIDP-1.0
	 */
	public String getAltText() {
		return alt;
	}

	/**
	 * @API MIDP-1.0
	 */
	public Image getImage() {
		return wrapper.image;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getLayout() {
		return wrapper.layout;
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setAltText(String s) {
		alt = s;
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setImage(Image img) {

		if (img != null) {
			if (img.mutable)
				throw new IllegalArgumentException();
		}
		wrapper.image = img;
		wrapper.invalidate();
		//	wrapper.setSize (img.getWidth (), img.getHeight ());
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setLayout(int l) {
		if ((l & ~0x0303) != 0)
			throw new IllegalArgumentException();

		wrapper.layout = l;
	}
}

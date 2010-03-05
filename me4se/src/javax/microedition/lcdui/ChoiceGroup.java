// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors:
//
// STATUS: API complete, optional image functionality missing
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 * 
 */
public class ChoiceGroup extends Item implements Choice {

	private int type;
	private Vector group;
    private int fitPolicy;

	private ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			notifyChanged();
		}
	};

	/**
	 * @API MIDP-1.0 
	 */
	public ChoiceGroup(String label, int type) {
		super(label);
		this.type = type;
		if (type == EXCLUSIVE || type == POPUP)
			group = new Vector();
	}

	/**
	 * @API MIDP-1.0 
	 */
	public ChoiceGroup(String label, int type, String[] stringItems, Image[] imageItems) {
		this(label, type);
		for (int i = 0; i < stringItems.length; i++)
			append(stringItems[i], imageItems == null ? null : imageItems[i]);
	}

	/**
	 * @ME4SE INTERNAL 
	 */
	private ScmDeviceLabel getLabel(int i) {
		return (ScmDeviceLabel) lines.elementAt(i + 1);
	}

	/**
	 * @API MIDP-1.0 
	 */
	public int append(String stringItem, Image imageItem) {
		insert(size(), stringItem, imageItem);
		return size() - 1;
	}

	/**
	 * @API MIDP-1.0 
	 */
	public void delete(int index) {

		int i = delete();

		if (group != null)
			group.removeElementAt(index);

		lines.removeElementAt(index + 1);

		if (i != -1)
			form.insert(i, this);

		readd(i);
	}

	/**
	 * @API MIDP-2.0
	 */
	public void deleteAll() {
		while(size() > 0) {
		    delete(size()-1);
        }
	}

	/**
	 * @API MIDP-2.0
	 */	
	public int getFitPolicy() {
		return fitPolicy;
	}

	/**
	 * @API MIDP-2.0
	 */
	public Font getFont(int elementNum) {
	    Font font = getLabel(elementNum).midpFont;
        return font == null ? Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM) : font;
	}

	/**
	 * @API MIDP-1.0
	 */
	public Image getImage(int index) {
		return (Image) getLabel(index).object;
	}

	/**
	 * @API MIDP-1.0
	 */	
	public int getSelectedFlags(boolean[] flags) {
		int count = 0;
		for (int i = 0; i < size(); i++) {
			flags[i] = getLabel(i).selected();
			if (flags[i])
				count++;
		}

		for (int i = size(); i < flags.length; i++)
			flags[i] = false;

		return count;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int getSelectedIndex() {
		if (type != MULTIPLE)
			for (int i = 0; i < size(); i++)
				if (getLabel(i).selected())
					return i;

		return -1;
	}

	/**
	 * @API MIDP-1.0
	 */
	public String getString(int index) {
		return getLabel(index).getText();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void insert(int index, String stringItem, Image imageItem) {

		int i = delete();

		ScmDeviceLabel cb = new ScmDeviceLabel("choiceGroup", this, true);
		cb.addActionListener(listener);
		cb.selectButtonRequired = true;
		cb.checkbox = true;
		if (imageItem != null) {
			cb.image = imageItem._image;
			cb.object = imageItem;
		}
		cb.setText(stringItem);
		cb.highlight = true;
		if (group != null) {
			group.insertElementAt(cb, index);
			cb.group = group;
			if (group.size() == 1)
				cb.select(true);
		}
		lines.insertElementAt(cb, index + 1);
		cb.invalidate();

		readd(i);
	}

	/**
	 * @API MIDP-1.0
	 */
	public boolean isSelected(int index) {
		return getLabel(index).selected();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void set(int index, String str, Image img) {
		boolean sel = isSelected(index);
		delete(index);
		insert(index, str, img);
		setSelectedIndex(index, sel);
	}

	/**
	 * @API MIDP-2.0
	 * @ME4SE UNSUPPORTED
	 */
	public void setFitPolicy(int fitPolicy) {
	    this.fitPolicy = fitPolicy;
    }

	/**
	 * @API MIDP-2.0
     * @ME4SE UNSUPPORTED
	 */
	public void setFont(int elementNum, Font font) {
        getLabel(elementNum).midpFont = font;
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setLabel(int index, String s) {
		getLabel(index).setText(s);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setSelectedFlags(boolean[] flags) {

		boolean notify = false;

		for (int i = 0; i < size(); i++) {
			if (flags[i] != getLabel(i).selected) {
				getLabel(i).select(flags[i]);
				notify = true;
			}

			if (flags[i] && group != null) {
				notifyChanged();
				return;
			}
		}

		if (group != null && size() > 0)
			setSelectedIndex(0, true);
		else if (notify)
			notifyChanged();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setSelectedIndex(int index, boolean state) {
		if (getLabel(index).selected == state)
			return;
		getLabel(index).select(state);
		notifyChanged();
	}

	/**
	 * @API MIDP-1.0
	 */
	public int size() {
		return lines.size() - 1;
	}
}
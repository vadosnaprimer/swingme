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
 * @TODO: Dont perferform select event if focus not on the same event
 * @TODO: If the selected element is deleted, the focus is lost completly
 * @TODO: Selection index is lost if the button menu is displayed.
 * 
 */
public class List extends Screen implements Choice {

    /**
     * @API MIDP-1.0
     */
    public static final Command SELECT_COMMAND =
        new Command("Select", Command.OK, 0);

    Command selectCommand = SELECT_COMMAND;
    int type;
    int fitPolicy;
    ScmDeviceList list = new ScmDeviceList(this);
    Vector group;

    ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            if(selectCommand != null)
                handleCommand(selectCommand, null);
        }
    };

    /**
     * @API MIDP-1.0
     */
    public List(String title, int listType) {
        super(title);
        type = listType;
        if (type != MULTIPLE)
            group = new Vector();

        container.setMain(list, true);

        selectButtonRequired = true;
        container.updateButtons();
    }

    /**
     * @API MIDP-1.0
     */
    public List(
        String title,
        int listType,
        String[] stringElements,
        Image[] imageElements) {

        this(title, listType);

        for (int i = 0; i < stringElements.length; i++)
            append(
                stringElements[i],
                imageElements == null ? null : imageElements[i]);
    }

    /**
     * @API MIDP-1.0
     */
    public int append(String s, Image i) {
        insert(size(), s, i);
        return size() - 1;
    }

    /**
     * @API MIDP-1.0
     */
    public void delete(int index) {
        if (index >= size() || index < 0)
            throw new IndexOutOfBoundsException();
        if (group != null)
            group.removeElementAt(index);
        list.remove(index);
        
       _showNotify();
    }

    ScmDeviceLabel getLabel(int index) {
        if (index >= size() || index < 0)
            throw new IndexOutOfBoundsException();
        return (ScmDeviceLabel) list.getComponent(index);
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
        int cnt = size();

        for (int i = 0; i < cnt; i++)
            flags[i] = isSelected(i);

        for (int i = cnt; i < flags.length; i++)
            flags[i] = false;

        return cnt;
    }

    /**
     * @API MIDP-1.0
     */
    public int getSelectedIndex() {
        if (type != MULTIPLE) {
/*
			if (type == IMPLICIT) {
				for (int i = 0; i < size(); i++)
					if (getLabel(i).hasFocus()){
						setSelectedIndex(i, true);
						return i;
					}
			}*/

            for (int i = 0; i < size(); i++)
                if (isSelected(i))
                    return i;
        }
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
        if (index > size() || index < 0)
            throw new IndexOutOfBoundsException();
        ScmDeviceLabel label = new ScmDeviceLabel("item", null, true);
        label.selectButtonRequired = true;
        label.setText(stringItem);
        if (imageItem != null) {
            label.object = imageItem;
            label.image = imageItem._image;
        }
        label.highlight = true;
        label.checkbox = type != IMPLICIT;
        //       label.addKeyListener(eventHandler);

        if (type == IMPLICIT){
			label.selectOnFocus = true;
            label.addActionListener(listener);
		}

        if (group != null) {
            group.addElement(label);
            label.group = group;
        }

        list.add(label, index);
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
     * @API MIDP-1.0
     */
    public void setSelectedFlags(boolean[] flags) {

        for (int i = 0; i < size(); i++) {
            getLabel(i).select(flags[i]);
            if (group != null && flags[i])
                return;
        }
    }

    /**
     * @API MIDP-1.0
     */
    public void setSelectedIndex(int i, boolean state) {
        getLabel(i).select(state);
    }

    /**
     * @API MIDP-1.0
     */
    public int size() {
        return list.getComponentCount();
    }

    /**
     * @ME4SE INTERNAL
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(getClass() + "(lcduiList)[");
        for (int i = 0; i < size(); i++) {
            if (i > 0)
                buf.append(',');
            buf.append(getString(i));
        }
        buf.append(']');
        return buf.toString();
    }

    /**
     * @ME4SE INTERNAL
     */
    void _showNotify() {
		if(type == IMPLICIT){
			int i = getSelectedIndex();
			if(i == -1) list.validateFocus();
			else
			getLabel(i).requestFocus();
		}
		else    	
	        list.validateFocus();
        
    }


    /**
     * Deletes all elements from this List.
     *
     * @API MIDP-2.0
     */
    public void deleteAll() {
		for(int i = size()-1; i >= 0; i--){
			delete(i);
		}
    }


    /**
     * @API MIDP-2.0
     */

    public void setSelectCommand(Command command) {
        selectCommand = command;
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
     */
    public int getFitPolicy() {
        return fitPolicy;
    }

    /**
     * @API MIDP-2.0
     * @ME4SE UNSUPPORTED
    	 */
    public void setFont(int elementNum, Font font) {
        getLabel(elementNum).midpFont = font;
    }

    /**
     * @API MIDP-2.0
     */
    public Font getFont(int elementNum) {
        Font value = getLabel(elementNum).midpFont;
        return value == null ? Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM) : value;
    }
}

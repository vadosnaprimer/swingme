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

import java.util.Vector;

import org.me4se.scm.*;

/**
 * @API MIDP-1.0
 * @API MIDP-2.0
 */
public class Form extends Screen {

	Vector items = new Vector();
	ItemStateListener itemStateListener;

	ScmDeviceList list = new ScmDeviceList(this);

	/*    
	 * this should be moved to the corresponding item... 
	 * 
	 * FocusListener focusListener = new FocusAdapter() {
	        public void focusGained(FocusEvent ev) {
	            boolean sbr =
	                (ev.getSource() instanceof DeviceLabel)
	                    && ((DeviceLabel) ev.getSource()).highlight;
	
	            if (sbr != selectButtonRequired) {
	                selectButtonRequired = sbr;
	                container.updateButtons();
	            }
	        }
	    };*/

	/**
	 * @ME4SE INTERNAL
	 */
	protected void _showNotify() {
		// if (size() == 0)
		//   return;
		list.validateFocus();
	}

	/**
	 * @API MIDP-1.0
	 */
	public Form(String title) {
		super(title);
		
		container.setMain(list, true);
	}

	/**
	 * @API MIDP-1.0
	 */
	public Form(String title, Item[] items) {
		this(title);
		for (int i = 0; i < items.length; i++)
			append(items[i]);
	}

	int getComponentIndex(int index) {
		int ci = 0;
		for (int i = 0; i < index; i++)
			ci += ((Item) items.elementAt(i)).lines.size();
		return ci;
	}

	public void insert(int index, Item item) {
		if (index > size() || index < 0)
			throw new IndexOutOfBoundsException();

		if (item.form != null)
			throw new IllegalStateException();
		item.form = this;

		int cindex = getComponentIndex(index);

		for (int i = 0; i < item.lines.size(); i++) {
			ScmComponent c = (ScmComponent) item.lines.elementAt(i);
			list.add(c, cindex++);
			//      c.setVisible(true);
			//      c.addFocusListener(focusListener);
			//      c.addKeyListener(eventHandler);

			//System.out.println ("form.add: "+c);

			c.invalidate();
		}

		items.insertElementAt(item, index);
       // item.update();  // Leads to recursion.... perhaps we should dump this implementation and build sth based on custom item...
	}

	/**
	 * @API MIDP-1.0
	 */
	public int append(Item item) {
		int i = items.size();
		insert(i, item);
		return i;
	}

	/**
	 * @API MIDP-1.0
	 */
	public int append(String s) {
		return append(new StringItem(null, s));
	}

	/**
	 * @API MIDP-1.0
	 */
	public int append(Image img) {
		return append(new ImageItem(null, img, 0, null));
	}

	/**
	 * @API MIDP-1.0
	 */
	public void delete(int index) {
		if (index >= size() || index < 0)
			throw new IndexOutOfBoundsException();

		int ci = getComponentIndex(index);
		Item item = (Item) items.elementAt(index);
		for (int i = 0; i < item.lines.size(); i++) {
			ScmComponent c = list.getComponent(ci);
			list.remove(ci);
		}
		item.form = null;
		items.removeElementAt(index);
	}

	/** internal method, used when rebuilding items with
	a different set of lines... */

	int delete(Item item) {
		int i = items.indexOf(item);
		if (i != -1)
			delete(i);
		return i;
	}

	/**
	 * @API MIDP-1.0
	 */
	public Item get(int index) {
		if (index >= size() || index < 0)
			throw new IndexOutOfBoundsException();
		return (Item) items.elementAt(index);
	}

	/**
	 * @API MIDP-1.0
	 */
	public void set(int index, Item item) {
		delete(index);
		insert(index, item);
	}

	/**
	 * @API MIDP-1.0
	 */
	public int size() {
		return items.size();
	}

	/**
	 * @API MIDP-1.0
	 */
	public void setItemStateListener(ItemStateListener iListener) {

		itemStateListener = iListener;
	}

	/**
	 * @ME4SE INTERNAL
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer(getClass() + "(lcduiList)[");
		for (int i = 0; i < size(); i++) {
			if (i > 0)
				buf.append(',');
			buf.append(get(i).toString());
		}
		buf.append(']');
		return buf.toString();
	}

	/**
	 * Deletes all the items from this Form, leaving it with zero items. 
	 * This method does nothing if the Form is already empty.
	 * 
	 * @API MIDP-2.0
	 */
	public void deleteAll() {
		for (int i = size() - 1; i >= 0; i--)
			delete(i);
	}

	/**
  * @API MIDP-2.0
	 */
	public int getWidth() {
		return list.getWidth();
	}

	/**
	 * @API MIDP-2.0
	 */
	public int getHeight() {
		return super.getHeight();
	}

	Item getCurrentItem() {
		ScmComponent fo = container.getFocusOwner();
	//	System.out.println("Focus owner is: "+fo);
		return (fo instanceof ScmDeviceComponent) ? ((ScmDeviceComponent) fo).item : null;
	}
    
    /*
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("Form "+title+ " {");
        for(int i = 0; i < size(); i++){
            buf.append(i == 0 ? '{' : ',');
            buf.append(items.elementAt(i).toString());
            
        }
        buf.append('}');
        return buf.toString();
    }*/
}
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

package net.yura.mobile.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JComboBox
 */
public class ComboBox extends Button implements ActionListener{

	private Icon selectedImage;
	private Icon nonSelectedImage;

        // the model, has all the elements
	private List list;
        
        // the scroller for the list
	private ScrollPane scroll;

        // the window for the dropdown
        private Window dropDown;

        /**
         * @see javax.swing.JComboBox#JComboBox() JComboBox.JComboBox
         */
	public ComboBox() {
            setHorizontalAlignment(Graphics.LEFT);
	}
	/**
         * @param vec an vector of objects to insert into the combo box
         * @see javax.swing.JComboBox#JComboBox(java.util.Vector) JComboBox.JComboBox
         */
	public ComboBox(Vector vec){
		this();
		setItems(vec);
	}

        public String getDefaultName() {
            return "ComboBox";
        }

        public void setLoop(boolean b) {
            createList();
            list.setLoop(b);
        }

	public void workoutMinimumSize() {
		if (list!=null && list.getSize()>0) {

                    int count = 0;
                    int hi = 0;
                    int s = list.getSize();
                    for (int i = 0; i < s; i ++){

                        Object obj = list.getElementAt(i);

                        Icon img = (obj instanceof Option)?((Option)obj).getIcon():null;
                        int lenW = getCombinedWidth(String.valueOf(obj),img!=null?img.getIconWidth():0);
                        int lenH = getCombinedHeight(img!=null?img.getIconHeight():0);


                        if (count < lenW){
                            count = lenW;
                        }
                        if (hi < lenH){
                            hi = lenH;
                        }

                    }

                    width = count + padding*2 + ((nonSelectedImage == null)?getFont().getHeight():nonSelectedImage.getIconWidth());
                    height = hi + padding*2;

		}
                else {
                    // in case we are empty then use the normal size
                    height = getFont().getHeight() + (padding*2);
                    width = 10;
                }
	}

	public void fireActionPerformed() {
                createList();
                createScrollPane();

                if (dropDown==null) {
                    dropDown = new Window();
                    dropDown.setCloseOnFocusLost(true);
                    dropDown.setName("Menu");
                    dropDown.addWindowListener(this);

                    Button cancel = new Button( (String)DesktopPane.get("cancelText") );
                    cancel.setActionCommand(Frame.CMD_CLOSE);
                    cancel.addActionListener(this);
                    cancel.setMnemonic( KeyEvent.KEY_SOFTKEY2 );
                    dropDown.addCommand(cancel);
                }

                dropDown.removeAll();
                dropDown.add(scroll);

                scroll.removeAll();
                scroll.add(list);

                dropDown.pack();

                if (dropDown.getWidthWithBorder()<getWidthWithBorder()) {
                    dropDown.setBoundsWithBorder(0, 0, getWidthWithBorder(), dropDown.getHeightWithBorder());
                }

                Menu.positionMenuRelativeTo(
                        dropDown,
                        getXOnScreen() - getInsets().getLeft(), getYOnScreen(), getWidthWithBorder(),getHeight(),
                        getWindow().getDesktopPane(),
                        Graphics.TOP
                        );
                dropDown.setVisible(true);
                
                setSelected(true);
        }
	
	private void createList() {
            if (list==null) {
                List l = new List();
                l.setFixedCellHeight( DefaultListCellRenderer.setPrototypeCellValue("hello", l.getCellRenderer()) );
                setModel(l);
                l.setUseSelectButton(true);
            }
	}
        private void createScrollPane() {
            if (scroll==null) {
                scroll = new ScrollPane();
            }
        }

        /**
         * @param list
         * @see javax.swing.JComboBox#setModel(javax.swing.ComboBoxModel) JComboBox.setModel
         */
        public void setModel(List list) {
            this.list = list;
            list.addActionListener(this);
            list.setActionCommand("listSelect");
        }

        /**
         * @see javax.swing.plaf.basic.ComboPopup#getList() ComboPopup.getList
         */
        public List getList() {
            return list;
        }

	public void paintComponent(Graphics2D g) {

//            if (nonSelectedImage == null){
//
//                int w = width - getFont().getHeight();
//                g.setColor( foreground );
//                g.drawLine(w , 0, w , height);
//
//                int gp = 2; // gap between arrow and sides
//                ScrollPane.drawDownArrow(g, w+1+gp, (height/2)-2, width-w-gp*2-1, height);
//            }
//            else {
//
//                g.drawImage((isFocusOwner())?selectedImage.getImage():nonSelectedImage.getImage(), width-nonSelectedImage.getImage().getWidth(), (height-nonSelectedImage.getImage().getHeight())/2 , 0 );
//            }

            int right = getInsets().getRight();

            if ((selectedImage != null) && isSelected()) {
                selectedImage.paintIcon(this, g, width+right-selectedImage.getIconWidth(), (height-selectedImage.getIconHeight())/2);
            }
            else if (nonSelectedImage != null) {
                nonSelectedImage.paintIcon(this, g, width+right-nonSelectedImage.getIconWidth(), (height-nonSelectedImage.getIconHeight())/2);
            }

            super.paintComponent(g);
	}

        public void updateUI() {
                super.updateUI();
                
                nonSelectedImage = (Icon)theme.getProperty("icon", Style.ALL);
                selectedImage = (Icon)theme.getProperty("icon", Style.SELECTED);

                if (dropDown!=null) {
                    dropDown.updateUI();
                }
                if (scroll!=null) {
                    scroll.updateUI();
                }
                if (list!=null) {
                    list.updateUI();
                }
        }

	public Icon getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(Icon selectedImage) {
		this.selectedImage = selectedImage;
	}

	public Icon getNonSelectedImage() {
		return nonSelectedImage;
	}

	public void setNonSelectedImage(Icon nonSelectedImage) {
		this.nonSelectedImage = nonSelectedImage;
	}

        /**
         * return the vector, can not be a true representation of the model
         */
	public Vector getItems() {
            createList();
            return list.getItems();
	}

        public int getItemCount() {
            createList();
            return list.getSize();
        }

	public void setItems(Vector items) {
		createList();
		list.setListData(items);
		
		if (list.getSize()>0) {
			setSelectedIndex(0);
		}
		else {
			super.setText("");
                        super.setIcon(null);
		}

	}

	public void actionPerformed(String actionCommand) {
            dropDown.setVisible(false);
            if ("listSelect".equals(actionCommand)) {
                int index = list.getSelectedIndex();
                if (index >= 0) {
                    setSelectedIndex( index );
                }
		super.fireActionPerformed();
            }
            else if (Frame.CMD_CLOSE.equals(actionCommand)) {
                setSelected(false);
            }
            //#mdebug
            else {
                throw new RuntimeException();
            }
            //#enddebug
	}

        /**
         * @return the current selected item
         * @see javax.swing.JComboBox#getSelectedItem() JComboBox.getSelectedItem
         */
	public Object getSelectedItem() {
		createList();
		return list.getSelectedValue();
	}

        /**
         * @param selected
         * @see javax.swing.JComboBox#setSelectedItem(java.lang.Object) JComboBox.setSelectedItem
         */
	public void setSelectedItem(Object selected) {
            createList();
            setSelectedIndex( list.indexOf(selected) );
	}

        /**
         * @param i
         * @see javax.swing.JComboBox#setSelectedIndex(int) JComboBox.setSelectedIndex
         */
	public void setSelectedIndex(int i) {
                createList();
		list.setSelectedIndex( i );

                // attempt to use the renderer to get the info to render
                // TODO: maybe actually use the renderer to draw
                ListCellRenderer r = list.getCellRenderer();
                Component c = r.getListCellRendererComponent(list, list.getElementAt(i), i, true, false);
                if (c instanceof Label) {
                    Label l = (Label)c;
                    super.setText( l.getText() );
                    super.setIcon( l.getIcon() );
                }
                else {
                    super.setValue( list.getElementAt(i) );
                }
                String t = getText();
                if (t==null || "".equals(t)) {
                    super.setText(" ");
                }
	}
	
	/**
         * @return an integer specifying the currently selected list item
         * @see javax.swing.JComboBox#getSelectedIndex() JComboBox#getSelectedIndex
         */
	public int getSelectedIndex() {
            createList();
            return list.getSelectedIndex();
	}
	
	/**
	 * This throws an IllegalArgumentException and you should use the 
	 * setIndex method instead
	 */
	public void setText(String a) {
            throw new IllegalArgumentException();
	}
	
	public void setScrollMode(int m) {
            createScrollPane();
            scroll.setMode(m);
	}

        public void setValue(Object obj) {
            setSelectedItem(obj);
        }
        public Object getValue() {
            return getSelectedItem();
        }

}
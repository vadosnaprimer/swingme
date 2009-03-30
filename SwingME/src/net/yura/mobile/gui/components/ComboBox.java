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
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JComboBox
 */
public class ComboBox extends Button implements ActionListener{

	private Image selectedImage;
	private Image nonSelectedImage;

        private Window dropDown;
	private List list;
	private ScrollPane scroll;
	
	//private CommandButton[] pbuttons;
	
        /**
         * @see javax.swing.JComboBox#JComboBox() JComboBox.JComboBox
         */
	public ComboBox() {

	}
	/**
         * @param vec an vector of objects to insert into the combo box
         * @see javax.swing.JComboBox#JComboBox(java.util.Vector) JComboBox.JComboBox
         */
	public ComboBox(Vector vec){
		this();
		
		setItems(vec);
	}

        public void setLoop(boolean b) {
            createList();
            list.setLoop(b);
        }


	public void workoutSize() {
		if (list!=null) {

			if (list.getSize()>0){
                                int count = 0;
                                int hi = 0;
                                int s = list.getSize();
				for (int i = 0; i < s; i ++){
					
                                    Object obj = list.getElementAt(i);

                                    Image img = (obj instanceof Option)?((Option)obj).getIcon():null;
                                    int lenW = getCombinedWidth(String.valueOf(obj),img!=null?img.getWidth():0);
                                    int lenH = getCombinedHeight(img!=null?img.getHeight():0);

                                    
				    if (count < lenW){
                                        count = lenW;
				    }
                                    if (hi < lenH){
                                        hi = lenH;
				    }
					
				}

				width = count + padding*2 + ((nonSelectedImage == null)?getFont().getHeight():nonSelectedImage.getWidth());
				height = hi + padding*2;
			}
			

		}
                else {
                    // in case we are empty then use the normal size
                    super.workoutSize();
                }
	}

	public void fireActionPerformed() {

                createList();
                list.workoutSize();

                int y;

                int h = list.getHeightWithBorder();
                if(h > DesktopPane.getDesktopPane().getHeight()/2){
                        h = DesktopPane.getDesktopPane().getHeight()/2;
                }

                if ((getYOnScreen() + height + h) > (DesktopPane.getDesktopPane().getHeight() - DesktopPane.getDesktopPane().getSoftkeyHeight()) ){
                        y = getYOnScreen() - h;
                }
                else {
                        y = getYOnScreen() + height;
                }

                scroll.setPreferredSize(width, h);
                

                
                if (dropDown==null) {
                    dropDown = new Window();
                    dropDown.setName("Menu");
                    dropDown.add(scroll);
                }
                dropDown.pack();
                dropDown.setLocation(getXOnScreen(), y);
                dropDown.setVisible(true);
                
                setSelected(true);

                // TODO owner.setGlassPane(scroll);

//                pbuttons = new CommandButton[2];
//                pbuttons[0]=owner.getWindowCommands()[0];
//                pbuttons[1]=owner.getWindowCommands()[1];
//                owner.setWindowCommand(0, null);
//                owner.setWindowCommand(1, null);
//
//                owner.repaint();
        
        }
	
	private void createList() {
		if (list==null) {
                    setModel(new List());
		}

                if (scroll==null) {
                    scroll = new ScrollPane(list);
                }

                // TODO use a window, and set the windows name to "Menu"
                // do this when doing tooltips
	}

        /**
         * @param list
         * @see javax.swing.JComboBox#setModel(javax.swing.ComboBoxModel) JComboBox.setModel
         */
        public void setModel(List list) {
            this.list = list;
            list.addActionListener(this);
        }

	public void paintComponent(Graphics g) {

            if (nonSelectedImage == null){

                int w = width - getFont().getHeight();
                g.setColor( foreground );
                g.drawLine(w , 0, w , height);

                int gp = 2; // gap between arrow and sides
                ScrollPane.drawDownArrow(g, w+1+gp, (height/2)-2, width-w-gp*2-1, height);
            }
            else {

                g.drawImage((isFocusOwner())?selectedImage:nonSelectedImage, width-nonSelectedImage.getWidth(), (height-nonSelectedImage.getHeight())/2 , 0 );
            }

            super.paintComponent(g);
	}
	/*
        protected int getBorderColor() {
            if (border instanceof LineBorder) {
                return ((LineBorder)border).getLineColor();
            }
            return 0;
        }
        */
	public Image getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(Image selectedImage) {
		this.selectedImage = selectedImage;
	}

	public Image getNonSelectedImage() {
		return nonSelectedImage;
	}

	public void setNonSelectedImage(Image nonSelectedImage) {
		this.nonSelectedImage = nonSelectedImage;
	}

        /**
         * return the vector, can not be a true representation of the model
         */
	public Vector getItems() {
		return list.getItems();
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

                setSelectedIndex( list.getSelectedIndex() );

                //setValue( list.getSelectedValue() );

                dropDown.setVisible(false);
		
		super.fireActionPerformed();
		
		repaint();
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
	}
	
	/**
         * @return an integer specifying the currently selected list item
         * @see javax.swing.JComboBox#getSelectedIndex() JComboBox#getSelectedIndex
         */
	public int getSelectedIndex() {
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
		createList();
		scroll.setMode(m);
	}
}
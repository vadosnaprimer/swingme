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
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JComboBox
 */
public class ComboBox extends Button implements ActionListener{

	private Image selectedImage;
	private Image nonSelectedImage;
	
//	private Vector items;
	private List list;
	private ScrollPane scroll;
	
	private CommandButton[] pbuttons;
	
	public ComboBox() {

	}
	
	public ComboBox(Vector vec){
		this();
		
		setItems(vec);
	}


	public void workoutSize() {
		if (list!=null) {
                    
			Vector items = list.getItems();

			if (items != null){
                                int count = 0;
                                int hi = 0;
                                        
				for (int i = 0; i < items.size(); i ++){
					
                                    Object obj = items.elementAt(i);

                                    int lenW = getCombinedWidth(String.valueOf(obj),(obj instanceof Option)?((Option)obj).getIcon():null);
                                    int lenH = getCombinedHeight((obj instanceof Option)?((Option)obj).getIcon():null);

                                    
				    if (count < lenW){
                                        count = lenW;
				    }
                                    if (hi < lenH){
                                        hi = lenH;
				    }
					
				}

				width = count + padding*2 + ((nonSelectedImage == null)?hi:nonSelectedImage.getWidth());
				height = hi + padding*2;
			}
			
			int h = height * getItems().size();
			if(h > height * 4){
				h = height *4;
			}
			scroll.setSize(width, h);
			scroll.revalidate();
		}
	}

	public void fireActionPerformed() {
        
                createList();

                int y;

                if ((getYInWindow() + height + scroll.getHeight()) > owner.getHeight()){
                        y = getYInWindow() - scroll.getHeight();
                } else{
                        y = getYInWindow() + height;
                }

                scroll.setLocation(getXInWindow(), y);		
                owner.setGlassPaneComponent(scroll);

                pbuttons = new CommandButton[2];
                pbuttons[0]=owner.getWindowCommands()[0];
                pbuttons[1]=owner.getWindowCommands()[1];
                owner.setWindowCommand(0, null);
                owner.setWindowCommand(1, null);

                owner.repaint();
        
        }
	
	private void createList() {
		if (list==null) {
			list = new List( new DefaultListCellRenderer());
			list.setBackground(background);
			list.addActionListener(this);
			scroll = new ScrollPane(list);
			// TODO setbackground?? scroll.setTransparent(false);
			scroll.setBorder(activeBorder);
			
		}
		
	}

	public void paintComponent(Graphics g){

            if (nonSelectedImage == null){

                int w = width - height + padding*2;
                g.setColor( getBorderColor() );
                g.drawLine(w , 0, w , height);

                int gp = 2; // gap between arrow and sides
                ScrollPane.drawDownArrow(g, w+1+gp, (height/2)-2, width-w-gp*2-1, height);
            }
            else {

                g.drawImage((isFocused())?selectedImage:nonSelectedImage, width-nonSelectedImage.getWidth(), (height-nonSelectedImage.getHeight())/2 , 0 );
            }

            super.paintComponent(g);
	}
	
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

	public Vector getItems() {
		return list.getItems();
	}

	public void setItems(Vector items) {
		createList();
		list.setListData(items);
		
		if (items.size()>0) {
			setSelectedIndex(0);
		}
		else {
			super.setText("");
		}

	}

	public void actionPerformed(String actionCommand) {
            
                setValue( list.getFocusedItem() );
                
		owner.setGlassPaneComponent(null);
		owner.setWindowCommand(0, pbuttons[0]);
		owner.setWindowCommand(1, pbuttons[1]);
		pbuttons=null;
		
		super.fireActionPerformed();
		
		repaint();
	}

	public Object getSelected() {
		createList();
		return list.getFocusedItem();
	}

	public void setSelected(Object selected) {
		createList();
		list.setFocusedItem(selected);
		super.setText(selected.toString());
		
	}

	public void setSelectedIndex(int i) {
		setSelected( list.getItems().elementAt(i) );
	}
	
	
	public int getFocusedItemIndex(){
		return list.getFocusedItemIndex();
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
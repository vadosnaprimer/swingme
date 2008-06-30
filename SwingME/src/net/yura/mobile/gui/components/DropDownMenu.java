package net.yura.mobile.gui.components;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.util.Option;

public class DropDownMenu extends Button implements ActionListener{
	
	private int textWidth;
	
	private Image selectedImage;
	private Image nonSelectedImage;
	
//	private Vector items;
	private List list;
	private ScrollPane scroll;
	
	private CommandButton[] pbuttons;
	
	public DropDownMenu() {
		super((String)null);
	}
	
	public DropDownMenu(Vector vec){
		this();
		
		setItems(vec);
	}


	public void doLayout() {
		if (list!=null) {
                    
			Vector items = list.getItems();

			if (items != null){
                                int count = 0;
                                
				for (int i = 0; i < items.size(); i ++){
					
                                    Object obj = items.elementAt(i);

                                    int len = getCombinedWidth(String.valueOf(obj),(obj instanceof Option)?((Option)obj).getIcon():null);

				    if (count < len){
                                        count = len;
				    }
					
				}
				textWidth = count + 2;
				width = textWidth + (getFont().getWidth('E')) * 2;
				height = getFont().getHeight() + 4;
			}
			
			int h = height * getItems().size();
			if(h > height * 4){
				h = height *4;
			}
			scroll.setSize(width, h);
			scroll.doLayout();
		}
	}
	
	public boolean keyEvent(KeyEvent keypad){

		boolean response = super.keyEvent(keypad);

		if (response) {
			createList();
			
			int y;
			
			if ((getYInWindow() + height + scroll.getHeight()) > owner.getHeight()){
				y = getYInWindow() - scroll.getHeight();
			} else{
				y = getYInWindow() + height;
			}
			
			scroll.setPosition(getXInWindow(), y);		
			owner.setGlassPaneComponent(scroll);
			
			pbuttons = new CommandButton[2];
			pbuttons[0]=owner.getPanelCommands()[0];
			pbuttons[1]=owner.getPanelCommands()[1];
			owner.setWindowCommand(0, null);
			owner.setWindowCommand(1, null);
			
			owner.repaint();
		}
		return response;
	}
	
	public void fireActionPerformed() { }
	
	private void createList() {
		if (list==null) {
			list = new List( new DefaultListCellRenderer());
			list.setBackground(background);
			list.addActionListener(this);
			scroll = new ScrollPane(list);
			scroll.setTransparent(false);
			scroll.setBorder(border);
			
		}
		
	}


	/**
	 * Draws the button at given y position with set alignment
	 * @param Graphics - the graphics object
	 * @param int - Y position
	 * @return int - height of the item
	 */
	public void paintComponent(Graphics g){
		int boxX = getFont().getWidth('E') + 1;
		int x = textWidth;
		int y = 0;

			if (getNonSelectedImage() == null){
				if (isFocused()){
					g.setColor(activeBorderColor);
				}
				else{
					g.setColor(borderColor);
				}
				g.drawLine(x + boxX/2, y-1, x + boxX/2 , height);
				ScrollPane.drawDownArrow(g, x+boxX-2, y+(height/2)-2, boxX-1, height);
			}
			else{
				g.setColor(borderColor);
				g.drawImage(nonSelectedImage, (height-nonSelectedImage.getHeight())/2, (height-nonSelectedImage.getHeight())/2 , 0 );
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
		
		//workoutSize();
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
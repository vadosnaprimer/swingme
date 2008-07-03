package net.yura.mobile.gui.layout;

import java.util.Hashtable;
import java.util.Vector;

import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Panel;

/**
 * @author Yura Mamyrin
 * @see java.awt.FlowLayout
 */
public class FormFlowLayout implements Layout {

	// Item spacing
	private int	itemSpacing;
	
	public FormFlowLayout() {
		
		this( RootPane.getDefaultSpace() );
		
	}
	
	public FormFlowLayout(int a) {
		
		itemSpacing = a;
	}
	
	public void layoutPanel(Panel panel, Hashtable cons) {

		int fullwidth = panel.getWidth();
		//int compwidth = (fullwidth*4)/5;
		//int offset = (fullwidth - compwidth)/2;
		//int height = panel.getHeight();
		Vector components = panel.getComponents();
		
		for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);

			if ( comp.getWidthWithBorder() > fullwidth) {
			// TODO !(comp instanceof MStringItem) &&
				
				fullwidth = comp.getWidthWithBorder();
			}
		}
		
		int down=itemSpacing;
		
		for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);
			
			int height = comp.getHeightWithBorder();
			int width = comp.getWidthWithBorder();
				
			int offset = (fullwidth - width)/2;

			comp.setBoundsWithBorder(offset,down, width, height );
			
			if (height!=0) {
				down = down + height + itemSpacing;
			}
			
		}
		
		panel.setSize( fullwidth, down);
		
	}

	public int getItemSpacing() {
		return itemSpacing;
	}

	
	
}

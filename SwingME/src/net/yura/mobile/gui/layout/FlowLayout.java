package net.yura.mobile.gui.layout;

import java.util.Hashtable;
import java.util.Vector;

import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Panel;


/**
 * @author Yura Mamyrin
 */

public class FlowLayout implements Layout {

	private int padding;
	
	public FlowLayout() {
		
		this( RootPane.getDefaultSpace() );
	}
	public FlowLayout(int p) {
		
		padding = p;
	}
	
	public void doLayout(Panel panel, Hashtable cons) {

		int width = panel.getWidth();
		int height = panel.getHeight();
                
		Vector components = panel.getComponents();
		
		int fullwidth=(components.size()>0)?((components.size()+1)*padding):0;
		int fullheight=0;
		
		for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);
			
			fullwidth = fullwidth + comp.getWidthWithBorder();
			
			if (fullheight < comp.getHeightWithBorder()+padding*2) {
				
				fullheight = comp.getHeightWithBorder()+padding*2;
			}
			
		}
		
		if (fullwidth > width) { width = fullwidth; }
		if (fullheight > height) { height = fullheight; }
		
		int offset = (width - fullwidth)/2 + padding;

		for (int c=0;c<components.size();c++) {
			
			Component comp = (Component)components.elementAt(c);
			
			comp.setBoundsWithBorder(offset , (height-comp.getHeightWithBorder())/2, comp.getWidthWithBorder(), comp.getHeightWithBorder() );
			offset = offset + comp.getWidthWithBorder() + padding;
		}
		panel.setSize(width,height);
	}

}

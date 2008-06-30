package net.yura.mobile.gui.layout;

import java.util.Hashtable;
import java.util.Vector;

import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Panel;

public class GridLayout implements Layout {

	private int across;
	private int padding;
	private int down;
        
	public GridLayout(int acc,int down) {
		
		this(acc, down,RootPane.getDefaultSpace() );
	}
	
	public GridLayout(int acc,int down,int p) {
		
		across = acc;
		padding = p;
		this.down = down;
	}
	
	public void doLayout(Panel panel, Hashtable cons) {

		Vector components = panel.getComponents();
		
                int ac;
                if (across!=0) {
                    ac=across;
                }
                else {
                    ac = (components.size()+(down-1)) / down;
                }
                
		int cwidth = (panel.getWidth() -(ac*padding) -padding) /ac;
		int cheight = 0;
		
		for (int i=0;i<components.size();i++) {
			
			Component comp = (Component)components.elementAt(i);
			
			if (comp.getHeightWithBorder() > cheight) {
				
				cheight = comp.getHeightWithBorder(); 
				
			}
			if (comp.getWidthWithBorder() > cwidth) {
				
				cwidth = comp.getWidthWithBorder();
			}
		}

		int a=0;
		int d=0;
                int maxa=0;
		for (int i=0;i<components.size();i++) {
			
			Component comp = (Component)components.elementAt(i);
			comp.setBoundsWithBorder((a*cwidth)+padding+(padding*a), (d*cheight)+padding+(padding*d), cwidth, cheight);
			a++;
                        
                        if (a>maxa) { maxa=a;}
                        // when it gets to the end of the row it adds 1
			if (a==ac && i!=(components.size()-1)) {
				a=0;
				d++;
			}
		}
                d++;
		panel.setSize( (maxa*cwidth) +(maxa*padding) +padding, (d*cheight) +(d*padding) +padding );
	}

}

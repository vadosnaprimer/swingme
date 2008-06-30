package net.yura.mobile.gui.cellrenderer;

import javax.microedition.lcdui.Graphics;

import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;


public class DefaultListCellRenderer extends Label implements ListCellRenderer {

	private int color;
	private Border normal;
	private Border selected;
	
        public DefaultListCellRenderer() {
            
            this(
            RootPane.getDefaultStyle().font,
            RootPane.getDefaultStyle().itemBorderColor,
            RootPane.getDefaultStyle().itemActiveBorderColor);
        }
        
	public DefaultListCellRenderer(Font s,int color,int c2) {
		
		super("",s);
		
		normal = new EmptyBorder(1,1,1,1);
		selected = new LineBorder(c2,1,Graphics.DOTTED);
		setBorder(normal);
		this.color=color;
	}
	
	public Component getListCellRendererComponent(List list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

                setValue(value);

		if (cellHasFocus) {
			setBorder(selected);
		}
		else {
			setBorder(normal);
		}
                
                if (isSelected) {
                    	setBackground(color);
                }
                else {
                        setBackground(-1);
                }
		
                doLayout();
                
		return this;
	}
        // max width!
        public int getMaxTextWidth() {
            return 10000;
        }
	
}

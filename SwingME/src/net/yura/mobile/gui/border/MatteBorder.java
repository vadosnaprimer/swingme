package net.yura.mobile.gui.border;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.components.Component;

/**
 * @author ymamyrin
 */
public class MatteBorder extends EmptyBorder {

    private int color;
    
    	public MatteBorder(int top, int left, int bottom, int right,int color) {
            super(top,left,bottom,right);
            
            this.color = color;
        }
        
        public void paintBorder(Component c, Graphics g, int width,int height) {
            
                g.setColor(color);
            
            	g.fillRect(-left, -top, width+left+right, top); // top
                
		g.fillRect(-left, height, width+left+right, bottom); // bottom
			
		g.fillRect(-left, 0, left, height ); // left
                
		g.fillRect(width, 0, right, height ); // right
            
        }

    
}



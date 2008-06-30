package net.yura.mobile.gui.border;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.util.Properties;

/**
 * @author ymamyrin
 */
public class ImageBorder implements Border {

    private Image activeimage;
    private int top;
    private int bottom;
    private int right;
    private int left;
    private boolean back;
    private int color;
    
    	public ImageBorder(String name) {

		try {
			
			Properties newborder = new Properties();
			
			newborder.load( getClass().getResourceAsStream(name) );
                        
                        
                        String imageName = newborder.getProperty("active");
				
			if (name.charAt(0)=='/' && imageName.charAt(0)!='/') {
				imageName = "/"+imageName;
			}
				
			activeimage = Image.createImage(imageName);
                        top=Integer.parseInt(newborder.getProperty("top"));
                        bottom=Integer.parseInt(newborder.getProperty("bottom"));
                        right=Integer.parseInt(newborder.getProperty("right"));
                        left=Integer.parseInt(newborder.getProperty("left"));
                        
                        String b =newborder.getProperty("back");
                        
                        back = (b!=null && b.equals("Y"));
                        
                        String c =newborder.getProperty("color");
                        
                        color = (c==null)?-1:Integer.parseInt(c, 16);
                        
		}
		catch (IOException ex) {
			
			ex.printStackTrace();
			throw new RuntimeException("unable to load border: "+name);
		}
		
		
	}
    
    
    
    
    public void paintBorder(Component c, Graphics g, int width, int height) {

        int w=activeimage.getWidth();
        int h=activeimage.getHeight();
        
        g.drawRegion(activeimage, 0,  0, left, top, Sprite.TRANS_NONE, -left, -top,Graphics.TOP|Graphics.LEFT);
        g.drawRegion(activeimage, w-right,  0, right, top, Sprite.TRANS_NONE, width, -top,Graphics.TOP|Graphics.LEFT);
        g.drawRegion(activeimage, 0,  h-bottom, left, bottom, Sprite.TRANS_NONE, -left, height,Graphics.TOP|Graphics.LEFT);
        g.drawRegion(activeimage, w-right,  h-bottom, right, bottom, Sprite.TRANS_NONE, width, height,Graphics.TOP|Graphics.LEFT);
        
        if (back) {
            fillArea(g,activeimage,right,top,w-right-left,h-top-bottom,0,0,width,height); // centre
        }
        else if (color!=-1) {
            g.setColor(color);
            g.fillRect(0,0,width,height);
        }
        
        fillArea(g,activeimage,right,0,w-right-left,top,0,-top,width,top); // top line
        
        fillArea(g,activeimage,right,h-bottom,w-right-left,bottom,0,height,width,bottom); // bottom line
        
        fillArea(g,activeimage,0,top,left,h-top-bottom,-left,0,left,height); // left
        
        fillArea(g,activeimage,w-right,top,right,h-top-bottom,width,0,right,height); // right
    }
    
    private static void fillArea(Graphics g,Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h) {
        
        if (src_w==0 || src_h==0) return;
        
        final int cx = g.getClipX();
        final int cy = g.getClipY();
        final int cw = g.getClipWidth();
        final int ch = g.getClipHeight();
        
        g.setClip(dest_x,dest_y,dest_w,dest_h);
        
        for (int pos_x=dest_x;pos_x<(dest_x+dest_w);pos_x=pos_x+src_w) {
            for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+src_h) {
                g.drawRegion(img, src_x,  src_y, src_w, src_h, Sprite.TRANS_NONE, pos_x, pos_y,Graphics.TOP|Graphics.LEFT);
            }
        }
        
        g.setClip(cx,cy,cw,ch);
        
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getRight() {
        return right;
    }

    public int getLeft() {
        return left;
    }

}

package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @API MIDP-2.0 
 */
public abstract class Layer {

	int x;
	int y;
	int w;
	int h;
	boolean visible =true;
	Image image;
    int frameWidth;
    int frameHeight;
    
	/**
	 * @API MIDP-2.0 
	 */
	public Layer() {
	}

	/**
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED	 
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void move(int dx, int dy) {
		setPosition(x + dx, y + dy);
	}

	/**
	 * @API MIDP-2.0 
	 */
	public final int getX() {
		return x;
	}

	/**
	 * @API MIDP-2.0 
	 */
	public final int getY() {
		return y;
	}

	public final int getWidth() {
		return w;
	}

	/**
	 * @API MIDP-2.0 
	 */
	public final int getHeight() {
		return h;
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void setVisible(boolean flag) {
		visible = flag;
	}

	/**
	 * @API MIDP-2.0 
	 */
	public final boolean isVisible() {
		return visible;
	}
    
    /**
     * 
     * @ME4SE INTERNAL
     */

    void drawImage(Graphics g, int dx, int dy, int index, int transf){
        if(!visible) return;
        
        int cnt = image.getWidth() / frameWidth;
        
        int row = index / cnt;
        int col = index % cnt;
        
        int srcX = col * frameWidth;
        int srcY = row * frameHeight;
        
        	g.drawRegion(image, srcX, srcY, frameWidth, frameHeight, transf, x+dx, y+dy, 0);
    }
    
    int getFrameCount(){
        return (image.getWidth() / frameWidth) * (image.getHeight() / frameHeight);
    }
    
    
	/**
	 * @API MIDP-2.0 
	 */
	public abstract void paint(Graphics g);
}

package javax.microedition.lcdui.game;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

/**
 * @API MIDP-2.0 
 */
public class LayerManager {

    Vector layers = new Vector();
    int viewX;
    int viewY;
    int viewH = Integer.MAX_VALUE;
    int viewW = Integer.MAX_VALUE;



    
	/**
	 * @API MIDP-2.0 
	 */
	public LayerManager() {
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void append(Layer l) {
	    layers.addElement(l);
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void insert(Layer l, int index) {
	    layers.insertElementAt(l, index);
	}

	/**
	 * @API MIDP-2.0 
	 */
	public Layer getLayerAt(int index) {

		return (Layer) layers.elementAt(index);
	}

	/**
	 * @API MIDP-2.0 
	 */
	public int getSize() {

		return layers.size();
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void remove(Layer l) {
	    layers.removeElement(l);
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void paint(Graphics g, int x, int y) {
        int cx = g.getClipX();
        int cy = g.getClipY();
        int cw = g.getClipWidth();
        int ch = g.getClipHeight();
        
        int tx = x - viewX;
        int ty = y - viewY;
        
        g.translate(tx, ty);
        g.clipRect(viewX, viewY, viewW, viewH);
        
        for(int i = layers.size()-1; i >= 0; i--){
            getLayerAt(i).paint(g);
        }
        
        g.translate(-tx, -ty);
        g.setClip(cx, cy, cw, ch);
	}

	/**
	 * @API MIDP-2.0 
	 */
	public void setViewWindow(int x, int y, int width, int height) {
	    viewX = x;
        viewY = y;
        viewH = height;
        viewW = width;
	}
}

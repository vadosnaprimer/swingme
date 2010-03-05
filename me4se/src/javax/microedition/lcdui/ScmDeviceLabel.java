package javax.microedition.lcdui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.lcdui.FontInfo;
import org.me4se.impl.lcdui.PhysicalFont;
import org.me4se.impl.lcdui.TextBlock;

import java.util.Vector;
//import org.kobjects.scm.*;

/**
 * A kind of label that carries additiona formatting information
 * @ME4SE INTERNAL
 *  */

public class ScmDeviceLabel extends ScmDeviceComponent {

    //    String type;
    protected String label;
    
    javax.microedition.lcdui.Font midpFont;
    
    /* overrides the fontinfo alignment if != 0 
    protected int align; */

    Vector actionListeners;
    public boolean selected;
	public boolean selectOnFocus;

    public String actionCommand;
    public boolean compact;
    public boolean highlight;
    public boolean checkbox;
    public Vector group;
    public BufferedImage image;
    public Object object;

    TextBlock textBlock;
    
	public ScmDeviceLabel(String type, Item item, boolean focusable){
        //this.type = type;
        super(item, type, focusable);
    }
    



    public void action() {
        select(group != null | !selected);
        
        //System.out.println ("Action!!");
        
        if (actionListeners != null)
            for (int i = 0; i < actionListeners.size(); i++) {
                (
                    (ActionListener) actionListeners.elementAt(
                        i)).actionPerformed(
                    new ActionEvent(
                        ScmDeviceLabel.this,
                        ActionEvent.ACTION_PERFORMED,
                        actionCommand == null ? label : actionCommand));
            }
        //repaint();
    }

 
    public void addActionListener(ActionListener l) {
        if (actionListeners == null) {
            actionListeners = new Vector();

        }
        actionListeners.addElement(l);
    }

    
//	public boolean keyTyped (char c) {

	public boolean keyReleased(String s) {
				
     if (s.equals("SELECT")) {
         action ();
         return true;
     }
     else 
     	return super.keyReleased(s);
	}
	


   
	public void focusGained(){
		super.focusGained();
		if(selectOnFocus){
			select(true);	    
		}
	}
    

	/** 
	 * Important: if the focus is just gained, do not interpret the mouse click as
	 * "call for action": Otherwise, it is not possible to chose other commands!
	 */

    public boolean mouseClicked (int button, int x, int y, int modifiers, int clicks) {
      //  if (!super.mouseClicked (button, x, y, modifiers, clicks))
      if (!(super.mouseClicked (button, x, y, modifiers, clicks) && selectOnFocus))      
      	action ();

      return true;
    }


    public void paint(java.awt.Graphics g) {

        super.paint (g);

        int x = getFocusable() ? 3 : 0;
        int w = getWidth();
        int h = getHeight();        	

        FontInfo fi = fontInfo[hasFocus() ? 1 : 0];

        g.setColor(fi.foreground);
        g.setColor (fi.foreground);

        if(textBlock != null){
            textBlock.paint(g, x, 0);
            return;
        }

        PhysicalFont pf = fi.font;
        ApplicationManager manager = ApplicationManager.getInstance();

        
        int y = h / 2 - 1;

        if (checkbox) {
            if (group == null) {
                g.setColor(
                    new java.awt.Color(
                        manager.getDeviceColor(
                            0x0808080)));

                g.drawRect(x, y - 4, 8, 8);
                g.setColor(Color.black);
                if (selected) {
                    g.drawLine(x, y, x + 4, y + 4);
                    g.drawLine(x + 1, y, x + 4, y + 4);
                    g.drawLine(x + 4, y + 4, x + 9, y - 5);
                    g.drawLine(x + 4, y + 4, x + 10, y - 5);
                }
            }
            else {
                g.drawOval(x, y - 4, 7, 7);

                if (selected) {
                    g.drawOval(x + 2, y - 2, 3, 3);
                    g.fillOval(x + 2, y - 2, 3, 3);
                }
            }
            x += 11;
        }

        if (label == null)
            return;

        if (x == 0) {
            switch (fi.align) {
                case FontInfo.CENTER :
                    x = (w - pf.stringWidth(label)) / 2;
                    break;

                case FontInfo.BORDER :
                    if (getX() == 0)
                    	break;

                case FontInfo.RIGHT :
                    x = (w - pf.stringWidth(label));
                    break;
            }

            if (x < 0) {
                x = 0;
            }
        }

		// IMPROVE Currently every image is scaled down in oder to fit...
        // also, images are only displayed for left aligned items
        // because in other cases position calculation would be wrong
        
		if (image != null && fi.align == FontInfo.LEFT) {
		    int imgH = image.getHeight();
		    int imgW = image.getWidth();
		    /*            if (imgH > h 
            || manager.getImageWidth(image, "list item image") > h)
    			g.drawImage(image, x, y-h/2, h, h, manager.awtContainer); 
            else 
                        manager.drawImage(g, image, x+(h-imgH)/2, y-imgH/2, "list item image"); 
            x += h+3;
            */

            g.drawImage(image, x, y-imgH/2, null); 
            x += imgW+3;
        }

        y = (h - pf.height) / 2 ;
        
		if(fi.background != null && (fi.decoration & FontInfo.COMPACT) != 0){
			g.setColor(fi.background);
			g.fillRect(x-1, y, pf.stringWidth(label)+2, pf.height);			
			g.setColor(fi.foreground);
		}
        
        getFontInfo().drawString(g, label, x, y+ pf.ascent);
        //y += fm.getAscent () + 1;
        //        if (fi.underline)
        //          g.drawLine(x, y + 1, x + fi.stringWidth(label), y + 1);
    }



    public void setActionCommand(String cmd) {
        this.actionCommand = cmd;
    }

    public String getText() {
        return label;
    }

    public void setText(String l) {
        this.label = l;
        /*    if (getParent() != null) {
                invalidate();
                getParent().validate();
                repaint();
            }*/
        repaint();
    }

    public void doLayout(){
        if(compact && !getFocusable() && getParent() != null && !checkbox){
            textBlock = new TextBlock(fontInfo[0].font, label, getParent().getWidth());
        }
        else{
            super.doLayout();
        }
    }
    
    public Dimension getMinimumSize() {
        if (compact && !getFocusable ()) {
            
            if(label == null || label.length() == 0){
                return new Dimension(0, 0);
            }
            
            if(!checkbox && getParent() != null){
                return new Dimension(0, new TextBlock(fontInfo[0].font, label, getParent().getWidth()).getHeight());
            }
        }

        return super.getMinimumSize ();
    }


    public boolean selected() {
        return selected;
    }

    public void select(boolean state) {
        if (group == null) {
            if (selected != state) {
                selected = state;
                repaint();
            }
        }
        else
            for (int i = 0; i < group.size(); i++) {
                ScmDeviceLabel dl = (ScmDeviceLabel) group.elementAt(i);
                if (dl.selected != (dl == this)) {
                    dl.selected = !dl.selected;
                    dl.repaint();
                }
            }

    }
    
    
    public String toString(){
        return "DeviceLabel type "+type+" text: "+label+ " class "+getClass();
    }

}

package javax.microedition.lcdui;


import javax.microedition.midlet.ApplicationManager;

import org.me4se.impl.lcdui.*;

/**
 * @ME4SE INTERNAL
 */

class ScmSoftButton extends ScmDeviceLabel {

	/** Minimum with for a softButton to "Consume" a label */
	static final int MIN_WIDTH = 50;
	
	ScmDisplayable owner;
	Command command;
	Item item;
	int index;

	ScmSoftButton(ScmDisplayable owner, int index) {
		super("softButton."+(index), null, false);
		this.index = index;
		this.owner = owner;

		compact = false;
		highlight = false;
		setText("");

		int cnt = owner.softButtons.length;

		if(location != null){

			if(location.length >= 6){
				fontInfo = 
		            new FontInfo[] {
		                FontInfo.getFontInfo(location[4]+"."+index), 
						null};
				
				String al = location[5].toLowerCase();
				if("left".equals(al)) fontInfo[0].align = FontInfo.LEFT;
				else if("right".equals(al)) fontInfo[0].align = FontInfo.RIGHT;
				else if("center".equals(al)) fontInfo[0].align=FontInfo.CENTER;
			}
		}
		else {
			int w = (owner.getWidth() - 5 * (cnt - 1)) / cnt;
			int h = getMinimumSize().height;
			int x = (w + 5) * index;

			setBounds(x, owner.getHeight() - h, w, h);
		}
	}

	/*
	void setLabel (String s) {
	setText (s);
	
	}*/

	
	/*
	public void paint(java.awt.Graphics g){
		java.awt.Color fib = fontInfo[0].background;
		if(fib != null && (label == null || label.length() == 0) &&
			fib.getRed()+fib.getGreen()+fib.getBlue() < 3*128){
				g.setColor(new java.awt.Color(
					ApplicationManager.manager.getDeviceColor(
						new java.awt.Color(
							Math.min(fib.getRed()*2,255), 
							Math.min(fib.getGreen()*2,255), 
							Math.min(fib.getBlue()*2,255)).getRGB())));

			g.fillRect(0,0, getWidth(), getHeight());
		}
		else
			super.paint(g);
	}
*/


	public void setText(String s) {
//		java.awt.Color fib = fontInfo[0].background;
	/*	if(fib != null){
//			System.out.println("color: "+fontInfo[0].background.getRGB()&0x0ff00000);
		if ((s == null || s.length() == 0) 
			&& (fib.getRed()+fib.getGreen()+fib.getBlue() < 128*3))
			setBackground(
				new java.awt.Color(
					ApplicationManager.manager.getDeviceColor(
						new java.awt.Color(
							fib.getRed()/2, fib.getGreen()/2, fib.getBlue()/2).getRGB())));
		else
			setBackground(fontInfo[0].background);
		}*/
		super.setText(s);
		//System.out.println ("softb-settext "+s);

	}
	/*
	public void update (java.awt.Graphics g) {
	System.out.println ("paintsoftbutton; label: "+label);
	super.paint (g);
	}
	public void paint (java.awt.Graphics g) {
	System.out.println ("paintsoftbutton; label: "+getText ());
	super.paint (g);
	}
	*/
    
    
    String getLabel(String[] label){
        
        // start with longest
        
        boolean oneChar = getWidth() < MIN_WIDTH;
    
        if(!oneChar){
            for(int i = 2; i >= 1; i--){
                if(label[i] != null && !"".equals(label[i]) && getFontInfo().font.stringWidth(label[i]) <= getWidth()) {
                    return label[i];
                }
            }
        }

        for(int i = oneChar ? 0 : 1; i < 3; i++){
            if(label[i] != null && !"".equals(label[i])) {
                return oneChar ? label[i].substring(0, 1) : label[i];
            }
        }

        return "?";
    }

	void setCommand(Command cmd, Item item) {
		this.item = item;
		command = cmd;
		if (cmd == null)
			setText("");
		else {
			setText(getLabel(cmd.label));
		}

	//	invalidate();
	}

	public void action() {
		if (command != null){
	        Displayable d = ((ScmDisplayable) getParent()).displayable;
	        if(command.type == Command.CENTERBLOCKER){
	            Canvas c = (Canvas) d;
	            c.keyPressed(ApplicationManager.getInstance().getDeviceKeyCode("SOFT"+(index+1)));
	        }
	        else{
	            d.handleCommand(command, item);
	        }
		}
	}
}

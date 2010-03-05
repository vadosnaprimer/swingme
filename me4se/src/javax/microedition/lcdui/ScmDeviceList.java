package javax.microedition.lcdui;

import org.me4se.impl.lcdui.*;
import org.me4se.scm.*;

/** 
 * @ME4SE INTERNAL
 */


class ScmDeviceList extends ScmList {

    Screen owner;
    Item lastItem;

    public ScmDeviceList(Screen owner) {
        this.owner = owner;
    }

	public void paint(java.awt.Graphics g){
		super.paint(g);
		
		if(owner.iconUp != null)
			owner.iconUp.setState(getY() < 0 ? "on" : "off");
		if(owner.iconDown != null)
			owner.iconDown.setState(getY() + getHeight() > getParent().getHeight() ? "on" : "off");
	}


	private int getItemCmdCount(Item item){
		return (item == null || item.commands == null) 
			? 0
			: item.commands.size();
	}

    public void focusGained() {
        ScmDeviceComponent c = (ScmDeviceComponent) getFocusOwner();
        
        if(c == null) return;
        
        boolean update = false;
        if (c.selectButtonRequired != owner.selectButtonRequired
        	|| getItemCmdCount(c.item) != 0 
        	|| getItemCmdCount(lastItem) != 0) {
            owner.selectButtonRequired = c.selectButtonRequired;
            owner.container.updateButtons();
        }
        lastItem = c.item;
    }

    public void validateFocus() {

        getParent().doLayout();
        
        for (int i = 0; i < getComponentCount(); i++) {
            ScmComponent c = getComponent(i);
            //System.out.println ("component "+i+" focusable: "+c.getFocusable () + " y: "+getY () + " c.y:"+ c.getY () + " c.h:"+c.getHeight () + " parentH: "+getParent().getHeight ());
            if (getY() + c.getY() + c.getHeight() > getParent().getHeight()) {

                getComponent(0).requestFocus();
                return;
            }

            if (c.getFocusable()) {
                c.requestFocus();
                return;
            }
        }
        owner.container.requestFocus();
    }

}

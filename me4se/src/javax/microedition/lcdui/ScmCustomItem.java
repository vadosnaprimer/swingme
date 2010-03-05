/*
 * Created on 27.06.2004 by Stefan Haustein
 */
package javax.microedition.lcdui;

import java.awt.Dimension;

import javax.microedition.midlet.ApplicationManager;


/**
 * @author Stefan Haustein
 * @ME4SE INTERNAL
 */
public class ScmCustomItem extends ScmDeviceComponent {

	CustomItem customItem;
	
	public ScmCustomItem(CustomItem customItem) {
		super(customItem, "CustomItem", true);
		this.item = customItem;
		this.customItem = customItem;
	}
	
    public void focusGained(){
        super.focusGained();
        int [] viewport = new int[]{0,0, getWidth(), getHeight()};
        customItem.traverse(CustomItem.NONE, getWidth(), getHeight(), viewport);
    }
    
    public void focusLost(){
        super.focusLost();
        customItem.traverseOut();
    }
    
    
    public boolean keyPressed(String code){
        
        if(code.equals("UP") || code.equals("DOWN") || code.equals("LEFT") || code.equals("RIGHT")){
            
            int ga = customItem.getGameAction(ApplicationManager.getInstance().getDeviceKeyCode(code));
            
            int [] viewport = new int[]{0,0, getWidth(), getHeight()};
            boolean stay = customItem.traverse(ga, getWidth(), getHeight(), viewport);
            if(!stay){
                if(ga == Canvas.LEFT){
                    code = "UP";
                }
                else if(ga == Canvas.RIGHT){
                    code = "DOWN";
                }
                
                super.keyPressed(code);
             //   customItem.traverseOut();
            }
            return stay;
        }
        else {
            customItem.keyPressed(ApplicationManager.getInstance().getDeviceKeyCode(code));         
            return true;
        }
    }
	
	public void paint(java.awt.Graphics g){
		System.out.println(getWidth() + ", " +getHeight());
		customItem.paint(new Graphics(null, null, g), getWidth(), getHeight());
	}

	public Dimension getMinimumSize(){
		return new Dimension(customItem.getMinContentWidth(), customItem.getMinContentHeight());
	}
}

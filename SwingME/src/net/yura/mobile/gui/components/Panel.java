/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui.components;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.layout.Layout;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JPanel
 */
public class Panel extends Component {

	private String name;
	private Layout layout;
	private Vector components;
	private Hashtable constraints;

	
	public Panel() {
		
		components = new Vector();
		constraints = new Hashtable();
		selectable = false;
		// this is true as u need to select a panel to get to 1 of its components
	}
	
	public Panel(String n) {
		this();
		setName(n);
	}
	
	public Panel(Layout n) {
		this();
		setLayout(n);
		
	}
	
        /**
         * @param lt the specified layout manager
         * @see java.awt.Container#setLayout(java.awt.LayoutManager) Container.setLayout
         */
	public void setLayout(Layout lt) {

		layout = lt;

	}


    public void setOwnerAndParent(Window owner,Panel p){
    	super.setOwnerAndParent(owner,p);
    	
		for(int i = 0; i < components.size(); i++) {
			
			Component component = (Component)components.elementAt(i);
			
			component.setOwnerAndParent(owner,this);
		}
    	
    }

    /**
     * @param component the component to be added 
     * @see java.awt.Container#add(java.awt.Component) Container.add
     */
	public void add(Component component){
		components.addElement(component);
		
		component.setOwnerAndParent( owner,this );

	}
	
       public void add(Component component,int constraint){
		add(component);
		
		constraints.put(component, new Integer(constraint));
	}
        
	public void remove(Component component) {
		components.removeElement(component);
		
		component.setOwnerAndParent(null,null);
		
		constraints.remove(component);
	}
        
        public void remove(int c) {
            
                Component component = (Component)components.elementAt(c);
                remove(component);
	}
	
	public void insert(Component component,int index) {
		components.insertElementAt(component, index); 
                component.setOwnerAndParent( owner,this );
	}
	
	public void insert(Component component,int index,String constraint) {
		insert(component, index);
		
		constraints.put(component, constraint);
	}

	public int getComponentCount() {
            return components.size();
        }
        
	public void paint(Graphics g) {
		super.paint(g);

		paintChildren(g);
	}
	
	public void paintChildren(Graphics g){
            
            int clipX = g.getClipX();
            int clipY = g.getClipY();
            int clipWidth = g.getClipWidth();
            int clipHeight = g.getClipHeight();
            
            for(int i = 0; i < components.size(); i++){
                    Component component = (Component)components.elementAt(i);

                    int rx = component.getXWithBorder();
                    int ry = component.getYWithBorder();
                    
                    if (!(rx>clipX+clipWidth || ry >clipY+clipHeight || rx+component.getWidthWithBorder()<clipX || ry+component.getHeightWithBorder()<clipY)) {

                        int cx=component.getX();
                        int cy=component.getY();

                        g.translate(cx,cy );
                        component.paint(g);
                        g.translate(-cx, -cy);
                    }
                    //else {
                    //    System.out.println("Wont paint "+component);
                    //}

            }
	}
	
	// does nothing, but can be overridden
        public void paintComponent(Graphics g) {}

	public void doLayout() {
		
		for(int i = 0; i < components.size(); i++) {
			
			Component component = (Component)components.elementAt(i);
			
			//if (component instanceof Panel) {
				
				component.doLayout();
			//}
			
		}
		
		if (layout!=null) {
			layout.layoutPanel(this,constraints);
		}
	}

	
	
	

	public void pointerEvent(int type, int x, int y) {
		 
		for(int i = 0; i < components.size(); i++){
			Component component = (Component)components.elementAt(i);
			int x1 = component.getX();
			int y1 = component.getY();
			int x2 = x1 + component.getWidth();
			int y2 = y1 + component.getHeight();
			if( (x > x1 && x < x2) && (y > y1 && y < y2)){
				component.pointerEvent(type, x-x1, y-y1 );
			}
		}
		 
	}

	
	// BREAK OUT!!!
	// find next component in this panel
	
	public void breakOutAction(final Component component, final int direction, final boolean scrolltothere) {
		
                boolean right = (direction == Canvas.RIGHT) || (direction == Canvas.DOWN);
	
		int index = components.indexOf(component);
		int next = (index==components.size()-1)?(-1):(index+1);
		int prev = (index==-1)?( components.size()-1 ): (   (index==0)?(-1):(index-1)   );
		
		Component newone=null;
		
		if (right && next!=-1) {
		
			newone = (Component)components.elementAt(next);
			
		}
		else if (!right && prev!=-1) {
			
			 newone = (Component)components.elementAt(prev);
			
		}

		if (newone!=null) {
			

			if (newone.isSelectable()) {
			
				if ( scrollRectToVisible( newone.getXWithBorder(),newone.getYWithBorder(),newone.getWidthWithBorder(),newone.getHeightWithBorder() , true) ) {
					DesktopPane.getDesktopPane().setFocusedComponent(newone);
				}
				
			}
			else if (newone instanceof Panel) {

				((Panel)newone).breakOutAction(null,direction,false);
                                // here we do NOT pass scrolltothere onto the child panel
                                // dont scroll if we go to a child, only scroll if we hit a parent
			}
			else if (newone!=component) {// this is just a check so it cant go into a infinite loop
				
				breakOutAction(newone,direction,scrolltothere);
				
				// this is not a very good place to do this
				// DO NOT REMOVE THESE COMMENTS
				// it shows how this used to be done, and is useful to know
//				if ( scrollTo(newone) ) {
//					
//					if (newone instanceof Panel) {
//						((Panel)newone).breakOutAction(null,right,scrolltothere);
//					}
//					else {
//						owner.setActiveComponent(newone);
//					}
//				}
	
			}
			
		}
		else {

			// scroll at least in that direction
                    
                    // this will only be comming from a child
                    // only scroll in the direction if the child is NONE-selectable
                    // as if it IS selectable, it should handel its own moving around and scrolling
			if (scrolltothere && this instanceof ScrollPane && !component.isSelectable()) {

				((ScrollPane)this).getComponent().scrollUpDown(direction);
	
			}

                    	if (parent!=null) {
			
				parent.breakOutAction(this, direction ,scrolltothere);
			}
                        else if (DesktopPane.getDesktopPane().getFocusedComponent()!=null) {
                            // TODO
                            // we need to find out if we smart scrolled anywhere,
                            // or scrolled at all
                            // if not then go back to top component from the bottom one
                            System.out.println("TODO: if scroll did not happen, go to top component");
                        }

		}
		
	}

	public Vector getComponents() {
		return components;
	}
	
	public boolean repaintComponent(Graphics g, Component focusComponent) {
		
                for(int i = 0; i < components.size(); i++){
                        Component component = (Component)components.elementAt(i);

                        int x=component.getX();
                        int y=component.getY();

                    if (component == focusComponent){

                        g.translate(x,y);
                        component.paint(g);
                        g.translate(-x,-y);

                        return true;
                    }
                    else if (component instanceof Panel){

                        g.translate(x,y);
                        boolean good = ((Panel)component).repaintComponent(g,focusComponent);
                        g.translate(-x,-y);

                        if (good) { return good; }
                    }
                }
		return false;
	}

	public void removeAll() {

            for(int i = 0; i < components.size(); i++){
                Component component = (Component)components.elementAt(i);

                if (component.getOwner() == owner) {
                    component.setOwnerAndParent(null,null);
                }
            }
            components.removeAllElements();
            constraints.clear();
	}
	
	public void setName(String n) {
		
		name  = n;
	}
        public String getName() {
            return name;
        }
        
        // if a layout manager is resizing us, we want to redo the layout of our children
	public void setBoundsWithBorder(int x,int y,int w, int h){
            
            int oldw = width;
            int oldh = height;
            
            super.setBoundsWithBorder(x,y,w,h);
            
            if (oldw!=width || oldh != height) {
                doLayout();
            }
        }
        
	public String toString() {
		
		return ((name!=null)?(name+" "):"")+super.toString() + " "+ components;
		
	}
	
}


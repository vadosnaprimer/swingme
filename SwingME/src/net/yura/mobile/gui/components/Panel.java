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

        protected int preferredWidth=-1;
        protected int preferredHeight=-1;
        
	/**
         * @see javax.swing.JPanel#JPanel() JPanel.JPanel
         */
	public Panel() {
		components = new Vector();
		constraints = new Hashtable(1);
		selectable = false;
		// this is true as u need to select a panel to get to 1 of its components
	}
	
        /**
         * @param n the LayoutManager to use
         * @see javax.swing.JPanel#JPanel(java.awt.LayoutManager) JPanel.JPanel
         */
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

        public Hashtable getConstraints() {
            return constraints;
        }


    /**
     * @param component the component to be added 
     * @see java.awt.Container#add(java.awt.Component) Container.add
     */
	public void add(Component component){
            addImpl(component);

	}
	/**
         * @param component
         * @param constraint
         * @see java.awt.Container#add(java.awt.Component, java.lang.Object) Container.add
         */
       public void add(Component component,int constraint){
		addImpl(component);
		constraints.put(component, new Integer(constraint));
	}
       
       /**
        * @see java.awt.Container#addImpl(java.awt.Component, java.lang.Object, int) Container.addImpl
        */
       private void addImpl(Component component) {
           	components.addElement(component);
		component.setParent( this );
       }
       
        /**
         * @param component The component to remove
         * @see java.awt.Container#remove(java.awt.Component) Container.remove
         */
	public void remove(Component component) {
		components.removeElement(component);
		
		component.setParent(null);
		
		constraints.remove(component);
	}
        /**
         * @param c The index of the component to remove
         * @see java.awt.Container#remove(int) Container.remove
         */
        public void remove(int c) {
            
                Component component = (Component)components.elementAt(c);
                remove(component);
	}
	
        
        /**
         * @see java.awt.Container#add(java.awt.Component, int) Container.add
         */
	public void insert(Component component,int index) {
		components.insertElementAt(component, index); 
                component.setParent( this );
	}
	
        /**
         * @see java.awt.Container#add(java.awt.Component, java.lang.Object, int) Container.add
         */
	public void insert(Component component,int constraint,int index) {
		insert(component, index);
		
		constraints.put(component, new Integer(constraint) );
	}

        /**
         * @return the number of components in this panel
         * @see java.awt.Container#getComponentCount() Container.getComponentCount
         */
	public int getComponentCount() {
            return components.size();
        }
        
        /**
         * @param g Graphics object
         * @see java.awt.Container#paint(java.awt.Graphics) Container.paint
         */
	public void paint(Graphics g) {
		super.paint(g);

		paintChildren(g);
	}
	
        /**
         * @param g Graphics object
         * @see java.awt.Container#paintComponents(java.awt.Graphics) Container.paintComponents
         */
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

        /**
         * @param w The preferred Width (can be -1 for no preference)
         * @param h The preferred Height (can be -1 for no preference)
         * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension) JComponent.setPreferredSize
         */
        public void setPreferredSize(int w, int h) {
            preferredWidth = w;
            preferredHeight = h;
        }
	public int getPreferredWidth() {
		return preferredWidth;
	}
	public int getPreferredHeight() {
		return preferredHeight;
	}

        /**
         * works out the current size of this panel
         * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
         */
        public void workoutSize() {
            
            	for(int i = 0; i < components.size(); i++) {
			
			Component component = (Component)components.elementAt(i);
			component.workoutSize();

		}
                
                if (preferredWidth!=-1) {
                    width = preferredWidth;
                }
                else if (layout!=null) {
			width = layout.getPreferredWidth(this);
		}
                // TODO else??? use 0???

                if (preferredHeight!=-1) {
                    height = preferredHeight;
                }
                else if (layout!=null) {
			height = layout.getPreferredHeight(this);
		}
                // TODO else??? use 0???

        }
        
        /**
         * redo the layout
         * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
         */
	public void doLayout() {
		
		if (layout!=null) {
			layout.layoutPanel(this);
		}
                
                for(int i = 0; i < components.size(); i++) {
			
			Component component = (Component)components.elementAt(i);
			if (component instanceof Panel) {
                            ((Panel)component).doLayout();
                        }

		}
                
	}
        
        /**
         * this means reclac the size of children
         * and then redo the layout
         * @see javax.swing.JComponent#revalidate() JComponent.revalidate
         */
        public void revalidate() {
                for(int i = 0; i < components.size(); i++) {	
			((Component)components.elementAt(i)).workoutSize();
                }
                doLayout();
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

				((Panel)newone).breakOutAction(null,direction,scrolltothere && DesktopPane.getDesktopPane().getFocusedComponent()==null);
                                // here we do NOT pass scrolltothere onto the child panel
                                // unless we have NOTHING active, then pss it on to children
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

        /**
         * @return an array of all the components in this container
         * @see java.awt.Container#getComponents() Container.getComponents
         */
	public Vector getComponents() {
		return components;
	}

        /**
         * @see java.awt.Container#removeAll() Container.removeAll
         */
	public void removeAll() {

            for(int i = 0; i < components.size(); i++){
                Component component = (Component)components.elementAt(i);
                component.setParent(null);
            }
            components.removeAllElements();
            constraints.clear();
	}
	
        /**
         * @param n The new name for this panel
         * @see java.awt.Component#setName(java.lang.String) Component.setName
         * @see TabbedPane#add(net.yura.mobile.gui.components.Component)
         */
	public void setName(String n) {
		name  = n;
                updateUI();
	}
        
	public String toString() {
		
		return ((name!=null)?(name+" "):"")+super.toString() + " "+ components;
		
	}

        public void clip(Graphics g) {
            if (parent!=null) {
                parent.clip(g);
            }
        }

        /**
         * @param x the x coordinate
         * @param y the y coordinate 
         * @return the top-most child is returned
         * @see java.awt.Container#getComponentAt(int, int) Container.getComponentAt
         */
        public Component getComponentAt(int x, int y) {
        	for(int i = 0; i < components.size(); i++){
			Component component = (Component)components.elementAt(i);
			int x1 = component.getXWithBorder();
			int y1 = component.getYWithBorder();
			int x2 = x1 + component.getWidthWithBorder();
			int y2 = y1 + component.getHeightWithBorder();
			if( x >= x1 && x <= x2 && y >= y1 && y <= y2){
                            
                            if (component instanceof Panel) {
                                return ((Panel)component).getComponentAt( x-component.getX(), y-component.getY() );
                            }
			    return component;
			}
		}
                return this;
        }
        
        /**
         * @return The name of the panel
         * @see java.awt.Component#getName() Component.getName
         * @see TabbedPane#add(net.yura.mobile.gui.components.Component)
         */
    public String getName() {
        return name==null?"Panel":name;
    }
	
}


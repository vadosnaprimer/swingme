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
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.layout.Layout;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JPanel
 */
public class Panel extends Component {

    private Layout layout;
    private Vector components;
    private Hashtable constraints;

    /**
     * @see javax.swing.JPanel#JPanel() JPanel.JPanel
     */
    public Panel() {
        components = new Vector();
        constraints = new Hashtable(1);
        focusable = false;
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
    /**
     * @see java.awt.Container#getLayout() Container.getLayout
     */
    public Layout getLayout() {
        return layout;
    }

    public Hashtable getConstraints() {
        return constraints;
    }

    /**
     * @param component the component to be added
     * @see java.awt.Container#add(java.awt.Component) Container.add
     */
    public void add(Component component){
            addImpl(component,null,getComponentCount());
    }

    public void add(Component component,int constraint){
        addImpl(component,new Integer(constraint),getComponentCount());
    }

    /**
     * @param component
     * @param constraint
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object) Container.add
     */
    public void add(Component component,Object constraint){
        addImpl(component,constraint,getComponentCount());
    }

    /**
     * @see java.awt.Container#add(java.awt.Component, int) Container.add
     */
    public void insert(Component component,int index) {
            addImpl(component,null, index);
    }

    /**
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object, int) Container.add
     */
    public void insert(Component component,Object constraint,int index) {
            addImpl(component,constraint, index);
    }

   /**
    * @see java.awt.Container#addImpl(java.awt.Component, java.lang.Object, int) Container.addImpl
    */
   protected void addImpl(Component component,Object cons,int index) {
        components.insertElementAt(component,index);
        if (cons!=null) {
            constraints.put(component, cons);
        }
        component.setParent( this );
   }

    /**
     * @param component The component to remove
     * @see java.awt.Container#remove(java.awt.Component) Container.remove
     */
    public void remove(Component component) {
            remove( components.indexOf(component) );
    }
    /**
     * @param c The index of the component to remove
     * @see java.awt.Container#remove(int) Container.remove
     */
    public void remove(int c) {
        Component component = (Component)components.elementAt(c);

        if (component.isFocusOwner()) {
            component.getWindow().setFocusedComponent(null);
        }

        components.removeElementAt(c);
        component.removeParent(this);
        constraints.remove(component);
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
    public void paint(Graphics2D g) {
        super.paint(g);
        paintChildren(g);
    }

    /**
     * @param g Graphics object
     * @see java.awt.Container#paintComponents(java.awt.Graphics) Container.paintComponents
     */
    public void paintChildren(Graphics2D g){

            int clip[] = g.getClip();

            for(int i = 0; i < components.size(); i++){
                    Component component = (Component)components.elementAt(i);
                    if (!component.isVisible()) {
                        continue;
                    }

                    int rx = component.getXWithBorder();
                    int ry = component.getYWithBorder();

                    if (!(rx>clip[0]+clip[2] || ry >clip[1]+clip[3] || rx+component.getWidthWithBorder()<clip[0] || ry+component.getHeightWithBorder()<clip[1])) {

                        int cx=component.getX();
                        int cy=component.getY();

                        g.translate(cx,cy );
                        component.paint(g);
                        g.translate(-cx, -cy);
                    }
                    //else {
                    //    Logger.debug("Wont paint "+component);
                    //}

            }
    }

    // does nothing, but can be overridden
    public void paintComponent(Graphics2D g) {}

    /**
     * works out the current size of this panel
     * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
     */
    public void workoutMinimumSize() {

        for(int i = 0; i < components.size(); i++) {

            Component component = (Component)components.elementAt(i);
            component.workoutSize();

        }

        if (layout!=null) {
            width = layout.getPreferredWidth(this);
            height = layout.getPreferredHeight(this);
        }
        else {
            width=0;
            height=0;
        }

    }


    /**
     * sets the new size and revaliates the window
     * @param width new Width
     * @param height new Height
     * @see java.awt.Component#setSize(int, int) Component.setSize
     */
    public void setSize(int width, int height) {
        super.setSize(width, height);
        doLayout();
    }

    /**
     * redo the layout
     * (SHOULD NOT BE CALLED OUTSIDE THE FRAMEWORK)
     */
    public void doLayout() {

        if (layout!=null) {
            layout.layoutPanel(this);
        }
        else {
            for(int i = 0; i < components.size(); i++) {
                Component component = (Component)components.elementAt(i);
                if (component instanceof Panel) {
                    ((Panel)component).doLayout();
                }
            }
        }

    }

    /**
     * this means reclac the size of children
     * and then redo the layout
     * @see javax.swing.JComponent#validate() JComponent.validate
     */
    public void validate() {
        for(int i = 0; i < components.size(); i++) {
                ((Component)components.elementAt(i)).workoutSize();
        }
        doLayout();
        Window w1 = getWindow();
        if (w1!=null) {
            w1.setupFocusedComponent();
        }
    }

    // BREAK OUT!!!
    // find next component in this panel

    protected void breakOutAction(Component component, final int direction, final boolean scrolltothere,final boolean forceFocus) {
        int index = components.indexOf(component);
        boolean right = (direction == Canvas.RIGHT) || (direction == Canvas.DOWN);

        while (true) {

            index =  right? ((index==components.size()-1)?(-1):(index+1)) : ((index==-1)?( components.size()-1 ): (   (index==0)?(-1):(index-1)   ));
            if (index == -1) break;
            component = (Component)components.elementAt(index);

            if (!component.isVisible()) {
                continue;
            }

            if (component.isFocusable()) {

                boolean requestFocus = false;

                if (getWindow().getFocusOwner() == null) {
                    if (component.isComponentVisible()) {
                        requestFocus = true;
                    }
                    else {
                        breakOutAction(component,direction,scrolltothere,forceFocus);
                    }
                }
                else if (scrolltothere) {
                    requestFocus = scrollRectToVisible( component.getXWithBorder(),component.getYWithBorder(),component.getWidthWithBorder(),component.getHeightWithBorder() , !forceFocus);
                }

                if (requestFocus || component.isComponentVisible()) {
                    component.requestFocusInWindow();
                }
                return;
            }
            else if (component instanceof Panel) {

                ((Panel)component).breakOutAction(null,direction,scrolltothere,forceFocus);
                                // && DesktopPane.getDesktopPane().getFocusedComponent()==null
                                // ^ this hack was here, but it broke things like TextField test scrolling

                                // here we do NOT pass scrolltothere onto the child panel
                                // unless we have NOTHING active, then pass it on to children
                                // dont scroll if we go to a child, only scroll if we hit a parent
                return;
            }


//            else if (newone!=component) {// this is just a check so it cant go into a infinite loop
//
//                breakOutAction(newone,direction,scrolltothere,forceFocus);
//
////                this is not a very good place to do this
////                DO NOT REMOVE THESE COMMENTS
////                it shows how this used to be done, and is useful to know
////                if ( scrollTo(newone) ) {
////
////                    if (newone instanceof Panel) {
////                        ((Panel)newone).breakOutAction(null,right,scrolltothere);
////                    }
////                    else {
////                        owner.setActiveComponent(newone);
////                    }
////                }
//
//            }

        }


        boolean scrolled=false;
        // scroll at least in that direction
        // this will only be comming from a child
        // only scroll in the direction if the child is NONE-selectable
        // as if it IS selectable, it should handel its own moving around and scrolling
        if (scrolltothere && this instanceof ScrollPane && !component.isFocusable()) {
            scrolled = ((ScrollPane)this).getView().scrollUpDown(direction);
        }

        if (!scrolled) {
            if (parent != null && !(parent instanceof Window)) {
                // passes onto parent
                ((Panel)parent).breakOutAction(this, direction ,scrolltothere,forceFocus);
            }
            else if (getWindow().getFocusOwner()!=null) {
                // done for loop to first/last component
                breakOutAction(null, direction, scrolltothere,true);
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
        while (!components.isEmpty()){
            remove( components.size()-1 );
        }
    }


    public String toString() {
        return super.toString() + " "+ components;
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
            if (!component.isVisible()) {
                continue;
            }
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

    protected String getDefaultName() {
        return "Panel";
    }

    public Button findMneonicButton(int mnu) {
        for(int i = 0; i < components.size(); i++) {
            Component component = (Component)components.elementAt(i);
            if (component instanceof Button) {
                Button button = (Button)component;
                if (button.getMnemonic() == mnu) {
                    return button;
                }
                else if (component instanceof Menu) {
                    Button button1 = ((Menu)component).findMneonicButton(mnu);
                    if (button1!=null) {
                        return button1;
                    }
                }
            }
            else if (component instanceof MenuBar) {
                Button button = ((MenuBar)component).findMneonicButton(mnu);
                if (button!=null) {
                    return button;
                }
            }
            else if (component instanceof Panel) {
                Button button = ((Panel)component).findMneonicButton(mnu);
                if (button!=null) {
                    return button;
                }
            }

        }
        return null;
    }

}


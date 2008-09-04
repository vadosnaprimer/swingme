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

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.cellrenderer.DefaultTabRenderer;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.Layout;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JTabbedPane
 */
public class TabbedPane extends Panel implements ChangeListener {

        private List tabList;
        private Vector tabs;
        private Panel tabBar;
        private Panel tabContent;
        private int tabPosition;

        /**
         * @see javax.swing.JTabbedPane#JTabbedPane() JTabbedPane.JTabbedPane
         */
        public TabbedPane() {
            this(Graphics.TOP);
        }
        
        /**
         * @see javax.swing.JTabbedPane#JTabbedPane(int) JTabbedPane.JTabbedPane
         */
        public TabbedPane(int a) {

            Layout l = new BorderLayout();
            setLayout(l);
            setName("TabbedPane");
            tabList = new List( new DefaultTabRenderer() );
            tabs = new Vector();

            tabList.addChangeListener(this);
            ScrollPane scroll = new ScrollPane(tabList,ScrollPane.MODE_SCROLLARROWS);

            tabContent = new Panel(l);
            tabBar = new Panel(l);
            tabBar.add(scroll);
            // this will ALWAYS be transparent as its the scroll that does the drawing for the theme
            tabList.background = -1;
            scroll.background = -1;
            
            setTabPlacement(a);

        }

        /**
         * @param a the placement for the tabs relative to the content 
         * @see javax.swing.JTabbedPane#setTabPlacement(int) JTabbedPane.setTabPlacement
         */
        public void setTabPlacement(int a) {
            
            tabPosition = a;
            
            tabList.setLayoutOrientation( (a==Graphics.TOP || a==Graphics.BOTTOM) );
            
            ListCellRenderer lcr = tabList.getCellRenderer();
            if (lcr instanceof DefaultTabRenderer) {
                ((DefaultTabRenderer)lcr).setTabPlacement(a);
            }
            
            tabBar.setName("Tab" + (a==Graphics.TOP?"Top":(a==Graphics.LEFT?"Left":(a==Graphics.RIGHT?"Right":"Bottom"))) );

            tabContent.setName("TabContent" + (a==Graphics.TOP?"Bottom":(a==Graphics.LEFT?"Right":(a==Graphics.RIGHT?"Left":"Top"))) );
            
            super.removeAll();
            
            if (tabPosition==Graphics.TOP || tabPosition==Graphics.LEFT) {
                super.add(tabBar,tabPosition);
            }

            super.add(tabContent);

            if (tabPosition==Graphics.BOTTOM || tabPosition==Graphics.RIGHT) {
                super.add(tabBar,tabPosition);
            }
            
        }

        /**
         * @see javax.swing.JList#setCellRenderer(javax.swing.ListCellRenderer) JList.setCellRenderer
         */
        public void setTabRenderer(ListCellRenderer lcr) {
            
            tabList.setCellRenderer(lcr);
        }

        /**
         * @param p the Panel to add
         * @see javax.swing.JTabbedPane#add(java.awt.Component) JTabbedPane.add
         */
        public void add(Component p) {

                addTab(p.getName(),p);
        }

        /**
         * @param title The title for the tab
         * @param component The component of the tab
         * @see javax.swing.JTabbedPane#addTab(java.lang.String, java.awt.Component) JTabbedPane.addTab
         */
        public void addTab(String title, Component component) {
            addTab(title, null, component);
        }

        /**
         * @param title The title for the tab
         * @param icon The icon for the tab
         * @param component The component of the tab
         * @see javax.swing.JTabbedPane#addTab(java.lang.String, javax.swing.Icon, java.awt.Component) JTabbedPane.addTab
         */
        public void addTab(String title, Image icon, Component component) {
            addTab(title, icon, component, null);
        }

        /**
         * @param title
         * @param icon
         * @param component
         * @param tip
         * @see javax.swing.JTabbedPane#addTab(java.lang.String, javax.swing.Icon, java.awt.Component, java.lang.String) JTabbedPane.addTab
         */
        public void addTab(String title, Image icon, Component component, String tip) {
            tabList.addElement(new Option(null,title,icon,tip));
            tabs.addElement(component);

            if (tabList.getSelectedIndex()==-1) {
                setSelectedIndex(0);
            }
        }
        
        /**
         * @param a index of tab to remove
         * @see javax.swing.JTabbedPane#removeTabAt(int) JTabbedPane.removeTabAt
         */
        public void removeTabAt(int a) {

            if (tabs.size()==1 && a==0) {
                removeAll();
            }
            else {

                // setup the new tab to be selected
                if (tabList.getSelectedIndex()==a) {
                    if ((tabs.size()-1) == tabList.getSelectedIndex()) {
                        setSelectedIndex(tabList.getSelectedIndex()-1);
                    }
                    else {
                        setSelectedIndex(tabList.getSelectedIndex()+1);
                    }
                }

                // actually remove the tab
                tabList.removeElementAt(a);
                tabs.removeElementAt(a);

            }

        }
        /**
         * @param title
         * @param icon
         * @param component
         * @param tip
         * @param index
         * @see javax.swing.JTabbedPane#insertTab(java.lang.String, javax.swing.Icon, java.awt.Component, java.lang.String, int) JTabbedPane.insertTab
         */
        public void insertTab(String title, Image icon, Component component, String tip, int index) {

            tabList.getItems().insertElementAt(new Option(null,title,icon,tip), index);
            tabs.insertElementAt(component,index);

            setSelectedIndex(index);
        }

        /**
         * @param index the index of the tab to get
         * @return gets the component on the tab with index a
         * @see javax.swing.JTabbedPane#getComponentAt(int) JTabbedPane.getComponentAt
         */
        public Panel getComponentAt(int index) {
            return (Panel)tabs.elementAt(index);
        }

        public void workoutSize() {

            tabList.workoutSize();

            // we can only really do this here
            // as when we have NO tabs added
            // we dont know the thickness of the tab bar
            if (tabPosition==Graphics.TOP || tabPosition==Graphics.BOTTOM) {
                tabBar.setPreferredSize(-1, tabList.getHeight());
            }
            else {
                tabBar.setPreferredSize(tabList.getWidth(), -1);
            }

	    tabContent.setPreferredSize(-1,-1);

	    for(int i = 0; i < tabs.size(); i++) {
			
		Component component = (Component)tabs.elementAt(i);
		component.workoutSize();
		if (component.getWidthWithBorder() > tabContent.getPreferredWidth()) {
			tabContent.setPreferredSize(component.getWidthWithBorder(),tabContent.getPreferredHeight());
		}
		if (component.getHeightWithBorder() > tabContent.getPreferredHeight()) {
			tabContent.setPreferredSize(tabContent.getPreferredWidth(),component.getHeightWithBorder());
		}
	    }

	    super.workoutSize();

        }

    public void changeEvent(int num) {

        Component thetabtoAdd = (Component)tabs.elementAt(num);

        //Component oldTab = (Component)tabs.elementAt(currentTabIndex);
        //int index = getComponents().indexOf(oldTab);
        
        tabContent.removeAll();
        tabContent.add(thetabtoAdd);

        revalidate();
        repaint();

        if (!tabList.isSelectable() && getWindow()!=null) {
            getWindow().setupFocusedComponent();
        }

    }

    public void removeAll() {

        if (tabList.getSelectedIndex()!=-1) {

            tabContent.removeAll();

            tabs.removeAllElements();
            tabList.setListData( new Vector() );

        }

    }
    /**
     * @param a the index to be selected
     * @see javax.swing.JTabbedPane#setSelectedIndex(int) JTabbedPane.setSelectedIndex
     */
    public void setSelectedIndex(int a) {
        tabList.setSelectedIndex(a);
    }
    /**
     * @param c the Component to be selected
     * @see javax.swing.JTabbedPane#setSelectedComponent(java.awt.Component) JTabbedPane.setSelectedComponent
     */
    public void setSelectedComponent(Component c) {
        setSelectedIndex(tabs.indexOf(c));
    }
    /**
     * @return the index of the selected tab
     * @see javax.swing.JTabbedPane#getSelectedIndex() JTabbedPane.getSelectedIndex
     */
    public int getSelectedIndex() {
        return tabList.getSelectedIndex();
    }
    
    /**
     * @return an integer specifying the number of tabbed pages
     * @see javax.swing.JTabbedPane#getTabCount() JTabbedPane.getTabCount
     */
    public int getTabCount() {
        return tabList.getItems().size();
    }

    public void setSelectable(boolean a) {
        tabList.setSelectable(a);
    }

}

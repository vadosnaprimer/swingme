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
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.cellrenderer.DefaultTabRenderer;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JTabbedPane
 */
public class TabbedPane extends Panel implements ChangeListener {

        private List tabList;
        private Vector tabs;
        private ScrollPane scroll;
        private int tabPosition;
        private int currentTabIndex;

        public TabbedPane() {
            this(Graphics.TOP,new DefaultTabRenderer(Graphics.TOP),new MatteBorder(0, 0, 1, 0, 0x00000000));

            //this(Graphics.LEFT,new DefaultTabRenderer(Graphics.LEFT),new MatteBorder(0, 0, 0, 1, 0x00000000));

            //this(Graphics.RIGHT,new DefaultTabRenderer(Graphics.RIGHT),new MatteBorder(0, 1, 0, 0, 0x00000000));

            //this(Graphics.BOTTOM,new DefaultTabRenderer(Graphics.BOTTOM),new MatteBorder(1, 0, 0, 0, 0x00000000));

        }
        public TabbedPane(int a,ListCellRenderer b,Border art) {

            setLayout(new BorderLayout());
            setName("TabbedPane");
            tabList = new List(null,b,(a==Graphics.TOP || a==Graphics.BOTTOM));
            tabs = new Vector();

            tabList.addChangeListener(this);
            scroll = new ScrollPane(tabList,ScrollPane.MODE_SCROLLARROWS);

            if (art!=null) {
                scroll.setBorder(new CompoundBorder(
                      art,
                      new EmptyBorder(art)
                ));
            }

            tabPosition = a;
            currentTabIndex = -1;

            tabList.background = -1;

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
            tabList.addElement(new Option(null,title,icon));
            tabs.addElement(component);

            if (currentTabIndex==-1) {
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
                if (currentTabIndex==a) {
                    if ((tabs.size()-1) == currentTabIndex) {
                        setSelectedIndex(currentTabIndex-1);
                    }
                    else {
                        setSelectedIndex(currentTabIndex+1);
                        currentTabIndex--;
                    }
                }
                else if (currentTabIndex > a) {
                    currentTabIndex--;
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

            tabList.getItems().insertElementAt(new Option(null,title,icon), index);
            tabs.insertElementAt(component,index);
            if (index <= currentTabIndex) {
                currentTabIndex++;
            }
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

            super.workoutSize();

            // we can only really do this here
            // as when we have NO tabs added
            // we dont know the thickness of the tab bar
            if (tabPosition==Graphics.TOP || tabPosition==Graphics.BOTTOM) {
                scroll.setSize(width, tabList.getHeight());
            }
            else {
                scroll.setSize(tabList.getWidth(), height);
            }

        }

    public void changeEvent(int num) {

        Component thetabtoAdd = (Component)tabs.elementAt(num);

        if (currentTabIndex==-1) {

                if (tabPosition==Graphics.TOP || tabPosition==Graphics.LEFT) {
                    super.add(scroll,tabPosition);
                }

                super.add(thetabtoAdd);

                if (tabPosition==Graphics.BOTTOM || tabPosition==Graphics.RIGHT) {
                    super.add(scroll,tabPosition);
                }

        }
        else {

            Component oldTab = (Component)tabs.elementAt(currentTabIndex);

            int index = getComponents().indexOf(oldTab);

            remove(index);
            insert(thetabtoAdd, index);
        }

        currentTabIndex = num;
        revalidate();
        if (!tabList.isSelectable() && owner!=null) {
            owner.setupFocusedComponent();
        }

    }

    public void removeAll() {

        if (currentTabIndex!=-1) {

            remove(scroll);
            remove( getComponents().indexOf( tabs.elementAt(currentTabIndex) ) );

            tabs.removeAllElements();
            tabList.setListData( new Vector() );

            currentTabIndex = -1;
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

    public List getList() {
        return tabList;
    }

}

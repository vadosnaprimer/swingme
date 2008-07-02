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
 * @author ymamyrin
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
            
            tabList = new List(null,b,(a==Graphics.TOP || a==Graphics.BOTTOM));
            tabs = new Vector();
            
            tabList.addChangeListener(this);
            scroll = new ScrollPane(tabList,ScrollPane.MODE_SCROLLARROWS);
            
            if (art!=null) {
                scroll.setBorder(new CompoundBorder(
                      art,
                      new EmptyBorder(-art.getTop(),-art.getLeft(),-art.getBottom(),-art.getRight())
                ));
            }

            tabPosition = a;
            currentTabIndex = -1;
        }

        public void addTab(Panel p) {
            addTab(p.getName(),p);
        }
        
        public void addTab(String title, Component component) {
            addTab(title, null, component);
        }

        public void addTab(String title, Image icon, Component component) {
            tabList.addListItem(new Option(null,title,icon));
            tabs.addElement(component);
            
            if (currentTabIndex==-1) {
                setSelectedIndex(0);
            }
        }

        public void doLayout() {

            // we can only really do this here
            // as when we have NO tabs added
            // we dont know the thickness of the tab bar
            if (tabPosition==Graphics.TOP || tabPosition==Graphics.BOTTOM) {
                scroll.setSize(width, tabList.getHeight());
            }
            else {
                scroll.setSize(tabList.getWidth(), height);
            }
            
            super.doLayout();
            
        }
        
    public void changeEvent(int num) {

        Component thetabtoAdd = (Component)tabs.elementAt(num);
        
        if (currentTabIndex==-1) {
        
                if (tabPosition==Graphics.TOP || tabPosition==Graphics.LEFT) {
                    add(scroll,tabPosition);
                }
                
                add(thetabtoAdd);
                
                if (tabPosition==Graphics.BOTTOM || tabPosition==Graphics.RIGHT) {
                    add(scroll,tabPosition);
                }

        }
        else {
            
            Component oldTab = (Component)tabs.elementAt(currentTabIndex);
            
            int index = getComponents().indexOf(oldTab);
            
            remove(index);
            insert(thetabtoAdd, index);
        }

        currentTabIndex = num;
        doLayout();

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
    
    public void setSelectedIndex(int a) {
        tabList.setFocusedItemIndex(a);
    }
    public int getSelectedIndex() {
        return tabList.getFocusedItemIndex();
    }
    public int getTabCount() {
        return tabList.getItems().size();
    }

    public void setSelectable(boolean a) {
        tabList.setSelectable(a);
    }

}

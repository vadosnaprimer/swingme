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

package net.yura.mobile.gui;

import java.util.Hashtable;
import java.util.Vector;
import net.yura.mobile.gui.border.Border;

/**
 * @author Yura Mamyrin
 */
public class Style {

    public static final int ALL = 0;
    public static final int SELECTED = 8;
    public static final int ENABLED = 1;
    public static final int DISABLED = 2;
    public static final int FOCUSED = 4;
    private static final int[] searchOrder = new int[] { SELECTED , FOCUSED , ENABLED , DISABLED , ALL};
    
    private Font font;
    private Border border;
    private int background;
    private int foreground;

    private Hashtable fontStates;
    private Hashtable borderStates;
    private Hashtable backgroundStates;
    private Hashtable foregroundStates;
    private Hashtable propertiesStates;

    public Style() {
        

    }
    
    void putAll(Style oldStyle) {

    }
    Object getValueFromMap(Hashtable t,int state) {
        if (t==null) return null;
        Object a;
        a = t.get(new Integer(state));
        
        // as to indicate that something is selected is quite important
        // we will try and get the value of this
        for (int c=0;c<searchOrder.length;c++) {
            if (a!=null) {
                break;
            }
            if ((state & searchOrder[c])!=0) {
                a = t.get(new Integer(searchOrder[c]));
            }
        }

        return a;
    }
    
    public void addBorder(Border b, int state) {
        if (state==ALL) { border=b; return; }
        if (borderStates==null) {
            borderStates = new Hashtable();
        }
        borderStates.put(new Integer(state), b);
    }
    public Border getBorder(int state) {
        Border b = (Border)getValueFromMap(borderStates,state);
        return (b==null)?border:b;
    }
    
    public void addFont(Font b, int state) {
        if (state==ALL) { font=b; return; }
        if (fontStates==null) {
            fontStates = new Hashtable();
        }
        fontStates.put(new Integer(state), b);
    }
    public Font getFont(int state) {
        Font b = (Font)getValueFromMap(fontStates,state);
        return (b==null)?font:b;
    }
    
    
    public void addBackground(int b, int state) {
        if (state==ALL) { background=b; return; }
        if (backgroundStates==null) {
            backgroundStates = new Hashtable();
        }
        backgroundStates.put(new Integer(state), new Integer(b));
    }
    public int getBackground(int state) {
        Integer b = (Integer)getValueFromMap(backgroundStates,state);
        return (b==null)?background:b.intValue();
    }
    
    public void addForeground(int b, int state) {
        if (state==ALL) { foreground=b; return; }
        if (foregroundStates==null) {
            foregroundStates = new Hashtable();
        }
        foregroundStates.put(new Integer(state), new Integer(b));
    }
    public int getForeground(int state) {
        Integer b = (Integer)getValueFromMap(foregroundStates,state);
        return (b==null)?foreground:b.intValue();
    }
    
    public void addProperty(Object b, String key, int state) {
        if (propertiesStates==null) {
            propertiesStates = new Hashtable();
        }
        Hashtable table = (Hashtable)propertiesStates.get(new Integer(state));
        if (table==null) {
            table = new Hashtable();
            propertiesStates.put(new Integer(state), table);
        }
        
        table.put(key, b);
    }
    public Object getProperty(String key,int state) {
        Object obj = findProperty(key,state);

        for (int c=0;c<searchOrder.length;c++) {
            if (obj!=null) {
                break;
            }
            if ((state & searchOrder[c])!=0) {
                obj = findProperty(key,searchOrder[c]);
            }
        }
        return obj;

    }
    
    private Object findProperty(String key,int state) {
        Hashtable v = (Hashtable)getValueFromMap(propertiesStates,state);
        if (v==null) {
            return null;
        }
        return v.get(key);
    }

}

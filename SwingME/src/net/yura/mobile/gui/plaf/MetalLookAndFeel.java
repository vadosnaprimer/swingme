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

package net.yura.mobile.gui.plaf;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.border.TabBorder;

/**
 * @author Yura Mamyrin
 * @see javax.swing.plaf.metal.MetalLookAndFeel
 */
public class MetalLookAndFeel extends LookAndFeel {

    	public MetalLookAndFeel() {

            Font font = new Font();
            
            Style defaultStyle = new Style();
            defaultStyle.addFont(font, Style.ALL);
            defaultStyle.addBackground( getSecondary2() , Style.ALL);
            defaultStyle.addForeground( getSecondary1() , Style.ALL);
            setStyleFor("",defaultStyle);
            
            
            
            
            
            Style radioStyle = new Style(defaultStyle);
            radioStyle.addForeground(getPrimary2(), Style.FOCUSED);
            radioStyle.addForeground(0x00808080, Style.DISABLED);
            setStyleFor("RadioButton",radioStyle);
            setStyleFor("CheckBox",radioStyle);
            
            Style buttonStyle = new Style(radioStyle);
            buttonStyle.addBackground(0x00FFFFFF, Style.ALL);
            buttonStyle.addBorder(new LineBorder(0x00808080),Style.ALL);
            buttonStyle.addBorder(new LineBorder(0x00000000), Style.FOCUSED);
            setStyleFor("Button",buttonStyle);
            setStyleFor("TextField",buttonStyle);

            EmptyBorder padding = new EmptyBorder(0, font.getHeight(), 0, font.getHeight());
            Style spinnerStyle = new Style(radioStyle);
            spinnerStyle.addBorder(new CompoundBorder(padding, new LineBorder(0x00808080)),Style.ALL);
            spinnerStyle.addBorder(new CompoundBorder(padding, new LineBorder(0x00000000)), Style.FOCUSED);
            setStyleFor("Spinner",spinnerStyle);
            
            
            
            
            Style scrollStyle = new Style(defaultStyle);
            scrollStyle.addProperty(new Integer(0x00FFFFFF),"scrollBarCol",Style.ALL );
            scrollStyle.addProperty(new Integer(0x00000000),"scrollTrackCol",Style.ALL );
            setStyleFor("ScrollPane",scrollStyle);

            Style listStyle = new Style(defaultStyle);
            listStyle.addBackground(0x00FFFFFF, Style.ALL);
            setStyleFor("List",listStyle);
            setStyleFor("TextArea",listStyle);
            setStyleFor("SoftkeyRenderer",listStyle);
            
            Style labelStyle = new Style(defaultStyle);
            labelStyle.addBackground(-1, Style.ALL);
            setStyleFor("Label",labelStyle);
            
            Style tabTop = new Style(defaultStyle);
            tabTop.addBorder(new CompoundBorder( new MatteBorder(0, 0, 1, 0, 0x00FFFFFF), new EmptyBorder(0,0,-1,0)),Style.ALL);
            tabTop.addBackground(-1, Style.ALL);
            setStyleFor("TabTop",tabTop);

            Style tabLeft = new Style(defaultStyle);
            tabLeft.addBorder(new CompoundBorder( new MatteBorder(0, 0, 0, 1, 0x00FFFFFF), new EmptyBorder(0,0,0,-1)),Style.ALL);
            tabLeft.addBackground(-1, Style.ALL);
            setStyleFor("TabLeft",tabLeft);

            Style tabRight = new Style(defaultStyle);
            tabRight.addBorder(new CompoundBorder( new MatteBorder(0, 1, 0, 0, 0x00000000), new EmptyBorder(0,-1,0,0)),Style.ALL);
            tabRight.addBackground(-1, Style.ALL);
            setStyleFor("TabRight",tabRight);

            Style tabBottom = new Style(defaultStyle);
            tabBottom.addBorder(new CompoundBorder( new MatteBorder(1, 0, 0, 0, 0x00000000), new EmptyBorder(-1,0,0,0)),Style.ALL);
            tabBottom.addBackground(-1, Style.ALL);
            setStyleFor("TabBottom",tabBottom);

            
            
            
            Style tabContentTop = new Style(defaultStyle);
            tabContentTop.addBorder(new CompoundBorder( new MatteBorder(1, 1, 0, 0, 0x00FFFFFF), new MatteBorder(0, 0, 0, 1, 0x00000000)),Style.ALL);
            setStyleFor("TabContentTop",tabContentTop);

            Style tabContentLeft = new Style(defaultStyle);
            tabContentLeft.addBorder(new CompoundBorder( new MatteBorder(1, 1, 0, 0, 0x00FFFFFF), new MatteBorder(0, 0, 1, 0, 0x00000000)),Style.ALL);
            setStyleFor("TabContentLeft",tabContentLeft);

            Style tabContentRight = new Style(defaultStyle);
            tabContentRight.addBorder(new CompoundBorder( new MatteBorder(1, 0, 0, 0, 0x00FFFFFF), new MatteBorder(0, 0, 1, 1, 0x00000000)),Style.ALL);
            setStyleFor("TabContentRight",tabContentRight);

            Style tabContentBottom = new Style(defaultStyle);
            tabContentBottom.addBorder(new CompoundBorder( new MatteBorder(0, 1, 0, 0, 0x00FFFFFF), new MatteBorder(0, 0, 1, 1, 0x00000000)),Style.ALL);
            setStyleFor("TabContentBottom",tabContentBottom);
            
            
            
            
            TabBorder toptb = new TabBorder(Graphics.TOP);
            TabBorder bottomtb = new TabBorder(Graphics.BOTTOM);
            TabBorder lefttb = new TabBorder(Graphics.LEFT);
            TabBorder righttb = new TabBorder(Graphics.RIGHT);

            Style tabRendererTop = new Style(defaultStyle);
            tabRendererTop.addBorder(new CompoundBorder(
                        new EmptyBorder(1, 0, 1, 0),
                        new CompoundBorder(
                            toptb,
                            new EmptyBorder(0, 1, 0, 1))
                    ),Style.ALL);
            tabRendererTop.addBorder(new CompoundBorder(toptb, new LineBorder(getPrimary1(),-1, 1,false, Graphics.DOTTED)),Style.FOCUSED);
            tabRendererTop.addBorder(new CompoundBorder(toptb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererTop",tabRendererTop);

            Style tabRendererLeft = new Style(defaultStyle);
            tabRendererLeft.addBorder(new CompoundBorder(
                        new EmptyBorder(0, 1, 0, 1),
                        new CompoundBorder(
                            lefttb,
                            new EmptyBorder(1, 0, 1, 0))
                    ),Style.ALL);
            tabRendererLeft.addBorder(new CompoundBorder(lefttb, new LineBorder(getPrimary1(),-1, 1,false, Graphics.DOTTED)),Style.FOCUSED);
            tabRendererLeft.addBorder(new CompoundBorder(lefttb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererLeft",tabRendererLeft);

            Style tabRendererRight = new Style(defaultStyle);
            tabRendererRight.addBorder(new CompoundBorder(
                        new EmptyBorder(0, 1, 0, 1),
                        new CompoundBorder(
                            righttb,
                            new EmptyBorder(1, 0, 1, 0))
                    ),Style.ALL);
            tabRendererRight.addBorder(new CompoundBorder(righttb, new LineBorder(getPrimary1(),-1, 1,false, Graphics.DOTTED)),Style.FOCUSED);
            tabRendererRight.addBorder(new CompoundBorder(righttb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererRight",tabRendererRight);

            Style tabRendererBottom = new Style(defaultStyle);
            tabRendererBottom.addBorder(new CompoundBorder(
                        new EmptyBorder(1, 0, 1, 0),
                        new CompoundBorder(
                            bottomtb,
                            new EmptyBorder(0, 1, 0, 1))
                    ),Style.ALL);
            tabRendererBottom.addBorder(new CompoundBorder(bottomtb, new LineBorder(getPrimary1(),-1, 1,false, Graphics.DOTTED)),Style.FOCUSED);
            tabRendererBottom.addBorder(new CompoundBorder(bottomtb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererBottom",tabRendererBottom);
            
            
            
            Style titleBar = new Style(defaultStyle);
            titleBar.addBackground(getPrimary3(), Style.ALL);
            setStyleFor("TitleBar",titleBar);
            
            Style listCellRenderer = new Style(defaultStyle);
            listCellRenderer.addBorder(new EmptyBorder(1,1,1,1),Style.ALL);
            listCellRenderer.addBorder(new LineBorder( getPrimary1() ,-1,1,false,Graphics.DOTTED),Style.FOCUSED);
            listCellRenderer.addBackground( getSecondary3() , Style.ALL);
            listCellRenderer.addBackground( getPrimary3() , Style.SELECTED);
            setStyleFor("ListRenderer",listCellRenderer);
            
            Style windowSkin = new Style(defaultStyle);
            windowSkin.addBorder(new LineBorder( getSecondary3() ), Style.ALL);
            setStyleFor("Window",windowSkin);
            setStyleFor("Dialog",windowSkin);
            
	}
        
    // the color colors
    protected int getPrimary1() { return 0x00500A16; } // the dark color
    protected int getPrimary2() { return 0x00C10A2C; } // lighter
    protected int getPrimary3() { return 0x00F40A42; } // very light
        
    // the gray colors
    protected int getSecondary1() { return 0x00000000; } // dark
    protected int getSecondary2() { return 0x00EEEEEE; } // lighter
    protected int getSecondary3() { return 0x00FFFFFF; } // light
    
    protected int getBlack() { return 0x00000000; }
    protected int getWhite() { return 0x00FFFFFF; }
    
}

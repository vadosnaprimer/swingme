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

/**
 * @author Yura Mamyrin
 * @see javax.swing.plaf.metal.MetalLookAndFeel
 */
public class MetalLookAndFeel extends LookAndFeel {

    	public MetalLookAndFeel() {

            Font font = new Font();
            
            Style defaultStyle = new Style();
            defaultStyle.addFont(font, Style.ALL);
            defaultStyle.addBackground(0x00EEEEEE, Style.ALL);
            defaultStyle.addForeground(0x00000000, Style.ALL);
            setStyleFor("",defaultStyle);
            
            Style buttonStyle = new Style(defaultStyle);
            buttonStyle.addBackground(0x00FFFFFF, Style.ALL);
            buttonStyle.addForeground(0x00000000, Style.ALL);
            buttonStyle.addForeground(0x000000FF, Style.FOCUSED);
            buttonStyle.addForeground(0x00808080, Style.DISABLED);
            buttonStyle.addBorder(new LineBorder(0x00808080),Style.ALL);
            buttonStyle.addBorder(new LineBorder(0x00000000), Style.FOCUSED);
            setStyleFor("Button",buttonStyle);
            setStyleFor("TextField",buttonStyle);

            Style radioStyle = new Style(defaultStyle);
            radioStyle.addForeground(0x00000000, Style.ALL);
            radioStyle.addForeground(0x000000FF, Style.FOCUSED);
            radioStyle.addForeground(0x00808080, Style.DISABLED);
            setStyleFor("RadioButton",radioStyle);
            setStyleFor("CheckBox",radioStyle);
            
            Style scrollStyle = new Style(defaultStyle);
            scrollStyle.addProperty(new Integer(0x00FFFFFF),"scrollBarCol",Style.ALL );
            scrollStyle.addProperty(new Integer(0x00000000),"scrollTrackCol",Style.ALL );
            setStyleFor("ScrollPane",scrollStyle);

            
            EmptyBorder padding = new EmptyBorder(0, font.getHeight(), 0, font.getHeight());
            Style spinnerStyle = new Style(defaultStyle);
            spinnerStyle.addBorder(new CompoundBorder(padding, new LineBorder(0x00808080)),Style.ALL);
            spinnerStyle.addBorder(new CompoundBorder(padding, new LineBorder(0x00000000)), Style.FOCUSED);
            setStyleFor("Spinner",spinnerStyle);
            
            Style listStyle = new Style(defaultStyle);
            listStyle.addBackground(0x00FFFFFF, Style.ALL);
            setStyleFor("List",listStyle);
            setStyleFor("TextArea",listStyle);
            setStyleFor("SoftkeyRenderer",listStyle);
            
            Style labelStyle = new Style(defaultStyle);
            labelStyle.addBackground(-1, Style.ALL);
            setStyleFor("Label",labelStyle);
            
            Style tabTop = new Style(defaultStyle);
            tabTop.addBorder(new CompoundBorder( new MatteBorder(0, 0, 1, 0, 0x00000000), new EmptyBorder(0,0,-1,0)),Style.ALL);
            tabTop.addBackground(-1, Style.ALL);
            setStyleFor("TabTop",tabTop);

            Style tabLeft = new Style(defaultStyle);
            tabLeft.addBorder(new CompoundBorder( new MatteBorder(0, 0, 0, 1, 0x00000000), new EmptyBorder(0,0,0,-1)),Style.ALL);
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

            Style titleBar = new Style(defaultStyle);
            titleBar.addBackground(0x00AAAAFF, Style.ALL);
            setStyleFor("TitleBar",titleBar);
            
            Style listCellRenderer = new Style(defaultStyle);
            listCellRenderer.addBorder(new EmptyBorder(1,1,1,1),Style.ALL);
            listCellRenderer.addBorder(new LineBorder(0x00000000,-1,1,false,Graphics.DOTTED),Style.FOCUSED);
            listCellRenderer.addBackground(0x00FFFFFF, Style.ALL);
            listCellRenderer.addBackground(0x00AAAAFF, Style.SELECTED);
            setStyleFor("ListCellRenderer",listCellRenderer);
            
	}
    
}

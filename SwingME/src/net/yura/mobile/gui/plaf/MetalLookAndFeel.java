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

import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.border.LineBorder;

/**
 * @author Yura Mamyrin
 * @see javax.swing.plaf.metal.MetalLookAndFeel
 */
public class MetalLookAndFeel extends LookAndFeel {

    	public MetalLookAndFeel() {

            Style defaultStyle = new Style();
            defaultStyle.addFont(new Font(), Style.ALL);
            defaultStyle.addBackground(0x00FFFFFF, Style.ALL);
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

            System.out.println(buttonStyle.getFont(Style.ALL));
            
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

	}
    
}

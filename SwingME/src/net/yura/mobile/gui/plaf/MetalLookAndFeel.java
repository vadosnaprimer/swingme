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

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.BevelBorder;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.border.TabBorder;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.plaf.metal.MetalLookAndFeel
 */
public class MetalLookAndFeel extends LookAndFeel {

    public  MetalLookAndFeel() {
        this(FONT_MEDIUM);
    }

    	public MetalLookAndFeel(int size) {

            Font font = new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_PLAIN, size);

            int iconSize = font.getHeight()-1;

            MetalIcon radioIcon = new MetalIcon(iconSize,LookAndFeel.ICON_RADIO,Style.NO_COLOR,getSecondary3());
            MetalIcon checkboxIcon = new MetalIcon(iconSize,LookAndFeel.ICON_CHECKBOX,Style.NO_COLOR,getSecondary3());
            MetalIcon comboIcon = new MetalIcon(iconSize,LookAndFeel.ICON_COMBO,Style.NO_COLOR,getSecondary3());
            MetalIcon spinnerLeftIcon = new MetalIcon(iconSize, LookAndFeel.ICON_SPINNER_LEFT,getBlack(),getSecondary3());
            MetalIcon spinnerRightIcon = new MetalIcon(iconSize, LookAndFeel.ICON_SPINNER_RIGHT,getBlack(),getSecondary3());
            MetalIcon spinnerLeftIconSelected = new MetalIcon(iconSize, LookAndFeel.ICON_SPINNER_LEFT,getPrimary1(),getSecondary3());
            MetalIcon spinnerRightIconSelected = new MetalIcon(iconSize, LookAndFeel.ICON_SPINNER_RIGHT,getPrimary1(),getSecondary3());

            Style defaultStyle = new Style();
            defaultStyle.addFont(font, Style.ALL);
            defaultStyle.addBackground( getSecondary3() , Style.ALL);
            defaultStyle.addForeground( getBlack() , Style.ALL);
            setStyleFor("",defaultStyle);            


            Style listCellRenderer = new Style(defaultStyle);
            listCellRenderer.addBorder(new EmptyBorder(1,1,1,1),Style.ALL);
            Border darkline = new LineBorder( getPrimary2() ,Style.NO_COLOR,1,false,Graphics.DOTTED);
            listCellRenderer.addBorder(darkline,Style.FOCUSED | Style.SELECTED);
            listCellRenderer.addBorder(darkline,Style.FOCUSED);
            listCellRenderer.addBorder(new LineBorder( getPrimary3() ),Style.SELECTED);
            listCellRenderer.addBackground( getWhite() , Style.ALL);
            listCellRenderer.addBackground( getPrimary3() , Style.SELECTED);
            setStyleFor("ListRenderer",listCellRenderer);

            Style progressBar = new Style(defaultStyle);
            progressBar.addForeground( getPrimary2() , Style.ALL);
            setStyleFor("ProgressBar",progressBar);

            Style abstractButtonStyle = new Style(defaultStyle);
            abstractButtonStyle.addForeground( getBlack() , Style.FOCUSED);
            abstractButtonStyle.addForeground( getSecondary2() , Style.DISABLED);
            abstractButtonStyle.addForeground( getPrimary1() , Style.FOCUSED);
            
            Style radioStyle = new Style(abstractButtonStyle);
            radioStyle.addProperty(radioIcon, "icon", Style.ALL);
            setStyleFor("RadioButton",radioStyle);

            Style checkboxStyle = new Style(abstractButtonStyle);
            checkboxStyle.addProperty(checkboxIcon, "icon", Style.ALL);
            setStyleFor("CheckBox",checkboxStyle);

            Style checkboxRendererStyle = new Style(checkboxStyle);
            checkboxRendererStyle.putAll(listCellRenderer);
            addMetalIcon(checkboxRendererStyle,"/image.gif" , "imageIcon");
            addMetalIcon(checkboxRendererStyle,"/directory.gif" , "folderIcon");
            addMetalIcon(checkboxRendererStyle,"/sound.gif" , "soundIcon");
            addMetalIcon(checkboxRendererStyle,"/movie.gif" , "videoIcon");
            addMetalIcon(checkboxRendererStyle,"/unknown.gif" , "unknownIcon");
            setStyleFor("CheckBoxRenderer",checkboxRendererStyle);

            Style buttonStyle = new Style(abstractButtonStyle);
            buttonStyle.addBackground( getSecondary3() , Style.ALL);
            buttonStyle.addBorder(new BevelBorder( 1, getWhite(), getSecondary1() ),Style.ALL);
            buttonStyle.addBorder(new BevelBorder( 1, getSecondary1(), getWhite() ), Style.SELECTED);
            buttonStyle.addBorder(new LineBorder( getSecondary2() ), Style.DISABLED);
            setStyleFor("Button",buttonStyle);

            Style menuItemStyle = new Style(defaultStyle);
            menuItemStyle.addProperty(spinnerRightIcon, "icon", Style.ALL);
            menuItemStyle.addBackground( getPrimary2() , Style.SELECTED );
            //menuItemStyle.addForeground( getWhite() , Style.SELECTED );
            setStyleFor("MenuRenderer",menuItemStyle);
            setStyleFor("MenuItem",menuItemStyle); // for the arrow to work

            Border inputBorder = new BevelBorder( 1, getSecondary1(), getWhite() );

            Style inputStyle = new Style(defaultStyle);
            inputStyle.addBackground( getWhite() , Style.ALL);
            setStyleFor("List",inputStyle);
            setStyleFor("TextArea",inputStyle);

            Style comboStyle = new Style(buttonStyle);
            comboStyle.addProperty(comboIcon, "icon", Style.ALL);
            setStyleFor("ComboBox",comboStyle);

            Style textStyle = new Style(inputStyle);
            textStyle.addBorder(inputBorder, Style.ALL);
            textStyle.addBackground(getSecondary3(), Style.DISABLED);
            setStyleFor("TextField",textStyle);


            EmptyBorder padding = new EmptyBorder(0, font.getHeight(), 0, font.getHeight());
            Style spinnerStyle = new Style(radioStyle);
            spinnerStyle.addBorder(new CompoundBorder(padding, inputBorder ),Style.ALL);
            spinnerStyle.addProperty(spinnerLeftIcon, "iconLeft", Style.ALL);
            spinnerStyle.addProperty(spinnerRightIcon, "iconRight", Style.ALL);
            spinnerStyle.addProperty(spinnerLeftIconSelected, "iconLeft", Style.SELECTED);
            spinnerStyle.addProperty(spinnerRightIconSelected, "iconRight", Style.SELECTED);
            setStyleFor("Spinner",spinnerStyle);
            
            
            
            
            Style scrollStyle = new Style(defaultStyle);
            //scrollStyle.addBackground(, Style.ALL);
            //scrollStyle.addBorder(inputBorder,Style.ALL);
            //scrollStyle.addProperty(new Integer( getPrimary1() ),"thumbFill",Style.ALL );
            //scrollStyle.addProperty(new Integer( getSecondary3() ),"trackFill",Style.ALL );

            scrollStyle.addProperty(new MetalIcon(iconSize, ICON_THUMB_TOP,getBlack(),getSecondary3()),"thumbTop",Style.ALL );
            scrollStyle.addProperty(new MetalIcon(iconSize, ICON_THUMB_FILL,getBlack(),getSecondary3()),"thumbFill",Style.ALL );
            scrollStyle.addProperty(new MetalIcon(iconSize, ICON_THUMB_BOTTOM,getBlack(),getSecondary3()),"thumbBottom",Style.ALL );

            scrollStyle.addProperty(new MetalIcon(iconSize, ICON_TRACK_TOP,getBlack(),getSecondary3()),"trackTop",Style.ALL );
            scrollStyle.addProperty(new MetalIcon(iconSize, ICON_TRACK_FILL,getBlack(),getSecondary3()),"trackFill",Style.ALL );
            scrollStyle.addProperty(new MetalIcon(iconSize, ICON_TRACK_BOTTOM,getBlack(),getSecondary3()),"trackBottom",Style.ALL );


            scrollStyle.addProperty(spinnerLeftIcon,"leftArrow",Style.ALL );
            scrollStyle.addProperty(spinnerRightIcon,"rightArrow",Style.ALL );
            setStyleFor("ScrollPane",scrollStyle);

            Style labelStyle = new Style(defaultStyle);
            labelStyle.addBackground(Style.NO_COLOR, Style.ALL);
            setStyleFor("Label",labelStyle);
            setStyleFor("TitleBarLabel",labelStyle);
            
            Style tabTop = new Style(defaultStyle);
            tabTop.addBorder(new CompoundBorder( new MatteBorder(0, 0, 1, 0, getWhite() ), new EmptyBorder(0,0,-1,0)),Style.ALL);
            tabTop.addBackground(Style.NO_COLOR, Style.ALL);
            setStyleFor("TabTop",tabTop);

            Style tabLeft = new Style(defaultStyle);
            tabLeft.addBorder(new CompoundBorder( new MatteBorder(0, 0, 0, 1, getWhite() ), new EmptyBorder(0,0,0,-1)),Style.ALL);
            tabLeft.addBackground(Style.NO_COLOR, Style.ALL);
            setStyleFor("TabLeft",tabLeft);

            Style tabRight = new Style(defaultStyle);
            tabRight.addBorder(new CompoundBorder( new MatteBorder(0, 1, 0, 0, getSecondary1() ), new EmptyBorder(0,-1,0,0)),Style.ALL);
            tabRight.addBackground(Style.NO_COLOR, Style.ALL);
            setStyleFor("TabRight",tabRight);

            Style tabBottom = new Style(defaultStyle);
            tabBottom.addBorder(new CompoundBorder( new MatteBorder(1, 0, 0, 0, getSecondary1() ), new EmptyBorder(-1,0,0,0)),Style.ALL);
            tabBottom.addBackground(Style.NO_COLOR, Style.ALL);
            setStyleFor("TabBottom",tabBottom);

            
            
            
            Style tabContentTop = new Style(defaultStyle);
            tabContentTop.addBorder(new CompoundBorder( new MatteBorder(1, 1, 0, 0, getWhite()), new MatteBorder(0, 0, 0, 1, getSecondary1())),Style.ALL);
            setStyleFor("TabContentTop",tabContentTop);

            Style tabContentLeft = new Style(defaultStyle);
            tabContentLeft.addBorder(new CompoundBorder( new MatteBorder(1, 1, 0, 0, getWhite()), new MatteBorder(0, 0, 1, 0, getSecondary1())),Style.ALL);
            setStyleFor("TabContentLeft",tabContentLeft);

            Style tabContentRight = new Style(defaultStyle);
            tabContentRight.addBorder(new CompoundBorder( new MatteBorder(1, 0, 0, 0, getWhite()), new MatteBorder(0, 0, 1, 1, getSecondary1())),Style.ALL);
            setStyleFor("TabContentRight",tabContentRight);

            Style tabContentBottom = new Style(defaultStyle);
            tabContentBottom.addBorder(new CompoundBorder( new MatteBorder(0, 1, 0, 0, getWhite()), new MatteBorder(0, 0, 1, 1, getSecondary1())),Style.ALL);
            setStyleFor("TabContentBottom",tabContentBottom);
            
            
            
            
            TabBorder toptb = new TabBorder(Graphics.TOP,getWhite(),getSecondary3(),getSecondary2(),getSecondary1());
            TabBorder bottomtb = new TabBorder(Graphics.BOTTOM,getWhite(),getSecondary3(),getSecondary2(),getSecondary1());
            TabBorder lefttb = new TabBorder(Graphics.LEFT,getWhite(),getSecondary3(),getSecondary2(),getSecondary1());
            TabBorder righttb = new TabBorder(Graphics.RIGHT,getWhite(),getSecondary3(),getSecondary2(),getSecondary1());

            Style tabRendererTop = new Style(defaultStyle);
            tabRendererTop.addBorder(new CompoundBorder(
                        new EmptyBorder(1, 0, 1, 0),
                        new CompoundBorder(
                            toptb,
                            new EmptyBorder(0, 1, 0, 1))
                    ),Style.ALL);
            tabRendererTop.addBorder(new CompoundBorder(toptb, new LineBorder(getPrimary1(),Style.NO_COLOR, 1,false, Graphics.DOTTED)),Style.SELECTED|Style.FOCUSED);
            tabRendererTop.addBorder(new CompoundBorder(toptb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererTop",tabRendererTop);

            Style tabRendererLeft = new Style(defaultStyle);
            tabRendererLeft.addBorder(new CompoundBorder(
                        new EmptyBorder(0, 1, 0, 1),
                        new CompoundBorder(
                            lefttb,
                            new EmptyBorder(1, 0, 1, 0))
                    ),Style.ALL);
            tabRendererLeft.addBorder(new CompoundBorder(lefttb, new LineBorder(getPrimary1(),Style.NO_COLOR, 1,false, Graphics.DOTTED)),Style.SELECTED|Style.FOCUSED);
            tabRendererLeft.addBorder(new CompoundBorder(lefttb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererLeft",tabRendererLeft);

            Style tabRendererRight = new Style(defaultStyle);
            tabRendererRight.addBorder(new CompoundBorder(
                        new EmptyBorder(0, 1, 0, 1),
                        new CompoundBorder(
                            righttb,
                            new EmptyBorder(1, 0, 1, 0))
                    ),Style.ALL);
            tabRendererRight.addBorder(new CompoundBorder(righttb, new LineBorder(getPrimary1(),Style.NO_COLOR, 1,false, Graphics.DOTTED)),Style.SELECTED|Style.FOCUSED);
            tabRendererRight.addBorder(new CompoundBorder(righttb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererRight",tabRendererRight);

            Style tabRendererBottom = new Style(defaultStyle);
            tabRendererBottom.addBorder(new CompoundBorder(
                        new EmptyBorder(1, 0, 1, 0),
                        new CompoundBorder(
                            bottomtb,
                            new EmptyBorder(0, 1, 0, 1))
                    ),Style.ALL);
            tabRendererBottom.addBorder(new CompoundBorder(bottomtb, new LineBorder(getPrimary1(),Style.NO_COLOR, 1,false, Graphics.DOTTED)),Style.SELECTED|Style.FOCUSED);
            tabRendererBottom.addBorder(new CompoundBorder(bottomtb, new EmptyBorder(1, 1, 1, 1)),Style.SELECTED);
            setStyleFor("TabRendererBottom",tabRendererBottom);
            
            
            
            Style titleBar = new Style(defaultStyle);
            titleBar.addBackground(getPrimary3(), Style.ALL);
            setStyleFor("TitleBar",titleBar);

            Style menuSkin = new Style(defaultStyle);
            menuSkin.addBorder(new LineBorder( getPrimary1() ), Style.ALL);
            setStyleFor("Menu",menuSkin);

            Style windowSkin = new Style(defaultStyle);
            // TODO: windowSkin.addBorder(new LineBorder( getSecondary1(), 2 ), Style.ALL);
            windowSkin.addBorder(new LineBorder( getPrimary1(), 2 ), Style.ALL);
            setStyleFor("Frame",windowSkin);

            addMetalIcon(windowSkin,"/metal-warning.png" , "WARNING_MESSAGE");
            addMetalIcon(windowSkin,"/metal-question.png" , "QUESTION_MESSAGE");
            addMetalIcon(windowSkin,"/metal-error.png" , "ERROR_MESSAGE");
            addMetalIcon(windowSkin,"/metal-info.png" , "INFORMATION_MESSAGE");

            setStyleFor("Dialog",windowSkin);

            Style tooltipSkin = new Style(defaultStyle);
            tooltipSkin.addBackground(getPrimary3(), Style.ALL);
            tooltipSkin.addBorder(new LineBorder( getPrimary1() ),Style.ALL);
            setStyleFor("ToolTip",tooltipSkin);

            Style separator = new Style(defaultStyle);
            separator.addBackground( getSecondary1() , Style.ALL);
            setStyleFor("Separator",separator);

	}

        public void addMetalIcon(Style style, String icon,String prop) {
            try {
                Icon icn = new Icon(icon);
                style.addProperty(icn, prop, Style.ALL);
            }
            catch (IOException ex) {
                //#debug info
                Logger.info("can not find icon for metal theme "+icon);
            }
        }

    // the color colors
    protected int getPrimary1() { return 0xFF666699; } // the dark color
    protected int getPrimary2() { return 0xFF9999CC; } // lighter
    protected int getPrimary3() { return 0xFFCCCCFF; } // very light

    // the gray colors
    protected int getSecondary1() { return 0xFF666666; } // dark
    protected int getSecondary2() { return 0xFF999999; } // lighter
    protected int getSecondary3() { return 0xFFCCCCCC; } // light

    protected int getBlack() { return 0xFF000000; }
    protected int getWhite() { return 0xFFFFFFFF; }

}

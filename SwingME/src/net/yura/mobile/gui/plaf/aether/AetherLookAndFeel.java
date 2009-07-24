package net.yura.mobile.gui.plaf.aether;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Nathan
 */
public class AetherLookAndFeel extends LookAndFeel {

    public AetherLookAndFeel() {
        this("");
    }

    public AetherLookAndFeel(String theme) {

        Font font = new Font(javax.microedition.lcdui.Font.getFont(javax.microedition.lcdui.Font.FACE_SYSTEM, javax.microedition.lcdui.Font.STYLE_PLAIN, javax.microedition.lcdui.Font.SIZE_MEDIUM));

        /*
         * Color Scheme's
         */
        int background;
        int dark;
        int light;
        int highlight;
        int base;
        int baseHighlight;
        int corner;
        int text = 0x00000;
        int disabledText = 0x00777777;
        int tabHighlight;

        Vector defaultBorderSettings = new Vector();
        Vector focusedBorderSettings = new Vector();
        Vector selectedBorderSettings = new Vector();
        Vector disabledBorderSettings = new Vector();
        Vector textareaBorderSettings = new Vector();
        Vector textareaSelectedSettings = new Vector();
        Vector textareaDisabledSettings = new Vector();
        Vector tabFocusedSettings = new Vector();
        Vector tabSelectedSettings = new Vector();
        Vector tabBottomFocusedSettings = new Vector();
        Vector titleSettings = new Vector();
        Vector listItemSelectedSettings = new Vector();
        Vector highlightAreaSettings = new Vector();

        Icon checkboxIcon = new Icon();
        Icon checkboxSelectedIcon = new Icon();
        Icon radioIcon = new Icon();
        Icon radioFocusedIcon = new Icon();
        Icon comboBoxIcon = new Icon();
        Icon comboBoxSelectedIcon = new Icon();
        Icon spinnerLeftIcon = new Icon();
        Icon spinnerLeftSelectedIcon = new Icon();
        Icon spinnerRightIcon = new Icon();
        Icon spinnerRightSelectedIcon = new Icon();

        if (theme.equals("net")) {
            // office 2007
            background  = 0x00BBD7FC;

            highlight = 0x006593CF;
            dark = 0x00AFD2FF;
            light = 0x00E3EFFF;

            int highlightFocused = 0x00FEC579;
            int darkFocused = 0x00FFDD79;
            int lightFocused = 0x00FFF6CB;

            int hightlightSelected = 0x00FA954C;
            tabHighlight = hightlightSelected;
            base = 0x00FFFFFF;
            baseHighlight = 0x00F0F000;
            corner = 0;

            int mid = AetherBorder.getGradientColor(light, dark, 5, 10, 1);
            int disabledBorderColor = AetherBorder.getGradientColor(dark, background, 6, 10, 1);

            defaultBorderSettings.addElement(new AetherBorderSetting(highlight, highlight, 1, corner, 1));
            defaultBorderSettings.addElement(new AetherBorderSetting(light, dark, 1, corner, 1));
            
            focusedBorderSettings.addElement(new AetherBorderSetting(highlightFocused, highlightFocused, 1, corner, 1));
            focusedBorderSettings.addElement(new AetherBorderSetting(lightFocused, darkFocused, 1, corner, 1));
            
            selectedBorderSettings.addElement(new AetherBorderSetting(hightlightSelected, hightlightSelected, 1, corner, 1));
            selectedBorderSettings.addElement(new AetherBorderSetting(hightlightSelected, darkFocused, 1, corner, 1));
            
            copyBorders(defaultBorderSettings, disabledBorderSettings);
            disabledBorderSettings.setElementAt(new AetherBorderSetting(disabledBorderColor, disabledBorderColor, 1, corner, 1), 0);
            
            copyBorders(defaultBorderSettings, textareaBorderSettings);
            textareaBorderSettings.setElementAt(new AetherBorderSetting(base, base, 1, corner, 1), 1);
           
            copyBorders(textareaBorderSettings, textareaSelectedSettings);
            textareaSelectedSettings.setElementAt(new AetherBorderSetting(highlightFocused, highlightFocused, 1, corner, 1), 0);

            copyBorders(disabledBorderSettings, textareaDisabledSettings);
            textareaDisabledSettings.setElementAt(new AetherBorderSetting(base, base, 1, corner, 1), 1);

            tabFocusedSettings.addElement(new AetherBorderSetting(tabHighlight, tabHighlight, 1, corner, 1));
            tabFocusedSettings.addElement(new AetherBorderSetting(light, background, 1, corner, 1));
            
            copyBorders(tabFocusedSettings, tabSelectedSettings);
            tabSelectedSettings.setElementAt(new AetherBorderSetting(background, background, 1, corner, 1), 1);
            
            copyBorders(tabFocusedSettings, tabBottomFocusedSettings);
            tabBottomFocusedSettings.setElementAt(new AetherBorderSetting(background, light, 1, corner, 1), 1);

//            highlightAreaSettings.addElement(new AetherBorderSetting(highlightFocused, highlightFocused, 1, 0, 1));
//            highlightAreaSettings.addElement(new AetherBorderSetting(0x00FFFFFF, lightFocused, 1, 0, 1));
            copyBorders(focusedBorderSettings, highlightAreaSettings);
            
            listItemSelectedSettings.addElement(new AetherBorderSetting(highlight, highlight, 1, 0, 1));
            listItemSelectedSettings.addElement(new AetherBorderSetting(light, light, 1, 0, 1));

            titleSettings.addElement(new AetherBorderSetting(0x00FFFFFF, mid, 1, 0, 1));

            //Vector checkboxSelectedSettings = new Vector();
            //copyBorders(textareaBorderSettings,checkboxSelectedSettings);
            //checkboxSelectedSettings.addElement(new AetherBorderSetting(highlight, highlight, 1, 0, 1));

            Vector comboBoxIconSettings = new Vector();
            copyBorders(defaultBorderSettings, comboBoxIconSettings);
            comboBoxIconSettings.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1),0);
            comboBoxIconSettings.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1),0);

            Vector comboBoxSelectedIconSettings = new Vector();
            copyBorders(focusedBorderSettings, comboBoxSelectedIconSettings);
            comboBoxSelectedIconSettings.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1),0);
            comboBoxSelectedIconSettings.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1),0);

            checkboxIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, textareaBorderSettings, highlight);
            //checkboxFocusedIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, textareaSelectedSettings, highlight);
            checkboxSelectedIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, textareaBorderSettings, highlight);
            radioIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_RADIO, textareaBorderSettings, highlight);
            radioFocusedIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_RADIO, textareaBorderSettings, highlight);
            comboBoxIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_COMBO, comboBoxIconSettings, highlight);
            comboBoxSelectedIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_COMBO, comboBoxSelectedIconSettings, hightlightSelected);
            spinnerLeftIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_LEFT, comboBoxIconSettings, highlight);
            spinnerLeftSelectedIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_LEFT, comboBoxSelectedIconSettings, hightlightSelected);
            spinnerRightIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_RIGHT, comboBoxIconSettings, highlight);
            spinnerRightSelectedIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_RIGHT, comboBoxSelectedIconSettings, hightlightSelected);
        }
        else {
            // yura.net
            background  = 0x00E0E3EC;
            highlight = 0x00576C7D;
            dark = AetherBorder.getShade(0x00828282,1.1);
            light = 0x00F0F0EE;
            base = 0x00EEF2FB;
            baseHighlight = 0x00FFFFBB;
            corner = 4;

            int mid = AetherBorder.getGradientColor(light, dark, 5, 10, 1);
            int reallyDark = AetherBorder.getShade(light,0.5);
            int buttonSelectedLight = AetherBorder.getShade(light, 1.1);
            int buttonSelectedDark = AetherBorder.getGradientColor(light, dark, 6, 10, 1);
            int disabledBorderColor = AetherBorder.getGradientColor(dark, background, 6, 10, 1);
            int baseReallyHighlight = AetherBorder.getShade(baseHighlight, 1.1);

            defaultBorderSettings.addElement(new AetherBorderSetting(reallyDark, reallyDark, 1, corner, 1));
            defaultBorderSettings.addElement(new AetherBorderSetting(light, light, 1, corner, 1));
            defaultBorderSettings.addElement(new AetherBorderSetting(light, dark, 1, corner, 1));

            focusedBorderSettings.addElement(new AetherBorderSetting(highlight, highlight, 1, corner, 1));
            //focusedBorderSettings.addElement(new AetherBorderSetting(AetherBorder.getShade(highlight,1.2), AetherBorder.getShade(highlight,1.2), 1, corner, 1));
            focusedBorderSettings.addElement(new AetherBorderSetting(baseHighlight, baseHighlight, 1, corner, 1));
            focusedBorderSettings.addElement(new AetherBorderSetting(buttonSelectedLight, buttonSelectedDark, 1, corner, 1));

            copyBorders(focusedBorderSettings, selectedBorderSettings);
            selectedBorderSettings.setElementAt(new AetherBorderSetting(AetherBorder.getShade(buttonSelectedLight, 0.9), AetherBorder.getShade(buttonSelectedDark, 0.9), 1, corner, 1), 2);

            copyBorders(defaultBorderSettings, disabledBorderSettings);
            disabledBorderSettings.setElementAt(new AetherBorderSetting(disabledBorderColor, disabledBorderColor, 1, corner, 1), 0);
            disabledBorderSettings.setElementAt(new AetherBorderSetting(buttonSelectedLight, buttonSelectedDark, 1, corner, 1),2);

            copyBorders(defaultBorderSettings, textareaBorderSettings);
            textareaBorderSettings.setElementAt(new AetherBorderSetting(base, base, 1, corner, 1), 1);
            textareaBorderSettings.removeElementAt(2);

            copyBorders(focusedBorderSettings, textareaSelectedSettings);
            textareaSelectedSettings.setElementAt(new AetherBorderSetting(baseReallyHighlight, baseHighlight, 1, corner, 1), 1);
            textareaSelectedSettings.removeElementAt(2);

            copyBorders(disabledBorderSettings, textareaDisabledSettings);
            textareaDisabledSettings.setElementAt(new AetherBorderSetting(base, base, 1, corner, 1), 1);
            textareaDisabledSettings.removeElementAt(2);

            Vector checkboxSettings = new Vector();
            checkboxSettings.addElement(new AetherBorderSetting(reallyDark, reallyDark, 1, 0, 1));
            checkboxSettings.addElement(new AetherBorderSetting(base, base, 1, 0, 1));

            Vector checkboxSelectedSettings = new Vector();
            copyBorders(checkboxSettings, checkboxSelectedSettings);
            checkboxSelectedSettings.setElementAt(new AetherBorderSetting(highlight, highlight, 1, 0, 1), 0);

            copyBorders(focusedBorderSettings, textareaSelectedSettings);
            textareaSelectedSettings.setElementAt(new AetherBorderSetting(baseReallyHighlight, baseHighlight, 1, corner, 1), 1);
            textareaSelectedSettings.removeElementAt(2);

            copyBorders(selectedBorderSettings, tabFocusedSettings);
            tabFocusedSettings.setElementAt(new AetherBorderSetting(light, background, 1, corner, 1), 2);

            copyBorders(selectedBorderSettings, tabSelectedSettings);
            tabSelectedSettings.setElementAt(new AetherBorderSetting(background, background, 1, corner, 1), 2);

            copyBorders(selectedBorderSettings, tabBottomFocusedSettings);
            tabBottomFocusedSettings.setElementAt(new AetherBorderSetting(background, light, 1, corner, 1), 2);

            tabHighlight = highlight;

            highlightAreaSettings.addElement(new AetherBorderSetting(highlight, highlight, 1, 0, 1));
            highlightAreaSettings.addElement(new AetherBorderSetting(0x00FFFFFF, baseHighlight, 1, 0, 1));

            listItemSelectedSettings.addElement(new AetherBorderSetting(highlight, highlight, 1, 0, 1));
            listItemSelectedSettings.addElement(new AetherBorderSetting(light, light, 1, 0, 1));

            titleSettings.addElement(new AetherBorderSetting(0x00FFFFFF, mid, 1, 0, 1));

            Vector comboIconSetting = new Vector();
            copyBorders(defaultBorderSettings, comboIconSetting);
            comboIconSetting.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1), 0);
            comboIconSetting.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1), 0);

            Vector comboSelectedIconSetting = new Vector();
            copyBorders(selectedBorderSettings, comboSelectedIconSetting);
            comboSelectedIconSetting.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1), 0);
            comboSelectedIconSetting.insertElementAt(new AetherBorderSetting(-1, -1, 1, corner, 1), 0);

            checkboxIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, checkboxSettings, dark);
            checkboxSelectedIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, checkboxSelectedSettings, highlight);
            radioIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_RADIO, textareaBorderSettings, dark);
            radioFocusedIcon = new AetherIcon(font.getHeight(), LookAndFeel.ICON_RADIO, textareaSelectedSettings, highlight);
            comboBoxIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_COMBO, comboIconSetting, text);
            comboBoxSelectedIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_COMBO, comboSelectedIconSetting, text);
            spinnerLeftIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_LEFT, comboIconSetting, text);
            spinnerLeftSelectedIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_LEFT, comboSelectedIconSetting, text);
            spinnerRightIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_RIGHT, comboIconSetting, text);
            spinnerRightSelectedIcon = new AetherIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_RIGHT, comboSelectedIconSetting, text);
        }

        /*
         * Borders setup
         */

        Border windowBackground = new CompoundBorder(new LineBorder(dark), new LineBorder(highlight));

        // TODO, FIX ME this is bad, as adds extra class
        Border titleBackground = new AetherBorder(titleSettings, 0, 0, 6, 0, 0, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT) {
            public void paintBorder(Component c, Graphics2D g, int width, int height) {
                super.paintBorder(c, g, width, height);
                AetherBorderSetting border = (AetherBorderSetting) borders.elementAt(0);
                g.setColor(AetherBorder.getShade(border.color2,0.8));
                g.drawLine(-getLeft(), height-1, width+getRight(), height-1);
            }
        };


        Border buttonDefault  = new AetherBorder(defaultBorderSettings, 0, 0, 0, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);
        Border buttonDisabled  = new AetherBorder(disabledBorderSettings, 0, 0, 0, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);
        Border buttonFocused  = new AetherBorder(focusedBorderSettings, 0, 0, 0, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);
        Border buttonSelected  = new AetherBorder(selectedBorderSettings, 0, 0, 0, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);

        Border textboxBorder = new AetherBorder(textareaBorderSettings, 0, 0, 0, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);
        Border textboxFocused = new AetherBorder(textareaSelectedSettings, 0, 0, 0, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);
        Border textboxDisabled = new AetherBorder(textareaDisabledSettings, 0, 0, 0, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);

        Border spinnerBorder = new AetherBorder(textareaBorderSettings, 0, 0, 20, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);
        Border spinnerFocused = new AetherBorder(textareaSelectedSettings, 0, 0, 20, 0, corner, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);

        Border listItemFocused = new AetherBorder(highlightAreaSettings, 0, 0, 0, 0, 0, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);
        Border listItemSelected = new AetherBorder(listItemSelectedSettings, 0, 0, 0, 0, 0, AetherBorder.CLIP_NONE, AetherBorder.ORIENTATION_VERT);

        Border tabTop  = new AetherBorder(defaultBorderSettings, 1, 2, 2, 0, corner, AetherBorder.CLIP_BOTTOM, AetherBorder.ORIENTATION_VERT);
        Border tabTopSelected  = new AetherBorder(tabSelectedSettings, 1, 0, 2, 0, corner, AetherBorder.CLIP_BOTTOM, AetherBorder.ORIENTATION_VERT);
        Border tabTopFocused  = new AetherBorder(tabFocusedSettings, 1, 0, 2, 0, corner, AetherBorder.CLIP_BOTTOM, AetherBorder.ORIENTATION_VERT);

        Border tabLeft  = new AetherBorder(defaultBorderSettings, 2, 1, 2, 0, corner, AetherBorder.CLIP_RIGHT, AetherBorder.ORIENTATION_HORI);
        Border tabLeftSelected  = new AetherBorder(tabSelectedSettings, 0, 1, 2, 0, corner, AetherBorder.CLIP_RIGHT, AetherBorder.ORIENTATION_HORI);
        Border tabLeftFocused  = new AetherBorder(tabFocusedSettings, 0, 1, 2, 0, corner, AetherBorder.CLIP_RIGHT, AetherBorder.ORIENTATION_HORI);

        Border tabBottom  = new AetherBorder(defaultBorderSettings, 1, 2, 2, 0, corner, AetherBorder.CLIP_TOP, AetherBorder.ORIENTATION_VERT);
        Border tabBottomSelected  = new AetherBorder(tabSelectedSettings, 1, 0, 2, 0, corner, AetherBorder.CLIP_TOP, AetherBorder.ORIENTATION_VERT);
        Border tabBottomFocused  = new AetherBorder(tabBottomFocusedSettings, 1, 0, 2, 0, corner, AetherBorder.CLIP_TOP, AetherBorder.ORIENTATION_VERT);

        Border tabRight  = new AetherBorder(defaultBorderSettings, 2, 1, 2, 0, corner, AetherBorder.CLIP_LEFT, AetherBorder.ORIENTATION_HORI);
        Border tabRightSelected  = new AetherBorder(tabSelectedSettings, 0, 1, 2, 0, corner, AetherBorder.CLIP_LEFT, AetherBorder.ORIENTATION_HORI);
        Border tabRightFocused  = new AetherBorder(tabBottomFocusedSettings, 0, 1, 2, 0, corner, AetherBorder.CLIP_LEFT, AetherBorder.ORIENTATION_HORI);

        Border nullBorder = new LineBorder(background);
        Border checkboxBorder = new LineBorder(highlight,-1,1,false,Graphics.DOTTED);

        Border tooltipBorder = new LineBorder(AetherBorder.getShade(baseHighlight,0.9),baseHighlight,1,true);

        /*
         * Apply the styles
         */
        
        Style defaultStyle = new Style();
        defaultStyle.addFont(font, Style.ALL);
        defaultStyle.addBackground( background, Style.ALL);
        defaultStyle.addForeground( text, Style.ALL);
        setStyleFor("",defaultStyle);

//        Style label = new Style(defaultStyle);
//        label.addBackground(-1,Style.ALL);
//        label.addBorder(new LineBorder(0x00FF0000),Style.ALL);
//        setStyleFor("Label", label);

        Style window = new Style(defaultStyle);
        window.addBorder(windowBackground, Style.ALL);
        setStyleFor("Window",window);
        setStyleFor("Dialog",window);

        Style scrollStyle = new Style(defaultStyle);
        scrollStyle.addProperty(new Integer( dark ),"thumbFill",Style.ALL );
        scrollStyle.addProperty(new Integer( light ),"trackFill",Style.ALL );
        setStyleFor("ScrollPane",scrollStyle);

        Style title = new Style();
        title.addBorder(titleBackground,Style.ALL);
        title.addBackground(-1, Style.ALL);
        setStyleFor("TitleBar",title);
        
        Style buttonStyle = new Style(defaultStyle);
        //buttonStyle.addFont(font, Style.ALL);
        //buttonStyle.addForeground( 0x00000000 , Style.ALL);
        buttonStyle.addBackground(-1, Style.ALL);
        buttonStyle.addForeground( disabledText, Style.DISABLED);
        buttonStyle.addBorder(buttonDefault, Style.ALL);
        buttonStyle.addBorder(buttonFocused, Style.FOCUSED);
        buttonStyle.addBorder(buttonSelected, Style.SELECTED);
        buttonStyle.addBorder(buttonDisabled, Style.DISABLED);
        setStyleFor("Button",buttonStyle);

        Style clear = new Style(defaultStyle);
        clear.addBackground(-1,Style.ALL);
        setStyleFor("List",clear);
        setStyleFor("Label",clear);

        Style list = new Style(defaultStyle);
        list.addBorder(new LineBorder(dark),Style.ALL);
        list.addBackground(-1,Style.ALL);
        setStyleFor("Menu",list);

        Style listRenderer = new Style(defaultStyle);
        listRenderer.addBackground(base,Style.ALL);
        listRenderer.addBackground(-1,Style.SELECTED);
        listRenderer.addBorder(new LineBorder( base ,-1,1,false),Style.ALL);
        listRenderer.addBorder(listItemFocused,Style.FOCUSED|Style.SELECTED);
        listRenderer.addBorder(listItemSelected,Style.SELECTED);
        setStyleFor("ListRenderer",listRenderer);

        Style textbox = new Style(defaultStyle);
        textbox.addBorder(textboxBorder,Style.ALL);
        textbox.addBorder(textboxFocused,Style.FOCUSED);
        textbox.addBorder(textboxFocused,Style.SELECTED);
        textbox.addBorder(textboxDisabled,Style.DISABLED);
        textbox.addBackground(-1,Style.ALL);
        textbox.addForeground(disabledText, Style.DISABLED);
        //setStyleFor("TextArea",textbox);
        setStyleFor("TextField",textbox);

        Style comboBox = new Style(textbox);
        comboBox.addProperty(comboBoxIcon,"icon", Style.ALL);
        comboBox.addProperty(comboBoxSelectedIcon,"icon", Style.SELECTED);
        setStyleFor("ComboBox", comboBox);

        Style checkbox = new Style(defaultStyle);
        checkbox.addBorder(checkboxBorder, Style.FOCUSED);
        //checkbox.addBorder(null, Style.SELECTED);
        checkbox.addBorder(nullBorder, Style.ALL);        
        checkbox.addForeground(disabledText, Style.DISABLED);
        checkbox.addProperty(checkboxIcon,"icon", Style.ALL);
        checkbox.addProperty(checkboxSelectedIcon,"icon", Style.SELECTED);
        setStyleFor("CheckBox", checkbox);

        Style radio = new Style(checkbox);
        radio.addProperty(radioIcon,"icon", Style.ALL);
        radio.addProperty(radioFocusedIcon,"icon", Style.SELECTED);
        setStyleFor("RadioButton", radio);

        Style spinner = new Style(defaultStyle);
        spinner.addBorder(spinnerBorder,Style.ALL);
        spinner.addBorder(spinnerFocused,Style.FOCUSED);
        spinner.addBackground(-1,Style.ALL);
        spinner.addProperty(spinnerLeftIcon, "iconLeft", Style.ALL);
        spinner.addProperty(spinnerLeftSelectedIcon, "iconLeft", Style.SELECTED);
        spinner.addProperty(spinnerRightIcon, "iconRight", Style.ALL);
        spinner.addProperty(spinnerRightSelectedIcon, "iconRight", Style.SELECTED);
        setStyleFor("Spinner",spinner);


        /**
         * TABS
         */
        Style tabTopStyle = new Style(defaultStyle);
        tabTopStyle.addBorder(tabTop, Style.ALL);
        tabTopStyle.addBorder(tabTopSelected, Style.SELECTED);
        tabTopStyle.addBorder(tabTopFocused, Style.SELECTED|Style.FOCUSED);
        tabTopStyle.addBackground(-1,Style.ALL);
        setStyleFor("TabRendererTop", tabTopStyle);

        Style tabBottomStyle = new Style(defaultStyle);
        tabBottomStyle.addBorder(tabBottom, Style.ALL);
        tabBottomStyle.addBorder(tabBottomSelected, Style.SELECTED);
        tabBottomStyle.addBorder(tabBottomFocused, Style.SELECTED|Style.FOCUSED);
        tabBottomStyle.addBackground(-1,Style.ALL);
        setStyleFor("TabRendererBottom", tabBottomStyle);

        Style tabLeftStyle = new Style(defaultStyle);
        tabLeftStyle.addBorder(tabLeft, Style.ALL);
        tabLeftStyle.addBorder(tabLeftSelected, Style.SELECTED);
        tabLeftStyle.addBorder(tabLeftFocused, Style.SELECTED|Style.FOCUSED);
        tabLeftStyle.addBackground(-1,Style.ALL);
        setStyleFor("TabRendererLeft", tabLeftStyle);

        Style tabRightStyle = new Style(defaultStyle);
        tabRightStyle.addBorder(tabRight, Style.ALL);
        tabRightStyle.addBorder(tabRightSelected, Style.SELECTED);
        tabRightStyle.addBorder(tabRightFocused, Style.SELECTED|Style.FOCUSED);
        tabRightStyle.addBackground(-1,Style.ALL);
        setStyleFor("TabRendererRight", tabRightStyle);

        Style tabsTop = new Style(defaultStyle);
        tabsTop.addBackground(-1, Style.ALL);
        tabsTop.addBorder(new CompoundBorder( new CompoundBorder( new MatteBorder(0, 0, 1, 0, tabHighlight ), new MatteBorder(0, 0, 1, 0, tabHighlight )), new EmptyBorder(2,0,-2,0)),Style.ALL);
        setStyleFor("TabTop", tabsTop);

        Style tabsBottom = new Style(tabsTop);
        tabsBottom.addBorder(new CompoundBorder( new CompoundBorder( new MatteBorder(1, 0, 0, 0, tabHighlight ), new MatteBorder(1, 0, 0, 0, tabHighlight )), new EmptyBorder(-2,0,2,0)),Style.ALL);
        setStyleFor("TabBottom", tabsBottom);

        Style tabsLeft = new Style(tabsTop);
        tabsLeft.addBorder(new CompoundBorder( new CompoundBorder( new MatteBorder(0, 0, 0, 1, tabHighlight ), new MatteBorder(1, 0, 0, 0, tabHighlight )) , new EmptyBorder(0,2,0,-2)),Style.ALL);
        setStyleFor("TabLeft", tabsLeft);

        Style tabsRight = new Style(tabsTop);
        tabsRight.addBorder(new CompoundBorder( new CompoundBorder( new MatteBorder(0, 1, 0, 0, tabHighlight ), new MatteBorder(0, 1, 0, 0, tabHighlight )), new EmptyBorder(0,-2,0,2)),Style.ALL);
        setStyleFor("TabRight", tabsRight);

        Style toolTip = new Style(defaultStyle);
        toolTip.addBorder(tooltipBorder,Style.ALL);
        toolTip.addBackground(-1,Style.ALL);
        setStyleFor("ToolTip", toolTip);
    }

    private void copyBorders(Vector from,Vector too) {
        too.removeAllElements();
        for (int i=0;i<from.size();i++) {
            too.insertElementAt(from.elementAt(i),i);
        }
    }

}

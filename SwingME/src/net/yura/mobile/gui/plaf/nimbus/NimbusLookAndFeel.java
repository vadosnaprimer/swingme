package net.yura.mobile.gui.plaf.nimbus;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.MetalScrollBar;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;

/**
 * @author Nathan
 */
public class NimbusLookAndFeel extends SynthLookAndFeel {

    private Hashtable uiSettings = new Hashtable();

    public NimbusLookAndFeel() {
        this(javax.microedition.lcdui.Font.SIZE_MEDIUM);
    }

    public NimbusLookAndFeel(int size) {
        this(size,null);
    }

    public NimbusLookAndFeel(int size,Hashtable styles) {

        if (styles!=null) uiSettings = styles;


        Integer noColor = new Integer(Style.NO_COLOR);

        // Primary Colors
        setUIDefault("control", new Integer(0xFFd6d9df));
        setUIDefault("info", new Integer(0xFFf2f2bd));
        setUIDefault("nimbusAlertYellow", new Integer(0xFFffdc23));
        setUIDefault("nimbusBase", new Integer(0xFF33628c));
        setUIDefault("nimbusDisabledText", new Integer(0xFF8e8f91));
        setUIDefault("nimbusFocus", new Integer(0xFF73a4d1));
        setUIDefault("nimbusGreen", new Integer(0xFFb0b332));
        setUIDefault("nimbusInfoBlue", new Integer(0xFF2f5cb4));
        setUIDefault("nimbusLightBackground", new Integer(0xFFffffff));
        setUIDefault("nimbusOrange", new Integer(0xFFbf6204));
        setUIDefault("nimbusRed", new Integer(0xFFa92e22));
        setUIDefault("nimbusSelectedText", new Integer(0xFFffffff));
        setUIDefault("nimbusSelectionBackground", new Integer(0xFF39698a));
        setUIDefault("text", new Integer(0xFF000000));
        setUIDefault("info",new Integer(NimbusBorder.getRGB(242, 242, 189)));
        
        // Secondary Colors
        setUIDefault("nimbusBlueGrey",getDerivedColor("nimbusBase",0.032459438f,-0.48f,0.19607842f,0));
        setUIDefault("background",uiSettings.get("control"));
        setUIDefault("infoText",getDerivedColor("text",0.0f,0.0f,0.0f,0));
        setUIDefault("menuText",getDerivedColor("text",0.0f,0.0f,0.0f,0));
        setUIDefault("menu",getDerivedColor("nimbusBase",0.021348298f,-0.6150531f,0.39999998f,0));
        setUIDefault("scrollbar",getDerivedColor("nimbusBlueGrey",-0.006944418f,-0.07296763f,0.09019607f,0));
        setUIDefault("controlText",getDerivedColor("text",0.0f,0.0f,0.0f,0));
        setUIDefault("controlHighlight",getDerivedColor("nimbusBlueGrey",0.0f,-0.07333623f,0.20392156f,0));
        setUIDefault("controlLHighlight",getDerivedColor("nimbusBlueGrey",0.0f,-0.098526314f,0.2352941f,0));
        setUIDefault("controlShadow",getDerivedColor("nimbusBlueGrey",-0.0027777553f,-0.0212406f,0.13333333f,0));
        setUIDefault("controlDkShadow",getDerivedColor("nimbusBlueGrey",-0.0027777553f,-0.0018306673f,-0.02352941f,0));
        setUIDefault("textHighlight",getDerivedColor("nimbusSelectionBackground",0.0f,0.0f,0.0f,0));
        setUIDefault("textHighlightText",getDerivedColor("nimbusSelectedText",0.0f,0.0f,0.0f,0));
        setUIDefault("textInactiveText",getDerivedColor("nimbusDisabledText",0.0f,0.0f,0.0f,0));
        setUIDefault("desktop",getDerivedColor("nimbusBase",-0.009207249f,-0.13984653f,-0.07450983f,0));
        setUIDefault("activeCaption",getDerivedColor("nimbusBlueGrey",0.0f,-0.049920253f,0.031372547f,0));
        setUIDefault("inactiveCaption",getDerivedColor("nimbusBlueGrey",-0.00505054f,-0.055526316f,0.039215684f,0));
        setUIDefault("nimbusBorder",getDerivedColor("nimbusBlueGrey",0.0f,-0.017358616f,-0.11372548f,0));
        setUIDefault("nimbusSelection",getDerivedColor("nimbusBase",-0.010750473f,-0.04875779f,-0.007843137f,0));

        // Misc.
        setUIDefault("font", new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_PLAIN, size));

        // Command button defaults
        //int color1 = getDerivedColor("nimbusBase", 0.03f, -0.58f, 0.07f, 0).intValue();
        int color2 = getDerivedColor("nimbusBlueGrey", 0f, -0.01f, -0.38f, 0).intValue();
        int color3 = getDerivedColor("nimbusBlueGrey", 0f, -0.07f, 0.21f, 0).intValue();
        int color4 = getDerivedColor("nimbusBlueGrey", 0f, -0.07f, 0.12f, 0).intValue();

        // Command button selected
        //int color23 = getDerivedColor("nimbusBase", 0.03f, -0.55f, -0.02f, 0).intValue();
        int color24 = getDerivedColor("nimbusBlueGrey", 0f, 0.11f, -0.54f, 0).intValue();
        int color25 = getDerivedColor("nimbusBlueGrey", 0f, -0.1f, 0.26f, 0).intValue();
        int color26 = getDerivedColor("nimbusBlueGrey", 0f, -0.07f, 0.17f, 0).intValue();

        // Default button defaults
        int color5 = getDerivedColor("nimbusBase", 0f, -0.35f, 0.01f, 0).intValue();
        int color6 = getDerivedColor("nimbusBase", 0f, -0.1f, -0.26f, 0).intValue();
        int color7 = getDerivedColor("nimbusBase", 0f, -0.58f, 0.38f, 0).intValue();
        int color8 = getDerivedColor("nimbusBase", 0f, -0.44f, 0.30f, 0).intValue();

        // Default button focused
        //int color9 = getDerivedColor("nimbusBase", 0f, -0.28f, 0.12f, 0).intValue();
        int color10 = getDerivedColor("nimbusBase", 0f, -0.18f, -0.12f, 0).intValue();
        int color11 = getDerivedColor("nimbusBase", 0f, -0.62f, 0.44f, 0).intValue();
        int color12 = getDerivedColor("nimbusBase", 0f, -0.46f, 0.32f, 0).intValue();

        // Default button selected
        int color13 = getDerivedColor("nimbusBase", -1f, -1f, -1f, 0).intValue();
        int color14 = getDerivedColor("nimbusBase", 0f, 0.1f, -0.23f, 0).intValue();
        int color15 = getDerivedColor("nimbusBase", 0f, -0.3f, 0.15f, 0).intValue();
        int color16 = getDerivedColor("nimbusBase", 0f, -0f, 0f, 0).intValue();

        // Default button disabled
        //int color17 = getDerivedColor("nimbusBase", 0.03f, -0.57f, 0.26f, 0).intValue();
        int color18 = getDerivedColor("nimbusBase", 0.05f, -0.5f, 0.26f, 0).intValue();
        int color19 = getDerivedColor("nimbusBase", 0.03f, -0.59f, 0.34f, 0).intValue();
        int color20 = getDerivedColor("nimbusBase", 0.03f, -0.57f, 0.31f, 0).intValue();

        // Default Stlyes
        setUIDefault("foreground",uiSettings.get("text"));

        // Window
        Border windowBorder = new LineBorder(decodeColor("desktop"),Style.NO_COLOR,1,true,1);
        setUIDefault("Frame.border", windowBorder);

        // OptionPane
        setUIDefault("Dialog.background", uiSettings.get("controlHighlight"));//uiSettings.get("controlLHighlight"));
        setUIDefault("Dialog.border", windowBorder);

        // TitleBar
        int[] bottomBorderOnly = {0,0,1,0};
        Vector titleSettings = new Vector();
        titleSettings.addElement(new NimbusBorderSetting(decodeColor("controlDkShadow"), decodeColor("controlDkShadow"), bottomBorderOnly, 0, 1));
        titleSettings.addElement(new NimbusBorderSetting(decodeColor("controlHighlight"), decodeColor("controlShadow"), 1, 0, 1));
        Border title = new NimbusBorder(titleSettings);
        setUIDefault("TitleBar.border", title);
        setUIDefault("TitleBar.background", noColor);

        // MenuBar
        Vector menuSettings = new Vector();
        int menuBarBorderColor = getDerivedColor("background",0.0f,0.0f,-0.1f,0).intValue();
        int menuBarShadow = getDerivedColor("background",0.0f,0.0f,-0.05f,0).intValue();
        menuSettings.addElement(new NimbusBorderSetting(menuBarBorderColor, menuBarBorderColor, bottomBorderOnly, 0, 1));
        menuSettings.addElement(new NimbusBorderSetting(decodeColor("background"), menuBarShadow, 1, 0, 1));
        Border menubar = new NimbusBorder(menuSettings);
        setUIDefault("MenuBar.border", menubar);
        setUIDefault("MenuBar.background", noColor);

        // Menu Items

        // Label
        setUIDefault("Label.background", noColor);
        setUIDefault("TitleBarLabel.background", noColor);

        // BUTTONS

        Vector defaultBorderSettings = new Vector();
        defaultBorderSettings.addElement(new NimbusBorderSetting(color5, color5, 1, 3, 1));
        defaultBorderSettings.addElement(new NimbusBorderSetting(color7, color8, 1, 2, 0.65));

        Vector focusedBorderSettings = new Vector();
        focusedBorderSettings.addElement(new NimbusBorderSetting(color24, color24, 1, 3, 1));
        focusedBorderSettings.addElement(new NimbusBorderSetting(color25, color26, 1, 2, 0.65));
        
        Vector selectedBorderSettings = new Vector();
        selectedBorderSettings.addElement(new NimbusBorderSetting(color14, color14, 1, 3, 1));
        selectedBorderSettings.addElement(new NimbusBorderSetting(color15, color16, 1, 2, 0.65));

        Vector disabledBorderSettings = new Vector();
        disabledBorderSettings.addElement(new NimbusBorderSetting(color18, color18, 1, 3, 1));
        disabledBorderSettings.addElement(new NimbusBorderSetting(color19, color20, 1, 2, 0.65));

        setUIDefault("Button.border",new NimbusBorder(defaultBorderSettings));
        setUIDefault("Button.background",noColor);

        setUIDefault("Button[focused].border",new NimbusBorder(focusedBorderSettings));
        setUIDefault("Button[selected].border",new NimbusBorder(selectedBorderSettings));
        setUIDefault("Button[selected].foreground",uiSettings.get("nimbusSelectedText"));
        setUIDefault("Button[focused+selected].foreground",uiSettings.get("nimbusSelectedText"));
        setUIDefault("Button[disabled].border",new NimbusBorder(disabledBorderSettings));
        setUIDefault("Button[disabled].foreground",uiSettings.get("inactiveCaption"));

        // ICON BUTTON SETTINGS

        int[] leftCorners = {3,0,3,0};
        int[] leftInnerCorners = {2,0,2,0};
        Vector buttonLeftSettings = new Vector();
        buttonLeftSettings.addElement(new NimbusBorderSetting(Style.NO_COLOR, Style.NO_COLOR, 1, leftCorners, 1));
        buttonLeftSettings.addElement(new NimbusBorderSetting(color5, color5, new int[] {0,1,0,0}, leftCorners, 1));
        buttonLeftSettings.addElement(new NimbusBorderSetting(color7, color8, 1, leftInnerCorners, 0.65));

        Vector buttonLeftSelectedSettings = new Vector();
        buttonLeftSelectedSettings.addElement(new NimbusBorderSetting(Style.NO_COLOR, Style.NO_COLOR, 1, leftCorners, 1));
        buttonLeftSelectedSettings.addElement(new NimbusBorderSetting(color13, color13, new int[] {0,1,0,0}, leftCorners, 1));
        buttonLeftSelectedSettings.addElement(new NimbusBorderSetting(color15, color16, 1, leftInnerCorners, 0.65));

        int[] rightCorners = {0,3,0,3};
        int[] rightInnerCorners = {0,2,0,2};
        Vector buttonRightSettings = new Vector();
        buttonRightSettings.addElement(new NimbusBorderSetting(Style.NO_COLOR, Style.NO_COLOR, 1, rightCorners, 1));
        buttonRightSettings.addElement(new NimbusBorderSetting(color5, color5, new int[] {0,0,0,1}, rightCorners, 1));
        buttonRightSettings.addElement(new NimbusBorderSetting(color7, color8, 1, rightInnerCorners, 0.65));

        Vector buttonRightSelectedSettings = new Vector();
        buttonRightSelectedSettings.addElement(new NimbusBorderSetting(color14, color14, 1, rightCorners, 1));
        buttonRightSelectedSettings.addElement(new NimbusBorderSetting(color15, color16, 1, rightInnerCorners, 0.65));


        Font font = (Font) uiSettings.get("font");

        // TEXT AREAS

        Vector textareaBorderSettings = new Vector();
        int colorLightYellow = getDerivedColor("nimbusAlertYellow", 0.02f, -0.59f, 0f, 0f).intValue();
        textareaBorderSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusBorder"), decodeColor("nimbusBorder"), 1, 0, 1));
        textareaBorderSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), decodeColor("nimbusLightBackground"), 1, 0, 1));

        Vector textareaFocusedSettings = new Vector();
        copyBorders(textareaBorderSettings, textareaFocusedSettings);
        textareaFocusedSettings.setElementAt(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), colorLightYellow, 1, 0, 1), 1);

        Vector textareaDisabledSettings = new Vector();
        copyBorders(textareaBorderSettings, textareaDisabledSettings);

        setUIDefault("TextField.background",noColor);
        setUIDefault("TextField.border",new NimbusBorder(textareaBorderSettings));
        setUIDefault("TextField[focused].border",new NimbusBorder(textareaFocusedSettings));
        //setUIDefault("TextField[selected].border",new NimbusBorder(textareaSelectedSettings));
        setUIDefault("TextField[disabled].border",new NimbusBorder(textareaDisabledSettings));
        //setUIDefault("TextField.foreground",uiSettings.get("nimbusDisabledText"));

        setUIDefault("TextArea.background", uiSettings.get("nimbusLightBackground"));
        // TODO: this doesn't work
        setUIDefault("TextArea[disabled].background", noColor);

        // LISTS
        Vector listSettings = new Vector();
        Vector listItemSettings = new Vector();
        Vector listItemFocusedSettings = new Vector();
        Vector listItemSelectedSettings = new Vector();
        Vector listItemFocusedSelectedSettings = new Vector();

        int[] topBorderOnly = {1,0,0,0};
        int[] topAndBottomBorders = {1,0,1,0};
        copyBorders(textareaBorderSettings, listSettings);
        setUIDefault("Popup.border",new NimbusBorder(listSettings)); // things like the combo box popup
        setUIDefault("Menu.border",new NimbusBorder(listSettings));

        int seperatorBackground = getDerivedColor("nimbusLightBackground", 0f, 0f, -0.05f, 0f).intValue();
        int seperatorSelectedBackground = getDerivedColor("nimbusSelection", 0f, 0f, -0.05f, 0f).intValue();
        int darkerBackground = getDerivedColor("nimbusLightBackground", 0f, 0f, -0.15f, 0f).intValue();
        int darkerSelection = getDerivedColor("nimbusSelection", 0f, 0f, -0.1f, 0f).intValue();
        int lighterSelection = getDerivedColor("nimbusSelection", 0f, -0.1f, 0.2f, 0f).intValue();


         listItemSettings.addElement(new NimbusBorderSetting(seperatorBackground, seperatorBackground, bottomBorderOnly, 0, 1));
         listItemSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), decodeColor("nimbusLightBackground"), topBorderOnly, 0, 1));
//        listItemSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), decodeColor("nimbusLightBackground"), 2, 0, 1));
         listItemSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), decodeColor("nimbusLightBackground"), 0, 0, 1));
 
         listItemFocusedSettings.addElement(new NimbusBorderSetting(darkerBackground, darkerBackground, topAndBottomBorders, 0, 1));
         listItemFocusedSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), darkerBackground, 1, 0, 0.65));
//        listItemFocusedSettings.addElement(new NimbusBorderSetting(darkerBackground, darkerBackground, bottomBorderOnly, 0, 1));
//        listItemFocusedSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), decodeColor("nimbusLightBackground"), 1, 0, 1));
//        listItemFocusedSettings.addElement(new NimbusBorderSetting(darkerBackground, darkerBackground, 1, 3, 1));
//        listItemFocusedSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusLightBackground"), darkerBackground, 1, 2, 0.65));

         listItemSelectedSettings.addElement(new NimbusBorderSetting(seperatorSelectedBackground, seperatorSelectedBackground, bottomBorderOnly, 0, 1));
         listItemSelectedSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusSelection"), decodeColor("nimbusSelection"), topBorderOnly, 0, 1));
//        listItemSelectedSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusSelection"), decodeColor("nimbusSelection"), 2, 0, 1));
         listItemSelectedSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusSelection"), decodeColor("nimbusSelection"), 0, 0, 1));

         listItemFocusedSelectedSettings.addElement(new NimbusBorderSetting(darkerSelection, darkerSelection, topAndBottomBorders, 0, 1));
         listItemFocusedSelectedSettings.addElement(new NimbusBorderSetting(lighterSelection, decodeColor("nimbusSelection"), 1, 0, 0.65));
//        listItemFocusedSelectedSettings.addElement(new NimbusBorderSetting(seperatorSelectedBackground, seperatorSelectedBackground, bottomBorderOnly, 0, 1));
//        listItemFocusedSelectedSettings.addElement(new NimbusBorderSetting(decodeColor("nimbusSelection"), decodeColor("nimbusSelection"), 1, 0, 1));
//        listItemFocusedSelectedSettings.addElement(new NimbusBorderSetting(darkerSelection, darkerSelection, 1, 3, 1));
//        listItemFocusedSelectedSettings.addElement(new NimbusBorderSetting(lighterSelection, decodeColor("nimbusSelection"), 1, 2, 0.65));

        NimbusBorder tmp1 = new NimbusBorder(listItemSettings);
        NimbusBorder tmp2 = new NimbusBorder(listItemSelectedSettings);
        NimbusBorder tmp3 = new NimbusBorder(listItemFocusedSettings);
        NimbusBorder tmp4 = new NimbusBorder(listItemFocusedSelectedSettings);

        String componentName;
        String[] components = {"ListRenderer", "PopupListRenderer", "CheckBoxRenderer", "ListRendererCollapsed"};
        for (int c=0;c<components.length;c++) {
            componentName = components[c];

            setUIDefault(componentName+".background", noColor);
            setUIDefault(componentName+".border", tmp1);
            setUIDefault(componentName+"[selected].border",tmp2);
            setUIDefault(componentName+"[selected].foreground",uiSettings.get("nimbusSelectedText"));
            setUIDefault(componentName+"[disabled].foreground",uiSettings.get("nimbusDisabledText"));
            setUIDefault(componentName+"[focused].border",tmp3);
            setUIDefault(componentName+"[focused+selected].border",tmp4);
        }

        setUIDefault("List.background", uiSettings.get("nimbusLightBackground") );

        componentName = "MenuRenderer";

        setUIDefault(componentName+".background", noColor);
        setUIDefault(componentName+".border", new EmptyBorder(tmp2.getTop(), tmp2.getLeft(), tmp2.getBottom(), tmp2.getRight()));
        setUIDefault(componentName+"[selected].border",tmp2);
        setUIDefault(componentName+"[selected].foreground",uiSettings.get("nimbusSelectedText"));
        setUIDefault(componentName+"[disabled].foreground",uiSettings.get("nimbusDisabledText"));

        // COMBOS
        Vector comboBorderSettings = new Vector();
        comboBorderSettings.addElement(new NimbusBorderSetting(color2, color2, 1, 3, 1));
        comboBorderSettings.addElement(new NimbusBorderSetting(color3, color4, 1, 2, 0.65));

        Vector comboSelectedSettings = new Vector();
        comboSelectedSettings.addElement(new NimbusBorderSetting(color24, color24, 1, 3, 1));
        comboSelectedSettings.addElement(new NimbusBorderSetting(color25, color26, 1, 2, 0.65));

        Vector comboDisabledSettings = new Vector();
        comboDisabledSettings.addElement(new NimbusBorderSetting(color18, color18, 1, 3, 1));
        comboDisabledSettings.addElement(new NimbusBorderSetting(color19, color20, 1, 2, 0.65));

        Icon comboBoxIcon = new NimbusIcon(font.getHeight()+6, LookAndFeel.ICON_COMBO, buttonRightSettings, decodeColor("text"));
        Icon comboBoxSelectedIcon = new NimbusIcon(font.getHeight()+6, LookAndFeel.ICON_COMBO, buttonRightSelectedSettings, decodeColor("nimbusSelectedText"));
        Icon comboBoxDisabledIcon = new NimbusIcon(font.getHeight()+6, LookAndFeel.ICON_COMBO, disabledBorderSettings, decodeColor("nimbusDisabledText"));

        setUIDefault("ComboBox.border",new NimbusBorder(comboBorderSettings));
        setUIDefault("ComboBox.background",noColor);
        setUIDefault("ComboBox.property[arrow]",comboBoxIcon);
        setUIDefault("ComboBox[focused].border",new NimbusBorder(comboSelectedSettings));
        setUIDefault("ComboBox[selected].border",new NimbusBorder(comboSelectedSettings));
        setUIDefault("ComboBox[selected].property[arrow]",comboBoxSelectedIcon);
        setUIDefault("ComboBox[disabled].border",new NimbusBorder(comboDisabledSettings));
        setUIDefault("ComboBox[disabled].foreground",uiSettings.get("nimbusDisabledText"));
        setUIDefault("ComboBox[disabled].property[arrow]",comboBoxDisabledIcon);

        // CHECKBOX

        Vector checkboxSettings = new Vector();
        checkboxSettings.addElement(new NimbusBorderSetting());
        checkboxSettings.addElement(new NimbusBorderSetting(color2, color2, 1, 1, 1));
        checkboxSettings.addElement(new NimbusBorderSetting(color3, color4, 1, 1, 1));

//        Vector checkboxFocusedSettings = new Vector();
//        checkboxFocusedSettings.addElement(new NimbusBorderSetting());
//        checkboxFocusedSettings.addElement(new NimbusBorderSetting(color24, color24, 1, 1, 1));
//        checkboxFocusedSettings.addElement(new NimbusBorderSetting(color25, color26, 1, 1, 1));

        Icon checkboxIcon = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, checkboxSettings, decodeColor("text"));
        //Icon checkboxFocusedIcon = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, checkboxFocusedSettings, decodeColor("text"));
        Icon checkboxSelectedIcon = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_CHECKBOX, checkboxSettings, decodeColor("text"));

        Border nullBorder = new LineBorder(decodeColor("background"));

        setUIDefault("CheckBox.border",nullBorder);
        setUIDefault("CheckBox[focused].border",new LineBorder(decodeColor("nimbusBorder"),Style.NO_COLOR,1,false,Graphics.DOTTED));
        setUIDefault("CheckBox.property[icon]",checkboxIcon);
        //setUIDefault("CheckBox[focused].property[icon]",checkboxFocusedIcon);
        setUIDefault("CheckBox[selected].property[icon]",checkboxSelectedIcon);
        setUIDefault("CheckBox[disabled].foreground",uiSettings.get("nimbusDisabledText"));

        setUIDefault("CheckBoxRenderer.property[icon]",checkboxIcon);
        //setUIDefault("CheckBoxRenderer[focused].property[icon]",checkboxFocusedIcon);
        setUIDefault("CheckBoxRenderer[selected].property[icon]",checkboxSelectedIcon);
        
        // RADIOBUTTON

        Vector radioSettings = new Vector();
        radioSettings.addElement(new NimbusBorderSetting());
        radioSettings.addElement(new NimbusBorderSetting(color2, color2, 1, 3, 1));
        radioSettings.addElement(new NimbusBorderSetting(color3, color4, 1, 3, 1));

        Vector radioSelectedSettings = new Vector();
        radioSelectedSettings.addElement(new NimbusBorderSetting());
        radioSelectedSettings.addElement(new NimbusBorderSetting(color10, color10, 1, 3, 1));
        radioSelectedSettings.addElement(new NimbusBorderSetting(color11, color12, 1, 3, 1));

        Icon radioIcon = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_RADIO, radioSettings, decodeColor("text"));
        Icon radioFocusedIcon = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_RADIO, radioSelectedSettings, decodeColor("text"));

        setUIDefault("RadioButton.border",nullBorder);
        setUIDefault("RadioButton[focused].border",new LineBorder(decodeColor("nimbusBorder"),Style.NO_COLOR,1,false,Graphics.DOTTED));
        setUIDefault("RadioButton.property[icon]",radioIcon);
        setUIDefault("RadioButton[selected].property[icon]",radioIcon);
        setUIDefault("RadioButton[disabled].foreground",uiSettings.get("nimbusDisabledText"));

        // SPINNER

        Icon spinnerLeftIcon = new NimbusIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_LEFT, buttonLeftSettings, decodeColor("text"));
        Icon spinnerLeftSelectedIcon = new NimbusIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_LEFT, buttonLeftSelectedSettings, decodeColor("nimbusSelectedText"));
        Icon spinnerRightIcon = new NimbusIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_RIGHT, buttonRightSettings, decodeColor("text"));
        Icon spinnerRightSelectedIcon = new NimbusIcon(font.getHeight()+6, LookAndFeel.ICON_SPINNER_RIGHT, buttonRightSelectedSettings, decodeColor("nimbusSelectedText"));

        Vector spinnerBorderSettings = new Vector();
        Vector spinnerSelectedSettings = new Vector();
        Vector spinnerDisabledSettings = new Vector();
        copyBorders(comboBorderSettings, spinnerBorderSettings);
        copyBorders(comboSelectedSettings, spinnerSelectedSettings);
        copyBorders(comboDisabledSettings, spinnerDisabledSettings);

        setUIDefault("Spinner.background",noColor);
        setUIDefault("Spinner.border",new NimbusBorder(spinnerBorderSettings,0,0,20,0));
        setUIDefault("Spinner[focused].border",new NimbusBorder(spinnerSelectedSettings,0,0,20,0));
        setUIDefault("Spinner.property[iconLeft]",spinnerLeftIcon);
        setUIDefault("Spinner[selected].property[iconLeft]",spinnerLeftSelectedIcon);
        setUIDefault("Spinner.property[iconRight]",spinnerRightIcon);
        setUIDefault("Spinner[selected].property[iconRight]",spinnerRightSelectedIcon);

        // TAB'S TOP

        int[] topTabRendererBorders = {1,1,0,1};
        int[] topCorners = {3,3,0,0};
        int[] topInnerCorners = {2,2,0,0};
        Vector tabRendererTopSettings = new Vector();
        tabRendererTopSettings.addElement(new NimbusBorderSetting(color24, color24, topTabRendererBorders, topCorners, 1));
        tabRendererTopSettings.addElement(new NimbusBorderSetting(color25, color26, topTabRendererBorders, topInnerCorners, 1));
        NimbusBorder tabRendererTop = new NimbusBorder(tabRendererTopSettings,1,2,2,2);

        Vector tabRendererTopFocusedSettings = new Vector();
        tabRendererTopFocusedSettings.addElement(new NimbusBorderSetting(color13, color2, topTabRendererBorders, topCorners, 1));
        tabRendererTopFocusedSettings.addElement(new NimbusBorderSetting(color15, color8, topTabRendererBorders, topInnerCorners, 1));
        NimbusBorder tabRendererTopFocused = new NimbusBorder(tabRendererTopFocusedSettings,1,1,2,2);

        Vector tabRendererTopSelectedSettings = new Vector();
        tabRendererTopSelectedSettings.addElement(new NimbusBorderSetting(color6, color6, topTabRendererBorders, topCorners, 1));
        tabRendererTopSelectedSettings.addElement(new NimbusBorderSetting(color7, color8, topTabRendererBorders, topInnerCorners, 1));
        NimbusBorder tabRendererTopSelected = new NimbusBorder(tabRendererTopSelectedSettings,1,1,2,2);

        setUIDefault("TabRendererTop.background",noColor);
        setUIDefault("TabRendererTop.border",tabRendererTop);
        setUIDefault("TabRendererTop[focused+selected].border",tabRendererTopFocused);
        setUIDefault("TabRendererTop[selected].border",tabRendererTopSelected);

        // TABS BOTTOM

        int[] bottomTabRendererBorders = {0,1,1,1};
        int[] bottomCorners = {0,0,3,3};
        int[] bottomInnerCorners = {0,0,2,2};
        Vector tabRendererBottomSettings = new Vector();
        tabRendererBottomSettings.addElement(new NimbusBorderSetting(color24, color24, bottomTabRendererBorders, bottomCorners, 1));
        tabRendererBottomSettings.addElement(new NimbusBorderSetting(color25, color26, bottomTabRendererBorders, bottomInnerCorners, 1));
        NimbusBorder tabRendererBottom = new NimbusBorder(tabRendererBottomSettings,1,2,2,2);

        Vector tabRendererBottomFocusedSettings = new Vector();
        tabRendererBottomFocusedSettings.addElement(new NimbusBorderSetting(color13, color2, bottomTabRendererBorders, bottomCorners, 1));
        tabRendererBottomFocusedSettings.addElement(new NimbusBorderSetting(color15, color8, bottomTabRendererBorders, bottomInnerCorners, 1));
        NimbusBorder tabRendererBottomFocused = new NimbusBorder(tabRendererBottomFocusedSettings,1,1,2,2);

        Vector tabRendererBottomSelectedSettings = new Vector();
        tabRendererBottomSelectedSettings.addElement(new NimbusBorderSetting(color13, color5, bottomTabRendererBorders, bottomCorners, 1));
        tabRendererBottomSelectedSettings.addElement(new NimbusBorderSetting(color15, color7, bottomTabRendererBorders, bottomInnerCorners, 1));
        NimbusBorder tabRendererBottomSelected = new NimbusBorder(tabRendererBottomSelectedSettings,1,1,2,2);

        setUIDefault("TabRendererBottom.background",noColor);
        setUIDefault("TabRendererBottom.border",tabRendererBottom);
        setUIDefault("TabRendererBottom[focused+selected].border",tabRendererBottomFocused);
        setUIDefault("TabRendererBottom[selected].border",tabRendererBottomSelected);

        // TABS LEFT
        int[] leftTabRendererBorders = {1,0,1,1};
        Vector tabRendererLeftSettings = new Vector();
        tabRendererLeftSettings.addElement(new NimbusBorderSetting(color24, color24, leftTabRendererBorders, leftCorners, 1, NimbusBorder.ORIENTATION_HORI));
        tabRendererLeftSettings.addElement(new NimbusBorderSetting(color25, color26, leftTabRendererBorders, leftInnerCorners, 1, NimbusBorder.ORIENTATION_HORI));
        NimbusBorder tabRendererLeft = new NimbusBorder(tabRendererLeftSettings,2,1,2,2);

        Vector tabRendererLeftFocusedSettings = new Vector();
        tabRendererLeftFocusedSettings.addElement(new NimbusBorderSetting(color13, color2, leftTabRendererBorders, leftCorners, 1, NimbusBorder.ORIENTATION_HORI));
        tabRendererLeftFocusedSettings.addElement(new NimbusBorderSetting(color15, color8, leftTabRendererBorders, leftInnerCorners, 1, NimbusBorder.ORIENTATION_HORI));
        NimbusBorder tabRendererLeftFocused = new NimbusBorder(tabRendererLeftFocusedSettings,1,1,2,2);

        Vector tabRendererLeftSelectedSettings = new Vector();
        tabRendererLeftSelectedSettings.addElement(new NimbusBorderSetting(color5, color2, leftTabRendererBorders, leftCorners, 1, NimbusBorder.ORIENTATION_HORI));
        tabRendererLeftSelectedSettings.addElement(new NimbusBorderSetting(color7, color8, leftTabRendererBorders, leftInnerCorners, 1, NimbusBorder.ORIENTATION_HORI));
        NimbusBorder tabRendererLeftSelected = new NimbusBorder(tabRendererLeftSelectedSettings,1,1,2,2);

        setUIDefault("TabRendererLeft.background",noColor);
        setUIDefault("TabRendererLeft.border",tabRendererLeft);
        setUIDefault("TabRendererLeft[focused+selected].border",tabRendererLeftFocused);
        setUIDefault("TabRendererLeft[selected].border",tabRendererLeftSelected);

        // TABS RIGHT
        int[] rightTabRendererBorders = {1,1,1,0};
        Vector tabRendererRightSettings = new Vector();
        tabRendererRightSettings.addElement(new NimbusBorderSetting(color24, color24, rightTabRendererBorders, rightCorners, 1, NimbusBorder.ORIENTATION_HORI));
        tabRendererRightSettings.addElement(new NimbusBorderSetting(color26, color25, rightTabRendererBorders, rightInnerCorners, 1, NimbusBorder.ORIENTATION_HORI));
        NimbusBorder tabRendererRight = new NimbusBorder(tabRendererRightSettings,2,1,2,2);

        Vector tabRendererRightFocusedSettings = new Vector();
        tabRendererRightFocusedSettings.addElement(new NimbusBorderSetting(color13, color2, rightTabRendererBorders, rightCorners, 1, NimbusBorder.ORIENTATION_HORI));
        tabRendererRightFocusedSettings.addElement(new NimbusBorderSetting(color15, color8, rightTabRendererBorders, rightInnerCorners, 1, NimbusBorder.ORIENTATION_HORI));
        NimbusBorder tabRendererRightFocused = new NimbusBorder(tabRendererRightFocusedSettings,1,1,2,2);

        Vector tabRendererRightSelectedSettings = new Vector();
        tabRendererRightSelectedSettings.addElement(new NimbusBorderSetting(color13, color5, rightTabRendererBorders, rightCorners, 1, NimbusBorder.ORIENTATION_HORI));
        tabRendererRightSelectedSettings.addElement(new NimbusBorderSetting(color15, color7, rightTabRendererBorders, rightInnerCorners, 1, NimbusBorder.ORIENTATION_HORI));
        NimbusBorder tabRendererRightSelected = new NimbusBorder(tabRendererRightSelectedSettings,1,1,2,2);

        setUIDefault("TabRendererRight.background",noColor);
        setUIDefault("TabRendererRight.border",tabRendererRight);
        setUIDefault("TabRendererRight[focused+selected].border",tabRendererRightFocused);
        setUIDefault("TabRendererRight[selected].border",tabRendererRightSelected);

        // TAB AREA TOP
        Border tabTop = new CompoundBorder(new MatteBorder(0,0,1,0,color2),new MatteBorder(0,0,2,0,color8));
        tabTop = new CompoundBorder(tabTop,new MatteBorder(0,0,1,0,color2));
        setUIDefault("TabTop.background",noColor);
        setUIDefault("TabTop.border",new CompoundBorder(tabTop,new EmptyBorder(0,0,-2,0)));

        // TAB AREA BOTTOM
        Border tabBottom = new CompoundBorder(new MatteBorder(1,0,0,0,color2),new MatteBorder(2,0,0,0,color15));
        tabBottom = new CompoundBorder(tabBottom,new MatteBorder(1,0,0,0,color2));
        setUIDefault("TabBottom.background",noColor);
        setUIDefault("TabBottom.border",new CompoundBorder(tabBottom,new EmptyBorder(-2,0,0,0)));

        // TAB AREA RIGHT
        Border tabRight = new CompoundBorder(new MatteBorder(0,1,0,0,color2),new MatteBorder(0,2,0,0,color15));
        tabRight = new CompoundBorder(tabRight,new MatteBorder(0,1,0,0,color2));
        setUIDefault("TabRight.background",noColor);
        setUIDefault("TabRight.border",new CompoundBorder(tabRight,new EmptyBorder(0,-2,0,0)));

        // TAB AREA LEFT
        Border tabLeft = new CompoundBorder(new MatteBorder(0,0,0,1,color2),new MatteBorder(0,0,0,2,color8));
        tabLeft = new CompoundBorder(tabLeft,new MatteBorder(0,0,0,1,color2));
        setUIDefault("TabLeft.background",noColor);
        setUIDefault("TabLeft.border",new CompoundBorder(tabLeft,new EmptyBorder(0,0,0,-2)));

        // ToolTip

        setUIDefault("ToolTip.background",uiSettings.get("nimbusAlertYellow"));
        setUIDefault("ToolTip.border",new LineBorder(decodeColor("nimbusOrange")));

        // TODO: this is WRONG!!! do NOT do this, as will create strange effects with iPhone scroll mode
        // the reason this is here is to allow for the colored Dialogs, as its the color of the window that is supposed to show though
        // this is not good enough as font color will still be wrong if the main app is light, but the dialogs are dark, Android LightTheme has this problem
        // Panel
        setUIDefault("Panel.background", noColor);
        // ScrollPane
        setUIDefault("ScrollPane.background", noColor);

        Icon arrowUp = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_ARROW_UP, null, decodeColor("text"));
        Icon arrowDown = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_ARROW_DOWN, null, decodeColor("text"));
        Icon arrowLeft = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_ARROW_LEFT, null, decodeColor("text"));
        Icon arrowRight = new NimbusIcon(font.getHeight(), LookAndFeel.ICON_ARROW_RIGHT, null, decodeColor("text"));

        setUIDefault("ScrollPane.property[upArrow]", arrowUp);
        setUIDefault("ScrollPane.property[downArrow]", arrowDown);
        setUIDefault("ScrollPane.property[leftArrow]", arrowLeft);
        setUIDefault("ScrollPane.property[rightArrow]", arrowRight);

        // SCROLL BARS

        int evensize = (font.getHeight()/2)*2;

        Vector thumbFillSettings = new Vector();
        thumbFillSettings.addElement(new NimbusBorderSetting(color6, color6, 1, 0, 1));
        thumbFillSettings.addElement(new NimbusBorderSetting(color7, color8, 1, 0, 1));

                                                                                                                    // this -1 is the tickness
        Border thumb = new CompoundBorder(new NimbusBorder(thumbFillSettings),new EmptyBorder((font.getHeight()/2)-1,1,(font.getHeight()/2)-1,1));
        setUIDefault("ScrollBarThumb.border",thumb);

        int[] singleBorder = {1,1,1,1};

        Vector trackTopSettings = new Vector();
        trackTopSettings.addElement(new NimbusBorderSetting(color6, color6, singleBorder, leftCorners, 1));
        trackTopSettings.addElement(new NimbusBorderSetting(color7, color8, singleBorder, leftInnerCorners, 1));

        Vector trackBottomSettings = new Vector();
        trackBottomSettings.addElement(new NimbusBorderSetting(color6, color6, singleBorder, rightCorners, 1));
        trackBottomSettings.addElement(new NimbusBorderSetting(color7, color8, singleBorder, rightInnerCorners, 1));

        Vector trackFillSettings = new Vector();
        trackFillSettings.addElement(new NimbusBorderSetting(color4, color4, 1, 0, 1));

        Icon trackTop = new NimbusIcon(evensize, LookAndFeel.ICON_ARROW_LEFT, trackTopSettings, decodeColor("text"));
        Icon trackBottom = new NimbusIcon(evensize, LookAndFeel.ICON_ARROW_RIGHT, trackBottomSettings, decodeColor("text"));

        Border track = new CompoundBorder(
            new MetalScrollBar( trackTop, trackBottom ),
            new CompoundBorder(new NimbusBorder(trackFillSettings),new EmptyBorder((font.getHeight()/2),0,(font.getHeight()/2),0))
        );

        setUIDefault("ScrollBarTrack.border",track);


        // SLIDER

        int h = font.getHeight();

        Vector sliderSettings = new Vector();
        sliderSettings.addElement(new NimbusBorderSetting());
        sliderSettings.addElement(new NimbusBorderSetting(color5, color5, 1, 3, 1));
        sliderSettings.addElement(new NimbusBorderSetting(color7, color8, 1, 3, 1));

        setUIDefault("SliderThumb.border",new CompoundBorder(new NimbusBorder(sliderSettings),new EmptyBorder(h/3,h/3,h/3,h/3)) );

        Vector sliderBorderSettings = new Vector();
        sliderBorderSettings.addElement(new NimbusBorderSetting(color2, color2, 1, 3, 1));
        sliderBorderSettings.addElement(new NimbusBorderSetting(color3, color4, 1, 2, 0.65));

        Border strack = new CompoundBorder(
            new EmptyBorder((h/4),0,(h/4),0),
            new CompoundBorder(
                new NimbusBorder(sliderBorderSettings),
                new EmptyBorder((h/4),0,(h/4),0)
            )
        );

        setUIDefault("SliderTrack.border", strack );

    }

    private void setUIDefault(String key,Object value) {
        if (!uiSettings.containsKey(key)) {
            uiSettings.put(key, value);
        }
        //#mdebug debug
        else {
            System.out.println("[NimbusLookAndFeel] UIDefault already set: "+key+" "+value);
        }
        //#enddebug
    }


    public int decodeColor(String name) {
        if (uiSettings.containsKey(name)) {
            return ((Integer) uiSettings.get(name)).intValue();
        }
        return 0;
    }

    private void copyBorders(Vector from,Vector too) {
        too.removeAllElements();
        for (int i=0;i<from.size();i++) {
            too.insertElementAt(from.elementAt(i),i);
        }
    }

//    private Object getStyleSetting(String component,String setting,String state) {
//
//        String[] keys = {
//            component+"["+state+"]."+setting,
//            component+"."+setting,
//            setting
//        };
//
//        for (int i=0;i<keys.length;i++) {
//            if (uiSettings.containsKey(keys[i])) {
//                Object style = uiSettings.get(keys[i]);
//                return style;
//            }
//        }
//
//        return null;
//    }

//    private int getStyleSettingAsInt(String component,String setting,String state) {
//        Object o = getStyleSetting(component, setting, state);
//        if (o != null) {
//            return ((Integer)o).intValue();
//        }
//        return 0;
//    }

    public Style getStyle(String name) {

        Style style = super.getStyle(name);

        if (style != null) return style;

        // GET DEFAULT STYLE
        Style defaultStyle = new Style();
        if (!name.equals("")) {
            defaultStyle = getStyle("");
        }

        style = new Style(defaultStyle);

        Hashtable states = new Hashtable();

        states.put("all", new Integer(Style.ALL));
        states.put("focused", new Integer(Style.FOCUSED));
        states.put("selected", new Integer(Style.SELECTED));
        states.put("disabled", new Integer(Style.DISABLED));

        Enumeration settings = uiSettings.keys();
        Enumeration values   = uiSettings.elements();

        boolean somethingSet = false;

        while (settings.hasMoreElements()) {

            String key = (String) settings.nextElement();
            Object value = values.nextElement();

            String setting = null;
            String component = null;
            String property = null;
            int styleState = 0;
            if (name.equals("")) {
                if (key.indexOf(".") == -1) {
                    // Is a default setting
                    setting = key;
                }
            }
            else if (key.startsWith(name+".") || key.startsWith(name+"[")) {
              component = key.substring(0, key.indexOf("."));
                setting = key.substring(key.indexOf(".")+1);

                // Determine required state
                if (component.indexOf("[") > -1) {
                    String state = component.substring(component.indexOf("["), component.indexOf("]"));
                    Enumeration stateNames = states.keys();
                    Enumeration stateValues = states.elements();
                    while (stateNames.hasMoreElements()) {
                        String stateName = (String) stateNames.nextElement();
                        Integer stateValue = (Integer) stateValues.nextElement();
                        if (state.indexOf(stateName) > -1) {
                            styleState |= stateValue.intValue();
                        }
                    }
                }
            }

            if (setting != null) {
                somethingSet=true;
                if (setting.indexOf("[") > -1) {
                    property = setting.substring(setting.indexOf("[")+1, setting.indexOf("]"));
                }

                if (setting.equals("foreground")) {
                    style.addForeground(((Integer)value).intValue(), styleState);
                }
                if (setting.equals("background")) {
                    style.addBackground(((Integer)value).intValue(), styleState);
                }
                if (setting.equals("border")) {
                    style.addBorder(((Border)value), styleState);
                }
                if (setting.equals("font")) {
                    style.addFont(((Font)value), styleState);
                }
                if ((property != null) && (setting.indexOf("property") == 0)) {
                    style.addProperty(value, property, styleState);
                }
            }
        }
//
//        Enumeration values = states.elements();
//        Enumeration keys = states.keys();
//
//        while (keys.hasMoreElements()) {
//            String key = (String) keys.nextElement();
//            int state = ((Integer) values.nextElement()).intValue();
//
//            // Helper... may get rid of this one
//            Object setting = getStyleSetting(name,"borderSettings",key);
//            if (setting != null) {
//                int paddingX = getStyleSettingAsInt(name,"paddingX",key);
//                int paddingY = getStyleSettingAsInt(name,"paddingY",key);
//                int marginX = getStyleSettingAsInt(name,"marginX",key);
//                int marginY = getStyleSettingAsInt(name,"marginY",key);
//                Border border = new NimbusBorder((Vector) setting,marginX,marginY,paddingX,paddingY);
//                style.addBorder(border, state);
//            }
//            // end helper
//
//            setting = getStyleSetting(name,"border",key);
//            if (setting != null) {
//                Border border = (Border) setting;
//                style.addBorder(border, state);
//            }
//
//            setting = getStyleSetting(name,"foreground",key);
//            if (setting != null) {
//                style.addForeground(((Integer) setting).intValue(), state);
//            }
//
//            setting = getStyleSetting(name,"background",key);
//            if (setting != null) {
//                style.addBackground(((Integer) setting).intValue(), state);
//            }
//
//            setting = getStyleSetting(name,"font",key);
//            if (setting != null) {
//                style.addFont((Font) setting, state);
//            }
//
//            for (int i=0;i<properties.length;i++) {
//                setting = getStyleSetting(name,"property["+properties[i]+"]",key);
//                if (setting != null) {
//                    style.addProperty(setting, properties[i], state);
//                }
//            }
//        }

        if (!somethingSet) return null;

        setStyleFor(name, style);

        return style;
    }

    public Integer getDerivedColor(String color, float hOffset, float sOffset, float bOffset, float aOffset) {
        int c = decodeColor(color);
        float[] tmp = RGBtoHSB(NimbusBorder.getRed(c), NimbusBorder.getGreen(c), NimbusBorder.getBlue(c), null);
        // apply offsets
        tmp[0] = clamp(tmp[0] + hOffset);
        tmp[1] = clamp(tmp[1] + sOffset);
        tmp[2] = clamp(tmp[2] + bOffset);
        int alpha = (int)(clamp( ((c >> 24) & 0xff)/255f  + aOffset) * 255f);
        int newColor = HSBtoRGB(tmp[0], tmp[1], tmp[2]) | (alpha << 24);
        return new Integer(newColor);
    }

    private float clamp(float value) {
        if (value < 0) {
            value = 0;
        } else if (value > 1) {
            value = 1;
        }
        return value;
    }

    /*
     * FROM java.awt.Color
     */

    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
	float hue, saturation, brightness;
	if (hsbvals == null) {
	    hsbvals = new float[3];
	}
    	int cmax = (r > g) ? r : g;
	if (b > cmax) cmax = b;
	int cmin = (r < g) ? r : g;
	if (b < cmin) cmin = b;

	brightness = ((float) cmax) / 255.0f;
	if (cmax != 0)
	    saturation = ((float) (cmax - cmin)) / ((float) cmax);
	else
	    saturation = 0;
	if (saturation == 0)
	    hue = 0;
	else {
	    float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
	    float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
	    float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
	    if (r == cmax)
		hue = bluec - greenc;
	    else if (g == cmax)
	        hue = 2.0f + redc - bluec;
            else
		hue = 4.0f + greenc - redc;
	    hue = hue / 6.0f;
	    if (hue < 0)
		hue = hue + 1.0f;
	}
	hsbvals[0] = hue;
	hsbvals[1] = saturation;
	hsbvals[2] = brightness;
	return hsbvals;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
	int r = 0, g = 0, b = 0;
    	if (saturation == 0) {
	    r = g = b = (int) (brightness * 255.0f + 0.5f);
	} else {
	    float h = (hue - (float)Math.floor(hue)) * 6.0f;
	    float f = h - (float)java.lang.Math.floor(h);
	    float p = brightness * (1.0f - saturation);
	    float q = brightness * (1.0f - saturation * f);
	    float t = brightness * (1.0f - (saturation * (1.0f - f)));
	    switch ((int) h) {
	    case 0:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (t * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 1:
		r = (int) (q * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (p * 255.0f + 0.5f);
		break;
	    case 2:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (brightness * 255.0f + 0.5f);
		b = (int) (t * 255.0f + 0.5f);
		break;
	    case 3:
		r = (int) (p * 255.0f + 0.5f);
		g = (int) (q * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 4:
		r = (int) (t * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (brightness * 255.0f + 0.5f);
		break;
	    case 5:
		r = (int) (brightness * 255.0f + 0.5f);
		g = (int) (p * 255.0f + 0.5f);
		b = (int) (q * 255.0f + 0.5f);
		break;
	    }
	}
	return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }

}

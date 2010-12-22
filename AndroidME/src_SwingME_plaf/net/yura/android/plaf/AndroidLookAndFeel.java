package net.yura.android.plaf;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import net.yura.android.AndroidMeActivity;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;

public class AndroidLookAndFeel extends SynthLookAndFeel {

    private final static EmptyBorder EMPTY_BORDER = new EmptyBorder(0,0,0,0);

    public AndroidLookAndFeel() {

        Context ctx = AndroidMeActivity.DEFAULT_ACTIVITY;

        TypedArray a2 = ctx.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorForeground});
        int c = a2.getColor(0, 0xFF000000);
        a2.recycle();

//        ctx.setTheme(android.R.style.Theme_Black);

        Style defaultStyle = new Style();
        defaultStyle.addFont( new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_PLAIN, javax.microedition.lcdui.Font.SIZE_MEDIUM) , Style.ALL);
        defaultStyle.addForeground(c , Style.ALL);
        setStyleFor("", defaultStyle);

        Style androidMenuStyle = new Style(defaultStyle);
        androidMenuStyle.addBorder(getBorder(ctx, 0, android.R.drawable.menu_full_frame),Style.ALL);
        setStyleFor("AndroidMenu", androidMenuStyle);

        Style menuStyle = new Style(defaultStyle);
        menuStyle.addBorder(getBorder(ctx, 0, android.R.drawable.menu_frame),Style.ALL);
        //setForegroundColor(ctx, menuStyle, android.R.style.TextAppearance_Widget_IconMenu_Item,null); // a window does not need a foreground
        setStyleFor("Menu", menuStyle);

        Style menuBarStyle = new Style(defaultStyle);

        Drawable divider = getDrawable(ctx, android.R.style.Widget_ListView_Menu, android.R.attr.divider);
        int thickness = divider.getIntrinsicHeight();
        Rect thicknessRect = new Rect(thickness,thickness,0,0);
        //AndroidBorder.setDrawableState(Style.ALL, divider); // we set up the drawable to be able to get its color
        //Border divider = getListDivider(ctx, android.R.style.Widget_ListView_Menu, android.R.attr.divider);
        menuBarStyle.addProperty(new AndroidBorder(divider, thicknessRect), "divider", Style.ALL);
        // TODO we do not know where to get the verticalDivider, so here is a hack to create one.
        menuBarStyle.addProperty(new AndroidBorder(new ColorDrawable( getColorAtCenter(divider) ), thicknessRect), "verticalDivider", Style.ALL);
        setStyleFor("MenuBar", menuBarStyle);

        Rect menuRenderExtraPadding = getAdjustedDensityRect(ctx, 8, 8, 8, 8);
        Style menuRendererStyle = new Style(defaultStyle);
        //Border menuBorder = getBorder(ctx, android.R.style.Widget_ListView_Menu, android.R.attr.listSelector, menuRenderExtraPadding);
        //menuRendererStyle.addBorder(menuBorder,Style.ALL);


        // TODO: This is wrong... we should be using android.R.style.Widget_ListView, and then get its android.R.attr.textAppearance.
        // That should map to TextAppearance_Widget_TextView... For some reason, this is not working...
        // setForegroundColor(ctx, menuRendererStyle, android.R.style.TextAppearance_Widget_IconMenu_Item);

        Drawable menuBorder = getDrawable(ctx, android.R.style.Widget_ListView_Menu, android.R.attr.listSelector);
        menuRendererStyle.addBorder(new AndroidBorder(menuBorder, menuRenderExtraPadding),Style.ALL);

        menuBorder.setState( new int[] {android.R.attr.state_window_focused,android.R.attr.state_enabled,android.R.attr.state_focused} );

        //AndroidBorder.setDrawableState(Style.FOCUSED|Style.SELECTED, menuBorder);

        int color = getColorAtCenter(menuBorder);

        // TODO: Could not make it work... hard coded values for light theme.
        menuRendererStyle.addForeground(0xFF000000, Style.ALL);
        menuRendererStyle.addForeground( getTextColorFor(color), Style.SELECTED | Style.FOCUSED);
        menuRendererStyle.addForeground(0xFF808080, Style.DISABLED);
        setStyleFor("MenuRenderer",menuRendererStyle);
//        setStyleFor("MenuItem",menuItemStyle); // for the arrow to work

        // --- List ---
        Rect listExtraPadding = getAdjustedDensityRect(ctx, 10, 10, 10, 10);
        Border divider2 = getListDivider(ctx, android.R.style.Widget_ListView, android.R.attr.listDivider);

        Style listCellRenderer = new Style(defaultStyle);
        Border mainBorder = getBorder(ctx, android.R.style.Widget_ListView, android.R.attr.listSelector , listExtraPadding);
        listCellRenderer.addBorder(new BorderWithDivider(mainBorder, divider2,false ),Style.ALL);
        setForegroundColor(ctx, listCellRenderer, android.R.style.TextAppearance_Large,ListCellRenderer.class); // Has defined in simple_list_item.xml
        setStyleFor("ListRenderer",listCellRenderer);

        // --- List (No padding) ---
        Style listCellRendererCollapsed = new Style(defaultStyle);
        Border mainBorder2 = getBorder(ctx, android.R.style.Widget_ListView, android.R.attr.listSelector);
        listCellRendererCollapsed.addBorder(new BorderWithDivider(mainBorder2, divider2,true ),Style.ALL);
        setForegroundColor(ctx, listCellRendererCollapsed, android.R.style.TextAppearance_Large,ListCellRenderer.class); // Has defined in simple_list_item.xml
        setStyleFor("ListRendererCollapsed",listCellRendererCollapsed);


        // com.android.internal.R.style.Theme_Dialog_Alert
        // Dialog
        Style dialogStyle = new Style(defaultStyle);
        dialogStyle.addBorder(getBorder(ctx, 0, android.R.drawable.dialog_frame),Style.ALL);
        setStyleFor("Dialog", dialogStyle);

        Style titleBarStyle = new Style(defaultStyle);

        titleBarStyle.addBorder(getBorder(ctx, 0, android.R.drawable.title_bar),Style.ALL);
        setStyleFor("TitleBar", titleBarStyle);

        // This a non focusable component. Just set Style.ALL
        Style titleBarLabelStyle = new Style(defaultStyle);
        titleBarLabelStyle.addFont( new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_PLAIN, javax.microedition.lcdui.Font.SIZE_LARGE) , Style.ALL);
        setForegroundColor(ctx, titleBarLabelStyle, android.R.style.TextAppearance_WindowTitle, null, Style.ALL); // Has defined in alert_dialog.xml
        setStyleFor("TitleBarLabel", titleBarLabelStyle);

        // -- PreferenceSeparator --

        // as preference_category.xml -> theme(Theme.Light)/listSeparatorTextViewStyle -> style/Widget.TextView.ListSeparator.White -> background
        // This a non focusable component. Just set Style.ALL
        Style preferenceSeparatorStyle = new Style(defaultStyle);
        preferenceSeparatorStyle.addFont( new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_BOLD, javax.microedition.lcdui.Font.SIZE_SMALL) , Style.ALL);

        // this is a hack to get it to look decent on the sont erricson,
        // as just using the listSeparatorTextViewStyle is not enough
        Rect separatorPadding = getAdjustedDensityRect(ctx, 5, 2, 5, 2);
        Border titlebar = getBorder(ctx, 0, android.R.drawable.title_bar,new Rect(0,0,0,0));
        Border listSeparatorTextViewStyle = getBorder(ctx, android.R.attr.listSeparatorTextViewStyle, android.R.attr.background, separatorPadding);

        preferenceSeparatorStyle.addBorder(new CompoundBorder(titlebar, listSeparatorTextViewStyle),Style.ALL);
        setForegroundColor(ctx, preferenceSeparatorStyle, android.R.attr.listSeparatorTextViewStyle, null,Style.ALL); // Has defined in alert_dialog.xml
        setStyleFor("PreferenceSeparator", preferenceSeparatorStyle);


        // --- Button ---
        Style buttonStyle = new Style(defaultStyle);
        buttonStyle.addBorder(getBorder(ctx, android.R.attr.buttonStyle, android.R.attr.background),Style.ALL);
        setForegroundColor(ctx, buttonStyle, android.R.style.Widget_Button,Button.class);
        setStyleFor("Button", buttonStyle);

        Style redButtonStyle = new Style(defaultStyle);
        Drawable d = getDrawable(ctx, android.R.attr.buttonStyle, android.R.attr.background);
        d.setColorFilter(0x88FF0000, Mode.SRC_ATOP);
        redButtonStyle.addBorder(new AndroidBorder(d),Style.ALL);
        setForegroundColor(ctx, redButtonStyle, android.R.style.Widget_Button,Button.class);
        setStyleFor("RedButton", redButtonStyle);

        // --- Radio Button ---
        Style radioStyle = new Style(defaultStyle);
        radioStyle.addProperty(createButtonIcon(ctx, android.R.style.Widget_CompoundButton_RadioButton), "icon", Style.ALL);
        // Can't use the border. The border contains (left) padding for the button! Assuming transparent
        // addBorder(ctx, radioStyle, android.R.style.Widget_CompoundButton_RadioButton, android.R.attr.background);
        setForegroundColor(ctx, radioStyle, android.R.style.Widget_CompoundButton_RadioButton,RadioButton.class);
        setStyleFor("RadioButton",radioStyle);

        // --- Check Box ---
        Style checkboxStyle = new Style(defaultStyle);
        checkboxStyle.addProperty(createButtonIcon(ctx, android.R.style.Widget_CompoundButton_CheckBox), "icon", Style.ALL);
        // Can't use the border. The border contains (left) padding for the button! Assuming transparent
        // addBorder(ctx, checkboxStyle, android.R.style.Widget_CompoundButton_CheckBox, android.R.attr.background);
        setForegroundColor(ctx, checkboxStyle, android.R.style.Widget_CompoundButton_CheckBox,CheckBox.class);
        setStyleFor("CheckBox",checkboxStyle);

        // --- Frame ---
        Style frameStyle = new Style(defaultStyle);
        frameStyle.addBorder(getBorder(ctx, 0, android.R.attr.windowBackground),Style.ALL);
        setStyleFor("Frame",frameStyle);

        // --- Window ---
        Style windowStyle = new Style(defaultStyle);
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int size = Math.max(dm.widthPixels, dm.heightPixels);
        windowStyle.addProperty(new FadeIcon(0x80000000, size), "dim", Style.ALL);
        setStyleFor("Window", windowStyle);

        // --- TextField ---
        Style textFieldStyle = new Style(defaultStyle);
        textFieldStyle.addBorder(getBorder(ctx, android.R.attr.editTextStyle, android.R.attr.background),Style.ALL);
        setForegroundColor(ctx, textFieldStyle, android.R.style.Widget_EditText,TextField.class);
        setStyleFor("TextField",textFieldStyle);

        // --- TextArea ---
        Style inputStyle1 = new Style(textFieldStyle);
        inputStyle1.addBorder(getBorder(ctx, android.R.attr.textViewStyle, android.R.attr.background), Style.DISABLED);
        setForegroundColor(ctx, inputStyle1, android.R.style.TextAppearance_Widget_TextView, TextArea.class, Style.DISABLED);
        setStyleFor("TextArea",inputStyle1);

        // --- ComboBox ---
        Style comboStyle = new Style(buttonStyle);
        comboStyle.addBorder(getBorder(ctx, android.R.style.Widget_Spinner, android.R.attr.background),Style.ALL);
        setForegroundColor(ctx, comboStyle, android.R.style.TextAppearance_Widget_TextView_SpinnerItem,ComboBox.class);
        setStyleFor("ComboBox",comboStyle);

        // --- ComboBox2 ---

        // this is a total crazy hack, but sometimes we need these graphics, and this is the only way we have found to get them
        Style comboStyle2 = new Style(defaultStyle);
        Drawable d1 = getDrawable(ctx, "btn_circle");
        Drawable d2 = getDrawable(ctx, "ic_btn_round_more");

        Rect padding = getAdjustedDensityRect(ctx, 5, 8, 5, 8);
        comboStyle2.addBorder( new IconBorder(padding.top,padding.left,padding.bottom,padding.right,new AndroidIcon(d1),new AndroidIcon(d2)) ,Style.ALL);
        setForegroundColor(ctx, comboStyle2, android.R.style.Widget_CompoundButton_RadioButton,RadioButton.class); // TODO this is a guess
        setStyleFor("ComboBox2",comboStyle2);

        // --- Popup ---
        Style popupStyle = new Style(defaultStyle);
        popupStyle.addBorder(getBorder(ctx, 0, android.R.drawable.alert_light_frame),Style.ALL); // As defined on AlertController
        setStyleFor("Popup", popupStyle);

        // TODO: This may not be the right render
        Style popupRendererStyle = new Style(menuRendererStyle);
        Rect menuRenderExtraPadding2 = getAdjustedDensityRect(ctx, 12, 12, 12, 12);
        popupRendererStyle.addBorder(new BorderWithDivider( new AndroidBorder(menuBorder,menuRenderExtraPadding2) , divider2,false),Style.ALL);
        setStyleFor("PopupListRenderer", popupRendererStyle);

        // --- Tooltip ---
        Style tooltipStyle = new Style(defaultStyle);
        tooltipStyle.addBorder(getBorder(ctx, 0, android.R.drawable.toast_frame),Style.ALL);
        setStyleFor("ToolTip", tooltipStyle);


        // -- Scrollbars --
        Style scrollBarThumbStyle = new Style(defaultStyle);
        Drawable scroll = getDrawable(ctx, 0, android.R.attr.scrollbarThumbVertical);
        Rect scrollSize = new Rect(scroll.getIntrinsicWidth()/2, scroll.getIntrinsicHeight()/2, scroll.getIntrinsicWidth()/2, scroll.getIntrinsicHeight()/2);
        scrollBarThumbStyle.addBorder(new AndroidBorder(scroll, scrollSize),Style.ALL);
        setStyleFor("ScrollBarThumb",scrollBarThumbStyle);

        // -- IndeterminateSpinner --
        Style indeterminateSpinner = new Style(defaultStyle);
        Drawable spin = getDrawable(ctx,android.R.style.Widget_ProgressBar,android.R.attr.indeterminateDrawable);
        spin.setState( new int[] {android.R.attr.state_window_focused,android.R.attr.state_enabled} );

        indeterminateSpinner.addProperty( new AndroidSprite(spin), "sprite", Style.ALL);
        setStyleFor("IndeterminateSpinner",indeterminateSpinner);

        // yes = ok, no = cencel, dont use this as it seems to be wrong
        //ctx.getResources().getString(android.R.string.ok);

        // -- Slider --
        Style sliderThumbStyle = new Style(defaultStyle);
        Drawable slider = getDrawable(ctx,  android.R.style.Widget_SeekBar, android.R.attr.thumb);
        Rect sliderSize = new Rect(slider.getIntrinsicWidth()/2, slider.getIntrinsicHeight()/2, slider.getIntrinsicWidth()/2, slider.getIntrinsicHeight()/2);
        sliderThumbStyle.addBorder(new AndroidBorder(slider, sliderSize),Style.ALL);
        setStyleFor("SliderThumb",sliderThumbStyle);

        Style sliderTrackStyle = new Style(defaultStyle);
        //sliderTrackStyle.addBorder( getBorder(ctx, android.R.style.Widget_SeekBar, android.R.attr.background),Style.ALL); NOT WORKING
        //sliderTrackStyle.addBorder( getBorder(ctx, android.R.style.Widget_SeekBar, android.R.attr.process),Style.ALL); NOT WORKING
        //sliderTrackStyle.addBorder( getBorder(ctx, android.R.style.Widget_ProgressBar_Horizontal, android.R.attr.process),Style.ALL); NOT WORKING
        //sliderTrackStyle.addBorder( getBorder(ctx, android.R.style.Widget_ProgressBar_Horizontal, android.R.attr.background),Style.ALL); NOT WORKING
        sliderTrackStyle.addBorder( getBorder(ctx, android.R.style.Widget_SeekBar, android.R.attr.progressDrawable),Style.ALL);
        setStyleFor("SliderTrack",sliderTrackStyle);

        // -- ProgressBar --
        Style progressBar = new Style(defaultStyle);
        progressBar.addBorder( getBorder(ctx, android.R.style.Widget_ProgressBar_Horizontal, android.R.attr.progressDrawable),Style.ALL);
        //addBorder(progressBar, android.R.drawable.progress_horizontal); ????
        setStyleFor("ProgressBar",progressBar);


    }

    private Drawable getDrawable(Context ctx, String name) {
        try {
            Class clazz = Class.forName("com.android.internal.R$drawable");
            int attrIdx = clazz.getField(name).getInt(clazz);
            return ctx.getResources().getDrawable(attrIdx);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    private Border getListDivider(Context ctx, int styleDef, int attr) {
        Drawable divider2 = getDrawable(ctx, styleDef, attr);
        int thickness2 = divider2.getIntrinsicHeight();
        Rect thicknessRect2 = new Rect(thickness2,thickness2,0,0);
        return new AndroidBorder(divider2, thicknessRect2);

    }

    private AndroidIcon createButtonIcon(Context ctx, int id) {
        return new AndroidIcon(getDrawable(ctx, id, android.R.attr.button));
    }


    private Border getBorder(Context ctx, int styleDef, int attr) {
        Drawable  d0 = getDrawable(ctx, styleDef, attr);
        if (d0 != null) {
            return new AndroidBorder(d0);
        }
        return EMPTY_BORDER;
    }

    // TODO not sure if this is needed, this is a quick hack to make menus usable
    // though a better solution is needed to make all menu items including radio buttons consistent in size
    private Border getBorder(Context ctx, int styleDef, int attr, Rect padding) {
        Drawable  d0 = getDrawable(ctx, styleDef, attr);
        if (d0 != null) {
            return new AndroidBorder(d0, padding);
        }
        return EMPTY_BORDER;
    }


    private static Drawable getDrawable(Context ctx, int defStyle, int defAttr) {

        Drawable res = null;

//        try {
//            Class clazz = Class.forName("com.android.internal.R$styleable");
//            int attrs2[] = (int[]) clazz.getField("TextAppearance").get(clazz);
//            int attrIdx = clazz.getField("TextAppearance_textColor").getInt(clazz);
//            System.out.println("");
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }

        // 1 - Attempt to return directly from the resources
        try {
            res = ctx.getResources().getDrawable(defAttr);
        } catch (Throwable e) {
        }

        // NOTE: obtainStyledAttributes() fails if we request invalid attributes,
        // so we request one at a time
        int attrsWanted[] = { defAttr };

        // 2 - Try with a obtainStyledAttributes v1
        if (res == null) {
            TypedArray a = ctx.obtainStyledAttributes(null, attrsWanted, defStyle, 0);
            res = a.getDrawable(0);
            a.recycle();
        }

        // 3 - Try with a obtainStyledAttributes v2
        if (res == null) {
            TypedArray a = ctx.obtainStyledAttributes(defStyle, attrsWanted);
            res = a.getDrawable(0);
            a.recycle();
        }

        // 4 - Try with a obtainStyledAttributes v3
        if (res == null) {
            TypedArray a = ctx.obtainStyledAttributes(attrsWanted);
            res = a.getDrawable(0);
            a.recycle();
        }

        if (res==null) {
            System.out.println("no Drawable found for: defStyle="+defStyle+", defAttr="+defAttr);
        }

        return res;
    }

    static ColorStateList getTextColor(Context ctx, int defStyle) {

        ColorStateList res = null;

        int attrs[] = {android.R.attr.textColor, android.R.attr.textAppearance};
        TypedArray a = ctx.obtainStyledAttributes(defStyle, attrs);

        res = a.getColorStateList(0);

        if (res == null) {
            TypedArray a2 = ctx.obtainStyledAttributes(null, attrs, defStyle, 0);
            res = a2.getColorStateList(0);
            a2.recycle();
        }

        if (res == null) {
            // If textColor is not available, we need to dig inside "text appearance"
            // TODO: This "digging" does not seem to work...
            TypedArray appearance = null;
            int ap = a.getResourceId(1, -1);
            if (ap != -1) {
                appearance = ctx.obtainStyledAttributes(ap, new int[] {android.R.attr.textColor});
            }
            if (appearance != null) {
                res = a.getColorStateList(0);
                appearance.recycle();
            }
        }

        if (res == null) {
            res = ColorStateList.valueOf(0xFFFF00FF);
        }

        a.recycle();

        return res;
    }

    static void setForegroundColor(Context ctx, Style style, int defStyle,Class cl) {
        int[] map = {
                Style.ALL,
                Style.DISABLED,
                Style.DISABLED|Style.FOCUSED,
                Style.DISABLED|Style.SELECTED,
                Style.DISABLED|Style.FOCUSED|Style.SELECTED,
                Style.FOCUSED,
                Style.SELECTED,
                Style.FOCUSED|Style.SELECTED,
        };

        ColorStateList clist = getTextColor(ctx, defStyle);
        int defColor = clist.getDefaultColor();

        for (int i = 0; i < map.length; i++) {

            int swingMeStyle = map[i];
            int[] stateSet = AndroidBorder.getDrawableState(swingMeStyle,cl,true);
            int color = clist.getColorForState(stateSet, defColor);
            style.addForeground(color , swingMeStyle);
        }
    }


    static void setForegroundColor(Context ctx, Style style, int defStyle, Class cl, int swingMeStyle) {

        ColorStateList clist = getTextColor(ctx, defStyle);
        int defColor = clist.getDefaultColor();

        int[] stateSet = AndroidBorder.getDrawableState(swingMeStyle,cl,true);
        int color = clist.getColorForState(stateSet, defColor);
        style.addForeground(color , swingMeStyle);
    }

    private static Rect getAdjustedDensityRect(Context ctx, int left, int top, int right, int bottom) {
        float density = ctx.getResources().getDisplayMetrics().density;

        Rect rect = new Rect(
                (int) (left * density),
                (int) (top * density),
                (int) (right * density),
                (int) (bottom * density));

        return rect;
    }

    private static int getColorAtCenter(Drawable d) {
        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicHeight();

        if (w<=0) {
            w=50;
        }
        if (h<=0) {
            h=50;
        }

        int x = w / 2;
        int y = h / 2;
        Bitmap iBitmap = Bitmap.createBitmap(w,h, Config.ARGB_8888);
        Canvas canvas = new Canvas(iBitmap);
        d.setBounds(0, 0, w, h); // NEED to do this
        d.draw(canvas);
        int c = iBitmap.getPixel(x, y);
        iBitmap.recycle();
        return c;
    }



    public static int getTextColorFor(int c) {

            int r = Color.red(c);
            int g = Color.green(c);
            // int b = c.getBlue();

            if ((r > 240 || g > 240) || (r > 150 && g > 150)) {
                    return Color.BLACK;
            }
            else {
                    return Color.WHITE;
            }

    }

}

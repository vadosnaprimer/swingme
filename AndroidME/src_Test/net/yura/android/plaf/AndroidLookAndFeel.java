package net.yura.android.plaf;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import net.yura.android.AndroidMeMIDlet;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;

public class AndroidLookAndFeel extends LookAndFeel {

    public AndroidLookAndFeel() {

        Context ctx = AndroidMeMIDlet.DEFAULT_ACTIVITY.getApplicationContext();

        TypedArray a2 = ctx.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorForeground});
        int c = a2.getColor(0, 0xFF000000);
        a2.recycle();

        Style defaultStyle = new Style();
        defaultStyle.addFont( new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_PLAIN, javax.microedition.lcdui.Font.SIZE_MEDIUM) , Style.ALL);
        defaultStyle.addForeground(c , Style.ALL);
        setStyleFor("", defaultStyle);

        Style androidMenuStyle = new Style(defaultStyle);
        addBorder(ctx, androidMenuStyle, 0, android.R.drawable.menu_full_frame);
        setStyleFor("AndroidMenu", androidMenuStyle);

        Style menuStyle = new Style(defaultStyle);
        addBorder(ctx, menuStyle, 0, android.R.drawable.menu_frame);
        setForegroundColor(menuStyle, android.R.style.TextAppearance_Widget_IconMenu_Item);
        setStyleFor("Menu", menuStyle);

        Rect menuRenderExtraPadding = new Rect(10, 10, 10, 10);
        Style menuRendererStyle = new Style(defaultStyle);
        addBorder(ctx, menuRendererStyle, android.R.style.Widget_ListView_Menu, android.R.attr.listSelector, menuRenderExtraPadding);
        // TODO: This is wrong... we should be using android.R.style.Widget_ListView, and then get its android.R.attr.textAppearance.
        // That should map to TextAppearance_Widget_TextView... For some reason, this is not working...
        setForegroundColor(menuRendererStyle, android.R.style.TextAppearance_Widget_IconMenu_Item);
        setStyleFor("MenuRenderer",menuRendererStyle);

//        setStyleFor("MenuItem",menuItemStyle); // for the arrow to work

        // --- List ---
        Rect listExtraPadding = new Rect(10, 10, 10, 10);
        Style listCellRenderer = new Style(defaultStyle);
        addBorder(ctx, listCellRenderer, android.R.style.Widget_ListView, android.R.attr.listSelector, listExtraPadding);

        setForegroundColor(listCellRenderer, android.R.style.TextAppearance_Large); // Has defined in simple_list_item.xml
        setStyleFor("ListRenderer",listCellRenderer);


        // com.android.internal.R.style.Theme_Dialog_Alert
        // Dialog
        Style dialogStyle = new Style(defaultStyle);
        addBorder(ctx, dialogStyle, 0, android.R.drawable.dialog_frame);
        setStyleFor("Dialog", dialogStyle);

        Style titleBarStyle = new Style(defaultStyle);
        addBorder(ctx, titleBarStyle, 0, android.R.drawable.title_bar);
        setStyleFor("TitleBar", titleBarStyle);

//        Style progressBar = new Style(defaultStyle);
//        addBorder(progressBar, android.R.drawable.progress_horizontal);
//        setStyleFor("ProgressBar",progressBar);

        // --- Button ---
        Style buttonStyle = new Style(defaultStyle);
        addBorder(ctx, buttonStyle, android.R.attr.buttonStyle, android.R.attr.background);
        setForegroundColor(buttonStyle, android.R.style.Widget_Button);
        setStyleFor("Button", buttonStyle);

        // --- Radio Button ---
        Style radioStyle = new Style(defaultStyle);
        radioStyle.addProperty(createIcon(ctx, android.R.style.Widget_CompoundButton_RadioButton), "icon", Style.ALL);
        setForegroundColor(radioStyle, android.R.style.Widget_CompoundButton_RadioButton);
        setStyleFor("RadioButton",radioStyle);

        // --- Check Box ---
        Style checkboxStyle = new Style(defaultStyle);
        checkboxStyle.addProperty(createIcon(ctx, android.R.style.Widget_CompoundButton_CheckBox), "icon", Style.ALL);
        setForegroundColor(radioStyle, android.R.style.Widget_CompoundButton_CheckBox);
        setStyleFor("CheckBox",checkboxStyle);

        // --- Frame ---
        Style frameStyle = new Style(defaultStyle);
        addBorder(ctx, frameStyle, 0, android.R.attr.windowBackground);
        setStyleFor("Frame",frameStyle);

        // --- TextArea and TextField ---
        Style inputStyle = new Style(defaultStyle);
        addBorder(ctx, inputStyle, android.R.attr.editTextStyle, android.R.attr.background);
        setForegroundColor(inputStyle, android.R.style.Widget_EditText);
        setStyleFor("TextArea",inputStyle);
        setStyleFor("TextField",inputStyle);

        // --- ComboBox ---
        Style comboStyle = new Style(buttonStyle);
        addBorder(ctx, comboStyle, android.R.style.Widget_Spinner, android.R.attr.background);
        setForegroundColor(comboStyle, android.R.style.Widget_Spinner);
        setStyleFor("ComboBox",comboStyle);

        Style popupStyle = new Style(defaultStyle);
        addBorder(ctx, popupStyle, 0, android.R.drawable.alert_light_frame); // As defined on AlertController
        setStyleFor("Popup", popupStyle);

        Style tooltipStyle = new Style(defaultStyle);
        addBorder(ctx, tooltipStyle, 0, android.R.drawable.toast_frame);
        setStyleFor("ToolTip", tooltipStyle);

    }

    private Drawable getDrawable(Context ctx, int defStyle, int defAttr) {

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

        return res;
    }

    private AndroidIcon createIcon(Context ctx, int id) {
        return new AndroidIcon(getDrawable(ctx, id, android.R.attr.button));
    }

    private void addBorder(Context ctx, Style style, int styleDef, int attr) {
        Drawable  d0 = getDrawable(ctx, styleDef, attr);
        if (d0 != null) {
            style.addBorder(new AndroidBorder(d0), Style.ALL);
        }
    }

    private void addBorder(Context ctx, Style style, int styleDef, int attr, Rect extraPadding) {
        Drawable  d0 = getDrawable(ctx, styleDef, attr);
        if (d0 != null) {
            style.addBorder(new AndroidBorder(d0, extraPadding), Style.ALL);
        }
    }

    static ColorStateList getTextColor(Context ctx, int defStyle) {

        ColorStateList res = null;

        int attrs[] = {android.R.attr.textColor, android.R.attr.textAppearance};
        TypedArray a = ctx.obtainStyledAttributes(defStyle, attrs);

        res = a.getColorStateList(0);

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

    static void setForegroundColor(Style style, int defStyle) {
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

        ColorStateList clist = getTextColor(AndroidMeMIDlet.DEFAULT_ACTIVITY, defStyle);
        int defColor = clist.getDefaultColor();

        for (int i = 0; i < map.length; i++) {

            int swingMeStyle = map[i];
            int[] stateSet = AndroidBorder.getDrawableState(swingMeStyle);
            int color = clist.getColorForState(stateSet, defColor);
            style.addForeground(color , swingMeStyle);
        }
    }
}

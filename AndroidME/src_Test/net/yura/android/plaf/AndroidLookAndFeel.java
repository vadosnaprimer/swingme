package net.yura.android.plaf;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import net.yura.android.AndroidMeMIDlet;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;

public class AndroidLookAndFeel extends LookAndFeel {

    public AndroidLookAndFeel() {

        Context ctx = AndroidMeMIDlet.DEFAULT_ACTIVITY.getApplicationContext();
        Resources res = ctx.getResources();

        TypedArray a2 = ctx.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorForeground});
        int c = a2.getColor(0, 0xFF000000);


        Style defaultStyle = new Style();
        defaultStyle.addFont( new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_PLAIN, javax.microedition.lcdui.Font.SIZE_MEDIUM) , Style.ALL);
        defaultStyle.addForeground(c , Style.ALL);
        setStyleFor("",defaultStyle);

        // --- List ---
        Style listCellRenderer = new Style(defaultStyle);
        listCellRenderer.addBorder(new AndroidBorder(res.getDrawable(android.R.drawable.list_selector_background)), Style.ALL);
        setForegroundColor(listCellRenderer, android.R.style.Widget_Button);
        setStyleFor("ListRenderer",listCellRenderer);

        Style progressBar = new Style(defaultStyle);
        addBorder(progressBar, android.R.drawable.progress_horizontal);
        setStyleFor("ProgressBar",progressBar);

        // --- Button ---
        Style buttonStyle = new Style(defaultStyle);
        addBorder(buttonStyle, android.R.attr.buttonStyle);
        setForegroundColor(buttonStyle, android.R.style.Widget_Button);
        setStyleFor("Button", buttonStyle);

        // --- Radio Button ---
        Style radioStyle = new Style(defaultStyle);
        radioStyle.addProperty(createIcon(android.R.style.Widget_CompoundButton_RadioButton), "icon", Style.ALL);
        setForegroundColor(radioStyle, android.R.style.Widget_CompoundButton_RadioButton);
        setStyleFor("RadioButton",radioStyle);

        // --- Check Box ---
        Style checkboxStyle = new Style(defaultStyle);
        checkboxStyle.addProperty(createIcon(android.R.style.Widget_CompoundButton_CheckBox), "icon", Style.ALL);
        setForegroundColor(radioStyle, android.R.style.Widget_CompoundButton_CheckBox);
        setStyleFor("CheckBox",checkboxStyle);

        // --- Frame ---
        TypedArray a = ctx.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        Drawable d = a.getDrawable(0);
        Style windowSkin = new Style(defaultStyle);
        windowSkin.addBorder(new AndroidBorder(d), Style.ALL);
        setStyleFor("Frame",windowSkin);

        Style inputStyle = new Style(defaultStyle);
        addBorder(inputStyle, android.R.attr.editTextStyle);
        setForegroundColor(inputStyle, android.R.style.Widget_EditText);

        setStyleFor("TextArea",inputStyle);
        setStyleFor("TextField",inputStyle);
    }

    private Drawable getDrawable(Context ctx, int defStyle, int defAttr) {

        Drawable res = null;

//        try {
//            Class clazz = Class.forName("com.android.internal.R$styleable");
//            int attrs2[] = (int[]) clazz.getField("TextAppearance").get(clazz);
//            int attrIdx = clazz.getField("TextAppearance_textColor").getInt(clazz);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }

        // NOTE: obtainStyledAttributes() fails if we request invalid attributes,
        // so we request one at a time
        int attrsWanted[] = { defAttr };

        TypedArray a = ctx.obtainStyledAttributes(null, attrsWanted, defStyle, 0);
        res = a.getDrawable(0);
        a.recycle();

        // NOTE: The two versions of obtainStyledAttributes() return different
        // results, so if the first fails, we try the second.
        if (res == null) {
            a = ctx.obtainStyledAttributes(defStyle, attrsWanted);
            res = a.getDrawable(0);
            a.recycle();
        }

        return res;
    }

    private Drawable getDrawable(int defStyle) {

        Drawable res = null;

        Context ctx = AndroidMeMIDlet.DEFAULT_ACTIVITY.getApplicationContext();
        int attrsWanted[] = new int[] {android.R.attr.button, android.R.attr.drawable, android.R.attr.background };

        for (int i = 0; res == null && i < attrsWanted.length; i++) {
            res = getDrawable(ctx, defStyle, attrsWanted[i]);
        }

        return res;
    }

    private AndroidIcon createIcon(int id) {
        return new AndroidIcon(getDrawable(id));
    }

    private void addBorder(Style style, int styleDef) {
        Drawable  d0 = getDrawable(styleDef);
        style.addBorder(new AndroidBorder(d0), Style.ALL);
    }

    static ColorStateList getTextColor(Context ctx, int defStyle) {

        ColorStateList res = null;

        int attrs[] = {android.R.attr.textColor};
        TypedArray a = ctx.obtainStyledAttributes(defStyle, attrs);

        final int N = a.getIndexCount();
        for (int i = 0; res == null && i < N; i++) {
            int attr = a.getIndex(i);

            res = a.getColorStateList(attr);
        }

        a.recycle();

        if (res == null) {
            res = ColorStateList.valueOf(0xFF000000);
        }

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

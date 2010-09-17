package net.yura.android.plaf;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import net.yura.android.AndroidMeMIDlet;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;

public class AndroidLookAndFeel extends LookAndFeel {

    public AndroidLookAndFeel() {

        Context ctx = AndroidMeMIDlet.DEFAULT_ACTIVITY.getApplicationContext();
        Resources res = ctx.getResources();

        Style defaultStyle = new Style();
        defaultStyle.addFont( new Font(javax.microedition.lcdui.Font.FACE_PROPORTIONAL, javax.microedition.lcdui.Font.STYLE_PLAIN, javax.microedition.lcdui.Font.SIZE_MEDIUM) , Style.ALL);
        defaultStyle.addForeground( 0xFF000000 , Style.ALL);

        setStyleFor("",defaultStyle);

        Style listCellRenderer = new Style(defaultStyle);
        listCellRenderer.addBorder(new AndroidBorder(res.getDrawable(android.R.drawable.list_selector_background)), Style.ALL);
        setStyleFor("ListRenderer",listCellRenderer);

        Style progressBar = new Style(defaultStyle);
        listCellRenderer.addBorder(new AndroidBorder(res.getDrawable(android.R.drawable.progress_horizontal)), Style.ALL);
        setStyleFor("ProgressBar",progressBar);

        Style buttonStyle = new Style(defaultStyle);
        addBorder(buttonStyle, new Button(ctx));
        setStyleFor("Button",buttonStyle);

        Style radioStyle = new Style(defaultStyle);
        radioStyle.addProperty(createIcon(android.R.style.Widget_CompoundButton_RadioButton), "icon", Style.ALL);
        setStyleFor("RadioButton",radioStyle);

//        int checkBoxId = ctx.getResources().getIdentifier("btn_check", "drawable", "android");
//
//        Style checkboxStyle = new Style(defaultStyle);
//        checkboxStyle.addProperty(new AndroidIcon(res.getDrawable(checkBoxId)), "icon", Style.ALL);
//        setStyleFor("CheckBox",checkboxStyle);

        Style windowSkin = new Style(defaultStyle);
        windowSkin.addBorder(new AndroidBorder(res.getDrawable(android.R.drawable.menu_frame)), Style.ALL);
        setStyleFor("Frame",windowSkin);

//        TypedArray a = ctx.getTheme().obtainStyledAttributes( new int[]{android.R.attr.windowBackground});


        Style checkboxStyle = new Style(defaultStyle);
        checkboxStyle.addProperty(createIcon(android.R.style.Widget_CompoundButton_CheckBox), "icon", Style.ALL);
        setStyleFor("CheckBox",checkboxStyle);

    }

    private Drawable getDrawable(int id) {
        Context ctx = AndroidMeMIDlet.DEFAULT_ACTIVITY.getApplicationContext();
        int attrsWanted[] = new int[]{android.R.attr.button, android.R.attr.drawable, android.R.attr.background};

        TypedArray a = ctx.obtainStyledAttributes(id, attrsWanted);
        for (int i = 0; i < attrsWanted.length; i++) {
            Drawable d = a.getDrawable(i);
            if (d != null) {
                return d;
            }
        }

        System.out.println(">>>>>>>>> null!");

        return null;
    }

    private AndroidIcon createIcon(int id) {
        return new AndroidIcon(getDrawable(id));
    }

    private void addBorder(Style style, View view) {
        AndroidBorder border = new AndroidBorder(view.getBackground());
        style.addBorder(border, Style.ALL);
    }
}

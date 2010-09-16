package net.yura.android.plaf;

import android.content.res.Resources;
import android.widget.CheckBox;
import net.yura.android.AndroidMeMIDlet;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.Style;

public class AndroidLookAndFeel extends LookAndFeel {

    public AndroidLookAndFeel() {

        Resources res = AndroidMeMIDlet.DEFAULT_ACTIVITY.getResources();
//        android.R.attr.button

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
        buttonStyle.addBorder(new AndroidBorder(res.getDrawable(android.R.drawable.btn_default_small)), Style.ALL);
        setStyleFor("Button",buttonStyle);
        Style radioStyle = new Style(defaultStyle);
        radioStyle.addProperty(new AndroidIcon(res.getDrawable(android.R.drawable.btn_radio)), "icon", Style.ALL);
        setStyleFor("RadioButton",radioStyle);

        Style checkboxStyle = new Style(defaultStyle);

        CheckBox cb = new CheckBox(AndroidMeMIDlet.DEFAULT_ACTIVITY);
        cb.layout(0, 0, 50, 50);

        checkboxStyle.addProperty(new AndroidIcon(cb.getBackground()), "icon", Style.ALL);
//        checkboxStyle.addProperty(new AndroidIcon(res.getDrawable(android.R.drawable.checkbox_on_background)), "icon", Style.ALL);
        setStyleFor("CheckBox",checkboxStyle);

        Style windowSkin = new Style(defaultStyle);
        windowSkin.addBorder(new AndroidBorder(res.getDrawable(android.R.drawable.menu_frame)), Style.ALL);
        setStyleFor("Frame",windowSkin);

    }

}

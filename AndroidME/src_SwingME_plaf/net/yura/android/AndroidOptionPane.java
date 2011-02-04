package net.yura.android;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.logging.Logger;

public class AndroidOptionPane extends OptionPane {

    public static int optionPaneCounter;

    @Override
    public void setVisible(boolean b) {
        if (getMessage() instanceof String) {
            if (b) {
                // Make sure we have unique url's, so we can have as many activities (pop-ups) as needed.
                optionPaneCounter++;
                Midlet.getMidlet().platformRequest("nativeNoResult://"+OptionPaneActivity.class.getName()+"/" + optionPaneCounter, this);
            }
            else {
                Logger.warn("why is this happening???? setVisible(false) in AndroidOptionPane");
            }
        }
        else {
            super.setVisible(b);
        }
    }

}

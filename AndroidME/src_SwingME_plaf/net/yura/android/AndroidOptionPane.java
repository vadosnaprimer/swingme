package net.yura.android;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.logging.Logger;

public class AndroidOptionPane extends OptionPane {

    // Make sure we have unique url's, so we can have as many activities (pop-ups) as needed.
    public static int optionPaneCounter;

    @Override
    public void setVisible(boolean b) {

        Object obj = getMessage();

        if (obj==null || obj instanceof String) {
            if (b) {

                try {
                    Midlet.getMidlet().platformRequest("nativeNoResult://"+OptionPaneActivity.class.getName()+"/" + (optionPaneCounter++), this);
                }
                catch(Exception ex) {
                    //#debug debug
                    ex.printStackTrace();

                    //#debug warn
                    Logger.warn("failed to start OptionPaneActivity, falling back to SwingME OptionPane");
                    super.setVisible(b);
                }
            }
            else {
                //#debug warn
                Logger.warn("this should never happen!!!! setVisible(false) in AndroidOptionPane");
                super.setVisible(b);
            }
        }
        else {
            super.setVisible(b);
        }
    }

}

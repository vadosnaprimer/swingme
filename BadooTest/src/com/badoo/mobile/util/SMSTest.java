package com.badoo.mobile.util;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;

public class SMSTest extends Midlet implements ActionListener {

    TextField phoneNumber;

    protected DesktopPane makeNewRootPane() {
        return new DesktopPane(this, 0x00000000, null);
    }

    protected void initialize(DesktopPane rootpane) {
        try {
            Logger.init();
            rootpane.setLookAndFeel(new MetalLookAndFeel());

            Frame window = new Frame();

            phoneNumber = new TextField();
            phoneNumber.setMode(TextField.PHONENUMBER);
            phoneNumber.setText("07769744745");

            window.add(phoneNumber);

            Button send = new Button("Send");
            send.addActionListener(this);
            send.setActionCommand("send_sms");
            send.setMnemonic(KeyEvent.KEY_SOFTKEY1);
            
            window.addCommand(send);

            rootpane.add(window);
     

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void actionPerformed(String action) {
        if ("send_sms".equals(action)) {
            try {
                SMS.sendMessage(phoneNumber.getText(), "TEST MESSAGE");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}

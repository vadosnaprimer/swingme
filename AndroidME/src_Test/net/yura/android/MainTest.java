package net.yura.android;



import javax.microedition.io.Connector;
import javax.microedition.lcdui.Graphics;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.test.MainPane;
import net.yura.mobile.test.MainPane.Section;

public class MainTest extends Section {
    private TextField smsTextField;
    private TextField smsNumberField;


    public MainTest(MainPane mainPane) {
        super(mainPane);
    }

    //Override
    public void createTests() {
        add(new Label("Android Tests"));

        addSection("PIM", new PimTest());
        addSection("Multimedia", new MultimediaTest());
        addTest("Send SMS", "sms");
    }

    //Override
    public void openTest(String actionCommand) {
        if ("mainmenu".equals(actionCommand)) {
            addToScrollPane(this, null, makeButton("Exit", "exit"));
        }
        else if ("exit".equals(actionCommand)) {
            Midlet.exit();
        }
        else if ("sms".equals(actionCommand)) {


            smsTextField = new TextField();
            smsNumberField = new TextField(TextField.PHONENUMBER);

            Button smsSendBtn = new Button("Send SMS");
            smsSendBtn.setActionCommand("sendSms");
            smsSendBtn.addActionListener(this);

            Panel left = new Panel(new FlowLayout(Graphics.VCENTER, 0));
            left.add(new Label("Text:"));
            left.add(new Label("Phone:"));

            Panel right = new Panel(new BoxLayout(Graphics.VCENTER));
            right.add(smsTextField);
            right.add(smsNumberField);

            Panel smsPanel = new Panel(new BorderLayout());
            smsPanel.add(left, Graphics.LEFT);
            smsPanel.add(right);
            smsPanel.add(smsSendBtn, Graphics.BOTTOM);

            addToScrollPane(smsPanel, null);
        }
    }

    public void actionPerformed(String actionCommand) {
        if ("sendSms".equals(actionCommand)) {
            System.out.println(">>>>> sendSms");

            String phoneNumber = smsNumberField.getText();
            String message = smsTextField.getText();

            try {
                MessageConnection conn = (MessageConnection) Connector.open("sms://" + phoneNumber);
                TextMessage msg = (TextMessage) conn.newMessage(MessageConnection.TEXT_MESSAGE);
                msg.setPayloadText(message);
                //#debug debug
                Logger.debug("Sending SMS message with body " + message + " to phone number " + phoneNumber);
                conn.send(msg);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
        else {
            super.actionPerformed(actionCommand);
        }
    }
}

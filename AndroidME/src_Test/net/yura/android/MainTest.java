package net.yura.android;

import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Graphics;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ProgressBar;
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
        addTest("Show Notification", "notification");
        addTest("Show Native Popup", "nativePopup");

        Label preferenceSeparator = new Label("here are some android only components");
        preferenceSeparator.setName("PreferenceSeparator");
        add(preferenceSeparator);

        Vector v = new Vector();
        v.add("HELLO");
        v.add("GOODBYE");
        ComboBox comboBox2 = new ComboBox(v);
        comboBox2.setName("ComboBox2");
        add(comboBox2);

        Button redButton = new Button("RED BUTTON");
        redButton.setName("RedButton");
        add(redButton);

        ProgressBar bar = new ProgressBar();
        bar.setName("IndeterminateSpinner");
        bar.setIndeterminate(true);
        add(bar);

    }

    //Override
    public void openTest(String actionCommand) {
        if ("mainmenu".equals(actionCommand)) {
            Button exit = makeButton("Exit", "exit");
            exit.setMnemonic(KeyEvent.KEY_END);
            addToScrollPane(this, null, exit);
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
        else if ("notification".equals(actionCommand)) {
            String url = "notify://dummyServer?title=test&num=4&message=Some Message&icon=notify_new_msgs";
            try {
                Midlet.getMidlet().platformRequest(url);
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if ("nativePopup".equals(actionCommand)) {
            String url = "native://net.yura.android.TestTimePickerActivity";
            try {
                Midlet.getMidlet().platformRequest(url);
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
